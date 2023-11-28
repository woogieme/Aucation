import React from "react";
import { BiArrowBack } from "react-icons/bi";
import { useRouter } from "next/navigation";
const BackBtn = () => {
  const router = useRouter();
  const backHandler = () => {
    router.back();
  };
  return (
    <div
      className="bg-white"
      style={{ cursor: "pointer", backgroundColor: "var(--c-white)" }}
      onClick={backHandler}
    >
      <BiArrowBack size={35} color="gray" />
    </div>
  );
};

export default BackBtn;
