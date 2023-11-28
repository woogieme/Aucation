import React, { useState } from "react";
import LikeBtn from "../../detail/components/LikeBtn";
import Image from "next/image";
import sellfinish from "@/app/images/sellfinish.png";
import RowCountDown from "./RowCountDown";
import LikeCardCountDown from "./LikeCardCountDown";
import formatKoreanCurrency from "../../utils/formatKoreanCurrency";
import { BiMap } from "react-icons/bi";
import { PiTimer } from "react-icons/pi";
import { useRouter } from "next/navigation";
import clsx from "clsx";

interface ItemType {
  auctionStatus: string;
  auctionTitle: string;
  auctionUUID: string;
  auctionPk: number;
  ownerPk: number;
  historyStatus?: string;
  mycity: string;
  zipcode: string;
  street: string;
  likeDateTime: string;
  imgfile: string;
  auctionStartDate: string;
  auctionEndDate: string;
  auctionStartPrice: number;
}
interface LikeCardProps {
  item: ItemType;
}

const LikeCard: React.FC<LikeCardProps> = ({ item }) => {
  const router = useRouter();
  const [state, setState] = useState<string>("");
  const stateHandler = (state: string) => {
    setState(state);

  };
  const toAuctionDetail = (e: React.MouseEvent<HTMLDivElement>) => {
    e.stopPropagation();
    router.push(`/detail/auction/${item.auctionPk}`);
  };
  const toDiscountDetail = (e: React.MouseEvent<HTMLDivElement>) => {
    e.stopPropagation();
    router.push(`/detail/discount/${item.auctionPk}`);
  };
  const toReAuctionDetail = (e: React.MouseEvent<HTMLDivElement>) => {
    e.stopPropagation();
    router.push(`/reverseauction/${item.auctionPk}`);
  };

  return (
    <div
      className={clsx(
        "flex-col mt-12 rounded-lg overflow-hidden shadow-lg w-[300px] h-[500px] hover:scale-105",
        {
          "cursor-pointer":
            item.auctionStatus === "BID" ||
            item.auctionStatus === "REVERSE_BID" ||
            item.auctionStatus == null,
        }
      )}
      onClick={e => {
        e.stopPropagation();
        if (item.auctionStatus === "BID") {
          toAuctionDetail(e);
        } else if (item.auctionStatus === "REVERSE_BID") {
          toReAuctionDetail(e);
        } else if (item.auctionStatus == null) {
          toDiscountDetail(e);
        }
      }}
    >
      {state == "종료" ? (
        <div>
          <div className="relative w-[300px] h-[300px]">
            <Image
              layout="fill"
              className="transition-transform transform duration-300 hover:scale-110"
              src={item.imgfile}
              alt="Building Image"
              style={{ filter: "brightness(50%)" }}
            />
            <div className="absolute top-10 left-[23%]">
              <Image width={160} height={192} src={sellfinish.src} alt="finish" />
            </div>
          </div>
        </div>
      ) : (
        <div>
          <div className="relative w-[300px] h-[300px]">
            <Image
              layout="fill"
              className="transition-transform transform duration-300 hover:scale-110"
              src={item.imgfile}
              alt={item.auctionTitle}
            />
          </div>
        </div>
      )}

      {/* 카운트 다운 */}
      <div className="px-6">
        <div className="flex h-[50px] justify-start items-center mr-4 gap-1">
          {state !== "종료" && <PiTimer />}
          <LikeCardCountDown
            auctionEndTime={item.auctionEndDate}
            auctionStartTime={item.auctionStartDate}
            stateHandler={stateHandler}
            auctionName={item.auctionStatus}
          />
        </div>
        {/* 제목 */}
        <div className="text-[30px] line-height[5px] h-[50px]">
          <div className="line-clamp-1">{item.auctionTitle}</div>
        </div>

        {/* 가격 */}
        <div className="flex items-center h-[50px] justify-start text-[26px]">
          <span className="flex items-center text-[24px] ">
            <span className="text-[16px]">
              {item.auctionStatus == null ? "할인가" : "시작가"}&nbsp;:&nbsp;
            </span>
            {formatKoreanCurrency(item.auctionStartPrice)}
          </span>
        </div>
        <div className="border-t-2 border-gray-400"></div>
        {/* 지역 */}
        <div className="flex items-center h-[50px]">
          <div className="flex items-center">
            <BiMap size={25} />
            <span className="ml-2 text-[16px]">
              {item.mycity}&nbsp;{item.street}&nbsp;{item.zipcode}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LikeCard;
