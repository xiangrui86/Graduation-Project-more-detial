<template>
  <div class="page-block">
    <div class="page-title">
      <h2>我的订单</h2>
      <span class="sub">支付、收货与查看明细</span>
    </div>

    <div class="order-container">
      <el-table
        :data="orders"
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
        <el-table-column label="操作" width="220">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.status === 'PENDING'"
              size="small"
              type="primary"
              class="btn-primary"
              @click="pay(scope.row.id)"
              >去支付</el-button
            >
            <el-button
              v-if="scope.row.status === 'SHIPPED'"
              size="small"
              class="btn-secondary"
              @click="confirmReceive(scope.row.id)"
              >确认收货</el-button
            >
            <el-button
              v-if="scope.row.status === 'RECEIVED'"
              size="small"
              type="primary"
              class="btn-primary"
              @click="openRefund(scope.row)"
              >申请退货</el-button
            >
            <el-button
              size="small"
              type="info"
              plain
              class="btn-secondary"
              @click="viewDetail(scope.row.id)"
              >详情</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-pagination
      v-if="total > pageSize"
      :current-page="page + 1"
      :page-size="pageSize"
      :total="total"
      layout="prev, pager, next"
      @current-change="
        (p) => {
          page = p - 1;
          load();
        }
      "
    />
    <el-dialog
      :title="'订单详情 ' + (detail && detail.order && detail.order.orderNo)"
      :visible.sync="detailVisible"
      width="600px"
      append-to-body
    >
      <div v-if="detail">
        <p>状态：{{ statusText(detail.order.status) }}</p>
        <p>金额：¥ {{ detail.order.totalAmount }}</p>
        <el-table :data="detail.items" border size="small">
          <el-table-column prop="productName" label="商品" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="price" label="单价" width="80" />
        </el-table>
      </div>
    </el-dialog>

    <el-dialog
      title="申请退货"
      :visible.sync="refundVisible"
      width="420px"
      append-to-body
    >
      <el-form :model="refundForm" label-width="80px">
        <el-form-item label="原因">
          <el-input
            v-model="refundForm.reason"
            type="textarea"
            :rows="3"
            maxlength="200"
            show-word-limit
            placeholder="请填写退货原因（可选）"
          />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button class="btn-secondary" @click="refundVisible = false"
          >取消</el-button
        >
        <el-button
          type="primary"
          class="btn-primary"
          :loading="refundLoading"
          @click="submitRefund"
          >提交</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getMyOrders,
  getOrderDetail,
  payOrder,
  confirmReceive,
  requestRefund,
} from "@/api/user";

export default {
  name: "MyOrders",
  data() {
    return {
      orders: [],
      page: 0,
      pageSize: 10,
      total: 0,
      detail: null,
      detailVisible: false,
      refundVisible: false,
      refundLoading: false,
      refundForm: { orderId: null, reason: "" },
    };
  },
  created() {
    this.load();
  },
  methods: {
    statusText(s) {
      const map = {
        PENDING: "待支付",
        PAID: "已支付",
        SHIPPED: "已发货",
        RECEIVED: "已收货",
        CANCELLED: "已取消",
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
        CANCELLED: "info",
        REFUND_REQUESTED: "warning",
        REFUNDED: "danger",
      };
      return map[s] || "info";
    },
    load() {
      getMyOrders({ page: this.page, size: this.pageSize }).then((res) => {
        if (res.data && res.data.content) {
          this.orders = res.data.content;
          this.total = res.data.totalElements || 0;
        }
      });
    },
    pay(id) {
      payOrder(id).then((res) => {
        if (res.code === 200) {
          this.$message.success("支付成功（模拟支付）");
          this.load();
        } else this.$message.error(res.message || "支付失败");
      });
    },
    confirmReceive(id) {
      confirmReceive(id).then((res) => {
        if (res.code === 200) {
          this.$message.success("已确认收货");
          this.load();
        }
      });
    },
    viewDetail(id) {
      getOrderDetail(id).then((res) => {
        if (res.data) {
          this.detail = res.data;
          this.detailVisible = true;
        }
      });
    },
    openRefund(row) {
      this.refundForm = { orderId: row.id, reason: "" };
      this.refundVisible = true;
    },
    submitRefund() {
      const { orderId, reason } = this.refundForm;
      if (!orderId) return;
      this.refundLoading = true;
      requestRefund(orderId, reason)
        .then((res) => {
          if (res.code === 200) {
            this.$message.success("已提交退货申请");
            this.refundVisible = false;
            this.load();
          } else {
            this.$message.error(res.message || "提交失败");
          }
        })
        .finally(() => {
          this.refundLoading = false;
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
</style>
