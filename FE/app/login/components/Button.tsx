"use client";

interface ButtonProps {
  onClick: () => void;
  children: React.ReactNode;
}

const Button: React.FC<ButtonProps> = ({ onClick, children }) => {
  return (
    <div
      className="
        bg-customBgLightBlue
        hover:brightness-95
        rounded-md
        ring-1
        ring-inset
        ring-customLightBlue
        w-1/5
        p-4
        inline-block
        text-center
        align-middle
        "
      // w-[20%]
      // h-14
        >
      <button onClick={onClick}>
        <p className="mx-auto break-keep">{children}</p>
      </button>
    </div>
  );
};

export default Button;
