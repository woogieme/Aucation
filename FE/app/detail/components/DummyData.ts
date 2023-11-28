interface DummyData {
    cardImgUrl: string
    likeCount: number
    auctionStartTime: Date
    title: string
    highestPrice: number
    isLiked: boolean
    nickname: string
    startPrice: number
    isIndividual: boolean
  }
  
const dummyData: DummyData[] = [
    {
      cardImgUrl: "https://cdn.thecolumnist.kr/news/photo/202302/1885_4197_221.jpg",
      likeCount: 100,
      auctionStartTime: new Date("2023-11-06T14:49:30"),
    
      title: "상품 제목 1",
      highestPrice: 5000000,
      isLiked: true,
      nickname: "사용자1",
      startPrice: 20000,
      isIndividual: true,
    },
    {
      cardImgUrl: "https://cdn.thecolumnist.kr/news/photo/202302/1885_4197_221.jpg",
      likeCount: 200,
      auctionStartTime: new Date("2023-11-05T21:26:30"),
   
      title: "상품 제목 2",
      highestPrice: 60000,
      isLiked: false,
      nickname: "사용자2",
      startPrice: 30000,
      isIndividual: false,
    },
    {
      cardImgUrl: "https://cdn.thecolumnist.kr/news/photo/202302/1885_4197_221.jpg",
      likeCount: 300,
      auctionStartTime: new Date("2023-10-31T15:32:30"),

      title: "상품 제목 3",
      highestPrice: 70000,
      isLiked: true,
      nickname: "사용자3",
      startPrice: 40000,
      isIndividual: true,
    },
    {
      cardImgUrl: "https://cdn.thecolumnist.kr/news/photo/202302/1885_4197_221.jpg",
      likeCount: 400,
      auctionStartTime: new Date("2023-10-31T14:32:30"),
    
      title: "상품 제목 4",
      highestPrice: 80000,
      isLiked: false,
      nickname: "사용자4",
      startPrice: 50000,
      isIndividual: false,
    },
    {
      cardImgUrl: "https://cdn.thecolumnist.kr/news/photo/202302/1885_4197_221.jpg",
      likeCount: 500,
      auctionStartTime: new Date("2023-10-31T14:32:30"),
   
      title: "상품 제목 5",
      highestPrice: 90000,
      isLiked: true,
      nickname: "사용자5",
      startPrice: 60000,
      isIndividual: true,
    },
    {
      cardImgUrl: "https://cdn.thecolumnist.kr/news/photo/202302/1885_4197_221.jpg",
      likeCount: 600,
      auctionStartTime: new Date("2023-11-02T14:32:30"),

      title: "상품 제목 6",
      highestPrice: 100000,
      isLiked: false,
      nickname: "사용자6",
      startPrice: 70000,
      isIndividual: false,
    },
    {
      cardImgUrl: "https://cdn.thecolumnist.kr/news/photo/202302/1885_4197_221.jpg",
      likeCount: 700,
      auctionStartTime: new Date("2023-11-02T14:32:30"),
    
      title: "상품 제목 7",
      highestPrice: 110000,
      isLiked: true,
      nickname: "사용자7",
      startPrice: 80000,
      isIndividual: true,
    },
    {
      cardImgUrl: "https://cdn.thecolumnist.kr/news/photo/202302/1885_4197_221.jpg",
      likeCount: 800,
      auctionStartTime: new Date("2023-11-02T14:32:30"),
    
      title: "상품 제목 8",
      highestPrice: 120000,
      isLiked: false,
      nickname: "사용자8",
      startPrice: 90000,
      isIndividual: false,
    },
    {
      cardImgUrl: "https://cdn.thecolumnist.kr/news/photo/202302/1885_4197_221.jpg",
      likeCount: 900,
      auctionStartTime: new Date("2023-11-02T14:32:30"),

      title: "상품 제목 9",
      highestPrice: 130000,
      isLiked: true,
      nickname: "사용자9",
      startPrice: 100000,
      isIndividual: true,
    },
    {
      cardImgUrl: "https://cdn.thecolumnist.kr/news/photo/202302/1885_4197_221.jpg",
      likeCount: 1000,
      auctionStartTime: new Date("2023-11-02T14:32:30"),

      title: "상품 제목 10",
      highestPrice: 140000,
      isLiked: false,
      nickname: "사용자10",
      startPrice: 110000,
      isIndividual: false,
    },
  ]
  
  export default dummyData;
  