<template>
  <div class="page-block">
    <div class="page-title">
      <h2>交易管理</h2>
      <span class="sub">发货处理与订单状态</span>
    </div>

    <el-table :data="list" border>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="totalAmount" label="金额" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          {{ statusText(scope.row.status) }}
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="下单时间" />
      <el-table-column label="操作" width="120">
        <template slot-scope="scope">
          <el-button v-if="scope.row.status === 'PAID'" size="small" type="primary" @click="ship(scope.row.id)">发货</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { getOrders, shipOrder } from '@/api/merchant'

export default {
  name: 'MerchantOrders',
  data() {
    return { list: [] }
  },
  created() {
    getOrders({ page: 0, size: 50 }).then(res => {
      if (res.data && res.data.content) this.list = res.data.content
    })
  },
  methods: {
    statusText(s) {
      const map = { PENDING: '待支付', PAID: '已支付', SHIPPED: '已发货', RECEIVED: '已收货' }
      return map[s] || s
    },
    ship(id) {
      shipOrder(id).then(res => {
        if (res.code === 200) {
          this.$message.success('已发货')
          this.list.find(o => o.id === id).status = 'SHIPPED'
        }
      })
    }
  }
}
</script>
