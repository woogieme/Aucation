import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import RecoilRootProvider from "./utils/recoilRootProvider";
import Navbar from "./components/navbar/Navbar";
import Footer from "./components/footer/Footer";
import ToasterContext from "./utils/ToasterContext";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { Roboto, Noto_Sans_KR } from "next/font/google";

const inter = Inter({ subsets: ["latin"] });

const notoSansKr = Noto_Sans_KR({
  // preload: true, 기본값
  subsets: ["latin"], // 또는 preload: false
  weight: ["100", "400", "700", "900"], // 가변 폰트가 아닌 경우, 사용할 fontWeight 배열
});
const roboto = Roboto({
  subsets: ["latin"], // preload에 사용할 subsets입니다.
  weight: ["100", "400", "700"],
  variable: "--roboto", // CSS 변수 방식으로 스타일을 지정할 경우에 사용합니다.
});
export const cls = (...classnames: string[]) => {
  return classnames.join(" ");
};

export const metadata: Metadata = {
  title: "Aucation",
  description: "중고 경매 플랫폼",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="ko">
      <RecoilRootProvider>
        <body className={cls(notoSansKr.className, roboto.variable)}>
          <Navbar />
          <ToastContainer />
          <ToasterContext />
          {children}
          <Footer />
        </body>
      </RecoilRootProvider>
    </html>
  );
}
// cls(notoSansKr.className, roboto.variable)
