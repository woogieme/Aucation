"use client";

import { useEffect, useRef, useState } from "react";
import MyChat from "./MyChat";
import OtherChat from "./OtherChat";
import Input from "./Input";
import { CompatClient, Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import axios from "axios";
import toast from "react-hot-toast";
import { useRouter } from "next/navigation";

type userData = {
  memberPk: number;
  ownerPk: number;
  ownerNickname: string;
  myNickname: string;
};

interface Chat {
  memberPk: number;
  memberNickname: string;
  messageContent: string;
  imageURL: string;
  messageTime: string;
}

interface DMChatProps {
  prodPk: any;
  prodType: any;
  memberPk: number;
  setChatRoom: React.Dispatch<React.SetStateAction<any>>;
}

const DMChat: React.FC<DMChatProps> = ({ prodPk, prodType, memberPk, setChatRoom }) => {
  const [chatDatas, setChatDatas] = useState<any>();
  const [chats, setChats] = useState<Chat[]>([]);
  const [message, setMessage] = useState<string>("");
  const rapperDiv = useRef<HTMLInputElement>(null);
  const client = useRef<CompatClient>();
  const router = useRouter();
  const connectToWebSocket = () => {
    if (client.current) {
      client.current.deactivate();
    }
    client.current = Stomp.over(() => {
      const ws = new SockJS(`${process.env.NEXT_PUBLIC_CHAT_SERVER_URL}/chat-server`);
      return ws;
    });

    client.current.connect({}, () => {
      client.current!.subscribe(`/sub/${chatDatas.chatUUID}`, res => {
        console.log(JSON.parse(res.body));
        const data = JSON.parse(res.body);
        setChats((chats: Chat[]) => {
          return [...chats, data];
        });
      });
    });
  };
  const bidHandler = (text: string) => {
    client.current!.send(
      `/pub/chat`,
      {},
      JSON.stringify({
        memberPk: memberPk,
        content: text,
        chatUUID: chatDatas.chatUUID,
      })
    );
  };

  useEffect(() => {
    scroll();
  }, [chats]);
  // 스크롤 로직
  const scroll = () => {
    rapperDiv.current!.scrollTo({
      top: rapperDiv.current!.scrollHeight,
      behavior: "smooth",
    });
  };
  const foo = async () => {
    await getChatHandler();
    await scroll();
  };
  useEffect(() => {
    if (chatDatas) {
      connectToWebSocket();
    }
  }, [chatDatas]);

  useEffect(() => {
    foo();
  }, []);

  const getChatHandler = () => {
    console.log(prodPk);
    console.log({
      memberPk: memberPk,
      prodPk: prodPk,
      prodType: prodType,
    });
    axios({
      url: `/api/v2/chat/enter`,
      method: "POST",
      data: {
        memberPk: memberPk,
        prodPk: prodPk,
        prodType: parseInt(prodType),
      },
    })
      .then(res => {
        console.log(res.data);
        setChatDatas(res.data);
        setChatRoom(res.data);
        setChats(res.data.chatList);
      })
      .catch(err => {
        // console.log(err);
        toast.error("정상적이지 않은 접근입니다.");
        router.push("/");
      });
  };

  return (
    <div className="h-full w-full shadow-2xl">
      <div
        className="
      bg-slate-100
      h-full
      w-full
      rounded-3xl
      rounded-b-none
      overflow-y-scroll
      scrollBar
    "
        ref={rapperDiv}
        style={{ backgroundColor: "var(--c-white)" }}
      >
        <div>
          {chats.map((chat, idx) => {
            return memberPk == chat.memberPk ? (
              <MyChat
                user={chat.memberNickname}
                key={idx}
                chat={chat.messageContent}
                userImg={chat.imageURL}
                isOwner={chat.memberPk == chatDatas.sellerPk}
              />
            ) : (
              <OtherChat
                user={chat.memberNickname}
                key={idx}
                chat={chat.messageContent}
                userImg={chat.imageURL}
                isOwner={chat.memberPk == chatDatas.sellerPk}
              />
            );
          })}
        </div>
      </div>
      <div
        className="min-w-full rounded-3xl rounded-t-none
      shadow-xl h-[10%] bg-white mx-auto flex justify-center items-center"
      >
        <Input text={message} setText={setMessage} bidHandler={bidHandler} />
      </div>
    </div>
  );
};
export default DMChat;
