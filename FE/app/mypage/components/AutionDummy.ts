interface ItemType {
    memberId: string;
	auctionBuy: ResDataType[];
  }
  
  interface ResDataType {
	auctionPk: number;
	auctionStatus: string;
	auctionTitle: string;
	auctionObjectName: String;
	auctionType: String;
	auctionStartPrice: number;
	auctionEndPrice: number;
	auctionDetail: String,
    acutionStartTime: string;
    auctionEndTime: string;
  }
  

const AuctionDummy:ItemType = {
    memberId: "asd",
    auctionBuy: [
        {
            auctionPk: 1,
            auctionStatus: "경매",
            auctionTitle: "제목",
            auctionObjectName: "String",
            auctionType: "String",
            auctionStartPrice: 0, // 이 값을 실제 숫자로 대체해야 합니다.
            auctionEndPrice: 0, // 이 값을 실제 숫자로 대체해야 합니다.
            auctionDetail: "String",
            acutionStartTime: "2023-11-1",
            auctionEndTime: "2023-11-3"
        },
        {
            auctionPk: 1,
            auctionStatus: "경매",
            auctionTitle: "제목",
            auctionObjectName: "String",
            auctionType: "String",
            auctionStartPrice: 0, // 이 값을 실제 숫자로 대체해야 합니다.
            auctionEndPrice: 0, // 이 값을 실제 숫자로 대체해야 합니다.
            auctionDetail: "String",
            acutionStartTime: "2023-11-1",
            auctionEndTime: "2023-11-3"
        },
        {
            auctionPk: 1,
            auctionStatus: "경매",
            auctionTitle: "제목",
            auctionObjectName: "String",
            auctionType: "String",
            auctionStartPrice: 0, // 이 값을 실제 숫자로 대체해야 합니다.
            auctionEndPrice: 0, // 이 값을 실제 숫자로 대체해야 합니다.
            auctionDetail: "String",
            acutionStartTime: "2023-11-1",
            auctionEndTime: "2023-11-3"
        },
        {
            auctionPk: 1,
            auctionStatus: "경매",
            auctionTitle: "제목",
            auctionObjectName: "String",
            auctionType: "String",
            auctionStartPrice: 0, // 이 값을 실제 숫자로 대체해야 합니다.
            auctionEndPrice: 0, // 이 값을 실제 숫자로 대체해야 합니다.
            auctionDetail: "String",
            acutionStartTime: "2023-11-1",
            auctionEndTime: "2023-11-3"
        }
    ]
}



  export default AuctionDummy