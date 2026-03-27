<template>
  <div class="home-page page-full-width">
    <!-- 顶部导航栏 -->
    <div class="top-nav">
      <div class="nav-container">
        <div class="logo">
          <h1>运动装备</h1>
        </div>
        <div class="search-bar">
          <div class="search-box">
            <el-input
              placeholder="搜索商品..."
              v-model="searchQuery"
              @keyup.enter="search"
              prefix-icon="el-icon-search"
              class="search-input"
              clearable
              @focus="showHotSearch = true"
              @blur="hideHotSearch"
            />
            <div class="hot-search" v-show="showHotSearch && !searchQuery">
              <div class="hot-title">
                <i class="el-icon-local-fire"></i>
                热门搜索
              </div>
              <div class="hot-list">
                <span
                  v-for="(item, index) in hotSearchList"
                  :key="index"
                  class="hot-item"
                  :class="{ 'hot-item-top': index < 3 }"
                  @click="hotSearch(item)"
                >
                  {{ item }}
                </span>
              </div>
            </div>
          </div>
          <el-button type="primary" class="search-btn" @click="search">
            <i class="el-icon-search"></i>
          </el-button>
        </div>
        <div class="user-actions">
          <el-button type="text" @click="$router.push('/cart')" class="nav-btn">
            <i class="el-icon-shopping-cart-2"></i>
            <span>购物车</span>
          </el-button>
          <el-button
            type="text"
            @click="$router.push('/favorite')"
            class="nav-btn"
          >
            <i class="el-icon-star-on"></i>
            <span>收藏</span>
          </el-button>
          <el-button
            type="text"
            @click="$router.push('/orders')"
            class="nav-btn"
          >
            <i class="el-icon-document"></i>
            <span>订单</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 商品轮播图区域 -->
    <div
      class="carousel-section"
      @mouseenter="pauseCarousel"
      @mouseleave="resumeCarousel"
    >
      <!-- 主轮播 -->
      <div
        class="carousel-track"
        :style="{ transform: `translateX(-${activeIndex * 100}%)` }"
      >
        <div
          class="carousel-slide"
          v-for="(product, index) in featuredProducts"
          :key="product.id"
          :class="`theme-${index % 5}`"
        >
          <!-- 背景装饰 -->
          <div class="slide-bg-decor">
            <svg class="decor-circle-1" viewBox="0 0 200 200">
              <circle cx="100" cy="100" r="100" fill="rgba(255,255,255,0.06)" />
            </svg>
            <svg class="decor-circle-2" viewBox="0 0 200 200">
              <circle cx="100" cy="100" r="100" fill="rgba(255,255,255,0.04)" />
            </svg>
            <svg class="decor-ring" viewBox="0 0 200 200">
              <circle
                cx="100"
                cy="100"
                r="90"
                fill="none"
                stroke="rgba(255,255,255,0.08)"
                stroke-width="20"
              />
            </svg>
          </div>
          <!-- 左侧文案 -->
          <div class="slide-left">
            <div class="slide-badge">
              <i class="el-icon-star-on"></i>
              <span>{{ product.categoryName || "热门推荐" }}</span>
            </div>
            <h2 class="slide-title">{{ product.name }}</h2>
            <p class="slide-desc">{{ product.description }}</p>
            <div class="slide-price-row">
              <div class="slide-price-box">
                <span class="price-label">优惠价</span>
                <span class="price-value">¥{{ product.price }}</span>
              </div>
              <div
                class="slide-origin-box"
                v-if="product.originalPrice > product.price"
              >
                <span class="origin-label">原价</span>
                <span class="origin-value">¥{{ product.originalPrice }}</span>
                <span class="discount-badge"
                  >省¥{{
                    (product.originalPrice - product.price).toFixed(0)
                  }}</span
                >
              </div>
            </div>
            <div class="slide-actions">
              <button class="btn-primary" @click="goToDetail(product.id)">
                <span>立即抢购</span>
                <i class="el-icon-arrow-right"></i>
              </button>
              <button class="btn-secondary" @click="addToCart(product)">
                <i class="el-icon-shopping-cart-2"></i>
                <span>加入购物车</span>
              </button>
            </div>
            <!-- 小特性标签 -->
            <div class="slide-tags">
              <span class="slide-tag-item"
                ><i class="el-icon-check"></i> 正品保证</span
              >
              <span class="slide-tag-item"
                ><i class="el-icon-check"></i> 极速发货</span
              >
              <span class="slide-tag-item"
                ><i class="el-icon-check"></i> 七天退换</span
              >
            </div>
          </div>
          <!-- 右侧图片 -->
          <div class="slide-right">
            <div class="img-frame">
              <div class="img-glow"></div>
              <img
                :src="product.image"
                :alt="product.name"
                class="slide-product-img"
                @click="goToDetail(product.id)"
              />
              <div class="img-reflection"></div>
            </div>
            <!-- 浮动销量卡片 -->
            <div class="float-card float-card-sales">
              <i class="el-icon-data-line"></i>
              <div>
                <div class="float-card-num">{{ product.sales || 0 }}+</div>
                <div class="float-card-label">月销量</div>
              </div>
            </div>
            <!-- 浮动评分卡片 -->
            <div class="float-card float-card-rate">
              <i class="el-icon-star-on"></i>
              <div>
                <div class="float-card-num">4.9</div>
                <div class="float-card-label">好评率</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 左右切换按钮 -->
      <button
        class="slide-btn slide-btn-prev"
        @click="prevSlide"
        :disabled="featuredProducts.length <= 1"
      >
        <i class="el-icon-arrow-left"></i>
      </button>
      <button
        class="slide-btn slide-btn-next"
        @click="nextSlide"
        :disabled="featuredProducts.length <= 1"
      >
        <i class="el-icon-arrow-right"></i>
      </button>

      <!-- 底部指示器 -->
      <div class="carousel-dots" v-if="featuredProducts.length > 1">
        <div
          v-for="(_, i) in featuredProducts"
          :key="i"
          class="dot-item"
          :class="{ active: activeIndex === i }"
          @click="goToSlide(i)"
        >
          <div
            class="dot-progress"
            v-if="activeIndex === i"
            :style="{ animationDuration: carouselInterval + 'ms' }"
          ></div>
        </div>
      </div>

      <!-- 缩略图条 -->
      <div class="carousel-thumbs" v-if="featuredProducts.length > 1">
        <div
          v-for="(product, i) in featuredProducts"
          :key="product.id"
          class="thumb-item"
          :class="{ active: activeIndex === i }"
          @click="goToSlide(i)"
        >
          <img :src="product.image" :alt="product.name" />
          <div class="thumb-overlay"></div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="content-container">
      <!-- 新品上架 -->
      <div class="products-section">
        <div class="section-header">
          <h2 class="section-title">新品上架</h2>
          <span class="section-sub">发现最新运动装备</span>
          <router-link to="/new" class="view-more">查看更多</router-link>
        </div>
        <el-row :gutter="20">
          <el-col
            v-for="p in newList"
            :key="p.id"
            :xs="12"
            :sm="12"
            :md="8"
            :lg="6"
          >
            <product-card :product="p" />
          </el-col>
        </el-row>
      </div>

      <!-- 销量排行 -->
      <div class="products-section">
        <div class="section-header">
          <h2 class="section-title">销量排行</h2>
          <span class="section-sub">热门商品精选</span>
          <router-link to="/rank" class="view-more">查看更多</router-link>
        </div>
        <el-row :gutter="20">
          <el-col
            v-for="p in rankList"
            :key="p.id"
            :xs="12"
            :sm="12"
            :md="8"
            :lg="6"
          >
            <product-card :product="p" />
          </el-col>
        </el-row>
      </div>

      <!-- 个性化推荐 -->
      <template v-if="user && recommendList.length">
        <div class="products-section">
          <div class="section-header">
            <h2 class="section-title">为你推荐</h2>
            <span class="section-sub">根据您的喜好推荐</span>
            <router-link to="/recommend" class="view-more"
              >查看更多</router-link
            >
          </div>
          <el-row :gutter="20">
            <el-col
              v-for="p in recommendList"
              :key="p.id"
              :xs="12"
              :sm="12"
              :md="8"
              :lg="6"
            >
              <product-card :product="p" />
            </el-col>
          </el-row>
        </div>
      </template>

      <!-- 品牌专区 -->
      <div class="brands-section">
        <div class="section-header">
          <h2 class="section-title">合作品牌</h2>
          <span class="section-sub">品质保证</span>
        </div>
        <div class="brands-container">
          <div class="brand-item" v-for="brand in brands" :key="brand.id">
            <img
              :src="brand.logo"
              :alt="brand.name"
              class="brand-logo"
              @error="handleBrandImageError($event, brand.name)"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 底部信息 -->
    <div class="footer">
      <div class="footer-benefits">
        <div class="benefit-item">
          <div class="benefit-icon">
            <i class="el-icon-check"></i>
          </div>
          <div class="benefit-content">
            <div class="benefit-title">7天无理由退换</div>
            <div class="benefit-desc">收货后7天内可无理由退换</div>
          </div>
        </div>
        <div class="benefit-item">
          <div class="benefit-icon">
            <i class="el-icon-truck"></i>
          </div>
          <div class="benefit-content">
            <div class="benefit-title">全场包邮</div>
            <div class="benefit-desc">订单金额满足条件即可免运费</div>
          </div>
        </div>
        <div class="benefit-item">
          <div class="benefit-icon">
            <i class="el-icon-circle-check"></i>
          </div>
          <div class="benefit-content">
            <div class="benefit-title">100%品质保证</div>
            <div class="benefit-desc">所有商品通过严格质量检测</div>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2026 运动装备电商平台. 保留所有权利.</p>
      </div>
    </div>
  </div>
