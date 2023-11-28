import { atom } from "recoil";
import { recoilPersist } from "recoil-persist";

const { persistAtom } = recoilPersist();

// export interface AuthState {
//   isLoggedIn: boolean;
//   role: string;
// }

export const authState = atom({
  key: "authState", // 고유한 키
  default: {
    isLoggedIn: false,
    role: "",
  }, // 기본값
  effects_UNSTABLE: [persistAtom],
});
