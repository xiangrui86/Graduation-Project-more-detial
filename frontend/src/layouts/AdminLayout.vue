<template>
  <div class="app-shell">
    <div class="container surface shell">
      <header class="topbar">
        <div class="brand">
          <span class="mark" />
          <div class="meta">
            <div class="title">Sports Mall</div>
            <div class="sub">管理后台</div>
          </div>
        </div>
        <div class="actions">
          <el-button size="small" type="primary" @click="logout">退出登录</el-button>
        </div>
      </header>

      <div class="body">
        <aside class="aside">
          <el-menu :default-active="$route.path" router class="menu">
            <el-menu-item index="/admin">数据报表</el-menu-item>
            <el-menu-item index="/admin/users">用户管理</el-menu-item>
            <el-menu-item index="/admin/merchants">运营管理</el-menu-item>
            <el-menu-item index="/admin/categories">分类管理</el-menu-item>
            <el-menu-item index="/admin/products">商品管理</el-menu-item>
            <el-menu-item index="/admin/orders">交易管理</el-menu-item>
            <el-menu-item index="/admin/news">资讯管理</el-menu-item>
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
  name: 'AdminLayout',
  methods: {
    logout() {
      this.$store.dispatch('logout')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.shell{ overflow: hidden; }
.topbar{
  display:flex;
  align-items:center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
  border-bottom: 1px solid rgba(15, 23, 42, .07);
  background: rgba(255,255,255,.92);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
}
.brand{ display:flex; align-items:center; gap: 12px; }
.mark{
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: linear-gradient(135deg, #818cf8 0%, #6366f1 60%, #4f46e5 100%);
  box-shadow: 0 4px 14px rgba(99,102,241,.35);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.mark::after{
  content: "A";
  color: #fff;
  font-size: 15px;
  font-weight: 900;
  letter-spacing: -.5px;
}
.meta{ line-height: 1.2; }
.title{ font-weight: 900; font-size: 15px; letter-spacing: .2px; color: var(--text); }
.sub{ font-size: 11px; color: var(--muted); margin-top: 1px; font-weight: 500; }

.body{
  display:flex;
  min-height: calc(100vh - 18px*2 - 60px);
}
.aside{
  width: 224px;
  padding: 12px 8px;
  border-right: 1px solid rgba(15, 23, 42, .06);
  background: linear-gradient(180deg, rgba(248,250,253,1) 0%, rgba(241,244,251,1) 100%);
  flex-shrink: 0;
}
.menu{ background: transparent; }
.main{ flex: 1; min-width: 0; }
</style>
