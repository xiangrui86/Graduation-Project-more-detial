<template>
  <div class="page-block">
    <div class="page-title">
      <h2>数据报表</h2>
      <span class="sub">全站关键指标</span>
    </div>

    <el-row :gutter="16">
      <el-col :span="8">
        <el-card class="metric">
          <div class="num">{{ data.userCount || 0 }}</div>
          <div class="label">用户数</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="metric">
          <div class="num">{{ data.productCount || 0 }}</div>
          <div class="label">商品数</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="metric">
          <div class="num">{{ data.orderCount || 0 }}</div>
          <div class="label">订单数</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getReport } from '@/api/admin'

export default {
  name: 'AdminDashboard',
  data() {
    return { data: {} }
  },
  created() {
    getReport().then(res => { if (res.data) this.data = res.data })
  }
}
</script>

<style scoped>
.metric{
  border-radius: 16px;
}
.num { font-size: 30px; font-weight: 900; color: #2563eb; letter-spacing: .2px; }
.label{ color: rgba(15,23,42,.64); margin-top: 4px; font-weight: 700; }
</style>
