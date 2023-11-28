import { ReactNode } from "react";

interface ModalProps {
  children: ReactNode;
  onClick: () => void;
}

const Modal: React.FC<ModalProps> = ({ children, onClick }) => {
  return (
    <div
      className="fixed top-0 left-0 w-[1920px] h-full bg-[rgba(0,0,0,0.4)] justify-center flex items-center z-50"
      onClick={onClick}
    >
      {children}
    </div>
  );
};
export default Modal;
