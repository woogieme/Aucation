import React, { useEffect, useState } from "react";
import clsx from "clsx";
interface StateCardProps {
  currentTime: Date;
  auctionStartTime: Date;
  stateHandler: (state: string) => void;
}

interface TimeLeft {
  days: number;
  hours: number;
  minutes: number;
  seconds: number;
}

const CountDown: React.FC<StateCardProps> = ({ auctionStartTime, stateHandler, currentTime }) => {
  const [nowtime, setNowtime] = useState(currentTime);
  const endTime = new Date(auctionStartTime.getTime() + 1 * 30 * 60 * 1000);

  const calcTime = (): TimeLeft => {
    let difference: number;
    if (nowtime < auctionStartTime) {
      difference = auctionStartTime.getTime() - nowtime.getTime();
    } else if (nowtime < endTime) {
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
  };

  const [timeLeft, setTimeLeft] = useState(calcTime());

  useEffect(() => {
    const timer = setInterval(() => {
      const tmp = nowtime;
      tmp.setSeconds(nowtime.getSeconds() + 1);
      setNowtime(tmp);
      setTimeLeft(calcTime());
      if (nowtime < auctionStartTime) {
        stateHandler("경매시작");
      } else if (nowtime < endTime) {
        stateHandler("경매종료");
      } else {
        stateHandler("종료");
      }
    }, 1000);

    if (nowtime >= endTime) {
      clearInterval(timer);
    }

    return () => clearInterval(timer);
  }, [auctionStartTime, endTime, nowtime]);

  const { days, hours, minutes, seconds } = timeLeft;

  let statusMessage;
  if (nowtime < auctionStartTime) {
    statusMessage = "경매시작";
  } else if (nowtime < endTime) {
    statusMessage = "경매종료";
  } else {
    statusMessage = "종료";
  }

  return (
    <span className="flex w-full justify-end text-customLightTextColor">
      <div
        className={clsx("flex mr-2 items-center font-medium",
          statusMessage == "종료"
            ? "text-red-500"
            : statusMessage == "경매종료"
            ? "text-customBlue"
            : "text-black"
        )}
      >
        {statusMessage}
      </div>
      <div className="flex font-medium">

        {days > 0 && <div>{days}:</div>}
        {(days > 0 || hours > 0) && <div>&nbsp;{hours}&nbsp;:</div>}
        {(days > 0 || hours > 0 || minutes > 0) && <div>&nbsp;{minutes}&nbsp;:</div>}
        {nowtime <= endTime && <div className="font-medium">&nbsp;{seconds} 전</div>}
      </div>
    </span>
  );
};

export default CountDown;
