<script setup>
import { computed, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import useUserStore from "@/stores/user.js";
import { getProfile } from "@/services/memberService.js";
const router = useRouter();
const userStore = useUserStore();
const isLoggedIn = computed(() => Boolean(userStore.token));
const loadProfile = async () => {
  const token = userStore.token;
  if (!token) {
    userStore.setProfile(null);
    return;
  }
  try {
    const response = await getProfile();
    if (userStore.token === token) userStore.setProfile(response.data);
  } catch (error) {
    if (userStore.token === token) userStore.setProfile(null);
    console.error("取得會員資料失敗", error);
  }
};
const logout = () => {
  userStore.clear();
  router.push("/login");
};
onMounted(loadProfile);
watch(() => userStore.token, loadProfile);
</script>
<template>
  <nav
    class="navbar navbar-expand-lg navbar-dark shadow-sm"
    style="background-color: #008577"
  >
    <div class="container-fluid px-4 px-xl-5">
      <RouterLink
        class="navbar-brand fw-bold"
        :to="isLoggedIn ? '/products' : '/login'"
      >
        <i class="bi bi-bank2 me-2"></i>
        金融商品喜好系統
      </RouterLink>
      <button
        class="navbar-toggler"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#mainNavbar"
        aria-controls="mainNavbar"
        aria-expanded="false"
        aria-label="切換導覽選單"
      >
        <span class="navbar-toggler-icon"></span>
      </button>
      <div id="mainNavbar" class="collapse navbar-collapse">
        <div v-if="isLoggedIn" class="navbar-nav mx-lg-auto">
          <RouterLink
            class="nav-link mx-xl-2"
            active-class="active"
            to="/products"
          >
            <i class="bi bi-box-seam me-2"></i>產品清單
          </RouterLink>
          <RouterLink
            class="nav-link mx-xl-2"
            active-class="active"
            to="/likes"
          >
            <i class="bi bi-heart me-2"></i>喜好清單
          </RouterLink>
          <RouterLink
            class="nav-link mx-xl-2"
            active-class="active"
            to="/profile"
          >
            <i class="bi bi-person me-2"></i>個人資料
          </RouterLink>
          <RouterLink
            class="nav-link mx-xl-2"
            active-class="active"
            to="/change-password"
          >
            <i class="bi bi-shield-lock me-2"></i>修改密碼
          </RouterLink>
        </div>
        <div
          v-if="isLoggedIn"
          class="d-flex flex-column flex-lg-row align-items-lg-center gap-3 py-2 py-lg-0"
        >
          <div v-if="userStore.profile" class="d-flex align-items-center gap-2">
            <i class="bi bi-person-circle fs-5 text-white"></i>
            <span class="text-white fw-semibold">{{
              userStore.profile.userName
            }}</span>
            <span class="text-white-50 small">{{
              userStore.profile.email
            }}</span>
          </div>
          <div class="vr opacity-25 d-none d-lg-block"></div>
          <button
            type="button"
            class="nav-link text-white d-flex align-items-center gap-2 p-0 border-0 bg-transparent"
            @click="logout"
          >
            <i class="bi bi-box-arrow-right"></i>登出
          </button>
        </div>
        <div v-else class="navbar-nav ms-lg-auto gap-lg-3">
          <RouterLink class="nav-link" active-class="active" to="/register">
            <i class="bi bi-person-plus me-2"></i>註冊
          </RouterLink>
          <RouterLink class="nav-link" active-class="active" to="/login">
            <i class="bi bi-box-arrow-in-right me-2"></i>登入
          </RouterLink>
        </div>
      </div>
    </div>
  </nav>
</template>
