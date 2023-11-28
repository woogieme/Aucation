import React, { CSSProperties } from "react";
import "react-responsive-carousel/lib/styles/carousel.min.css";
import { Carousel } from "react-responsive-carousel";
import CarouselImg from "./CarouselImg";

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

const Banner: React.FC = () => {
  function createArray(n: number) {
    return Array.from({ length: n }, (_, i) => i + 1);
  }
  const imgIdx = createArray(5);

  return (
    <div className="h-full">
      <Carousel
        showThumbs={false}
        autoPlay={true}
        interval={7000}
        showArrows={true}
        infiniteLoop={true}
        showStatus={false}
        renderArrowPrev={(onClickHandler, hasPrev, label) =>
          hasPrev && (
            <button
              type="button"
              onClick={onClickHandler}
              title={label}
              // 여기서 화살표의 모양을 수정하세요
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
        {imgIdx.map(idx => {
          return <CarouselImg key={idx} index={idx} />;
        })}
      </Carousel>
    </div>
  );
};

export default Banner;
