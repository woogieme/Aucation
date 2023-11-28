import React from 'react';

interface UpdateButtonProps {
  onUpdate: () => void;
  buttonText: string;
}

const UpdateBtn: React.FC<UpdateButtonProps> = ({ onUpdate, buttonText }) => {
  return (
    <div
      className="flex text-sm text-customGray border px-[5px] rounded-sm text-center items-center border-customGray cursor-pointer"
      onClick={onUpdate}
    >
      {buttonText}
    </div>
  );
};

export default UpdateBtn;