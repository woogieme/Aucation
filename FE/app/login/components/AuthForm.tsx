"use client";

import { useState, useCallback, useEffect } from "react";
import AuthSocialButton from "./AuthSocialButton";
import { RiKakaoTalkFill } from "react-icons/ri";
import { toast } from "react-hot-toast";
import { useRouter } from "next/navigation";
import axios from "axios";
import Input from "./Input";
import Button from "./Button";
import tw from "tailwind-styled-components";
import { emailRegexp } from "@/app/utils/regexp";
import { useRecoilState } from "recoil";
import { authState } from "@/app/store/atoms";
import StayMap from "@/app/components/map/StayMap";

type Variant = "LOGIN" | "REGISTER";

const AuthForm = () => {
  const [auth, setAuth] = useRecoilState(authState);
  const router = useRouter();
  const [variant, setVariant] = useState<Variant>("LOGIN");
  const [id, setId] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [nickname, setNickname] = useState<string>("");
  const [email, setEmail] = useState<string>("");
  const [emailVerify, setEmailVerify] = useState<string>("");
  const [verifyCode, setVerifyCode] = useState<string>("");
  const [verify, setVerify] = useState<boolean>(false); // 인증요청 햇는지
  const [verifyemail, setVerifyemail] = useState<boolean>(true); // 이메일 중복체크 햇는지
  const [verifiedcode, setVerifiedcode] = useState<boolean>(true); // 인증번호 맞는지
  const [verifynickname, setVerifynickname] = useState<boolean>(true);
  const [verifyid, setVerifyid] = useState<boolean>(true);
  const [lat, setLat] = useState<number>(0);
  const [lng, setLng] = useState<number>(0);

  const signOrlogin = () => {
    if (variant == "LOGIN") {
      login();
    } else {
      register();
    }
  };
  const register = () => {
    const data = {
      memberId: id,
      memberPw: password,
      memberEmail: email,
      memberNickname: nickname,
      memberLng: lng,
      memberLat: lat,
    };
    axios({
      method: "post",
      url: "/api/v1/members/signup",
      data: JSON.stringify(data),
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then(res => {
        // console.log(res.data);
        toast.success("회원가입 성공");
        login();
      })
      .catch(err => {
        // console.log(err.response.data);
        toast.error(err.response.data.message);
      });
  };

  const login = () => {
    const data = {
      memberId: id,
      memberPw: password,
    };
    axios({
      method: "post",
      url: "/api/v1/members/login",
      data: JSON.stringify(data),
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then(res => {
        localStorage.setItem("accessToken", res.headers.authorization);
        localStorage.setItem("refreshToken", res.headers["authorization-refresh"]);
        localStorage.setItem("role", res.data.role);
        setAuth({
          isLoggedIn: true,
          role: res.data.role,
        });
        toast.success("로그인 성공");
        // console.log(res.data);
        router.push("/");
      })
      .catch(err => {
        // console.log(err);
        // console.log(err);
        // console.log(err.response);
        toast.error(err.response.data);
      });
  };

  const idCheck = () => {
    axios({
      method: "get",
      url: `/api/v1/members/verification/id/${id}`,
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then(res => {
        // console.log(res.data);
        toast.success("중복된 아이디가 없습니다");
        setVerifyid(true);
      })
      .catch(err => {
        // console.log(err.response.data);
        toast.error(err.response.data.message);
        setVerifyid(false);
      });
  };
  const nicknameCheck = () => {
    axios({
      method: "get",
      url: `/api/v1/members/verification/nickname/${nickname}`,
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then(res => {
        // console.log(res.data);
        toast.success("중복된 닉네임이 없습니다");
        setVerifynickname(true);
      })
      .catch(err => {
        // console.log(err.response.data);
        toast.error(err.response.data.message);
        setVerifynickname(false);
      });
  };
  const emailCheck = () => {
    if (!emailRegexp.test(email)) {
      toast.error("이메일 형식이 맞지 않습니다");
      return;
    }
    axios({
      method: "get",
      url: `/api/v1/members/verification/email/${email}`,
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then(res => {
        // console.log(res.data);
        toast.success("중복된 이메일이 없습니다");
        setVerifyemail(true);
      })
      .catch(err => {
        // console.log(err.response.data);
        toast.error("해당 이메일이 이미 존재합니다");
        setVerifyemail(false);
      });
  };

  const certPushEmail = () => {
    if (!emailRegexp.test(email)) {
      toast.error("이메일 형식이 맞지 않습니다");
      return;
    }
    axios({
      method: "get",
      url: `/api/v1/members/certification/email/${email}`,
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then(res => {
        // console.log(res.data);
        setVerifyCode(res.data.code);
        toast.success("인증번호가 발송되었습니다");
        setVerify(true);
      })
      .catch(err => {
        // console.log(err.response.data);
        toast.error(err.response.data.message)
      });
  };

  const certEmail = () => {
    if (emailVerify == verifyCode) {
      setVerifiedcode(true);
      toast.success("인증되었습니다");
    } else {
      toast.error("인증번호가 일치하지 않습니다");
      setVerifiedcode(false);
    }
  };

  const toggleVariant = () => {
    setVariant(variant == "LOGIN" ? "REGISTER" : "LOGIN");
  };

  const onChangeId = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    setId(e.target.value);
  }, []);

  const onChangePassword = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    setPassword(e.target.value);
  }, []);

  const onChangeNickname = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    setNickname(e.target.value);
  }, []);

  const onChangeEmail = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    setEmail(e.target.value);
  }, []);

  const onChangeEmailVerify = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    setEmailVerify(e.target.value);
  }, []);

  useEffect(() => {
    const { geolocation } = navigator;
    geolocation.getCurrentPosition(
      position => {
        // success.
        setLat(position.coords.latitude);
        setLng(position.coords.longitude);
        // console.log(position.coords);
      },
      error => {
        console.warn("Fail to fetch current location", error);
        setLat(37);
        setLng(127);
      },
      {
        enableHighAccuracy: false,
        maximumAge: 0,
        timeout: Infinity,
      }
    );
  }, []);

  return (
    <div
      className="
    mt-6
    w-full
    flex-row
    items-center
    "
    >
      <h2
        className="
         text-center
         text-4xl
         font-bold
         tracking-tight
         mb-10
         "
      >
        {variant === "LOGIN" ? "로그인" : "회원가입"}
      </h2>
      <div className="my-5">
        <Label htmlFor="id">아이디</Label>
        <div
          className="
        flex
      "
        >
          <Input
            verify={verifyid}
            id="id"
            type="text"
            placeholder={"아이디를 입력하세요"}
            value={id}
            onChange={onChangeId}
          />
          {variant == "REGISTER" && <Button onClick={idCheck}>중복확인</Button>}
        </div>
      </div>
      {variant == "REGISTER" && (
        <div className="my-5">
          <Label htmlFor="email">이메일</Label>
          <div
            className="
              flex
            "
          >
            <Input
              verify={verifyemail}
              id="email"
              type="email"
              placeholder={"Ex:abc@example.com"}
              value={email}
              onChange={onChangeEmail}
            />
            <Button onClick={emailCheck}>중복확인</Button>
          </div>
        </div>
      )}
      {variant == "REGISTER" && (
        <div className="my-5">
          <Label htmlFor="emailVerify">이메일 인증번호 입력</Label>
          <div
            className="
              flex
            "
          >
            <Input
              verify={verifiedcode}
              id="emailVerify"
              type="text"
              placeholder={""}
              value={emailVerify}
              onChange={onChangeEmailVerify}
            />
            {verify == true ? (
              <Button onClick={certEmail}>인증하기</Button>
            ) : (
              <Button onClick={certPushEmail}>인증요청</Button>
            )}
          </div>
        </div>
      )}
      {variant == "REGISTER" && (
        <div className="my-5">
          <Label htmlFor="nickname">닉네임</Label>
          <div
            className="
              flex
            "
          >
            <Input
              verify={verifynickname}
              id="nickname"
              type="text"
              placeholder={""}
              value={nickname}
              onChange={onChangeNickname}
            />
            <Button onClick={nicknameCheck}>중복확인</Button>
          </div>
        </div>
      )}
      <div className="my-5">
        <Label htmlFor="nickname">비밀번호</Label>
        <div
          className="
              flex
            "
        >
          <Input
            id="password"
            type="password"
            placeholder={"비밀번호를 입력하세요"}
            value={password}
            onChange={onChangePassword}
            verify={true}
            onEnter={signOrlogin}
          />
        </div>
      </div>
      {variant == "REGISTER" && (
        <div className="w-full h-[500px] mr-5 mb-10">
          <Label htmlFor="map">우리 동네 확인 {"(현재 내 위치 기준)"}</Label>
          <StayMap inputLat={lat} inputLag={lng} />
        </div>
      )}

      <div
        className="
        mt-4
        flex
        items-center
        justify-center
      "
      >
        <button onClick={signOrlogin} className="hover:text-red-500 text-xl">
          {variant === "LOGIN" ? "로그인" : "회원가입"}
        </button>
      </div>

      <div className="border-t border-gray-400 flex-grow my-6"></div>

      {variant == "LOGIN" ? (
        <div className="flex items-center justify-center">
          <p className="text-xl">
            계정이 없으신가요?{" "}
            <button onClick={toggleVariant} className="font-bold text-2xl hover:text-red-500">
              회원가입
            </button>
          </p>
        </div>
      ) : (
        <div className="flex items-center justify-center ">
          <p>
            이미 계정이 있나요?{" "}
            <button onClick={toggleVariant} className="font-bold text-2xl hover:text-red-500 ">
              로그인
            </button>
          </p>
        </div>
      )}
    </div>
  );
};
export default AuthForm;

const Label = tw.label`
block
text-base
font-medium
leading-6
`;
