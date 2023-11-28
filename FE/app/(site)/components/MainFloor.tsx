"use-client";
import React from "react";
import MainFloorImg from "@/app/images/mainFloorImg.png";
import MainFloorText from "./MainFloorText";
import MainBtn from "@/app/components/button/MainBtn";
import Image from "next/image";
import Hammer from "@/app/images/hammer.png";
import Shop from "@/app/images/shop.png";
import Link from "next/link";

const MainFloor: React.FC = () => {
  return (
    <div className="w-full flex justify-center relative shadow-[0_-35px_60px_-15px_rgba(0,0,0,0.1)] bg-customBasic">
      <div className="absolute top-0 right-0">
        <Image src={Hammer} alt="Hammer" width={400} height={400} />
      </div>
      <div className="py-20 space-y-24">
        <MainFloorText className={"text-3xl font-bold"}>
          지금까지 1,972번의 경매가 열렸어요!
        </MainFloorText>
        <MainFloorText className={"text-xl text-customLightTextColor"}>
          <p>팔고 싶은 물건이 있나요?</p>
          <p>처치 곤란한 재고가 남았나요?</p>
        </MainFloorText>
        <MainFloorText className={"text-5xl font-extrabold bg-highlight-effect"}>
          지금 당장 경매에 참여해보세요!
        </MainFloorText>
        <Link href="/panmae" className="flex items-center justify-center">
          <MainBtn className={"px-12 py-6 font-bold w-auto text-[30px]"}>물건 올리기</MainBtn>
        </Link>
        <div className="absolute bottom-0 left-0">
          <Image src={Shop} alt="Shop" width={400} height={400} />
        </div>
      </div>
    </div>
  );
};

export default MainFloor;
