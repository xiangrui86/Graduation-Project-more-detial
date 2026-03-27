<template>
  <el-card class="product-card" shadow="hover" @click.native="goDetail">
    <div class="product-image-wrapper">
      <img
        v-if="product.image"
        :src="product.image"
        class="product-image"
        alt=""
      />
      <div v-else class="product-placeholder">
        <i class="el-icon-picture-outline"></i>
        <span>暂无图片</span>
      </div>
      <div v-if="product.isNew" class="badge-new">新品</div>
      <div v-if="product.sales > 0" class="badge-hot">热销</div>
    </div>
    <div class="product-info">
      <div class="product-name" :title="product.name">{{ product.name }}</div>
      <div class="product-desc">{{ product.description || "暂无描述" }}</div>
      <div class="product-bottom">
        <div class="price-wrapper">
          <span class="price-symbol">¥</span>
          <span class="price-value">{{ product.price }}</span>
          <span
            v-if="product.originalPrice > product.price"
            class="original-price"
            >¥{{ product.originalPrice }}</span
          >
        </div>
        <div class="sales-info">已售 {{ product.sales || 0 }}</div>
      </div>
      <div v-if="showCart" class="action-bar">
        <button class="add-cart-btn" @click.stop="addCart">
          <i class="el-icon-shopping-cart-2"></i>
          <span>加入购物车</span>
        </button>
      </div>
    </div>
  </el-card>
</template>

<script>
import { addCart } from "@/api/user";

export default {
  name: "ProductCard",
  props: {
    product: { type: Object, required: true },
    showCart: { type: Boolean, default: true },
  },
  methods: {
    goDetail() {
      this.$router.push(`/products/${this.product.id}`);
    },
    addCart() {
      const user = this.$store.state.user;
      if (!user) {
        this.$router.push("/login");
        return;
      }
      addCart({ productId: this.product.id, quantity: 1 })
        .then((res) => {
          if (res.code === 200) this.$message.success("已加入购物车");
          else this.$message.error(res.message || "失败");
        })
        .catch(() => this.$message.error("操作失败"));
    },
  },
};
</script>

<style scoped>
.product-card {
  cursor: pointer;
  margin-bottom: 20px;
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid #ececec;
  transition: all 0.25s ease;
  background: #fff;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.12);
}

.product-image-wrapper {
  position: relative;
  width: 100%;
  padding-top: 75%;
  overflow: hidden;
  background: #f5f5f5;
}

.product-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: contain;
  padding: 12px;
  transition: transform 0.25s ease;
}

.product-card:hover .product-image {
  transform: scale(1.03);
}

.product-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
}

.product-placeholder i {
  font-size: 48px;
  margin-bottom: 8px;
}

.badge-new,
.badge-hot {
  position: absolute;
  top: 12px;
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 600;
  color: white;
  border-radius: 999px;
  z-index: 1;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.badge-new {
  left: 12px;
  background: #34c759;
}

.badge-hot {
  right: 12px;
  background: #ff6900;
}

.product-info {
  padding: 16px;
}

.product-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 6px;
}

.product-desc {
  font-size: 13px;
  color: #999;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 12px;
}

.product-bottom {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 12px;
}

.price-wrapper {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.price-symbol {
  font-size: 14px;
  color: #ff6900;
  font-weight: 600;
}

.price-value {
  font-size: 24px;
  color: #ff6900;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.original-price {
  font-size: 13px;
  color: #a0aec0;
  text-decoration: line-through;
  margin-left: 6px;
}

.sales-info {
  font-size: 12px;
  color: #999;
}

.action-bar {
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.add-cart-btn {
  width: 100%;
  padding: 10px 16px;
  background: #ff6900;
  border: 1px solid #ff6900;
  border-radius: 8px;
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: all 0.25s;
  box-shadow: none;
}

.add-cart-btn:hover {
  background: #ff7e29;
  border-color: #ff7e29;
}

.add-cart-btn:active {
  transform: translateY(0);
}

@media (max-width: 768px) {
  .product-info {
    padding: 12px;
  }

  .product-name {
    font-size: 14px;
  }

  .price-value {
    font-size: 20px;
  }
}
</style>
