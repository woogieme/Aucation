import AuctionMain from "../components/AuctionMain";

const AuctionDetail = () => {
  return (
    <div
      className="
      w-full
      h-full
      flex
      justify-center
      items-center
      "
      style={{ backgroundColor: "var(--c-sky)" }}
    >
      <AuctionMain />
    </div>
  );
};
export default AuctionDetail;
