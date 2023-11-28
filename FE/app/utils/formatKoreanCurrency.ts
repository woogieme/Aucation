const formatKoreanCurrency = (price: number): string => {
  let formattedPrice: number = price;
  let remain: number = 0;

  if (formattedPrice >= 10000) {
    remain = formattedPrice % 10000; // 만 단위로 나눈 나머지를 저장
    formattedPrice = Math.floor(formattedPrice / 10000); // 만 단위로 나눈 값을 다시 저장
    let remainString: string = remain ? `${remain}` : "";
    let formattedString: string = remain
      ? `${formattedPrice}만 ${remainString}`
      : `${formattedPrice}만`;

    return `${formattedString}원`.trim();
  } else {
    return `${formattedPrice}원`.trim();
  }
};

export default formatKoreanCurrency;
// const formatKoreanCurrency = (price: number): string => {
//   var inputNumber: any = price < 0 ? false : price;
//   var unitWords = ["", "만", "억", "조", "경"];
//   var splitUnit = 10000;
//   var splitCount = unitWords.length;
//   var resultArray = [];
//   var resultString = "";

//   for (var i = 0; i < splitCount; i++) {
//     var unitResult = (inputNumber % Math.pow(splitUnit, i + 1)) / Math.pow(splitUnit, i);
//     unitResult = Math.floor(unitResult);
//     if (unitResult > 0) {
//       resultArray[i] = unitResult;
//     }
//   }

//   for (var i = 0; i < resultArray.length; i++) {
//     if (!resultArray[i]) continue;
//     resultString = String(resultArray[i]) + unitWords[i] + resultString;
//   }

//   return resultString + "원";
// };

// export default formatKoreanCurrency;
