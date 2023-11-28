"use client";

import React, { useState, useEffect } from "react";
import Image from "next/image";
import Star from "@/app/images/Star.png";
import Logo from "@/app/images/aucation_logo.png";

import { useRouter } from "next/navigation";
import NavBtn from "../button/MainBtn";
import { useRecoilState } from "recoil";
import { authState } from "@/app/store/atoms";
import Link from "next/link";
import logo2 from "@/app/images/logo2.png";
const Navbar: React.FC = () => {
  const [auth, setAuth] = useRecoilState(authState);
  const [check, setCheck] = useState<boolean>(false);

  const router = useRouter();

  // const checkAuth = (e: React.MouseEvent<HTMLButtonElement>) => {
  //   console.log(auth);
  // };
  const handleLogout = () => {
    // 로컬 스토리지에서 토큰 제거
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("role");
    localStorage.removeItem("recoil-persist");

    // 로그인 상태 업데이트
    setAuth({ isLoggedIn: false, role: "" });

    // 홈페이지로 리디렉션
    router.push("/login");
  };
  useEffect(() => {
    const checkLocalStorageToken = () => {
      const token = localStorage.getItem("accessToken");
      const whichRole = localStorage.getItem("role");
      if (token) {
        setAuth({ ...auth, isLoggedIn: true, role: whichRole });
      } else {
        setAuth({ ...auth, isLoggedIn: false });
      }
      setCheck(true);
    };
    checkLocalStorageToken();
  }, []);

  return (
    <div>
      {check && (
        <div className="w-full h-28 flex flex-row content-center items-center sticky top-0 z-50 bg-customBasic px-48 border-b border-customLightBlue">
          <div className="w-[250px] ">
            <Link href={`/`} className="flex flex-row">
              <Image src={Logo} alt="로고" className="object-cover" />
            </Link>
          </div>
          <div className="flex flex-row w-auto gap-8 ml-8">
            <Link
              href={`/auction/holding`}
              className={`text-[25px] whitespace-nowrap flex items-center font-semibold hover:text-customLightTextColor`}
            >
              경매 상품
            </Link>
            <Link
              href={`/discount`}
              className="text-[25px] whitespace-nowrap flex items-center hover:text-customLightTextColor font-semibold"
            >
              할인 상품
            </Link>
          </div>
          <div className="flex flex-row-reverse  justify-start w-full gap-8">
            <Link href={auth.isLoggedIn ? "/panmae" : "/login"}>
              <NavBtn className="px-4 text-[22px]">경매 올리기</NavBtn>
            </Link>

            {auth.isLoggedIn ? (
              <a
                onClick={handleLogout}
                className="text-[25px] flex items-center hover:text-customLightTextColor cursor-pointer"
              >
                로그아웃
              </a>
            ) : (
              <Link
                href={`/login`}
                className="text-[25px] flex items-center hover:text-customLightTextColor"
              >
                로그인
              </Link>
            )}

            <Link
              href={auth.isLoggedIn ? "/mypage" : "/login"}
              className="text-[25px] flex items-center hover:text-customLightTextColor"
            >
              마이페이지
            </Link>
          </div>
        </div>
      )}
    </div>
  );
};

export default Navbar;
