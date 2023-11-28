"use client";

import clsx from "clsx";
import { AiOutlineStop } from "react-icons/ai";
import PriceInput from "./PriceInput";
import { BsFillXCircleFill } from "react-icons/bs";
import { useRef } from "react";
import imageupload from "@/app/images/imageupload.png";
import Image from "next/image";
interface BidModalContentProps {
  priceHandler: (price: number) => void;
  price: number;
  description: string;
  handleBidModalOpen: () => void;
  descriptionhandler: (e: React.ChangeEvent<HTMLTextAreaElement>) => void;
  imagecount: number;
  setImagecount: React.Dispatch<React.SetStateAction<number>>;
  images: string[];
  setImages: React.Dispatch<React.SetStateAction<string[]>>;
  imagefiles: File[];
  setImagefiles: React.Dispatch<React.SetStateAction<File[]>>;
  handleBidHandler: () => void;
}

const BidModalContent: React.FC<BidModalContentProps> = ({
  priceHandler,
  handleBidModalOpen,
  price,
  description,
  descriptionhandler,
  imagecount,
  setImagecount,
  images,
  setImages,
  imagefiles,
  setImagefiles,
  handleBidHandler,
}) => {
  // 적당한 이미지 주소 5개를 리스트로 만들어줘

  const imgRef = useRef<HTMLInputElement>(null);
  const saveImgFile = () => {
    const file = imgRef.current!.files![0];
    const reader = new FileReader();

    if (images.length >= 5) {
      alert("등록할 수 있는 이미지는 최대 5장입니다.");
      return; // 이후의 코드를 실행하지 않고 함수를 종료합니다.
    }

    reader.readAsDataURL(file);
    reader.onloadend = () => {
      if (typeof reader.result === "string") {
        setImages([...images, reader.result]);
        setImagecount(imagecount + 1);
      }
      setImagefiles([...imagefiles, file]);
    };
  };
  const deleteImg = (index: number) => {
    const updatedImages = [...images];
    updatedImages.splice(index, 1);
    const updatedImageFiles = [...imagefiles];
    updatedImageFiles.splice(index, 1);
    setImages(updatedImages);
    setImagefiles(updatedImageFiles);
    setImagecount(imagecount - 1);
  };

  return (
    <div
      className="w-[70%] h-[800px] bg-[var(--c-white)] flex p-10 justify-start flex-col overflow-y-scroll overflow-x-hidden"
      onClick={e => e.stopPropagation()}
    >
      <div className="flex w-full mt-5 mx-4">
        <p
          className="text-xl mt-3 w-[130px] font-semibold
        '"
        >
          입찰가 <span className="text-red-500">*</span>
        </p>
        <div className="flex-col w-3/8">
          <PriceInput priceHandler={priceHandler} />

          <div className={clsx(`flex text-[var(--c-blue)] mt-3`, price ? "invisible" : "")}>
            <AiOutlineStop size={24} />
            <span>가격을 입력해 주세요</span>
          </div>
        </div>
      </div>
      {/* 설명 넣기 */}
      <div className="flex w-full mt-5 mx-4">
        <h1 className="text-xl font-semibold w-[147px]">
          제품 설명 <span className="text-red-500">*</span>
        </h1>
        <div className="flex flex-col w-full">
          <div className="text-lg">
            <textarea
              value={description}
              onChange={descriptionhandler}
              className="w-full h-72 border-2 
              border-gray-300
              rounded-md
              px-2
              py-2
              text-gray-700
              focus:outline-none
              focus:border-sky-500
              focus:ring-2
              focus:ring-sky-200
              focus:ring-opacity-50"
              placeholder="서로가 믿고 거래할 수 있도록, 자세한 정보와 다양한 각도의 상품 사진을 올려주세요."
            />
          </div>
          <div className={clsx(`flex text-[var(--c-blue)] mt-1`, description ? "invisible" : "")}>
            <AiOutlineStop size={24} />
            <span>제품 설명을 입력해 주세요</span>
          </div>
        </div>
      </div>
      {/* 이미지 넣기 */}
      <div className="flex my-10 w-full mx-4">
        <div className="flex flex-col mr-10 w-1/6">
          <h1 className="text-xl font-semibold ">
            상품 이미지 <span className="text-red-500">*</span>({imagecount}/5)
          </h1>
          <div className="mt-14 text-customLightTextColor">
            상품 이미지의 형식은 &quot;jpg/jpeg/png&quot;만 업로드 가능 <br /> 최대 5장까지 업로드
            가능
          </div>
        </div>
        <div className="w-5/6 flex flex-wrap">
          <div className="w-[300px] h-[300px] mr-5 mb-5">
            <label htmlFor="img_file">
              <Image
                src={imageupload}
                width="300"
                height="300"
                alt="이미지 등록"
                className="hover:cursor-pointer"
              />
              <input
                type="file"
                id="img_file"
                accept="image/jpg, image/png, image/jpeg"
                onChange={saveImgFile}
                ref={imgRef}
                hidden
              />
            </label>
          </div>
          {images.map((image, idx) => {
            return (
              <div
                className="relative w-[300px] h-[300px] bg-cover bg-no-repeat bg-center mr-5 mb-5"
                key={idx}
              >
                <img src={image} alt="이미지 등록" className="w-[300px] h-[300px]" />
                <div className="absolute top-2 right-2 ">
                  <BsFillXCircleFill
                    size={35}
                    className="cursor-pointer text-black hover:text-red-600 transition-colors duration-300"
                    onClick={() => {
                      deleteImg(idx);
                    }}
                  />
                </div>
              </div>
            );
          })}
        </div>
      </div>
      <div>
        {/* 취소와 확정 버튼*/}
        <div className="flex justify-end">
          <button
            className="w-28 h-12 bg-[var(--c-white)] text-black rounded-md mr-4 hover:bg-gray-300 border-[1px] border-black"
            onClick={handleBidModalOpen}
          >
            취소
          </button>
          <button
            className="w-28 h-12 bg-blue-400 text-black rounded-md hover:bg-blue-500"
            onClick={handleBidHandler}
          >
            확정
          </button>
        </div>
      </div>
    </div>
  );
};
export default BidModalContent;
