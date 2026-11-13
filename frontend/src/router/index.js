import { createRouter, createWebHistory } from "vue-router";
import useUserStore from "@/stores/user.js";
import LoginView from "@/views/LoginView.vue";
import RegisterView from "@/views/RegisterView.vue";
import ProductView from "@/views/ProductView.vue";
import LikeListView from "@/views/LikeListView.vue";
import ProfileView from "@/views/ProfileView.vue";
import ChangePasswordView from "@/views/ChangePasswordView.vue";
import UnauthorizedView from "@/errors/UnauthorizedView.vue";
import NotFoundView from "@/errors/NotFoundView.vue";
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      redirect: () => (useUserStore().token ? "/products" : "/login"),
    },
    {
      path: "/login",
      name: "login",
      component: LoginView,
    },
    {
      path: "/register",
      name: "register",
      component: RegisterView,
    },
    {
      path: "/products",
      name: "products",
      component: ProductView,
    },
    {
      path: "/likes",
      name: "likes",
      component: LikeListView,
    },
    {
      path: "/profile",
      name: "profile",
      component: ProfileView,
    },
    {
      path: "/change-password",
      name: "change-password",
      component: ChangePasswordView,
    },
    {
      path: "/401",
      name: "unauthorized",
      component: UnauthorizedView,
    },
    {
      path: "/:pathMatch(.*)*",
      name: "not-found",
      component: NotFoundView,
    },
  ],
});
router.beforeEach((to) => {
  const isLoggedIn = Boolean(useUserStore().token);
  if (isLoggedIn && ["/login", "/register"].includes(to.path)) {
    return "/products";
  }
  if (
    !isLoggedIn &&
    ["/products", "/likes", "/profile", "/change-password"].includes(to.path)
  ) {
    return "/login";
  }
});
export default router;
