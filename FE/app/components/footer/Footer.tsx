import React from "react";
import Link from "next/link";
import FootPtag from "./components/FootPtag";
import GrayPtag from "./components/GrayPtag";

const Footer: React.FC = () => {
  return (
    <footer className="bg-customGray text-white py-20 px-14">
      <div className="flex flex-row space-x-32">
        <Link href={"/"}>
          <h1 className="text-6xl  font-bold">AUCATION</h1>
        </Link>
        <div className="flex flex-col justify-between  text-xl h-24">
          <FootPtag>믿을 수 있는 경매거래</FootPtag>
          <FootPtag>자주 묻는 질문</FootPtag>
        </div>
        <div className="flex flex-col justify-between  text-xl h-24">
          <FootPtag>광고주 센터</FootPtag>
          <GrayPtag>동네상권</GrayPtag>
        </div>
        <div className="flex flex-col justify-between text-xl h-24">
          <FootPtag>옥케이션 소개</FootPtag>
          <GrayPtag>채용</GrayPtag>
        </div>
        <div className="flex flex-col justify-between  text-xl h-36">
          <GrayPtag>이용약관</GrayPtag>
          <GrayPtag>개인정보처리방침</GrayPtag>
          <GrayPtag>위치기반서비스 이용약관</GrayPtag>
        </div>
      </div>
      <div className="border-t-2 border-customGray my-8"></div>
      <div className="flex flex-row space-x-16">
        <div className="grid grid-cols-1 gap-y-1 text-xl">
          <GrayPtag>
            <span className="font-bold ">고객문의 </span> asdzxc3621@hanmail.net
          </GrayPtag>
          <GrayPtag>
            <span className="font-bold ">지역광고 </span> asdzxc3621@hanmail.net
          </GrayPtag>
          <GrayPtag>대전광역시 유성구 덕명동 124</GrayPtag>
        </div>
        <div className="flex flex-col justify-between text-xl ">
          <GrayPtag>
            <span className="font-bold">채용문의</span> asdzxc3621@hanmail.net
          </GrayPtag>
          <GrayPtag>
            <span className="font-bold">PR문의</span> asdzxc3621@hanmail.net
          </GrayPtag>
          <GrayPtag>사업자 등록번호 012-34-56789</GrayPtag>
        </div>
        <div></div>
        <div></div>
      </div>
    </footer>
  );
};

export default Footer;
