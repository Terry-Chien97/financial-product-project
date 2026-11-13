import { defineStore } from "pinia";
const useUserStore = defineStore("user", {
  state: () => ({
    token: "",
    profile: null,
  }),
  actions: {
    setToken(token) {
      this.token = token;
      this.profile = null;
    },
    setProfile(profile) {
      this.profile = profile
        ? {
            userName: profile.userName ?? "",
            email: profile.email ?? "",
          }
        : null;
    },
    clear() {
      this.token = "";
      this.profile = null;
    },
  },
  persist: {
    pick: ["token"],
  },
});
export default useUserStore;
