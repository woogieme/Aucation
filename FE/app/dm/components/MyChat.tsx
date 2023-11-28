import { RoundedImg } from "@/app/components/tailwinds";
import clsx from "clsx";

interface MyChatProps {
  user: string;
  chat: string;
  userImg: string;
  isOwner: boolean;
}

const MyChat: React.FC<MyChatProps> = ({ user, chat, userImg, isOwner }) => {
  return (
    <div className="flex justify-end items-center py-3 px-2 w-full">
      <div className="flex items-center">
        <div className="mr-3 flex items-end flex-col">
          <div className="flex items-center">
            {isOwner && <p className="text-xs text-gray-400 mr-2">판매자</p>}
            <p className="">{user}</p>
          </div>
          <div
            className={clsx(
              `
                    rounded-2xl
                    p-4
                    bg-blue-400
                `
            )}
          >
            <div className="flex items-center break-all text-right">
              <p>{chat}</p>
            </div>
          </div>
        </div>
        <RoundedImg src={userImg} alt="이미지" />
      </div>
    </div>
  );
};
export default MyChat;
