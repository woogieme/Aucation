"use client";

import { useEffect, useRef, useState } from "react";
import MyChat from "./MyChat";
import OtherChat from "./OtherChat";
import Input from "./Input";
import { callApi } from "@/app/utils/api";
import { useParams } from "next/navigation";
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
  memberNickname: string;
  messageContent: string;
  imageURL: string;
  messageTime: string;
}

const AuctionChat = () => {
  const router = useRouter();
  const [userDatas, setUserDatas] = useState<userData>({
    memberPk: 0,
    ownerNickname: "",
    ownerPk: 0,
    myNickname: "",
  });
  const [chats, setChats] = useState<Chat[]>([]);
  const [message, setMessage] = useState<string>("");
  const uuid = useParams().uuid;
  const rapperDiv = useRef<HTMLInputElement>(null);
  const client = useRef<CompatClient>();
  const connectToWebSocket = () => {
    if (client.current) {
      client.current.deactivate();
    }
    client.current = Stomp.over(() => {
      const ws = new SockJS(`${process.env.NEXT_PUBLIC_CHAT_SERVER_URL}/chat-server`);
      return ws;
    });

    client.current.connect({}, () => {
      client.current!.subscribe(`/sub/${uuid}`, res => {
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
      `/pub/groupchat/${uuid}`,
      {},
      JSON.stringify({
        memberPk: userDatas.memberPk,
        content: text,
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
    bidDataHandler();
    connectToWebSocket();
  }, []);
  useEffect(() => {
    if (userDatas.memberPk) {
      foo();
    }
  }, [userDatas.memberPk]);

  const bidDataHandler = () => {
    callApi("get", `/auction/place/${uuid}`, {})
      .then(res => {
        console.log(res.data);
        setUserDatas((datas: userData) => {
          return {
            ...datas,
            memberPk: res.data.memberPk,
            ownerPk: res.data.ownerPk,
            ownerNickname: res.data.ownerNickname,
            myNickname: res.data.myNickname,
          };
        });
      })
      .catch(err => {
        if (err.response.data.code == "P002") {
          toast.error(err.response.data.message);
          router.push("/");
        }
      });
  };

  const getChatHandler = () => {
    axios
      .get(`/api/v2/groupchat/enter/${uuid}/${userDatas.memberPk}`)
      .then(res => {
        setChats(res.data);
        console.log(res.data);
      })
      .catch(err => {
        console.log(err);
      });
  };

  return (
    <div className="h-[80%] w-[30%] mr-5 my-auto shadow-2xl">
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
            return chat.memberNickname == userDatas.myNickname ? (
              <MyChat
                user={chat.memberNickname}
                myname={userDatas.myNickname}
                key={idx}
                chat={chat.messageContent}
                userImg={chat.imageURL}
                isOwner={chat.memberNickname === userDatas.ownerNickname}
              />
            ) : (
              <OtherChat
                user={chat.memberNickname}
                myname={userDatas.myNickname}
                key={idx}
                chat={chat.messageContent}
                userImg={chat.imageURL}
                isOwner={chat.memberNickname === userDatas.ownerNickname}
              />
            );
          })}
        </div>
      </div>
      <div
        className="min-w-full rounded-3xl rounded-t-none
      shadow-xl h-[10%] bg-white mx-auto"
      >
        <Input text={message} setText={setMessage} bidHandler={bidHandler} />
      </div>
    </div>
  );
};
export default AuctionChat;
