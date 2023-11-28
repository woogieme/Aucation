import React from "react";
import { orderType } from "./type";
import clsx from "clsx";
interface OwnProps {
  orderType: orderType;
  selectedOrderType: boolean;
  setOrderType: (order: orderType) => void;
}

const OrderTypeBtn: React.FC<OwnProps> = ({ orderType, selectedOrderType, setOrderType }) => {
  return (
    <div
      onClick={() => {
        setOrderType(orderType);
      }}
      className={clsx(
        "text-xl font-semibold",
        "border",
        "rounded-3xl px-4 py-2",
        "hover:cursor-pointer",
        { "text-customBlue border-customBlue": selectedOrderType },
        {
          "border-customGray text-customGray  hover:text-customBlue hover:border-customBlue":
            !selectedOrderType,
        }
      )}
    >
      {orderType.typeName}
    </div>
  );
};

export default OrderTypeBtn;
