<template>
  <div class="app-shell">
    <div class="container surface user-shell">
      <header class="topbar">
        <div class="left">
          <router-link to="/" class="brand">
            <svg
              class="mark"
              width="32"
              height="32"
              viewBox="0 0 48 48"
              fill="none"
            >
              <circle cx="24" cy="24" r="24" fill="#ff6900" />
              <path
                d="M14 34c2-8 7-14 10-14s8 6 10 14"
                stroke="#fff"
                stroke-width="2.5"
                stroke-linecap="round"
              />
              <circle cx="24" cy="20" r="5" fill="#fff" />
            </svg>
            <span class="title">SPORTS</span>
            <span class="badge">用户端</span>
          </router-link>
          <div class="links">
            <router-link to="/" exact>首页</router-link>
            <router-link to="/products">全部商品</router-link>
            <router-link to="/new">新品上架</router-link>
            <router-link to="/recommend">猜您想买</router-link>
            <router-link to="/rank">销量排行</router-link>
            <router-link to="/cart">购物车</router-link>
            <router-link to="/favorite">我的收藏</router-link>
            <router-link to="/orders">我的订单</router-link>
            <router-link to="/profile">个人中心</router-link>
            <router-link to="/news">通知与资讯</router-link>
          </div>
        </div>

        <div class="right">
          <template v-if="user">
            <span class="pill">
              <span class="dot" />
              {{ user.nickname }}
            </span>
            <el-button size="small" type="primary" @click="logout"
              >退出</el-button
            >
          </template>
          <template v-else>
            <el-button size="small" type="text" @click="$router.push('/login')"
              >登录</el-button
            >
            <el-button
              size="small"
              type="primary"
              @click="$router.push('/register')"
              >注册</el-button
            >
          </template>
        </div>
      </header>

      <main class="page">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script>
export default {
  name: "UserLayout",
  computed: {
    user() {
      return this.$store.state.user;
    },
  },
  methods: {
    logout() {
      this.$store.dispatch("logout");
      this.$router.push("/login");
    },
  },
};
</script>

<style scoped>
.user-shell {
  overflow: hidden;
  border-radius: var(--radius-xl);
}
.topbar {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  padding: 0 20px;
  height: 62px;
  border-bottom: 1px solid var(--border-2);
  background: rgba(255, 255, 255, 0.94);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
}
.left {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 0;
}
.brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  color: inherit;
  white-space: nowrap;
}
.mark {
  display: block;
  flex-shrink: 0;
  filter: drop-shadow(0 3px 8px rgba(255, 105, 0, 0.3));
  transition: filter 0.2s;
}
.brand:hover .mark {
  filter: drop-shadow(0 4px 12px rgba(255, 105, 0, 0.45));
}
.title {
  font-weight: 900;
  font-size: 15px;
  letter-spacing: 0.3px;
  color: var(--text);
  background: linear-gradient(135deg, #ff6900 0%, #ff4d00 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.badge {
  font-size: 11px;
  color: var(--primary);
  border: 1.5px solid rgba(var(--primary-rgb), 0.3);
  padding: 3px 9px;
  border-radius: 999px;
  background: var(--primary-light);
  font-weight: 600;
}
.links {
  display: flex;
  align-items: center;
  gap: 2px;
  flex-wrap: wrap;
  min-width: 0;
}
.links a {
  color: var(--text-2);
  text-decoration: none;
  padding: 6px 10px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 500;
  transition: background 0.15s ease, color 0.15s ease;
  white-space: nowrap;
}
.links a:hover {
  background: var(--surface-3);
  color: var(--text);
}
.links a.router-link-active {
  background: var(--primary-light);
  color: var(--primary);
  font-weight: 700;
}
.right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}
.dot {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: var(--success);
  box-shadow: 0 0 0 3px rgba(34, 197, 89, 0.18);
}
</style>
