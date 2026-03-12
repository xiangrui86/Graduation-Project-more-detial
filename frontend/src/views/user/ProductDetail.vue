<template>
  <div v-if="product" class="page-block detail">
    <div class="page-title">
      <h2>{{ product.name }}</h2>
      <span class="sub">¥ {{ product.price }}</span>
    </div>

    <el-row :gutter="18">
      <el-col :xs="24" :sm="10">
        <div class="media">
          <img
            v-if="product.image"
            :src="product.image"
            class="main-img"
            alt=""
          />
          <div v-else class="main-img placeholder">暂无图片</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="14">
        <div class="meta">
          <div class="price">¥ {{ product.price }}</div>
          <div class="desc">{{ product.description || "暂无描述" }}</div>

          <div class="buy">
            <div class="muted">数量</div>
            <el-input-number v-model="quantity" :min="1" :max="product.stock" />
          </div>

          <div class="actions">
            <el-button type="primary" @click="addCart">加入购物车</el-button>
            <el-button @click="addFavorite">收藏</el-button>
            <el-button @click="goChat" type="info">联系客服</el-button>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-divider>商品评价</el-divider>

    <div class="reviews">
      <div v-if="!reviews.length" class="muted">暂无评价</div>
      <div v-for="r in reviews" :key="r.id" class="review">
        <div class="who">用户 {{ r.userId }}</div>
        <div class="body">
          <el-rate v-model="r.rating" disabled />
          <div class="content">{{ r.content }}</div>
        </div>
      </div>
    </div>

    <el-button v-if="canReview" type="text" @click="showReviewDialog = true"
      >我要评价</el-button
    >
    <el-dialog title="评价" :visible.sync="showReviewDialog" width="420px">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="reviewForm.content" type="textarea" rows="4" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showReviewDialog = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getProduct, getReviews } from "@/api/pub";
import { addCart, addFavorite, addReview } from "@/api/user";

export default {
  name: "ProductDetail",
  data() {
    return {
      product: null,
      quantity: 1,
      reviews: [],
      showReviewDialog: false,
      reviewForm: { rating: 5, content: "" },
    };
  },
  computed: {
    canReview() {
      return this.$store.state.user;
    },
  },
  created() {
    const id = this.$route.params.id;
    getProduct(id).then((res) => {
      if (res.data) this.product = res.data;
    });
    getReviews(id).then((res) => {
      if (res.data && res.data.content) this.reviews = res.data.content;
      else if (Array.isArray(res.data)) this.reviews = res.data;
    });
  },
  methods: {
    addCart() {
      if (!this.$store.state.user) {
        this.$router.push("/login");
        return;
      }
      addCart({ productId: this.product.id, quantity: this.quantity }).then(
        (res) => {
          if (res.code === 200) this.$message.success("已加入购物车");
          else this.$message.error(res.message || "失败");
        },
      );
    },
    addFavorite() {
      if (!this.$store.state.user) {
        this.$router.push("/login");
        return;
      }
      addFavorite({ productId: this.product.id }).then((res) => {
        if (res.code === 200) this.$message.success("已收藏");
        else this.$message.error(res.message || "失败");
      });
    },
    goChat() {
      this.$router.push("/chat");
    },
    submitReview() {
      addReview({
        productId: this.product.id,
        rating: this.reviewForm.rating,
        content: this.reviewForm.content,
      }).then((res) => {
        if (res.code === 200) {
          this.$message.success("评价成功");
          this.showReviewDialog = false;
          this.reviews.unshift(res.data);
        }
      });
    },
  },
};
</script>

<style scoped>
.media {
  border-radius: 18px;
  overflow: hidden;
  border: 1px solid rgba(15, 23, 42, 0.1);
  background: rgba(255, 255, 255, 0.55);
}
.detail .main-img {
  width: 100%;
  aspect-ratio: 4 / 3;
  object-fit: cover;
  display: block;
}
.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(15, 23, 42, 0.55);
}
.meta {
  padding: 8px 0;
}
.price {
  font-size: 28px;
  font-weight: 900;
  color: #ef4444;
  letter-spacing: 0.2px;
}
.desc {
  color: rgba(15, 23, 42, 0.7);
  margin-top: 10px;
  line-height: 1.65;
}
.buy {
  margin-top: 14px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.actions {
  margin-top: 16px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
.reviews {
  display: grid;
  gap: 10px;
}
.review {
  padding: 12px;
  border-radius: 16px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  background: rgba(255, 255, 255, 0.6);
}
.who {
  font-weight: 800;
  color: rgba(15, 23, 42, 0.72);
  margin-bottom: 6px;
}
.body {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}
.content {
  color: rgba(15, 23, 42, 0.8);
  line-height: 1.55;
}
</style>
