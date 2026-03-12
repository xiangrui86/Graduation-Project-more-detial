<template>
  <div class="page-block">
    <div class="page-title">
      <h2>分类精选</h2>
      <span class="sub">按分类快速浏览</span>
    </div>

    <el-row :gutter="16">
      <el-col v-for="p in list" :key="p.id" :xs="12" :sm="8" :md="6">
        <product-card :product="p" />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getProducts } from '@/api/pub'

export default {
  name: 'Category',
  data() {
    return { list: [] }
  },
  created() {
    const id = this.$route.params.id
    getProducts({ categoryId: id, page: 0, size: 24 }).then(res => {
      if (res.data && res.data.content) this.list = res.data.content
    })
  },
  components: {
    ProductCard: () => import('@/components/ProductCard.vue')
  }
}
</script>
