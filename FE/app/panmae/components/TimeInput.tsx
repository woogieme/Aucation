import React from "react";

interface OwnProps {
  hourHandler: React.ChangeEventHandler<HTMLSelectElement>;
  minuterHandler: React.ChangeEventHandler<HTMLSelectElement>;
  option: string;
}

const TimeInput: React.FC<OwnProps> = ({ hourHandler, minuterHandler, option }) => {
  const hourArray: number[] = Array.from({ length: 24 }, (_, i) => i);
  const minuteArray: number[] = Array.from({ length: 6 }, (_, i) => i * 10);
  return (
    <div className="flex w-full items-center h-3/4 space-x-10">
      <div className="flex items-center">
        <select
          id="hour"
          onChange={hourHandler}
          className=" h-12 border-2
              border-gray-300
              rounded-md
              px-4
              py-2
              mt-1
              text-gray-700
              focus:outline-none
              focus:border-sky-500
              focus:ring-2
              focus:ring-sky-200
              focus:ring-opacity-50"
        >
          {hourArray.map(hour => (
            <option key={hour} value={hour}>
              {hour}
            </option>
          ))}
        </select>
        <label htmlFor="hour" className="ml-5 text-xl">
          {option !== "할인" ? <span>시간</span> : <span>시</span>}
        </label>
      </div>
      <div className="flex items-center">
        <select
          id="minute"
          onChange={minuterHandler}
          className=" h-12 border-2
              border-gray-300
              rounded-md
              px-4
              py-2
              mt-1
              text-gray-700
              focus:outline-none
              focus:border-sky-500
              focus:ring-2
              focus:ring-sky-200
              focus:ring-opacity-50"
        >
          {minuteArray.map(minute => (
            <option key={minute} value={minute}>
              {minute}
            </option>
          ))}
        </select>
        <label htmlFor="hour" className="ml-5 text-xl">
          분
        </label>
      </div>
    </div>
  );
};

export default TimeInput;
