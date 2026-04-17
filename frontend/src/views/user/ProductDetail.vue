<template>
  <div class="product-detail">
    <!-- 面包屑导航 -->
    <div class="breadcrumb-bar">
      <div class="breadcrumb-inner">
        <span class="bc-back" @click="$router.back()">
          <i class="el-icon-arrow-left"></i> 返回
        </span>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>商品详情</el-breadcrumb-item>
          <el-breadcrumb-item v-if="product">{{
            product.name
          }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

    <!-- 骨架屏 loading -->
    <div v-if="!product" class="skeleton-wrap">
      <div class="skeleton-left">
        <div class="skeleton-img skeleton-pulse"></div>
        <div class="skeleton-thumbs">
          <div
            class="skeleton-thumb skeleton-pulse"
            v-for="i in 4"
            :key="i"
          ></div>
        </div>
      </div>
      <div class="skeleton-right">
        <div
          class="skeleton-line skeleton-pulse"
          style="width: 80%; height: 28px"
        ></div>
        <div
          class="skeleton-line skeleton-pulse"
          style="width: 40%; height: 44px; margin-top: 16px"
        ></div>
        <div
          class="skeleton-line skeleton-pulse"
          style="width: 60%; height: 18px; margin-top: 16px"
        ></div>
        <div
          class="skeleton-line skeleton-pulse"
          style="width: 100%; height: 100px; margin-top: 24px"
        ></div>
        <div
          class="skeleton-line skeleton-pulse"
          style="width: 100%; height: 52px; margin-top: 32px"
        ></div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div v-if="product" class="main-layout">
      <!-- 左侧：图片区域 -->
      <div class="image-col">
        <div class="main-image-wrap">
          <img
            v-if="currentMainImage"
            :src="currentMainImage"
            :alt="product.name"
            class="main-image"
          />
          <div v-else class="image-placeholder">
            <i class="el-icon-picture-outline"></i>
            <span>暂无图片</span>
          </div>
          <span v-if="product.isNew" class="badge badge-new">新品</span>
          <span v-if="product.onSale === false" class="badge badge-off"
            >已下架</span
          >
        </div>
        <div class="thumbnails" v-if="product.images && product.images.length">
          <div
            v-for="(img, i) in product.images"
            :key="i"
            class="thumb"
            :class="{ active: activeImageIndex === i }"
            @click="selectThumbnail(i, img)"
          >
            <img :src="img" :alt="`图${i + 1}`" />
          </div>
        </div>
        <!-- 详情页轮播图 -->
        <div
          class="detail-images"
          v-if="detailImageList && detailImageList.length"
        >
          <div class="detail-images-label">详情图</div>
          <div class="detail-gallery">
            <div
              v-for="(img, i) in detailImageList"
              :key="`detail-${i}`"
              class="detail-thumb"
              :class="{ 'detail-thumb--selected': selectedDetailImage === img }"
              @click="selectDetailImage(img)"
            >
              <img :src="img" :alt="`详情图${i + 1}`" />
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：商品信息 -->
      <div class="info-col">
        <!-- 商品名称 -->
        <h1 class="product-name">{{ product.name }}</h1>

        <!-- 数据统计 -->
        <div class="stats-row">
          <span class="stat"
            ><i class="el-icon-sold-out"></i> 已售
            {{ product.sales || 0 }}</span
          >
          <span class="stat"
            ><i class="el-icon-box"></i> 库存 {{ product.stock }} 件</span
          >
        </div>

        <!-- 价格区域 -->
        <div class="price-box">
          <div class="price-label">价格</div>
          <div class="price-row">
            <span class="current-price"
              >¥{{ Number(product.price).toFixed(2) }}</span
            >
            <span
              v-if="
                product.originalPrice && product.originalPrice > product.price
              "
              class="original-price"
              >¥{{ Number(product.originalPrice).toFixed(2) }}</span
            >
            <span
              v-if="
                product.originalPrice && product.originalPrice > product.price
              "
              class="discount-badge"
            >
              {{
                Math.round((1 - product.price / product.originalPrice) * 100)
              }}% OFF
            </span>
          </div>
          <div
            v-if="
              product.originalPrice && product.originalPrice > product.price
            "
            class="save-tip"
          >
            节省 ¥{{
              (Number(product.originalPrice) - Number(product.price)).toFixed(2)
            }}
          </div>
        </div>

        <!-- 商品描述 -->
        <div class="desc-box" v-if="product.description">
          <div class="desc-label">描述</div>
          <div class="desc-text">{{ product.description }}</div>
        </div>

        <!-- 品牌与参数 -->
        <div class="brand-attr-box">
          <div v-if="product.brand" class="info-item">
            <span class="info-label">品牌：</span>
            <span class="info-value">{{ product.brand }}</span>
          </div>
          <div v-if="product.attributes" class="info-item">
            <span class="info-label">参数：</span>
            <span class="info-value" style="white-space: pre-line">{{
              product.attributes
            }}</span>
          </div>
        </div>

        <!-- 规格选择 -->
        <div class="spec-box" v-if="specs && specs.length">
          <div class="spec-label">规格</div>
          <div class="spec-options">
            <span
              v-for="spec in specs"
              :key="spec.id"
              class="spec-chip"
              :class="{ 'spec-chip--active': selectedSpec === spec.id }"
              @click="selectSpec(spec)"
            >
              <img v-if="spec.image" :src="spec.image" class="spec-icon" />
              <span>{{ spec.specValue }}</span>
              <em v-if="spec.priceDelta">
                +¥{{ Number(spec.priceDelta).toFixed(2) }}
              </em>
            </span>
          </div>
        </div>

        <!-- 数量选择 -->
        <div class="quantity-row">
          <div class="qty-label">数量</div>
          <el-input-number
            v-model="quantity"
            :min="1"
            :max="product.stock"
            class="qty-input"
          />
          <span class="stock-hint" v-if="product.stock <= 10"
            >仅剩 {{ product.stock }} 件</span
          >
        </div>

        <!-- 操作按钮 -->
        <div class="action-row">
          <el-button class="btn-favorite" @click="doAddFavorite">
            <i class="el-icon-star-on"></i> 收藏
          </el-button>
          <el-button class="btn-cart" @click="doAddCart">
            <i class="el-icon-shopping-cart-2"></i> 加入购物车
          </el-button>
          <el-button class="btn-buy" @click="doBuyNow">立即购买</el-button>
          <el-button type="info" class="btn-service" @click="goToContact">
            <i class="el-icon-phone-outline"></i> 联系客服
          </el-button>
        </div>

        <!-- 服务保障 -->
        <div class="guarantee-strip">
          <div class="guarantee-item">
            <i class="el-icon-circle-check"></i>
            <span>正品保障</span>
          </div>
          <div class="guarantee-item">
            <i class="el-icon-time"></i>
            <span>7天退换</span>
          </div>
          <div class="guarantee-item">
            <i class="el-icon-truck"></i>
            <span>极速发货</span>
          </div>
          <div class="guarantee-item">
            <i class="el-icon-service"></i>
            <span>专属客服</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 详情 / 评价 / 售后 标签页 -->
    <div v-if="product" class="tabs-section">
      <el-tabs v-model="activeTab" class="detail-tabs">
        <!-- 商品详情 -->
        <el-tab-pane label="商品详情" name="details">
          <div class="tab-body">
            <div
              v-if="product.detailDescription"
              class="detail-desc-content"
              v-html="product.detailDescription"
            ></div>
            <div v-else class="tab-empty">
              <i class="el-icon-document"></i>
              <p>暂无详情内容</p>
            </div>
          </div>
        </el-tab-pane>

        <!-- 售后服务 -->
        <el-tab-pane label="售后服务" name="service">
          <div class="tab-body">
            <div class="service-grid">
              <div class="service-card">
                <div class="service-icon">
                  <i class="el-icon-refresh-left"></i>
                </div>
                <div class="service-title">7天无理由退换</div>
                <div class="service-desc">
                  收货后7天内，商品保持完好即可退换
                </div>
              </div>
              <div class="service-card">
                <div class="service-icon"><i class="el-icon-truck"></i></div>
                <div class="service-title">快速配送</div>
                <div class="service-desc">默认快递，预计3-5个工作日送达</div>
              </div>
              <div class="service-card">
                <div class="service-icon"><i class="el-icon-medal"></i></div>
                <div class="service-title">15天质保</div>
                <div class="service-desc">质量问题15天内包退包换</div>
              </div>
              <div class="service-card">
                <div class="service-icon"><i class="el-icon-service"></i></div>
                <div class="service-title">专属客服</div>
                <div class="service-desc">工作日 9:00-18:00 在线服务</div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- 商品评价 -->
        <el-tab-pane label="商品评价" name="reviews">
          <div class="tab-body">
            <div v-if="reviewsLoading" class="loading-wrap">
              <el-skeleton :rows="3" animated></el-skeleton>
            </div>
            <div v-else-if="reviews.length === 0" class="tab-empty">
              <i class="el-icon-comment"></i>
              <p>暂无评价</p>
            </div>
            <div v-else class="reviews-list">
              <div
                v-for="review in reviews"
                :key="review.id"
                class="review-item"
              >
                <div class="review-header">
                  <div class="review-user-info">
                    <span class="review-nickname">{{ review.nickname }}</span>
                    <span class="review-date">{{
                      formatReviewDate(review.createdAt)
                    }}</span>
                  </div>
                  <el-rate
                    :value="review.rating"
                    disabled
                    colors="#ff9900"
                    class="review-rating"
                  ></el-rate>
                </div>
                <div class="review-content" v-html="review.content"></div>
              </div>
              <!-- 加载更多按钮 -->
              <div class="review-footer" v-if="totalReviews > reviews.length">
                <el-button
                  :loading="reviewsLoading"
                  @click="loadMoreReviews"
                  plain
                >
                  加载更多评价
                </el-button>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 详情图选择对话框 -->
    <el-dialog
      title="联系客服"
      :visible.sync="showContactDialog"
      width="520px"
      :close-on-click-modal="false"
      :append-to-body="true"
    >
      <div class="chat-panel">
        <div class="chat-msgs">
          <div v-if="chatLoading" class="chat-loading">
            <el-skeleton :rows="3" animated />
          </div>
          <div v-else-if="chatMessages.length === 0" class="chat-empty">
            <i class="el-icon-chat-line-round"></i>
            <p>暂无客服记录，提交问题后客服会尽快回复。</p>
          </div>
          <div v-else class="chat-list">
            <div
              v-for="msg in chatMessages"
              :key="msg.id"
              :class="[
                'chat-item',
                msg.senderRole === 'USER'
                  ? 'chat-item-user'
                  : 'chat-item-merchant',
              ]"
            >
              <div class="chat-label">
                {{ msg.senderRole === "USER" ? "我" : "商家回复" }}
              </div>
              <div class="chat-body">{{ msg.content }}</div>
              <div class="chat-time">{{ formatDate(msg.createdAt) }}</div>
            </div>
          </div>
        </div>
        <el-input
          type="textarea"
          :rows="4"
          v-model="contactMessage"
          placeholder="请输入您想咨询的问题"
        />
        <div class="dialog-footer">
          <el-button @click="showContactDialog = false">取消</el-button>
          <el-button type="primary" @click="sendContactMessage">发送</el-button>
        </div>
      </div>
    </el-dialog>

    <el-dialog
      title="选择商品款式"
      :visible.sync="showDetailImageDialog"
      width="600px"
      class="detail-image-dialog"
      center
    >
      <div class="dialog-content">
        <div class="dialog-text">请选择要购买的商品款式：</div>
        <div class="dialog-images">
          <div
            v-for="(img, index) in detailImageList"
            :key="`dialog-${index}`"
            class="dialog-image-item"
            :class="{
              'dialog-image-item--selected': selectedDetailImage === img,
            }"
            @click="selectedDetailImage = img"
          >
            <img :src="img" :alt="`款式${index + 1}`" />
            <div class="dialog-image-badge">款式{{ index + 1 }}</div>
          </div>
        </div>
      </div>
      <span slot="footer">
        <el-button @click="showDetailImageDialog = false">取消</el-button>
        <el-button
          type="primary"
          @click="confirmBuy"
          :disabled="!selectedDetailImage"
        >
          确认选择并购买
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getProduct } from "@/api/pub";
import {
  addCart,
  addFavorite,
  getUserChatList,
  sendUserChat,
} from "@/api/user";
import { getReviews } from "@/api/pub";

