<template>
  <div class="page-block">
    <div class="page-title">
      <h2>销量排行</h2>
      <span class="sub">大家都在买</span>
    </div>

    <el-row :gutter="16">
      <el-col v-for="p in list" :key="p.id" :xs="12" :sm="8" :md="6">
        <product-card :product="p" />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getSalesRank } from '@/api/pub'

export default {
  name: 'SalesRank',
  data() {
    return { list: [] }
  },
  created() {
    getSalesRank(24).then(res => { if (res.data) this.list = res.data })
  },
  components: {
    ProductCard: () => import('@/components/ProductCard.vue')
  }
}
</script>
