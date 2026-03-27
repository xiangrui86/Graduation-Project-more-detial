<template>
  <div class="admin-dashboard">
    <div class="dashboard-header">
      <div class="header-content">
        <h1 class="page-title">数据看板</h1>
        <p class="page-subtitle">实时掌握平台运营状况</p>
      </div>
      <div class="header-decoration"></div>
    </div>

    <!-- 核心指标卡片 -->
    <el-row :gutter="20" class="metrics-row">
      <el-col :xs="12" :sm="8" :md="6">
        <div class="metric-card user-metric">
          <div class="metric-content">
            <div class="metric-icon-wrapper">
              <i class="el-icon-user"></i>
            </div>
            <div class="metric-text">
              <div class="metric-value">{{ data.userCount || 0 }}</div>
              <div class="metric-label">用户总数</div>
            </div>
          </div>
          <div class="metric-trend">
            <i class="el-icon-top"></i>
            <span>持续增长</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6">
        <div class="metric-card product-metric">
          <div class="metric-content">
            <div class="metric-icon-wrapper">
              <i class="el-icon-shopping-bag-1"></i>
            </div>
            <div class="metric-text">
              <div class="metric-value">{{ data.productCount || 0 }}</div>
              <div class="metric-label">商品总数</div>
            </div>
          </div>
          <div class="metric-trend">
            <i class="el-icon-top"></i>
            <span>丰富品类</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6">
        <div class="metric-card order-metric">
          <div class="metric-content">
            <div class="metric-icon-wrapper">
              <i class="el-icon-shopping-cart-2"></i>
            </div>
            <div class="metric-text">
              <div class="metric-value">{{ data.orderCount || 0 }}</div>
              <div class="metric-label">订单总数</div>
            </div>
          </div>
          <div class="metric-trend">
            <i class="el-icon-top"></i>
            <span>交易活跃</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="8" :md="6">
        <div class="metric-card revenue-metric">
          <div class="metric-content">
            <div class="metric-icon-wrapper">
              <i class="el-icon-coin"></i>
            </div>
            <div class="metric-text">
              <div class="metric-value">
                ¥ {{ formatNumber(data.totalRevenue) || 0 }}
              </div>
              <div class="metric-label">总销售额</div>
            </div>
          </div>
          <div class="metric-trend">
            <i class="el-icon-top"></i>
            <span>业绩优秀</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <!-- 订单趋势图 -->
      <el-col :xs="24" :md="12">
        <div class="chart-card">
          <div class="chart-header">
            <div class="chart-title-wrapper">
              <i class="el-icon-date"></i>
              <h3 class="chart-title">订单趋势</h3>
            </div>
            <span class="chart-subtitle">最近 7 天订单数据</span>
          </div>
          <div ref="orderChart" class="chart-container"></div>
        </div>
      </el-col>

      <!-- 分类销售占比 -->
      <el-col :xs="24" :md="12">
        <div class="chart-card">
          <div class="chart-header">
            <div class="chart-title-wrapper">
              <i class="el-icon-pie-chart"></i>
              <h3 class="chart-title">分类销售占比</h3>
            </div>
            <span class="chart-subtitle">各品类销售分布</span>
          </div>
          <div ref="categoryChart" class="chart-container"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <!-- 用户增长图 -->
      <el-col :xs="24" :md="12">
        <div class="chart-card">
          <div class="chart-header">
            <div class="chart-title-wrapper">
              <i class="el-icon-user-solid"></i>
              <h3 class="chart-title">用户增长</h3>
            </div>
            <span class="chart-subtitle">近 6 个月用户增长趋势</span>
          </div>
          <div ref="userChart" class="chart-container"></div>
        </div>
      </el-col>

      <!-- 商品状态分布 -->
      <el-col :xs="24" :md="12">
        <div class="chart-card">
          <div class="chart-header">
            <div class="chart-title-wrapper">
              <i class="el-icon-box"></i>
              <h3 class="chart-title">商品状态</h3>
            </div>
            <span class="chart-subtitle">商品在售情况统计</span>
          </div>
          <div ref="productStatusChart" class="chart-container"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getReport } from "@/api/admin";
import * as echarts from "echarts";

