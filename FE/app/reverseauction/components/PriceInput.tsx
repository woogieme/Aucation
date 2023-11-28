"use client";

import { useState } from "react";

interface PriceInputProps {
  priceHandler: (price: number) => void;
}

const PriceInput: React.FC<PriceInputProps> = ({ priceHandler }) => {
  const [text, setText] = useState<string | number>(0);

  const inputHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { value } = e.target;
    // value의 값이 숫자가 아닐경우 빈문자열로 replace 해버림.
    let onlyNumber = value.replace(/[^0-9]/g, "");
    if (onlyNumber === "") {
      onlyNumber = "0";
    }
    priceHandler(parseInt(onlyNumber));
    setText(parseInt(onlyNumber));
  };

  return (
    <div className="flex flex-row items-center">
      <input
        type="text"
        className="
                w-full
                h-12
                border-2
                border-gray-300
                rounded-md
                px-4
                py-2
                text-gray-700
                focus:outline-none
                focus:border-sky-500
                focus:ring-2
                focus:ring-sky-200
                focus:ring-opacity-50
                disabled:opacity-50
                disabled:cursor-not-allowed
            "
        placeholder="가격을 입력하세요"
        value={text}
        onChange={inputHandler}
      />
      <p className="text-2xl text-blue-500 ml-2">원</p>
    </div>
  );
};

export default PriceInput;
