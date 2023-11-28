"use client";
import React, { useState, useEffect, useRef, useCallback } from "react";

import HeaderTap from "../components/HeaderTap";
import OrderTypeBtn from "../components/OrderTypeBtn";
import { orderType, searchType } from "../components/type";
import { CategoryNameList } from "../../components/frontData/categoryNameList";
import DropdownButton from "../../components/button/DropDownBtn";
import SearchInput from "../components/SearchInput";
import { callApi } from "../../utils/api";
import Pagination from "react-js-pagination";
import "../components/Paging.css";
import { AuctionData, AuctionItem, PreAuctionData } from "../../components/Card/cardType";

import Image from "next/image";
import { useRouter } from "next/navigation";
import ClipLoader from "react-spinners/ClipLoader";
import PreAuctionListCard from "@/app/components/Card/PreAuctionListCard";
import NoResult from "../NoResult";
import toast from "react-hot-toast";

type PageParams = {
  headerTap: string;
};

const AuctionList = ({ params }: { params: PageParams }) => {
  const router = useRouter();
  const [selectedTap, setSelectedTap] = useState("경매전"); // 상단 탭, 경매 유형
  const [selectedCategory, setSelectedCategory] = useState<string>("전체"); // 카테고리
  const [searchType, setSearchType] = useState<searchType>({ id: 0, typeName: "제목" }); // 검색유형
  const [searchKeyword, setSearchKeyword] = useState<string>(""); // 검색키워드
  const [selectedOrderType, setSelectedOrderType] = useState<orderType>({
    id: 1,
    typeName: "최신순",
  }); // 정렬기준
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [pageNumber, setPageNumber] = useState<number>(1);
  const [data, setData] = useState<PreAuctionData>({
    nowTime: null, // 현재 시간
    currentPage: 0, // 현재 페이지
    totalPage: 0, // 총 페이지
    preItems: [], // 경매 아이템 목록
  });
  const [totalItemsCount, setTotalItemsCount] = useState(0); // 전체 아이템 수

  const tmp = new Date();

  const tapList = ["경매중", "경매전", "역경매"];
  const localCategoryList = ["전체", ...CategoryNameList];

  const orderTypeList: orderType[] = [
    { id: 1, typeName: "최신순" },
    { id: 2, typeName: "고가순" },
    { id: 3, typeName: "저가순" },
    { id: 4, typeName: "좋아요순" },
  ];
  const searchTypeList: searchType[] = [
    { id: 0, typeName: "제목" },
    { id: 1, typeName: "판매자" },
  ];

  const handlePageChange = (page: number) => {
    setPageNumber(page);
  };

  const headerHandler = (tap: string) => {
    setSelectedTap(tap);
    switch (tap) {
      case "경매중":
        router.push("/auction/holding");
        break;
      case "경매전":
        router.push("/auction/before");
        break;
      case "역경매":
        router.push("/auction/reverse-auction");
        break;
      default:
        // 기본 경우, 추가 처리가 필요하다면 여기에 작성
        break;
    }
  };

  const fetchAuctionData = useCallback(() => {
    setIsLoading(true);
    const searchFilters = {
      auctionCatalog: selectedCategory !== "전체" ? selectedCategory : null,
      auctionCondition: selectedOrderType.id,
      searchType: searchType.id,
      searchKeyword: searchKeyword,
    };
    callApi("post", `/auction/list/pre/${pageNumber}`, searchFilters)
      .then(response => {
        setData(response.data);
      })

      .catch(error => {
        toast.error(error.response.data.message)
      })
      .finally(() => {
        setIsLoading(false); // 데이터 로딩 완료
      });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedCategory, selectedOrderType.id, pageNumber, , searchKeyword]);

  useEffect(() => {
    fetchAuctionData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedCategory, selectedOrderType.id, pageNumber, searchKeyword]);

  const handleSearch = (keyword: string) => {
    setSearchKeyword(keyword); // 상태 업데이트
  };

  return (
    <div className="px-48 ">
      <div className="flex flex-row space-x-10 items-baseline mt-10">
        <div className="font-black text-5xl">경매 상품</div>
        {tapList.map((tap: string, idx) => (
          <HeaderTap
            key={idx}
            tap={tap}
            selected={tap === selectedTap}
            headerHandler={headerHandler}
          />
        ))}
      </div>

      <div className="flex flex-row h-[75px] items-center justify-between mb-7 mt-4">
        <div className="flex flex-row gap-5">
          <DropdownButton
            options={localCategoryList}
            selectedOption={selectedCategory}
            setSelectedOption={setSelectedCategory}
            size="big"
          />
          {orderTypeList.map(orderType => (
            <OrderTypeBtn
              key={orderType.id}
              orderType={orderType}
              selectedOrderType={selectedOrderType.typeName === orderType.typeName}
              setOrderType={setSelectedOrderType}
            />
          ))}
        </div>

        <div className="flex flex-row gap-5">
          <DropdownButton
            options={searchTypeList.map(st => st.typeName)}
            selectedOption={searchType.typeName}
            setSelectedOption={(typeName: string) => {
              const newSearchType = searchTypeList.find(st => st.typeName === typeName);
              if (newSearchType) {
                setSearchType(newSearchType);
              }
            }}
            size="small"
          />

          <SearchInput searchHandler={handleSearch} setSearchKeyword={setSearchKeyword} />
        </div>
      </div>
      {/* {dummyData.map(item => (
          <div key={item.auctionPk} className="shadow-lg h-[450px] rounded-lg">
            <AuctionListCard item={item} nowTime={tmp} />
          </div>
        ))} */}
      {isLoading ? (
        <div className="flex justify-center items-center">
          <ClipLoader color="#247eff" size={150} speedMultiplier={1} />
        </div>
      ) : data.preItems.length > 0 ? (
        <div className="grid grid-cols-5 gap-x-6 gap-y-10">
          {data.preItems.map(item => (
            <div key={item.auctionPk} className="shadow-lg h-[600px] rounded-lg">
              <PreAuctionListCard item={item} nowTime={data.nowTime} />
            </div>
          ))}
        </div>
      ) : (
        // <Image
        //   src="/assets/images/noResults.png"
        //   alt="검색 결과가 없어요"
        //   width={1536}
        //   height={400} // 여기서 통일된 높이를 사용하세요
        // />
        <NoResult />
      )}

      <Pagination
        activePage={pageNumber}
        itemsCountPerPage={15}
        totalItemsCount={data.totalPage}
        pageRangeDisplayed={5}
        prevPageText={"‹"}
        nextPageText={"›"}
        onChange={handlePageChange}
      />
    </div>
  );
};

export default AuctionList;
