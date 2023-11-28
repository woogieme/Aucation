import React, { useCallback, useEffect, useState } from "react";
import clsx from "clsx";
interface StateCardProps {
  currentTime: Date;
  auctionEndTime: Date;
  stateHandler: (state: string) => void;
  isPre?: boolean;
}

interface TimeLeft {
  days: number;
  hours: number;
  minutes: number;
  seconds: number;
}

const AuctionCountDown: React.FC<StateCardProps> = ({
  auctionEndTime,
  stateHandler,
  currentTime,
  isPre = false,
}) => {
  const [nowtime, setNowtime] = useState(new Date(currentTime));
  // eslint-disable-next-line react-hooks/exhaustive-deps
  const endTime = new Date(auctionEndTime);

  const calcTime = useCallback((): TimeLeft => {
    let difference: number;
    if (nowtime < endTime) {
      difference = endTime.getTime() - nowtime.getTime();
    } else {
      difference = 0;
    }

    let timeLeft: TimeLeft = {
      days: Math.floor(difference / (1000 * 60 * 60 * 24)),
      hours: Math.floor((difference / (1000 * 60 * 60)) % 24),
      minutes: Math.floor((difference / (1000 * 60)) % 60),
      seconds: Math.floor((difference / 1000) % 60),
    };

    return timeLeft;
  }, [endTime, nowtime]);

  const [timeLeft, setTimeLeft] = useState(calcTime());

  useEffect(() => {
    const timer = setInterval(() => {
      const tmp = nowtime;
      tmp.setSeconds(nowtime.getSeconds() + 1);
      setNowtime(tmp);
      setTimeLeft(calcTime());
      if (nowtime < endTime) {
        stateHandler("경매종료");
      } else {
        stateHandler("마감");
      }
    }, 1000);

    if (nowtime >= endTime) {
      clearInterval(timer);
    }

    return () => clearInterval(timer);
  }, [calcTime, endTime, nowtime, stateHandler]);

  const { days, hours, minutes, seconds } = timeLeft;

  let statusMessage;
  if (nowtime < endTime) {
    isPre ? (statusMessage = "경매시작") : (statusMessage = "경매종료");
  } else {
    isPre ? (statusMessage = "경매중") : (statusMessage = "경매마감");
  }

  return (
    <span className="flex w-full">
      <div
        className={clsx(
          "pr-2",
          statusMessage == "경매마감"
            ? "text-red-500"
            : statusMessage == "경매종료" || "경매시작"
            ? "text-customBlue"
            : "text-black"
        )}
      >
        {statusMessage}
      </div>
      {days > 0 && <div className=" text-customGray">{days}일</div>}
      {(days > 0 || hours > 0) && <div className=" text-customGray">&nbsp;{hours}&nbsp;시간</div>}
      {(days > 0 || hours > 0 || minutes > 0) && <div className=" text-customGray">&nbsp;{minutes}&nbsp;분</div>}
      {nowtime <= endTime && (
        <div className=" text-customGray">
          &nbsp;{seconds}초 <span className=" text-customGray">전</span>
        </div>
      )}
    </span>
  );
};

export default AuctionCountDown;
