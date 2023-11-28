"use client";
import React from "react";
import dummyData from "@/app/detail/components/DummyData";
import Card from "@/app/components/Card/ColCard";
import Carousel, { CarouselProps } from "react-multi-carousel";
import "react-multi-carousel/lib/styles.css";
import { ButtonGroupProps } from "react-multi-carousel/lib/types";
import { BsArrowLeftCircle } from "react-icons/bs";
import { BsArrowRightCircle } from "react-icons/bs";
import Link from "next/link";
import { AuctionItem, ReverseAuctionItem, DiscountItem } from "@/app/components/Card/cardType";
import AuctionListCard from "@/app/components/Card/AutionListCard";
import DiscountListCard from "@/app/components/Card/DiscountListCard";
import ClipLoader from "react-spinners/ClipLoader";
import Image from "next/image";
// interface CarouselButtonGroupProps extends ButtonGroupProps {
//   className?: string;
//   next: () => void;
//   previous: () => void;

// }

// const CarouselButtonGroup: React.FC<ButtonGroupProps> = ({ next, previous }) => {
//   return (
//     <div className="flex flex-rowspace text-4xl ">
//       <div className="mr-5">
//         <BsArrowLeftCircle onClick={() => previous()} />
//       </div>

//       <div>
//         <BsArrowRightCircle onClick={() => next()} />
//       </div>
//     </div>
//   );
// };
const ButtonGroup = ({ next, previous, goToSlide, ...rest }: any) => {
  const {
    carouselState: { currentSlide },
  } = rest;
  return (
    <div className="items-end text-3xl mb-4 absolute flex top-[-80px] left-[1380px] ">
      <button className="block p-3" onClick={() => previous()}>
        {" "}
        <BsArrowLeftCircle />
      </button>
      <button onClick={() => next()}>
        <span className="block p-3">
          <BsArrowRightCircle />
        </span>
      </button>
    </div>
  );
};
interface OwnProps {
  title: string;
  className?: string;
  moreShow?: boolean;
  goUrl?: string;
  type: string;
  item: AuctionItem[] | ReverseAuctionItem[] | DiscountItem[];
  nowTime: Date | null;
  isLoading: boolean;
}

const PopularBidList: React.FC<OwnProps> = ({
  title,
  className,
  moreShow = false,
  goUrl,
  type,
  item,
  nowTime,
  isLoading,
}) => {
  const responsive = {
    superLargeDesktop: {
      // the naming can be any, depends on you.
      breakpoint: { max: 4000, min: 3000 },
      items: 5,
    },
    desktop: {
      breakpoint: { max: 3000, min: 1024 },
      items: 4,
    },
    tablet: {
      breakpoint: { max: 1024, min: 464 },
      items: 2,
    },
    mobile: {
      breakpoint: { max: 464, min: 0 },
      items: 1,
    },
  };
  return (
    <div className={`pt-14 pb-14 ${className} px-48`}>
      <div className="pb-8 flex flex-row justify-between items-center">
        <div className="text-3xl font-bold pl-14 ">{title}</div>
        {moreShow && (
          <Link
            href={goUrl ==='discount' ? `/${goUrl}`: `/auction/${goUrl}`}
            className="text-customLightTextColor text-lg hover:text-xl hover:underline"
          >
            더 보기
          </Link>
        )}
      </div>

      <div className="relative ">
        {isLoading ? (
          <div className="flex justify-center items-center">
            <ClipLoader color="#247eff" size={200} speedMultiplier={1} />
          </div>
        ) : item.length > 0 ? (
          <Carousel
            className="h-full ml-10 flex pl-5 py-3"
            responsive={responsive}
            infinite={true}
            arrows={false}
            {...(!moreShow && {
              renderButtonGroupOutside: true,
              customButtonGroup: <ButtonGroup />,
            })}
          >
            {type === "hotAution" &&
              item &&
              item.map((item, idx) => (
                <div key={idx} className="w-[295px] h-[600px] flex justify-center items-center">
                  <AuctionListCard item={item as AuctionItem} nowTime={nowTime} />
                </div>
              ))}

            {type === "reverseAution" &&
              item &&
              item.map((item, idx) => (
                <div key={idx} className="w-[295px] h-[600px]  flex justify-center items-center">
                  <AuctionListCard
                    item={item as ReverseAuctionItem}
                    nowTime={nowTime}
                    type="reverse"
                  />
                </div>
              ))}
            {type === "discounts" &&
              item &&
              item.map((item, idx) => (
                <div key={idx} className="w-[295px] h-[600px] flex justify-center items-center">
                  <DiscountListCard item={item as DiscountItem} nowTime={nowTime} />
                </div>
              ))}
          </Carousel>
        ) : (
          // <Image src={"/assets/images/noItems.png"} alt="아이템이 없다" fill />
          <div className="border-2 border-customGray rounded-full flex flex-col justify-center items-center py-10 text-customLightTextColor text-xl">
            <div className="flex justify-center place-items-center rounded-full w-[100px] h-[100px] border-2 border-customGray text-3xl mb-3 text-red-400 ">
              {"!"}
            </div>
            <div className="text-xl">등록된 물품이 없습니다.</div>
          </div>
        )}
      </div>
    </div>
  );
};

export default PopularBidList;
