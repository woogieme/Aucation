"use client";
import React, { useState, useEffect, useRef, useCallback } from "react";

import OrderTypeBtn from "@/app/auction/components/OrderTypeBtn";
import { orderType, searchType } from "@/app/auction/components/type";
import { CategoryNameList } from "@/app/components/frontData/categoryNameList";
import DropdownButton from "@/app/components/button/DropDownBtn";
import SearchInput from "@/app/auction/components/SearchInput";
import { callApi } from "@/app/utils/api";
import Pagination from "react-js-pagination";
import "@/app/auction/components/Paging.css";
import { DiscountData } from "@/app/components/Card/cardType";
import DiscountListCard from "@/app/components/Card/DiscountListCard";
// import dummyData from "../components/dummyData";
import Image from "next/image";
import { useRouter } from "next/navigation";
import ClipLoader from "react-spinners/ClipLoader";
import NoResult from "../auction/NoResult";
import toast from "react-hot-toast";

const DiscountList = () => {
  const router = useRouter();
  const [selectedCategory, setSelectedCategory] = useState<string>("전체"); // 카테고리
  const [searchType, setSearchType] = useState<searchType>({ id: 0, typeName: "제목" }); // 검색유형
  const [searchKeyword, setSearchKeyword] = useState<string>(""); // 검색키워드
  const [selectedOrderType, setSelectedOrderType] = useState<orderType>({
    id: 1,
    typeName: "최신순",
  }); // 정렬기준
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [pageNumber, setPageNumber] = useState<number>(1);
  const [data, setData] = useState<DiscountData>({
    nowTime: null, // 현재 시간
    currentPage: 0, // 현재 페이지
    totalPage: 0, // 총 페이지
    items: [], // 경매 아이템 목록
  });

  const localCategoryList = ["전체", ...CategoryNameList];

  const orderTypeList: orderType[] = [
    { id: 1, typeName: "최신순" },
    { id: 2, typeName: "할인율순" },
    { id: 3, typeName: "저가순" },
    { id: 4, typeName: "좋아요순" },
  ];
  const searchTypeList: searchType[] = [
    { id: 0, typeName: "제목" },
    { id: 1, typeName: "판매자" },
  ];

  const handlePageChange = (page: number) => {
    setPageNumber(page);
    // console.log(page);
  };

  const fetchAuctionData = useCallback(() => {
    setIsLoading(true);
    const searchFilters = {
      discountCategory: selectedCategory !== "전체" ? selectedCategory : null,
      discountCondition: selectedOrderType.id,
      searchType: searchType.id,
      searchKeyword: searchKeyword,
    };
    // console.log(searchFilters);
    callApi("post", `/discount/list/${pageNumber}`, searchFilters)
      .then(response => {
        // console.log("데이터 성공", response.data);
        setData(response.data);
      })

      .catch(error => {
        toast.error(error.response.data.message)
        // console.log(searchFilters);
        // console.log("데이터 에러", error);
      })
      .finally(() => {
        setIsLoading(false); // 데이터 로딩 완료
      });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedCategory, selectedOrderType.id, pageNumber, searchKeyword]);

  useEffect(() => {
    fetchAuctionData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedCategory, selectedOrderType.id, pageNumber, searchKeyword]);

  const handleSearch = async (keyword: string) => {
    setSearchKeyword(keyword); // 상태 업데이트
  };

  return (
    <div className="px-48 ">
      <div className="flex flex-row space-x-10 items-baseline mt-10">
        <div className="font-black text-5xl">소상공인 할인제품</div>
      </div>

      <div className="flex flex-row h-[75px] items-center justify-between mb-7 mt-4">
        <div className="flex flex-row space-x-8">
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

        <div className="flex flex-row space-x-8">
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
      {/* <div className="grid grid-cols-5 gap-x-6 gap-y-10">
        {dummyData.map(item => (
          <div key={item.auctionPk} className="shadow-lg h-[450px] rounded-lg">
            <AuctionListCard item={item} nowTime={tmp} />
          </div>
        ))}
      </div> */}

      {isLoading ? (
        <div className="flex justify-center items-center">
          <ClipLoader color="#247eff" size={150} speedMultiplier={1} />
        </div>
      ) : data.items.length > 0 ? (
        <div className="grid grid-cols-5 gap-x-6 gap-y-10">
          {data.items.map(item => (
            <div key={item.discountPk} className="shadow-lg h-[600px] rounded-lg">
              <DiscountListCard item={item} nowTime={data.nowTime} />
            </div>
          ))}
        </div>
      ) : (
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

export default DiscountList;
