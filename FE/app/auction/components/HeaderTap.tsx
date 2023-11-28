import React, { ReactNode } from "react";
import clsx from "clsx";

interface OwnProps {
  key: number;
  tap: string;
  selected: boolean;
  headerHandler: (t: string) => void;
}

const HeaderTap: React.FC<OwnProps> = ({ tap, selected, headerHandler }) => {
  return (
    <div
      onClick={() => headerHandler(tap)}
      className={clsx("font-bold text-3xl hover:cursor-pointer", {
        "text-customBlue": selected, // 선택된 탭은 검은색
        "text-customGray": !selected, // 나머지 탭은 회색
      })}
    >
      {tap}
    </div>
  );
};

export default HeaderTap;
