<template>
  <div class="page-block">
    <div class="page-title">
      <h2>最新公告</h2>
      <span class="sub">平台动态与通知</span>
    </div>

    <el-card v-for="a in list" :key="a.id" class="item card-hover">
      <div class="title">{{ a.title }}</div>
      <div class="time">{{ a.createdAt }}</div>
      <div class="content">{{ a.content }}</div>
    </el-card>
  </div>
</template>

<script>
import { getAnnouncements } from '@/api/pub'

export default {
  name: 'Announcements',
  data() {
    return { list: [] }
  },
  created() {
    getAnnouncements(20).then(res => { if (res.data) this.list = res.data })
  }
}
</script>

<style scoped>
.item { margin-bottom: 16px; }
.title{ font-weight: 900; letter-spacing: .2px; }
.time { color: rgba(15,23,42,.55); font-size: 12px; margin-top: 6px; }
.content { margin-top: 10px; color: rgba(15,23,42,.72); line-height: 1.65; }
</style>
