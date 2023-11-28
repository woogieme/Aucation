"use client";

import Image from "next/image";
import { useRouter } from "next/navigation";
import clsx from "clsx";
import { useRef, useState, useEffect, ChangeEvent } from "react";

// 이미지 업로드 및 기본 프로필
import ImageUpload from "./components/ImageUpload";
import defaultprofile from "@/app/images/defaultprofile.png";

// 프로필 업데이트
import ProfileInput from "./components/ProfileInput";
import UpdateBtn from "./components/UpdateBtn";

// 카테고리 박스
import CategoryBox from "./components/CategoryBox";

// 가격 단위 파싱
import "../utils/pricecal";

// 카드 컴포넌트 (세로, 경매 판매/구매, 좋아요, 역경매 - 판매/구매)
import AuctionSell from "../components/Card/AuctionSell";
import AuctionBuy from "../components/Card/AuctionBuy";
import LikeCard from "../components/Card/LikeCard";
import ReAuctionSell from "../components/Card/ReAuctionSell";
import ReAuctionBuy from "../components/Card/ReAuctionBuy";
import DiscountSell from "../components/Card/DiscountSell";
import DiscountBuy from "../components/Card/DiscountBuy";
// api요청
import { callApi } from "../utils/api";

// 페이지 네이션
import Pagination from "react-js-pagination";
import "../auction/components/Paging.css";

// 결제 라이브러리 아임포트
import Script from "next/script";
import { NextPage } from "next";
import { RequestPayParams, RequestPayResponse } from "iamport-typings";
import toast from "react-hot-toast";
import Link from "next/link";

// 아이콘
import { HiBuildingStorefront } from "react-icons/hi2";
import { BsPersonFill } from "react-icons/bs";
import { BiWon } from "react-icons/bi";
// 모달
import PaymentModal from "../components/modal/PaymentModal";

interface userData {
  profileImg: string;
  nickname: string;
  info: string;
  pk: number;
  auctionStartTime: Date;
}

