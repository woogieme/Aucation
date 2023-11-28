import React, { useState, useRef, ChangeEvent, useEffect } from "react";
import Image from "next/image";
import defaultprofile from "@/app/images/defaultprofile.png";

interface ImageUploadProps {
  onImageUpload: (file: File) => void;
  imageURL:string;
}

const ImageUpload: React.FC<ImageUploadProps> = ({ onImageUpload, imageURL }) => {
  const [images, setImages] = useState<string>("");
  const [imgFile, setImgFile] = useState<File | null>(null);
  const imgRef = useRef<HTMLInputElement>(null);

  const saveImgFile = (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setImages(reader.result as string);
      };
      reader.readAsDataURL(file);
      setImgFile(file);
      onImageUpload(file);
    }
  };
  useEffect(() => {
    setImages(imageURL)
  },[imageURL])

  return (
    <div className="relative w-[310px] h-[310px]">
       {/* w-[310px] h-[310px] */}
      <label htmlFor="img_file">
        <Image
          src={images || defaultprofile}
          layout="fill"
          alt="이미지 등록"
          className="hover:cursor-pointer object-fill"
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
  );
};

export default ImageUpload;
