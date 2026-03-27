<template>
  <div class="page-block fav-page">
    <div class="page-title">
      <h2>我的收藏</h2>
      <span class="sub" v-if="list.length">共 {{ list.length }} 件商品</span>
      <span class="sub" v-else>快速回到喜欢的商品</span>
    </div>

    <!-- 骨架屏 -->
    <el-row v-if="loading" :gutter="16">
      <el-col v-for="i in 8" :key="i" :xs="12" :sm="8" :md="6">
        <div class="ske-card">
          <div class="ske ske-img"></div>
          <div class="ske-body">
            <div class="ske ske-line" style="width:80%"></div>
            <div class="ske ske-line" style="width:50%;margin-top:8px"></div>
            <div class="ske ske-line" style="width:40%;margin-top:8px"></div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 空状态 -->
    <div v-if="!loading && !list.length" class="empty-state">
      <div class="empty-icon">⭐</div>
      <div class="empty-title">还没有收藏商品</div>
      <div class="empty-desc">去发现好物，点击商品页「收藏」按钮即可加入</div>
      <router-link to="/products">
        <el-button type="primary" class="go-btn">去逛逛</el-button>
      </router-link>
    </div>

    <!-- 商品网格 -->
    <el-row v-if="!loading && list.length" :gutter="16">
      <el-col
        v-for="item in list"
        :key="item.id"
        :xs="12" :sm="8" :md="6"
        class="fav-col"
      >
        <div class="fav-card-wrap">
          <!-- 取消收藏按钮 -->
          <button
            class="remove-btn"
            title="取消收藏"
            @click.stop="confirmRemove(item.id)"
          >
            <i class="el-icon-star-on"></i>
          </button>
          <product-card :product="item" :show-cart="true" />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getFavorites, removeFavorite } from "@/api/user";

export default {
  name: "Favorite",
  data() {
    return { list: [], loading: false };
  },
  created() {
    this.load();
  },
  methods: {
    load() {
      this.loading = true;
      getFavorites()
        .then((res) => { if (res.data) this.list = res.data; })
        .finally(() => { this.loading = false; });
    },
    confirmRemove(productId) {
      this.$confirm("确定取消收藏该商品？", "提示", {
        type: "warning",
        confirmButtonText: "取消收藏",
        cancelButtonText: "再想想",
      })
        .then(() => {
          removeFavorite(productId).then(() => {
            this.list = this.list.filter((p) => p.id !== productId);
            this.$message.success("已取消收藏");
          });
        })
        .catch(() => {});
    },
  },
  components: {
    ProductCard: () => import("@/components/ProductCard.vue"),
  },
};
</script>

<style scoped>
.fav-page { min-height: 400px; }

/* ── 骨架屏 ── */
.ske-card {
  border-radius: var(--radius-md);
  overflow: hidden;
  border: 1px solid var(--border-2);
  background: var(--surface);
  margin-bottom: 16px;
}
.ske-img {
  width: 100%;
  padding-top: 75%;
}
.ske-body { padding: 14px; }
.ske {
  background: linear-gradient(90deg, #e8eaf0 25%, #f5f6fa 50%, #e8eaf0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite;
  border-radius: 6px;
  height: 13px;
}
@keyframes shimmer {
  0%   { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* ── 空状态 ── */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  gap: 12px;
}
.empty-icon  { font-size: 60px; line-height: 1; }
.empty-title { font-size: 18px; font-weight: 700; color: var(--text); }
.empty-desc  { font-size: 14px; color: var(--muted); text-align: center; }
.go-btn { margin-top: 8px; padding: 10px 32px; border-radius: 999px; font-weight: 700; }

/* ── 卡片包裹层 ── */
.fav-col { margin-bottom: 4px; }
.fav-card-wrap {
  position: relative;
}

/* 取消收藏悬浮按钮 */
.remove-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 10;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: rgba(255,255,255,.92);
  backdrop-filter: blur(6px);
  box-shadow: 0 2px 8px rgba(0,0,0,.12);
  color: #f59e0b;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background .15s, color .15s, transform .15s;
  opacity: 0;
}
.fav-card-wrap:hover .remove-btn {
  opacity: 1;
}
.remove-btn:hover {
  background: #fef2f2;
  color: var(--danger);
  transform: scale(1.1);
}
</style>
