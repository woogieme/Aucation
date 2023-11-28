import { RoundedImg } from "@/app/components/tailwinds";
import clsx from "clsx";

interface OtherChatProps {
  user: string;
  myname: string;
  chat: string;
  userImg: string;
  isOwner: boolean;
}

const OtherChat: React.FC<OtherChatProps> = ({ user, myname, chat, userImg, isOwner }) => {
  return (
    <div className="flex justify-start items-center py-3 px-2 w-full">
      <div className="flex items-center">
        <RoundedImg src={userImg} alt="이미지" />
        <div className="ml-3 flex items-start flex-col">
          <div className="flex items-center">
            <p className="">{user}</p>
            {isOwner && <p className="text-xs text-gray-400 ml-2">판매자</p>}
          </div>
          <div
            className={clsx(
              `
                    rounded-2xl
                    p-4
                `,
              user === myname ? "bg-sky-300" : "bg-blue-400"
            )}
          >
            <div className="flex items-center break-all">
              <p>{chat}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
export default OtherChat;
