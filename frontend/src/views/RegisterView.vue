<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { register } from "@/services/memberService.js";
const router = useRouter();
const saving = ref(false);
const form = reactive({
  userId: "",
  userName: "",
  email: "",
  account: "",
  password: "",
  confirmPassword: "",
});
const showPassword = reactive({
  password: false,
  confirmPassword: false,
});
const passwordFields = [
  { key: "password", label: "密碼", placeholder: "請輸入 6～15 碼密碼" },
  {
    key: "confirmPassword",
    label: "確認密碼",
    placeholder: "請再次輸入 6～15 碼密碼",
  },
];
const validateId = (value) => {
  const id = value.trim().toUpperCase();
  if (!/^[A-Z][12]\d{8}$/.test(id)) return false;
  const letterCode = {
    A: 10,
    B: 11,
    C: 12,
    D: 13,
    E: 14,
    F: 15,
    G: 16,
    H: 17,
    I: 34,
    J: 18,
    K: 19,
    L: 20,
    M: 21,
    N: 22,
    O: 35,
    P: 23,
    Q: 24,
    R: 25,
    S: 26,
    T: 27,
    U: 28,
    V: 29,
    W: 32,
    X: 30,
    Y: 31,
    Z: 33,
  };
  const code = letterCode[id[0]];
  let sum = Math.floor(code / 10) + (code % 10) * 9;
  for (let i = 1; i <= 8; i++) {
    sum += Number(id[i]) * (9 - i);
  }
  return (sum + Number(id[9])) % 10 === 0;
};
const handleAccountInput = (event) => {
  form.account = event.target.value.replace(/\D/g, "");
  event.target.value = form.account;
};
const validateForm = () => {
  if (!validateId(form.userId)) return "身分證字號格式錯誤";
  if (!form.userName.trim() || form.userName.trim().length > 10)
    return "姓名須為 1～10 字";
  if (form.email.length > 255 || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email))
    return "電子郵件格式錯誤";
  if (!/^\d{10,16}$/.test(form.account)) return "扣款帳號須為 10～16 碼數字";
  if (form.password.length < 6 || form.password.length > 15)
    return "密碼須為 6～15 碼";
  if (form.password !== form.confirmPassword) return "兩次輸入的密碼不一致";
  return null;
};
const submitRegister = async () => {
  if (saving.value) return;
  const message = validateForm();
  if (message) {
    window.alertUI.toast("error", message);
    return;
  }
  saving.value = true;
  try {
    const response = await register({
      userId: form.userId.trim().toUpperCase(),
      userName: form.userName.trim(),
      email: form.email.trim(),
      account: form.account,
      password: form.password,
    });
    await window.alertUI.toast("success", response.data.message);
    await router.push("/login");
  } catch (error) {
    window.alertUI.apiError(error);
  } finally {
    saving.value = false;
  }
};
</script>
<template>
  <div class="container py-4">
    <div class="row justify-content-center">
      <div class="col-12 col-md-8 col-lg-5">
        <div class="card shadow-sm">
          <div class="card-body p-4">
            <h4 class="text-center fw-bold mb-4">使用者註冊</h4>
            <form @submit.prevent="submitRegister">
              <div class="mb-3">
                <label for="userId" class="form-label"
                  >身分證字號 <span class="text-danger">*</span></label
                >
                <input
                  id="userId"
                  v-model.trim="form.userId"
                  type="text"
                  class="form-control"
                  placeholder="請輸入身分證字號"
                  required
                />
              </div>
              <div class="mb-3">
                <label for="userName" class="form-label"
                  >姓名 <span class="text-danger">*</span></label
                >
                <input
                  id="userName"
                  v-model.trim="form.userName"
                  type="text"
                  class="form-control"
                  placeholder="請輸入姓名"
                  required
                />
              </div>
              <div class="mb-3">
                <label for="email" class="form-label"
                  >電子郵件 <span class="text-danger">*</span></label
                >
                <input
                  id="email"
                  v-model.trim="form.email"
                  type="text"
                  inputmode="email"
                  autocomplete="email"
                  class="form-control"
                  placeholder="請輸入電子郵件"
                  required
                />
              </div>
              <div class="mb-3">
                <label for="account" class="form-label"
                  >預設扣款帳號 <span class="text-danger">*</span></label
                >
                <input
                  id="account"
                  v-model="form.account"
                  type="text"
                  inputmode="numeric"
                  class="form-control"
                  placeholder="請輸入 10～16 碼扣款帳號"
                  required
                  @input="handleAccountInput"
                />
              </div>
              <div
                v-for="field in passwordFields"
                :key="field.key"
                class="mb-3"
              >
                <label :for="field.key" class="form-label">
                  {{ field.label }} <span class="text-danger">*</span>
                </label>
                <div class="position-relative">
                  <input
                    :id="field.key"
                    v-model="form[field.key]"
                    :type="showPassword[field.key] ? 'text' : 'password'"
                    class="form-control pe-5"
                    autocomplete="new-password"
                    :placeholder="field.placeholder"
                    required
                  />
                  <button
                    type="button"
                    class="btn position-absolute top-50 end-0 translate-middle-y border-0 bg-transparent text-secondary"
                    :aria-label="`顯示或隱藏${field.label}`"
                    @click="showPassword[field.key] = !showPassword[field.key]"
                  >
                    <i
                      :class="
                        showPassword[field.key]
                          ? 'bi bi-eye'
                          : 'bi bi-eye-slash'
                      "
                    ></i>
                  </button>
                </div>
              </div>
              <button
                type="submit"
                class="btn btn-success w-100 mt-2"
                :disabled="saving"
              >
                {{ saving ? "註冊中..." : "註冊" }}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
