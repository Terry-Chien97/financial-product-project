<script setup>
import { computed } from "vue";
import useUserStore from "@/stores/user.js";
const userStore = useUserStore();
const homePath = computed(() => (userStore.token ? "/products" : "/login"));
</script>
<template>
  <section
    class="error-page d-flex align-items-center justify-content-center px-3 py-5"
  >
    <div class="text-center mx-auto">
      <div
        class="error-icon rounded-circle d-inline-flex align-items-center justify-content-center mb-3"
      >
        <i class="bi bi-compass" aria-hidden="true"></i>
      </div>
      <div class="error-code fw-bold mb-2">404</div>
      <h1 class="h3 fw-bold mb-3">找不到這個頁面</h1>
      <p class="text-secondary lh-lg mb-4">
        網址可能有誤，或頁面已經移動。<br />
        別擔心，可以從首頁重新出發。
      </p>
      <RouterLink :to="homePath" class="btn btn-success rounded-pill px-4 py-2">
        <i class="bi bi-house-door me-2" aria-hidden="true"></i>
        {{ userStore.token ? "返回產品清單" : "前往登入" }}
      </RouterLink>
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
