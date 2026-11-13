import axiosapi from "@/plugins/axios.js";
export function getProducts() {
  return axiosapi.get("/api/products");
}
export function addProduct(data) {
  return axiosapi.post("/api/products", data);
}
export function updateProduct(no, data) {
  return axiosapi.put(`/api/products/${no}`, data);
}
export function deleteProduct(no) {
  return axiosapi.delete(`/api/products/${no}`);
}