export default {
  name: "ProductDetail",
  data() {
    return {
      product: null,
      quantity: 1,
      specs: [],
      detailImageList: [],
      activeImageIndex: 0,
      selectedSpec: null,
      specPriceDelta: 0,
      activeTab: "details",
      selectedDetailImage: null,
      showDetailImageDialog: false,
      currentMainImage: null,
      reviews: [],
      reviewsLoading: false,
      reviewPage: 0,
      reviewPageSize: 10,
      totalReviews: 0,
      showContactDialog: false,
      contactMessage: "",
      chatMessages: [],
      chatLoading: false,
    };
  },

  created() {
    this.loadProductData(this.$route.params.id);
  },
  methods: {
    loadProductData(id) {
      getProduct(id).then((res) => {
        if (res.data) {
          this.product = res.data;
          // 初始化主图
          this.currentMainImage = res.data.image;
          // 解析详情图片
          if (res.data.detailImages) {
            this.detailImageList = res.data.detailImages
              .split(",")
              .filter((img) => img.trim());
          }
          // 加载评价
          this.loadReviews();
          // 加载商品客服聊天记录
          this.loadProductChat();
        }
      });
    },
    loadReviews() {
      if (!this.product) return;
      this.reviewsLoading = true;
      getReviews(this.product.id, this.reviewPage, this.reviewPageSize)
        .then((res) => {
          if (res.data) {
            if (this.reviewPage === 0) {
              // 第一页，替换评价列表
              this.reviews = res.data.content || [];
            } else {
              // 后续页，追加到列表
              this.reviews = this.reviews.concat(res.data.content || []);
            }
            this.totalReviews = res.data.totalElements || 0;
          }
          this.reviewsLoading = false;
        })
        .catch(() => {
          this.reviewsLoading = false;
        });
    },
    loadProductChat() {
      if (!this.$store.state.user || !this.product || !this.product.id) return;
      this.chatLoading = true;
      getUserChatList({ productId: this.product.id })
        .then((res) => {
          if (res.data) {
            this.chatMessages = res.data;
          }
        })
        .finally(() => {
          this.chatLoading = false;
        });
    },
    goToContact() {
      if (!this.$store.state.user) {
        this.$router.push("/login");
        return;
      }
      if (!this.product || !this.product.id) return;
      this.$router.push({ path: "/chat", query: { productId: this.product.id } });
    },
    sendContactMessage() {
      if (!this.contactMessage.trim()) {
        this.$message.warning("请输入消息内容");
        return;
      }
      sendUserChat({
        productId: this.product.id,
        content: this.contactMessage.trim(),
      })
        .then((res) => {
          if (res.code === 200) {
            this.chatMessages.push(res.data);
            this.contactMessage = "";
            this.$message.success("消息已发送，客服会尽快回复");
          } else {
            this.$message.error(res.message || "发送失败");
          }
        })
        .catch(() => {
          this.$message.error("发送失败");
        });
    },
    loadMoreReviews() {
      this.reviewPage++;
      this.loadReviews();
    },
    formatReviewDate(dateStr) {
      if (!dateStr) return "";
      const d = new Date(dateStr);
      const now = new Date();
      const diff = now - d;
      const days = Math.floor(diff / (1000 * 60 * 60 * 24));
      if (days === 0) return "今天";
      if (days === 1) return "昨天";
      if (days < 7) return `${days}天前`;
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(
        2,
        "0",
      )}-${String(d.getDate()).padStart(2, "0")}`;
    },
    selectSpec(spec) {
      this.selectedSpec = spec.id;
      this.specPriceDelta = spec.priceDelta || 0;
    },
    selectThumbnail(index, img) {
      // 点击缩略图时，更新索引和主图
      this.activeImageIndex = index;
      this.currentMainImage = img;
    },
    selectDetailImage(img) {
      // 点击详情图时，同时更新主图和选中状态
      this.currentMainImage = img;
      this.selectedDetailImage = img;
    },
    doAddCart() {
      if (!this.$store.state.user) {
        this.$router.push("/login");
        return;
      }
      addCart({
        productId: this.product.id,
        specId: this.selectedSpec,
        quantity: this.quantity,
      }).then((res) => {
        if (res.code === 200) this.$message.success("已加入购物车 🛒");
        else this.$message.error(res.message || "操作失败");
      });
    },
    doAddFavorite() {
      if (!this.$store.state.user) {
        this.$router.push("/login");
        return;
      }
      addFavorite({ productId: this.product.id }).then((res) => {
        if (res.code === 200) this.$message.success("已加入收藏 ⭐");
        else this.$message.error(res.message || "操作失败");
      });
    },
    doBuyNow() {
      if (!this.$store.state.user) {
        this.$router.push("/login");
        return;
      }
      // 如果有详情图，显示选择对话框
      if (this.detailImageList && this.detailImageList.length > 0) {
        this.selectedDetailImage = null;
        this.showDetailImageDialog = true;
      } else {
        this.confirmBuy();
      }
    },
    confirmBuy() {
      if (!this.$store.state.user) {
        this.$router.push("/login");
        return;
      }
      this.showDetailImageDialog = false;
      // 添加到购物车
      addCart({
        productId: this.product.id,
        specId: this.selectedSpec,
        quantity: this.quantity,
      }).then((res) => {
        if (res.code === 200) {
          // 添加成功，跳转到购物车进行结账
          this.$message.success("已加入购物车，跳转结账页面...");
          this.$router.push("/cart");
        } else {
          this.$message.error(res.message || "添加购物车失败");
        }
      });
    },
    formatDate(str) {
      if (!str) return "";
      const d = new Date(str);
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(
        2,
        "0",
      )}-${String(d.getDate()).padStart(2, "0")}`;
    },
  },
};
</script>

