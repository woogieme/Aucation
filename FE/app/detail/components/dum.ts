interface DummyData {
    cardImgUrl: string
    likeCount: number
    time: Date
    state: string
    title: string
    highestPrice: number
    isLiked: boolean
    nickname: string
    startPrice: number
    isIndividual: boolean
  }
  
const dum: DummyData[] = [
    {
      cardImgUrl: "https://cdn.thecolumnist.kr/news/photo/202302/1885_4197_221.jpg",
      likeCount: 100,
      time: new Date("2023-11-02T11:10:30"),
      state: '0',
      title: "상품 제목 1",
      highestPrice: 50000,
      isLiked: true,
      nickname: "사용자1",
      startPrice: 20000,
      isIndividual: true,
    },
    {
      cardImgUrl: "https://cdn.thecolumnist.kr/news/photo/202302/1885_4197_221.jpg",
      likeCount: 200,
      time: new Date("2023-11-02T11:10:30"),
      state: '1',
      title: "상품 제목 2",
      highestPrice: 60000,
      isLiked: false,
      nickname: "사용자2",
      startPrice: 30000,
      isIndividual: false,
    },
]
export default dum;
