const NoResult = () => {
    return(
        <div className="border-2 border-customGray rounded-full flex flex-col justify-center items-center py-10 text-customLightTextColor text-xl">
            <div className="flex justify-center place-items-center rounded-full w-[100px] h-[100px] border-2 border-customGray text-3xl mb-3 text-red-400 ">{"!"}</div>
            <div className="text-xl">검색 결과가 없습니다.</div>
        </div>
    )
}
export default NoResult