"use client";

import React, { useState } from "react";
import nakchalImg from "@/app/images/nakchal.png";
import LikeBtn from "../../detail/components/LikeBtn";
import Image from "next/image";
import { callApi } from "@/app/utils/api";
import formatKoreanCurrency from "../../utils/formatKoreanCurrency";
import { BiMap } from "react-icons/bi";
import { useRouter } from "next/navigation";
import { BsChatRightDots } from "react-icons/bs";
import clsx from "clsx";
import toast from "react-hot-toast";
interface ItemType {
  // 이미지, 등록일 x, 제목 x, 정가, 할인가, 좋아요, 할인률
  imgfile: string;
  discountTitle: string;
  discountPrice: number;
  discountDiscountedPrice: number;
  isLike: boolean;
  discountRate?: number;
  // 구매자
  customerNickname: string;
  // x 마감시간
  discountEnd: string;
  // 제품 uuid, pk
  discountUUID: string;
  discountPk: number;
  // 낙찰 상태, 일시, 확정일
  historyStatus: string;
  historyDatetime: string;
  historyDoneDatetime: string;
  // 유저pk, 소상공인pk
  discountCustomerPk: number;
  discountOwnerPk: number;
  // x 시,구,동
  mycity: string;
  zipcode: string;
  street: string;
}

interface CardProps {
  item: ItemType;
  confirmHandler: (type: string, UUID: string) => void
}
const DiscountBuy: React.FC<CardProps> = ({ item, confirmHandler }) => {
  const router = useRouter();
  const [isLiked, setIsLiked] = useState<boolean>(item.isLike);
  const [prodType, setProdType] = useState<string>("2");
  const likeHandler = (newLikeStatus: boolean) => {
    setIsLiked(newLikeStatus); // 옵티미스틱 업데이트

    callApi("get", `/discount/like/${item.discountPk}`)
      .then(response => {
        toast.success(response.data.message)
      })
      .catch(error => {
        toast.error(error.response.data.message)
      });
  };
  const toDetail = () => {
    router.push(`detail/discount/${item.discountPk}`);
  };

  const toChat = (e: React.MouseEvent<HTMLDivElement>) => {
    e.stopPropagation();
    router.push(`dm/${item.discountPk}/${prodType}`);
  };

  // const toConfirm = (e:React.MouseEvent<HTMLDivElement>) => {
  //   e.stopPropagation()
  //   callApi('get', `/discount/confirm/${item.discountUUID}`)
  //   .then((res) => {
  //     toast.success(res.data.message)
  //     console.log(res)
  //   })
  //   .catch((err) => {
  //     toast.error(err.response.data.message)
  //     console.log(err)
  //   })
  // }

  const discountConfirm = (e: React.MouseEvent<HTMLDivElement>) => {
    e.stopPropagation();
  
    confirmHandler("DISCOUNT_BID", item.discountUUID);
  };
  return (
    <>
      <div className="flex rounded-lg overflow-hidden shadow-lg bg-customBasic w-full h-[300px] mt-12 hover:cursor-pointer border hover:border-blue-400 transition-all duration-150">
        {/* 카드 이미지 */}
        {item.historyStatus == "AFTER_CONFIRM" ? (
          <div>
            <div className="relative w-[300px] h-[300px]">
              <Image
                layout="fill"
                alt={item.discountTitle}
                className="transition-transform transform duration-300 hover:scale-110"
                src={item.imgfile}
                onClick={toDetail}
                style={{ filter: "brightness(50%)" }}
              />
              <div className="absolute top-10 left-[23%]">
                <Image width={160} height={192} src={nakchalImg.src} alt="nakchal" />
              </div>
            </div>
          </div>
        ) : (
          <div>
            <div className="relative w-[300px] h-[300px]">
              <Image
                layout="fill"
                alt={item.discountTitle}
                className="transition-transform transform duration-300 hover:scale-110"
                src={item.imgfile}
                onClick={toDetail}
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
                할인
              </div>
              {item.historyStatus == "BEFORE_CONFIRM" ? (
                <span className="text-customBlue">예약중</span>
              ) : (
                <span>구매완료</span>
              )}
            </div>
            <div className="text-customGray text-sm">
              등록일 :&nbsp;&nbsp;
              {/* {auctionStartTime.toLocaleString()} */}
            </div>
          </div>
          {/* 카드 제목 */}
          <div className="text-3xl max-h-[100px] font-bold break-all overflow-hidden text-ellipsis">
            {item.discountTitle}
          </div>

          {/* 판매자 */}
          {/* <div className="flex mt-3 text-[22px] font-semibold">
            <div className="flex items-center">판매자 :&nbsp;</div>
            <div>
            <Link
                href={`/other/${item.}`}
                className="text-customLightTextColor text-lg hover:underline"
              >
                <span className="text-3xl font-bold">{item.}</span>
              </Link>
            </div>
          </div> */}

          {/* 가격 */}
          <div className="flex h-[55px] text-xl place-items-end">
            가격 :{" "}
            <div className="text-black">
              <div className="flex text-[24px] font-semibold justify-center">
                {item.discountRate && (
                  <div className="flex text-red-500">
                    &nbsp;{item.discountRate.toLocaleString()}%&nbsp;
                  </div>
                )}
              </div>
              <span className="ml-2 line-through">
                {formatKoreanCurrency(item.discountPrice).toLocaleString()}
              </span>
            </div>
            &nbsp; {"->"}{" "}
            <span className="text-2xl font-bold">
              &nbsp;{formatKoreanCurrency(item.discountDiscountedPrice)}
            </span>
          </div>

          {/* 낙찰일시 / 낙찰가 / 채팅 및 확정 버튼*/}
          <div className="flex h-[40px] text-[22px] items-center text-customGray">
            <div>
              {item.historyStatus == "BEFORE_CONFIRM" ? "예약일" : "구매일시"} : &nbsp;
              {new Date(item.historyDatetime).toLocaleString()}
            </div>
          </div>
          {/* 지역 */}
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
              {item.historyStatus ==("BEFORE_CONFIRM" || "NOT_SELL")&& (
                <div className="border-[1px] border-customGray px-3 mb-8 py-1 text-lg rounded-2xl
                font-bold text-customLightTextColor
                hover:scale-105 hover:text-customBlue hover:border-customBlue transition-all"
                onClick={discountConfirm}>
                 확정
               </div>
              )}


              {/* {item.historyStatus == ("BEFORE_CONFIRM" || "NOT_SELL") && (
                <div className="border-2 px-3 mb-8 py-1 text-2xl rounded-lg cursor-pointer" onClick={discountConfirm}>확정</div>
              )} */}
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default DiscountBuy;
