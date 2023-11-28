"use client";

import React, { useState } from "react";

import LikeBtn from "../../detail/components/LikeBtn";
import { BsFillPersonFill } from "react-icons/bs";
import Image from "next/image";
import ColCountDown from "./ColCountDown";
import { PreAuctionItem } from "@/app/components/Card/cardType";
import formatKoreanCurrency from "@/app/utils/formatKoreanCurrency";
import { callApi } from "@/app/utils/api";
import AuctionCountDown from "./AuctionCountDown";
import { useRouter } from "next/navigation";
import toast from "react-hot-toast";
interface CardProps {
  item: PreAuctionItem;
  nowTime: Date | null;
}

const PreAuctionListCard: React.FC<CardProps> = ({ item, nowTime }) => {
  const router = useRouter();
  const [likeCount, setLikeCount] = useState<number>(item.likeCnt);
  const [isLiked, setIsLiked] = useState<boolean>(item.isLike);
  // const tmp = nowTime ? new Date(nowTime) : null;
  const [auctionState, setAuctionState] = useState<string>("마감시간");

  const auctionStateHandler = (value: string) => {
    setAuctionState(value);
  };
  const likeHandler = (newLikeStatus: boolean) => {
    setIsLiked(newLikeStatus); // 옵티미스틱 업데이트
    setLikeCount(newLikeStatus ? likeCount + 1 : likeCount - 1); // 좋아요 수 변경

    callApi("get", `/auction/like/${item.auctionPk}`, { auctionPk: item.auctionPk })
      .then(response => {
        toast.success(response.data.message)
      })
      .catch(error => {
        toast.error(error.response.data.message)
      });
  };
  const EnterDetail = (pk: number) => {
    router.push(`/detail/auction/${pk}`);
  };
  return (
    <div className=" overflow-hidden h-full rounded-lg shadow-lg bg-white hover:border-sky-500 hover:ring-8 hover:ring-sky-200 hover:ring-opacity-100">
      <div className=" overflow-hidden h-full rounded-lg shadow-lg bg-white hover:border-sky-500 hover:ring-8 hover:ring-sky-200 hover:ring-opacity-100">
        <div className="h-1/2 relative">
          <Image
            src={item.auctionImg}
            alt={item.auctionTitle}
            layout="fill"
            objectFit="cover"
            objectPosition="center"
            className="cursor-pointer transition-transform transform duration-300 hover:scale-110"
            onClick={() => {
              EnterDetail(item.auctionPk);
            }}
          />
          <div className="absolute top-2 right-2">
            <LikeBtn isLiked={isLiked} likeHandler={likeHandler} />
          </div>
        </div>
        {/* 본문 */}
        <div className="h-1/2 px-3 pt-2 pb-3 flex flex-col place-content-between bg-customBasic">
          <div className="flex items-center justify-between">
            <p className="text-sm text-customGray">좋아요 {likeCount} 개</p>
          </div>

          {/* 제목과 카테고리 */}
          <div>
            <div
              onClick={() => {
                EnterDetail(item.auctionPk);
              }}
              className="cursor-pointer flex items-center justify-between font-extrabold text-2xl overflow-hidden"
            >
              <p className="max-h-[80px] overflow-hidden text-ellipsis break-all">
                {" "}
                {item.auctionTitle}
              </p>
            </div>
            <div className="text-base text-customGray">카테고리 {item.auctionType}</div>
          </div>

          <div className="flex items-center justify-between font-bold text-xl">
            <p>
              {" "}
              시작가{" "}
              <span className="text-customBlue">
                {formatKoreanCurrency(item.auctionStartPrice)}
              </span>
            </p>
          </div>

          <div className="flex items-center w-full rounded-3xl bg-customBgLightBlue h-[13%]">
            <div
              className="bg-customBgBlue flex items-center justify-center
            h-full rounded-3xl w-[40%] text-white ml-1"
            >
              {item.auctionOwnerIsShop ? "소상공인" : "개인"}
            </div>
            <div className=" flex items-center w-[60%] justify-start overflow-hidden flex-grow whitespace-nowrap pl-[5px] ">
              <p className="truncate">{item.auctionOwnerNickname}</p>
            </div>
          </div>

          <div className="flex items-center justify-between text-[16px]">
            <AuctionCountDown
              currentTime={nowTime!}
              auctionEndTime={item.auctionStartTime}
              stateHandler={auctionStateHandler}
              isPre={true}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default PreAuctionListCard;
