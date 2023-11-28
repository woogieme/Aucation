import React, { ReactNode } from "react";

interface OwnProps {
  children: ReactNode;
}

const FootPtag: React.FC<OwnProps> = ({ children }) => {
  return <p className="mb-4 text-customBasic">{children}</p>;
};

export default FootPtag;
