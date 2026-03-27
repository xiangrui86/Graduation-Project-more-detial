<template>
  <div class="page-block">
    <div class="page-title">
      <h2>交易管理</h2>
      <span class="sub">订单状态与时间追踪</span>
    </div>

    <el-table :data="list" border>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="userId" label="用户ID" width="80" />
      <el-table-column prop="merchantId" label="运营ID" width="80" />
      <el-table-column prop="totalAmount" label="金额" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          {{ statusText(scope.row.status) }}
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="下单时间" />
    </el-table>
    <el-pagination
      v-if="total > 10"
      :current-page="page + 1"
      :page-size="10"
      :total="total"
      layout="prev, pager, next"
      @current-change="p => { page = p - 1; load() }"
    />
  </div>
</template>

<script>
import { getOrders } from '@/api/admin'

export default {
  name: 'AdminOrders',
  data() {
    return { list: [], page: 0, total: 0 }
  },
  created() {
    this.load()
  },
  methods: {
    statusText(s) {
      const map = {
        PENDING: '待支付',
        PAID: '已支付',
        SHIPPED: '已发货',
        RECEIVED: '已收货',
        CANCELLED: '已取消',
        REFUND_REQUESTED: '退货申请中',
        REFUNDED: '已退款'
      }
      return map[s] || s
    },
    load() {
      getOrders({ page: this.page, size: 10 }).then(res => {
        if (res.data && res.data.content) {
          this.list = res.data.content
          this.total = res.data.totalElements || 0
        }
      })
    }
  }
}
</script>
