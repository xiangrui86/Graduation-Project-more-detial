<template>
  <div class="page-block">
    <div class="page-title">
      <h2>猜您想买</h2>
      <span class="sub">基于相似购买行为的推荐</span>
    </div>

    <p class="tip">根据与您有相似购买行为的用户，为您推荐以下商品</p>
    <el-row :gutter="16">
      <el-col v-for="p in list" :key="p.id" :xs="12" :sm="8" :md="6">
        <product-card :product="p" />
      </el-col>
    </el-row>
    <p v-if="!list.length" class="muted">暂无推荐，多买几单后会有个性化推荐哦</p>
  </div>
</template>

<script>
import { getRecommend } from '@/api/pub'

export default {
  name: 'Recommend',
  data() {
    return { list: [] }
  },
  created() {
    const user = this.$store.state.user
    if (user && user.userId) {
      getRecommend(user.userId, 20).then(res => { if (res.data) this.list = res.data })
    }
  },
  components: {
    ProductCard: () => import('@/components/ProductCard.vue')
  }
}
</script>

<style scoped>
.tip { color: rgba(15,23,42,.68); margin-bottom: 14px; line-height: 1.6; }
</style>