<style scoped>
/* ===== 整体 ===== */
.product-detail {
  min-height: 100vh;
  background: #f4f6fa;
  font-family: ui-sans-serif, system-ui, -apple-system, "Segoe UI", Roboto,
    "PingFang SC", "Microsoft YaHei", sans-serif;
}

/* ===== 面包屑 ===== */
.breadcrumb-bar {
  background: #fff;
  border-bottom: 1px solid #eef0f5;
}
.breadcrumb-inner {
  max-width: 1300px;
  margin: 0 auto;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
}
.bc-back {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: color 0.2s;
  white-space: nowrap;
}
.bc-back:hover {
  color: #ff6900;
}

/* ===== 骨架屏 ===== */
.skeleton-wrap {
  max-width: 1300px;
  margin: 28px auto;
  padding: 0 24px;
  display: grid;
  grid-template-columns: 500px 1fr;
  gap: 28px;
}
.skeleton-left {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.skeleton-img {
  width: 100%;
  height: 460px;
  border-radius: 16px;
}
.skeleton-thumbs {
  display: flex;
  gap: 10px;
}
.skeleton-thumb {
  width: 76px;
  height: 76px;
  border-radius: 8px;
}
.skeleton-right {
  display: flex;
  flex-direction: column;
  gap: 0;
}
.skeleton-line {
  border-radius: 8px;
}
.skeleton-pulse {
  background: linear-gradient(90deg, #e8eaf0 25%, #f5f6fa 50%, #e8eaf0 75%);
  background-size: 200% 100%;
  animation: pulse 1.4s infinite;
}
@keyframes pulse {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

/* ===== 主体两栏布局 ===== */
.main-layout {
  max-width: 1300px;
  margin: 28px auto 0;
  padding: 0 24px;
  display: grid;
  grid-template-columns: 500px 1fr;
  gap: 28px;
  align-items: start;
}

/* ===== 左侧图片列 ===== */
.image-col {
  position: sticky;
  top: 16px;
}
.main-image-wrap {
  position: relative;
  width: 100%;
  aspect-ratio: 1 / 1;
  border-radius: 20px;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
}
.main-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}
.main-image-wrap:hover .main-image {
  transform: scale(1.04);
}
.image-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #bbb;
  gap: 12px;
  font-size: 14px;
}
.image-placeholder i {
  font-size: 48px;
}
.badge {
  position: absolute;
  top: 16px;
  left: 16px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.4px;
}
.badge-new {
  background: #22c55e;
  color: #fff;
}
.badge-off {
  background: #94a3b8;
  color: #fff;
}
.thumbnails {
  display: flex;
  gap: 10px;
  margin-top: 14px;
  overflow-x: auto;
  padding-bottom: 4px;
}
.thumb {
  width: 76px;
  height: 76px;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: border-color 0.2s, box-shadow 0.2s;
  flex-shrink: 0;
  background: #fff;
}
.thumb.active {
  border-color: #ff6900;
  box-shadow: 0 0 0 3px rgba(255, 105, 0, 0.15);
}
.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 详情图片 */
.detail-images {
  margin-bottom: 12px;
  padding-top: 12px;
  border-top: 1px solid #e2e8f0;
}
.detail-images-label {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 600;
  margin-bottom: 8px;
}
.detail-gallery {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.detail-thumb {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  background: #f1f3f8;
  flex-shrink: 0;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.2s;
}
.detail-thumb:hover {
  border-color: #ff6900;
  box-shadow: 0 2px 8px rgba(255, 105, 0, 0.2);
  transform: translateY(-2px);
}
.detail-thumb--selected {
  border-color: #ff6900;
  box-shadow: 0 4px 12px rgba(255, 105, 0, 0.3);
}
.detail-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* ===== 右侧信息列 ===== */
.info-col {
  display: flex;
  flex-direction: column;
  gap: 0;
  background: #fff;
  border-radius: 20px;
  padding: 32px 36px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
}

.product-name {
  font-size: 22px;
  font-weight: 800;
  color: #0f172a;
  line-height: 1.45;
  margin: 0 0 14px;
}

/* 统计行 */
.stats-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f1f3f8;
}
.stat {
  font-size: 13px;
  color: #94a3b8;
  display: flex;
  align-items: center;
  gap: 4px;
}
.stat i {
  font-size: 14px;
}

/* 价格区域 */
.price-box {
  background: linear-gradient(135deg, #fff8f3 0%, #fff5ee 100%);
  border: 1px solid #fde8d8;
  border-radius: 14px;
  padding: 18px 22px;
  margin-bottom: 22px;
}
.price-label {
  font-size: 12px;
  color: #94a3b8;
  margin-bottom: 8px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.6px;
}
.price-row {
  display: flex;
  align-items: baseline;
  gap: 12px;
  flex-wrap: wrap;
}
.current-price {
  font-size: 38px;
  font-weight: 900;
  color: #ff6900;
  line-height: 1;
  letter-spacing: -0.5px;
}
.original-price {
  font-size: 18px;
  color: #94a3b8;
  text-decoration: line-through;
}
.discount-badge {
  background: #ff6900;
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  padding: 3px 10px;
  border-radius: 20px;
}
.save-tip {
  margin-top: 6px;
  font-size: 13px;
  color: #ef4444;
  font-weight: 600;
}

/* 描述 */
.desc-box {
  margin-bottom: 22px;
}
.desc-label {
  font-size: 13px;
  color: #94a3b8;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.6px;
  margin-bottom: 8px;
}
.desc-text {
  font-size: 15px;
  color: #475569;
  line-height: 1.7;
}

/* 规格 */
.spec-box {
  margin-bottom: 22px;
}
.spec-label {
  font-size: 13px;
  color: #94a3b8;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.6px;
  margin-bottom: 10px;
}
.spec-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.spec-chip {
  border: 1.5px solid #e2e8f0;
  border-radius: 8px;
  padding: 8px 18px;
  font-size: 14px;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s;
  background: #f8fafc;
}
.spec-chip:hover {
  border-color: #ff6900;
  color: #ff6900;
  background: #fff8f3;
}
.spec-chip--active {
  border-color: #ff6900;
  color: #ff6900;
  background: #fff4eb;
  font-weight: 700;
}
.spec-chip em {
  font-style: normal;
  font-size: 12px;
  display: block;
  margin-top: 2px;
}
.spec-icon {
  width: 20px;
  height: 20px;
  border-radius: 4px;
  margin-right: 6px;
  vertical-align: middle;
}
.spec-chip em {
  font-style: normal;
  margin-left: 4px;
  font-size: 12px;
}

/* 品牌和参数 */
.brand-attr-box {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 22px;
}
.info-item {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
  font-size: 14px;
}
.info-item:last-child {
  margin-bottom: 0;
}
.info-label {
  font-weight: 700;
  color: #475569;
  min-width: 60px;
}
.info-value {
  color: #64748b;
  flex: 1;
  line-height: 1.6;
}

/* 数量 */
.quantity-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 28px;
}
.qty-label {
  font-size: 13px;
  font-weight: 700;
  color: #334155;
  min-width: 36px;
}
.qty-input {
  width: 130px;
}
.stock-hint {
  font-size: 13px;
  color: #ef4444;
  font-weight: 600;
  background: #fef2f2;
  padding: 4px 10px;
  border-radius: 6px;
}

/* 按钮组 */
.action-row {
  display: grid;
  grid-template-columns: auto 1fr 1fr;
  gap: 12px;
  margin-bottom: 24px;
}
.btn-favorite,
.btn-cart,
.btn-buy {
  height: 52px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 700;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-favorite {
  background: #f8fafc;
  color: #475569;
  border: 1.5px solid #e2e8f0;
  padding: 0 18px;
  white-space: nowrap;
}
.btn-favorite:hover {
  background: #fff4eb;
  color: #ff6900;
  border-color: #ff6900;
}
.btn-cart {
  background: #fff4eb;
  color: #ff6900;
  border: 1.5px solid #ffd4b0;
}
.btn-cart:hover {
  background: #ffe8d3;
}
.btn-buy {
  background: linear-gradient(135deg, #ff6900 0%, #ff4d00 100%);
  color: #fff;
  box-shadow: 0 4px 16px rgba(255, 105, 0, 0.35);
}
.btn-buy:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(255, 105, 0, 0.45);
}
.btn-service {
  background: #eff6ff;
  color: #1d4ed8;
  border: 1.5px solid #bfdbfe;
  height: 52px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
.btn-service:hover {
  background: #dbeafe;
}
.chat-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.chat-msgs {
  max-height: 320px;
  overflow-y: auto;
  padding: 16px;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  background: #f8fafc;
}
.chat-empty {
  text-align: center;
  color: #64748b;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 36px 0;
}
.chat-item {
  margin-bottom: 14px;
  max-width: 80%;
  border-radius: 14px;
  padding: 12px 14px;
  line-height: 1.6;
  position: relative;
}
.chat-item-user {
  background: #2563eb;
  color: #fff;
  margin-left: auto;
}
.chat-item-merchant {
  background: #fff;
  color: #0f172a;
  border: 1px solid #e2e8f0;
}
.chat-label {
  font-size: 12px;
  color: inherit;
  margin-bottom: 4px;
  opacity: 0.85;
}
.chat-body {
  white-space: pre-wrap;
}
.chat-time {
  font-size: 12px;
  color: rgba(15, 23, 42, 0.55);
  margin-top: 8px;
}
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 服务保障 */
.guarantee-strip {
  display: flex;
  justify-content: space-around;
  padding: 18px 0 4px;
  border-top: 1px solid #f1f3f8;
}
.guarantee-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #64748b;
}
.guarantee-item i {
  font-size: 22px;
  color: #22c55e;
}

/* ===== 标签页 ===== */
.tabs-section {
  max-width: 1300px;
  margin: 24px auto 48px;
  padding: 0 24px;
}
.detail-tabs {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}
.detail-tabs >>> .el-tabs__header {
  border-bottom: 1px solid #f1f3f8;
  background: #fafbfd;
  margin: 0;
  padding: 0 24px;
}
.detail-tabs >>> .el-tabs__item {
  font-size: 15px;
  font-weight: 600;
  height: 56px;
  line-height: 56px;
  padding: 0 24px;
  color: #64748b;
  transition: color 0.2s;
}
.detail-tabs >>> .el-tabs__item.is-active {
  color: #ff6900;
  font-weight: 800;
}
.detail-tabs >>> .el-tabs__active-bar {
  background: #ff6900;
  height: 3px;
  border-radius: 2px;
}
.tab-badge {
  vertical-align: middle;
  margin-left: 4px;
}
.tab-body {
  padding: 32px 36px;
}
.html-content {
  line-height: 1.8;
  color: #334155;
}
.html-content img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 8px 0;
}
.detail-desc-content {
  line-height: 1.8;
  color: #334155;
  word-break: break-word;
  word-wrap: break-word;
}
.detail-desc-content img {
  max-width: 100%;
  width: 100%;
  height: auto;
  border-radius: 0;
  margin: 0;
  display: block;
  padding: 0;
}
.detail-desc-content p,
.detail-desc-content div {
  margin: 0;
  padding: 0;
}
.tab-empty {
  text-align: center;
  padding: 60px 0;
  color: #94a3b8;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}
.tab-empty i {
  font-size: 48px;
}
.tab-empty p {
  font-size: 14px;
  margin: 0;
}

/* 评分摘要 */
.rating-summary {
  display: flex;
  gap: 48px;
  align-items: center;
  padding: 24px 32px;
  background: #fafbfd;
  border-radius: 14px;
  margin-bottom: 28px;
}
.rating-score-block {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  min-width: 100px;
}
.big-score {
  font-size: 56px;
  font-weight: 900;
  color: #ff6900;
  line-height: 1;
}
.score-sub {
  font-size: 13px;
  color: #94a3b8;
}
.rating-bars {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.rating-bar-row {
  display: flex;
  align-items: center;
  gap: 12px;
}
.bar-label {
  width: 32px;
  font-size: 13px;
  color: #64748b;
  text-align: right;
}
.bar-track {
  flex: 1;
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}
.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #ff9900, #ff6900);
  border-radius: 4px;
  transition: width 0.5s ease;
}
.bar-count {
  width: 28px;
  font-size: 13px;
  color: #94a3b8;
}

/* 评价列表 */
.review-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}
.review-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  padding: 20px 22px;
  transition: box-shadow 0.2s;
}
.review-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.07);
  background: #fff;
}
.review-top {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}
.reviewer-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff6900, #ff4d00);
  color: #fff;
  font-size: 16px;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.reviewer-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.reviewer-name {
  font-size: 14px;
  font-weight: 700;
  color: #334155;
}
.review-date {
  font-size: 13px;
  color: #94a3b8;
  margin-left: auto;
}
.review-text {
  font-size: 14px;
  color: #475569;
  line-height: 1.7;
}
.review-imgs {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 12px;
}
.review-img {
  width: 76px;
  height: 76px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}
