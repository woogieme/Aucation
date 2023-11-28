import React, { useEffect, useState } from "react";
import Image from "next/image";
import Star from "@/app/images/Star.png";
import { MdCancel } from "react-icons/md";
import { IoCloseOutline } from "react-icons/io5";
import { RiAuctionLine } from "react-icons/ri";
import formatKoreanCurrency from "../../utils/formatKoreanCurrency";
interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  memberPoint: number;
  discountDiscountedPrice: number;
  buyHandler: () => void
}

const DiscountBuyModal: React.FC<ModalProps> = ({
  isOpen,
  onClose,
  memberPoint,
  discountDiscountedPrice,
  buyHandler,
}) => {
  const onBackGruondClick = (event: React.MouseEvent<HTMLDivElement, MouseEvent>) => {
    if (event.target === event.currentTarget) {
      onClose();
    }
  };

  useEffect(() => {
    const onPressEsc = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        onClose();
      }
    };

    window.addEventListener("keydown", onPressEsc);
    return () => window.removeEventListener("keydown", onPressEsc);
  }, [onClose]);

  if (!isOpen) return null;

  return (
    <div
      className="fixed inset-0 bg-black bg-opacity-50 z-50 flex justify-center items-center"
      onClick={onBackGruondClick}
    >
      <div className="flex-col rounded-lg bg-white w-1/2 h-1/3">
        <div className="flex h-1/4 w-[96%] items-center justify-end">
          <span className="cursor-pointer" onClick={onClose}>
            <IoCloseOutline size={30} />
          </span>
        </div>
        <div className="flex items-center justify-center h-1/6 text-[30px]">
          <RiAuctionLine size={50} />
          &nbsp;구매하시겠습니까?
        </div>
        <div className="flex text-[24px] items-center justify-center h-1/6 whitespace-pre-line">
          구매 시&nbsp;<span className="text-[28px] font-bold">{formatKoreanCurrency(discountDiscountedPrice)}</span>&nbsp;이 차감됩니다.
        </div>
        <div className="flex text-[24px] items-center justify-center h-1/6 whitespace-pre-line">
          잔여 포인트는&nbsp;<span className="font-bold text-[28px]">{formatKoreanCurrency(memberPoint - discountDiscountedPrice)} </span>&nbsp;입니다.
        </div>

        <div className="flex text-[20px] items-center justify-end gap-8 w-[95%] h-1/4">
          <span className="border-[0.2px] px-4 py-1 rounded-lg hover:bg-gray-100 cursor-pointer" onClick={onClose}>취소</span>
          <span className="border-[0.2px] px-4 py-1 rounded-lg hover:bg-gray-100 cursor-pointer" onClick={buyHandler}>확인</span>
        </div>
      </div>
    </div>
  );
};

export default DiscountBuyModal;
