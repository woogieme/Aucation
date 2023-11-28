export type AuctionData = {
  nowTime: Date | null; // assuming `date` should be a Date object
  currentPage: number;
  totalPage: number;
  ingItems: AuctionItem[];
};

export type AuctionItem = {
  likeCnt: number; // 좋아요 수
  isLike: boolean; // 사용자 좋아요 여부
  auctionPk: number;
  auctionUUID: number; // 경매입장을 위한 UUID
  auctionType: string; // 카테고리 종류
  auctionTitle: string; // 상품이름
  auctionStartPrice: number; // 경매 시작가
  auctionTopBidPrice: number; // 경매 입찰가
  auctionCurCnt: number; // 현재 사용자
  auctionOwnerIsShop: boolean; // 소상공인여부
  auctionOwnerNickname: String; // 판매자 닉네임
  auctionEndTime: Date; // 경매 시작 시간
  auctionImg: string; // 경매 사진 url
};

export type PreAuctionData = {
  nowTime: Date | null; // assuming `date` should be a Date object
  currentPage: number;
  totalPage: number;
  preItems: PreAuctionItem[];
};

export type PreAuctionItem = {
  likeCnt: number; // 좋아요 수
  isLike: boolean; // 사용자 좋아요 여부
  auctionPk: number;
  auctionTitle: string; // 상품이름
  auctionType: string; //
  auctionStartPrice: number; // 경매 시작가
  auctionOwnerIsShop: boolean; // 소상공인여부
  auctionOwnerNickname: string; // 판매자 닉네임
  auctionStartTime: Date; // 경매 시작 시간
  auctionImg: string;
};

export type ReverseAuctionData = {
  nowTime: Date | null; // assuming `date` should be a Date object
  currentPage: number;
  totalPage: number;
  reItems: ReverseAuctionItem[];
};

export type ReverseAuctionItem = {
  likeCnt: number; // 좋아요 수
  isLike: boolean; // 사용자 좋아요 여부
  reAuctionPk: number;
  reAuctionTitle: string; // 상품이름
  reAuctionType: string;
  reAuctionStartPrice: number; // 경매 시작가
  reAuctionLowBidPrice: number; // 경매 최저가
  reAuctionBidCnt: number; // 총 입찰한 사람 수
  reAuctionOwnerIsShop: boolean; // 소상공인여부
  reAuctionOwnerNickname: string; // 판매자 닉네임
  reAuctionEndTime: Date; // 역경매 종료 시간
  reAuctionImg: string; // 경매 사진 url
};

export type DiscountData = {
  nowTime: Date | null; // assuming `date` should be a Date object
  currentPage: number;
  totalPage: number;
  items: DiscountItem[];
};

export type DiscountItem = {
  likeCnt: number; // 좋아요 수
  isLike: boolean; // 사용자 좋아요 여부
  discountPk: number;
  discountTitle: string; // 상품이름
  originalPrice: number; // 원래 정가
  discountedPrice: number; // 할인가
  discountRate: number; // 할인률
  discountOwnerNickname: string; // 소상공인여부
  discountUUID: string; //
  discountType: string; //
  discountEnd: Date; // 할인 마감시간
  discountImg: string; // 할인 사진 url
};

export type HomePageData = {
  nowTime: Date | null;
  hotAuctions: AuctionItem[];
  discounts: DiscountItem[];
  recentAuctions: ReverseAuctionItem[];
};
