import React, { useState } from "react";
import { FiSearch } from "react-icons/fi";
interface OwnProps {
  searchHandler: (keyword: string) => void;
  setSearchKeyword: (keyword: string) => void;
}

const SearchInput: React.FC<OwnProps> = ({ searchHandler, setSearchKeyword }) => {
  const [inputValue, setInputValue] = useState("");
  const [isFocused, setIsFocused] = useState(false); // focus 상태를 추적하기 위한 state

  const handleSearchClick = () => {
    const trimmedValue = inputValue.trim();

    if (!trimmedValue) {
      alert("검색어를 입력해주세요!");
      return;
    } else if (trimmedValue.length < 2) {
      alert("글자 수는 최소 2자 이상이어야 합니다!");
      return;
    }
    setSearchKeyword(trimmedValue);
    searchHandler(trimmedValue);
  };
  const enterSearchClick = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      handleSearchClick();
    }
  };

  return (
    <div
      className={`flex space-x-2 border rounded-3xl px-4 py-2
      ${isFocused ? "border-sky-500 ring-2 ring-sky-200 ring-opacity-50" : "border-customGray border"} 
      items-center border
      `}
    >
      <input
        value={inputValue}
        onChange={e => setInputValue(e.target.value)}
        placeholder="검색어를 입력하세요"
        className="focus:outline-none"
        onFocus={() => setIsFocused(true)} // input에 focus가 되면 isFocused를 true로 설정
        onBlur={() => setIsFocused(false)} // input에서 focus가 사라지면 isFocused를 false로 설정
        onKeyDown={(e: React.KeyboardEvent<HTMLInputElement>) => {
          enterSearchClick(e);
        }}
      />
      <div onClick={handleSearchClick} className=" hover:cursor-pointer text-customGray">
        <FiSearch size={25} />
      </div>
    </div>
  );
};

export default SearchInput;
