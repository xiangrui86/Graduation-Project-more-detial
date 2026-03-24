<template>
  <div class="app-shell">
    <div class="container surface shell">
      <header class="topbar">
        <div class="brand">
          <span class="mark" />
          <div class="meta">
            <div class="title">Sports Mall</div>
            <div class="sub">商家中心</div>
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
  padding: 14px 16px;
  border-bottom: 1px solid rgba(15, 23, 42, 0.08);
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(14px);
}
.brand {
  display: flex;
  align-items: center;
  gap: 10px;
}
.mark {
  width: 30px;
  height: 30px;
  border-radius: 12px;
  background: linear-gradient(135deg, #22c55e 0%, #2563eb 100%);
  box-shadow: 0 10px 22px rgba(34, 197, 94, 0.16);
}
.meta {
  line-height: 1.1;
}
.title {
  font-weight: 900;
  letter-spacing: 0.2px;
}
.sub {
  font-size: 12px;
  color: rgba(15, 23, 42, 0.62);
  margin-top: 2px;
}
.body {
  display: flex;
  min-height: calc(100vh - 18px * 2 - 60px);
}
.aside {
  width: 240px;
  padding: 14px 10px;
  border-right: 1px solid rgba(15, 23, 42, 0.08);
  background: rgba(255, 255, 255, 0.55);
}
.menu {
  background: transparent;
}
.main {
  flex: 1;
  min-width: 0;
}
</style>
