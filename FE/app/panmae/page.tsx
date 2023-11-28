"use client";

import Image from "next/image";
import { useEffect, useRef, useState } from "react";
import imageupload from "@/app/images/imageupload.png";
import Input from "./components/Input";
import { AiOutlineStop } from "react-icons/ai";
import clsx from "clsx";
import PriceInput from "./components/PriceInput";
import MoveMap from "../components/map/MoveMap";
import CarouselMain from "../components/carousel/CarouselMain";
import SelectBox from "./components/SelectBox";
import TypeOfSales from "./components/TypeOfSales";
import TimeInput from "./components/TimeInput";
import { BsFillXCircleFill } from "react-icons/bs";
import { callApi } from "../utils/api";
import { set } from "react-hook-form";
import ClipLoader from "react-spinners/ClipLoader";
import { useRecoilValue } from "recoil";
import { authState } from "../store/atoms";
import { useRouter } from "next/navigation";
import { timeToDate } from "../utils/timetodate";
import toast from "react-hot-toast";

const Panmae = () => {
  const router = useRouter();
  const auth = useRecoilValue(authState);

  const [imagecount, setImagecount] = useState(0);
  const [images, setImages] = useState<string[]>([]); // 이미지의 url 주소를 담는 state
  const [imagefiles, setImagefiles] = useState<File[]>([]); // 이미지의 url 주소를 담는 state
  const [productname, setProductname] = useState<string>(""); // 삼품 이름을 저장
  const [category, setCategory] = useState<string>("여성의류"); //
  const [option, setOption] = useState<string>("");
  const [price, setPrice] = useState<number>(0);
  const [hour, setHour] = useState<number>(0);
  const [minute, setMinute] = useState<number>(0);
  const [description, setDescription] = useState<string>(""); // 상품 설명
  const [discountPrice, setDiscountPrice] = useState<number | null>(null); // 옵션에서 할인 선택시 할인가

  const [transActionLocation, setTransActionLocation] = useState<number[] | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [isDoingSubmit, setIsDoingSubmit] = useState(false);
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
  const productnameHandler = (name: string) => {
    setProductname(name);
  };

  const categoryHandler = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setCategory(e.target.value);
  };

  const optionHandler = async (e: React.ChangeEvent<HTMLInputElement>) => {
    setOption(e.target.value); // 선택한 라디오 버튼의 값으로 category를 업데이트
    if (e.target.value === "할인") {
      setDiscountPrice(0);
    } else {
      setDiscountPrice(null);
    }
  };

  const priceHandler = (price: number) => {
    setPrice(price);
  };
  const discountPriceHandler = (price: number) => {
    setDiscountPrice(price);
  };
  const hourHandler = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setHour(parseInt(e.target.value));
  };
  const minuterHandler = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setMinute(parseInt(e.target.value));
  };
  const descriptionhandler = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
    setDescription(event.target.value);
  };
  const submitHandler = () => {
    setIsDoingSubmit(true);
    if (
      !option ||
      !productname ||
      !category ||
      // (hour === 0 && minute === 0) ||
      imagefiles.length === 0
    ) {
      alert("필수 항목을 모두 채워주세요!");
      setIsDoingSubmit(false);
      return;
    }
    if (option !== "할인") {
      if (option == "BID" && price < 1000) {
        toast.error("경매가는 천원 이상이어야 합니다")
        setIsDoingSubmit(false);
        return;
      }
      const formData = new FormData();
      formData.append("auctionStatus", option);
      formData.append("auctionTitle", productname);
      formData.append("auctionType", category);
      formData.append(
        "auctionMeetingLat",
        transActionLocation ? transActionLocation[0].toString() : "37"
      );
      formData.append(
        "auctionMeetingLng",
        transActionLocation ? transActionLocation[1].toString() : "126"
      );
      formData.append("auctionStartPrice", price.toString());
      formData.append("auctionDetail", description);
      formData.append("auctionStartAfterTime", (hour * 60 + minute).toString());
      if (discountPrice !== null) {
        formData.append("prodDiscountedPrice", discountPrice.toString());
      }
      imagefiles.forEach((image, index) => {
        formData.append("multipartFiles", image);
      });
      callApi("post", "/auction/register", formData)
        .then(res => {
          // console.log(res.data);
          if (option == "BID") {
            router.push(`/detail/auction/${res.data.auctionPk}`);
          } else {
            router.push(`/reverseauction/${res.data.auctionPk}`);
          }
        })
        .catch(err => {
          // console.log();
          // console.log(err);
          toast.error(err.response.data.message)
        })
        .finally(() => {
          setLoading(false);
        });
    } else {
      if (discountPrice! > price) {
        alert("할인가가 정가보다 높습니다!!");
        setIsDoingSubmit(false);
        return;
      }
      const time = timeToDate(hour, minute);
      const formData = new FormData();
      formData.append("discountTitle", productname);
      formData.append("discountType", category);
      formData.append(
        "discountLat",
        transActionLocation ? transActionLocation[0].toString() : "37"
      );
      formData.append(
        "discountLng",
        transActionLocation ? transActionLocation[1].toString() : "126"
      );
      formData.append("discountPrice", price.toString());
      formData.append("discountDetail", description);
      formData.append("discountEnd ", time);
      formData.append("discountDiscountedPrice", discountPrice!.toString());
      imagefiles.forEach((image, index) => {
        formData.append("multipartFiles", image);
      });
      formData.forEach((key, value) => {
        console.log(key, value);
      });
      callApi("post", "/discount/register", formData)
        .then(res => {
          // console.log(res);
          const prodPk = res.data.prodPk;
          router.push(`/detail/discount/${prodPk}`);
        })
        .catch(err => {
          // console.log(err);
          toast.error(err.response.data.message)
        })
        .finally(() => {
          setLoading(false);
        });
    }
  };

  useEffect(() => {
    // 브라우저에서 로컬 스토리지에 접근하여 토큰 확인
    const accessToken = window.localStorage.getItem("accessToken");
    // console.log(auth);

    // 토큰이 없는 경우 로그인 페이지로 리다이렉션
    if (!accessToken) {
      router.push("/login");
    }
  }, [auth, router]);

  return (
    <div className="w-full px-60 py-20">
      <div
        className="
        h-full
    "
      >
        <div className="my-4">
          <span className="text-3xl font-bold mr-10">내 물건 경매하기</span>
          <span className="text-lg text-red-500"> *필수항목</span>
        </div>

        <div className="border-b-2 border-black w-full" />

        <div className="flex my-10 w-full mx-4">
          <div className="flex flex-col mr-10 w-1/6">
            <h1 className="text-2xl font-semibold ">
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

        <div className="border-b-2 border-black w-full"></div>

        <div className="flex my-10 w-full min-h-[80px] ml-4">
          <h1 className="text-2xl font-semibold mt-3 w-[120px]">
            제목 <span className="text-red-500">*</span>
          </h1>
          <div className="flex-col w-3/5 mt-2">
            <Input productnameHandler={productnameHandler} />
            <div
              className={clsx(
                `flex text-[var(--c-blue)] ml-10 mt-3 `,
                productname ? "invisible" : ""
              )}
            >
              <AiOutlineStop size={24} />
              <span>제목을 입력해 주세요</span>
            </div>
          </div>
        </div>

        <div className="border-b-2 border-black w-full mt-10"></div>

        {/* 카테고리 선택 박스 */}
        <div className="my-10 w-full mx-4">
          <SelectBox categoryHandler={categoryHandler} />
        </div>

        <div className="border-b-2 border-black w-full" />

        <div className="flex mt-10 mx-4">
          {/* <h1 className="text-2xl mr-10 font-semibold">
            거래지역 <span className="text-red-500">*</span>
          </h1> */}
          <div className="flex flex-col mr-10 w-1/6">
            <h1 className="text-2xl font-semibold ">
              거래지역 <span className="text-red-500">*</span>
            </h1>
            <div className="mt-14 text-customLightTextColor">
              지도 위에 마우스 클릭을 통해 <br />내 주변에서 원하는 거래 지역을 <br />
              선택할 수 있어요
            </div>
          </div>
          <div className="w-[600px] h-[500px] mr-5 mb-5">
            <MoveMap setTransActionLocation={setTransActionLocation} />
          </div>
        </div>

        <div className="border-b-2 border-black w-full mt-5" />

        <div className="flex mt-10 mx-4 w-full">
          <div className="flex flex-1">
            <h1 className="text-2xl mr-14 font-semibold ">
              경매 종류 <span className="text-red-500">*</span>
            </h1>
            <div>
              {auth.role && (
                <TypeOfSales option={option} optionHandler={optionHandler} isShopper={auth.role} />
              )}

              <div className={clsx(`flex text-[var(--c-blue)] mt-3`, option ? "invisible" : "")}>
                <AiOutlineStop size={24} />
                <span>경매의 종류를 선택해 주세요</span>
              </div>
            </div>
          </div>
          <div className="flex flex-1">
            <div className="mr-14 w-3/8">
              <p className="text-2xl font-semibold">
                {option !== "할인" ? (
                  option !== "BID" ? (
                    <span>경매 진행 시간</span>
                  ) : (
                    <span>경매 시작 시간</span>
                  )
                ) : (
                  <span>가게 마감 시간</span>
                )}{" "}
                <span className="text-red-500">*</span>
              </p>
            </div>
            <div className="flex flex-col">
              <TimeInput
                hourHandler={hourHandler}
                minuterHandler={minuterHandler}
                option={option}
              />
              <div
                className={clsx(
                  `flex text-[var(--c-blue)]  mt-3`,
                  hour === 0 && minute === 0 ? "" : "invisible"
                )}
              >
                <AiOutlineStop size={40} />
                {option !== "할인" ? (
                  option == "BID" ? (
                    <div>
                      <p>경매 시작 시간은 최소 10분 이상이어야 합니다.</p>
                      <p>경매 시작 시간 설정은 최대 24시간까지 가능합니다.</p>
                    </div>
                  ) : (
                    <div>
                      <p>경매 진행 시간 내에 입찰을 선택하여야 합니다.</p>
                      <p>경매 진행 시간 설정은 최대 24시간까지 가능합니다.</p>
                    </div>
                  )
                ) : (
                  <div>
                    <p>손님에게 가게 마감 시간을 알려주세요!</p>
                    <p>몇시 몇분에 문을 닫는 지 알려주세요.</p>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>

        <div className="border-b-2 border-black w-full mt-10 "></div>

        <div className="flex my-10 w-full min-h-[80px] mx-4">
          <div className="flex-1 flex w-full ">
            <h1 className="text-2xl font-semibold mt-3 w-[130px]">
              {option === "할인" ? <span>정가 </span> : <span>시작 가격</span>}{" "}
              <span className="text-red-500">*</span>
            </h1>
            <div className="flex-col w-3/8">
              <PriceInput priceHandler={priceHandler} />

              <div
                className={clsx(`flex text-[var(--c-blue)] ml-10 mt-3`, price ? "invisible" : "")}
              >
                <AiOutlineStop size={24} />
                <span>가격을 입력해 주세요</span>
              </div>
            </div>
            <div className="text-2xl pl-16 mt-3">원</div>
          </div>
          <div className={clsx(`flex-1 flex w-full`, option !== "할인" ? "invisible" : "")}>
            <h1 className="text-2xl font-semibold mt-3">
              할인가 <span className="text-red-500">*</span>
            </h1>
            <div className="flex-col w-3/8">
              <PriceInput priceHandler={discountPriceHandler} />

              <div
                className={clsx(`flex text-[var(--c-blue)] ml-10 mt-3`, price ? "invisible" : "")}
              >
                <AiOutlineStop size={24} />
                <span>할인 가격은 정가 이하로 입력 가능합니다.</span>
              </div>
            </div>
            <div className="text-2xl pl-16 mt-3">원</div>
          </div>
        </div>

        <div className="border-b-2 border-black w-full mt-10"></div>

        <div className="flex w-full mt-5 mx-4">
          <h1 className="text-2xl font-semibold mr-10 w-[150px]">
            설명 <span className="text-red-500">*</span>
          </h1>
          <div className="w-full mr-14 text-lg">
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
        </div>

        <div className="border-b-2 border-black w-full mt-10"></div>

        <div className="w-full h-80 flex items-center justify-end">
          <button
            onClick={submitHandler}
            disabled={isDoingSubmit}
            className="
            w-1/5 p-10 
            bg-custom-btn-gradient-red
            text-2xl 
            transition duration-300 
            hover:bg-custom-btn-gradient-red-hover 
            text-white rounded-md text-center my-auto "
          >
            등록하기
          </button>
        </div>
      </div>
      {loading && (
        <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex justify-center items-center">
          <ClipLoader color="#247eff" size={250} />
        </div>
      )}
    </div>
  );
};

export default Panmae;
