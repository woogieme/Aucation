import { initializeApp } from "firebase/app";
import { getMessaging, onMessage, getToken } from "firebase/messaging";
import { callApi } from "./api";
import { noti } from "./toast";
const firebaseConfig = {
  apiKey: "AIzaSyAcROxN0Cs7nAlSMdzm_h-8tmNarPJoWzU",
  authDomain: "aucation-f5a83.firebaseapp.com",
  projectId: "aucation-f5a83",
  storageBucket: "aucation-f5a83.appspot.com",
  messagingSenderId: "409998959069",
  appId: "1:409998959069:web:9ce727e4b1297228e49634",
  measurementId: "G-755RTX3Q1G",
};
export const onMessageFCM = async () => {
  // 브라우저에 알림 권한을 요청합니다.
  const permission = await Notification.requestPermission();
  if (permission !== "granted") return;

  // 이곳에도 아까 위에서 앱 등록할때 받은 'firebaseConfig' 값을 넣어주세요.
  const firebaseApp = initializeApp(firebaseConfig);

  const messaging = getMessaging(firebaseApp);

  // 이곳 vapidKey 값으로 아까 토큰에서 사용한다고 했던 인증서 키 값을 넣어주세요.
  getToken(messaging, {
    vapidKey:
      "BAioNavuRzqkuAY7bJ6MiOeq3YxnavlrcHHC11qNqkZrDzKLi8VoJlNQ6e2l3VOsbKqNJKM5UQ3fg0O_DR6qM6U",
  })
    .then(currentToken => {
      if (currentToken) {
        // 정상적으로 토큰이 발급되면 콘솔에 출력합니다.
        console.log(currentToken);
        callApi("POST", "/members/saveFCM", { token: currentToken })
          .then(res => {
            console.log(res.data);
          })
          .catch(err => {
            console.log(err);
          });
      } else {
        console.log("No registration token available. Request permission to generate one.");
      }
    })
    .catch(err => {
      console.log("An error occurred while retrieving token. ", err);
    });

  // 메세지가 수신되면 역시 콘솔에 출력합니다.
  onMessage(messaging, payload => {
    console.log("Message received. ", payload);
    if (payload.data!.status == "역경매") {
      if (payload.data!.type == "chat") {
        noti(payload.notification!.body!, `/dm/${payload.data!.prodPk}/1`);
      } else {
        noti(payload.notification!.body!, `/reverseauction/${payload.data!.prodPk}`);
      }
    } else if (payload.data!.status == "경매") {
      if (payload.data!.type == "chat") {
        noti(payload.notification!.body!, `/dm/${payload.data!.prodPk}/0`);
      } else {
        noti(payload.notification!.body!, `/bid/${payload.data!.auctionUUID}`);
      }
    } else {
      if (payload.data!.type == "chat") {
        noti(payload.notification!.body!, `/dm/${payload.data!.prodPk}/2`);
      } else {
        noti(payload.notification!.body!, `/detail/discount/${payload.data!.prodPk}`);
      }
    }
  });
};
