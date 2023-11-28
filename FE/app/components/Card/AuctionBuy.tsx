"use client";

import React, { useState } from "react";
import sellfinish from "@/app/images/sellfinish.png";
import LikeBtn from "../../detail/components/LikeBtn";
import Image from "next/image";
import clsx from "clsx";
import { callApi } from "@/app/utils/api";
import formatKoreanCurrency from "../../utils/formatKoreanCurrency";
import { useRouter } from "next/navigation";
import { BsChatRightDots } from "react-icons/bs";
import { BiMap } from "react-icons/bi";
import toast from "react-hot-toast";
interface ItemType {
  // 이미지
  imgfile: string;
  // 좋아요 여부
  isLike: boolean;
  // 경매 상태
  // x 경매, 역경매 여부 /BID
  auctionStatus: string;
  // 등록일
  registerDate: Date;
  // 제목
  auctionTitle: string;
  // x 판매자 닉네임
  ownerNickname: string;
  // 시작가
  auctionStartPrice: number;
  // 경매 시작시간,종료시간
  auctionStartDate: Date;
  auctionEndDate: Date;
  // 경매장 입장
  auctionUUID: string;
  // prodPk - 채팅방
  auctionPk: number;
  // x 낙찰 여부
  // x 경매전- null / b
  // 낙찰이 되면 경매종료시점에서 내가 최고 입찰자이다? 그럼 BEFORE_CONFIRM, 구매 확정을 했을 경우 AFTER_CONFIRM
  auctionHistory?: string;
  // 낙찰일시 - BEFORE_CONFIRM
  historyDatetime: Date;
  // 구매확정 일시 - 구매 확정
  historyDoneDateTime: Date;
  // 최종가
  auctionSuccessPay: number;
  // x 지역
  mycity: string;
  zipcode: string;
  street: string;
  // x 판매자 pk
  ownerPk: number;
  // x 카테고리
  auctionType: string;
  // x 구매자 닉네임
  customerNicknname: string;
}

interface CardProps {
  item: ItemType;
  confirmHandler: (type:string, discount?:string ,auctionPk?: number) => void
}