</template>

<script>
import {
  getNewArrivals,
  getSalesRank,
  getRecommend,
  getAnnouncements,
  getCategories,
  getProducts,
} from "@/api/pub";
import { addCart } from "@/api/user";

export default {
  name: "Home",
  data() {
    return {
      searchQuery: "",
      showHotSearch: false,
      hotSearchList: [
        "跑步鞋",
        "篮球",
        "瑜伽垫",
        "哑铃",
        "羽毛球拍",
        "泳镜",
        "足球",
        "运动内衣",
      ],
      featuredProducts: [],
      activeIndex: 0,
      carouselInterval: 5000,
      carouselTimer: null,
      categories: [
        { id: 1, name: "足球用品", icon: "⚽" },
        { id: 2, name: "篮球用品", icon: "🏀" },
        { id: 3, name: "健身器材", icon: "💪" },
        { id: 4, name: "羽毛球", icon: "🏸" },
        { id: 5, name: "乒乓球", icon: "🏓" },
        { id: 6, name: "跑步装备", icon: "🏃" },
        { id: 7, name: "游泳装备", icon: "🏊" },
        { id: 8, name: "更多分类", icon: "📦" },
      ],
      newList: [],
      rankList: [],
      recommendList: [],
      announcements: [],
      brands: [
        {
          id: 1,
          name: "Nike",
          logo: "/images/brands/nike.png",
        },
        {
          id: 2,
          name: "Adidas",
          logo: "/images/brands/adidas.png",
        },
        {
          id: 3,
          name: "Puma",
          logo: "/images/brands/puma.png",
        },
        {
          id: 4,
          name: "Under Armour",
          logo: "/images/brands/ua.png",
        },
        {
          id: 5,
          name: "Reebok",
          logo: "/images/brands/reebok.png",
        },
        {
          id: 6,
          name: "Asics",
          logo: "/images/brands/asics.png",
        },
      ],
    };
  },
  computed: {
    user() {
      return this.$store.state.user;
    },
  },
  created() {
    this.loadData();
  },
  mounted() {
    this.startCarousel();
  },
  beforeDestroy() {
    this.stopCarousel();
  },
  methods: {
    loadData() {
      // 获取推荐商品作为轮播商品
      getProducts({ page: 0, size: 5 }).then((res) => {
        if (res.data && res.data.content) {
          this.featuredProducts = res.data.content;
          this.$nextTick(() => this.startCarousel());
        }
      });

      getNewArrivals(8).then((res) => {
        if (res.data) this.newList = res.data;
      });
      getSalesRank(8).then((res) => {
        if (res.data) this.rankList = res.data;
      });
      getAnnouncements(5).then((res) => {
        if (res.data) this.announcements = res.data;
      });
      getCategories(null).then((res) => {
        if (res.data && Array.isArray(res.data)) {
          this.categories = res.data.slice(0, 7).map((cat, index) => ({
            id: cat.id,
            name: cat.name,
            icon: this.categories[index]?.icon || "🏷️",
          }));
          // 添加更多分类选项
          this.categories.push({ id: 0, name: "更多分类", icon: "📦" });
        }
      });
      if (this.user && this.user.userId) {
        getRecommend(this.user.userId, 8).then((res) => {
          if (res.data) this.recommendList = res.data;
        });
      }
    },
    goToDetail(id) {
      this.$router.push(`/products/${id}`);
    },
    goToCategory(id) {
      if (id === 0) {
        this.$router.push("/products");
      } else {
        this.$router.push(`/category/${id}`);
      }
    },
    addToCart(product) {
      const user = this.$store.state.user;
      if (!user) {
        this.$router.push("/login");
        return;
      }
      addCart({ productId: product.id, quantity: 1 }).then((res) => {
        if (res.code === 200) {
          this.$message.success("已加入购物车");
        } else {
          this.$message.error(res.message || "操作失败");
        }
      });
    },
    startCarousel() {
      this.stopCarousel();
      if (this.featuredProducts.length > 1) {
        this.carouselTimer = setInterval(() => {
          this.nextSlide();
        }, this.carouselInterval);
      }
    },
    stopCarousel() {
      if (this.carouselTimer) {
        clearInterval(this.carouselTimer);
        this.carouselTimer = null;
      }
    },
    pauseCarousel() {
      this.stopCarousel();
    },
    resumeCarousel() {
      this.startCarousel();
    },
    nextSlide() {
      if (!this.featuredProducts.length) return;
      this.activeIndex = (this.activeIndex + 1) % this.featuredProducts.length;
    },
    prevSlide() {
      if (!this.featuredProducts.length) return;
      this.activeIndex =
        (this.activeIndex - 1 + this.featuredProducts.length) %
        this.featuredProducts.length;
    },
    goToSlide(i) {
      this.activeIndex = i;
      this.startCarousel();
    },
    hideHotSearch() {
      setTimeout(() => {
        this.showHotSearch = false;
      }, 200);
    },
    hotSearch(keyword) {
      this.searchQuery = keyword;
      this.showHotSearch = false;
      this.search();
    },
    handleBrandImageError(event, brandName) {
      // 图片加载失败时，显示品牌名称作为备选
      const img = event.target;
      img.style.display = "none";
      img.parentElement.style.background = "#f5f5f5";
      img.parentElement.style.display = "flex";
      img.parentElement.style.alignItems = "center";
      img.parentElement.style.justifyContent = "center";
      img.parentElement.style.minWidth = "100px";
      img.parentElement.style.minHeight = "50px";
      if (!img.parentElement.querySelector(".brand-name-fallback")) {
        const span = document.createElement("span");
        span.className = "brand-name-fallback";
        span.textContent = brandName;
        span.style.cssText = "font-size: 14px; font-weight: 600; color: #666;";
        img.parentElement.appendChild(span);
      }
    },
    search() {
      const query = this.searchQuery.trim();
      if (query) {
        this.$router.push({
          path: "/products",
          query: { search: query },
        });
      }
    },
  },
  components: {
    ProductCard: () => import("@/components/ProductCard.vue"),
  },
};
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #f5f5f5;
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏 */
.top-nav {
  background: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.nav-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 70px;
}

