import axiosapi from "@/plugins/axios.js";
export function login(userId, password) {
  return axiosapi.post("/api/member/login", { userId, password });
}
export function register(data) {
  return axiosapi.post("/api/member/register", data);
}
export function getProfile() {
  return axiosapi.get("/api/member/profile");
}
export function updateProfile(data) {
  return axiosapi.put("/api/member/profile", data);
}
export function updatePassword(data) {
  return axiosapi.put("/api/member/password", data);
}
