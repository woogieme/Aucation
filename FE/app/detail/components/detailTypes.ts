export type AuctionDetailData = {
  auctionAskPrice: number;
  auctionBidCnt: number;
  auctionDetailItems: AuctionDetailItem[]; // 구체적인 타입 정보가 필요합니다
  auctionEndTime: Date;
  auctionInfo: string;
  auctionMeetingLat: number;
  auctionMeetingLng: number;
  auctionOwnerMemberRole: string; // "SHOP"이외의 다른 가능한 값이 있다면 추가합니다
  auctionOwnerNickname: string;
  auctionOwnerPhoto: string;
  auctionOwnerPk: number;
  auctionPhoto: string[]; // URL의 문자열 배열로 가정합니다
  auctionPk: number;
  auctionStartPrice: number;
  auctionStartTime: Date;
  auctionStatus: string; // "BID" 이외의 다른 가능한 값이 있다면 추가합니다
  auctionTitle: string;
  auctionTopPrice?: number;
  auctionType: string;
  auctionUuid: string;
  isAction: number; // 불리언 또는 숫자가 될 수 있습니다 (API 설계에 따라 다름)
  isLike: boolean;
  likeCnt: number;
  mycity: string;
  nowTime: Date;
  street: string;
  zipcode: string;
};

export type AuctionDetailItem = {
  auctionPhoto: string;
  auctionPk: number;
  auctionPrice: number;
  auctionStatus: string; // "REVERSE_BID" 이외의 다른 가능한 값이 있다면 추가합니다
  auctionTitle: string;
  isAuc: boolean;
  isLike: boolean;
  mycity: string;
  street: string;
  zipcode: string;
};
