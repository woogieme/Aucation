"use client";

import clsx from "clsx";
interface InputProps {
  label: string;
  id: string;
  change: (value: string) => void;
  value: string;
}

const Input: React.FC<InputProps> = ({ label, id, change, value }) => {
  return (
    <div>
      <label
        className="
        block
        text-sm
        font-medium
        leading-6
        text-gray-900"
        htmlFor="id"
      >
        {label}
      </label>
      <div className="mt-2">
        <input
          id={id}
          value={value}
          onChange={e => change(e.target.value)}
          className={clsx(
            `
            form-input
            block
            w-4/5
            rounded-md
            border-0
            py-1.5
            text-gray-900
            shadow-sm
            ring-1
            ring-inset
            ring-gray-300
            placeholder:text-gray-400
            focus:ring-2
            focus:ring-inset
            focus:ring-sky-600
            sm:text-sm
            sm:leading-6`
          )}
        />
      </div>
    </div>
  );
};

export default Input;
