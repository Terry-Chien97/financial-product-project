<script setup>
import { useRouter } from "vue-router";
import useUserStore from "@/stores/user.js";
const router = useRouter();
const userStore = useUserStore();
const goToLogin = () => {
  userStore.clear();
  router.replace("/login");
};
</script>
<template>
  <section
    class="error-page d-flex align-items-center justify-content-center px-3 py-5"
  >
    <div class="text-center mx-auto">
      <div
        class="error-icon rounded-circle d-inline-flex align-items-center justify-content-center mb-3"
      >
        <i class="bi bi-shield-lock" aria-hidden="true"></i>
      </div>
      <div class="error-code fw-bold mb-2">401</div>
      <h1 class="h3 fw-bold mb-3">請先登入</h1>
      <p class="text-secondary lh-lg mb-4">
        你尚未登入，或登入狀態已經失效。<br />
        重新登入後，就能繼續使用系統。
      </p>
      <button
        type="button"
        class="btn btn-success rounded-pill px-4 py-2"
        @click="goToLogin"
      >
        <i class="bi bi-box-arrow-in-right me-2" aria-hidden="true"></i>
        重新登入
      </button>
    </div>
  </section>
</template>
<style scoped>
.error-page {
  min-height: calc(100dvh - 72px);
  background: radial-gradient(circle at 50% 38%, #eaf7f3, #fff 65%);
}
.error-icon {
  width: 92px;
  height: 92px;
  background: #ddf2eb;
  color: #008577;
  font-size: 2.65rem;
  box-shadow: 0 10px 30px #00857714;
}
.error-code {
  color: #008577;
  font-size: clamp(5rem, 18vw, 8rem);
  line-height: 1;
  letter-spacing: -0.05em;
}
.btn-success {
  background-color: #008577;
  border-color: #008577;
}
</style>
