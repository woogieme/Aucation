"use client";

import { useState } from "react";

interface InputProps {
  text: string;
  setText: React.Dispatch<React.SetStateAction<string>>;
  bidHandler: (text: string) => void;
}

const Input: React.FC<InputProps> = ({ text, setText, bidHandler }) => {
  const inputHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    setText(e.target.value);
  };
  const sendHandler = (e: React.KeyboardEvent) => {
    if (e.key !== "Enter") return;
    bidHandler(text);
    setText("");
  };

  return (
    <input
      type="text"
      className="
                w-[80%]
                my-2
                ml-10
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
      placeholder="채팅을 입력하세요"
      value={text}
      onChange={inputHandler}
      onKeyDown={sendHandler}
    />
  );
};

export default Input;
