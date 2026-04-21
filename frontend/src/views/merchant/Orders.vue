<template>
  <div class="page-block">
    <div class="page-title">
      <h2>交易管理</h2>
      <span class="sub">订单状态与发货处理</span>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon">📊</div>
        <div class="stat-content">
          <div class="stat-value">{{ total }}</div>
          <div class="stat-label">总订单数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">💰</div>
        <div class="stat-content">
          <div class="stat-value">¥{{ totalAmount.toFixed(2) }}</div>
          <div class="stat-label">总金额</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">⏳</div>
        <div class="stat-content">
          <div class="stat-value">{{ pendingCount }}</div>
          <div class="stat-label">待发货</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🚚</div>
        <div class="stat-content">
          <div class="stat-value">{{ shippedCount }}</div>
          <div class="stat-label">已发货</div>
        </div>
      </div>
    </div>

    <div class="toolbar-row">
      <el-input
        v-model="filters.orderNo"
        placeholder="订单号"
        clearable
        style="width: 200px; margin-right: 12px"
      />
      <el-select
        v-model="filters.status"
        placeholder="订单状态"
        clearable
        style="width: 180px; margin-right: 12px"
      >
        <el-option label="待支付" value="PENDING" />
        <el-option label="已支付" value="PAID" />
        <el-option label="已发货" value="SHIPPED" />
        <el-option label="已收货" value="RECEIVED" />
        <el-option label="已取消" value="CANCELLED" />
        <el-option label="退货申请中" value="REFUND_REQUESTED" />
        <el-option label="已退款" value="REFUNDED" />
      </el-select>
      <el-date-picker
        v-model="filters.startDate"
        type="date"
        placeholder="开始日期"
        format="yyyy-MM-dd"
        value-format="yyyy-MM-dd"
        style="width: 140px; margin-right: 8px"
      />
      <span style="color: #606266; font-weight: 500; margin-right: 8px"
        >至</span
      >
      <el-date-picker
        v-model="filters.endDate"
        type="date"
        placeholder="结束日期"
        format="yyyy-MM-dd"
        value-format="yyyy-MM-dd"
        style="width: 140px; margin-right: 12px"
      />
      <el-button type="primary" @click="search" :loading="loading"
        >搜索</el-button
      >
      <el-button @click="resetFilters">重置</el-button>
      <el-button
        type="success"
        @click="batchShip"
        :disabled="selectedOrders.length === 0"
        :loading="batchLoading"
      >
        批量发货 ({{ selectedOrders.length }})
      </el-button>
    </div>

    <el-table
      :data="list"
      border
      stripe
      :loading="loading"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="orderNo" label="订单号" width="180" fixed="left">
        <template slot-scope="scope">
          <span class="order-no">{{ scope.row.orderNo }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="receiverName" label="收货人" width="120" />
      <el-table-column prop="totalAmount" label="金额" width="120">
        <template slot-scope="scope">
          <span class="amount">¥{{ scope.row.totalAmount.toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)" :effect="'dark'">
            {{ statusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="下单时间" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
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
            >查看退货</el-button
          >
          <el-button
            size="small"
            type="info"
            @click="showOrderDetail(scope.row)"
            >详情</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > 10">
      <el-pagination
        :current-page="page + 1"
        :page-size="10"
        :total="total"
        layout="prev, pager, next"
        @current-change="
          (p) => {
            page = p - 1;
            loadOrders();
          }
        "
      />
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
        <el-button class="btn-secondary" @click="rejectRefund">拒绝</el-button>
        <el-button type="primary" class="btn-primary" @click="acceptRefund"
          >同意退货</el-button
        >
      </span>
    </el-dialog>

    <!-- 订单详情对话框 -->
    <el-dialog
      title="订单详情"
      :visible.sync="detailDialogVisible"
      width="700px"
      append-to-body
    >
      <div v-if="orderDetail" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">
            {{ orderDetail.order.orderNo }}
          </el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="statusTagType(orderDetail.order.status)">
              {{ statusText(orderDetail.order.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="收货人">
            {{ orderDetail.order.receiverName }}
          </el-descriptions-item>
          <el-descriptions-item label="联系电话">
            {{ orderDetail.order.receiverPhone }}
          </el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">
            {{ orderDetail.order.receiverAddress }}
          </el-descriptions-item>
          <el-descriptions-item label="订单金额">
            ¥{{ orderDetail.order.totalAmount.toFixed(2) }}
          </el-descriptions-item>
          <el-descriptions-item label="下单时间">
            {{ orderDetail.order.createdAt }}
          </el-descriptions-item>
        </el-descriptions>

        <el-divider>商品信息</el-divider>
        <el-table :data="orderDetail.items" border style="margin-top: 20px">
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="price" label="单价" width="100" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="subTotal" label="小计" width="120" />
        </el-table>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getOrders,
  getOrderDetail,
  shipOrder,
  acceptRefund,
  rejectRefund,
} from "@/api/merchant";

export default {
  name: "MerchantOrders",
  data() {
    return {
      list: [],
      page: 0,
      total: 0,
      loading: false,
      batchLoading: false,
      selectedOrders: [],
      totalAmount: 0,
      pendingCount: 0,
      shippedCount: 0,
      filters: {
        orderNo: "",
        status: "",
        startDate: null,
        endDate: null,
      },
      refundDialogVisible: false,
      currentOrder: null,
      detailDialogVisible: false,
      orderDetail: null,
    };
  },
  created() {
    this.loadOrders();
  },
  methods: {
    search() {
      this.page = 0;
      this.loadOrders();
    },
    resetFilters() {
      this.filters = {
        orderNo: "",
        status: "",
        startDate: null,
        endDate: null,
      };
      this.page = 0;
      this.loadOrders();
    },
    loadOrders() {
      this.loading = true;
      const params = {
        page: this.page,
        size: 10,
      };
      if (this.filters.orderNo) params.orderNo = this.filters.orderNo;
      if (this.filters.status) params.status = this.filters.status;
      if (this.filters.startDate) params.startDate = this.filters.startDate;
      if (this.filters.endDate) params.endDate = this.filters.endDate;

      getOrders(params)
        .then((res) => {
          this.loading = false;
          if (res.data && res.data.content) {
            this.list = res.data.content;
            this.total = res.data.totalElements || 0;
            this.calculateStats();
          }
        })
        .catch(() => {
          this.loading = false;
          this.$message.error("加载订单列表失败");
        });
    },
    calculateStats() {
      this.totalAmount = this.list.reduce(
        (sum, order) => sum + (order.totalAmount || 0),
        0,
      );
      this.pendingCount = this.list.filter(
        (order) => order.status === "PAID",
      ).length;
      this.shippedCount = this.list.filter(
        (order) => order.status === "SHIPPED",
      ).length;
    },
    statusText(s) {
      const map = {
        PENDING: "待支付",
        PAID: "已支付",
        SHIPPED: "已发货",
        RECEIVED: "已收货",
        REFUND_REQUESTED: "退货申请中",
        REFUNDED: "已退款",
        CANCELLED: "已取消",
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
      this.$confirm(`确认发货此订单吗？`, "发货确认", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          shipOrder(id).then((res) => {
            if (res.code === 200) {
              this.$message.success("已发货");
              this.loadOrders();
            } else {
              this.$message.error(res.message || "发货失败");
            }
          });
        })
        .catch(() => {});
    },
    batchShip() {
      if (this.selectedOrders.length === 0) return;
      const validOrders = this.selectedOrders.filter(
        (order) => order.status === "PAID",
      );
      if (validOrders.length === 0) {
        this.$message.warning("请选择已支付的订单进行发货");
        return;
      }
      this.$confirm(
        `确认对 ${validOrders.length} 个已支付订单进行批量发货？`,
        "批量操作确认",
        { type: "warning" },
      ).then(() => {
        this.batchLoading = true;
        const promises = validOrders.map((order) => shipOrder(order.id));
        Promise.all(promises)
          .then(() => {
            this.batchLoading = false;
            this.$message.success("批量发货成功");
            this.selectedOrders = [];
            this.loadOrders();
          })
          .catch(() => {
            this.batchLoading = false;
            this.$message.error("批量发货失败");
          });
      });
    },
    handleSelectionChange(selection) {
      this.selectedOrders = selection;
    },
    showRefundDialog(order) {
      getOrderDetail(order.id).then((res) => {
        if (res.code === 200 && res.data && res.data.order) {
          this.currentOrder = res.data.order;
          this.refundDialogVisible = true;
        } else {
          this.$message.error(res.message || "获取订单详情失败");
        }
      });
    },
    showOrderDetail(order) {
      getOrderDetail(order.id).then((res) => {
        if (res.code === 200 && res.data) {
          this.orderDetail = res.data;
          this.detailDialogVisible = true;
        } else {
          this.$message.error(res.message || "获取订单详情失败");
        }
      });
    },
    acceptRefund() {
      if (!this.currentOrder) return;
      acceptRefund(this.currentOrder.id).then((res) => {
        if (res.code === 200) {
          this.$message.success("已同意退货");
          this.refundDialogVisible = false;
          this.loadOrders();
        } else {
          this.$message.error(res.message || "操作失败");
        }
      });
    },
    rejectRefund() {
      if (!this.currentOrder) return;
      rejectRefund(this.currentOrder.id).then((res) => {
        if (res.code === 200) {
          this.$message.success("已拒绝退货");
          this.refundDialogVisible = false;
          this.loadOrders();
        } else {
          this.$message.error(res.message || "操作失败");
        }
      });
    },
  },
};
</script>

<style scoped>
.page-block {
  padding: 20px;
}

.page-title {
  margin-bottom: 24px;
}

.page-title h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
}

.page-title .sub {
  color: #666;
  font-size: 14px;
}

/* 统计卡片 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
}

.stat-icon {
  font-size: 32px;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

/* 工具栏 */
.toolbar-row {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
}

/* 表格样式 */
.order-no {
  font-family: "Monaco", "Menlo", monospace;
  font-weight: 500;
  color: #1890ff;
}

.amount {
  font-weight: 600;
  color: #52c41a;
}

.btn-primary {
  background: linear-gradient(135deg, #ff7a1f 0%, var(--primary) 100%);
  border: none;
  border-radius: 20px;
}

.btn-secondary {
  border-radius: 20px;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: right;
}

.order-detail {
  max-height: 500px;
  overflow-y: auto;
}

.dialog-footer {
  text-align: right;
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
