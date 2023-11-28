import AuctionMainPage from "./AuctionMainPage";
import AuctionChat from "./AuctionChat";

const AuctionMain = () => {
  return (
    <>
      <div
        className="
        bg-white
        rounded-2xl
        h-[90%]
        w-[90%]
      "
      >
        <div
          className="
          h-full
          w-full
          flex
        "
        >
          <AuctionMainPage />
          <AuctionChat />
        </div>
      </div>
    </>
  );
};

export default AuctionMain;
