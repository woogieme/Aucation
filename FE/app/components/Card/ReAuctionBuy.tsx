"use client";

import React, { useState } from "react";
import Image from "next/image";
import LikeBtn from "../../detail/components/LikeBtn";
import { callApi } from "@/app/utils/api";
import formatKoreanCurrency from "../../utils/formatKoreanCurrency";
import { useRouter } from "next/navigation";
import sellfinish from "@/app/images/sellfinish.png";
import { BsChatRightDots } from "react-icons/bs";
import { BiMap } from "react-icons/bi";
import toast from "react-hot-toast";
interface ItemType {
  // v 이미지,제목, 시작가, 판매자, 입찰가, 입찰날짜, 등록일, 좋아요
  imgfile: string;
  auctionTitle: string;
  auctionStartPrice: number;
  customerNickname: string;
  reAucBidDateTime: string;
  registerDate: string;
  isLike: boolean;
  // v 낙찰 상태 여부, 일시, 구매확정일, 낙찰가
  auctionHistory: string;
  historyDateTime?: string;
  historyDoneDateTime?: string;
  auctionSuccessPay: number;
  // 구매자
  ownerNickname: string;
  // 경매장 입장 uuid, 채팅방 상품pk
  auctionUUID: string;
  auctionPk: number;
  // x 역경매인지 아닌지 state, type
  auctionStatus: string;
  auctionType: string;
  // x 시,구,동
  mycity: string;
  zipcode: string;
  street: string;
  // x 구매자pk(사려고 만든 사람) ,소비자pk(팔려고 입찰에 참여한 사람)
  ownerPk: number;
  customerPk: number;
  // 경매 시작시간
  auctionStartDate: string;
  // 최저가
  reAucBidPrice: number;
  // 입찰 수
  reauctionCount: number;
}
interface CardProps {
  item: ItemType;
  deleteHandler: (prodPk: number) => void;
  confirmHandler: (type:string, discount?:string ,auctionPk?: number) => void
}

