"use client";

import React, { useState } from "react";
import { useRouter } from "next/navigation";
import LikeBtn from "../../detail/components/LikeBtn";
import { BsFillPersonFill } from "react-icons/bs";
import Image from "next/image";
import ColCountDown from "./ColCountDown";
import { AuctionItem, ReverseAuctionItem } from "@/app/components/Card/cardType";
import formatKoreanCurrency from "@/app/utils/formatKoreanCurrency";
import { callApi } from "@/app/utils/api";
import AuctionCountDown from "./AuctionCountDown";
interface CardProps {
  item: AuctionItem | ReverseAuctionItem;
  nowTime: Date | null;
  type?: string;
}

const AuctionListCard: React.FC<CardProps> = ({ item, nowTime, type = "auction" }) => {
  const router = useRouter();
  const [likeCount, setLikeCount] = useState<number>(
    type === "reverse" ? (item as ReverseAuctionItem).likeCnt : (item as AuctionItem).likeCnt
  );
  const [isLiked, setIsLiked] = useState<boolean>(
    type === "reverse" ? (item as ReverseAuctionItem).isLike : (item as AuctionItem).isLike
  );
  // const tmp = nowTime ? new Date(nowTime) : null;
  const [auctionState, setAuctionState] = useState<string>("마감시간");

  const auctionStateHandler = (value: string) => {
    setAuctionState(value);
  };

  const likeHandler = (newLikeStatus: boolean) => {
    setIsLiked(newLikeStatus); // 옵티미스틱 업데이트
    setLikeCount(newLikeStatus ? likeCount + 1 : likeCount - 1); // 좋아요 수 변경

    if (type === "reverse" && "reAuctionPk" in item) {
      const apiEndpoint = `/auction/like/${item.reAuctionPk}`;

      callApi("get", apiEndpoint, { auctionPk: item.reAuctionPk })
        .then(response => {
          console.log("좋아요 성공", response);
        })
        .catch(error => {
          console.log("좋아요 실패", error);
        });
    } else if (type === "auction" && "auctionPk" in item) {
      const apiEndpoint = `/auction/like/${item.auctionPk}`;

      callApi("get", apiEndpoint, { auctionPk: item.auctionPk })
        .then(response => {
          console.log("좋아요 성공", response);
        })
        .catch(error => {
          console.log("좋아요 실패", error);
        });
    }
  };
  const EnterDetail = (pk: number) => {
    if (type === "auction") {
      router.push(`/detail/auction/${pk}`);
    } else {
      router.push(`/reverseauction/${pk}`);
    }
  };
  const EnterBid = () => {
    router.push(`/bid/${(item as AuctionItem).auctionUUID}`);
  };
  return (
    <div className=" overflow-hidden h-full w-full rounded-lg shadow-lg bg-white hover:border-sky-500 hover:ring-8 hover:ring-sky-200 hover:ring-opacity-100">
      <div className="h-1/2 relative">
        <Image
          onClick={() => {
            EnterDetail("reAuctionPk" in item ? item.reAuctionPk : item.auctionPk);
          }}
          src={"reAuctionPk" in item ? item.reAuctionImg : item.auctionImg}
          alt={"reAuctionPk" in item ? item.reAuctionTitle : item.auctionTitle}
          layout="fill"
          objectFit="cover"
          objectPosition="center"
          className="cursor-pointer transition-transform transform duration-300 hover:scale-110"
        />
        <div className="absolute top-2 right-2">
          <LikeBtn isLiked={isLiked} likeHandler={likeHandler} />
        </div>
        {type === "auction" && (
          <div
            onClick={() => EnterBid()}
            className="rounded-lg absolute bottom-2 left-2 px-3 py-2 bg-customBgBlue hover:bg-custom-btn-gradient-hover text-white cursor-pointer border-1"
          >
            바로입장
          </div>
        )}
      </div>
      {/* 본문 */}
      <div className="h-1/2 px-3 pt-2 pb-3 flex flex-col place-content-between bg-customBasic">
        {/* 좋아요 */}
        <div className="flex items-center justify-between">
          <p className="text-sm text-customGray"> 좋아요 {likeCount} 개</p>
          <p className="text-sm text-customGray">
            {"reAuctionPk" in item
              ? "입찰자 " + item.reAuctionBidCnt
              : "참여자 " + item.auctionCurCnt}{" "}
            명
          </p>
        </div>

        {/* 제목과 카테고리*/}
        <div>
          <div
            onClick={() => {
              EnterDetail("reAuctionPk" in item ? item.reAuctionPk : item.auctionPk);
            }}
            className="cursor-pointer flex items-center justify-between font-extrabold text-2xl"
          >
            <p className="max-h-[80px] overflow-hidden text-ellipsis break-all">
              {" "}
              {"reAuctionPk" in item ? item.reAuctionTitle : item.auctionTitle}
            </p>
          </div>
          <div className="text-base text-customGray">
            카테고리 {"reAuctionPk" in item ? item.reAuctionType : item.auctionType}
          </div>
        </div>

        {/* 가격 */}
        <div>
          <div className="flex items-center justify-between text-sm text-customGray -mb-1">
            <p>
              시작가{" "}
              {formatKoreanCurrency(
                "reAuctionPk" in item ? item.reAuctionStartPrice : item.auctionStartPrice
              )}
            </p>
          </div>
          <div className="flex items-center justify-between mb-1 font-bold text-xl">
            <p>
              {" "}
              입찰가{" "}
              <span className="text-customBlue">
                {formatKoreanCurrency(
                  "reAuctionPk" in item ? item.reAuctionLowBidPrice : item.auctionTopBidPrice
                )}
              </span>
            </p>
          </div>
        </div>

        {/* 닉네임 태그 */}
        <div className="flex items-center w-full rounded-3xl bg-customBgLightBlue h-[13%]">
          <div
            className="bg-customBgBlue flex items-center justify-center
            h-full rounded-3xl w-[40%] text-customBasic ml-1 "
          >
            {("reAuctionPk" in item ? item.reAuctionOwnerIsShop : item.auctionOwnerIsShop)
              ? "소상공인"
              : "개인"}
          </div>
          <div className=" flex items-center w-[60%] justify-start overflow-hidden flex-grow whitespace-nowrap pl-[5px]">
            <p className="truncate">
              {"reAuctionPk" in item ? item.reAuctionOwnerNickname : item.auctionOwnerNickname}
            </p>
          </div>
        </div>

        {/* 경매종료시간 */}
        <div className="flex items-center justify-between text-[16px]">
          <AuctionCountDown
            currentTime={nowTime!}
            auctionEndTime={"reAuctionPk" in item ? item.reAuctionEndTime : item.auctionEndTime}
            stateHandler={auctionStateHandler}
          />
        </div>
      </div>
    </div>
  );
};

export default AuctionListCard;
