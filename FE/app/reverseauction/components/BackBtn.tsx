import React from "react";
import { BiArrowBack } from "react-icons/bi";
import { useRouter } from "next/navigation";
const BackBtn = () => {
  const router = useRouter();
  return (
    <div
      className="bg-white"
      style={{ cursor: "pointer", backgroundColor: "var(--c-white)" }}
      onClick={() => {
        router.back();
      }}
    >
      <BiArrowBack size={35} color="gray" />
    </div>
  );
};

export default BackBtn;
