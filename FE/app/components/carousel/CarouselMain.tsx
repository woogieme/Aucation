import React, { CSSProperties, useState } from "react";
import "react-responsive-carousel/lib/styles/carousel.min.css";
import { Carousel } from "react-responsive-carousel";
import CarouselImages from "./CarouselImages";

const arrowStyles: CSSProperties = {
  position: "absolute",
  zIndex: 2,
  top: "calc(50% - 15px)",
  width: 20,
  height: 20,
  cursor: "pointer",
};

const indicatorStyles: CSSProperties = {
  background: "#fff",
  width: 8,
  height: 8,
  display: "inline-block",
  margin: "0 8px",
};

interface CarouselMainProps {
  images: string[];
}

const CarouselMain: React.FC<CarouselMainProps> = ({ images }) => {
  return (
    <div className="h-[600px] w-[750px] flex items-center justify-center">
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
              style={{ ...arrowStyles, left: 160, fontSize: 40, color: "gray" }}
            >
              <div className="border-black">{"<"}</div>
            </button>
          )
        }
        renderArrowNext={(onClickHandler, hasNext, label) =>
          hasNext && (
            <button
              type="button"
              onClick={onClickHandler}
              title={label}
              style={{ ...arrowStyles, right: 160, fontSize: 40, color: "gray" }}
            >
              <div className="border-black">{">"}</div>
            </button>
          )
        }
        animationHandler="fade"
      >
        {images.map((img, idx) => {
          return (
            <div className="flex items-center justify-center align-middle min-w-[600px]" key={idx}>
              <CarouselImages image={img} />
            </div>
          );
        })}
      </Carousel>
    </div>
  );
};

export default CarouselMain;
