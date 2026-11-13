import axios from "axios";
import useUserStore from "@/stores/user.js";
const axiosapi = axios.create({
  baseURL: "http://localhost:8080",
});
const isPublicRequest = (url) =>
  url === "/api/member/login" || url === "/api/member/register";
axiosapi.interceptors.request.use(
  (config) => {
    if (!isPublicRequest(config.url)) {
      const token = useUserStore().token;
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
    return config;
  },
  (error) => Promise.reject(error),
);
axiosapi.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && !isPublicRequest(error.config?.url)) {
      useUserStore().clear();
      if (window.location.pathname !== "/401") {
        window.location.replace("/401");
      }
    }
    return Promise.reject(error);
  },
);
export default axiosapi;