.review-write-wrap {
  text-align: center;
  padding-top: 8px;
}
.btn-write-review {
  height: 48px;
  padding: 0 36px;
  border-radius: 24px;
  font-size: 15px;
  font-weight: 700;
  background: linear-gradient(135deg, #ff6900, #ff4d00);
  border: none;
  box-shadow: 0 4px 14px rgba(255, 105, 0, 0.3);
}

/* 售后服务卡片 */
.service-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.service-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  padding: 24px 20px;
  text-align: center;
  transition: all 0.2s;
}
.service-card:hover {
  background: #fff;
  border-color: #ff6900;
  box-shadow: 0 4px 16px rgba(255, 105, 0, 0.1);
}
.service-icon {
  font-size: 32px;
  color: #ff6900;
  margin-bottom: 10px;
}
.service-title {
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 6px;
}
.service-desc {
  font-size: 13px;
  color: #64748b;
  line-height: 1.5;
}

/* 评价区域 */
.reviews-list {
  max-height: 800px;
  overflow-y: auto;
}

.review-item {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.2s;
}

.review-item:hover {
  background: #f9fafb;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.review-user-info {
  display: flex;
  gap: 12px;
  align-items: center;
}

.review-nickname {
  font-weight: 600;
  color: #0f172a;
  font-size: 14px;
}

