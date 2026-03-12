<template>
  <div class="page-block">
    <div class="page-title">
      <h2>新品上架</h2>
      <span class="sub">最新到货，先到先得</span>
    </div>

    <el-row :gutter="16">
      <el-col v-for="p in list" :key="p.id" :xs="12" :sm="8" :md="6">
        <product-card :product="p" />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getNewArrivals } from '@/api/pub'

export default {
  name: 'NewArrivals',
  data() {
    return { list: [] }
  },
  created() {
    getNewArrivals(24).then(res => { if (res.data) this.list = res.data })
  },
  components: {
    ProductCard: () => import('@/components/ProductCard.vue')
  }
}
</script>
