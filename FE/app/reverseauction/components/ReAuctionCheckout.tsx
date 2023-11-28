"use client";

import { RoundedImg } from "@/app/components/tailwinds";
import { pricetoString } from "@/app/utils/pricecal";
import Image from "next/image";
import { useState } from "react";

interface ReAuctionCheckoutProps {
  onClick: () => void;
  data: any;
  canClick: boolean;
  selectBidHandler?: (bidPk: number) => void;
}
const ReAuctionCheckout: React.FC<ReAuctionCheckoutProps> = ({
  onClick,
  data,
  canClick,
  selectBidHandler,
}) => {
  const select = (bidPk: number) => {
    if (selectBidHandler) {
      selectBidHandler(bidPk);
    } else {
      return;
    }
  };
  return (
    <div className="mt-4 w-full border-2 border-blue-400 h-[650px] flex">
      <div className="w-1/2 flex flex-col items-center justify-evenly overflow-hidden">
        <Image alt="상품이미지" src={data.bidPhotos[0]} width={550} height={600} />
        <button
          onClick={onClick}
          className="h-10 bg-sky-200 mt-2 w-2/5 rounded-lg border-2 border-blue-200 hover:bg-sky-300"
        >
          자세히 보기
        </button>
      </div>
      <div className="w-1/2 ml-2 p-4 mr-4">
        <span className="text-3xl text-blue-500 font-bold">{pricetoString(data.bidPrice)}</span>
        <span className="ml-3 text-2xl">제시</span>
        <div className="border-2 w-full border-gray-300"></div>
        <div className="break-all h-4/5 py-2">
          <p>{data.bidDetail}</p>
        </div>
        <div className="border-2 w-full border-gray-300"></div>
        <div className="w-full mt-4 flex justify-between">
          <div className="flex items-center">
            <RoundedImg src={data.customerPhoto} />
            <h1 className="ml-2 text-lg">{data.customerNickName}</h1>
          </div>
          <div>
            {canClick && (
              <button
                className="bg-[var(--c-blue)] rounded-xl text-white text-xl hover:bg-sky-500 h-10 px-3 text-center align-middle"
                onClick={() => select(data.bidPk)}
              >
                입찰선택
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ReAuctionCheckout;