.review-date {
  font-size: 12px;
  color: #999;
}

.review-rating {
  margin-right: 0;
}

.review-content {
  font-size: 14px;
  color: #475569;
  line-height: 1.6;
  word-break: break-word;
}

.review-content p {
  margin: 0 0 10px;
}

.review-content img {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 8px 0;
}

.review-footer {
  text-align: center;
  padding: 20px 0;
}

/* 评价弹窗 */
.review-dialog >>> .el-dialog__header {
  padding: 24px 28px 16px;
  font-weight: 800;
}
.review-dialog >>> .el-dialog__body {
  padding: 8px 28px;
}
.review-dialog >>> .el-dialog__footer {
  padding: 16px 28px 24px;
}

/* ===== 响应式 ===== */
@media (max-width: 1100px) {
  .main-layout {
    grid-template-columns: 420px 1fr;
  }
  .skeleton-wrap {
    grid-template-columns: 420px 1fr;
  }
}

@media (max-width: 900px) {
  .main-layout {
    grid-template-columns: 1fr;
    margin-top: 16px;
    padding: 0 16px;
  }
  .skeleton-wrap {
    grid-template-columns: 1fr;
    padding: 0 16px;
  }
  .image-col {
    position: static;
  }
  .main-image-wrap {
    aspect-ratio: 4/3;
  }
  .info-col {
    padding: 24px 22px;
  }
  .service-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .action-row {
    grid-template-columns: auto 1fr 1fr;
  }
  .tabs-section {
    padding: 0 16px;
  }
  .tab-body {
    padding: 24px 20px;
  }
  .rating-summary {
    flex-direction: column;
    gap: 20px;
  }
}

