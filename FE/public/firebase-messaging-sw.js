// /public/firebase-messaging-sw.js
importScripts("https://www.gstatic.com/firebasejs/10.6.0/firebase-app-compat.js");
importScripts("https://www.gstatic.com/firebasejs/10.6.0/firebase-messaging-compat.js");

// 이곳에 아까 위에서 앱 등록할때 받은 'firebaseConfig' 값을 넣어주세요.
const config = {
  apiKey: "AIzaSyAcROxN0Cs7nAlSMdzm_h-8tmNarPJoWzU",
  authDomain: "aucation-f5a83.firebaseapp.com",
  projectId: "aucation-f5a83",
  storageBucket: "aucation-f5a83.appspot.com",
  messagingSenderId: "409998959069",
  appId: "1:409998959069:web:9ce727e4b1297228e49634",
  measurementId: "G-755RTX3Q1G",
};

// Initialize Firebase
firebase.initializeApp(config);

const messaging = firebase.messaging();
