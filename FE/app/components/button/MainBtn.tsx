import React, { ReactNode } from "react";

interface NavProps {
  children: ReactNode;
  className?: string;
}

// children 이 부분에 내용을 넣으셈
const MainBtn: React.FC<NavProps> = ({ children, className }) => {
  return (
    <div // 색상은 아래서 bg 이 부분을 수정하세요
      className={`bg-custom-btn-gradient hover:bg-custom-btn-gradient-hover text-customBasic py-3 rounded-full cursor-pointer ${className}`}
    >
      {children}
    </div>
  );
};

export default MainBtn;
