<template>
  <div class="page-block">
    <div class="page-title">
      <h2>全部商品</h2>
      <span class="sub">浏览与挑选心仪装备</span>
    </div>

    <el-row :gutter="16">
      <el-col v-for="p in list" :key="p.id" :xs="12" :sm="8" :md="6">
        <product-card :product="p" />
      </el-col>
    </el-row>

    <el-pagination
      v-if="total > pageSize"
      :current-page="page + 1"
      :page-size="pageSize"
      :total="total"
      layout="prev, pager, next"
      @current-change="onPage"
    />
  </div>
</template>

<script>
import { getProducts } from '@/api/pub'

export default {
  name: 'AllProducts',
  data() {
    return {
      list: [],
      page: 0,
      pageSize: 12,
      total: 0
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      getProducts({ page: this.page, size: this.pageSize }).then(res => {
        if (res.data && res.data.content) {
          this.list = res.data.content
          this.total = res.data.totalElements || 0
        }
      })
    },
    onPage(p) {
      this.page = p - 1
      this.load()
    }
  },
  components: {
    ProductCard: () => import('@/components/ProductCard.vue')
  }
}
</script>
