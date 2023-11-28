"use client";
import Image from "next/image";
import { useEffect, useRef, useState } from "react";
import { AiOutlineArrowLeft, AiOutlineHeart } from "react-icons/ai";
import { RiUserFill, RiTimerFlashLine } from "react-icons/ri";
import { FaRegHandPaper } from "react-icons/fa";
import dojang from "@/app/images/dojang.png";
import { Stomp, CompatClient, IStompSocket } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import calculateRemainingTime from "@/app/utils/timer";
import { useParams, useRouter } from "next/navigation";
import { callApi } from "@/app/utils/api";
import toast from "react-hot-toast";
import Modal from "@/app/components/Modal";
import ModalContent from "@/app/reverseauction/components/ModalContent";
import { pricetoString } from "@/app/utils/pricecal";
import axios from "axios";

export type auctionData = {
  memberPk: number; // 입찰을 위한 pk
  ownerPk: number;
  memberPoint: number;
  title: string; // 경매 제목(품목이 없기 때문 제목으로 표시)
  detail: string; // 경매 상품 설명
  ownerNickname: string;
  ownerPicture: string;
  picture: string[]; // url 형식의 string을 list에 담아서 제공할 예정
  ownerType: string; // ["소상공인", "개인"]
  nowPrice: number; // 최고가 (입찰 없을 시 시작가로 대체)
  askPrice: number; // 입찰단위
  enterTime: number; // 시간 계산을 위해 서버시간 제공
  endTime: number; // 종료 시간
  headCnt: number; // 현재 접속자수
  highBid: boolean; // 현재 자신이 최고가인 사람인지 여부
};

