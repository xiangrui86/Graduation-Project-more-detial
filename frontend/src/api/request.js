import axios from "axios";

// axios 实例：统一基础地址与超时配置
const request = axios.create({
  baseURL: "/api",
  timeout: 10000,
});

// 请求拦截：自动附带本地 token
request.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

request.interceptors.response.use(
  (res) => res.data,
  (err) => {
    const status = err.response && err.response.status;
    if (status === 401) {
      // 登录态失效时清理本地会话并跳转登录页
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      try {
        // eslint-disable-next-line no-alert
        alert("登录已失效，请重新登录");
      } catch (e) {}
      window.location.href = "/#/login";
    } else if (status === 403) {
      try {
        // eslint-disable-next-line no-alert
        alert("当前账号无权限访问此功能，如有疑问请重新登录或联系管理员");
      } catch (e) {}
    }
    return Promise.reject(err);
  },
);

export default request;
