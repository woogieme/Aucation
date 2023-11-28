import React, { CSSProperties, useEffect } from "react";
import "react-responsive-carousel/lib/styles/carousel.min.css";
import { Carousel } from "react-responsive-carousel";
import DetailCarouselImg from "@/app/detail/components/DetailCarouselImg";

const arrowStyles: CSSProperties = {
  position: "absolute",
  zIndex: 2,
  top: "calc(50% - 15px)",
  width: 30,
  height: 30,
  cursor: "pointer",
};

const indicatorStyles: CSSProperties = {
  background: "#fff",
  width: 8,
  height: 8,
  display: "inline-block",
  margin: "0 8px",
};
interface DetailImgProps {
  imglist: any;
}

const ReAuctionCarousel: React.FC<DetailImgProps> = ({ imglist }) => {
  useEffect(() => {
    console.log("디테일 캐로셀 페이지", imglist);
  }, []);

  return (
    <div className="h-[500px] w-[600px]">
      <Carousel
        showArrows={true}
        infiniteLoop={true}
        showStatus={false}
        showThumbs={false}
        renderArrowPrev={(onClickHandler, hasPrev, label) =>
          hasPrev && (
            <button
              type="button"
              onClick={onClickHandler}
              title={label}
              style={{ ...arrowStyles, left: 40, fontSize: 40, color: "white" }}
            >
              <div className="border-black  bg-black bg-opacity-10">{"<"}</div>
            </button>
          )
        }
        renderArrowNext={(onClickHandler, hasNext, label) =>
          hasNext && (
            <button
              type="button"
              onClick={onClickHandler}
              title={label}
              style={{ ...arrowStyles, right: 40, fontSize: 40, color: "white" }}
            >
              <div className="border-black bg-black bg-opacity-10">{">"}</div>
            </button>
          )
        }
        animationHandler="fade"
        swipeable={false}
      >
        {imglist.map((item: any, idx: React.Key | null | undefined) => {
          return <DetailCarouselImg key={idx} item={item} />;
        })}
      </Carousel>
    </div>
  );
};

export default ReAuctionCarousel;
