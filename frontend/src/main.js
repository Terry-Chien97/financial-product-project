import { createApp } from "vue";
import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import "bootstrap-icons/font/bootstrap-icons.css";
import "datatables.net-bs5/css/dataTables.bootstrap5.min.css";
import App from "./App.vue";
import router from "./router";
import Swal from "sweetalert2";

const toast = Swal.mixin({
  toast: true,
  position: "bottom-end",
  showConfirmButton: false,
  timer: 2500,
  timerProgressBar: true,
});

window.alertUI = {
  toast(icon, message) {
    return toast.fire({ icon, title: message });
  },
  apiError(error) {
    return toast.fire({
      icon: "error",
      title: error.response?.data?.message || "連線失敗，請稍後再試",
    });
  },
  async confirmDelete(name) {
    const result = await Swal.fire({
      icon: "warning",
      title: "確認刪除？",
      text: name,
      showCancelButton: true,
      confirmButtonText: "刪除",
      cancelButtonText: "取消",
      confirmButtonColor: "#dc3545",
    });
    return result.isConfirmed;
  },
};

const app = createApp(App);
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);
app.use(pinia);
app.use(router);
app.mount("#app");
