<template>
  <div class="cart-page page-block">
    <!-- 标题 -->
    <div class="page-title">
      <h2>购物车</h2>
      <span class="sub" v-if="items.length">共 {{ items.length }} 件商品</span>
      <span class="sub" v-else>您的购物车空空如也</span>
    </div>

    <!-- 空购物车 -->
    <div v-if="!items.length && !loading" class="empty-cart">
      <div class="empty-icon">🛒</div>
      <div class="empty-title">购物车是空的</div>
      <div class="empty-desc">快去挑选心仪的运动装备吧～</div>
      <router-link to="/products">
        <el-button type="primary" class="go-shop-btn">去逛逛</el-button>
      </router-link>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-wrap">
      <div v-for="i in 3" :key="i" class="skeleton-row">
        <div class="ske ske-img"></div>
        <div class="ske-lines">
          <div class="ske ske-line" style="width: 55%"></div>
          <div class="ske ske-line" style="width: 35%; margin-top: 8px"></div>
        </div>
        <div class="ske ske-line" style="width: 80px; margin-left: auto"></div>
      </div>
    </div>

    <!-- 购物车列表 -->
    <div v-if="items.length" class="cart-list">
      <!-- 表头 -->
      <div class="cart-header">
        <span class="col-product">商品信息</span>
        <span class="col-price">单价</span>
        <span class="col-qty">数量</span>
        <span class="col-subtotal">小计</span>
        <span class="col-action"></span>
      </div>

      <!-- 每行商品 -->
      <div v-for="item in items" :key="item.productId" class="cart-row">
        <!-- 商品信息 -->
        <div class="col-product product-cell">
          <div class="product-thumb">
            <img
              v-if="
                productMap[item.productId] && productMap[item.productId].image
              "
              :src="productMap[item.productId].image"
              :alt="
                productMap[item.productId] && productMap[item.productId].name
              "
            />
            <div v-else class="thumb-placeholder">
              <i class="el-icon-picture-outline"></i>
            </div>
          </div>
          <div class="product-meta">
            <div class="product-name">
              {{
                (productMap[item.productId] &&
                  productMap[item.productId].name) ||
                `商品 #${item.productId}`
              }}
            </div>
            <div v-if="item.specValue" class="product-spec">
              <span>规格：{{ item.specValue }}</span>
            </div>
            <div
              class="product-tag"
              v-if="
                productMap[item.productId] && productMap[item.productId].isNew
              "
            >
              <el-tag size="mini" type="success">新品</el-tag>
            </div>
          </div>
        </div>

        <!-- 单价 -->
        <div class="col-price price-cell">
          <span class="unit-price">
            ¥{{
              productMap[item.productId]
                ? Number(productMap[item.productId].price).toFixed(2)
                : "--"
            }}
          </span>
          <span
            v-if="
              productMap[item.productId] &&
              productMap[item.productId].originalPrice >
                productMap[item.productId].price
            "
            class="original-price"
          >
            ¥{{ Number(productMap[item.productId].originalPrice).toFixed(2) }}
          </span>
        </div>

        <!-- 数量 -->
        <div class="col-qty qty-cell">
          <div class="qty-control">
            <button
              class="qty-btn"
              @click="updateQty(item.productId, item.quantity - 1)"
              :disabled="item.quantity <= 1"
            >
              −
            </button>
            <span class="qty-num">{{ item.quantity }}</span>
            <button
              class="qty-btn"
              @click="updateQty(item.productId, item.quantity + 1)"
            >
              +
            </button>
          </div>
        </div>

        <!-- 小计 -->
        <div class="col-subtotal subtotal-cell">
          ¥{{ (getPrice(item.productId) * item.quantity).toFixed(2) }}
        </div>

        <!-- 删除 -->
        <div class="col-action action-cell">
          <button
            class="delete-btn"
            @click="confirmRemove(item.productId)"
            title="移除"
          >
            <i class="el-icon-delete"></i>
          </button>
        </div>
      </div>
    </div>

    <!-- 结算栏 -->
    <div v-if="items.length" class="checkout-bar">
      <div class="bar-left">
        <router-link to="/products" class="continue-link">
          <i class="el-icon-arrow-left"></i> 继续购物
        </router-link>
      </div>
      <div class="bar-right">
        <div class="total-block">
          <span class="total-label">合计</span>
          <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
        </div>
        <el-button
          type="primary"
          class="checkout-btn"
          @click="showCheckout = true"
        >
          提交订单 ({{ items.length }})
        </el-button>
      </div>
    </div>

    <el-dialog
      title="确认订单"
      :visible.sync="showCheckout"
      width="520px"
      append-to-body
      class="checkout-dialog"
    >
      <!-- 订单摘要 -->
      <div class="order-summary">
        <div class="summary-row" v-for="item in items" :key="item.productId">
          <div class="summary-img">
            <img
              v-if="
                productMap[item.productId] && productMap[item.productId].image
              "
              :src="productMap[item.productId].image"
            />
          </div>
          <div class="summary-name">
            {{
              (productMap[item.productId] && productMap[item.productId].name) ||
              `#${item.productId}`
            }}
          </div>
          <div class="summary-qty">x{{ item.quantity }}</div>
          <div class="summary-sub">
            ¥{{ (getPrice(item.productId) * item.quantity).toFixed(2) }}
          </div>
        </div>
        <div class="summary-total-row">
          <span>商品合计</span>
          <span class="summary-total-price">¥{{ totalPrice.toFixed(2) }}</span>
        </div>
      </div>

      <!-- 收货信息 -->
      <div class="section-label">收货信息</div>
      <el-form :model="checkoutForm" label-width="90px" class="checkout-form">
        <el-form-item label="收货人">
          <el-input
            v-model="checkoutForm.receiverName"
            placeholder="请输入姓名"
          />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input
            v-model="checkoutForm.receiverPhone"
            placeholder="请输入手机号"
          />
        </el-form-item>
        <el-form-item label="收货地址">
          <el-input
            v-model="checkoutForm.receiverAddress"
            type="textarea"
            :rows="2"
            placeholder="省/市/区 + 详细地址"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="text" @click="useProfileAddress" icon="el-icon-document-copy">
            使用个人中心地址
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 支付方式 -->
      <div class="section-label">选择支付方式</div>
      <div class="payment-methods">
        <div
          class="payment-option"
          :class="{ active: checkoutForm.paymentMethod === 'WECHAT' }"
          @click="checkoutForm.paymentMethod = 'WECHAT'"
        >
          <div class="payment-icon">💚</div>
          <div class="payment-name">微信支付</div>
        </div>
        <div
          class="payment-option"
          :class="{ active: checkoutForm.paymentMethod === 'ALIPAY' }"
          @click="checkoutForm.paymentMethod = 'ALIPAY'"
        >
          <div class="payment-icon">💙</div>
          <div class="payment-name">支付宝</div>
        </div>
      </div>
      <div class="payment-tip">
        订单提交后请在 30 分钟内完成支付，超时未付款系统将自动取消订单。
      </div>

      <span slot="footer">
        <el-button @click="showCheckout = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitOrder">
          提交订单
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<style scoped>
.payment-methods {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.payment-tip {
  color: #718096;
  font-size: 12px;
  margin-bottom: 16px;
}

.payment-option {
  flex: 1;
  padding: 16px;
  border: 2px solid #ddd;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.payment-option:hover {
  border-color: #409eff;
  background-color: #f5f7fa;
}

.payment-option.active {
  border-color: #409eff;
  background-color: #e6f7ff;
}

.payment-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.payment-name {
  font-size: 14px;
  color: #333;
  font-weight: 600;
}

.section-label {
  margin: 16px 0 12px 0;
  font-size: 13px;
  font-weight: 600;
  color: #666;
}
</style>
  </div>
</template>

<script>
import {
  getCart,
  updateCartQuantity,
  removeCart,
  createOrder,
  getProfile,
} from "@/api/user";
import request from "@/api/request";

export default {
  name: "Cart",
  data() {
    return {
      items: [],
      productMap: {},
      loading: false,
      showCheckout: false,
      submitting: false,
      checkoutForm: {
        merchantId: null,
        receiverName: "",
        receiverPhone: "",
        receiverAddress: "",
        paymentMethod: "WECHAT",
      },
    };
  },
  computed: {
    totalPrice() {
      return (this.items || []).reduce((sum, item) => {
        return (
          sum + this.getPrice(item.productId) * (Number(item.quantity) || 0)
        );
      }, 0);
    },
  },
  created() {
    this.load();
  },
  methods: {
    useProfileAddress() {
      getProfile().then((res) => {
        if (res.code === 200 && res.data) {
          if (res.data.receiverName) {
            this.checkoutForm.receiverName = res.data.receiverName;
          }
          if (res.data.receiverPhone) {
            this.checkoutForm.receiverPhone = res.data.receiverPhone;
          }
          if (res.data.receiverAddress) {
            this.checkoutForm.receiverAddress = res.data.receiverAddress;
          }
          if (!res.data.receiverName || !res.data.receiverPhone || !res.data.receiverAddress) {
            this.$message.warning("个人中心地址信息不完整，请手动填写");
          } else {
            this.$message.success("已成功使用个人中心地址");
          }
        } else {
          this.$message.error("获取个人信息失败");
        }
      }).catch(() => {
        this.$message.error("获取个人信息失败");
      });
    },
    load() {
      this.loading = true;
      getCart()
        .then((res) => {
          if (res.data) this.items = res.data;
          const fetches = this.items.map((item) =>
            request.get(`/pub/products/${item.productId}`).then((r) => {
              if (r.data) this.$set(this.productMap, item.productId, r.data);
            }),
          );
          Promise.all(fetches).finally(() => {
            this.loading = false;
          });
          if (!this.items.length) this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    getPrice(productId) {
      const p = this.productMap[productId];
      return p ? Number(p.price) || 0 : 0;
    },
    updateQty(productId, v) {
      if (v < 1) return;
      const item = this.items.find(it => it.productId === productId);
      if (!item) return;

      // 乐观更新：先更新本地数据
      const oldQty = item.quantity;
      item.quantity = v;

      // 发送请求
      updateCartQuantity({ 
        productId, 
        specId: item.specId,
        quantity: v 
      }).then(() => {
        // 成功，不需要额外操作
      }).catch(() => {
        // 失败，回滚到旧数量
        item.quantity = oldQty;
        this.$message.error("更新数量失败，请重试");
      });
    },
    confirmRemove(productId) {
      const item = this.items.find(it => it.productId === productId);
      this.$confirm("确定移除该商品？", "提示", {
        type: "warning",
        confirmButtonText: "移除",
        cancelButtonText: "取消",
      })
        .then(() => {
          removeCart(productId).then(() => {
            this.$message.success("已移除");
            this.load();
          });
        })
        .catch(() => {});
    },
    submitOrder() {
      const f = this.checkoutForm;
      if (
        !f.receiverName ||
        !f.receiverPhone ||
        !f.receiverAddress
      ) {
        this.$message.warning("请填写完整收货信息");
        return;
      }
      if (!f.paymentMethod) {
        this.$message.warning("请选择支付方式");
        return;
      }
      this.submitting = true;
      createOrder({
        merchantId: f.merchantId || 1,
        receiverName: f.receiverName,
        receiverPhone: f.receiverPhone,
        receiverAddress: f.receiverAddress,
      })
        .then((res) => {
          this.submitting = false;
          if (res.code === 200 && res.data && res.data.id) {
            this.$message.success("订单已提交，请在 30 分钟内前往我的订单完成支付");
            this.showCheckout = false;
            this.load();
            this.$router.push("/orders");
          } else {
            this.$message.error(res.message || "下单失败，请重试");
          }
        })
        .catch(() => {
          this.submitting = false;
          this.$message.error("下单失败，请稍后重试");
        });
    },
  },
};
</script>

<style scoped>
.cart-page {
  min-height: 400px;
}

/* ── 空购物车 ── */
.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  gap: 12px;
}
.empty-icon {
  font-size: 64px;
  line-height: 1;
}
.empty-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text);
}
.empty-desc {
  font-size: 14px;
  color: var(--muted);
}
.go-shop-btn {
  margin-top: 8px;
  padding: 10px 32px;
  border-radius: 999px;
  font-weight: 700;
}

/* ── 骨架屏 ── */
.loading-wrap {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 8px 0;
}
.skeleton-row {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  background: var(--surface-2);
  border-radius: var(--radius-md);
}
.ske-lines {
  flex: 1;
}
.ske {
  background: linear-gradient(90deg, #e8eaf0 25%, #f5f6fa 50%, #e8eaf0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite;
  border-radius: 6px;
  height: 14px;
}
.ske-img {
  width: 56px;
  height: 56px;
  border-radius: 10px;
  flex-shrink: 0;
}
.ske-line {
  height: 13px;
}
@keyframes shimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

/* ── 列表表头 ── */
.cart-header {
  display: grid;
  grid-template-columns: 1fr 110px 130px 110px 48px;
  align-items: center;
  padding: 8px 16px;
  border-radius: var(--radius-sm);
  background: var(--surface-3);
  font-size: 12px;
  font-weight: 700;
  color: var(--muted);
  text-transform: uppercase;
  letter-spacing: 0.4px;
  margin-bottom: 8px;
}

/* ── 商品行 ── */
.cart-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}
.cart-row {
  display: grid;
  grid-template-columns: 1fr 110px 130px 110px 48px;
  align-items: center;
  padding: 14px 16px;
  border-radius: var(--radius-md);
  border: 1px solid var(--border-2);
  background: var(--surface);
  margin-bottom: 8px;
  transition: box-shadow 0.2s, border-color 0.2s;
}
.cart-row:hover {
  box-shadow: var(--shadow-sm);
  border-color: var(--border);
}

/* 商品信息列 */
.product-cell {
  display: flex;
  align-items: center;
  gap: 14px;
  min-width: 0;
}
.product-thumb {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  overflow: hidden;
  flex-shrink: 0;
  background: var(--surface-3);
  border: 1px solid var(--border-2);
}
.product-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.thumb-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: var(--muted);
  font-size: 22px;
}
.product-meta {
  min-width: 0;
}
.product-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 260px;
}
.product-spec {
  font-size: 12px;
  color: var(--muted);
  margin-top: 4px;
}
.product-tag {
  margin-top: 4px;
}

