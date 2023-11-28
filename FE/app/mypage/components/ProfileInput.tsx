import React, { ChangeEvent } from 'react';

interface InputProps {
  value?: string;
  onChange?: (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void;
  onTextAreaChange?: (e: ChangeEvent<HTMLTextAreaElement>) => void;
  size?: 'small' | 'medium' | 'large';
}

const ProfileInput: React.FC<InputProps> = ({ value, onChange, onTextAreaChange, size = 'medium' }) => {
  if (size === 'large') {
    return (
      <textarea
        className="flex border-2 mt-3 border-gray-400 px-2 rounded-sm focus:outline-none focus:border-sky-500 focus:ring-2 focus:ring-sky-200 focus:ring-opacity-50 disabled:opacity-50 disabled:cursor-not-allowed h-[167px] w-4/5"
        value={value}
        onChange={onTextAreaChange}
      />
    );
  }

  let inputClassName = "flex border-2 border-gray-400 px-2 py-1 rounded-md focus:outline-none focus:border-sky-500 focus:ring-2 focus:ring-sky-200 focus:ring-opacity-50 disabled:opacity-50 disabled:cursor-not-allowed";

  if (size === 'small') {
    inputClassName += ' text-xl';
  } else if (size === 'medium') {
    inputClassName += ' text-2xl w-[240px]';
  }

  return (
    <input
      type="text"
      className={inputClassName}
      value={value}
      onChange={onChange as (e: ChangeEvent<HTMLInputElement>) => void}
    />
  );
};

export default ProfileInput;
