import axiosapi from "@/plugins/axios.js";
export const getLikes = () => {
  return axiosapi.get("/api/likes");
};
export const addLike = (data) => {
  return axiosapi.post("/api/likes", data);
};
export const updateLike = (sn, data) => {
  return axiosapi.put(`/api/likes/${sn}`, data);
};
export const deleteLike = (sn) => {
  return axiosapi.delete(`/api/likes/${sn}`);
};