/* 单价列 */
.price-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.unit-price {
  font-size: 15px;
  font-weight: 700;
  color: var(--primary);
}
.original-price {
  font-size: 12px;
  color: var(--muted);
  text-decoration: line-through;
}

/* 数量列 */
.qty-cell {
}
.qty-control {
  display: inline-flex;
  align-items: center;
  border: 1.5px solid var(--border);
  border-radius: var(--radius-sm);
  overflow: hidden;
  background: var(--surface);
}
.qty-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  color: var(--text-2);
  transition: background 0.15s, color 0.15s;
  display: flex;
  align-items: center;
  justify-content: center;
}
.qty-btn:hover:not(:disabled) {
  background: var(--primary-light);
  color: var(--primary);
}
.qty-btn:disabled {
  opacity: 0.35;
  cursor: not-allowed;
}
.qty-num {
  min-width: 36px;
  text-align: center;
  font-size: 14px;
  font-weight: 700;
  color: var(--text);
  border-left: 1px solid var(--border);
  border-right: 1px solid var(--border);
  padding: 0 4px;
  line-height: 32px;
}

/* 小计列 */
.subtotal-cell {
  font-size: 15px;
  font-weight: 800;
  color: var(--primary);
}

/* 删除列 */
.action-cell {
  display: flex;
  justify-content: center;
}
.delete-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  color: var(--muted);
  cursor: pointer;
  border-radius: var(--radius-sm);
  font-size: 16px;
  transition: background 0.15s, color 0.15s;
  display: flex;
  align-items: center;
  justify-content: center;
}
.delete-btn:hover {
  background: #fef2f2;
  color: var(--danger);
}

