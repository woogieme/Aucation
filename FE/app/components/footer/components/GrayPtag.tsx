import React, { ReactNode } from "react";

interface OwnProps {
  children: ReactNode;
  className?: string;
}

const GrayPtag: React.FC<OwnProps> = ({ children, className }) => {
  return <p className={`mb-4 text-customGray  ${className || ""}`}>{children}</p>;
};

export default GrayPtag;
