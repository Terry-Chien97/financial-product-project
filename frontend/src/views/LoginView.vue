<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { login } from "@/services/memberService.js";
import useUserStore from "@/stores/user.js";
const router = useRouter();
const userStore = useUserStore();
const showPassword = ref(false);
const form = reactive({
  userId: "",
  password: "",
});
const submitLogin = async () => {
  const userId = form.userId.trim().toUpperCase();
  if (!/^[A-Z][12]\d{8}$/.test(userId)) {
    window.alertUI.toast("error", "身分證字號格式錯誤");
    return;
  }
  if (form.password.length < 6 || form.password.length > 15) {
    window.alertUI.toast("error", "密碼須為 6～15 碼");
    return;
  }
  try {
    const response = await login(userId, form.password);
    userStore.token = response.data.token;
    await window.alertUI.toast("success", response.data.message);
    router.push("/products");
  } catch (error) {
    window.alertUI.apiError(error);
  }
};
</script>
<template>
  <div
    class="container flex-grow-1 d-flex align-items-center justify-content-center py-3"
  >
    <div class="row justify-content-center w-100">
      <div class="col-12 col-md-8 col-lg-5">
        <div class="card shadow-sm">
          <div class="card-body p-4">
            <h4 class="text-center fw-bold mb-4">使用者登入</h4>
            <form @submit.prevent="submitLogin">
              <div class="mb-3">
                <label class="form-label">
                  身分證字號
                  <span class="text-danger">*</span>
                </label>
                <input
                  v-model.trim="form.userId"
                  type="text"
                  class="form-control"
                  placeholder="請輸入身分證字號"
                  required
                />
              </div>
              <div class="mb-4">
                <label class="form-label">
                  密碼
                  <span class="text-danger">*</span>
                </label>
                <div class="position-relative">
                  <input
                    v-model="form.password"
                    :type="showPassword ? 'text' : 'password'"
                    class="form-control pe-5"
                    placeholder="請輸入密碼"
                    required
                  />
                  <button
                    type="button"
                    class="btn position-absolute top-50 end-0 translate-middle-y border-0 bg-transparent text-secondary"
                    @click="showPassword = !showPassword"
                  >
                    <i
                      :class="showPassword ? 'bi bi-eye' : 'bi bi-eye-slash'"
                    ></i>
                  </button>
                </div>
              </div>
              <button type="submit" class="btn btn-success w-100">登入</button>
              <div class="text-center mt-3">
                <RouterLink to="/register" class="text-decoration-none">
                  尚未註冊？建立帳號
                </RouterLink>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
