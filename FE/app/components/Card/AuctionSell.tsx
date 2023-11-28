"use client";

import React, { useEffect, useState } from "react";
import sellfinish from "@/app/images/sellfinish.png"
import LikeBtn from "../../detail/components/LikeBtn";
import Image from "next/image";
import clsx from "clsx";
import RowCountDown from "./RowCountDown";
import { useRouter } from "next/navigation";
import { callApi } from "@/app/utils/api";
import formatKoreanCurrency from "../../utils/formatKoreanCurrency"
import {RiAuctionLine} from "react-icons/ri"
import {BsChatRightDots} from "react-icons/bs"
import { BiMap } from "react-icons/bi";
import toast from "react-hot-toast";
interface ItemType {
  // 이미지
  imgfile: string,
  // 좋아요 여부
  isLike:boolean;
  // 경매 상태 
  // x 경매, 역경매 여부 /BID
  auctionStatus: string,
  // 등록일
  registerDate: Date,
  // 제목
  auctionTitle: string,
  // x 판매자 닉네임
  ownerNicknname: string,
  // 시작가
  auctionStartPrice: number,
  // 경매 시작시간,종료시간
  auctionStartDate: Date,
  auctionEndDate: Date,
  // 경매장 입장
  auctionUUID: string,
  // prodPk - 채팅방, 디테일
  auctionPk: number,
  // x 낙찰 여부
  // x 경매전- null / b
  // 낙찰이 되면 경매종료시점에서 내가 최고 입찰자이다? 그럼 BEFORE_CONFIRM, 구매 확정을 했을 경우 AFTER_CONFIRM
  auctionHistory: string,
  // 낙찰일시 - BEFORE_CONFIRM
  historyDateTime: Date,
  // 구매확정 일시 - 구매 확정
  historyDoneDateTime: Date,
  // 최종가
  auctionSuccessPay: number,
  // x 지역
  mycity:string,
  zipcode:string,
  street:string,
  // x 판매자 pk
  ownerPk: number,
  // x 카테고리
  auctionType: string,
  // x 구매자 닉네임
  customerNicknname: string
}

interface CardProps {
  item: ItemType
  deleteHandler: (prodPk:number) => void
}