export default {
  name: "AdminDashboard",
  data() {
    return {
      data: {},
      orderChart: null,
      categoryChart: null,
      userChart: null,
      productStatusChart: null,
    };
  },
  created() {
    this.loadData();
  },
  mounted() {
    this.$nextTick(() => {
      this.initCharts();
    });
  },
  beforeDestroy() {
    this.disposeCharts();
  },
  methods: {
    formatNumber(num) {
      if (!num) return "0.00";
      return Number(num).toLocaleString("zh-CN", {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      });
    },
    loadData() {
      getReport().then((res) => {
        if (res.data) {
          this.data = res.data;
          console.log("Dashboard data:", this.data);
          this.$nextTick(() => {
            this.setChartOptions();
            this.updateCharts();
          });
        }
      });
    },
    initCharts() {
      this.orderChart = echarts.init(this.$refs.orderChart);
      this.categoryChart = echarts.init(this.$refs.categoryChart);
      this.userChart = echarts.init(this.$refs.userChart);
      this.productStatusChart = echarts.init(this.$refs.productStatusChart);
    },
    setChartOptions() {
      const commonTooltip = {
        trigger: "axis",
        backgroundColor: "rgba(255, 255, 255, 0.98)",
        borderColor: "#e2e8f0",
        textStyle: { color: "#2d3748", fontSize: 13 },
        extraCssText:
          "box-shadow: 0 4px 12px rgba(0,0,0,0.1); border-radius: 8px;",
      };

      const commonGrid = {
        left: "3%",
        right: "4%",
        bottom: "3%",
        top: "10%",
        containLabel: true,
      };

      // 计算最近7天的日期标签
      const dayLabels = [];
      const dayNames = ["日", "一", "二", "三", "四", "五", "六"];
      for (let i = 6; i >= 0; i--) {
        const date = new Date();
        date.setDate(date.getDate() - i);
        dayLabels.push(
          `${date.getMonth() + 1}/${date.getDate()} ${dayNames[date.getDay()]}`,
        );
      }

      // 订单趋势图
      const orderOption = {
        tooltip: commonTooltip,
        grid: commonGrid,
        xAxis: {
          type: "category",
          boundaryGap: false,
          data: dayLabels,
          axisLine: { lineStyle: { color: "#cbd5e0" } },
          axisLabel: {
            color: "#718096",
            fontSize: 12,
            margin: 16,
          },
        },
        yAxis: {
          type: "value",
          axisLine: { show: false },
          axisTick: { show: false },
          splitLine: {
            lineStyle: {
              color: "#e2e8f0",
              type: "dashed",
            },
          },
          axisLabel: {
            color: "#718096",
            fontSize: 12,
          },
        },
        series: [
          {
            name: "订单数",
            type: "line",
            smooth: true,
            data: this.data.orderTrend || [0, 0, 0, 0, 0, 0, 0],
            itemStyle: {
              color: "#4299e1",
            },
            lineStyle: {
              width: 3,
              color: "#4299e1",
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: "rgba(66, 153, 225, 0.3)" },
                { offset: 1, color: "rgba(66, 153, 225, 0.05)" },
              ]),
            },
            symbol: "circle",
            symbolSize: 8,
          },
        ],
      };

      // 分类销售占比
      const categoryData = (this.data.categorySales || []).map((item) => ({
        value: item.value,
        name: item.name,
      }));

      const colors = ["#4299e1", "#48bb78", "#ed8936", "#f56565", "#9f7aea"];
      const categoryDataWithColor = categoryData.map((item, index) => ({
        ...item,
        itemStyle: { color: colors[index % colors.length] },
      }));

      const categoryOption = {
        tooltip: {
          trigger: "item",
          backgroundColor: "rgba(255, 255, 255, 0.98)",
          borderColor: "#e2e8f0",
          textStyle: { color: "#2d3748", fontSize: 13 },
          extraCssText:
            "box-shadow: 0 4px 12px rgba(0,0,0,0.1); border-radius: 8px;",
        },
        legend: {
          orient: "vertical",
          right: "5%",
          top: "center",
          textStyle: { color: "#718096", fontSize: 13 },
          itemGap: 15,
        },
        series: [
          {
            name: "销售占比",
            type: "pie",
            radius: ["45%", "70%"],
            center: ["35%", "50%"],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 12,
              borderColor: "#fff",
              borderWidth: 3,
            },
            label: {
              show: false,
              position: "center",
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 20,
                fontWeight: "bold",
                color: "#2d3748",
              },
              itemStyle: {
                shadowBlur: 20,
                shadowColor: "rgba(0, 0, 0, 0.2)",
              },
            },
            labelLine: {
              show: false,
            },
            data:
              categoryDataWithColor.length > 0
                ? categoryDataWithColor
                : [{ value: 1, name: "暂无数据" }],
          },
        ],
      };

      // 计算最近6个月的月份标签
      const monthLabels = [];
      for (let i = 5; i >= 0; i--) {
        const date = new Date();
        date.setMonth(date.getMonth() - i);
        monthLabels.push(`${date.getMonth() + 1}月`);
      }

      // 用户增长图
      const userOption = {
        tooltip: commonTooltip,
        grid: commonGrid,
        xAxis: {
          type: "category",
          boundaryGap: false,
          data: monthLabels,
          axisLine: { lineStyle: { color: "#cbd5e0" } },
          axisLabel: {
            color: "#718096",
            fontSize: 12,
            margin: 16,
          },
        },
        yAxis: {
          type: "value",
          axisLine: { show: false },
          axisTick: { show: false },
          splitLine: {
            lineStyle: {
              color: "#e2e8f0",
              type: "dashed",
            },
          },
          axisLabel: {
            color: "#718096",
            fontSize: 12,
          },
        },
        series: [
          {
            name: "用户数",
            type: "line",
            smooth: true,
            data: this.data.userGrowth || [0, 0, 0, 0, 0, 0],
            itemStyle: {
              color: "#48bb78",
            },
            lineStyle: {
              width: 3,
              color: "#48bb78",
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: "rgba(72, 187, 120, 0.3)" },
                { offset: 1, color: "rgba(72, 187, 120, 0.05)" },
              ]),
            },
            symbol: "circle",
            symbolSize: 8,
          },
        ],
      };

      // 商品状态分布
      const productStatusData = this.data.productStatus || {
        销售中: 0,
        已售罄: 0,
        已下架: 0,
      };
      const productStatusChartData = [
        {
          value: productStatusData["销售中"] || 0,
          name: "销售中",
          itemStyle: { color: "#48bb78" },
        },
        {
          value: productStatusData["已售罄"] || 0,
          name: "已售罄",
          itemStyle: { color: "#f56565" },
        },
        {
          value: productStatusData["已下架"] || 0,
          name: "已下架",
          itemStyle: { color: "#a0aec0" },
        },
      ];

      const productStatusOption = {
        tooltip: {
          trigger: "item",
          backgroundColor: "rgba(255, 255, 255, 0.98)",
          borderColor: "#e2e8f0",
          textStyle: { color: "#2d3748", fontSize: 13 },
          extraCssText:
            "box-shadow: 0 4px 12px rgba(0,0,0,0.1); border-radius: 8px;",
        },
        legend: {
          top: "5%",
          left: "center",
          textStyle: { color: "#718096", fontSize: 13 },
          itemGap: 20,
        },
        series: [
          {
            name: "商品状态",
            type: "pie",
            radius: ["35%", "60%"],
            center: ["50%", "55%"],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: "#fff",
              borderWidth: 3,
            },
            label: {
              show: true,
              position: "outside",
              formatter: "{b}: {d}%",
              color: "#718096",
              fontSize: 13,
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 20,
                shadowColor: "rgba(0, 0, 0, 0.2)",
              },
            },
            data: productStatusChartData,
          },
        ],
      };

      // 确保图表对象存在后再设置选项
      if (!this.orderChart) {
        console.error("订单趋势图表未初始化");
        this.initCharts();
      }
      if (!this.categoryChart) {
        console.error("分类销售占比图表未初始化");
        this.initCharts();
      }
      if (!this.userChart) {
        console.error("用户增长图表未初始化");
        this.initCharts();
      }
      if (!this.productStatusChart) {
        console.error("商品状态图表未初始化");
        this.initCharts();
      }

      try {
        this.orderChart && this.orderChart.setOption(orderOption);
        this.categoryChart && this.categoryChart.setOption(categoryOption);
        this.userChart && this.userChart.setOption(userOption);
        this.productStatusChart &&
          this.productStatusChart.setOption(productStatusOption);
        console.log("图表已成功更新");
      } catch (e) {
        console.error("设置图表选项时出错:", e);
      }
    },
    updateCharts() {
      if (this.orderChart) this.orderChart.resize();
      if (this.categoryChart) this.categoryChart.resize();
      if (this.userChart) this.userChart.resize();
      if (this.productStatusChart) this.productStatusChart.resize();
    },
    disposeCharts() {
      if (this.orderChart) this.orderChart.dispose();
      if (this.categoryChart) this.categoryChart.dispose();
      if (this.userChart) this.userChart.dispose();
      if (this.productStatusChart) this.productStatusChart.dispose();
    },
  },
};
</script>

