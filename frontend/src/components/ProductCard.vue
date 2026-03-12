<template>
  <el-card
    class="product-card card-hover"
    shadow="hover"
    @click.native="goDetail"
  >
    <div class="media">
      <img v-if="product.image" :src="product.image" class="img" alt="" />
      <div v-else class="img placeholder">暂无图片</div>
    </div>
    <div class="info">
      <div class="name" :title="product.name">{{ product.name }}</div>
      <div class="bottom">
        <div class="price">¥ {{ product.price }}</div>
        <el-button
          v-if="showCart"
          size="small"
          type="primary"
          @click.stop="addCart"
          >加购</el-button
        >
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
        .catch(() => this.$message.error("失败"));
    },
  },
};
</script>

<style scoped>
<style scoped > .product-card {
  cursor: pointer;
  margin-bottom: 16px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}
.product-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
}
.media {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(15, 23, 42, 0.1);
  background: rgba(255, 255, 255, 0.55);
}
.img {
  width: 100%;
  aspect-ratio: 4 / 3;
  object-fit: cover;
  display: block;
  transition: 0.3s;
}
.product-card:hover .img {
  transform: scale(1.08);
}
.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(15, 23, 42, 0.55);
}
.info {
  padding-top: 10px;
}
.name {
  font-size: 14px;
  font-weight: 800;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: rgba(15, 23, 42, 0.88);
}
.bottom {
  margin-top: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}
.price {
  color: #ef4444;
  font-weight: 900;
  letter-spacing: 0.2px;
}
.el-button.add-cart-btn {
  background: linear-gradient(135deg, #ff7a18, #ffb347);
  border: none;
  color: white;
  border-radius: 20px;
}
</style>
