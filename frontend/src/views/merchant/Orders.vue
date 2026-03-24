<template>
  <div class="page-block">
    <div class="page-title">
      <h2>交易管理</h2>
      <span class="sub">发货处理与订单状态</span>
    </div>

    <div class="order-container">
      <el-table
        :data="list"
        border
        stripe
        highlight-current-row
        style="border-radius: 12px; overflow: hidden"
      >
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="totalAmount" label="金额" width="100" />
        <el-table-column prop="status" label="状态" width="120">
          <template slot-scope="scope">
            <el-tag :type="statusTagType(scope.row.status)" :effect="'dark'">
              {{ statusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="下单时间" />
        <el-table-column label="操作" width="180">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.status === 'PAID'"
              size="small"
              type="primary"
              class="btn-primary"
              @click="ship(scope.row.id)"
              >发货</el-button
            >
            <el-button
              v-if="scope.row.status === 'REFUND_REQUESTED'"
              size="small"
              type="danger"
              class="btn-secondary"
              @click="showRefundDialog(scope.row)"
              >查看退货原因</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog
      title="退货申请"
      :visible.sync="refundDialogVisible"
      width="450px"
      append-to-body
    >
      <div v-if="currentOrder" class="refund-info">
        <div class="info-row">
          <span class="label">订单号：</span>
          <span>{{ currentOrder.orderNo }}</span>
        </div>
        <div class="info-row">
          <span class="label">退货原因：</span>
          <span>{{ currentOrder.refundReason || "无" }}</span>
        </div>
        <div class="info-row">
          <span class="label">申请时间：</span>
          <span>{{ currentOrder.refundRequestTime || "无" }}</span>
        </div>
      </div>
      <span slot="footer">
        <el-button class="btn-secondary" @click="refundDialogVisible = false"
          >拒绝</el-button
        >
        <el-button type="primary" class="btn-primary" @click="acceptRefund"
          >同意退货</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getOrders, shipOrder, acceptRefund } from "@/api/merchant";

export default {
  name: "MerchantOrders",
  data() {
    return {
      list: [],
      refundDialogVisible: false,
      currentOrder: null,
    };
  },
  created() {
    this.loadOrders();
  },
  methods: {
    loadOrders() {
      getOrders({ page: 0, size: 50 }).then((res) => {
        if (res.data && res.data.content) this.list = res.data.content;
      });
    },
    statusText(s) {
      const map = {
        PENDING: "待支付",
        PAID: "已支付",
        SHIPPED: "已发货",
        RECEIVED: "已收货",
        REFUND_REQUESTED: "退货申请中",
        REFUNDED: "已退款",
      };
      return map[s] || s;
    },
    statusTagType(s) {
      const map = {
        PENDING: "info",
        PAID: "primary",
        SHIPPED: "warning",
        RECEIVED: "success",
        REFUND_REQUESTED: "warning",
        REFUNDED: "danger",
        CANCELLED: "info",
      };
      return map[s] || "info";
    },
    ship(id) {
      shipOrder(id).then((res) => {
        if (res.code === 200) {
          this.$message.success("已发货");
          this.list.find((o) => o.id === id).status = "SHIPPED";
        }
      });
    },
    showRefundDialog(order) {
      this.currentOrder = order;
      this.refundDialogVisible = true;
    },
    acceptRefund() {
      if (!this.currentOrder) return;
      acceptRefund(this.currentOrder.id).then((res) => {
        if (res.code === 200) {
          this.$message.success("已同意退货");
          this.refundDialogVisible = false;
          this.list.find((o) => o.id === this.currentOrder.id).status =
            "REFUNDED";
        } else {
          this.$message.error(res.message || "操作失败");
        }
      });
    },
  },
};
</script>

<style scoped>
.order-container {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  margin-bottom: 20px;
}

.table-header {
  background: #f7f8fa;
  font-weight: 600;
  color: #333;
}

.btn-primary {
  background: linear-gradient(135deg, #ff8a00, #ff5e00);
  border: none;
  border-radius: 20px;
}

.btn-secondary {
  border-radius: 20px;
}

.el-table__row:hover {
  background-color: #f5f7fa;
  transition: 0.2s;
}

.el-table td {
  padding: 16px 10px;
}

.refund-info {
  padding: 10px 0;
}
.info-row {
  display: flex;
  margin-bottom: 14px;
  line-height: 1.6;
}
.info-row .label {
  font-weight: 600;
  color: rgba(15, 23, 42, 0.8);
  width: 90px;
  flex-shrink: 0;
}
</style>
