import React, { useEffect, useState } from "react";
import clsx from "clsx";

interface StateCardProps {
  curTime: Date;
  auctionStartTime: Date;
  stateHandler:(state:string) => void;
}

interface TimeLeft {
  days: number;
  hours: number;
  minutes: number;
  seconds: number;
}

const CountDown: React.FC<StateCardProps> = ({ auctionStartTime,stateHandler,curTime }) => {
  const [currentTime, setCurrentTime] = useState(new Date(curTime));
  const endTime = new Date(auctionStartTime.getTime() + 1 * 30 * 60 * 1000);

  const calcTime = (): TimeLeft => {
    let difference: number;
    if (currentTime < auctionStartTime) {
      difference = auctionStartTime.getTime() - currentTime.getTime();
    } else if (currentTime < endTime) {
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
      setCurrentTime(new Date());
      setTimeLeft(calcTime());
      if (currentTime < auctionStartTime) {
        stateHandler("경매시작")
      } else if (currentTime < endTime) {
        stateHandler("경매종료")
      } else {
        stateHandler("종료")
      }
    }, 1000);

    if (currentTime >= endTime) {
      stateHandler("종료")
      clearInterval(timer);
    }

    return () => clearInterval(timer);
  }, [auctionStartTime, endTime, currentTime]);

  const { days, hours, minutes, seconds } = timeLeft;

  let statusMessage;
  if (currentTime < auctionStartTime) {
    statusMessage = "경매시작";
  } else if (currentTime < endTime) {
    statusMessage = "경매종료";
  } else {
    statusMessage = "";
  }

  return (
    <div className="flex">
      <div className={clsx(statusMessage == "경매시작" ? "text-blue-600" : statusMessage == "경매종료" ? "text-red-600" : "text-black" )}>{statusMessage}</div>
      
      {days > 0 && <div>{days}일</div>}
      {(days > 0 || hours > 0) && <div>&nbsp;{hours}시간 </div>}
      {(days > 0 || hours > 0 || minutes > 0) && <div>&nbsp;{minutes}분</div>}
      {(currentTime <= endTime) && <div>&nbsp;{seconds}초 전</div>}

      {/* 경매일시 */}
      {statusMessage == "" && <div>경매 일시 : {auctionStartTime.toLocaleString()}</div>} 
      
    </div>
  );
};

export default CountDown;
