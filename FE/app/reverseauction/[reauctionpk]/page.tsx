"use client";

import Image from "next/image";
import BackBtn from "../components/BackBtn";
import LikeBtn from "../components/LikeBtn";
import { RiAuctionLine } from "react-icons/ri";
import { BsFillPersonFill } from "react-icons/bs";
import PriceBox from "../components/PriceBox";
import { use, useEffect, useState } from "react";
import Modal from "../../components/Modal";
import ModalContent from "../components/ModalContent";
import StayMap from "../../components/map/StayMap";
import BidModalContent from "../components/BidModalContent";
import { callApi } from "../../utils/api";
import { useParams, useRouter } from "next/navigation";
import toast from "react-hot-toast";
import ReAuctionCarousel from "../components/ReAuctionCarousel";
import ReAuctionCheckout from "../components/ReAuctionCheckout";
import { AiOutlineArrowLeft, AiOutlineArrowRight } from "react-icons/ai";
import Link from "next/link";
import ReAuctionCountDown from "../components/ReAuctionCountDown";

const AuctionDetail = () => {
  const router = useRouter();
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [bidModalOpen, setBidModalOpen] = useState<boolean>(false);
  const [data, setData] = useState<any>(null); // 경매 데이터
  const [state, setState] = useState<string>();
  const [price, setPrice] = useState<number>(0);
  const [description, setDescription] = useState<string>("");

  const [imagecount, setImagecount] = useState(0);
  const [images, setImages] = useState<string[]>([]); // 이미지의 url 주소를 담는 state
  const [imagefiles, setImagefiles] = useState<File[]>([]); // 이미지의 url 주소를 담는 state

  const [bidCnt, setBidCnt] = useState<number>(0);
  const reauctionPk = useParams().reauctionpk;

  const stateHandler = (value: string) => {
    setState(value);
  };
  const descriptionhandler = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
    setDescription(event.target.value);
  };

  const priceHandler = (price: number) => {
    setPrice(price);
  };
  //좋아요 토글
  const handleLikeClicked = () => {
    callApi("get", `/auction/like/${reauctionPk}`)
      .then((res) => {
        toast.success(res.data.message);
      })
      .catch(err => {
        toast.error(err.response.data.message);
      });
    setData({
      ...data,
      isLike: !data.isLike,
      likeCnt: data.isLike ? data.likeCnt - 1 : data.likeCnt + 1,
    });
  };

  const handleModalOpen = () => {
    setIsModalOpen(!isModalOpen);
  };

  const handleBidModalOpen = () => {
    setBidModalOpen(!bidModalOpen);
  };

  const bidCntHandler = (key: number) => {
    if (bidCnt + key > data.reAuctionBidItems.length - 1) {
      setBidCnt(0);
    } else if (bidCnt + key < 0) {
      setBidCnt(data.reAuctionBidItems.length - 1);
    } else {
      setBidCnt(bidCnt + key);
    }
  };

  const handleBidHandler = () => {
    const formData = new FormData();
    formData.append("reAuctionBidPrice", price.toString());
    formData.append("reAuctionInfo", description);
    imagefiles.forEach(file => {
      formData.append("multipartFiles", file);
    });
    formData.append("reAuctionPk", reauctionPk.toString());
    callApi("post", `/reauction/bid`, formData)
      .then(res => {
        toast.success(res.data.message);
        handleBidModalOpen();
        router.refresh();
      })
      .catch(err => {
        toast.error(err.response.data.message);
        handleBidModalOpen();
      });
  };

  const getDataHandler = () => {
    callApi("get", `/auction/${reauctionPk}`, {})
      .then(res => {
        setData(res.data);
      })
      .catch(err => {
        toast.error(err.response.data.message);
        router.push("/");
      });
  };

  const selectBidHandler = (bidPk: number) => {
    callApi("post", "/reauction/select", {
      reAuctionPk: data.reAuctionPk,
      reAuctionBidPk: bidPk,
    })
      .then(res => {
        toast.success(res.data.message);
        router.refresh()
      })
      .catch(err => {
        toast.error(err.response.data.message);
      });
  };

  useEffect(() => {
    getDataHandler();
  }, []);

  return (
    <div className="w-full px-72 py-20">
      {data && (
        <>
          {/* 좋아요 버튼 및 뒤로가기 버튼 */}
          <div className="flex justify-between">
            <BackBtn />
            <div onClick={handleLikeClicked} className="flex">
              <LikeBtn isLiked={data.isLike} />
              <p className="text-xl ml-3">{data.likeCnt}</p>
            </div>
          </div>

          {/* 페이지 상단 타이틀 */}
          <div className="mt-10">
            <h2 className="text-4xl font-bold">{data.reAuctionTitle}</h2>
            {data ? <h1>{data.title}</h1> : <p>Loading...</p>}
          </div>
          {/* <div className="mt-10">
            <h2 className="text-5xl font-bold mb-7">{data.reAuctionTitle}</h2>
          </div> */}
          <div className="mt-7 text-xl">
            {data.isAction == 2 ? (
              <span className="text-red-600 mr-2">종료</span>
            ) : (
              <span className="text-blue-600 mr-2">역경매</span>
            )}
            <span>{data.reAuctionType}</span>
          </div>

          {/* 경매자 프로필 및 경매참여 인원, 경매까지 시간 */}
          <div className="flex mt-10">
            <Image
              alt="profile"
              className="w-20 h-20 rounded-full"
              src={data.reAuctionOwnerPhoto}
              width={80}
              height={80}
            />

            <div className="ml-4 flex flex-col justify-center flex-1">
              <div>
                <h3 className="text-xl font-medium text-customLightTextColor mb-1">
                  {data.reAuctionOwnerMemberRole == "SHOP" ? "소상공인" : "개인판매자"}
                </h3>
              </div>
              <Link href={`/other/${data.reAuctionOwnerPk}`}>
                <h2 className="text-2xl font-bold">{data.reAuctionOwnerNickname}</h2>
              </Link>
            </div>
            <div className="flex-1 flex justify-end items-end space-x-5">
              <div className=" flex ml-2 text-lg text-customLightTextColor">
                <BsFillPersonFill size={30} />
                {data.reAuctionBidCnt}명 참여중
              </div>
              <div className="mr-7 flex items-end">
                <h3 className="flex text-1xl font-thin ">
                  <ReAuctionCountDown
                    stateHandler={stateHandler}
                    nowTime={data.nowTime}
                    reAucEndTime={data.reAuctionEndTime}
                  />
                </h3>
              </div>
            </div>
          </div>
          <div className="border-t-2 border-gray-400 mt-10"></div>

          {/* 상품 이미지 및 지도 */}
          <div className="flex flex-row mt-8 gap-3">
            <div className="flex flex-col w-full">
              <h2 className="text-2xl text-left mb-5">상품사진</h2>
              <ReAuctionCarousel imglist={data.reAuctionPhoto} />
            </div>
            <div className="flex flex-col w-full">
              <h2 className="text-2xl text-left mb-5">거래 위치(협의가능)</h2>
              <StayMap inputLag={data.reAuctionMeetingLng} inputLat={data.reAuctionMeetingLat} />
            </div>
          </div>

          {/* 가격 버튼 */}

          {data.isAction == 2 && (
            <PriceBox
              startingPrice={data.reAuctionStartPrice}
              lowPrice={data.reAuctionEndPrice}
              endPrice={data.reAuctionEndPrice}
            />
          )}
          {data.isAction == 1 && (
            <PriceBox
              startingPrice={data.reAuctionStartPrice}
              lowPrice={data.reAuctionLowPrice}
              endPrice={0}
            />
          )}
          {data.isAction == 0 && (
            <PriceBox
              startingPrice={data.reAuctionStartPrice}
              lowPrice={data.reAuctionLowPrice}
              endPrice={0}
            />
          )}

          {/* 입찰버튼 */}
          {!data.isOwner && data.isAction == 1 && !bidModalOpen && (
            <div
              className="fixed bottom-4 right-4 rounded-xl flex items-center gap-2 p-6  
              text-[22px] mr-64 mb-8 z-50 bg-custom-btn-gradient hover:bg-custom-btn-gradient-hover hover:cursor-pointer shadow-xl"
              onClick={handleBidModalOpen}
            >
              <RiAuctionLine size={32} color="#F8F9FB" />
              <p className="text-2 text-customBasic">입찰하러 가기</p>
            </div>
          )}

          {/* 상품소개 */}
          <div className="mt-12">
            <h2 className="text-3xl font-bold">상품소개</h2>
            <div className="rounded-lg flex flex-row items-center p-6 bg-gray-100 border border-gray-400 mt-6">
              <h2 className="text-1xl text-customLightTextColor">{data.reAuctionInfo}</h2>
            </div>
          </div>

          <h1 className="mt-20 text-3xl font-bold">입찰 목록</h1>
          {data.reAuctionBidItems && data.reAuctionBidItems.length != 0 && data.isOwner && (
            <div className="flex flex-row items-center">
              <button
                className="absolute left-[12rem] text-5xl"
                onClick={() => {
                  bidCntHandler(-1);
                }}
              >
                <AiOutlineArrowLeft />
              </button>
              <ReAuctionCheckout
                onClick={handleModalOpen}
                data={data.reAuctionBidItems[bidCnt]}
                canClick={true}
                selectBidHandler={selectBidHandler}
              />
              <button className="absolute right-[12rem] text-5xl" onClick={() => bidCntHandler(1)}>
                <AiOutlineArrowRight />
              </button>
            </div>
          )}
          {data.ownBid && !data.isOwner && (
            <ReAuctionCheckout onClick={handleModalOpen} data={data.ownBid} canClick={false} />
          )}

          {data.selectedBid && data.isOwner && (
            <ReAuctionCheckout onClick={handleModalOpen} data={data.selectedBid} canClick={false} />
          )}

          {!data.selectedBid &&
            data.reAuctionBidItems &&
            data.reAuctionBidItems.length != 0 &&
            isModalOpen && (
              <Modal onClick={handleModalOpen}>
                <ModalContent images={data.reAuctionBidItems[bidCnt].bidPhotos} />
              </Modal>
            )}

          {data.ownBid && isModalOpen && (
            <Modal onClick={handleModalOpen}>
              <ModalContent images={data.ownBid.bidPhotos} />
            </Modal>
          )}

          {data.selectedBid && isModalOpen && (
            <Modal onClick={handleModalOpen}>
              <ModalContent images={data.selectedBid.bidPhotos} />
            </Modal>
          )}
          {bidModalOpen && (
            <Modal onClick={handleBidModalOpen}>
              <BidModalContent
                priceHandler={priceHandler}
                price={price}
                descriptionhandler={descriptionhandler}
                description={description}
                images={images}
                imagecount={imagecount}
                setImagecount={setImagecount}
                imagefiles={imagefiles}
                setImagefiles={setImagefiles}
                setImages={setImages}
                handleBidModalOpen={handleBidModalOpen}
                handleBidHandler={handleBidHandler}
              />
            </Modal>
          )}
          {!data.ownBid && !data.selectedBid && <div className="h-[20rem]"></div>}
        </>
      )}
    </div>
  );
};

export default AuctionDetail;