<style scoped>
.admin-dashboard {
  min-height: 100vh;
  background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
  padding: 30px 20px 40px;
}

.dashboard-header {
  margin-bottom: 40px;
  position: relative;
  overflow: hidden;
}

.header-content {
  position: relative;
  z-index: 1;
}

.page-title {
  font-size: 42px;
  font-weight: 900;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 10px;
}

.page-subtitle {
  font-size: 18px;
  color: #718096;
  font-weight: 500;
}

.header-decoration {
  position: absolute;
  top: -30px;
  right: -30px;
  width: 200px;
  height: 200px;
  background: linear-gradient(
    135deg,
    rgba(102, 126, 234, 0.1) 0%,
    rgba(118, 75, 162, 0.1) 100%
  );
  border-radius: 50%;
  z-index: 0;
}

/* 指标卡片 */
.metrics-row {
  margin-bottom: 30px;
}

.metric-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 20px;
  position: relative;
  overflow: hidden;
}

.metric-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 6px;
  height: 100%;
  border-radius: 0 6px 6px 0;
}

.user-metric::before {
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
}

.product-metric::before {
  background: linear-gradient(180deg, #f093fb 0%, #f5576c 100%);
}

.order-metric::before {
  background: linear-gradient(180deg, #4facfe 0%, #00f2fe 100%);
}

.revenue-metric::before {
  background: linear-gradient(180deg, #43e97b 0%, #38f9d7 100%);
}

.metric-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.12);
}

.metric-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.metric-icon-wrapper {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
  flex-shrink: 0;
}

.user-metric .metric-icon-wrapper {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.product-metric .metric-icon-wrapper {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.order-metric .metric-icon-wrapper {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.revenue-metric .metric-icon-wrapper {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.metric-text {
  flex: 1;
}

.metric-value {
  font-size: 32px;
  font-weight: 900;
  color: #2d3748;
  line-height: 1;
  letter-spacing: -0.5px;
}

.metric-label {
  font-size: 14px;
  color: #718096;
  margin-top: 6px;
  font-weight: 500;
}

.metric-trend {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #48bb78;
  font-weight: 600;
}

.metric-trend i {
  font-size: 16px;
}

/* 图表卡片 */
.charts-row {
  margin-bottom: 30px;
}

.chart-card {
  background: white;
  border-radius: 20px;
  padding: 28px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 12px;
}

.chart-title-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
}

.chart-title-wrapper i {
  font-size: 24px;
  color: #667eea;
}

.chart-title {
  font-size: 20px;
  font-weight: 800;
  color: #2d3748;
  margin: 0;
}

.chart-subtitle {
  font-size: 14px;
  color: #718096;
  font-weight: 500;
}

.chart-container {
  width: 100%;
  height: 340px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .admin-dashboard {
    padding: 20px 12px 30px;
  }

  .page-title {
    font-size: 28px;
  }

  .page-subtitle {
    font-size: 15px;
  }

  .metric-card {
    padding: 18px;
  }

  .metric-icon-wrapper {
    width: 50px;
    height: 50px;
    font-size: 22px;
  }

  .metric-value {
    font-size: 24px;
  }

  .metric-label {
    font-size: 13px;
  }

  .chart-container {
    height: 280px;
  }

  .chart-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
