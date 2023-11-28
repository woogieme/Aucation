const emailRegexp = new RegExp("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
const nameRegexp = new RegExp("^[a-zA-Z0-9가-힣]{1,15}$");

export { emailRegexp, nameRegexp };
