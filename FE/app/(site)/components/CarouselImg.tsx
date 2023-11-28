import React from "react";
import Image from "next/image";

interface OwnProps {
  index: number;
}

const CarouselImg: React.FC<OwnProps> = ({ index }) => {
  return (
    <div key={index} className="w-full h-full">
      <Image src={`/assets/images/${index}.png`} alt={`${index} `} width={1536} height={384} />
    </div>
  );
};

export default CarouselImg;