/* ── 结算栏 ── */
.checkout-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 16px;
  padding: 16px 20px;
  background: var(--surface-2);
  border: 1px solid var(--border-2);
  border-radius: var(--radius-lg);
}
.continue-link {
  font-size: 14px;
  color: var(--text-2);
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.15s;
}
.continue-link:hover {
  color: var(--primary);
}
.bar-right {
  display: flex;
  align-items: center;
  gap: 24px;
}
.total-block {
  display: flex;
  align-items: baseline;
  gap: 10px;
}
.total-label {
  font-size: 14px;
  color: var(--text-2);
  font-weight: 500;
}
.total-price {
  font-size: 28px;
  font-weight: 900;
  color: var(--primary);
  letter-spacing: -0.5px;
}
.checkout-btn {
  height: 48px;
  padding: 0 32px;
  border-radius: 999px;
  font-size: 15px;
  font-weight: 700;
}

/* ── 结算弹窗 ── */
.order-summary {
  background: var(--surface-2);
  border: 1px solid var(--border-2);
  border-radius: var(--radius-md);
  padding: 12px 16px;
  margin-bottom: 20px;
}
.summary-row {
  display: grid;
  grid-template-columns: 40px 1fr auto auto;
  align-items: center;
  gap: 10px;
  padding: 6px 0;
  border-bottom: 1px solid var(--border-2);
}
.summary-row:last-of-type {
  border-bottom: none;
}
.summary-img {
  width: 36px;
  height: 36px;
  border-radius: 6px;
  overflow: hidden;
  background: var(--surface-3);
}
.summary-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.summary-name {
  font-size: 13px;
  color: var(--text);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.summary-qty {
  font-size: 13px;
  color: var(--muted);
  white-space: nowrap;
}
.summary-sub {
  font-size: 13px;
  font-weight: 700;
  color: var(--primary);
  white-space: nowrap;
}
.summary-total-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
  margin-top: 6px;
  border-top: 1.5px solid var(--border);
  font-size: 14px;
  font-weight: 700;
  color: var(--text-2);
}
.summary-total-price {
  font-size: 20px;
  font-weight: 900;
  color: var(--primary);
}

.section-label {
  font-size: 13px;
  font-weight: 700;
  color: var(--text-2);
  margin-bottom: 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.checkout-form {
  margin-bottom: 0;
}

/* ── 响应式 ── */
@media (max-width: 700px) {
  .cart-header {
    display: none;
  }
  .cart-row {
    grid-template-columns: 1fr auto;
    grid-template-rows: auto auto;
    gap: 10px;
  }
  .col-product {
    grid-column: 1 / -1;
  }
  .col-price {
    order: 2;
  }
  .col-qty {
    order: 3;
  }
  .col-subtotal {
    order: 4;
    font-size: 14px;
  }
  .col-action {
    order: 5;
  }
  .checkout-bar {
    flex-direction: column;
    gap: 14px;
  }
  .bar-right {
    width: 100%;
    justify-content: space-between;
  }
  .product-name {
    max-width: 200px;
  }
}
</style>
