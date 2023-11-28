import React from "react";
import { HiOutlineMagnifyingGlass } from "react-icons/hi2";
const NavInput: React.FC = () => {
  return (
    <div className="w-1/5">
      <div className="border-solid border-#646C76 border-2 pl-4 py-2 rounded-full flex flex-row ">
        <div className="w-[85%] text-xl">
          <input type="text" placeholder="검색어 입력" className="w-full outline-none" />
        </div>

        <div className="flex flex-row items-center pl-4">
          <HiOutlineMagnifyingGlass />
        </div>
      </div>
    </div>
  );
};

export default NavInput;
