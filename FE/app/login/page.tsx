import Image from "next/image";
import AuthForm from "./components/AuthForm";
import logo2 from "@/app/images/aucation_logo.png";

export default function Home() {
  return (
    <div
      className="
    flex
    min-h-full
    flex-col
    justify-center
    items-center
    py-12
    sm:px-6
    lg:px-8
   
    "
    >
      <div className="w-[35%]">
        <Image alt="Logo" className="mx-auto w-auto" src={logo2} />
        
        <AuthForm />
      </div>
    </div>
  );
}
