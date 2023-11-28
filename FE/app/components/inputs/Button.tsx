"use client";

import clsx from "clsx";

interface ButtonProps {
  children: React.ReactNode;
  onClick?: () => void;
  fullWidth?: boolean;
}

const Button: React.FC<ButtonProps> = ({ children, onClick, fullWidth }) => {
  return (
    <button
      onClick={onClick}
      className={clsx(
        `
        flex
        justify-center
        rounded-md
        px-3
        py-2
        text-sm
        font-semibold
        focus-visible:outline
        focus-visible:outline-2
        focus-visible:outline-offset-2
      `,
        // disabled && "opacity-50 cursor-default",
        fullWidth && "w-full"
        // secondary ? "text-gray-900" : "text-white",
        // danger && "bg-rose-500 hover:bg-rose-600 focus-visible:ring-rose-600",
        // !secondary && !danger && "bg-sky-500 hover:bg-sky-600 focus-visible:outline-sky-600"
      )}
    >
      {children}
    </button>
  );
};
export default Button;
