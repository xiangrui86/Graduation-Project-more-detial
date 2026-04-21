<template>
  <div class="page-block">
    <div class="page-title">
      <h2>交易管理</h2>
      <span class="sub">订单状态与时间追踪</span>
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
          <div class="stat-label">待处理</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🚚</div>
        <div class="stat-content">
          <div class="stat-value">{{ shippedCount }}</div>
          <div class="stat-label">待收货</div>
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
      <el-button type="success" @click="exportOrders" :loading="exporting"
        >导出</el-button
      >
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
          <el-tag :type="statusTagType(scope.row.status)">
            {{ statusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="createdAt"
        label="下单时间"
        width="160"
        sortable="custom"
      />
      <el-table-column prop="updatedAt" label="更新时间" width="160" />
      <el-table-column label="操作" width="300" fixed="right">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="small"
            @click="openDetailDialog(scope.row)"
            class="btn-view"
          >
            <i class="el-icon-view"></i> 查看
          </el-button>
          <el-button
            type="text"
            size="small"
            @click="handleUpdateStatus(scope.row, 'SHIPPED')"
            v-if="scope.row.status === 'PAID'"
            class="btn-ship"
          >
            <i class="el-icon-truck"></i> 发货
          </el-button>
          <el-button
            type="text"
            size="small"
            @click="handleUpdateStatus(scope.row, 'RECEIVED')"
            v-if="scope.row.status === 'SHIPPED'"
            class="btn-receive"
          >
            <i class="el-icon-check"></i> 确认收货
          </el-button>
          <el-button
            type="text"
            size="small"
            @click="handleUpdateStatus(scope.row, 'REFUNDED')"
            v-if="scope.row.status === 'REFUND_REQUESTED'"
            class="btn-refund"
          >
            <i class="el-icon-money"></i> 退款完成
          </el-button>
          <el-button
            type="text"
            size="small"
            @click="handleUpdateStatus(scope.row, 'CANCELLED')"
            v-if="scope.row.status === 'PENDING'"
            class="btn-cancel"
          >
            <i class="el-icon-close"></i> 取消
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 批量操作栏 -->
    <div class="batch-actions" v-if="selectedOrders.length > 0">
      <span class="batch-info">已选择 {{ selectedOrders.length }} 个订单</span>
      <el-button size="small" @click="batchShip" type="primary">
        <i class="el-icon-truck"></i> 批量发货
      </el-button>
      <el-button size="small" @click="batchCancel" type="danger">
        <i class="el-icon-close"></i> 批量取消
      </el-button>
      <el-button size="small" @click="clearSelection">清空选择</el-button>
    </div>

    <el-dialog
      :title="detail.order ? '订单详情 ' + detail.order.orderNo : '订单详情'"
      :visible.sync="detailDialogVisible"
      width="720px"
    >
      <div v-if="detail.order">
        <el-descriptions title="订单信息" column="2" border>
          <el-descriptions-item label="订单号">{{
            detail.order.orderNo
          }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{
            statusText(detail.order.status)
          }}</el-descriptions-item>
          <el-descriptions-item label="总金额">{{
            detail.order.totalAmount
          }}</el-descriptions-item>
          <el-descriptions-item label="实付金额">{{
            detail.order.payAmount || "-"
          }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{
            detail.order.createdAt
          }}</el-descriptions-item>
          <el-descriptions-item label="收货人">{{
            detail.order.receiverName
          }}</el-descriptions-item>
          <el-descriptions-item label="收货电话">{{
            detail.order.receiverPhone
          }}</el-descriptions-item>
          <el-descriptions-item label="收货地址">{{
            detail.order.receiverAddress
          }}</el-descriptions-item>
          <el-descriptions-item label="退款原因">{{
            detail.order.refundReason || "-"
          }}</el-descriptions-item>
          <el-descriptions-item label="退款申请时间">{{
            detail.order.refundRequestTime || "-"
          }}</el-descriptions-item>
          <el-descriptions-item
            label="退款拒绝说明"
            v-if="detail.order.refundRejectReason"
          >
            {{ detail.order.refundRejectReason }}
          </el-descriptions-item>
        </el-descriptions>
        <el-table :data="detail.items" border style="margin-top: 20px">
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="price" label="单价" width="100" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="subTotal" label="小计" width="120" />
          <el-table-column label="退款状态" width="160">
            <template slot-scope="scope">
              {{ refundItemStatusText(scope.row.refundStatus) }}
            </template>
          </el-table-column>
        </el-table>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>

    <el-pagination
      v-if="total > 10"
      :current-page="page + 1"
      :page-size="10"
      :total="total"
      layout="prev, pager, next"
      @current-change="
        (p) => {
          page = p - 1;
          load();
        }
      "
    />
  </div>
</template>

<script>
import { getOrders, getOrderDetail, updateOrderStatus } from "@/api/admin";

export default {
  name: "AdminOrders",
  data() {
    return {
      list: [],
      page: 0,
      total: 0,
      loading: false,
      exporting: false,
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
      detailDialogVisible: false,
      detail: {
        order: null,
        items: [],
      },
    };
  },
  created() {
    this.load();
  },
  methods: {
    search() {
      this.page = 0;
      this.load();
    },
    resetFilters() {
      this.filters = {
        orderNo: "",
        status: "",
        startDate: null,
        endDate: null,
      };
      this.page = 0;
      this.load();
    },
    statusTagType(status) {
      const types = {
        PENDING: "warning",
        PAID: "success",
        SHIPPED: "primary",
        RECEIVED: "info",
        CANCELLED: "danger",
        REFUND_REQUESTED: "warning",
        REFUNDED: "info",
      };
      return types[status] || "";
    },
    refundItemStatusText(status) {
      const map = {
        REFUND_REQUESTED: "退款申请中",
        REFUNDED: "已退款",
      };
      return map[status] || status || "无";
    },
    openDetailDialog(row) {
      this.loading = true;
      getOrderDetail(row.id)
        .then((res) => {
          this.loading = false;
          if (res.code === 200 && res.data) {
            this.detail.order = res.data.order;
            this.detail.items = res.data.items || [];
            this.detailDialogVisible = true;
          } else {
            this.$message.error(res.message || "获取订单详情失败");
          }
        })
        .catch(() => {
          this.loading = false;
          this.$message.error("获取订单详情失败");
        });
    },
    handleUpdateStatus(row, status) {
      this.$confirm(
        `确认将订单 ${row.orderNo} 状态更新为 ${this.statusText(status)}？`,
        "操作确认",
        {
          type: "warning",
        },
      )
        .then(() => {
          this.loading = true;
          updateOrderStatus(row.id, status)
            .then((res) => {
              this.loading = false;
              if (res.code === 200) {
                this.$message.success("操作成功");
                this.load();
              } else {
                this.$message.error(res.message || "操作失败");
              }
            })
            .catch(() => {
              this.loading = false;
              this.$message.error("操作失败");
            });
        })
        .catch(() => {});
    },
    handleSelectionChange(selection) {
      this.selectedOrders = selection;
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
        this.loading = true;
        const promises = validOrders.map((order) =>
          updateOrderStatus(order.id, "SHIPPED"),
        );
        Promise.all(promises)
          .then(() => {
            this.loading = false;
            this.$message.success("批量发货成功");
            this.selectedOrders = [];
            this.load();
          })
          .catch(() => {
            this.loading = false;
            this.$message.error("批量发货失败");
          });
      });
    },
    batchCancel() {
      if (this.selectedOrders.length === 0) return;
      const validOrders = this.selectedOrders.filter(
        (order) => order.status === "PENDING",
      );
      if (validOrders.length === 0) {
        this.$message.warning("请选择待支付的订单进行取消");
        return;
      }
      this.$confirm(
        `确认取消 ${validOrders.length} 个待支付订单？`,
        "批量操作确认",
        { type: "warning" },
      ).then(() => {
        this.loading = true;
        const promises = validOrders.map((order) =>
          updateOrderStatus(order.id, "CANCELLED"),
        );
        Promise.all(promises)
          .then(() => {
            this.loading = false;
            this.$message.success("批量取消成功");
            this.selectedOrders = [];
            this.load();
          })
          .catch(() => {
            this.loading = false;
            this.$message.error("批量取消失败");
          });
      });
    },
    clearSelection() {
      this.selectedOrders = [];
    },
    exportOrders() {
      this.exporting = true;
      // 这里可以实现导出功能
      setTimeout(() => {
        this.exporting = false;
        this.$message.success("导出功能开发中...");
      }, 1000);
    },
    calculateStats() {
      this.totalAmount = this.list.reduce(
        (sum, order) => sum + (order.totalAmount || 0),
        0,
      );
      this.pendingCount = this.list.filter(
        (order) => order.status === "PENDING" || order.status === "PAID",
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
        CANCELLED: "已取消",
        REFUND_REQUESTED: "退货申请中",
        REFUNDED: "已退款",
      };
      return map[s] || s;
    },
    load() {
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

.btn-view {
  color: #1890ff;
}

.btn-ship {
  color: #52c41a;
}

.btn-receive {
  color: #722ed1;
}

.btn-refund {
  color: #faad14;
}

.btn-cancel {
  color: #ff4d4f;
}

/* 批量操作栏 */
.batch-actions {
  background: #fff;
  padding: 16px 20px;
  margin-top: 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
}

.batch-info {
  color: #666;
  font-weight: 500;
}

/* 分页 */
.el-pagination {
  margin-top: 20px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .toolbar-row {
    flex-direction: column;
    align-items: stretch;
  }

  .stats-row {
    grid-template-columns: 1fr;
  }

  .stat-card {
    padding: 16px;
  }

  .batch-actions {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
