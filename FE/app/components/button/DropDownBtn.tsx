import React, { useState, useRef, useEffect } from "react";
import { LuTriangle } from "react-icons/lu";
import clsx from "clsx";
interface Props {
  options: string[]; // 드롭 박스에 들어갈 문자열 배열
  selectedOption: string; // 드롭 박스에서 내가 선택한 요소
  setSelectedOption: (option: string) => void; // 드롭박스 안에서 요소를 선택할 때 선택한 값을 저장하기 위한 세터함수
  size?: "big" | "small" | "medium"; // big은 3줄 medium은 두줄 small은 한 줄로 박스를 만듦
}

const DropdownButton: React.FC<Props> = ({ options, selectedOption, setSelectedOption, size }) => {
  const [isShowToggle, setIsShowToggle] = useState<boolean>(false);
  const dropdownRef = useRef<HTMLDivElement | null>(null);

  // 드롭 박스의 외부에서 클릭했을 경우 드롭박스가 닫힘
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsShowToggle(false);
      }
    };

    document.addEventListener("click", handleClickOutside);

    // 드롭 박스가 언마운트시 클릭 이벤트를 제거
    return () => {
      document.removeEventListener("click", handleClickOutside);
    };
  }, []);

  // 드롭 박스 내우의 요소를 선택했을 경우 실행
  const handleOptionClick = (option: string) => {
    setSelectedOption(option);
    setIsShowToggle(false);
  };

  return (
    <div
      onClick={() => setIsShowToggle(!isShowToggle)}
      ref={dropdownRef}
      className={clsx(
        "text-customLightTextColor text-xl border rounded-3xl px-4 py-2 hover:cursor-pointer flex flex-row items-center space-x-2 relative",
        {
          "border-customGray": !isShowToggle,
          "border-sky-500 ring-2 ring-sky-200 ring-opacity-50": isShowToggle,
        }
      )}
    >
      <p className="text-customBlue font-semibold">{selectedOption}</p>
      <LuTriangle
        className={`transition-transform duration-300 ease-in-out transform ${
          isShowToggle ? "" : "rotate-180"
        }`}
        size={20}
      />
      {isShowToggle && (
        <div
          className={clsx(
            `absolute z-10 top-full left-0 mt-2  rounded-2xl border border-customGray bg-white divide-y divide-gray-100`,
            {
              //
              "w-[600px]": size === "big",
              "w-[400px]": size === "medium",
              "w-[200px]": size === "small",
            }
          )}
        >
          <div
            className={clsx(`p-2 grid gap-2`, {
              "grid-cols-3": size === "big",
              "grid-cols-2": size === "medium",
              "grid-cols-1": size === "small",
            })}
          >
            {options.map((option, idx) => (
              <div
                key={idx} // 호버시 색상의 경우 커스터 마이징 하세요
                className="hover:bg-gray-200 hover:text-customBlue font-normal rounded cursor-pointer px-1 py-1 "
                onClick={() => handleOptionClick(option)}
              >
                {option}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default DropdownButton;