const AuctionMainPage = () => {
  const [datas, setDatas] = useState<auctionData>({
    memberPk: 0, // 입찰을 위한 pk
    ownerPk: 0,
    memberPoint: 0,
    title: "아이폰 14", // 경매 제목(품목이 없기 때문 제목으로 표시)
    detail: "상품 설명", // 경매 상품 설명
    ownerNickname: "test",
    ownerPicture: "https://cdn.thecolumnist.kr/news/photo/202302/1885_4197_221.jpg",
    picture: [
      "https://cdn.thecolumnist.kr/news/photo/202302/1885_4197_221.jpg",
      "https://cdn.thecolumnist.kr/news/photo/202302/1885_4197_221.jpg",
    ], // url 형식의 String을 list에 담아서 제공할 예정
    ownerType: "소상공인", // ["소상공인", "개인"]
    nowPrice: 10000, // 최고가 (입찰 없을 시 시작가로 대체)
    askPrice: 1, // 입찰단위
    enterTime: Date.now(), // 시간 계산을 위해 서버시간 제공
    endTime: Date.now(), // 종료 시간
    headCnt: 0, // 현재 접속자수
    highBid: false, // 현재 자신이 최고가인 사람인지 여부
  });
  const [headCnt, setHeadCnt] = useState<number>(0); // 현재 접속자수
  const [remainTime, setRemainTime] = useState<string>("00:00:00"); // 남은 시간
  const [isModal, setIsModal] = useState<boolean>(false); // 모달 여부
  const [isBid, setIsBid] = useState<boolean>(false); // 입찰 여부
  const uuid = useParams().uuid;
  const router = useRouter();

  const modalHandler = () => {
    setIsModal(!isModal);
  };
  useEffect(() => {
    const timer = setInterval(() => {
      const startTime = Date.now(); // 종료 시간을 적절히 설정하세요
      const endTime = new Date(datas.endTime); // 현재 시간을 적절히 설정하세요

      const remainingTime = calculateRemainingTime(startTime, endTime.getTime());
      setRemainTime(remainingTime);
    }, 1000);

    return () => clearInterval(timer);
  }, [remainTime]);

  const client = useRef<CompatClient>();

  // 웹소켓 연결 및 이벤트 핸들러 설정
  const connectToWebSocket = () => {
    if (client.current) {
      client.current.disconnect();
    }
    client.current = Stomp.over(() => {
      const ws = new SockJS(`${process.env.NEXT_PUBLIC_SERVER_URL}/auc-server`);
      return ws;
    });
    client.current.connect({}, () => {
      // const _transport = (client.current!.webSocket as any)?._transport.url.split("/")[5];
      client.current!.subscribe(`/topic/sub/${uuid}`, res => {
        // console.log(JSON.parse(res.body));
        const data = JSON.parse(res.body);
        if (data.messageType == "count") {
          // console.log(data.headCnt, 1231241245125124);
          setHeadCnt(data.headCnt);
          return;
        } else if (data.messageType == "error") {
          if (data.memberPk == datas.memberPk) {
            toast.error(data.errMessage);
            return;
          } else if (data.code == "P002") {
            toast.error(data.message);
            router.push("/");
            return;
          } else {
            return;
          }
        }
        if (data.firstUser == datas.memberPk) {
          setDatas((datas: auctionData) => {
            return {
              ...datas,
              memberPoint: data.firstUserPoint,
              nowPrice: data.firstBid,
              highBid: true,
              askPrice: data.askPrice,
            };
          });
        } else if (data.secondUser == datas.memberPk) {
          setDatas((datas: auctionData) => {
            return {
              ...datas,
              memberPoint: data.secondUserPoint,
              nowPrice: data.firstBid,
              highBid: false,
              askPrice: data.askPrice,
            };
          });
        } else {
          setDatas((datas: auctionData) => {
            return {
              ...datas,
              nowPrice: data.firstBid,
              askPrice: data.askPrice,
            };
          });
        }
      });
    });
  };

  useEffect(() => {
    bidDataHandler();
  }, []);
  useEffect(() => {
    if (datas.memberPk != 0) {
      if (!client.current) {
        connectToWebSocket();
      }
    }
    setIsBid(datas.highBid);
  }, [datas]);

  const bidHandler = () => {
    client.current!.send(
      `/app/send/register/${uuid}`,
      {},
      JSON.stringify({
        memberPk: datas.memberPk,
        message: "입찰",
      })
    );
  };

  const bidDataHandler = () => {
    callApi("get", `/auction/place/${uuid}`, {})
      .then(res => {
        setDatas((datas: auctionData) => {
          return {
            ...datas,
            memberPk: res.data.memberPk,
            ownerPk: res.data.ownerPk,
            memberPoint: res.data.memberPoint,
            title: res.data.title,
            detail: res.data.detail,
            ownerNickname: res.data.ownerNickname,
            ownerPicture: res.data.ownerPicture,
            picture: res.data.picture,
            ownerType: res.data.ownerType,
            nowPrice: res.data.nowPrice,
            askPrice: res.data.askPrice,
            enterTime: res.data.enterTime,
            endTime: res.data.endTime,
            headCnt: res.data.headCnt,
            highBid: res.data.highBid,
          };
        });
        setHeadCnt(res.data.headCnt);
      })
      .catch(err => {
        router.push("/");
        return;
        // if (err.response.data.code == "P002") {
        //   toast.error(err.response.data.message);
        //   router.push("/");
        // } else if (err.response.data.code == "P009") {
        //   toast.error(err.response.data.message);
        //   router.push("/");
        // }
      });
  };

  const exitHandler = () => {
    router.back();
  };

  return (
    <div
      className="
        mr-5
        w-[80%]
        ml-6
    "
    >
      <div
        className="
            flex
            justify-between
            pt-6
            px-6
            h-10
            items-center
        "
      >
        <button onClick={exitHandler}>
          <AiOutlineArrowLeft size="40"></AiOutlineArrowLeft>
        </button>
        <div
          className="
            flex
            justify-center
            items-center
          "
        ></div>
      </div>
      <div
        className="
            flex
            justify-between
            items-end
            mt-8
            mb-3
        "
      >
        <h1 className="text-4xl font-bold mx-6">{datas.title}</h1>
        <div
          className="
            flex
            mx-3
          "
        >
          <RiUserFill size="24"></RiUserFill>
          <p className="text-base px-2">{headCnt}</p>
          <RiTimerFlashLine size="24"></RiTimerFlashLine>
          <div className="w-20">
            <p className="text-base px-2">{remainTime}</p>
          </div>
        </div>
      </div>
      <div
        className="
            flex
            justify-between
            min-h-[40%]
        "
      >
        <div
          className="
            mx-6
            my-3
            w-1/3
            min-h-full
          "
        >
          <p className="mb-2 text-lg">상품 사진</p>
          <div
            className="
              relative
              w-full
              h-full
              hover:cursor-pointer
            "
            onClick={modalHandler}
          >
            <Image alt="상품사진" src={datas.picture[0]} fill></Image>
          </div>
        </div>
        <div
          className="
            mx-6
            my-3
            w-3/5
          "
        >
          <p className="mb-2 text-lg">상품 설명</p>
          <div
            className="
                p-4
                min-h-full
                break-all
                bg-[var(--c-white)]
            "
          >
            <div className="w-full h-1/5 p-3 flex">
              <Image
                src={datas.ownerPicture}
                className="rounded-full"
                width={60}
                height={60}
                alt="프로필"
              />
              <div className="ml-3">
                <p>{datas.ownerType}</p>
                <h1 className="text-2xl">{datas.ownerNickname}</h1>
              </div>
            </div>
            <p>{datas.detail}</p>
          </div>
        </div>
      </div>
      <div
        className="
        border-b-2
        border-slate-200
        mt-10
        w-[95%]
        mx-auto
      "
      >
        {/* 밑줄 div */}
      </div>
      <div
        className="
        mt-6
        mx-6
      "
      >
        <span className="text-3xl text-gray-400">최고가</span>
        <span
          className="text-5xl decoration-sky-200 mx-3 relative font-bold min-w-[180px]"
          style={{ color: "var(--c-blue)" }}
        >
          {pricetoString(datas.nowPrice)}
          {isBid && (
            <Image
              src={dojang}
              alt="낙찰"
              className="absolute -top-20 -right-24"
              width={150}
              height={150}
            ></Image>
          )}
        </span>
        <p className="mt-3 text-stone-400">방심은 금물! 언제 최고 낙찰자가 나올 지 몰라요 </p>
      </div>
      <div
        className="
        mt-6
        mx-6
        rounded-lg
        bg-stone-100
        w-[95%]
        h-[20%]
        border-2
        border-gray-400
        flex
        text-gray-500
        items-center
      "
      >
        <div className="w-[30%] ml-5">
          <p>다음 입찰가</p>
          <p className="text-3xl">{pricetoString(datas.askPrice)}</p>
        </div>
        <div
          className="
            border-r-2
        border-gray-500
            mx-3
            h-[80%]
        "
        ></div>
        <div className="w-[50%] ml-5">
          <p>보유 포인트</p>
          <p className="text-3xl">{pricetoString(datas.memberPoint)}</p>
        </div>
        {datas.memberPk != datas.ownerPk && (
          <div
            className="w-[15%] bg-blue-400 h-[30%] mr-3 rounded-lg flex items-center justify-center text-white hover:bg-blue-500
            hover:border-2
            hover:border-blue-700
            hover:cursor-pointer"
            onClick={bidHandler}
          >
            <FaRegHandPaper className="inline-block mr-3"></FaRegHandPaper>
            <span>입찰</span>
          </div>
        )}
      </div>
      {isModal && (
        <Modal onClick={modalHandler}>
          <ModalContent images={datas.picture} />
        </Modal>
      )}
    </div>
  );
};
export default AuctionMainPage;
