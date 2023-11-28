import { toast } from "react-toastify";
export const noti = (body: string, url: string) => {
  toast.info(body, {
    onClick: () => {
      window.location.href = "https://aucation.co.kr/" + url;
    },
  });
};
