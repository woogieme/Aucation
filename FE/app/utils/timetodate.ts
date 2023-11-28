export const timeToDate = (hour: number, minute: number) => {
  const 마감시간 = `${hour}시${minute}분`; // 이 부분을 원하는 마감 시간으로 바꾸세요

  // 현재 날짜를 가져오기
  const 현재시간 = new Date();

  // 시간과 분 추출
  const 시간분 = 마감시간.match(/(\d+)[시:](\d+)분/);
  const tem시 = parseInt(시간분![1]);
  const tem분 = parseInt(시간분![2]);

  // 마감 시간 설정
  const 마감날짜 = new Date(
    현재시간.getFullYear(),
    현재시간.getMonth(),
    현재시간.getDate(),
    tem시,
    tem분,
    0,
    0
  );

  // 날짜 및 시간을 원하는 형식으로 문자열로 변환
  const 년 = 마감날짜.getFullYear();
  const 월 = String(마감날짜.getMonth() + 1).padStart(2, "0");
  const 일 = String(마감날짜.getDate()).padStart(2, "0");
  const 시 = String(마감날짜.getHours()).padStart(2, "0");
  const 분 = String(마감날짜.getMinutes()).padStart(2, "0");
  const 초 = String(마감날짜.getSeconds()).padStart(2, "0");

  const 형식화된시간 = `${년}-${월}-${일}T${시}:${분}:${초}.0000`;

  return 형식화된시간;
};
