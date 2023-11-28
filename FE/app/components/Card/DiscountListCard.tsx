"use client";

import React, { useState } from "react";
import { useRouter } from "next/navigation";
import LikeBtn from "../../detail/components/LikeBtn";
import { BsFillPersonFill } from "react-icons/bs";
import Image from "next/image";
import ColCountDown from "./ColCountDown";
import { DiscountItem } from "@/app/components/Card/cardType";
import formatKoreanCurrency from "@/app/utils/formatKoreanCurrency";
import { callApi } from "@/app/utils/api";
import AuctionCountDown from "./AuctionCountDown";
interface CardProps {
  item: DiscountItem;
  nowTime: Date | null;
}

const DiscountListCard: React.FC<CardProps> = ({ item, nowTime }) => {
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
    const apiEndpoint = `/discount/like/${item.discountPk}`;

    callApi("get", apiEndpoint, { auctionPk: item.discountPk })
      .then(response => {
        console.log("좋아요 성공", response);
      })
      .catch(error => {
        console.log("좋아요 실패", error);
      });
  };
  const EnterDetail = (pk: number) => {
    router.push(`/detail/discount/${pk}`);
  };
  const EnterBid = () => {
    router.push(`/bid/${item.discountUUID}`);
  };
  return (
    <div className=" overflow-hidden h-full w-full rounded-lg shadow-lg bg-white hover:border-sky-500 hover:ring-8 hover:ring-sky-200 hover:ring-opacity-100">
      <div className="h-1/2 relative">
        <Image
          onClick={() => {
            EnterDetail(item.discountPk);
          }}
          src={item.discountImg}
          alt={item.discountTitle}
          layout="fill"
          objectFit="cover"
          objectPosition="center"
          className="cursor-pointer transition-transform transform duration-300 hover:scale-110"
        />
        <div className="absolute top-2 right-2">
          <LikeBtn isLiked={isLiked} likeHandler={likeHandler} />
        </div>
        {/* <div
          onClick={() => EnterBid()}
          className="rounded-lg absolute bottom-2 left-2 px-3 py-2 bg-customBgBlue hover:bg-custom-btn-gradient-hover text-customBasic cursor-pointer"
        >
          바로입장
        </div> */}
      </div>

      {/* 본문 */}
      <div className="h-1/2 px-3 pt-2 pb-3 flex flex-col place-content-between bg-customBasic">
        {/* 좋아요 */}
        <div className="flex items-center justify-between">
          <p className="text-sm text-customGray"> 좋아요 {likeCount} 개</p>
          {/* <p>
            `
            {"reAuctionPk" in item
              ? "입찰자: " + item.reAuctionBidCnt
              : "참여자: " + item.auctionCurCnt}{" "}
            명
          </p> */}
        </div>

        {/* 제목과 카테고리 */}
        <div>
          <div
            onClick={() => {
              EnterDetail(item.discountPk);
            }}
            className="cursor-pointer flex items-center justify-between font-extrabold text-2xl"
          >
            <p className="max-h-[80px] overflow-hidden text-ellipsis break-all">
              {" "}
              {item.discountTitle}
            </p>
          </div>
          <div className="text-base text-customGray">카테고리 {item.discountType}</div>
        </div>

        {/* 할인률 */}
        <div>
          <div className="flex items-center justify-between font-normal text-sm text-customLightTextColor -mb-2">
            <p>
              {/* {item.discountRate}%{" "} */}
              <div className="inline-block w-[75px]">{""}</div>
            </p>
          </div>

          {/* 가격 */}
          <div className="flex flex-row gap-1 place-items-end">
            <div className="text-red-500 text-2xl font-extrabold">{item.discountRate}%</div>
            <div>
              <p className="line-through text-customGray -mb-2 text-sm">
                {formatKoreanCurrency(item.originalPrice)}
              </p>
              <p className="text-xl font-bold">{formatKoreanCurrency(item.discountedPrice)}</p>
            </div>
          </div>
        </div>

        {/* 닉네임 태그 */}
        <div className="flex items-center w-full rounded-3xl bg-customBgLightBlue h-[13%]">
          <div
            className="bg-customBgBlue flex items-center justify-center
            h-full rounded-3xl w-[40%] text-customBasic ml-1"
          >
            {item.discountOwnerNickname ? "소상공인" : "개인"}
          </div>
          <div className=" flex items-center w-[60%] justify-start overflow-hidden flex-grow whitespace-nowrap pl-[5px] ">
            <p className="truncate">{item.discountOwnerNickname}</p>
          </div>
        </div>

        {/* 경매종료시간 */}
        <div className="flex items-center justify-between text-[16px]">
          <AuctionCountDown
            currentTime={nowTime!}
            auctionEndTime={item.discountEnd}
            stateHandler={auctionStateHandler}
          />
        </div>
      </div>
    </div>
  );
};

export default DiscountListCard;
