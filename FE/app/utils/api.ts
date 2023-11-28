import axios from "axios";

const api = axios.create({
  baseURL: "/api/v1",
});
// 요청 인터셉터: 토큰을 헤더에 추가
api.interceptors.request.use(
  config => {
    const accesstoken = localStorage.getItem("accessToken");
    const refreshToken = localStorage.getItem("refreshToken");
    if (accesstoken) {
      config.headers["Authorization"] = `Bearer ${accesstoken}`;
    }
    if (refreshToken) {
      config.headers["refreshToken"] = `${refreshToken}`;
    }
    return config;
  },
  error => Promise.reject(error)
);

// 응답 인터셉터: 토큰 만료 시 재요청 or 로그인 페이지로 리디렉션
api.interceptors.response.use(
  response => {
    return response;
  },
  async err => {
    const originalRequest = err.config;
    if (err.response.data.code == "C007") {
      const accesstoken = localStorage.getItem("accessToken");
      const refreshToken = localStorage.getItem("refreshToken");
      if (!refreshToken) {
        // 리프레시 토큰이 없으면 로그인 페이지로 리디렉션
        if (window.location.pathname !== "/") window.location.href = "/login";
        return Promise.reject(err);
      }

      try {
        // 토큰 재발급 API 호출
        const { headers } = await axios({
          url: "/api/v1/members/reissue",
          method: "POST",
          headers: {
            Authorization: `Bearer ${accesstoken}`,
            "Authorization-refresh": `Bearer ${refreshToken}`,
          },
        });
        localStorage.setItem("accessToken", headers.authorization);
        localStorage.setItem("refreshToken", headers["authorization-refresh"]);
        // console.log(headers.authorization);
        // console.log(headers["authorization-refresh"]);
        originalRequest.headers["Authorization"] = `Bearer ${headers.Authorization}`;
        return api(originalRequest);
      } catch (err) {
        // 리프레시 토큰이 만료되었거나 재발급 요청에 문제가 있으면 로그인 페이지로 리디렉션
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        window.location.href = "/login";
        return Promise.reject(err);
      }
    }
    return Promise.reject(err);
  }
);

export const callApi = async (method: string, url: string, body: any = {}) => {
  return api({
    method: method,
    url: url,
    headers: {
      Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
    },
    data: body,
  });
};