type CategoryTypes = {
  [key: string]:
    | string[]
    | {
        판매?: string[];
        구매?: string[];
      };
};
interface categoryLikeType {
  // pk: number
  category: string;
  state: string;
  title: string;
  imgUrl: string;
  isLiked: boolean;
}
const MyPage: NextPage = () => {
  // apiData
  const [dataList, setDataList] = useState<any>();
  // 프로필 이미지/ 네임 / 인포
  const router = useRouter();
  const [images, setImages] = useState<string>("");
  const [imgFile, setImgFile] = useState<string | null>(null);
  const imgRef = useRef<HTMLInputElement>(null);

  // 사용자 이름, 정보 , 업데이트 상태
  const [username, setUsername] = useState<string>("사용자01");
  const [info, setInfo] = useState<string>(
    " 안녕하세요 사용자01입니다. 가전제품을 전문적으로 경매합니다. 안녕하가전제품을 전문적으로 경매합니다. 안녕하세요 사용자01입니다. 가전제품을 전문적으로 경매합니다. 안녕하세요 사용자01입니다. 가전제품을 전문적으로 경매합니다. 안녕하세요 사용자01입니다. 가전제품을 전문적으로 경매합니다."
  );
  const [usernameUpdate, setUsernameUpdate] = useState<boolean>(false);
  const [userInfoUpdate, setUserInfoUpdate] = useState<boolean>(false);

  // 소상공인 인증 모달, 포인트 충전 모달
  const [isShopModal, setIsShopModal] = useState<boolean>(false); // 소상공인 인증 모달 여부
  const [isPointModal, setIsPointModal] = useState<boolean>(false); // 포인트 충전 모달 여부
  const [myPoint, setMyPoint] = useState(0);

  // 현재 선택된 카테고리 및 카테고리 목록
  const [category, setCategory] = useState<string>("경매");
  const [secondCategory, setSecondCategory] = useState<any>("판매");
  const [thirdCategory, setThirdCategory] = useState<string>("경매전");
  const [itemsort, setItemsort] = useState<string>("최신순");

  // 현재 페이지, 총 페이지
  const [pageNumber, setPageNumber] = useState<number>(1);
  const [totalpage, setTotalpage] = useState<number>(1);

  // 소상공인 판단
  const [isShop, setIsShop] = useState<any>("개인");
  const [shopNum, setShopNum] = useState<any>("");

  const [isOpen, setIsOpen] = useState<boolean>(false);

  const apiUrl: any = {
    경매: "members/mypage/auction",
    역경매: "members/mypage/reauction",
    할인: "members/mypage/discount",
    좋아요: "members/mypage/likeauction",
  };
  const categories: any = {
    경매: {
      판매: ["경매전", "경매중", "경매완료"],
      구매: ["낙찰", "구매완료"],
    },
    역경매: {
      판매: ["입찰중", "낙찰", "거래완료"],
      구매: ["경매중", "입찰완료", "경매종료"],
    },
    할인: {
      판매: ["판매중", "예약중", "판매완료"],
      구매: ["예약중", "구매완료"],
    },
    좋아요: {
      전체: ["경매", "역경매", "할인"],
    },
  };

  // 요소 정렬
  const itemsortList: string[] = ["최신순", "저가순", "고가순"];

  // 이미지 업로드
  const handleImageUpload = (imageFile: File) => {
    const formData = new FormData();
    formData.append("multipartFile", imageFile);
    callApi("patch", "members/modify/image", formData)
      .then(res => {
        toast.success(res.data.message);
      })
      .catch(error => {
        toast.error(error.response.data.message);
      });
  };

  // 사용자 이름, 정보 업데이트 중인지 판별
  const handleUsernameUpdate = () => {
    setUsernameUpdate(true);
    setUserInfoUpdate(false);
  };
  const handleUserInfoUpdate = () => {
    setUsernameUpdate(false);
    setUserInfoUpdate(true);
  };
  const handleNameUpdateConfirm = () => {
    setUsernameUpdate(false);
    let data = {
      memberNickname: username,
    };
    callApi("patch", "members/modify/nickname", data)
      .then(res => {
        toast.success(res.data.message);
      })
      .catch(err => {
        toast.error(err.response.data.message);
      });
  };
  const handleInfoUpdateConfirm = () => {
    setUserInfoUpdate(false);
    let data = {
      memberDetail: info,
    };
    callApi("patch", "members/modify/detail", data)
      .then(res => {
        toast.success(res.data.message);
      })
      .catch(err => {
        toast.error(err.response.data.message);
      });
  };

  // 유저 네임 / 인포 수정
  const handleUsernameChange = (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setUsername(e.target.value);
  };
  const handleInfoChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
    setInfo(e.target.value);
  };

  // 모달 핸들러
  const pointModalHandler = () => {
    setIsPointModal(!isPointModal);
  };
  const shopModalHandler = () => {
    setIsShopModal(!isShopModal);
  };

  // 사업자 등록번호 입력
  const shopInputHandler = (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setShopNum(e.target.value);
  };

  const shopRegistHandler = () => {
    const data = {
      smallkey: shopNum,
    };
    callApi("post", "shop/verify/business", data)
      .then(res => {
        toast.success(res.data.message);
        shopModalHandler();
        localStorage.setItem("role", "소상공인");
      })
      .catch(err => {
        toast.error(err.response.data.message);
      });
  };

  // 대장 카테고리 핸들러
  const categoryHandler = (value: string) => {
    setCategory(value);
  };
  // 2번째 카테고리 핸들러
  const secondCategoryHandler = (value: string) => {
    setSecondCategory(value);
  };
  // 3번째 카테고리 핸들러
  const thirdCategoryHandler = (value: string) => {
    setThirdCategory(value);
  };

  // 요소 정렬
  const itemSortHandler = (value: string) => {
    setItemsort(value);
  };
  // 카드 삭제
  const deleteHandler = (prodPk: number) => {
    let data: any;
    if (category !== "역경매") {
      data = {
        status: category,
        prodPk: prodPk,
      };
    } else if (secondCategory == "판매") {
      data = {
        status: "입찰",
        prodPk: prodPk,
      };
    } else {
      data = {
        status: "역경매",
        prodPk: prodPk,
      };
    }

    // console.log("삭제할때 데이터", JSON.stringify(data, null, 2));
    callApi("delete", "/members/delete", data)
      .then(res => {
        let data: any;
        if (category !== "좋아요") {
          data = {
            productStatus: secondCategory,
            auctionStatus: thirdCategory,
            productFilter: itemsort,
            myPageNum: pageNumber,
          };
        } else {
          data = {
            productStatus: thirdCategory,
            myPageNum: pageNumber,
          };
        }
        toast.success(res.data.message);
        callApi("post", `${apiUrl[category]}`, data)
          .then(res => {
            setDataList(res.data);
            setUsername(res.data.memberNickname);
            setMyPoint(res.data.memberPoint);
            setInfo(res.data.memberDetail);
            setIsShop(res.data.memberRole);
            setTotalpage(res.data.totalPage);
          })
          .catch(err => {
            toast.error(err.response.data.message);
          });
      })
      .catch(err => {
        toast.error(err.response.data.message);
      });
  };

  const cashHandler = (value: any) => {
    if (!window.IMP) return;
    const { IMP } = window;
    const impkey = `${process.env.NEXT_PUBLIC_IAMPORT_IMP}`;
    IMP.init(impkey);
    const data = {
      pg: "kakaopay.TC0ONETIME",
      pay_method: "card",
      merchant_uid: `mid_${new Date().getTime()}`,
      name: "AUCATION",
      amount: value,
      buyer_email: "aucation@aucation.com",
      buyer_name: "오케이션",
      buyer_tel: "010-1234-5678",
      buyer_addr: "남해",
      buyer_postcode: "123-456",
    };
    const callback = (response: RequestPayResponse) => {
      const { success, error_msg } = response;
      if (success) {
        toast.success("결제 성공");
        callApi("post", "/verifyIamport", {
          amount: response.paid_amount,
          impUID: response.imp_uid,
        })
          .then(() => {
            setIsOpen(false);
            setMyPoint(data.amount + myPoint);
          })
          .catch(err => {
            toast.error(err.response.data.message);
          });
      } else {
        toast.error(`결제 실패: ${error_msg}`);
      }
    };

    IMP.request_pay(data, callback);
  };

  // 페이지
  const handlePageChange = (page: number) => {
    setPageNumber(page);
  };

  const confirmHandler = (type: string, discountUUID?: string, auctionPk?: number) => {
    const apiUrl: any = {
      BID: "/auction/confirm",
      REVERSE_BID: "/reauction/confirm",
      DISCOUNT_BID: `/discount/confirm/${discountUUID}`,
    };
    let data: any;
    data = {
      productStatus: secondCategory,
      auctionStatus: thirdCategory,
      productFilter: itemsort,
      myPageNum: pageNumber,
    };
    let auctionData: any;
    auctionData = {
      auctionPk: auctionPk,
    };
    let reAuctionData: any;
    reAuctionData = {
      reAuctionPk: auctionPk,
    };
    callApi(
      type == "DISCOUNT_BID" ? "get" : "post",
      `${apiUrl[type]}`,
      type == "BID" ? auctionData : type == "REVERSE_BID" ? reAuctionData : ""
    )
      .then(res => {
        tmp();
        toast.success(res.data.message);
        callApi("post", `${apiUrl[category]}`, data)
          .then(res => {
            tmp();
            setDataList(res.data);
            setUsername(res.data.memberNickname);
            setMyPoint(res.data.memberPoint);
            setInfo(res.data.memberDetail);
            setIsShop(res.data.memberRole);
            setTotalpage(res.data.totalPage);
          })
          .catch(err => {
            toast(err.response.data.message);
          });
      })
      .catch(err => {
        toast(err.response.data.message);
      });
  };
  useEffect(() => {
    // 브라우저에서 로컬 스토리지에 접근하여 토큰 확인
    const accessToken = window.localStorage.getItem("accessToken");
    // 토큰이 없는 경우 로그인 페이지로 리다이렉션
    if (!accessToken) {
      router.push("/login");
    } else {
      // 토큰이 있다면 마이페이지 조회
      // fetchMypage()
    }
  }, [router]);

  // 1번 카테고리 변경 시 2번 카테고리 초기화
  useEffect(() => {
    if (category !== "좋아요") {
      const firstCategory = Object.keys(categories[category])[0];
      secondCategoryHandler(firstCategory);
    } else {
      secondCategoryHandler("전체");
    }
  }, [category]);

  useEffect(() => {
    if (category !== "좋아요") {
      const secondCategoryData = categories[category][secondCategory];
      if (secondCategoryData && secondCategoryData.length > 0) {
        const defaultThirdCategory = secondCategoryData[0];
        thirdCategoryHandler(defaultThirdCategory);
      } else {
        // 두 번째 카테고리 데이터가 없을 때 처리
        console.error("두 번째 카테고리 데이터가 없습니다.");
        // 아니면 디폴트값을 설정하거나 다른 대응을 취할 수 있습니다.
      }
    } else {
      const defaultThirdCategory = categories[category]["전체"][0];
      thirdCategoryHandler(defaultThirdCategory);
    }
  }, [category, secondCategory]);

  // 3번재 카테고리 바뀔 시 페이지 초기화
  useEffect(() => {
    setPageNumber(1);
  }, [thirdCategory]);
  // 데이터 불러오기
  const tmp = () => {
    setDataList({ mypageItems: [] });
  };
  useEffect(() => {
    tmp();

    let data: any;
    if (category !== "좋아요") {
      data = {
        productStatus: secondCategory,
        auctionStatus: thirdCategory,
        productFilter: itemsort,
        myPageNum: pageNumber,
      };
    } else {
      data = {
        productStatus: thirdCategory,
        myPageNum: pageNumber,
      };
    }
    // 유저 데이터 불러오기
    console.log(JSON.stringify(data, null, 2), "보내는 데이터");
    callApi("post", `${apiUrl[category]}`, data)
      .then(res => {
        console.log(res.data);
        setDataList(res.data);
        setImages(res.data.imgURL);
        setUsername(res.data.memberNickname);
        setMyPoint(res.data.memberPoint);
        setInfo(res.data.memberDetail);
        setIsShop(res.data.memberRole);
        setTotalpage(res.data.totalPage);
      })
      .catch(err => {
        toast.error(err.response.data.message);
      });
  }, [thirdCategory, itemsort, pageNumber]);

  if (dataList) {
    return (
      <div className="w-full px-80 py-20">
        <Script src="https://cdn.iamport.kr/v1/iamport.js" />
        {/* 프로필 영역 */}
        {/* 결제 */}
        <div>
          <div className="border-t border-customGray"></div>
          <div className="flex gap-6 ">
            {/* 이미지 업로드 */}
            <label htmlFor="img_file" className="">
              <ImageUpload onImageUpload={handleImageUpload} imageURL={images} />
            </label>
            {/* 사용자 이름, 정보 업데이트 */}
            <div className="flex-col w-full">
              <div className="flex mt-5 w-full place-content-between">
                <div className="flex gap-5">
                  <span className="flex items-center w-[50px]">
                    {isShop == "SHOP" ? (
                      <HiBuildingStorefront size={40} />
                    ) : (
                      <BsPersonFill size={40} />
                    )}
                  </span>
                  {/* 유저네임/ 유저네임 인풋 */}
                  <div className="flex text-lg max-w-[250px] items-center align-bottom">
                    {!usernameUpdate && (
                      <div className="font-bold whitespace-nowrap overflow-hidden text-ellipsis">
                        {username}
                      </div>
                    )}
                    {usernameUpdate && (
                      <ProfileInput
                        value={username}
                        onChange={handleUsernameChange}
                        size="medium"
                      />
                    )}
                  </div>
                  {/* 수정버튼 */}
                  <div className="flex items-center">
                    {!usernameUpdate && (
                      <UpdateBtn onUpdate={handleUsernameUpdate} buttonText="수정하기" />
                    )}
                    {usernameUpdate && (
                      <div className="flex">
                        <UpdateBtn onUpdate={handleNameUpdateConfirm} buttonText="확인" />
                      </div>
                    )}
                  </div>
                </div>

                {/* 소상공인 인증 */}
                {isShop !== "SHOP" ? (
                  <div className="flex items-center">
                    소상공인 이신가요? &nbsp;
                    <span
                      className="text-customBlue text-lg h font-bold hover:underline cursor-pointer"
                      onClick={shopModalHandler}
                    >
                      인증하기
                    </span>
                    {isShopModal && (
                      <div className="absolute z-1 mt-[200px] right-[360px]">
                        <div className="relative bg-white rounded-lg border-4 border-blue-300 p-6 ">
                          <div className="absolute border-x-8 border-x-transparent border-b-[16px] border-b-blue-300 bottom-full right-1 transform translate-x-[-50%]"></div>
                          <div className="flex-col">
                            <span className="flex text-[24px] justify-center mr-10 mb-4 font-bold">
                              사업자 번호 입력
                            </span>
                            <div className="flex gap-2">
                              <ProfileInput value={shopNum} onChange={shopInputHandler} />
                              <span
                                className="flex border-2 border-blue-300 px-2 py-1 items-center rounded-lg cursor-pointer hover:bg-gray-100"
                                onClick={shopRegistHandler}
                              >
                                인증
                              </span>
                            </div>
                          </div>
                        </div>
                      </div>
                    )}
                  </div>
                ) : (
                  ""
                )}
              </div>
              {/* 사용자 정보창 / 사용자 정보 업데이트창 */}
              <div>
                {!userInfoUpdate && (
                  <div className="pr-4 py-3 leading-[1.57] rounded-2xl h-[167px] w-4/5 break-all whitespace-pre-wrap overflow-y-auto">
                    {info}
                  </div>
                )}
                {userInfoUpdate && (
                  <div>
                    <ProfileInput value={info} onTextAreaChange={handleInfoChange} size="large" />
                  </div>
                )}
              </div>
              {/* 수정버튼 */}
              <div className="flex h-[56px] items-center justify-between">
                {!userInfoUpdate && (
                  <UpdateBtn onUpdate={handleUserInfoUpdate} buttonText="소개글 수정" />
                )}
                {userInfoUpdate && (
                  <div className="flex w-[156px]">
                    <UpdateBtn onUpdate={handleInfoUpdateConfirm} buttonText="확인" />
                  </div>
                )}
                <div className="flex text-lg">
                  내 포인트 :&nbsp;
                  <span className="font-bold">{myPoint.toLocaleString()}</span>&nbsp;
                  <span className="flex items-center" onClick={pointModalHandler}>
                    <BiWon size={25} />
                  </span>
                  <span
                    className="cursor-pointer ml-6 text-customBlue font-bold"
                    onClick={() => setIsOpen(!isOpen)}
                  >
                    충전하기
                  </span>
                </div>
              </div>
              <PaymentModal onClose={() => setIsOpen(false)} isOpen={isOpen} cash={cashHandler} />
            </div>
          </div>
          <div className="border-t border-customGray"></div>

          {/* 첫번째 카테고리 출력 및 클릭 효과 */}
          <div className="rounded-lg flex p-3 bg-gray-100 border border-gray-400 text-gray-700 mt-16 cursor-pointer">
            <div className="flex flex-1 h-20">
              {Object.keys(categories).map((item, idx) => (
                <CategoryBox
                  name={item}
                  selectedCategory={category}
                  key={idx}
                  categoryHandler={categoryHandler}
                  css="flex items-center justify-center text-2xl flex-1 font-semibold "
                  dynamicCss={"first"}
                />
              ))}
            </div>
          </div>

          {/* 2번째 카테고리 출력 및 클릭 시 저장 */}
          <div className="flex mt-20 gap-3 items-center">
            <h2 className="font-semibold text-2xl">{category} 상품</h2>
            {/* 상품개수 바인딩 */}
            <h2 className="text-red-600  text-2xl font-bold ml-2">{dataList.count}</h2>
            {/* 카테고리 - 판매/구매 */}
            <div className={clsx("flex gap-3 items-center", category !== "좋아요" ? "" : "hidden")}>
              {category !== "좋아요" && (
                <>
                  {Object.keys(categories[category]).map((item, idx) => (
                    <CategoryBox
                      name={item}
                      selectedCategory={secondCategory!}
                      key={idx}
                      categoryHandler={secondCategoryHandler}
                      css="border ml-4 rounded-xl text-lg px-3 py-1 cursor-pointer transition-transform transform duration-300 hover:scale-110"
                      dynamicCss={"second"}
                    />
                  ))}
                </>
              )}
            </div>
          </div>
          <div className="border-t border-customGray mt-10"></div>
          {/* 카테고리 - 상태값 ex) 경매전/ 중 / 완료  */}

          {/* 3번째 카테고리 출력 */}
          <div className="flex justify-between mt-10">
            <div className="flex">
              <div className="flex gap-4 text-xl font-semibold h-[35px] items-center cursor-pointer">
                {category !== "좋아요" &&
                  categories[category][secondCategory]?.map((item: any, idx: any) => (
                    <CategoryBox
                      name={item}
                      selectedCategory={thirdCategory!}
                      key={idx}
                      categoryHandler={thirdCategoryHandler}
                      css="flex items-center justify-center font-semibold "
                      dynamicCss={"second"}
                    />
                  ))}

                {category == "좋아요" &&
                  categories[category][secondCategory]?.map((item: any, idx: any) => (
                    <CategoryBox
                      name={item}
                      selectedCategory={thirdCategory}
                      key={idx}
                      categoryHandler={thirdCategoryHandler}
                      css="flex items-center justify-center font-semibold "
                      dynamicCss={"second"}
                    />
                  ))}
              </div>
              <div className="ml-4">
                <span className="font-bold text-xl ml-4 text-red-500">
                  {dataList.mypageItems.length}
                </span>
                {category !== "좋아요" && <span className="font-bold ml-2 text-xl">개</span>}
              </div>
            </div>

            {/* 솔트 */}
            {category !== "좋아요" && (
              <div className="flex text-lg font-semibold text-center cursor-pointer text-customGray">
                {itemsortList.map((item, idx) => (
                  <div key={idx}>
                    <div
                      className={clsx(
                        "text-lg",
                        itemsort == item ? "font-bold text-customBlue" : ""
                      )}
                      onClick={() => itemSortHandler(item)}
                    >
                      <span>|</span>&nbsp;{item}&nbsp;
                    </div>
                  </div>
                ))}
                |
              </div>
            )}
          </div>

          {/* 경매 - 판매 */}
          {category == "경매" && secondCategory == "판매" && dataList.mypageItems.length > 0 && (
            <div>
              {/* DummyUserData.mypageItems */}
              {dataList.mypageItems?.map((item: any, idx: any) => (
                <AuctionSell item={item} key={idx} deleteHandler={deleteHandler} />
              ))}
            </div>
          )}

          {/* 경매 - 구매 */}
          {category == "경매" && secondCategory == "구매" && dataList.mypageItems.length > 0 && (
            <div>
              {/* dataList.mypageItems? */}
              {dataList.mypageItems?.map((item: any, idx: any) => (
                <AuctionBuy item={item} key={idx} confirmHandler={confirmHandler} />
              ))}
            </div>
          )}

          {/* 역경매 판매 */}
          {/* dataList.mypageItems.length > 0 && */}
          {category == "역경매" && secondCategory == "판매" && dataList.mypageItems.length > 0 && (
            <div>
              {/* {dataList.mypageItems?.map((item:any, idx:any) => ( */}
              {dataList.mypageItems.map((item: any, idx: any) => (
                <ReAuctionSell item={item} key={idx} deleteHandler={deleteHandler} />
              ))}
            </div>
          )}

          {/* 역경매 구매 */}
          {category == "역경매" && secondCategory == "구매" && dataList.mypageItems.length > 0 && (
            <div>
              {/* {dataList.mypageItems?.map((item:any, idx:any) => ( */}
              {dataList.mypageItems?.map((item: any, idx: any) => (
                <ReAuctionBuy
                  item={item}
                  key={idx}
                  deleteHandler={deleteHandler}
                  confirmHandler={confirmHandler}
                />
              ))}
            </div>
          )}

          {/* 할인 - 판매 */}
          {category == "할인" && secondCategory == "판매" && dataList.mypageItems.length > 0 && (
            <div>
              {/* {dataList.mypageItems?.map((item:any, idx:any) => ( */}
              {dataList.mypageItems?.map((item: any, idx: any) => (
                <DiscountSell
                  item={item}
                  key={idx}
                  thirdCategory={thirdCategory!}
                  deleteHandler={deleteHandler}
                />
              ))}
            </div>
          )}
          {/* 할인 - 구매 */}
          {category == "할인" && secondCategory == "구매" && dataList.mypageItems.length > 0 && (
            <div>
              {dataList.mypageItems?.map((item: any, idx: any) => (
                <DiscountBuy item={item} key={idx} confirmHandler={confirmHandler} />
              ))}
            </div>
          )}
          <div className="flex">
            {/* 좋아요 */}
            {category == "좋아요" && (
              <div className="flex flex-wrap gap-[26px]">
                {dataList.mypageItems?.map((item: any, idx: any) => (
                  <LikeCard item={item} key={idx} />
                ))}
              </div>
            )}
          </div>

          {/* 페이지 네이션 */}
          {category !== "좋아요" && (
            <div className="flex justify-center mt-4">
              <Pagination
                activePage={pageNumber}
                itemsCountPerPage={5}
                totalItemsCount={5 * totalpage}
                pageRangeDisplayed={5}
                prevPageText={"‹"}
                nextPageText={"›"}
                onChange={handlePageChange}
              />
            </div>
          )}
          {category == "좋아요" && (
            <div className="flex justify-center mt-4">
              <Pagination
                activePage={pageNumber}
                itemsCountPerPage={8}
                totalItemsCount={8 * totalpage}
                pageRangeDisplayed={5}
                prevPageText={"‹"}
                nextPageText={"›"}
                onChange={handlePageChange}
              />
            </div>
          )}
        </div>
      </div>
    );
  }
};
export default MyPage;
