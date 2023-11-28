const calculateRemainingTime = (startTime: number, endTime: number) => {
  const remainingMilliseconds = endTime - startTime;
  // console.log(endTime, startTime, remainingMilliseconds);

  const hours = Math.floor(remainingMilliseconds / 3600000);
  const minutes = Math.floor((remainingMilliseconds % 3600000) / 60000);
  const seconds = Math.floor((remainingMilliseconds % 60000) / 1000);

  const formattedTime = `${hours.toString().padStart(2, "0")}:${minutes
    .toString()
    .padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`;
  return formattedTime;
};

// 사용 가이드
// useEffect(() => {
//     const timer = setInterval(() => {
//       const startTime = Date.now(); // 종료 시간을 적절히 설정하세요
//       const endTime = new Date("2023-11-01T15:30:00"); // 현재 시간을 적절히 설정하세요

//       const remainingTime = calculateRemainingTime(startTime, endTime.getTime());
//       setRemainTime(remainingTime);
//     }, 1000);

//     return () => clearInterval(timer);
//   }, [remainTime]);

export default calculateRemainingTime;
