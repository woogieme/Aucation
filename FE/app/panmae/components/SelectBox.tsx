import React from "react";
import { CategoryNameList } from "@/app/components/frontData/categoryNameList";
interface OwnProps {
  categoryHandler: (e: React.ChangeEvent<HTMLSelectElement>) => void;
}

const SelectBox: React.FC<OwnProps> = ({ categoryHandler }) => {
  return (
    <div className="w-4/5 flex">
      <label htmlFor="category" className="w-[160px]">
        <h1 className="text-2xl font-semibold my-3 ">
          카테고리 <span className="text-red-500">*</span>
        </h1>
      </label>
      <select
        name="category"
        id="category"
        onChange={categoryHandler}
        className="w-1/5 h-12 border-2
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
        {CategoryNameList.map((category, idx) => {
          return (
            <option key={idx} value={category}>
              {category}
            </option>
          );
        })}
      </select>
    </div>
  );
};

export default SelectBox;
