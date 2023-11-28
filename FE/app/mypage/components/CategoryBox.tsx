import clsx from "clsx";

interface CategoryBoxProps {
  name: string;
  css?: string | undefined;
  dynamicCss?: string;
  categoryHandler: (category:string) => void;
  selectedCategory?: string;
}

const CategoryBox: React.FC<CategoryBoxProps> = ({ name, css, categoryHandler, selectedCategory,dynamicCss}) => {
  let firstCategoryCss = clsx(css, {
    "border-customBlue  text-2xl text-customBlue": selectedCategory === name,
    "text-customGray": selectedCategory !== name,
  })
  let secondCategoryCss = clsx(css, {
    "border-customBlue  text-2xl text-customBlue": selectedCategory === name,
    "text-customGray border-customGray": selectedCategory !== name,
  })


  return (
    <div className={clsx(dynamicCss == "first" ? firstCategoryCss : dynamicCss == "second" ? secondCategoryCss : css)} onClick={()=>categoryHandler(name)}>
      {name}
    </div>
  );
};
export default CategoryBox