const ReAuctionBuy: React.FC<CardProps> = ({ item, deleteHandler, confirmHandler }) => {
  const router = useRouter();
  const [isLiked, setIsLiked] = useState<boolean>(item.isLike);
  const [prodType, setProdType] = useState<string>("1");
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
    router.push(`reverseauction/${item.auctionPk}`);
  };

  const toChat = (e: React.MouseEvent<HTMLDivElement>) => {
    e.stopPropagation();
    router.push(`dm/${item.auctionPk}/${prodType}`);
  };

  // const toConfirm = (e: React.MouseEvent<HTMLDivElement>) => {
  //   e.stopPropagation();
  //   const data = {
  //     reAuctionPk: item.auctionPk,
  //   };
  //   callApi("post", "/reauction/confirm", data)
  //     .then(res => {
  //       res.data;
  //     })
  //     .catch(err => {
  //       err;
  //     });
  // };

  const cardDelete = (e: React.MouseEvent<HTMLDivElement>) => {
    e.stopPropagation();
    deleteHandler(item.auctionPk);
  };

  const reAuctionConfirm = (e: React.MouseEvent<HTMLDivElement>) => {
    e.stopPropagation();
    confirmHandler("REVERSE_BID", undefined, item.auctionPk);
  };
  return (
    <>
      <div className="flex rounded-lg overflow-hidden shadow-lg bg-customBasic w-full h-[300px] mt-12 hover:cursor-pointer border hover:border-blue-400 transition-all duration-150">
        {/* 카드 이미지 */}
        {item.historyDoneDateTime !== null ? (
          <div>
            <div className="relative w-[300px] h-[300px]">
              <Image
                layout="fill"
                alt={item.auctionTitle}
                className="transition-transform transform duration-300 hover:scale-110"
                src={item.imgfile}
                onClick={toDetail}
                style={{ filter: "brightness(50%)" }}
              />
              {/* 거래완료 도장 */}
              <div className="absolute top-10 left-[25%]">
                <Image width={160} height={192} src={sellfinish.src} alt="sellfinish" />
              </div>
            </div>
          </div>
        ) : (
          <div>
            <div className="relative w-[300px] h-[300px]">
              {item.historyDateTime == null && (
                <Image
                  layout="fill"
                  alt={item.auctionTitle}
                  className="transition-transform transform duration-300 hover:scale-110"
                  src={item.imgfile}
                  onClick={toDetail}
                />
              )}
              {item.historyDateTime !== null && item.historyDoneDateTime == null && (
                <Image
                  layout="fill"
                  alt={item.auctionTitle}
                  className="transition-transform transform duration-300 hover:scale-110"
                  src={item.imgfile}
                  onClick={toDetail}
                  style={{ filter: "brightness(50%)" }}
                />
              )}
              <div className="absolute top-3 right-4">
                <LikeBtn isLiked={isLiked} likeHandler={likeHandler} />
              </div>
            </div>
          </div>
        )}

        {/* 본문 */}
        <div className="w-full px-5 py-3 flex flex-col justify-between" onClick={toDetail}>
          {/* 경매 상태 / 경매 마크 /*/}
          <div className="flex justify-between items-center mb-2">
            <div className="flex text-[16px] gap-4">
              <div className="rounded-xl border-[0.1px] px-3 items-center border-customGray  text-customGray">
                역경매
              </div>
              <div className="">
                {item.historyDateTime == null && <span className="text-red-500 ">경매중</span>}
                {item.historyDateTime !== null && item.historyDoneDateTime == null && (
                  <span className="text-customBlue">입찰완료</span>
                )}
                {item.historyDoneDateTime !== null && <span>경매종료</span>}
              </div>
            </div>
            <div className="text-customGray text-sm">
              등록일 &nbsp;{new Date(item.registerDate).toLocaleString()}
            </div>
          </div>

          {/* 카드 제목 */}
          <div className="text-3xl max-h-[100px] font-bold break-all overflow-hidden text-ellipsis">
            {item.auctionTitle}
          </div>

          {/* 시작가/ 입찰완료, 경매종료도 시작가 출력 */}
          {/* {item.historyDateTime == null && ( */}
          <div>
            <div className="text-[16px] text-customGray mt-1 -mb-1">
              <span className="ml-[100px]">{" "}</span>
              시작가{" "}
              {/* <span className="text-[25px] font-bold text-blue-500"> */}
                {" "}

                <span className=" line-through">{formatKoreanCurrency(item.auctionStartPrice)}</span>
              {/* </span> */}
            </div>
            {/* )} */}

            {/* 입찰완료/경매종료 - 판매자 */}
            {/* {((item.historyDateTime !== null && item.historyDoneDateTime == null) ||
              item.historyDoneDateTime !== null) && (
              <div className="flex h-[45px]">
                <div className="flex items-center">판매자 :&nbsp;</div>
                <div>
                  <Link
                    href={`/other/${item.customerNickname}`}
                    className="text-customLightTextColor text-lg hover:underline"
                  >
                    <span className="text-3xl font-bold">{item.customerNickname}</span>
                  </Link>
                </div>
              </div>
            )} */}

            {/* 경매중 - 최저가 */}
            {item.historyDateTime == null && (
              <div className="flex text-2xl items-center">
                최저가 &nbsp;
                <span className="font-bold text-red-500">
                  {formatKoreanCurrency(item.reAucBidPrice)}
                </span>
              </div>
            )}
            {/* 입찰완료/경매종료 - 최종가 */}
            {((item.historyDateTime !== null && item.historyDoneDateTime == null) ||
              item.historyDoneDateTime !== null) && (
              <div className="flex text-2xl items-center">
                최종가 &nbsp;
                <span className="font-bold text-red-500">
                  {formatKoreanCurrency(item.auctionSuccessPay)}
                </span>
              </div>
            )}
          </div>
          {/*               <div className="flex text-[22px] items-center text-red-500">
                  확정일 :&nbsp;
                  <span className="text-[28px]">
                    {new Date(item.historyDoneDateTime!).toLocaleString()}
                  </span>
                </div> */}
          {/* 입찰날짜, 입찰완료, 경매종료도 출력*/}
          {/* {item.historyDateTime == null && ( */}

          <div className="flex">
            <div className="flex text-[22px] items-center text-customLightTextColor">
              <span className="font-bold">{item.reauctionCount}&nbsp;</span>
              {item.historyDateTime == null ? "명 입찰중" : "명 참여"}
            </div>
          </div>
          {/* )} */}

          <div className="flex items-center h-[55px] justify-between">
            <div className="flex text-sm text-customGray">
              <BiMap size={25} />
              <span className="ml-2">
                {item.mycity}&nbsp;{item.street}&nbsp;{item.zipcode}
              </span>
            </div>
            {/* 경매중 */}
            {item.historyDateTime == null && (
              <div className="flex gap-3 mb-8">
                <span
                  className="flex items-center border-[1px] rounded-2xl mb-8 border-red-500  text-red-500
                  text-lg font-bold py-1 px-3 cursor-pointer
                  hover:scale-105 transition-all"
                  onClick={cardDelete}
                >
                  삭제하기
                </span>
                <span
                  className="border-[1px] mb-8 border-customGray cursor-pointer rounded-2xl 
                text-customLightTextColor text-lg font-bold py-1 px-3
                hover:scale-105 hover:text-customBlue hover:border-customBlue transition-all"
                >
                  입찰보기
                </span>
              </div>
            )}
            {/* 입찰완료 */}
            {item.historyDateTime !== null && item.historyDoneDateTime == null && (
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
                <div
                  className="border-[1px] border-customGray px-3 mb-8 py-1 text-lg rounded-2xl
                 font-bold text-customLightTextColor
                 hover:scale-105 hover:text-customBlue hover:border-customBlue transition-all"
                  onClick={reAuctionConfirm}
                >
                  확정
                </div>
              </div>
            )}
            {/* 경매종료 */}
            {/* {item.historyDoneDateTime !== null && (
              <div className="flex gap-3 mb-8">
                <div
                  className="flex items-center border-[1px] rounded-2xl mb-8 border-customGray  text-customLightTextColor
                   text-lg font-bold py-1 px-3 cursor-pointer
                   hover:scale-105 hover:text-customBlue hover:border-customBlue transition-all"
                  onClick={toChat}
                >
                  <BsChatRightDots size={22} />
                  <span className="ml-2">채팅</span>
                </div>
              </div>
            )} */}
          </div>
        </div>
      </div>
    </>
  );
};

export default ReAuctionBuy;
