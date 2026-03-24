<template>
  <div class="page-block">
    <div class="page-title">
      <h2>购物车</h2>
      <span class="sub">调整数量后去结算</span>
    </div>

    <el-table :data="items" border>
      <el-table-column label="商品" width="200">
        <template slot-scope="scope">
          <div style="display: flex; align-items: center">
            <img
              v-if="productMap[scope.row.productId]"
              :src="productMap[scope.row.productId].image"
              alt="图片"
              style="
                width: 40px;
                height: 40px;
                object-fit: cover;
                margin-right: 8px;
              "
            />
            <span>{{
              (productMap[scope.row.productId] &&
                productMap[scope.row.productId].name) ||
              scope.row.productId
            }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="数量" width="120">
        <template slot-scope="scope">
          <el-input-number
            :value="scope.row.quantity"
            :min="1"
            size="small"
            @change="(v) => updateQty(scope.row.productId, v)"
            style="width: 90px; min-width: 90px"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="small"
            @click="remove(scope.row.productId)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <div v-if="items.length" class="summary">
      <div class="left">
        <span class="muted">商品总价</span>
        <span class="total">¥ {{ totalPrice.toFixed(2) }}</span>
      </div>
      <el-button type="primary" @click="showCheckout = true">去结算</el-button>
    </div>
    <p v-if="!items.length">
      购物车为空，<router-link to="/products">去逛逛</router-link>
    </p>

    <el-dialog
      title="确认订单"
      :visible.sync="showCheckout"
      width="500px"
      :style="{ maxHeight: '70vh', overflow: 'auto', zIndex: 3000 }"
      append-to-body
      class="custom-dialog"
    >
      <el-form :model="checkoutForm" label-width="100px">
        <el-form-item label="商品总价">
          <div class="checkout-total">¥ {{ totalPrice.toFixed(2) }}</div>
        </el-form-item>
        <el-form-item label="商家ID">
          <el-input-number
            v-model="checkoutForm.merchantId"
            :min="1"
            placeholder="从商品所属商家选一个"
          />
        </el-form-item>
        <el-form-item label="收货人">
          <el-input
            v-model="checkoutForm.receiverName"
            placeholder="收货人姓名"
          />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="checkoutForm.receiverPhone" placeholder="手机号" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input
            v-model="checkoutForm.receiverAddress"
            type="textarea"
            placeholder="详细地址"
          />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showCheckout = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitOrder"
          >提交订单</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getCart,
  updateCartQuantity,
  removeCart,
  createOrder,
} from "@/api/user";
import request from "@/api/request";

export default {
  name: "Cart",
  data() {
    return {
      items: [],
      productMap: {},
      showCheckout: false,
      submitting: false,
      checkoutForm: {
        merchantId: null,
        receiverName: "",
        receiverPhone: "",
        receiverAddress: "",
      },
    };
  },
  computed: {
    totalPrice() {
      return (this.items || []).reduce((sum, item) => {
        const p = this.productMap && this.productMap[item.productId];
        const unit = Number(p && p.price) || 0;
        const qty = Number(item.quantity) || 0;
        return sum + unit * qty;
      }, 0);
    },
  },
  created() {
    this.load();
  },
  methods: {
    load() {
      getCart().then((res) => {
        if (res.data) this.items = res.data;
        this.items.forEach((item) => {
          request.get(`/pub/products/${item.productId}`).then((r) => {
            if (r.data) this.$set(this.productMap, item.productId, r.data);
          });
        });
      });
    },
    updateQty(productId, v) {
      if (v < 1) return;
      updateCartQuantity({ productId, quantity: v }).then(() => this.load());
    },
    remove(productId) {
      removeCart(productId).then(() => this.load());
    },
    submitOrder() {
      const f = this.checkoutForm;
      if (
        !f.merchantId ||
        !f.receiverName ||
        !f.receiverPhone ||
        !f.receiverAddress
      ) {
        this.$message.warning("请填写完整收货信息及商家ID");
        return;
      }
      this.submitting = true;
      createOrder({
        merchantId: f.merchantId,
        receiverName: f.receiverName,
        receiverPhone: f.receiverPhone,
        receiverAddress: f.receiverAddress,
      })
        .then((res) => {
          this.submitting = false;
          if (res.code === 200) {
            this.$message.success("订单已创建，请到「我的订单」支付");
            this.showCheckout = false;
            this.load();
            this.$router.push("/orders");
          } else {
            this.$message.error(res.message || "下单失败");
          }
        })
        .catch(() => {
          this.submitting = false;
        });
    },
  },
};
</script>

<style scoped>
.summary{
  margin-top: 12px;
  padding: 12px 14px;
  border-radius: 16px;
  border: 1px solid rgba(15, 23, 42, .08);
  background: rgba(255,255,255,.60);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.summary .left{
  display: inline-flex;
  align-items: baseline;
  gap: 10px;
}
.total{
  font-weight: 900;
  font-size: 18px;
  color: #ef4444;
  letter-spacing: .2px;
}
.checkout-total{
  font-weight: 900;
  color: #ef4444;
  letter-spacing: .2px;
}

.custom-dialog >>> .el-dialog__wrapper {
  background: rgba(0, 0, 0, 0.2) !important;
  z-index: 3000 !important;
}
.custom-dialog >>> .el-dialog {
  z-index: 3001 !important;
}
</style>
