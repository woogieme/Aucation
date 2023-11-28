import { AiOutlineHeart, AiFillHeart } from "react-icons/ai";

interface LikeBtnProps{
  isLiked:boolean,
}

const LikeBtn: React.FC<LikeBtnProps> = ({ isLiked }) => {
  return (
      <div className="bg-white" style={{ backgroundColor: "rgba(255, 255, 255, 0)" }}>
          {isLiked ? (
              <AiFillHeart size={30} color="red" style={{ cursor: "pointer" }} />
          ) : (
              <AiOutlineHeart size={30} color="red" style={{ cursor: "pointer" }} />
          )}
      </div>
  );
};

export default LikeBtn;
