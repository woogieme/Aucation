"use client";

import React, { useState } from "react";
import nakchalImg from "@/app/images/nakchal.png";
import LikeBtn from "../../detail/components/LikeBtn";
import Image from "next/image";
import { GoReport } from "react-icons/go";
import { callApi } from "@/app/utils/api";
import formatKoreanCurrency from "../../utils/formatKoreanCurrency";
import { BiMap } from "react-icons/bi";
import { useRouter } from "next/navigation";
import { BsChatRightDots } from "react-icons/bs";
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
  thirdCategory?: string;
  deleteHandler: (prodPk: number) => void;
}
const DiscountSell: React.FC<CardProps> = ({ item, thirdCategory, deleteHandler }) => {
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

  const cardDelete = (e: React.MouseEvent<HTMLDivElement>) => {
    e.stopPropagation();
    deleteHandler(item.discountPk);
  };
  return (
    <>
      <div className="flex rounded-lg overflow-hidden shadow-lg bg-customBasic w-full h-[300px] mt-12 hover:cursor-pointer border hover:border-blue-400 transition-all duration-150">
        {/* 카드 이미지 */}
        {thirdCategory == "판매완료" ? (
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
                <Image width={160} height={192} src={nakchalImg.src} alt="dojang" />
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
              <div className="">{thirdCategory}</div>
            </div>
            <div className="text-xl">
              {/* 등록일 &nbsp;&nbsp; */}
              {/* {auctionStartTime.toLocaleString()} */}
            </div>
          </div>
          {/* 카드 제목 */}
          <div className="text-3xl max-h-[100px] font-bold break-all overflow-hidden text-ellipsis">
            {/* 제목 */}
            {item.discountTitle}
          </div>
          {/* 구매자 */}
          {/* {(thirdCategory == "예약중" || thirdCategory == "판매완료") && (
            <div className="flex mt-2 text-[22px] font-semibold">
              <div className="flex items-center">구매자 :&nbsp;</div>
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
          {/* 가격 */}
          <div className="flex text-2xl flex-row place-items-end">
            {/* 가격 :{" "} */}
            <div className="flex font-semibold justify-center">
              {/* 할인율 */}
              {item.discountRate && (
                <div className="flex text-red-500">
                  {item.discountRate.toLocaleString()}%
                </div>
              )}
            </div>
            {/* 원가 */}
            <div>
              <p className="ml-2 line-through text-customGray text-base">
                {formatKoreanCurrency(item.discountPrice).toLocaleString()}
              </p>
              <p className="text-2xl font-bold">
                &nbsp;{formatKoreanCurrency(item.discountDiscountedPrice)}
              </p>
            </div>
          </div>

          {/*  / / 채팅 및 신고 버튼 */}
          <div className="flex text-xl items-center justify-between text-customGray">
            {item.historyDatetime !== null && item.historyDoneDatetime == null && (
              <div className="flex items-center">
                낙찰 일시  <span>{new Date(item.historyDatetime).toLocaleString()}</span>
              </div>
            )}
            {item.historyDoneDatetime !== null && (
              <div className="flex items-center">
                확정 일시  <span>{new Date(item.historyDoneDatetime).toLocaleString()}</span>
              </div>
            )}
            {item.historyDatetime == null && (
              <div className="flex h-[50px] items-center text-sm">
                <div className="flex items-center">
                  <BiMap size={25} />
                  &nbsp;
                  <span>
                    {item.mycity}&nbsp;
                    {item.zipcode}&nbsp;
                    {item.street}
                  </span>
                </div>
              </div>
            )}
            {item.historyDatetime == null && (
              <div 
              className="flex items-center border-[1px] rounded-2xl border-red-500  text-red-500
              text-lg font-bold py-1 px-3 cursor-pointer
              hover:scale-105 transition-all"
              onClick={cardDelete}>
                삭제하기
              </div>
            )}
          </div>
          {item.historyDatetime !== null && (
            <div className="flex items-center h-[50px] justify-between text-sm">
              <div>
                <div className="flex items-center">
                  <BiMap size={25} />
                  <span className="ml-2">
                    {item.mycity}&nbsp;{item.street}&nbsp;{item.zipcode}
                  </span>
                </div>
              </div>
              <div className="flex gap-3">
                {/* <div className="flex items-center border-[0.1px] rounded-lg mb-8 border-red-300 text-red-500 text-2xl font-bold py-1 px-3 cursor-pointer">
                  {" "}
                  <GoReport size={25} color="#EC4747" />
                  <span className="ml-1">신고하기</span>
                </div> */}
                <div
                  className="flex items-center border-[1px] border-customGray text-customLightTextColor 
                  rounded-2xl mb-8  text-lg font-bold py-1 px-3 cursor-pointer
                  hover:scale-105 hover:text-customBlue hover:border-customBlue transition-all"
                  onClick={toChat}
                >
                  <BsChatRightDots size={22} />
                  <span className="ml-2">채팅</span>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </>
  );
};

export default DiscountSell;
