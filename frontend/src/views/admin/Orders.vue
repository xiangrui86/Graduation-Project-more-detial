<template>
  <div class="page-block">
    <div class="page-title">
      <h2>交易管理</h2>
      <span class="sub">订单状态与时间追踪</span>
    </div>

    <div class="toolbar-row">
      <el-input
        v-model="filters.orderNo"
        placeholder="订单号"
        clearable
        style="width: 220px; margin-right: 12px"
      />
      <el-input
        v-model="filters.userId"
        placeholder="用户ID"
        clearable
        style="width: 140px; margin-right: 12px"
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
      <el-button type="primary" @click="search">搜索</el-button>
      <el-button @click="resetFilters">重置</el-button>
    </div>

    <el-table :data="list" border>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="userId" label="用户ID" width="80" />
      <el-table-column prop="merchantId" label="商家ID" width="80" />
      <el-table-column prop="totalAmount" label="金额" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          {{ statusText(scope.row.status) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="small"
            @click="openDetailDialog(scope.row)"
            >查看</el-button
          >
          <el-button
            type="text"
            size="small"
            @click="handleUpdateStatus(scope.row, 'SHIPPED')"
            v-if="scope.row.status === 'PAID'"
            >发货</el-button
          >
          <el-button
            type="text"
            size="small"
            @click="handleUpdateStatus(scope.row, 'RECEIVED')"
            v-if="scope.row.status === 'SHIPPED'"
            >标记已收货</el-button
          >
          <el-button
            type="text"
            size="small"
            @click="handleUpdateStatus(scope.row, 'REFUNDED')"
            v-if="scope.row.status === 'REFUND_REQUESTED'"
            >退款完成</el-button
          >
          <el-button
            type="text"
            size="small"
            @click="handleUpdateStatus(scope.row, 'CANCELLED')"
            v-if="scope.row.status === 'PENDING'"
            >取消</el-button
          >
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="下单时间" />
    </el-table>

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
          <el-descriptions-item label="用户ID">{{
            detail.order.userId
          }}</el-descriptions-item>
          <el-descriptions-item label="商家ID">{{
            detail.order.merchantId
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
      filters: {
        orderNo: "",
        userId: "",
        status: "",
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
        userId: "",
        status: "",
      };
      this.page = 0;
      this.load();
    },
    refundItemStatusText(status) {
      const map = {
        REFUND_REQUESTED: "退款申请中",
        REFUNDED: "已退款",
      };
      return map[status] || status || "无";
    },
    openDetailDialog(row) {
      getOrderDetail(row.id).then((res) => {
        if (res.code === 200 && res.data) {
          this.detail.order = res.data.order;
          this.detail.items = res.data.items || [];
          this.detailDialogVisible = true;
        } else {
          this.$message.error(res.message || "获取订单详情失败");
        }
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
          updateOrderStatus(row.id, status).then((res) => {
            if (res.code === 200) {
              this.$message.success("操作成功");
              this.load();
            } else {
              this.$message.error(res.message || "操作失败");
            }
          });
        })
        .catch(() => {});
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
      const params = {
        page: this.page,
        size: 10,
      };
      if (this.filters.orderNo) params.orderNo = this.filters.orderNo;
      if (this.filters.status) params.status = this.filters.status;
      if (this.filters.userId) params.userId = this.filters.userId;
      getOrders(params).then((res) => {
        if (res.data && res.data.content) {
          this.list = res.data.content;
          this.total = res.data.totalElements || 0;
        }
      });
    },
  },
};
</script>
