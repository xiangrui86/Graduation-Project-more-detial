<template>
  <div class="app-shell">
    <div class="container surface shell">
      <header class="topbar">
        <div class="brand">
          <span class="mark" />
          <div class="meta">
            <div class="title">Sports Mall</div>
            <div class="sub">运营中心</div>
          </div>
        </div>
        <div class="actions">
          <el-button size="small" type="primary" @click="logout"
            >退出登录</el-button
          >
        </div>
      </header>

      <div class="body">
        <aside class="aside">
          <el-menu :default-active="$route.path" router class="menu">
            <el-menu-item index="/merchant">数据报表</el-menu-item>
            <el-menu-item index="/merchant/products">商品管理</el-menu-item>
            <el-menu-item index="/merchant/inventory">库存管理</el-menu-item>
            <el-menu-item index="/merchant/reviews">评价管理</el-menu-item>
            <el-menu-item index="/merchant/orders">交易管理</el-menu-item>
          </el-menu>
        </aside>

        <main class="main page">
          <router-view />
        </main>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "MerchantLayout",
  methods: {
    logout() {
      this.$store.dispatch("logout");
      this.$router.push("/login");
    },
  },
};
</script>

<style scoped>
.shell {
  overflow: hidden;
}
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
  border-bottom: 1px solid rgba(15, 23, 42, 0.07);
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
}
.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}
.mark {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: linear-gradient(135deg, #34d399 0%, #10b981 60%, #059669 100%);
  box-shadow: 0 4px 14px rgba(16, 185, 129, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.mark::after {
  content: "O";
  color: #fff;
  font-size: 15px;
  font-weight: 900;
  letter-spacing: -0.5px;
}
.meta {
  line-height: 1.2;
}
.title {
  font-weight: 900;
  font-size: 15px;
  letter-spacing: 0.2px;
  color: var(--text);
}
.sub {
  font-size: 11px;
  color: var(--muted);
  margin-top: 1px;
  font-weight: 500;
}

.body {
  display: flex;
  min-height: calc(100vh - 18px * 2 - 60px);
}
.aside {
  width: 224px;
  padding: 12px 8px;
  border-right: 1px solid rgba(15, 23, 42, 0.06);
  background: linear-gradient(
    180deg,
    rgba(240, 253, 250, 1) 0%,
    rgba(236, 253, 245, 1) 100%
  );
  flex-shrink: 0;
}
.menu {
  background: transparent;
}
.main {
  flex: 1;
  min-width: 0;
}
</style>
