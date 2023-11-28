import React from "react";
import { pricetoString } from "@/app/utils/pricecal";
type PriceBoxProps = {
  startingPrice?: string;
  highestPrice?: string;
  bidUnit?: string;
  discountedPrice?: string;
  discountPer?: string;
};

const PriceBox: React.FC<PriceBoxProps> = ({
  startingPrice,
  highestPrice,
  bidUnit,
  discountedPrice,
  discountPer,
}) => {
  const numberFormat = new Intl.NumberFormat("ko-KR");

  return (
    <div className="rounded-lg flex flex-row items-center p-6 bg-gray-100 border border-gray-400 mt-14">
      <div className="flex flex-col items-start flex-1 ml-4">
        <h3 className="text-sm text-customGray">시작가</h3>
        <p className="text-xl font-semibold text-customLightTextColor">
          {startingPrice ? pricetoString(Number(startingPrice)) : "0원"}
        </p>
      </div>

      {highestPrice && (
        <>
          <div className="border-l border-gray-400 h-20"></div>
          <div className="flex flex-col items-start flex-1 ml-10">
            <h3 className="text-sm text-customGray">최고가</h3>
            <p className="text-xl font-semibold text-blue-500">
              {highestPrice ? pricetoString(Number(highestPrice)) : "0원"}
            </p>
          </div>
        </>
      )}

      {discountedPrice && (
        <>
          <div className="border-l border-gray-400 h-20"></div>
          <div className="flex flex-col items-start flex-1 ml-10">
            <h3 className="text-sm text-customGray">할인가</h3>
            <p className="text-xl font-semibold text-blue-500">
              {discountedPrice ? pricetoString(Number(discountedPrice)) : "0원"}
            </p>
          </div>
        </>
      )}

      {bidUnit && (
        <>
          <div className="border-l border-gray-400 h-20"></div>
          <div className="flex flex-col items-start flex-1 ml-10">
            <h3 className="text-sm text-customGray">입찰단위</h3>
            <p className="text-xl font-semibold text-customLightTextColor">
              {bidUnit ? pricetoString(Number(bidUnit)) : "0원"}
            </p>
          </div>
        </>
      )}
      {(discountPer || discountPer == "0") && (
        <>
          <div className="border-l border-gray-400 h-20"></div>
          <div className="flex flex-col items-start flex-1 ml-10">
            <h3 className="text-sm text-customGray">할인률</h3>
            <p className="text-xl f ont-semibold font-bold text-red-500">
              {discountPer}
              <span className="text-customLightTextColor text-sm">&nbsp;%</span>
            </p>
          </div>
        </>
      )}

    </div>
  );
};

export default PriceBox;