@media (max-width: 600px) {
  .product-name {
    font-size: 18px;
  }
  .current-price {
    font-size: 30px;
  }
  .action-row {
    grid-template-columns: 1fr 1fr;
    grid-template-rows: auto auto;
  }
  .btn-favorite {
    grid-column: 1 / -1;
  }
  .service-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .tabs-section {
    margin-bottom: 28px;
  }
  .detail-tabs >>> .el-tabs__item {
    font-size: 14px;
    padding: 0 16px;
  }
}

/* 详情图选择对话框 */
.detail-image-dialog >>> .el-dialog {
  border-radius: 16px;
}

.detail-image-dialog >>> .el-dialog__header {
  background: linear-gradient(135deg, #ff6900 0%, #ff8a3d 100%);
  padding: 20px 24px;
  border-radius: 16px 16px 0 0;
}

.detail-image-dialog >>> .el-dialog__title {
  color: #fff;
  font-weight: 700;
  font-size: 18px;
}

.detail-image-dialog >>> .el-dialog__close {
  color: #fff;
}

.dialog-content {
  padding: 28px 24px;
}

.dialog-text {
  font-size: 15px;
  color: #475569;
  font-weight: 600;
  margin-bottom: 20px;
}

.dialog-images {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.dialog-image-item {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  border: 3px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s;
}

.dialog-image-item:hover {
  border-color: #ff6900;
  box-shadow: 0 4px 16px rgba(255, 105, 0, 0.2);
  transform: translateY(-4px);
}

.dialog-image-item--selected {
  border-color: #ff6900;
  box-shadow: 0 6px 20px rgba(255, 105, 0, 0.3);
}

.dialog-image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.dialog-image-badge {
  position: absolute;
  bottom: 8px;
  left: 8px;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.detail-image-dialog >>> .el-dialog__footer {
  border-top: 1px solid #e2e8f0;
  padding: 16px 24px;
  text-align: right;
}
</style>
