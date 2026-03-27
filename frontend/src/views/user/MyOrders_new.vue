<template>
  <div class="orders-page">
    <div class="page-title">
      <h2>我的订单</h2>
      <span class="sub">订单管理中心 · 支付、收货、退款全流程</span>
    </div>

    <div class="orders-toolbar">
      <div class="status-tabs">
        <button
          v-for="item in statusOptions"
          :key="item.value"
          :class="['tab-btn', { active: statusFilter === item.value }]"
          @click="statusFilter = item.value"
        >
          {{ item.label }}
        </button>
      </div>
      <div class="toolbar-right">
        <el-input
          v-model.trim="keyword"
          placeholder="搜索订单号"
          clearable
          prefix-icon="el-icon-search"
          class="search-input"
        />
        <el-button icon="el-icon-refresh" @click="load">刷新</el-button>
      </div>
    </div>

    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-label">当前页订单</div>
        <div class="stat-value">{{ orders.length }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">待支付</div>
        <div class="stat-value warning">{{ statusCount.PENDING || 0 }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">待收货</div>
        <div class="stat-value primary">{{ statusCount.SHIPPED || 0 }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">已完成</div>
        <div class="stat-value success">{{ statusCount.RECEIVED || 0 }}</div>
      </div>
    </div>

    <!-- 骨架屏 -->
    <div v-if="loading" class="ske-list">
      <div v-for="i in 4" :key="i" class="ske-order">
        <div class="ske-order-head">
          <div class="ske ske-line" style="width:180px"></div>
          <div class="ske ske-line" style="width:80px"></div>
        </div>
        <div class="ske-order-body">
          <div class="ske ske-line" style="width:120px;margin-top:8px"></div>
          <div class="ske ske-line" style="width:220px;margin-top:8px"></div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && !filteredOrders.length" class="empty-state">
      <div class="empty-icon">📦</div>
      <div class="empty-title">{{ orders.length ? "没有匹配订单" : "还没有订单" }}</div>
      <div class="empty-desc">
        {{ orders.length ? "请调整筛选条件后重试" : "下单后，订单将显示在这里" }}
      </div>
      <router-link to="/products">
        <el-button type="primary" class="go-btn">去购物</el-button>
      </router-link>
    </div>

    <!-- 订单表格 -->
    <el-table
      v-if="!loading && filteredOrders.length"
      :data="filteredOrders"
      border
      stripe
      style="width: 100%"
      :header-cell-style="{ background: '#fafafa', color: '#333' }"
    >
      <el-table-column prop="orderNo" label="订单号" width="180" align="center">
        <template #default="scope">
          <span class="order-no">{{ scope.row.orderNo }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品信息" width="250">
        <template #default="scope">
          <div class="product-info">
            <img
              v-if="scope.row.items && scope.row.items[0] && scope.row.items[0].productImage"
              :src="scope.row.items[0].productImage"
              class="product-img"
              alt=""
            />
            <div v-else class="product-placeholder">
              <i class="el-icon-picture-outline"></i>
            </div>
            <div class="product-name">
              {{ scope.row.items && scope.row.items[0] ? scope.row.items[0].productName : '未知商品' }}
              <span v-if="scope.row.items && scope.row.items.length > 1"> 等{{ scope.row.items.length }}件</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="数量" width="80" align="center">
        <template #default="scope">
          {{ scope.row.items ? scope.row.items.reduce((sum, item) => sum + item.quantity, 0) : 0 }}
        </template>
      </el-table-column>
      <el-table-column label="总价" width="100" align="center">
        <template #default="scope">
          ¥{{ Number(scope.row.totalAmount).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column label="下单时间" width="160" align="center">
        <template #default="scope">
          {{ formatDate(scope.row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="收货信息" width="200">
        <template #default="scope">
          <div class="receiver-info">
            <div>{{ scope.row.receiverName }}</div>
            <div>{{ scope.row.receiverAddress }}</div>
            <div>{{ scope.row.receiverPhone }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ statusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center">
        <template #default="scope">
          <el-button
            v-if="scope.row.status === 'PENDING'"
            type="primary"
            size="small"
            @click="pay(scope.row.id)"
          >
            去支付
          </el-button>
          <el-button
            v-if="['PENDING', 'PAID', 'SHIPPED'].includes(scope.row.status)"
            size="small"
            @click="doCancelOrder(scope.row.id)"
          >
            取消订单
          </el-button>
          <el-button
            v-if="scope.row.status === 'SHIPPED'"
            size="small"
            @click="doConfirmReceive(scope.row.id)"
          >
            确认收货
          </el-button>
          <el-button
            v-if="scope.row.status === 'RECEIVED'"
            size="small"
            :loading="refundLoading && refundForm.orderId === scope.row.id"
            @click="openRefund(scope.row)"
          >
            申请退货
          </el-button>
          <el-button
            size="small"
            plain
            @click="viewDetail(scope.row.id)"
          >
            查看详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-if="total > pageSize"
      :current-page="page + 1"
      :page-size="pageSize"
      :total="total"
      layout="prev, pager, next"
      class="pagination"
      @current-change="(p) => { page = p - 1; load(); }"
    />

    <!-- 订单详情弹窗 -->
    <el-dialog
      title="订单详情"
      :visible.sync="detailVisible"
      width="560px"
      append-to-body
    >
      <div v-if="detail" class="detail-wrap">
        <!-- 状态 & 金额 -->
        <div class="detail-meta">
          <div class="detail-meta-item">
            <span class="dm-label">订单号</span>
            <span class="dm-value mono">{{ detail.order.orderNo }}</span>
          </div>
          <div class="detail-meta-item">
            <span class="dm-label">状态</span>
            <span :class="['status-chip', `status-${detail.order.status}`]">
              {{ statusText(detail.order.status) }}
            </span>
          </div>
          <div class="detail-meta-item">
            <span class="dm-label">合计</span>
            <span class="dm-value amount-highlight">¥{{ Number(detail.order.totalAmount).toFixed(2) }}</span>
          </div>
          <div class="detail-meta-item" v-if="detail.order.payAmount">
            <span class="dm-label">实付金额</span>
            <span class="dm-value">¥{{ Number(detail.order.payAmount).toFixed(2) }}</span>
          </div>
          <div class="detail-meta-item" v-if="detail.order.payMethod">
            <span class="dm-label">支付方式</span>
            <span class="dm-value">{{ payMethodText(detail.order.payMethod) }}</span>
          </div>
          <div class="detail-meta-item" v-if="detail.order.payTime">
            <span class="dm-label">支付时间</span>
            <span class="dm-value">{{ formatDate(detail.order.payTime) }}</span>
          </div>
          <div class="detail-meta-item" v-if="detail.order.receiverName">
            <span class="dm-label">收货人</span>
            <span class="dm-value">{{ detail.order.receiverName }} · {{ detail.order.receiverPhone }}</span>
          </div>
          <div class="detail-meta-item" v-if="detail.order.receiverAddress">
            <span class="dm-label">收货地址</span>
            <span class="dm-value">{{ detail.order.receiverAddress }}</span>
          </div>
          <div class="detail-meta-item" v-if="detail.order.refundReason">
            <span class="dm-label">退款原因</span>
            <span class="dm-value">{{ detail.order.refundReason }}</span>
          </div>
          <div class="detail-meta-item" v-if="detail.order.refundRequestTime">
            <span class="dm-label">退款申请时间</span>
            <span class="dm-value">{{ formatDate(detail.order.refundRequestTime) }}</span>
          </div>
        </div>

        <!-- 商品明细 -->
        <div class="detail-section-bar">
          <span class="detail-section-label">商品明细</span>
        </div>
        <div class="detail-items">
          <div
            v-for="item in detail.items"
            :key="item.id"
            class="detail-item-row"
          >
            <!-- 缩略图 -->
            <div class="item-thumb">
              <img v-if="item.productImage" :src="item.productImage" :alt="item.productName" />
              <div v-else class="item-thumb-placeholder"><i class="el-icon-picture-outline"></i></div>
            </div>
            <!-- 名称 + 单价 -->
            <div class="item-info">
              <div class="item-name">{{ item.productName }}</div>
              <div class="item-meta">单价 ¥{{ Number(item.price).toFixed(2) }}</div>
              <div v-if="item.refundReason" class="item-refund-reason">
                退款原因：{{ item.refundReason }}
              </div>
              <div v-if="item.refundRequestTime" class="item-refund-time">
                申请时间：{{ formatDate(item.refundRequestTime) }}
              </div>
            </div>
            <!-- 数量 -->
            <div class="item-qty">× {{ item.quantity }}</div>
            <!-- 小计 -->
            <div class="item-sub">¥{{ (Number(item.price) * item.quantity).toFixed(2) }}</div>
            <!-- 退款状态徽章 -->
            <div class="item-refund-col">
              <span v-if="item.refundStatus === 'REFUND_REQUESTED'" class="item-refund-chip chip-pending">
                <i class="el-icon-loading"></i> 退款申请中
              </span>
              <span v-else-if="item.refundStatus === 'REFUNDED'" class="item-refund-chip chip-done">
                <i class="el-icon-circle-check"></i> 已退款
              </span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 申请退货弹窗 -->
    <el-dialog
      title="申请退货"
      :visible.sync="refundVisible"
      width="680px"
      append-to-body
      class="refund-dialog"
    >
      <div class="refund-tip">
        <i class="el-icon-info-outline"></i>
        请选择需要退款的商品及数量，提交后将进入审核流程，审核通过后退款原路返回。
      </div>
      <el-form :model="refundForm" label-width="80px">
        <el-form-item label="退款商品" v-if="refundItems.length">
          <div class="refund-items-box">
            <div v-if="refundItems.length > 1" class="refund-items-head">
              <el-checkbox
                :indeterminate="refundItemsIndeterminate"
                :value="refundItemsAllSelected"
                @change="toggleAllRefundItems"
              >
                全选
              </el-checkbox>
              <span class="refund-select-tip">
                已选 {{ refundSelectedItemIds.length }}/{{ refundItems.length }} 件
              </span>
            </div>
            <el-checkbox-group v-model="refundSelectedItemIds" class="refund-item-group">
              <div
                v-for="it in refundItems"
                :key="it.id"
                class="refund-item-row"
              >
                <el-checkbox :label="it.id" class="refund-check" />
                <img
                  v-if="it.productImage"
                  :src="it.productImage"
                  class="refund-item-thumb"
                  alt=""
                />
                <div v-else class="refund-item-thumb placeholder">
                  <i class="el-icon-picture-outline"></i>
                </div>
                <div class="refund-item-info">
                  <div class="name">{{ it.productName }}</div>
                  <div class="meta">¥{{ Number(it.price).toFixed(2) }} × {{ it.quantity }}</div>
                </div>
                <el-input-number
                  v-model="refundItemQuantities[it.id]"
                  :min="1"
                  :max="it.quantity"
                  size="mini"
                  class="refund-qty-input"
                  :disabled="!refundSelectedItemIds.includes(it.id)"
                />
                <div class="refund-item-sub">
                  ¥{{ (Number(it.price) * (refundItemQuantities[it.id] || 0)).toFixed(2) }}
                </div>
              </div>
            </el-checkbox-group>
            <div class="refund-items-summary">
              <span>
                已选 <strong>{{ refundSelectedItemIds.length }}</strong> 件商品
              </span>
              <span>
                预计退款金额
                <strong class="summary-amount">¥{{ selectedRefundAmount.toFixed(2) }}</strong>
              </span>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="快捷原因">
          <div class="reason-tags">
            <button
              v-for="r in refundReasons"
              :key="r"
              type="button"
              :class="['reason-tag', { active: refundForm.reason === r }]"
              @click="refundForm.reason = r"
            >
              {{ r }}
            </button>
          </div>
        </el-form-item>
        <el-form-item label="退货原因">
          <el-input
            v-model="refundForm.reason"
            type="textarea"
            :rows="3"
            maxlength="200"
            show-word-limit
            placeholder="请描述退货原因（可选）"
          />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="refundVisible = false">取消</el-button>
        <el-button type="primary" :loading="refundLoading" :disabled="!canSubmitRefund" @click="submitRefund">
          提交申请
        </el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  getMyOrders,
  getOrderDetail,
  payOrder,
  cancelOrder,
  confirmReceive,
  batchRefundItems,
} from "@/api/user";

const orders = ref([])
const page = ref(0)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const detail = ref(null)
const detailVisible = ref(false)
const refundVisible = ref(false)
const refundLoading = ref(false)
const refundForm = ref({ orderId: null, reason: "" })
const refundItems = ref([])
const refundSelectedItemIds = ref([])
const refundItemQuantities = ref({})
const refundReasons = ref(["不想要了", "商品与描述不符", "尺寸/规格不合适", "收货后发现质量问题", "其他"])
const statusFilter = ref("ALL")
const keyword = ref("")
const statusOptions = ref([
  { label: "全部", value: "ALL" },
  { label: "待支付", value: "PENDING" },
  { label: "待发货", value: "PAID" },
  { label: "待收货", value: "SHIPPED" },
  { label: "已完成", value: "RECEIVED" },
  { label: "退款中", value: "REFUND_REQUESTED" },
  { label: "已退款", value: "REFUNDED" },
])

const filteredOrders = computed(() => {
  return orders.value.filter((o) => {
    const passStatus = statusFilter.value === "ALL" || o.status === statusFilter.value;
    const passKeyword = !keyword.value || String(o.orderNo || "").includes(keyword.value);
    return passStatus && passKeyword;
  });
})

const statusCount = computed(() => {
  return orders.value.reduce((acc, cur) => {
    const key = cur.status || "UNKNOWN";
    acc[key] = (acc[key] || 0) + 1;
    return acc;
  }, {});
})

const canSubmitRefund = computed(() => {
  return (
    !!refundForm.value.orderId &&
    normalizeReason(refundForm.value.reason).length >= 2 &&
    refundSelectedItemIds.value.length > 0 &&
    refundSelectedItemIds.value.every((id) => {
      const item = refundItems.value.find((it) => it.id === id);
      const qty = Number(refundItemQuantities.value[id] || 0);
      return item && qty >= 1 && qty <= item.quantity;
    })
  );
})

const refundItemsAllSelected = computed(() => {
  return refundItems.value.length > 0 && refundSelectedItemIds.value.length === refundItems.value.length;
})

const refundItemsIndeterminate = computed(() => {
  return refundSelectedItemIds.value.length > 0 && !refundItemsAllSelected.value;
})

const selectedRefundAmount = computed(() => {
  return refundSelectedItemIds.value.reduce((sum, id) => {
    const item = refundItems.value.find((it) => it.id === id);
    const qty = Number(refundItemQuantities.value[id] || 0);
    if (!item || qty <= 0) return sum;
    return sum + Number(item.price) * qty;
  }, 0);
})

const statusText = (s) => {
  const map = {
    PENDING: "待付款", PAID: "已付款", SHIPPED: "已发货",
    RECEIVED: "已完成", CANCELLED: "已取消",
    REFUND_REQUESTED: "退款中", REFUNDED: "已退款",
  };
  return map[s] || s;
}

const getStatusType = (s) => {
  const map = {
    PENDING: "warning",
    PAID: "primary",
    SHIPPED: "info",
    RECEIVED: "success",
    CANCELLED: "",
    REFUND_REQUESTED: "warning",
    REFUNDED: "danger",
  };
  return map[s] || "";
}

const payMethodText = (method) => {
  const map = {
    ALIPAY: "支付宝",
    WECHAT: "微信支付",
    CARD: "银行卡",
    COD: "货到付款",
  };
  return map[method] || method;
}

const normalizeReason = (reason) => {
  return String(reason || "").trim();
}

const formatDate = (str) => {
  if (!str) return "";
  const d = new Date(str);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")} ${String(d.getHours()).padStart(2, "0")}:${String(d.getMinutes()).padStart(2, "0")}`;
}

const load = () => {
  loading.value = true;
  getMyOrders({ page: page.value, size: pageSize.value })
    .then((res) => {
      if (res.data && res.data.content) {
        orders.value = res.data.content;
        total.value = res.data.totalElements || 0;
      } else {
        orders.value = [];
        total.value = 0;
      }
    })
    .catch(() => {
      console.error("订单加载失败，请稍后重试");
    })
    .finally(() => { loading.value = false; });
}

const pay = (id) => {
  payOrder(id).then((res) => {
    if (res.code === 200) { 
      console.log("支付成功（模拟支付）"); 
      load(); 
    }
    else console.error(res.message || "支付失败");
  });
}

const doConfirmReceive = (id) => {
  if (confirm("确认已收到货物？")) {
    confirmReceive(id).then((res) => {
      if (res.code === 200) { 
        console.log("已确认收货"); 
        load(); 
      }
    });
  }
}

const doCancelOrder = (id) => {
  if (confirm("订单在收货前可取消，是否确认取消？")) {
    cancelOrder(id).then((res) => {
      if (res.code === 200) {
        console.log("订单已取消");
        load();
      } else {
        console.error(res.message || "取消失败");
      }
    });
  }
}

const viewDetail = (id) => {
  getOrderDetail(id).then((res) => {
    if (res.data) {
      detail.value = res.data;
      detailVisible.value = true;
    }
  });
}

const openRefund = (row) => {
  getOrderDetail(row.id).then((res) => {
    const data = res.data;
    if (!data || !data.items) {
      console.error("订单详情加载失败");
      return;
    }
    const candidates = data.items.filter((it) => !it.refundStatus);
    if (!candidates.length) {
      console.warn("该订单暂无可退款商品");
      return;
    }
    refundItems.value = candidates;
    refundSelectedItemIds.value = candidates.length === 1 ? [candidates[0].id] : [];
    refundItemQuantities.value = candidates.reduce((acc, it) => {
      acc[it.id] = it.quantity;
      return acc;
    }, {});
    refundForm.value = { orderId: row.id, reason: "" };
    refundVisible.value = true;
  });
}

const toggleAllRefundItems = (checked) => {
  refundSelectedItemIds.value = checked ? refundItems.value.map((it) => it.id) : [];
}

const submitRefund = () => {
  const { orderId } = refundForm.value;
  const reason = normalizeReason(refundForm.value.reason);
  if (!orderId) return;
  if (!refundSelectedItemIds.value.length) {
    console.warn("请先选择要退款的商品");
    return;
  }
  if (reason.length < 2) {
    console.warn("请至少填写2个字的退货原因");
    return;
  }
  if (refundLoading.value) return;
  if (confirm("提交后将进入退款审核流程，是否确认提交？")) {
    refundLoading.value = true;
    const itemQuantities = refundSelectedItemIds.value.reduce((acc, id) => {
      acc[id] = Number(refundItemQuantities.value[id] || 1);
      return acc;
    }, {});
    batchRefundItems(orderId, { itemIds: refundSelectedItemIds.value, itemQuantities, reason })
      .then((res) => {
        if (res.code === 200) {
          console.log(`已提交 ${refundSelectedItemIds.value.length} 件商品退款申请`);
          refundVisible.value = false;
          refundItems.value = [];
          refundSelectedItemIds.value = [];
          refundItemQuantities.value = {};
          load();
        } else {
          console.error(res.message || "提交失败");
        }
      })
      .finally(() => { refundLoading.value = false; });
  }
}

onMounted(() => {
  load();
})
</script>

<style scoped>
.orders-page {
  min-height: calc(100vh - 140px);
  width: 100%;
  padding: 16px;
  border: 1px solid #ececec;
  border-radius: 12px;
  background: #fff;
}
.page-title {
  margin-bottom: 16px;
}
.page-title h2 {
  margin: 0 0 4px 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}
.page-title .sub {
  font-size: 14px;
  color: #999;
}

.orders-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}
.status-tabs {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.tab-btn {
  height: 34px;
  padding: 0 14px;
  border-radius: 999px;
  border: 1px solid #ebeef5;
  background: #fff;
  color: #666;
  cursor: pointer;
  transition: all .2s;
}
.tab-btn:hover { color: #ff6900; border-color: #ffd6bd; }
.tab-btn.active {
  color: #ff6900;
  border-color: #ffcfad;
  background: #fff4ed;
  font-weight: 700;
}
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 10px;
}
.search-input { width: 220px; }

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 12px;
}
.stat-card {
  border: 1px solid #f0f0f0;
  border-radius: 10px;
  padding: 12px 14px;
  background: #fafafa;
}
.stat-label { font-size: 12px; color: #999; margin-bottom: 4px; }
.stat-value { font-size: 22px; font-weight: 700; color: #333; }
.stat-value.primary { color: #409eff; }
.stat-value.success { color: #67c23a; }
.stat-value.warning { color: #e6a23c; }

/* ── 骨架屏 ── */
.ske-list { display: flex; flex-direction: column; gap: 12px; }
.ske-order {
  border-radius: var(--radius-md);
  border: 1px solid var(--border-2);
  background: var(--surface);
  overflow: hidden;
}
.ske-order-head {
  display: flex; justify-content: space-between; align-items: center;
  padding: 14px 18px; background: var(--surface-2);
  border-bottom: 1px solid var(--border-2);
}
.ske-order-body { padding: 14px 18px; }
.ske {
  background: linear-gradient(90deg, #e8eaf0 25%, #f5f6fa 50%, #e8eaf0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite;
  border-radius: 6px;
  height: 13px;
}
@keyframes shimmer {
  0%   { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* ── 空状态 ── */
.empty-state {
  display: flex; flex-direction: column; align-items: center;
  justify-content: center; padding: 80px 0; gap: 12px;
}
.empty-icon  { font-size: 60px; line-height: 1; }
.empty-title { font-size: 18px; font-weight: 700; color: var(--text); }
.empty-desc  { font-size: 14px; color: var(--muted); }
.go-btn { margin-top: 8px; padding: 10px 32px; border-radius: 999px; font-weight: 700; }

/* ── 表格样式 ── */
.product-info {
  display: flex;
  align-items: center;
  gap: 8px;
}
.product-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}
.product-placeholder {
  width: 60px;
  height: 60px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  color: #ccc;
}
.product-name {
  flex: 1;
  font-size: 14px;
  line-height: 1.4;
}
.receiver-info {
  font-size: 12px;
  line-height: 1.4;
  color: #666;
}
.order-no {
  font-family: monospace;
  font-weight: 600;
}

/* ── 分页 ── */
.pagination { margin-top: 16px; text-align: center; }

/* ── 详情弹窗 ── */
.detail-wrap {}
.detail-meta {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 20px;
}
.detail-meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
}
.dm-label {
  font-size: 14px;
  color: #666;
  min-width: 80px;
}
.dm-value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}
.amount-highlight {
  color: #ff6900;
  font-weight: 700;
}
.mono {
  font-family: monospace;
}
.status-chip {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}
.status-PENDING { background: #fdf6ec; color: #e6a23c; }
.status-PAID { background: #ecf5ff; color: #409eff; }
.status-SHIPPED { background: #f4f4f5; color: #909399; }
.status-RECEIVED { background: #f0f9ff; color: #67c23a; }
.status-CANCELLED { background: #fef0f0; color: #f56c6c; }
.status-REFUND_REQUESTED { background: #fdf6ec; color: #e6a23c; }
.status-REFUNDED { background: #fef0f0; color: #f56c6c; }

.detail-section-bar {
  margin: 20px 0 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}
.detail-section-label {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.detail-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.detail-item-row {
  display: grid;
  grid-template-columns: 60px 1fr 60px 80px 100px;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  background: #fafafa;
}
.item-thumb {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  overflow: hidden;
}
.item-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.item-thumb-placeholder {
  width: 100%;
  height: 100%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ccc;
}
.item-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.item-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
}
.item-meta {
  font-size: 12px;
  color: #999;
}
.item-refund-reason {
  font-size: 12px;
  color: #e6a23c;
}
.item-refund-time {
  font-size: 12px;
  color: #909399;
}
.item-qty {
  font-size: 14px;
  font-weight: 500;
  color: #666;
  text-align: center;
}
.item-sub {
  font-size: 14px;
  font-weight: 600;
  color: #ff6900;
  text-align: right;
}
.item-refund-col {
  text-align: center;
}
.item-refund-chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}
.chip-pending {
  background: #fdf6ec;
  color: #e6a23c;
}
.chip-done {
  background: #f0f9ff;
  color: #67c23a;
}

/* ── 退货弹窗 ── */
.refund-dialog :deep(.el-dialog__body) {
  padding: 20px 24px;
}
.refund-tip {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px 16px;
  margin-bottom: 20px;
  background: #f6f7fb;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  font-size: 14px;
  color: #666;
}
.refund-tip i {
  color: #409eff;
  font-size: 16px;
  margin-top: 2px;
}

.refund-items-box {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
}
.refund-items-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}
.refund-select-tip {
  font-size: 12px;
  color: #909399;
}
.refund-item-group {
  padding: 0;
}
.refund-item-row {
  display: grid;
  grid-template-columns: 20px 60px 1fr 100px 80px;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
}
.refund-item-row:last-child {
  border-bottom: none;
}
.refund-check {
  margin-right: 0;
}
.refund-item-thumb {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}
.refund-item-thumb.placeholder {
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ccc;
}
.refund-item-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.refund-item-info .name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
}
.refund-item-info .meta {
  font-size: 12px;
  color: #999;
}
.refund-qty-input {
  width: 80px;
}
.refund-item-sub {
  font-size: 14px;
  font-weight: 600;
  color: #ff6900;
  text-align: right;
}
.refund-items-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #f9fafc;
  border-top: 1px solid #e4e7ed;
  font-size: 14px;
  color: #666;
}
.summary-amount {
  color: #ff6900;
  font-size: 16px;
}

.reason-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.reason-tag {
  padding: 6px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #fff;
  color: #606266;
  cursor: pointer;
  transition: all .2s;
}
.reason-tag:hover {
  border-color: #c0c4cc;
}
.reason-tag.active {
  border-color: #ff6900;
  background: #fff4ed;
  color: #ff6900;
}

/* ── 响应式 ── */
@media (max-width: 768px) {
  .orders-page {
    padding: 12px;
  }
  .orders-toolbar {
    flex-direction: column;
    align-items: stretch;
  }
  .status-tabs {
    justify-content: center;
  }
  .toolbar-right {
    justify-content: center;
  }
  .search-input {
    width: 100%;
  }
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
  .product-info {
    flex-direction: column;
    text-align: center;
  }
  .product-img,
  .product-placeholder {
    width: 40px;
    height: 40px;
  }
  .receiver-info {
    text-align: center;
  }
  .detail-meta {
    grid-template-columns: 1fr;
  }
  .detail-item-row {
    grid-template-columns: 40px 1fr;
    gap: 8px;
  }
  .item-qty,
  .item-sub,
  .item-refund-col {
    grid-column: span 2;
    text-align: center;
  }
  .refund-item-row {
    grid-template-columns: 20px 40px 1fr;
    gap: 8px;
  }
  .refund-qty-input,
  .refund-item-sub {
    grid-column: span 3;
    text-align: center;
  }
}
</style>
    <div v-if="loading" class="ske-list">
      <div v-for="i in 4" :key="i" class="ske-order">
        <div class="ske-order-head">
          <div class="ske ske-line" style="width:180px"></div>
          <div class="ske ske-line" style="width:80px"></div>
        </div>
        <div class="ske-order-body">
          <div class="ske ske-line" style="width:120px"></div>
          <div class="ske ske-line" style="width:220px;margin-top:8px"></div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && !filteredOrders.length" class="empty-state">
      <div class="empty-icon">📦</div>
      <div class="empty-title">{{ orders.length ? "没有匹配订单" : "还没有订单" }}</div>
      <div class="empty-desc">
        {{ orders.length ? "请调整筛选条件后重试" : "下单后，订单将显示在这里" }}
      </div>
      <router-link to="/products">
        <el-button type="primary" class="go-btn">去购物</el-button>
      </router-link>
    </div>

    <!-- 订单表格 -->
    <el-table
      v-if="!loading && filteredOrders.length"
      :data="filteredOrders"
      border
      stripe
      style="width: 100%"
      :header-cell-style="{ background: '#fafafa', color: '#333' }"
    >
      <el-table-column prop="orderNo" label="订单号" width="180" align="center">
        <template #default="scope">
          <span class="order-no">{{ scope.row.orderNo }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品信息" width="250">
        <template #default="scope">
          <div class="product-info">
            <img
              v-if="scope.row.items && scope.row.items[0] && scope.row.items[0].productImage"
              :src="scope.row.items[0].productImage"
              class="product-img"
              alt=""
            />
            <div v-else class="product-placeholder">
              <i class="el-icon-picture-outline"></i>
            </div>
            <div class="product-name">
              {{ scope.row.items && scope.row.items[0] ? scope.row.items[0].productName : '未知商品' }}
              <span v-if="scope.row.items && scope.row.items.length > 1"> 等{{ scope.row.items.length }}件</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="数量" width="80" align="center">
        <template #default="scope">
          {{ scope.row.items ? scope.row.items.reduce((sum, item) => sum + item.quantity, 0) : 0 }}
        </template>
      </el-table-column>
      <el-table-column label="总价" width="100" align="center">
        <template #default="scope">
          ¥{{ Number(scope.row.totalAmount).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column label="下单时间" width="160" align="center">
        <template #default="scope">
          {{ formatDate(scope.row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="收货信息" width="200">
        <template #default="scope">
          <div class="receiver-info">
            <div>{{ scope.row.receiverName }}</div>
            <div>{{ scope.row.receiverAddress }}</div>
            <div>{{ scope.row.receiverPhone }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ statusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center">
        <template #default="scope">
          <el-button
            v-if="scope.row.status === 'PENDING'"
            type="primary"
            size="small"
            @click="pay(scope.row.id)"
          >
            去支付
          </el-button>
          <el-button
            v-if="['PENDING', 'PAID', 'SHIPPED'].includes(scope.row.status)"
            size="small"
            @click="doCancelOrder(scope.row.id)"
          >
            取消订单
          </el-button>
          <el-button
            v-if="scope.row.status === 'SHIPPED'"
            size="small"
            @click="doConfirmReceive(scope.row.id)"
          >
            确认收货
          </el-button>
          <el-button
            v-if="scope.row.status === 'RECEIVED'"
            size="small"
            :loading="refundLoading && refundForm.orderId === scope.row.id"
            @click="openRefund(scope.row)"
          >
            申请退货
          </el-button>
          <el-button
            size="small"
            plain
            @click="viewDetail(scope.row.id)"
          >
            查看详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-if="total > pageSize"
      :current-page="page + 1"
      :page-size="pageSize"
      :total="total"
      layout="prev, pager, next"
      class="pagination"
      @current-change="(p) => { page = p - 1; load(); }"
    />

    <!-- 订单详情弹窗 -->
    <el-dialog
      title="订单详情"
      :visible.sync="detailVisible"
      width="560px"
      append-to-body
    >
      <div v-if="detail" class="detail-wrap">
        <!-- 状态 & 金额 -->
        <div class="detail-meta">
          <div class="detail-meta-item">
            <span class="dm-label">订单号</span>
            <span class="dm-value mono">{{ detail.order.orderNo }}</span>
          </div>
          <div class="detail-meta-item">
            <span class="dm-label">状态</span>
            <span :class="['status-chip', `status-${detail.order.status}`]">
              {{ statusText(detail.order.status) }}
            </span>
          </div>
          <div class="detail-meta-item">
            <span class="dm-label">合计</span>
            <span class="dm-value amount-highlight">¥{{ Number(detail.order.totalAmount).toFixed(2) }}</span>
          </div>
          <div class="detail-meta-item" v-if="detail.order.payAmount">
            <span class="dm-label">实付金额</span>
            <span class="dm-value">¥{{ Number(detail.order.payAmount).toFixed(2) }}</span>
          </div>
          <div class="detail-meta-item" v-if="detail.order.payMethod">
            <span class="dm-label">支付方式</span>
            <span class="dm-value">{{ payMethodText(detail.order.payMethod) }}</span>
          </div>
          <div class="detail-meta-item" v-if="detail.order.payTime">
            <span class="dm-label">支付时间</span>
            <span class="dm-value">{{ formatDate(detail.order.payTime) }}</span>
          </div>
          <div class="detail-meta-item" v-if="detail.order.receiverName">
            <span class="dm-label">收货人</span>
            <span class="dm-value">{{ detail.order.receiverName }} · {{ detail.order.receiverPhone }}</span>
          </div>
          <div class="detail-meta-item" v-if="detail.order.receiverAddress">
            <span class="dm-label">收货地址</span>
            <span class="dm-value">{{ detail.order.receiverAddress }}</span>
          </div>
          <div class="detail-meta-item" v-if="detail.order.refundReason">
            <span class="dm-label">退款原因</span>
            <span class="dm-value">{{ detail.order.refundReason }}</span>
          </div>
          <div class="detail-meta-item" v-if="detail.order.refundRequestTime">
            <span class="dm-label">退款申请时间</span>
            <span class="dm-value">{{ formatDate(detail.order.refundRequestTime) }}</span>
          </div>
        </div>

        <!-- 商品明细 -->
        <div class="detail-section-bar">
          <span class="detail-section-label">商品明细</span>
        </div>
        <div class="detail-items">
          <div
            v-for="item in detail.items"
            :key="item.id"
            class="detail-item-row"
          >
            <!-- 缩略图 -->
            <div class="item-thumb">
              <img v-if="item.productImage" :src="item.productImage" :alt="item.productName" />
              <div v-else class="item-thumb-placeholder"><i class="el-icon-picture-outline"></i></div>
            </div>
            <!-- 名称 + 单价 -->
            <div class="item-info">
              <div class="item-name">{{ item.productName }}</div>
              <div class="item-meta">单价 ¥{{ Number(item.price).toFixed(2) }}</div>
              <div v-if="item.refundReason" class="item-refund-reason">
                退款原因：{{ item.refundReason }}
              </div>
              <div v-if="item.refundRequestTime" class="item-refund-time">
                申请时间：{{ formatDate(item.refundRequestTime) }}
              </div>
            </div>
            <!-- 数量 -->
            <div class="item-qty">× {{ item.quantity }}</div>
            <!-- 小计 -->
            <div class="item-sub">¥{{ (Number(item.price) * item.quantity).toFixed(2) }}</div>
            <!-- 退款状态徽章 -->
            <div class="item-refund-col">
              <span v-if="item.refundStatus === 'REFUND_REQUESTED'" class="item-refund-chip chip-pending">
                <i class="el-icon-loading"></i> 退款申请中
              </span>
              <span v-else-if="item.refundStatus === 'REFUNDED'" class="item-refund-chip chip-done">
                <i class="el-icon-circle-check"></i> 已退款
              </span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 申请退货弹窗 -->
    <el-dialog
      title="申请退货"
      :visible.sync="refundVisible"
      width="680px"
      append-to-body
      class="refund-dialog"
    >
      <div class="refund-tip">
        <i class="el-icon-info-outline"></i>
        请选择需要退款的商品及数量，提交后将进入审核流程，审核通过后退款原路返回。
      </div>
      <el-form :model="refundForm" label-width="80px">
        <el-form-item label="退款商品" v-if="refundItems.length">
          <div class="refund-items-box">
            <div v-if="refundItems.length > 1" class="refund-items-head">
              <el-checkbox
                :indeterminate="refundItemsIndeterminate"
                :value="refundItemsAllSelected"
                @change="toggleAllRefundItems"
              >
                全选
              </el-checkbox>
              <span class="refund-select-tip">
                已选 {{ refundSelectedItemIds.length }}/{{ refundItems.length }} 件
              </span>
            </div>
            <el-checkbox-group v-model="refundSelectedItemIds" class="refund-item-group">
              <div
                v-for="it in refundItems"
                :key="it.id"
                class="refund-item-row"
              >
                <el-checkbox :label="it.id" class="refund-check" />
                <img
                  v-if="it.productImage"
                  :src="it.productImage"
                  class="refund-item-thumb"
                  alt=""
                />
                <div v-else class="refund-item-thumb placeholder">
                  <i class="el-icon-picture-outline"></i>
                </div>
                <div class="refund-item-info">
                  <div class="name">{{ it.productName }}</div>
                  <div class="meta">¥{{ Number(it.price).toFixed(2) }} × {{ it.quantity }}</div>
                </div>
                <el-input-number
                  v-model="refundItemQuantities[it.id]"
                  :min="1"
                  :max="it.quantity"
                  size="mini"
                  class="refund-qty-input"
                  :disabled="!refundSelectedItemIds.includes(it.id)"
                />
                <div class="refund-item-sub">
                  ¥{{ (Number(it.price) * (refundItemQuantities[it.id] || 0)).toFixed(2) }}
                </div>
              </div>
            </el-checkbox-group>
            <div class="refund-items-summary">
              <span>
                已选 <strong>{{ refundSelectedItemIds.length }}</strong> 件商品
              </span>
              <span>
                预计退款金额
                <strong class="summary-amount">¥{{ selectedRefundAmount.toFixed(2) }}</strong>
              </span>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="快捷原因">
          <div class="reason-tags">
            <button
              v-for="r in refundReasons"
              :key="r"
              type="button"
              :class="['reason-tag', { active: refundForm.reason === r }]"
              @click="refundForm.reason = r"
            >
              {{ r }}
            </button>
          </div>
        </el-form-item>
        <el-form-item label="退货原因">
          <el-input
            v-model="refundForm.reason"
            type="textarea"
            :rows="3"
            maxlength="200"
            show-word-limit
            placeholder="请描述退货原因（可选）"
          />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="refundVisible = false">取消</el-button>
        <el-button type="primary" :loading="refundLoading" :disabled="!canSubmitRefund" @click="submitRefund">
          提交申请
        </el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  getMyOrders,
  getOrderDetail,
  payOrder,
  cancelOrder,
  confirmReceive,
  batchRefundItems,
} from "@/api/user";

const orders = ref([])
const page = ref(0)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const detail = ref(null)
const detailVisible = ref(false)
const refundVisible = ref(false)
const refundLoading = ref(false)
const refundForm = ref({ orderId: null, reason: "" })
const refundItems = ref([])
const refundSelectedItemIds = ref([])
const refundItemQuantities = ref({})
const refundReasons = ref(["不想要了", "商品与描述不符", "尺寸/规格不合适", "收货后发现质量问题", "其他"])
const statusFilter = ref("ALL")
const keyword = ref("")
const statusOptions = ref([
  { label: "全部", value: "ALL" },
  { label: "待支付", value: "PENDING" },
  { label: "待发货", value: "PAID" },
  { label: "待收货", value: "SHIPPED" },
  { label: "已完成", value: "RECEIVED" },
  { label: "退款中", value: "REFUND_REQUESTED" },
  { label: "已退款", value: "REFUNDED" },
])

const filteredOrders = computed(() => {
  return orders.value.filter((o) => {
    const passStatus = statusFilter.value === "ALL" || o.status === statusFilter.value;
    const passKeyword = !keyword.value || String(o.orderNo || "").includes(keyword.value);
    return passStatus && passKeyword;
  });
})

const statusCount = computed(() => {
  return orders.value.reduce((acc, cur) => {
    const key = cur.status || "UNKNOWN";
    acc[key] = (acc[key] || 0) + 1;
    return acc;
  }, {});
})

const canSubmitRefund = computed(() => {
  return (
    !!refundForm.value.orderId &&
    normalizeReason(refundForm.value.reason).length >= 2 &&
    refundSelectedItemIds.value.length > 0 &&
    refundSelectedItemIds.value.every((id) => {
      const item = refundItems.value.find((it) => it.id === id);
      const qty = Number(refundItemQuantities.value[id] || 0);
      return item && qty >= 1 && qty <= item.quantity;
    })
  );
})

const refundItemsAllSelected = computed(() => {
  return refundItems.value.length > 0 && refundSelectedItemIds.value.length === refundItems.value.length;
})

const refundItemsIndeterminate = computed(() => {
  return refundSelectedItemIds.value.length > 0 && !refundItemsAllSelected.value;
})

const selectedRefundAmount = computed(() => {
  return refundSelectedItemIds.value.reduce((sum, id) => {
    const item = refundItems.value.find((it) => it.id === id);
    const qty = Number(refundItemQuantities.value[id] || 0);
    if (!item || qty <= 0) return sum;
    return sum + Number(item.price) * qty;
  }, 0);
})

const statusText = (s) => {
  const map = {
    PENDING: "待付款", PAID: "已付款", SHIPPED: "已发货",
    RECEIVED: "已完成", CANCELLED: "已取消",
    REFUND_REQUESTED: "退款中", REFUNDED: "已退款",
  };
  return map[s] || s;
}

const getStatusType = (s) => {
  const map = {
    PENDING: "warning",
    PAID: "primary",
    SHIPPED: "info",
    RECEIVED: "success",
    CANCELLED: "",
    REFUND_REQUESTED: "warning",
    REFUNDED: "danger",
  };
  return map[s] || "";
}

const payMethodText = (method) => {
  const map = {
    ALIPAY: "支付宝",
    WECHAT: "微信支付",
    CARD: "银行卡",
    COD: "货到付款",
  };
  return map[method] || method;
}

const normalizeReason = (reason) => {
  return String(reason || "").trim();
}

const formatDate = (str) => {
  if (!str) return "";
  const d = new Date(str);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")} ${String(d.getHours()).padStart(2, "0")}:${String(d.getMinutes()).padStart(2, "0")}`;
}

const load = () => {
  loading.value = true;
  getMyOrders({ page: page.value, size: pageSize.value })
    .then((res) => {
      if (res.data && res.data.content) {
        orders.value = res.data.content;
        total.value = res.data.totalElements || 0;
      } else {
        orders.value = [];
        total.value = 0;
      }
    })
    .catch(() => {
      console.error("订单加载失败，请稍后重试");
    })
    .finally(() => { loading.value = false; });
}

const pay = (id) => {
  payOrder(id).then((res) => {
    if (res.code === 200) { 
      console.log("支付成功（模拟支付）"); 
      load(); 
    }
    else console.error(res.message || "支付失败");
  });
}

const doConfirmReceive = (id) => {
  if (confirm("确认已收到货物？")) {
    confirmReceive(id).then((res) => {
      if (res.code === 200) { 
        console.log("已确认收货"); 
        load(); 
      }
    });
  }
}

const doCancelOrder = (id) => {
  if (confirm("订单在收货前可取消，是否确认取消？")) {
    cancelOrder(id).then((res) => {
      if (res.code === 200) {
        console.log("订单已取消");
        load();
      } else {
        console.error(res.message || "取消失败");
      }
    });
  }
}

const viewDetail = (id) => {
  getOrderDetail(id).then((res) => {
    if (res.data) {
      detail.value = res.data;
      detailVisible.value = true;
    }
  });
}

const openRefund = (row) => {
  getOrderDetail(row.id).then((res) => {
    const data = res.data;
    if (!data || !data.items) {
      console.error("订单详情加载失败");
      return;
    }
    const candidates = data.items.filter((it) => !it.refundStatus);
    if (!candidates.length) {
      console.warn("该订单暂无可退款商品");
      return;
    }
    refundItems.value = candidates;
    refundSelectedItemIds.value = candidates.length === 1 ? [candidates[0].id] : [];
    refundItemQuantities.value = candidates.reduce((acc, it) => {
      acc[it.id] = it.quantity;
      return acc;
    }, {});
    refundForm.value = { orderId: row.id, reason: "" };
    refundVisible.value = true;
  });
}

const toggleAllRefundItems = (checked) => {
  refundSelectedItemIds.value = checked ? refundItems.value.map((it) => it.id) : [];
}

const submitRefund = () => {
  const { orderId } = refundForm.value;
  const reason = normalizeReason(refundForm.value.reason);
  if (!orderId) return;
  if (!refundSelectedItemIds.value.length) {
    console.warn("请先选择要退款的商品");
    return;
  }
  if (reason.length < 2) {
    console.warn("请至少填写2个字的退货原因");
    return;
  }
  if (refundLoading.value) return;
  if (confirm("提交后将进入退款审核流程，是否确认提交？")) {
    refundLoading.value = true;
    const itemQuantities = refundSelectedItemIds.value.reduce((acc, id) => {
      acc[id] = Number(refundItemQuantities.value[id] || 1);
      return acc;
    }, {});
    batchRefundItems(orderId, { itemIds: refundSelectedItemIds.value, itemQuantities, reason })
      .then((res) => {
        if (res.code === 200) {
          console.log(`已提交 ${refundSelectedItemIds.value.length} 件商品退款申请`);
          refundVisible.value = false;
          refundItems.value = [];
          refundSelectedItemIds.value = [];
          refundItemQuantities.value = {};
          load();
        } else {
          console.error(res.message || "提交失败");
        }
      })
      .finally(() => { refundLoading.value = false; });
  }
}

onMounted(() => {
  load();
})
</script>

<style scoped>
.orders-page {
  min-height: calc(100vh - 140px);
  width: 100%;
  padding: 16px;
  border: 1px solid #ececec;
  border-radius: 12px;
  background: #fff;
}
.orders-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}
.status-tabs {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.tab-btn {
  height: 34px;
  padding: 0 14px;
  border-radius: 999px;
  border: 1px solid #ebeef5;
  background: #fff;
  color: #666;
  cursor: pointer;
  transition: all .2s;
}
.tab-btn:hover { color: #ff6900; border-color: #ffd6bd; }
.tab-btn.active {
  color: #ff6900;
  border-color: #ffcfad;
  background: #fff4ed;
  font-weight: 700;
}
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 10px;
}
.search-input { width: 220px; }
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 12px;
}
.stat-card {
  border: 1px solid #f0f0f0;
  border-radius: 10px;
  padding: 12px 14px;
  background: #fafafa;
}
.stat-label { font-size: 12px; color: #999; margin-bottom: 4px; }
.stat-value { font-size: 22px; font-weight: 700; color: #333; }
.stat-value.primary { color: #409eff; }
.stat-value.success { color: #67c23a; }
.stat-value.warning { color: #e6a23c; }

/* ── 骨架屏 ── */
.ske-list { display: flex; flex-direction: column; gap: 12px; }
.ske-order {
  border-radius: var(--radius-md);
  border: 1px solid var(--border-2);
  background: var(--surface);
  overflow: hidden;
}
.ske-order-head {
  display: flex; justify-content: space-between; align-items: center;
  padding: 14px 18px; background: var(--surface-2);
  border-bottom: 1px solid var(--border-2);
}
.ske-order-body { padding: 14px 18px; }
.ske {
  background: linear-gradient(90deg, #e8eaf0 25%, #f5f6fa 50%, #e8eaf0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite;
  border-radius: 6px;
  height: 13px;
}
@keyframes shimmer {
  0%   { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* ── 空状态 ── */
.empty-state {
  display: flex; flex-direction: column; align-items: center;
  justify-content: center; padding: 80px 0; gap: 12px;
}
.empty-icon  { font-size: 60px; line-height: 1; }
.empty-title { font-size: 18px; font-weight: 700; color: var(--text); }
.empty-desc  { font-size: 14px; color: var(--muted); }
.go-btn { margin-top: 8px; padding: 10px 32px; border-radius: 999px; font-weight: 700; }

/* ── 表格样式 ── */
.product-info {
  display: flex;
  align-items: center;
  gap: 8px;
}
.product-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}
.product-placeholder {
  width: 60px;
  height: 60px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  color: #ccc;
}
.product-name {
  flex: 1;
  font-size: 14px;
  line-height: 1.4;
}
.receiver-info {
  font-size: 12px;
  line-height: 1.4;
  color: #666;
}
.order-no {
  font-family: monospace;
  font-weight: 600;
}

/* ── 分页 ── */
.pagination { margin-top: 16px; text-align: center; }

/* ── 详情弹窗 ── */
.detail-wrap {}
.detail-meta {
  display: grid;
  grid-template-columns: 1fr 1fr;