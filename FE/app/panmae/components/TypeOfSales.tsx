import React, { useEffect } from "react";

interface OwnProps {
  option: string;
  optionHandler: React.ChangeEventHandler<HTMLInputElement>;
  isShopper: string;
}
interface RadioButtonProps {
  id: string;
  name: string;
  value: string;
  checked: boolean;
  onChange: React.ChangeEventHandler<HTMLInputElement>;
  label: string;
}

const RadioButton: React.FC<RadioButtonProps & { disabled?: boolean }> = ({
  id,
  name,
  value,
  checked,
  onChange,
  label,
  disabled,
}) => {
  return (
    <div className="mr-4 text-xl">
      <input
        type="radio"
        id={id}
        name={name}
        value={value}
        defaultChecked={checked}
        onChange={onChange}
        disabled={disabled}
      />
      <label htmlFor={id} className="ml-2">
        {label}
      </label>
    </div>
  );
};

const TypeOfSales: React.FC<OwnProps> = ({ option, optionHandler, isShopper }) => {
  const radioOptions = [
    { id: "bid", value: "BID", label: "경매" },
    { id: "reversebid", value: "REVERSE_BID", label: "역경매" },
    { id: "discount", value: "할인", label: "할인", disabled: isShopper !== "소상공인" },
  ];

  return (
    <div className="flex">
      {radioOptions.map(opt => (
        <RadioButton
          key={opt.id}
          id={opt.id}
          name="drone"
          value={opt.value}
          checked={option === opt.value}
          onChange={optionHandler}
          label={opt.label}
          disabled={opt.disabled}
        />
      ))}
    </div>
  );
};

export default TypeOfSales;
