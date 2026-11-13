<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { updatePassword } from "@/services/memberService.js";
import useUserStore from "@/stores/user.js";
const router = useRouter();
const userStore = useUserStore();
const saving = ref(false);
const form = reactive({
  currentPassword: "",
  newPassword: "",
  confirmPassword: "",
});
const showPassword = reactive({
  currentPassword: false,
  newPassword: false,
  confirmPassword: false,
});
const fields = [
  {
    key: "currentPassword",
    label: "目前密碼",
    placeholder: "請輸入目前密碼",
    autocomplete: "current-password",
  },
  {
    key: "newPassword",
    label: "新密碼",
    placeholder: "請輸入 6～15 碼新密碼",
    autocomplete: "new-password",
  },
  {
    key: "confirmPassword",
    label: "確認新密碼",
    placeholder: "請再次輸入新密碼",
    autocomplete: "new-password",
  },
];
const submitPassword = async () => {
  if (saving.value) return;
  let message = "";
  if (form.newPassword.length < 6 || form.newPassword.length > 15)
    message = "新密碼須為 6～15 碼";
  else if (form.currentPassword === form.newPassword)
    message = "新密碼不可與目前密碼相同";
  else if (form.newPassword !== form.confirmPassword)
    message = "兩次輸入的新密碼不一致";
  if (message) {
    window.alertUI.toast("error", message);
    return;
  }
  saving.value = true;
  try {
    const response = await updatePassword({
      currentPassword: form.currentPassword,
      newPassword: form.newPassword,
    });
    userStore.clear();
    await window.alertUI.toast("success", response.data.message);
    await router.replace("/login");
  } catch (error) {
    window.alertUI.apiError(error);
  } finally {
    saving.value = false;
  }
};
</script>
<template>
  <div
    class="container flex-grow-1 d-flex align-items-center justify-content-center py-3"
  >
    <div class="card shadow-sm w-100" style="max-width: 500px">
      <div class="card-body p-4">
        <h4 class="text-center fw-bold mb-4">修改密碼</h4>
        <form @submit.prevent="submitPassword">
          <div v-for="field in fields" :key="field.key" class="mb-3">
            <label :for="field.key" class="form-label">
              {{ field.label }} <span class="text-danger">*</span>
            </label>
            <div class="position-relative">
              <input
                :id="field.key"
                v-model="form[field.key]"
                :type="showPassword[field.key] ? 'text' : 'password'"
                :autocomplete="field.autocomplete"
                :placeholder="field.placeholder"
                class="form-control pe-5"
                required
              />
              <button
                type="button"
                class="btn position-absolute top-50 end-0 translate-middle-y border-0 text-secondary"
                :aria-label="`顯示或隱藏${field.label}`"
                @click="showPassword[field.key] = !showPassword[field.key]"
              >
                <i
                  :class="
                    showPassword[field.key] ? 'bi bi-eye' : 'bi bi-eye-slash'
                  "
                ></i>
              </button>
            </div>
          </div>
          <div class="d-flex gap-2 mt-4">
            <RouterLink to="/profile" class="btn btn-secondary flex-fill"
              >取消</RouterLink
            >
            <button
              type="submit"
              class="btn btn-success flex-fill"
              :disabled="saving"
            >
              {{ saving ? "儲存中..." : "儲存" }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
