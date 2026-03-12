<template>
  <div class="page-block">
    <div class="page-title">
      <h2>系统资讯</h2>
      <span class="sub">新品、活动与系统更新</span>
    </div>

    <el-card v-for="n in list" :key="n.id" class="news-item card-hover">
      <div class="title">{{ n.title }}</div>
      <div class="time">{{ n.createdAt }}</div>
      <div class="content">{{ n.content }}</div>
    </el-card>
  </div>
</template>

<script>
import { getNews } from '@/api/pub'

export default {
  name: 'News',
  data() {
    return { list: [] }
  },
  created() {
    getNews({ page: 0, size: 20 }).then(res => {
      if (res.data && res.data.content) this.list = res.data.content
      else if (Array.isArray(res.data)) this.list = res.data
    })
  }
}
</script>

<style scoped>
.news-item { margin-bottom: 16px; }
.title{ font-weight: 900; letter-spacing: .2px; }
.time { color: rgba(15,23,42,.55); font-size: 12px; margin-top: 6px; }
.content { margin-top: 10px; color: rgba(15,23,42,.72); line-height: 1.65; }
</style>
