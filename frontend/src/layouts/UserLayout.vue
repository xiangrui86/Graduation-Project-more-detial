<template>
  <div class="app-shell">
    <div class="container surface user-shell">
      <header class="topbar">
        <div class="left">
          <router-link to="/" class="brand">
            <span class="mark" />
            <span class="title">Sports Mall</span>
            <span class="badge">用户端</span>
          </router-link>
          <div class="links">
            <router-link to="/">首页</router-link>
            <router-link to="/products">全部商品</router-link>
            <router-link to="/new">新品上架</router-link>
            <router-link to="/recommend">猜您想买</router-link>
            <router-link to="/rank">销量排行</router-link>
            <router-link to="/cart">购物车</router-link>
            <router-link to="/favorite">我的收藏</router-link>
            <router-link to="/orders">我的订单</router-link>
            <router-link to="/news">系统资讯</router-link>
            <router-link to="/announcements">最新公告</router-link>
          </div>
        </div>

        <div class="right">
          <span class="pill">
            <span class="dot" />
            {{ user && user.nickname }}
          </span>
          <el-button size="small" type="primary" @click="logout">退出</el-button>
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
  name: 'UserLayout',
  computed: {
    user() {
      return this.$store.state.user
    }
  },
  methods: {
    logout() {
      this.$store.dispatch('logout')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.user-shell{
  overflow: hidden;
}
.topbar{
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  padding: 14px 16px;
  border-bottom: 1px solid rgba(15, 23, 42, .08);
  background: rgba(255,255,255,.70);
  backdrop-filter: blur(14px);
}
.left{ display:flex; align-items:center; gap: 14px; min-width: 0; }
.brand{
  display: inline-flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  color: inherit;
  white-space: nowrap;
}
.mark{
  width: 28px;
  height: 28px;
  border-radius: 10px;
  background: linear-gradient(135deg, #2563eb 0%, #22c55e 100%);
  box-shadow: 0 10px 22px rgba(37, 99, 235, .18);
}
.title{ font-weight: 900; letter-spacing: .2px; }
.badge{
  font-size: 12px;
  color: rgba(15,23,42,.7);
  border: 1px solid rgba(15,23,42,.12);
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(255,255,255,.55);
}
.links{
  display:flex;
  align-items:center;
  gap: 10px;
  flex-wrap: wrap;
  min-width: 0;
}
.links a{
  color: rgba(15,23,42,.72);
  text-decoration: none;
  padding: 8px 10px;
  border-radius: 12px;
  transition: background .15s ease, color .15s ease;
}
.links a:hover{
  background: rgba(15,23,42,.06);
  color: rgba(15,23,42,.92);
}
.links a.router-link-active{
  background: rgba(37, 99, 235, .12);
  color: #2563eb;
  font-weight: 800;
}
.right{ display:flex; align-items:center; gap: 10px; }
.dot{
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: #22c55e;
  box-shadow: 0 0 0 4px rgba(34, 197, 94, .14);
}
</style>
