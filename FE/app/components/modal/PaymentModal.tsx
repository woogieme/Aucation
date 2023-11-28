import React, { useEffect, useState } from "react";
import Image from "next/image";
import Star from "@/app/images/Star.png";
import { MdCancel } from "react-icons/md";
import { TbPigMoney } from "react-icons/tb";

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  cash: (value: any) => void;
}

const PaymentModal: React.FC<ModalProps> = ({ isOpen, onClose, cash }) => {
  const [amount, setAmount] = useState<number>(0);
  const [formattedAmount, setFormattedAmount] = useState<string>("");
  const cashHandler = () => {
    cash(amount);
  };
  const buttonNum = [
    { formatNum: "+1천원", num: 1000 },
    { formatNum: "+5천원", num: 5000 },
    { formatNum: "+1만원", num: 10000 },
    { formatNum: "+5만원", num: 50000 },
    { formatNum: "+10만원", num: 100000 },
  ];
  const onBackGruondClick = (event: React.MouseEvent<HTMLDivElement, MouseEvent>) => {
    if (event.target === event.currentTarget) {
      onClose();
    }
  };
  const handleCurrencyFocus = (e: React.FocusEvent<HTMLInputElement>) => {
    // setTimeout을 사용하여 포커스 이벤트가 완전히 처리된 후에 커서 위치를 변경합니다.
    setTimeout(() => {
      const valueLength = e.target.value.length - 1; // '원' 문자를 제외한 길이
      e.target.setSelectionRange(valueLength, valueLength);
    }, 0);
  };

  const addButtonHandler = (addedValue: number) => {
    const newAmount = amount + addedValue;
    setAmount(newAmount);
    setFormattedAmount(
      newAmount === 0 ? "" : `${new Intl.NumberFormat("ko-KR").format(newAmount)}원`
    );
  };

  const inputHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { value } = e.target;
    // value의 값이 숫자가 아닐경우 빈문자열로 replace 해버림.
    let onlyNumber = value.replace(/[^0-9]/g, "");
    if (onlyNumber === "") {
      onlyNumber = "0";
    }
    // const numericValue = parseInt(value.replace(/[^0-9]/g, ""), 10) || 0;
    setFormattedAmount(onlyNumber); // 사용자 입력을 그대로 반영
    setAmount(parseInt(onlyNumber));
  };
  const onBlurHandler = () => {
    setFormattedAmount(amount === 0 ? "" : `${new Intl.NumberFormat("ko-KR").format(amount)}원`);
  };

  const onFocusHandler = () => {
    setFormattedAmount(amount.toString());
  };

  // const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
  //   const { selectionStart, selectionEnd, value } = e.currentTarget;
  //   const valueLength = value.length - 1; // '원'을 제외한 값의 길이

  //   // '원' 문자 바로 앞에서 백스페이스가 눌렸을 때
  //   if (e.key === "Backspace" && selectionStart !== null && selectionStart > valueLength) {
  //     // '원' 바로 앞에 커서가 있는 경우 백스페이스를 방지합니다.
  //     e.preventDefault();
  //     const newValue = value.slice(0, valueLength);
  //     setFormattedAmount(newValue);
  //     setAmount(parseInt(newValue) || 0);
  //   } else if (e.key === "Backspace" && selectionStart === valueLength) {
  //     // '원' 앞의 숫자를 삭제합니다.
  //     e.preventDefault();
  //     const newValue = value.slice(0, valueLength - 1);
  //     setFormattedAmount(newValue);
  //     setAmount(parseInt(newValue) || 0);
  //   }
  // };

  useEffect(() => {
    setFormattedAmount(amount === 0 ? "" : `${new Intl.NumberFormat("ko-KR").format(amount)}원`);
    const isAmountMax = () => {
      if (amount > 2000000) {
        alert("한번에 2백만원을 초과하여 충전하실 수 없습니다.");
        setAmount(2000000);
      }
    };
    isAmountMax();
  }, [amount]);

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
      <div className="bg-white p-10 w-[700px] rounded-xl shadow-lg z-50">
        <div className="w-full text-3xl font-bold flex justify-center space-x-4 mb-16">
          <TbPigMoney className=" text-customBlue bg-customBgLightBlue rounded-full w-[50px] h-[50px]"/> <p className="bg-lightblue-highlight">옥케머니 충전하기</p>
        </div>
        <div className="w-full h-10 text-xl flex place-content-center space-x-4 mb-16">
          <div className="h-12 w-12">
            <Image
              layout="responsive"
              width={10}
              height={10}
              src={"/assets/images/kakaoPay.png"}
              alt={"카카오페이"}
              className="h-8 w-auto"
            />
          </div>

          <p>
            <span className="font-semibold">카카오 페이</span><span className="text-customLightTextColor">로 결제하기</span>{" "}
          </p>
        </div>

        <div className=" flex">
          <input
            type="text"
            value={formattedAmount === "" ? "" : formattedAmount}
            onChange={inputHandler}
            onBlur={onBlurHandler}
            // onFocus={handleCurrencyFocus}
            onFocus={onFocusHandler}
            // onKeyDown={handleKeyDown}
            placeholder="금액을 입력하세요"
            className="focus:outline-none focus:border-blue-500 border-b-2 border-customGray w-full"
          />
          <div onClick={() => setAmount(0)} className="w-5 h-full ml-2 text-2xl cursor-pointer text-customLightTextColor  ">
            <MdCancel />
          </div>
        </div>

        <div className=" w-full mt-12 grid grid-cols-5 gap-2">
          {buttonNum.map(item => (
            <div
              key={item.num}
              onClick={() => addButtonHandler(item.num)}
              className="text-customGray flex justify-center items-center rounded-full border-2 py-3 cursor-pointer hover:border-none hover:border-sky-500 hover:ring-8 hover:ring-sky-200 hover:ring-opacity-100"
            >
              {item.formatNum}
            </div>
          ))}
        </div>

        <div className="flex mt-10 justify-end gap-3">
          <div
            onClick={onClose}
            className="w-[100px] text-center cursor-pointer text-customLightTextColor rounded-full border-2 py-3"
          >
            취소
          </div>
          <div
            className="text-center w-[100px] cursor-pointer text-customBasic rounded-full bg-customBgBlue hover:bg-custom-btn-gradient-hover py-3"
            onClick={cashHandler}
          >
            결제
          </div>
        </div>
      </div>
    </div>
  );
};

export default PaymentModal;
