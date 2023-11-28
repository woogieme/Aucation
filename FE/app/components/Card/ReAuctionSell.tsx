"use client";

import React, { useState } from "react";
import Image from "next/image";
import LikeBtn from "../../detail/components/LikeBtn";
import sellfinish from "@/app/images/sellfinish.png";
import { callApi } from "@/app/utils/api";
import formatKoreanCurrency from "../../utils/formatKoreanCurrency";
import { useRouter } from "next/navigation";
import { BsChatRightDots } from "react-icons/bs";
import { BiMap } from "react-icons/bi";
import toast from "react-hot-toast";
interface ItemType {
  // v 이미지,제목, 시작가, 판매자, 입찰가, 입찰날짜, 등록일, 좋아요
  imgfile: string;
  auctionTitle: string;
  auctionStartPrice: number;
  customerNickname: string;
  reAucBidPrice: number;
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
}

interface CardProps {
  item: ItemType;
  deleteHandler: (prodPk: number) => void;
}

const ReAuctionSell: React.FC<CardProps> = ({ item, deleteHandler }) => {
  const router = useRouter();
  const [isLiked, setIsLiked] = useState<boolean>(item.isLike);
  const [prodType, setProdType] = useState<string>("1");
  const likeHandler = (newLikeStatus: boolean) => {
    setIsLiked(newLikeStatus); // 옵티미스틱 업데이트

    callApi("get", `/auction/like/${item.auctionPk}`)
      .then(response => {
        toast.success(response.data.message);
      })
      .catch(error => {
        toast.error(error.response.data.message);
      });
  };
  const toDetail = () => {
    router.push(`reverseauction/${item.auctionPk}`);
  };

  const toChat = (e: React.MouseEvent<HTMLDivElement>) => {
    e.stopPropagation();
    router.push(`dm/${item.auctionPk}/${prodType}`);
  };

  const cardDelete = (e: React.MouseEvent<HTMLDivElement>) => {
    e.stopPropagation();
    deleteHandler(item.auctionPk);
  };
  return (
    <>
      {/* w-[1280px] */}
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
              <Image
                className="transition-transform transform duration-300 hover:scale-110"
                src={item.imgfile}
                alt={item.auctionTitle}
                layout="fill"
                onClick={toDetail}
              />
              <div className="absolute top-3 right-4">
                <LikeBtn isLiked={isLiked} likeHandler={likeHandler} />
              </div>
            </div>
          </div>
        )}

        {/* [900px] */}
        <div className="w-full px-5 py-3 flex flex-col justify-between" onClick={toDetail}>
          {/* 경매 상태 / 경매 마크 /*/}
          <div className="flex justify-between items-center">
            <div className="flex text-[16px] gap-4">
              <span className="rounded-xl border-[0.1px] px-3 items-center border-customGray  text-customGray">
                역경매
              </span>
              <div className="">
                {item.historyDateTime == null && <span className="text-red-500">입찰중</span>}
                {item.historyDateTime !== null && item.historyDoneDateTime == null && (
                  <span className="text-customBlue">낙찰</span>
                )}
                {item.historyDoneDateTime !== null && <span>거래완료</span>}
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

          {/* 구매자 */}
          {/* <div className="mt-2 text-[24px] font-semibold">
            구매자 :
            <Link
                href={`/other/${item.ownerNickname}`}
                className="text-customLightTextColor text-lg hover:underline"
              >
                <span className="text-3xl font-bold">{item.ownerNickname}</span>
              </Link>
          </div> */}

          {/* 입찰가 */}
          <div className="text-xl">
            입찰가&nbsp;
            <span className="font-bold text-red-500">
              &nbsp;{formatKoreanCurrency(item.reAucBidPrice)}
            </span>{" "}
          </div>

          {/* 입찰날짜 / 삭제버튼*/}

          <div className="flex  text-customGray">
            <div className="flex text-[22px] items-center">
              {item.historyDateTime === null && (
                <div>
                  입찰 날짜 &nbsp;
                  <span className="text-[22px]">
                    {new Date(item.reAucBidDateTime).toLocaleString()}
                  </span>
                </div>
              )}
              {item.historyDateTime !== null && item.historyDoneDateTime == null && (
                <div>
                  {" "}
                  낙찰 날짜 &nbsp;
                  <span className="text-[22px]">
                    {new Date(item.historyDateTime!).toLocaleString()}
                  </span>
                </div>
              )}

              {item.historyDoneDateTime !== null && (
                <div>
                  낙찰 확정일 &nbsp;
                  <span className="text-[22px]">
                    {new Date(item.historyDoneDateTime!).toLocaleString()}
                  </span>
                </div>
              )}
            </div>
          </div>
          <div className="flex items-center h-[55px] justify-between">
            <div className="flex text-sm text-customGray">
              <BiMap size={25} />
              <span className="ml-2 text-[16px]">
                {item.mycity}&nbsp;{item.street}&nbsp;{item.zipcode}
              </span>
            </div>
            {item.historyDateTime == null && (
              <div className="flex gap-3 mb-8 ">
                <div
                  className="flex items-center border-[1px] rounded-2xl mb-8 border-red-500  text-red-500
                  text-lg font-bold py-1 px-3 cursor-pointer
                  hover:scale-105 transition-all"
                  onClick={cardDelete}
                >
                  삭제하기
                </div>
                <span
                  className="border-[1px] border-customGray cursor-pointer rounded-2xl 
                text-customLightTextColor text-lg font-bold py-1 px-3 mb-8
                hover:scale-105 hover:text-customBlue hover:border-customBlue transition-all"
                >
                  입찰보기
                </span>
              </div>
            )}
            {item.historyDateTime !== null && item.historyDoneDateTime == null && (
              <div className="flex gap-3 mb-8 ">
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
            )}
          </div>
        </div>
      </div>
    </>
  );
};

export default ReAuctionSell;
