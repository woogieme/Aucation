import { pricetoString } from "@/app/utils/pricecal";
import React from "react";

type PriceBoxProps = {
  startingPrice: number;
  lowPrice: number;
  endPrice: number;
};

const PriceBox: React.FC<PriceBoxProps> = ({ startingPrice, lowPrice, endPrice }) => {
  return (
    <div className="rounded-lg flex flex-row items-center p-6 bg-gray-100 border border-gray-400 mt-14">
      <div className="flex flex-col items-start flex-1 ml-4">
        <h3 className="text-sm text-gray-600">희망가</h3>
        <p className="text-xl font-semibold text-gray-700">{pricetoString(startingPrice)}</p>
      </div>
      <div className="border-l border-gray-400 h-20"></div>
      {endPrice != 0 && (
        <div className="flex flex-col items-start flex-1 ml-10">
          <h3 className="text-sm text-customGray">낙찰가</h3>
          <p className="text-xl font-semibold text-customLightTextColor">{pricetoString(endPrice)}</p>
        </div>
      )}

      {endPrice == 0 && (
        <div className="flex flex-col items-start flex-1 ml-10">
          <h3 className="text-sm text-gray-600">입찰 최저가</h3>
          <p className="text-xl font-semibold text-gray-700">{pricetoString(lowPrice)}</p>
        </div>
      )}
    </div>
  );
};

export default PriceBox;
