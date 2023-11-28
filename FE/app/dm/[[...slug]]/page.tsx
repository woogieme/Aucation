"use client";

import { RoundedImg } from "@/app/components/tailwinds";
import { useParams, useRouter } from "next/navigation";
import { AiOutlineArrowLeft } from "react-icons/ai";
import tem from "@/app/images/nakchal.png";
import DMChat from "../components/DMChat";
import { useEffect, useState } from "react";
import axios from "axios";
import { callApi } from "@/app/utils/api";
import { pricetoString } from "@/app/utils/pricecal";
const dummy = {
  sellerPk: 0, // 판매자Pk
  sellerNickName: "", // 판매자 닉네임
  sellerImageURL: "", // 판매자 프사
  prodName: "", // 제목
  prodType: "", // ex ) 역경매 완료
  prodCategory: "", // ex  ) 전자기기
  prodEndPrice: 1, // 낙찰가, 할인가
  chatUUID: "123", // 채팅방 UUID
  chatList: [],
};
const DM = () => {
  const slug = useParams().slug;
  const prodPk = slug[0];
  const prodType = slug[1];
  const router = useRouter();
  const [memberPk, setMemberPk] = useState<number>(0);
  const [chatRoom, setChatRoom] = useState<any>(dummy);
  const exitHander = () => {
    router.back();
  };

  const getMemberPk = () => {
    callApi("get", "/members/get", {}).then(res => {
      setMemberPk(res.data.memberPk);
    });
  };

  useEffect(() => {
    getMemberPk();
  }, []);

  return (
    <div className="flex justify-center bg-[var(--c-white)]">
      <div className="px-48 py-24 w-[75%] bg-white">
        <button onClick={exitHander}>
          <AiOutlineArrowLeft size="40"></AiOutlineArrowLeft>
        </button>
        <div className="flex mt-10 rounded-2xl shadow-md w-60 items-center h-14 p-4 shadow-gray-400">
          <RoundedImg src={chatRoom.sellerImageURL} alt="이미지" />
          <p className="ml-2 text-lg">{chatRoom.sellerNickName}</p>
        </div>
        <h1 className="text-4xl break-all font-semibold mt-16">{chatRoom.prodName}</h1>
        <div className="mt-6 text-xl font-semibold">
          <span className="text-red-500"> {chatRoom.prodType}완료 </span>
          <span className="text-gray-500 ml-4">{chatRoom.prodCategory}</span>
        </div>
        <div className="mt-8 mb-12 ">
          <span className="text-2xl text-gray-500">낙찰가</span>
          <span className="ml-4 text-3xl text-[var(--c-blue)] font-bold">
            {pricetoString(chatRoom.prodEndPrice)}
          </span>
        </div>
        <div className="w-full h-[700px]">
          {memberPk != 0 && (
            <DMChat
              prodPk={prodPk}
              prodType={prodType}
              memberPk={memberPk}
              setChatRoom={setChatRoom}
            />
          )}
        </div>
      </div>
    </div>
  );
};
export default DM;
