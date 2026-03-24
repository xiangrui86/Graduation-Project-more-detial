<template>
  <div class="page-block">
    <div class="page-title">
      <h2>数据报表</h2>
      <span class="sub">关键指标一览</span>
    </div>

    <el-row :gutter="16">
      <el-col :span="12">
        <el-card class="metric">
          <div class="num">{{ data.productCount || 0 }}</div>
          <div class="label">商品数</div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="metric">
          <div class="num">{{ data.orderCount || 0 }}</div>
          <div class="label">订单数</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="24">
        <el-card class="chart-card">
          <div class="chart-title">商品销量占比</div>
          <div ref="pie" class="pie"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getReport } from '@/api/merchant'
import * as echarts from 'echarts'

export default {
  name: 'MerchantDashboard',
  data() {
    return { data: {}, chart: null }
  },
  created() {
    getReport().then(res => {
      if (res.data) this.data = res.data
      this.$nextTick(() => this.renderPie())
    })
  },
  mounted() {
    this.renderPie()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    if (this.chart) {
      this.chart.dispose()
      this.chart = null
    }
  },
  watch: {
    'data.productSalesPie': {
      handler() {
        this.$nextTick(() => this.renderPie())
      },
      deep: true
    }
  },
  methods: {
    handleResize() {
      if (this.chart) this.chart.resize()
    },
    renderPie() {
      const el = this.$refs.pie
      if (!el) return
      const seriesData = Array.isArray(this.data.productSalesPie)
        ? this.data.productSalesPie
        : []

      if (!this.chart) {
        this.chart = echarts.init(el)
      }

      if (!seriesData.length) {
        this.chart.clear()
        this.chart.setOption({
          title: {
            text: '暂无数据',
            left: 'center',
            top: 'middle',
            textStyle: { color: 'rgba(15,23,42,.55)', fontSize: 14, fontWeight: 700 }
          }
        })
        return
      }

      this.chart.setOption({
        tooltip: { trigger: 'item' },
        legend: { type: 'scroll', bottom: 0 },
        series: [
          {
            name: '销量',
            type: 'pie',
            radius: ['35%', '70%'],
            avoidLabelOverlap: true,
            itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
            label: { show: true, formatter: '{b}\n{d}%', fontWeight: 700 },
            labelLine: { show: true },
            data: seriesData
          }
        ]
      })
    }
  }
}
</script>

<style scoped>
.metric{
  border-radius: 16px;
}
.num { font-size: 30px; font-weight: 900; color: #2563eb; letter-spacing: .2px; }
.label{ color: rgba(15,23,42,.64); margin-top: 4px; font-weight: 700; }

.chart-card{
  border-radius: 16px;
}
.chart-title{
  font-weight: 900;
  color: #0f172a;
  margin-bottom: 10px;
}
.pie{
  width: 100%;
  height: 360px;
}
</style>
