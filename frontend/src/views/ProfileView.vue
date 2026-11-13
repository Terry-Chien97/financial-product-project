<script setup>
import { onMounted, reactive } from "vue";
import { getProfile, updateProfile } from "@/services/memberService.js";
import useUserStore from "@/stores/user.js";
const userStore = useUserStore();
const form = reactive({
  userId: "",
  userName: "",
  email: "",
  account: "",
});
const loadProfile = async () => {
  try {
    const response = await getProfile();
    form.userId = response.data.userId;
    form.userName = response.data.userName;
    form.email = response.data.email;
    form.account = response.data.account;
    userStore.setProfile(response.data);
  } catch (error) {
    window.alertUI.apiError(error);
  }
};
const handleAccountInput = (event) => {
  form.account = event.target.value.replace(/\D/g, "");
  event.target.value = form.account;
};
const validateForm = () => {
  if (!form.userName.trim() || form.userName.trim().length > 10)
    return "姓名須為 1～10 字";
  if (form.email.length > 255 || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email))
    return "電子郵件格式錯誤";
  if (!/^\d{10,16}$/.test(form.account)) return "扣款帳號須為 10～16 碼數字";
  return null;
};
const submitProfile = async () => {
  const message = validateForm();
  if (message) {
    window.alertUI.toast("error", message);
    return;
  }
  try {
    const response = await updateProfile({
      userName: form.userName.trim(),
      email: form.email.trim(),
      account: form.account.trim(),
    });
    userStore.setProfile({
      userName: form.userName.trim(),
      email: form.email.trim(),
    });
    window.alertUI.toast("success", response.data.message);
  } catch (error) {
    window.alertUI.apiError(error);
  }
};
onMounted(() => {
  loadProfile();
});
</script>
<template>
  <div
    class="container flex-grow-1 d-flex align-items-center justify-content-center py-3"
  >
    <div class="row justify-content-center w-100">
      <div class="col-12 col-md-8 col-lg-5">
        <div class="card shadow-sm">
          <div class="card-body p-4">
            <h4 class="text-center fw-bold mb-4">個人資料</h4>
            <form @submit.prevent="submitProfile">
              <div class="mb-3">
                <label class="form-label"> 身分證字號 </label>
                <input
                  v-model="form.userId"
                  type="text"
                  class="form-control"
                  disabled
                />
              </div>
              <div class="mb-3">
                <label class="form-label">
                  姓名
                  <span class="text-danger">*</span>
                </label>
                <input
                  v-model.trim="form.userName"
                  type="text"
                  class="form-control"
                  placeholder="請輸入姓名"
                  required
                />
              </div>
              <div class="mb-3">
                <label class="form-label">
                  電子郵件
                  <span class="text-danger">*</span>
                </label>
                <input
                  v-model.trim="form.email"
                  type="text"
                  inputmode="email"
                  class="form-control"
                  placeholder="請輸入電子郵件"
                  required
                />
              </div>
              <div class="mb-4">
                <label class="form-label">
                  預設扣款帳號
                  <span class="text-danger">*</span>
                </label>
                <input
                  v-model="form.account"
                  type="text"
                  class="form-control"
                  inputmode="numeric"
                  placeholder="請輸入 10～16 碼扣款帳號"
                  required
                  @input="handleAccountInput"
                />
              </div>
              <button type="submit" class="btn btn-success w-100">儲存</button>
              <RouterLink
                to="/change-password"
                class="btn btn-outline-success w-100 mt-2"
                >修改密碼</RouterLink
              >
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
