<template>
  <div class="page-block">
    <div class="page-title">
      <h2>我的订单</h2>
      <span class="sub">支付、收货与查看明细</span>
    </div>

    <el-table :data="orders" border>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="totalAmount" label="金额" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          {{ statusText(scope.row.status) }}
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="下单时间" />
      <el-table-column label="操作" width="220">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.status === 'PENDING'"
            size="small"
            type="primary"
            @click="pay(scope.row.id)"
            >去支付</el-button
          >
          <el-button
            v-if="scope.row.status === 'SHIPPED'"
            size="small"
            @click="confirmReceive(scope.row.id)"
            >确认收货</el-button
          >
          <el-button size="small" @click="viewDetail(scope.row.id)"
            >详情</el-button
          >
        </template>
      </el-table-column>
    </el-table>
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
  </div>
</template>

<script>
import {
  getMyOrders,
  getOrderDetail,
  payOrder,
  confirmReceive,
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
      };
      return map[s] || s;
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
  },
};
</script>
