import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./components/**/*.{js,ts,jsx,tsx,mdx}",
    "./app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      backgroundImage: {
        "gradient-radial": "radial-gradient(var(--tw-gradient-stops))",
        "gradient-conic": "conic-gradient(from 180deg at 50% 50%, var(--tw-gradient-stops))",
        "main-floor": "url('./images/MainFloorImg.png')",
        "custom-btn-gradient": "linear-gradient(170deg, #6BA8FF, #247EFF)",
        "custom-btn-gradient-hover": "linear-gradient(170deg, #3467F9, #3467F9)",
        "custom-btn-gradient-red": "linear-gradient(170deg, #f43, #f15)",
        "custom-btn-gradient-red-hover": "linear-gradient(170deg, #8b0000, #8b0000)",
        "highlight-effect": "linear-gradient(180deg, transparent, transparent, var(--custom-bg-lightblue))",
        "blue-gradient": "linear-gradient(170deg, #0033CC, #33599CC)",
        "lightblue-highlight" : "linear-gradient(to top, #D4E0FF 50%, transparent 50%)",
      },
      backgroundColor: {
        customBasic: "#F8F9FB", // 커스텀 기본 색
        customGray: "#646C76",
        customBgBlue: "#247eff",
        customBgLightBlue: "#D4E0FF", // 중간 포인트 색
        mapCustom: "#fff",
      },
      textColor: {
        customBasic: "#F8F9FB", // 커스텀 기본 색
        customGray: "#9EA6B2",
        customBlue: "#247eff",
        myPageGray: "#7A7A7A",
        customLightTextColor: "#646C76", // 연한 글자 색
      },
      borderColor: {
        customGray: "#9EA6B2",
        customBlue: "#247eff",
        customLightBlue : "#D4E0FF",
      },
      ringColor:{
        customBlue : "#247eff",
        customLightBlue : "#D4E0FF",
      },  
      fontSize: {
        "27px": "27px",
      },
    },
  },
  plugins: [],
};
export default config;