.logo h1 {
  font-size: 24px;
  font-weight: 800;
  color: #ff6900;
  margin: 0;
}

.search-bar {
  flex: 1;
  max-width: 600px;
  margin: 0 40px;
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.search-box {
  flex: 1;
  position: relative;
}

.search-input {
  width: 100%;
  border-radius: 25px;
  height: 42px;
}

.search-input >>> .el-input__inner {
  border-radius: 21px;
  padding-left: 40px;
  border: 2px solid #e8e8e8;
  transition: all 0.3s;
}

.search-input >>> .el-input__inner:focus {
  border-color: #ff6900;
  box-shadow: 0 0 0 3px rgba(255, 105, 0, 0.1);
}

.search-btn {
  width: 60px;
  height: 42px;
  border-radius: 21px;
  background: linear-gradient(135deg, #ff6900, #ff8533);
  border: none;
  transition: all 0.3s;
}

.search-btn:hover {
  background: linear-gradient(135deg, #ff8533, #ffa040);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 105, 0, 0.3);
}

.hot-search {
  position: absolute;
  top: 50px;
  left: 0;
  width: 100%;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  padding: 16px;
  z-index: 100;
}

.hot-title {
  font-size: 13px;
  color: #999;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.hot-title i {
  color: #ff6900;
}

.hot-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.hot-item {
  padding: 6px 14px;
  background: #f5f5f5;
  border-radius: 15px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.hot-item:hover {
  background: #fff3eb;
  color: #ff6900;
}

.hot-item-top {
  background: linear-gradient(135deg, #ff6900, #ff8533);
  color: white;
  font-weight: 600;
}

.hot-item-top:hover {
  background: linear-gradient(135deg, #ff8533, #ffa040);
  color: white;
}

.user-actions {
  display: flex;
  gap: 20px;
}

.nav-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  color: #333;
  transition: color 0.3s;
}

.nav-btn:hover {
  color: #ff6900;
}

.nav-btn i {
  font-size: 18px;
}

.nav-btn span {
  font-size: 12px;
}

/* 商品轮播图区域 */
.carousel-section {
  width: 100%;
  position: relative;
  margin-bottom: 40px;
  height: 520px;
  overflow: hidden;
  background: #1a1a2e;
  user-select: none;
}

/* 轮播轨道 - 水平排列所有幻灯片 */
.carousel-track {
  display: flex;
  height: 100%;
  transition: transform 0.7s cubic-bezier(0.77, 0, 0.175, 1);
  will-change: transform;
}

/* 单张幻灯片 */
.carousel-slide {
  flex: 0 0 100%;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  position: relative;
  overflow: hidden;
}

/* 五套主题渐变色 */
.theme-0 {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
}
.theme-1 {
  background: linear-gradient(135deg, #0d0d0d 0%, #1a0533 50%, #3d0066 100%);
}
.theme-2 {
  background: linear-gradient(135deg, #003322 0%, #004d1a 50%, #006600 100%);
}
.theme-3 {
  background: linear-gradient(135deg, #1a0000 0%, #330000 50%, #660000 100%);
}
.theme-4 {
  background: linear-gradient(135deg, #001a33 0%, #002952 50%, #003d7a 100%);
}

/* 背景装饰 SVG */
.slide-bg-decor {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}
.decor-circle-1 {
  position: absolute;
  width: 500px;
  height: 500px;
  right: -100px;
  top: -100px;
  animation: rotateSlow 30s linear infinite;
}
.decor-circle-2 {
  position: absolute;
  width: 300px;
  height: 300px;
  left: -50px;
  bottom: -80px;
  animation: rotateSlow 20s linear infinite reverse;
}
.decor-ring {
  position: absolute;
  width: 400px;
  height: 400px;
  right: 200px;
  top: 50%;
  transform: translateY(-50%);
  animation: rotateSlow 25s linear infinite;
}
@keyframes rotateSlow {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
@keyframes rotateSlow-center {
  from {
    transform: translateY(-50%) rotate(0deg);
  }
  to {
    transform: translateY(-50%) rotate(360deg);
  }
}

/* 左侧文案 */
.slide-left {
  flex: 0 0 50%;
  padding: 0 6% 0 8%;
  z-index: 2;
  position: relative;
  animation: fadeSlideIn 0.7s ease both;
}
@keyframes fadeSlideIn {
  from {
    opacity: 0;
    transform: translateX(-30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.slide-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 16px;
  background: rgba(255, 105, 0, 0.2);
  border: 1px solid rgba(255, 105, 0, 0.5);
  border-radius: 30px;
  color: #ff9a4d;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 20px;
  backdrop-filter: blur(6px);
}
.slide-badge i {
  font-size: 13px;
  color: #ff6900;
}

.slide-title {
  font-size: 38px;
  font-weight: 900;
  color: #ffffff;
  margin: 0 0 14px 0;
  line-height: 1.2;
  text-shadow: 0 2px 20px rgba(0, 0, 0, 0.5);
  letter-spacing: -0.5px;
}

.slide-desc {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0 0 24px 0;
  line-height: 1.7;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.slide-price-row {
  display: flex;
  align-items: flex-end;
  gap: 20px;
  margin-bottom: 28px;
  flex-wrap: wrap;
}

.slide-price-box {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.price-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  letter-spacing: 1px;
  text-transform: uppercase;
}
.price-value {
  font-size: 44px;
  font-weight: 900;
  color: #ff6900;
  line-height: 1;
  text-shadow: 0 0 20px rgba(255, 105, 0, 0.5);
}

.slide-origin-box {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-self: flex-end;
  padding-bottom: 6px;
}
.origin-label {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.4);
}
.origin-value {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.4);
  text-decoration: line-through;
}
.discount-badge {
  padding: 2px 8px;
  background: rgba(255, 51, 51, 0.8);
  border-radius: 4px;
  font-size: 11px;
  font-weight: 700;
  color: white;
  width: fit-content;
}

.slide-actions {
  display: flex;
  gap: 14px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.btn-primary {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 32px;
  background: linear-gradient(135deg, #ff6900, #ff8c38);
  border: none;
  border-radius: 50px;
  color: white;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 8px 24px rgba(255, 105, 0, 0.45);
  letter-spacing: 0.5px;
}
.btn-primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 32px rgba(255, 105, 0, 0.6);
  background: linear-gradient(135deg, #ff7a1a, #ffa050);
}
.btn-primary i {
  font-size: 14px;
  transition: transform 0.3s;
}
.btn-primary:hover i {
  transform: translateX(4px);
}

.btn-secondary {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 28px;
  background: rgba(255, 255, 255, 0.1);
  border: 1.5px solid rgba(255, 255, 255, 0.3);
  border-radius: 50px;
  color: white;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  backdrop-filter: blur(10px);
}
.btn-secondary:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.6);
  transform: translateY(-2px);
}

.slide-tags {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}
.slide-tag-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: rgba(255, 255, 255, 0.55);
  font-size: 12px;
}
.slide-tag-item i {
  color: #4ade80;
  font-size: 12px;
}

/* 右侧图片 */
.slide-right {
  flex: 0 0 50%;
  height: 100%;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2;
}

.img-frame {
  position: relative;
  width: 380px;
  height: 380px;
  animation: floatUp 3s ease-in-out infinite alternate;
}
@keyframes floatUp {
  from {
    transform: translateY(0px);
  }
  to {
    transform: translateY(-16px);
  }
}

.img-glow {
  position: absolute;
  inset: 10%;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 105, 0, 0.35), transparent 70%);
  filter: blur(30px);
  z-index: 0;
}

.slide-product-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  position: relative;
  z-index: 1;
  cursor: pointer;
  filter: drop-shadow(0 20px 40px rgba(0, 0, 0, 0.5));
  transition: transform 0.4s ease;
}
.slide-product-img:hover {
  transform: scale(1.06);
}

.img-reflection {
  position: absolute;
  bottom: -20px;
  left: 10%;
  width: 80%;
  height: 60px;
  background: radial-gradient(
    ellipse at center,
    rgba(255, 105, 0, 0.2) 0%,
    transparent 70%
  );
  filter: blur(10px);
  z-index: 0;
}

/* 浮动卡片 */
.float-card {
  position: absolute;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 18px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 14px;
  color: white;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
  z-index: 10;
  animation: floatCard 4s ease-in-out infinite alternate;
}
@keyframes floatCard {
  from {
    transform: translateY(0px);
  }
  to {
    transform: translateY(-8px);
  }
}
.float-card i {
  font-size: 24px;
  color: #ff6900;
}
.float-card-num {
  font-size: 18px;
  font-weight: 800;
  line-height: 1;
}
.float-card-label {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.6);
  margin-top: 2px;
}
.float-card-sales {
  top: 20%;
  right: 8%;
  animation-delay: 0s;
}
.float-card-rate {
  bottom: 22%;
  right: 12%;
  animation-delay: 1s;
}

/* 左右切换按钮 */
.slide-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.1);
  border: 1.5px solid rgba(255, 255, 255, 0.25);
  border-radius: 50%;
  color: white;
  font-size: 18px;
  cursor: pointer;
  z-index: 20;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  transition: all 0.3s;
  opacity: 0;
}
.carousel-section:hover .slide-btn {
  opacity: 1;
}
.slide-btn:hover {
  background: rgba(255, 105, 0, 0.6);
  border-color: #ff6900;
  transform: translateY(-50%) scale(1.1);
}
.slide-btn:disabled {
  opacity: 0 !important;
  cursor: not-allowed;
}
.slide-btn-prev {
  left: 20px;
}
.slide-btn-next {
  right: 20px;
}

/* 底部小点指示器 */
.carousel-dots {
  position: absolute;
  bottom: 72px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
  z-index: 20;
}
.dot-item {
  width: 8px;
  height: 8px;
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.3);
  cursor: pointer;
  transition: all 0.4s;
  position: relative;
  overflow: hidden;
}
.dot-item.active {
  width: 32px;
  background: rgba(255, 255, 255, 0.25);
}
.dot-progress {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: #ff6900;
  border-radius: 4px;
  animation: progressFill linear forwards;
  animation-duration: inherit;
}
@keyframes progressFill {
  from {
    width: 0%;
  }
  to {
    width: 100%;
  }
}

/* 缩略图条 */
.carousel-thumbs {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
  padding: 10px 16px;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(10px);
  border-radius: 12px 12px 0 0;
  z-index: 20;
}
.thumb-item {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
  border: 2px solid transparent;
  transition: all 0.3s;
  opacity: 0.6;
}
.thumb-item.active {
  border-color: #ff6900;
  opacity: 1;
  transform: translateY(-2px);
}
.thumb-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.thumb-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.2);
}
.thumb-item.active .thumb-overlay {
  background: transparent;
}

/* 内容容器 */
.content-container {
  flex: 1;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30px;
  flex-wrap: wrap;
  gap: 10px;
}

.section-header .section-title {
  font-size: 30px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.section-header .section-sub {
  font-size: 16px;
  color: #999;
  margin-left: 10px;
}

.view-more {
  font-size: 14px;
  color: #ff6900;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s;
}

.view-more:hover {
  color: #ff7e29;
  text-decoration: underline;
}

/* 商品区域 */
.products-section {
  background: white;
  border-radius: 12px;
  padding: 32px;
  margin-bottom: 30px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid #e5e7eb;
}

.products-section .section-header {
  margin-bottom: 24px;
}

/* 品牌专区 */
.brands-section {
  background: white;
  border-radius: 12px;
  padding: 32px;
  margin-bottom: 30px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid #e5e7eb;
}

.brands-container {
  display: flex;
  flex-wrap: wrap;
  gap: 40px;
  justify-content: center;
  align-items: center;
  padding: 20px 0;
}

.brand-item {
  flex: 0 0 auto;
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
  transition: all 0.3s;
}

.brand-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 底部信息 */
.footer {
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
  color: white;
  padding: 60px 0 30px;
  margin-top: auto;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.footer-benefits {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
  margin-bottom: 40px;
}

.benefit-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  border: 1px solid rgba(255, 105, 0, 0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(10px);
}

.benefit-item:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 105, 0, 0.5);
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(255, 105, 0, 0.15);
}

.benefit-icon {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(255, 105, 0, 0.3), rgba(255, 105, 0, 0.1));
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(255, 105, 0, 0.25);
}

