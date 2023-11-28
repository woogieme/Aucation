"use client";

import clsx from "clsx";

interface InputProps {
  placeholder: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  id: string;
  type: string;
  verify?: boolean;
  onEnter?: () => void;
}

const Input: React.FC<InputProps> = ({
  placeholder,
  value,
  onChange,
  id,
  type,
  verify,
  onEnter,
}) => {
  const loginorsign = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key !== "Enter") return;
    if (onEnter) {
      onEnter();
    } else {
      return;
    }
  };
  return (
    <div className="w-full h-14">
      <div className="h-full">
        <input
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          id={id}
          type={type}
          onKeyDown={e => loginorsign(e)}
          className={clsx(
            `
            block
            w-full
            rounded-md
            border-0
            px-3
            text-gray-900
            shadow-sm
            ring-1
            ring-inset
            ring-customLightBlue
            placeholder:text-gray-400
            focus:ring-2
            focus:ring-inset
            focus:ring-sky-200
            
            h-full  `,
            verify ? "ring-blue-200" : "ring-red-400"
          )}
        />
      </div>
    </div>
  );
};

export default Input;
