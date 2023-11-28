import React, { ReactNode } from "react";

interface OwnProps {
  children: ReactNode;
  className?: string;
}

const MainFloorText: React.FC<OwnProps> = ({ children, className }) => {
  return <div className={`flex flex-col items-center ${className}`}>{children}</div>;
};

export default MainFloorText;