.benefit-icon i {
  font-size: 24px;
  color: #ff9a4d;
  filter: drop-shadow(0 2px 4px rgba(255, 105, 0, 0.2));
}

.benefit-content {
  flex: 1;
  min-width: 0;
}

.benefit-title {
  font-size: 16px;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 4px;
  letter-spacing: 0.3px;
}

.benefit-desc {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.5;
}

.footer-bottom {
  text-align: center;
  padding-top: 30px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
  letter-spacing: 0.3px;
}

.footer-bottom p {
  margin: 0;
}

/* 响应式调整 */
@media (max-width: 992px) {
  .nav-container {
    flex-wrap: wrap;
    height: auto;
    padding: 10px 20px;
    gap: 10px;
  }

  .search-bar {
    order: 3;
    margin: 0;
    max-width: 100%;
  }

  .carousel-section {
    height: 420px;
  }

  .slide-title {
    font-size: 28px;
  }

  .price-value {
    font-size: 34px;
  }

  .btn-primary,
  .btn-secondary {
    padding: 12px 22px;
    font-size: 13px;
  }

  .img-frame {
    width: 280px;
    height: 280px;
  }

  .float-card {
    display: none;
  }
}

@media (max-width: 768px) {
  .carousel-section {
    height: 320px;
  }

  .slide-left {
    padding: 0 4% 0 5%;
  }

  .slide-title {
    font-size: 22px;
    margin-bottom: 8px;
  }

  .slide-desc {
    font-size: 13px;
    margin-bottom: 14px;
    -webkit-line-clamp: 1;
  }

  .price-value {
    font-size: 28px;
  }

  .slide-tags {
    display: none;
  }

  .img-frame {
    width: 200px;
    height: 200px;
  }

  .carousel-thumbs {
    display: none;
  }

  .section-header .section-title {
    font-size: 24px;
  }

  .content-container {
    padding: 0 12px;
  }

  .products-section {
    padding: 20px;
  }

  .footer-container {
    flex-direction: column;
    gap: 30px;
  }

  .footer-section {
    min-width: 100%;
  }
}

@media (max-width: 576px) {
  .carousel-section {
    height: 240px;
  }

  .slide-title {
    font-size: 18px;
  }

  .slide-desc {
    display: none;
  }

  .price-value {
    font-size: 24px;
  }

  .btn-primary span,
  .btn-secondary span {
    display: none;
  }

  .btn-primary,
  .btn-secondary {
    padding: 10px 14px;
  }

  .announcement-list {
    flex-direction: column;
  }

  .announcement-list li {
    min-width: 100%;
  }
}
</style>
