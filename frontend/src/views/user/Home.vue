<template>
  <div class="page-block home">
    <div class="page-title">
      <h2>商城首页</h2>
      <span class="sub">新品、排行与推荐</span>
    </div>

    <div class="ann">
      <div class="section-title">最新公告</div>
      <ul v-if="announcements.length" class="ann-list">
        <li v-for="a in announcements" :key="a.id">
          <router-link :to="'/announcements'">{{ a.title }}</router-link>
        </li>
      </ul>
      <div v-else class="muted">暂无公告</div>
    </div>

    <div class="section-title">新品上架</div>
    <el-row :gutter="16">
      <el-col v-for="p in newList" :key="p.id" :xs="12" :sm="8" :md="6">
        <product-card :product="p" />
      </el-col>
    </el-row>

    <div class="section-title">销量排行</div>
    <el-row :gutter="16">
      <el-col v-for="p in rankList" :key="p.id" :xs="12" :sm="8" :md="6">
        <product-card :product="p" />
      </el-col>
    </el-row>

    <template v-if="user">
      <div class="section-title">猜您想买</div>
      <el-row :gutter="16">
        <el-col v-for="p in recommendList" :key="p.id" :xs="12" :sm="8" :md="6">
          <product-card :product="p" />
        </el-col>
      </el-row>
    </template>
  </div>
</template>

<script>
import {
  getNewArrivals,
  getSalesRank,
  getRecommend,
  getAnnouncements,
} from "@/api/pub";

export default {
  name: "Home",
  data() {
    return {
      newList: [],
      rankList: [],
      recommendList: [],
      announcements: [],
    };
  },
  computed: {
    user() {
      return this.$store.state.user;
    },
  },
  created() {
    getNewArrivals(8).then((res) => {
      if (res.data) this.newList = res.data;
    });
    getSalesRank(8).then((res) => {
      if (res.data) this.rankList = res.data;
    });
    getAnnouncements(5).then((res) => {
      if (res.data) this.announcements = res.data;
    });
    if (this.user && this.user.userId) {
      getRecommend(this.user.userId, 8).then((res) => {
        if (res.data) this.recommendList = res.data;
      });
    }
  },
  components: {
    ProductCard: () => import("@/components/ProductCard.vue"),
  },
};
</script>

<style scoped>
.ann {
  padding: 12px;
  border-radius: 16px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  background: rgba(255, 255, 255, 0.55);
  margin-bottom: 14px;
}
.ann-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  gap: 8px;
}
.ann-list a {
  color: #2563eb;
  text-decoration: none;
  font-weight: 700;
}
.ann-list a:hover {
  text-decoration: underline;
}
.section-title {
  border-left: 4px solid #409eff;
  padding-left: 10px;
  font-weight: 600;
  margin: 18px 0 10px;
  font-size: 15px;
}
</style>
