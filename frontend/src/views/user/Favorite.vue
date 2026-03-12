<template>
  <div class="page-block">
    <div class="page-title">
      <h2>我的收藏</h2>
      <span class="sub">快速回到喜欢的商品</span>
    </div>

    <el-row :gutter="16">
      <el-col v-for="p in list" :key="p.id" :xs="12" :sm="8" :md="6">
        <product-card :product="p" :show-cart="true" />
        <el-button size="small" type="text" @click="remove(p.id)">取消收藏</el-button>
      </el-col>
    </el-row>
    <p v-if="!list.length" class="muted">暂无收藏</p>
  </div>
</template>

<script>
import { getFavorites, removeFavorite } from '@/api/user'

export default {
  name: 'Favorite',
  data() {
    return { list: [] }
  },
  created() {
    getFavorites().then(res => { if (res.data) this.list = res.data })
  },
  methods: {
    remove(productId) {
      removeFavorite(productId).then(() => {
        this.list = this.list.filter(p => p.id !== productId)
      })
    }
  },
  components: {
    ProductCard: () => import('@/components/ProductCard.vue')
  }
}
</script>
