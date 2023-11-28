import React, { useEffect, useState } from "react";
import clsx from "clsx";

interface StateCardProps {
  nowTime: string;
  reAucEndTime: string;
  stateHandler:(state:string) => void;
}

interface TimeLeft {
  days: number;
  hours: number;
  minutes: number;
  seconds: number;
}

const ReAuctionCountDown: React.FC<StateCardProps> = ({ nowTime,stateHandler,reAucEndTime, }) => {
  const [currentTime, setCurrentTime] = useState(new Date(nowTime))
  const endTime = new Date(reAucEndTime);

  const calcTime = (): TimeLeft => {
    let difference: number;
    if (currentTime < endTime) {
      difference = endTime.getTime() - currentTime.getTime();
    } else {
      difference = 0;
    }

    let timeLeft: TimeLeft = {
      days: Math.floor(difference / (1000 * 60 * 60 * 24)),
      hours: Math.floor((difference / (1000 * 60 * 60)) % 24),
      minutes: Math.floor((difference / (1000 * 60)) % 60),
      seconds: Math.floor((difference / 1000) % 60)
    };

    return timeLeft;
  };

  const [timeLeft, setTimeLeft] = useState(calcTime());

  useEffect(() => {
    const timer = setInterval(() => {
        const tmp = currentTime;
        tmp.setSeconds(currentTime.getSeconds() + 1);
        setCurrentTime(tmp);
        setTimeLeft(calcTime());
      if (currentTime < endTime) {
        stateHandler("역경매 종료")
      } else {
        stateHandler("종료")
      }
    }, 1000);

    if (currentTime >= endTime) {
      stateHandler("종료")
      clearInterval(timer);
    }

    return () => clearInterval(timer);
  }, [endTime, currentTime]);

  const { days, hours, minutes, seconds } = timeLeft;

  let statusMessage;
  if (currentTime < endTime) {
    statusMessage = "역경매 종료";
  } else {
    statusMessage = "종료";
  }

  return (
    <div className="font-semibold flex">
      <div className={clsx(statusMessage == "역경매 종료" ? "text-blue-600" : "text-red-500")}>{statusMessage}</div>
      &nbsp;
        <div className="flex text-black font-bold">
            {days > 0 && <div>{days}일</div>}
            {(days > 0 || hours > 0) && <div>&nbsp;{hours}시간 </div>}
            {(days > 0 || hours > 0 || minutes > 0) && <div>&nbsp;{minutes}분</div>}
            {(currentTime <= endTime) && <div>&nbsp;{seconds}초 전</div>}
        </div>

      {/* 경매일시 */}
      
    </div>
  );
};

export default ReAuctionCountDown;
