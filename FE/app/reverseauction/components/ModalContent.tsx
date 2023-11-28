"use client";

import CarouselMain from "@/app/components/carousel/CarouselMain";

interface ModalContentProps {
  images: string[];
}

const ModalContent: React.FC<ModalContentProps> = ({ images }) => {
  // 적당한 이미지 주소 5개를 리스트로 만들어줘

  return (
    <div
      className="w-3/5 h-[800px] bg-[var(--c-white)] flex items-center justify-center"
      onClick={e => e.stopPropagation()}
    >
      <CarouselMain images={images} />
    </div>
  );
};
export default ModalContent;