const AuctionSell: React.FC<CardProps> = ({ item, deleteHandler}) => {
  const router = useRouter();
  // 경매전, 중, 완료 체크
  const [state, setState] = useState<string>("");
  const [isLiked, setIsLiked] = useState<boolean>(item.isLike);
  // 채팅방 - 경매0 역경매1 할인2
  const [prodType, setProdType] = useState<string>("0")

  const stateHandler = (state: string) => {
    setState(state);
  };

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
  }
  const toBid = (e:React.MouseEvent<HTMLDivElement>) => {
    e.stopPropagation()
    router.push(`/bid/${item.auctionUUID}`)
  }
  const cardDelete = (e: React.MouseEvent<HTMLDivElement>) => {
    e.stopPropagation()
    deleteHandler(item.auctionPk)
  }
  const toChat = (e:React.MouseEvent<HTMLDivElement>) => {
    e.stopPropagation()
    router.push(`dm/${item.auctionPk}/${prodType}`)
  }

  return (
    <>
      <div className="flex rounded-lg overflow-hidden shadow-lg bg-customBasic w-full h-[300px] mt-12 hover:cursor-pointer border hover:border-blue-400 transition-all duration-150">
        {/* 카드 이미지 */}
        {item.historyDoneDateTime !== null ? (
          <div>
            <div className="relative w-[300px] h-[300px]">
              <Image
                src={item.imgfile}
                alt={item.auctionTitle}
                onClick={toDetail}
                layout="fill"
                className="transition-transform transform duration-300 hover:scale-110"
                style={{ filter: "brightness(50%)" }}
              />
              <div className="absolute top-10 left-[25%]">
                <Image width={160} height={192} src={sellfinish.src} alt="sellfinish" />
              </div>
            </div>
          </div>
        ) :  state == "종료" && item.historyDoneDateTime === null ? (
          <div>
            
            <div className="relative w-[300px] h-[300px]">
              <Image
                onClick={toDetail}
                layout="fill"
                className="transition-transform transform duration-300 hover:scale-110"
                src={item.imgfile}
                alt={item.auctionTitle}
                style={{ filter: "brightness(50%)" }}
              />
            </div>
        </div>
        ) : (
          <div>
            <div className="relative w-[300px] h-[300px]">
              <Image
                onClick={toDetail}
                className="transition-transform transform duration-300 hover:scale-110 overflow-hidden"
                src={item.imgfile}
                layout="fill"
                alt={item.auctionTitle}
              />
              <div className="absolute top-3 right-4">
                <LikeBtn isLiked={isLiked} likeHandler={likeHandler} />
              </div>
            </div>
          </div>
        )}

        {/* 본문 */}
        <div className="w-full px-5 py-3 flex flex-col justify-between" onClick={toDetail}>
          {/* 경매 상태 / 경매 마크 / 남은 시간 카운트*/}
          <div className="flex justify-between items-center mb-2">
            <div className="flex text-[16px] gap-4">
              <span className="rounded-xl border-[0.1px] px-3 items-center border-customGray  text-customGray">경매</span>
              <span className="">
                {state === "경매시작" && <span className="text-customBlue">경매전</span>}
                {state === "경매종료" && <span className="text-red-500">경매중</span>}
                {state === "종료" && <span>경매종료</span>}
              </span>

            </div>
            <div className="text-customLightTextColor text-sm">
              <RowCountDown curTime={new Date()} auctionStartTime={new Date(item.auctionStartDate)} stateHandler={stateHandler} />
            </div>
          </div>
          {/* 카드 제목 */}
          <div className="text-3xl max-h-[100px] font-bold break-all overflow-hidden text-ellipsis">
            {item.auctionTitle}
          </div>

          {/* 경매 시작가 */}
          <div className="text-[22px] font-medium">
            경매시작가 
            <span
              className={clsx("text-2xl font-bold", state == "종료" ? "" : "text-customBlue")}
            >
              &nbsp;{formatKoreanCurrency(item.auctionStartPrice)}
            </span>
          </div>

          {/* 경매 종료일 / 삭제버튼*/}
            <div className="flex items-center text-customGray">
                {state !== "종료" && (<div className="text-[22px]">경매 등록일 &nbsp;<span className="text-[22px] ">{new Date(item.registerDate).toLocaleString()}</span></div>)}
                {state === "종료" && (<div className="flex text-[22px] items-center">최종 입찰가&nbsp;&nbsp;<span className="flex mr-2 font-bold text-red-500 w-[100px] justify-end flex-nowrap overflow-hidden text-ellipsis">{item.auctionSuccessPay?.toLocaleString()}</span>원</div>)}
            </div>
            
            <div className="flex items-center h-[55px] justify-between">
              <div className="flex text-sm text-customGray">
                <BiMap size={25}/><span className="ml-2 text-[16px]">{item.mycity}&nbsp;{item.street}&nbsp;{item.zipcode}</span>
              </div>
                {state == "경매종료" ? 
                  <div 
                  className="border-[1px] border-customGray cursor-pointer rounded-2xl 
                  text-customLightTextColor text-lg font-bold py-1 px-3
                  hover:scale-105 hover:text-customBlue hover:border-customBlue transition-all"
                  onClick={toBid}>
                    <div className="flex items-center"><RiAuctionLine size={22}/><span className="ml-1">경매장 입장</span></div>
                  </div> : ""
                }
                {state == "종료" ?
                  <div 
                  className="flex items-center border-[1px] rounded-2xl mb-8 border-customGray  text-customLightTextColor
                   text-lg font-bold py-1 px-3 cursor-pointer
                   hover:scale-105 hover:text-customBlue hover:border-customBlue transition-all"
                  onClick={toChat}>
                    <BsChatRightDots size={22}/><span className="ml-2">채팅</span>
                  </div>
                  : ""
                }
              
              {state == "경매시작" && (<div className="flex items-center border-[1px] rounded-2xl border-red-500 text-red-500 text-lg font-bold py-1 px-3 cursor-pointer
                  hover:scale-105 transition-all" onClick={cardDelete}>
                  삭제하기
              </div>)}
            </div>
          </div>
      </div>
    </>
  );
};

export default AuctionSell;
