import Vue from "vue";
import VueRouter from "vue-router";

Vue.use(VueRouter);

// 路由表：按用户、管理员、商家三类角色划分
const routes = [
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/Login.vue"),
    meta: { guest: true },
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("@/views/Login.vue"),
    meta: { guest: true },
  },
  {
    path: "/",
    component: () => import("@/layouts/UserLayout.vue"),
    children: [
      {
        path: "",
        name: "Home",
        component: () => import("@/views/user/Home.vue"),
      },
      {
        path: "products",
        name: "AllProducts",
        component: () => import("@/views/user/AllProducts.vue"),
      },
      {
        path: "products/:id",
        name: "ProductDetail",
        component: () => import("@/views/user/ProductDetail.vue"),
      },
      {
        path: "cart",
        name: "Cart",
        component: () => import("@/views/user/Cart.vue"),
        meta: { auth: true, role: "USER" },
      },
      {
        path: "favorite",
        name: "Favorite",
        component: () => import("@/views/user/Favorite.vue"),
        meta: { auth: true, role: "USER" },
      },
      {
        path: "orders",
        name: "MyOrders",
        component: () => import("@/views/user/MyOrders.vue"),
        meta: { auth: true, role: "USER" },
      },
      {
        path: "profile",
        name: "UserProfile",
        component: () => import("@/views/user/Profile.vue"),
        meta: { auth: true, role: "USER" },
      },
      {
        path: "address-book",
        name: "AddressBook",
        component: () => import("@/views/user/AddressBook.vue"),
        meta: { auth: true, role: "USER" },
      },
      {
        path: "new",
        name: "NewArrivals",
        component: () => import("@/views/user/NewArrivals.vue"),
      },
      {
        path: "recommend",
        name: "Recommend",
        component: () => import("@/views/user/Recommend.vue"),
      },
      {
        path: "category/:id",
        name: "Category",
        component: () => import("@/views/user/Category.vue"),
      },
      {
        path: "rank",
        name: "SalesRank",
        component: () => import("@/views/user/SalesRank.vue"),
      },
      {
        path: "news",
        name: "News",
        component: () => import("@/views/user/News.vue"),
      },
      {
        path: "announcements",
        redirect: "news",
      },
      {
        path: "chat",
        name: "UserChat",
        component: () => import("@/views/user/Chat.vue"),
        meta: { auth: true, role: "USER" },
      },
    ],
  },
  {
    path: "/admin",
    component: () => import("@/layouts/AdminLayout.vue"),
    meta: { auth: true, role: "ADMIN" },
    children: [
      {
        path: "",
        name: "AdminDashboard",
        component: () => import("@/views/admin/Dashboard.vue"),
      },
      {
        path: "users",
        name: "AdminUsers",
        component: () => import("@/views/admin/Users.vue"),
      },
      {
        path: "merchants",
        name: "AdminMerchants",
        component: () => import("@/views/admin/Merchants.vue"),
      },
      {
        path: "categories",
        name: "AdminCategories",
        component: () => import("@/views/admin/Categories.vue"),
      },
      {
        path: "orders",
        name: "AdminOrders",
        component: () => import("@/views/admin/Orders.vue"),
      },
      {
        path: "news",
        name: "AdminNews",
        component: () => import("@/views/admin/News.vue"),
      },
      {
        path: "reviews",
        name: "AdminReviews",
        component: () => import("@/views/admin/Reviews.vue"),
      },
      {
        path: "products",
        name: "AdminProducts",
        component: () => import("@/views/admin/Products.vue"),
      },
      {
        path: "products/:id/detail",
        name: "AdminProductDetail",
        component: () => import("@/views/admin/ProductDetail.vue"),
      },
    ],
  },
  {
    path: "/merchant",
    component: () => import("@/layouts/MerchantLayout.vue"),
    meta: { auth: true, role: "MERCHANT" },
    children: [
      {
        path: "",
        name: "MerchantDashboard",
        component: () => import("@/views/merchant/Dashboard.vue"),
      },
      {
        path: "products",
        name: "MerchantProducts",
        component: () => import("@/views/merchant/Products.vue"),
      },
      {
        path: "products/:id/detail",
        name: "MerchantProductDetail",
        component: () => import("@/views/merchant/ProductDetail.vue"),
      },
      {
        path: "inventory",
        name: "MerchantInventory",
        component: () => import("@/views/merchant/Inventory.vue"),
      },
      {
        path: "reviews",
        name: "MerchantReviews",
        component: () => import("@/views/merchant/Reviews.vue"),
      },
      {
        path: "orders",
        name: "MerchantOrders",
        component: () => import("@/views/merchant/Orders.vue"),
      },
      {
        path: "chat",
        name: "MerchantChatCenter",
        component: () => import("@/views/merchant/ChatCenter.vue"),
      },
    ],
  },
];

const router = new VueRouter({
  mode: "hash",
  base: process.env.BASE_URL,
  routes,
});

// 全局前置守卫：处理登录态校验与角色路由权限
router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem("user") || "null");
  if (to.meta.guest) {
    if (user && user.token) {
      const role = user.role;
      if (role === "ADMIN") next("/admin");
      else if (role === "MERCHANT") next("/merchant");
      else next("/");
    } else next();
    return;
  }
  if (to.meta.auth && (!user || !user.token)) {
    next("/login");
    return;
  }
  if (to.meta.role && (!user || !user.token)) {
    next("/login");
    return;
  }
  if (to.meta.role && to.meta.role !== user.role) {
    if (user && user.role === "ADMIN") next("/admin");
    else if (user && user.role === "MERCHANT") next("/merchant");
    else next("/");
    return;
  }
  next();
});

export default router;