const AuctionBuy: React.FC<CardProps> = ({ item, confirmHandler }) => {
  const router = useRouter();
  const [isLiked, setIsLiked] = useState<boolean>(item.isLike);
  const [prodType, setProdType] = useState<string>("0");

  const likeHandler = (newLikeStatus: boolean) => {
    setIsLiked(newLikeStatus); // 옵티미스틱 업데이트

    callApi("get", `/auction/like/${item.auctionPk}`)
      .then(response => {
        toast.success(response.data.message)
      })
      .catch(error => {
        toast.error(error.response.data.message)
      });
  };
  const toDetail = () => {
    router.push(`/detail/auction/${item.auctionPk}`);
  };
  const toChat = (e: React.MouseEvent<HTMLDivElement>) => {
    e.stopPropagation();
    router.push(`dm/${item.auctionPk}/${prodType}`);
  };

  
  const auctionConfirm = (e: React.MouseEvent<HTMLDivElement>) => {
    e.stopPropagation();
    confirmHandler("BID", undefined ,item.auctionPk);
  };
  return (
    <>
      <div className="flex rounded-lg overflow-hidden shadow-lg bg-customBasic w-full h-[300px] mt-12 hover:cursor-pointer border hover:border-blue-400 transition-all duration-150">
        {/* 카드 이미지 */}
        {item.auctionHistory == "AFTER_CONFIRM" ? (
          <div>
            <div className="relative w-[300px] h-[300px]">
              <Image
                layout="fill"
                objectFit="cover"
                objectPosition="center"
                className="transition-transform transform duration-300 hover:scale-110"
                src={item.imgfile}
                alt={item.auctionTitle}
                onClick={toDetail}
                style={{ filter: "brightness(50%)" }}
              />
              <div className="absolute top-10 left-[25%]">
                <Image width={160} height={192} src={sellfinish.src} alt="sellfinish" />
              </div>
            </div>
          </div>
        ) : (
          <div>
            <div className="relative w-[300px] h-[300px]">
              <Image
                className="transition-transform transform duration-300 hover:scale-110"
                src={item.imgfile}
                layout="fill"
                objectFit="cover"
                objectPosition="center"
                alt={item.auctionTitle}
                onClick={toDetail}
                style={{ filter: "brightness(50%)" }}
              />
              <div className="absolute top-3 right-4">
                <LikeBtn isLiked={isLiked} likeHandler={likeHandler} />
              </div>
            </div>
          </div>
        )}
        <div className="w-full px-5 py-3 flex flex-col justify-between" onClick={toDetail}>
          {/* 경매 상태 / 경매 마크 / 남은 시간 카운트*/}
          <div className="flex justify-between items-center mb-2">
            <div className="flex text-[16px] gap-4">
            <div className="rounded-xl border-[0.1px] px-3 items-center border-customGray  text-customGray">
                경매
              </div>
              <div
                className={clsx(
                  "",
                  item.auctionHistory == "BEFORE_CONFIRM" ? "text-customBlue" : ""
                )}
              >
                {item.auctionHistory == "BEFORE_CONFIRM" ? "낙찰" : "구매완료"}
              </div>
            </div>
            <div className="text-customGray text-sm">
              <span className="">등록일 &nbsp;&nbsp;</span>
              {new Date(item.registerDate).toLocaleString()}
            </div>
          </div>
          {/* 카드 제목 */}
          <div className="text-3xl max-h-[100px] font-bold break-all overflow-hidden text-ellipsis">
            {item.auctionTitle}
          </div>

          {/* 판매자 */}
          {/* <div className="text-[24px] h-[50px] font-semibold">
            <div className="flex items-center">판매자 페이</div>
            <div>
              <Link
                href={`/other/${item.ownerNickname}`}
                className="text-customLightTextColor text-lg hover:underline"
              >
                <span className="text-3xl font-bold">{item.ownerNickname}</span>
              </Link>
            </div>
          </div> */}

          {/* 시작가, 낙찰가 */}
          <div>
            {/* 경매 시작가 */}
            <div className="text-[22px] font-medium text-customLightTextColor">
              경매 시작가 {" "}
              <span className=" ">
                {item.auctionStartPrice.toLocaleString()}<span className="">원</span>
              </span>
            </div>

            {/* 낙찰일시 / 낙찰가 / 채팅 및 확정 버튼*/}
            <div className="flex text-2xl justify-between items-center font-semibold">
              {item.historyDatetime && (
                <div>
                  낙찰일시 {" "}
                  <span className="font-normal">{item.historyDatetime.toLocaleString()}</span>
                </div>
              )}
              <div>
                낙찰가 {" "}
                <span
                  className={clsx(
                    "font-bold ml-1 ",
                    item.auctionHistory == "AFTER_CONFIRM" ? "text-customBlue" : "text-red-500"
                  )}
                >
                  {formatKoreanCurrency(item.auctionSuccessPay)}
                </span>
                &nbsp;
              </div>
            </div>
          </div>
          {/* 시작가, 낙찰가 끝 */}

          {/* 버튼 */}
          <div className="flex items-center h-[55px] justify-between">
            <div className="flex items-center text-customGray">
              <BiMap size={25} />
              <span className="ml-2 text-[16px]">
                {item.mycity}&nbsp;{item.street}&nbsp;{item.zipcode}
              </span>
            </div>
            <div className="flex gap-3">
              
              <div
                className="flex items-center border-[1px] border-customGray text-customLightTextColor 
                rounded-2xl mb-8  text-lg font-bold py-1 px-3 cursor-pointer
                hover:scale-105 hover:text-customBlue hover:border-customBlue transition-all"
                onClick={toChat}
              >
                <BsChatRightDots size={22} />
                <span className="ml-2">채팅</span>
              </div>
              {/* 확정 메서드 달아줘야함 ex)확정버튼 클릭 시 리디렉션 어디? */}
              {item.auctionHistory == "BEFORE_CONFIRM" && (
                <div className="border-[1px] border-customGray px-3 mb-8 py-1 text-lg rounded-2xl
                font-bold text-customLightTextColor
                hover:scale-105 hover:text-customBlue hover:border-customBlue transition-all"
                onClick={auctionConfirm}>
                 확정
               </div>
                // <div className="border-2 px-3 mb-8 py-1 text-2xl rounded-lg cursor-pointer" onClick={auctionConfirm}>확정</div>
              )}
              
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default AuctionBuy;
