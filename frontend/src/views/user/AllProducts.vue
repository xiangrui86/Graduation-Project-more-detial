<template>
  <div class="page-block">
    <div class="page-title">
      <h2 v-if="isSearchResult">
        <i class="el-icon-search"></i>
        搜索：<span class="keyword-title">{{ currentKeyword }}</span>
      </h2>
      <h2 v-else>全部商品</h2>
      <span class="sub" v-if="!isSearchResult">浏览与挑选心仪装备</span>
    </div>

    <div class="search-bar">
      <el-input
        v-model="searchQuery"
        placeholder="搜索商品名称、关键词..."
        @keyup.enter.native="doSearch"
        prefix-icon="el-icon-search"
        class="search-input"
        clearable
        @clear="clearSearch"
      />
      <el-button type="primary" class="search-btn" @click="doSearch" :loading="loading">
        <i class="el-icon-search"></i> 搜索
      </el-button>
    </div>

    <div class="filter-bar">
      <!-- 左侧分类过滤（搜索模式下隐藏） -->
      <div class="filter-category" v-if="!isSearchResult">
        <span
          class="fcat-item"
          :class="{ active: categoryId === null }"
          @click="selectCategory(null)"
        >全部</span>
        <span
          v-for="c in categories"
          :key="c.id"
          class="fcat-item"
          :class="{ active: categoryId === c.id }"
          @click="selectCategory(c.id)"
        >{{ c.name }}</span>
      </div>
      <!-- 搜索结果条 -->
      <div v-if="isSearchResult" class="search-result-tip">
        <i class="el-icon-s-order"></i>
        共找到 <span class="count">{{ total }}</span> 个与
        "<span class="keyword">{{ currentKeyword }}</span>"相关商品
        <span class="clear-btn" @click="clearSearch">
          <i class="el-icon-close"></i> 清除
        </span>
      </div>
      <!-- 右侧排序区 -->
      <div class="sort-group">
        <span class="sort-label">排序</span>
        <div class="sort-tabs">
          <span
            class="sort-tab"
            :class="{ active: sortType === 'default' }"
            @click="handleSortChange('default')"
          >综合</span>
          <span
            class="sort-tab"
            :class="{ active: sortType === 'price-asc' }"
            @click="handleSortChange('price-asc')"
          >
            价格
            <i :class="sortType === 'price-asc' ? 'el-icon-top active-icon' : 'el-icon-sort'"></i>
          </span>
          <span
            class="sort-tab"
            :class="{ active: sortType === 'price-desc' }"
            @click="handleSortChange('price-desc')"
          >
            价格
            <i :class="sortType === 'price-desc' ? 'el-icon-bottom active-icon' : 'el-icon-sort'"></i>
          </span>
          <span
            class="sort-tab"
            :class="{ active: sortType === 'sales' }"
            @click="handleSortChange('sales')"
          >销量</span>
        </div>
        <span class="total-count" v-if="total > 0">共 {{ total }} 件</span>
      </div>
    </div>

    <div v-if="loading" class="loading-wrap">
      <i class="el-icon-loading"></i> 加载中...
    </div>

    <el-row :gutter="16" class="product-grid" v-else>
      <el-col v-for="p in list" :key="p.id" :xs="12" :sm="8" :md="6" :lg="4">
        <product-card :product="p" />
      </el-col>
    </el-row>

    <div v-if="!loading && list.length === 0" class="empty-tip">
      <div class="empty-icon">
        <i class="el-icon-search"></i>
      </div>
      <div class="empty-text" v-if="isSearchResult">
        未找到与 "<span>{{ currentKeyword }}</span>" 相关的商品
      </div>
      <div class="empty-text" v-else>暂无商品</div>
      <div class="empty-suggest" v-if="isSearchResult">
        <p>建议您：</p>
        <ul>
          <li>检查关键词拼写是否正确</li>
          <li>尝试使用更简短、通用的词语</li>
          <li>换一个相关词重新搜索</li>
        </ul>
        <el-button type="primary" round @click="clearSearch">
          <i class="el-icon-arrow-left"></i> 返回全部商品
        </el-button>
      </div>
    </div>

    <el-pagination
      v-if="total > pageSize"
      :current-page="page + 1"
      :page-size="pageSize"
      :total="total"
      layout="total, prev, pager, next, jumper"
      @current-change="onPage"
      class="pagination-center"
    />
  </div>
</template>

<script>
import { getProducts, getAllCategories } from "@/api/pub";

export default {
  name: "AllProducts",
  data() {
    return {
      list: [],
      categories: [],
      categoryId: null,
      searchQuery: "",
      currentKeyword: "",   // 已提交的搜索词（与 searchQuery 分离）
      isSearchResult: false,
      loading: false,
      sortType: "default",
      page: 0,
      pageSize: 12,
      total: 0,
    };
  },
  created() {
    this.loadCategories();
    const q = this.$route.query.search;
    if (q) {
      this.searchQuery = q;
      this.currentKeyword = q;
      this.isSearchResult = true;
    }
    this.load();
  },
  watch: {
    // 首页搜索跳转过来时重新加载
    "$route.query.search"(val) {
      if (val) {
        this.searchQuery = val;
        this.currentKeyword = val;
        this.isSearchResult = true;
      } else {
        this.searchQuery = "";
        this.currentKeyword = "";
        this.isSearchResult = false;
      }
      this.page = 0;
      this.load();
    },
  },
  methods: {
    loadCategories() {
      getAllCategories()
        .then((res) => {
          if (Array.isArray(res.data)) this.categories = res.data;
          else if (res.data && Array.isArray(res.data.content))
            this.categories = res.data.content;
        })
        .catch(() => {});
    },
    load() {
      this.loading = true;
      const params = { page: this.page, size: this.pageSize };
      if (this.categoryId != null) params.categoryId = this.categoryId;
      if (this.currentKeyword.trim()) params.search = this.currentKeyword.trim();
      // 排序参数
      if (this.sortType === "price-asc") { params.sort = "price"; params.direction = "asc"; }
      else if (this.sortType === "price-desc") { params.sort = "price"; params.direction = "desc"; }
      else if (this.sortType === "sales") { params.sort = "sales"; params.direction = "desc"; }

      getProducts(params)
        .then((res) => {
          if (res && res.content) {
            this.list = res.content;
            this.total = res.totalElements || 0;
          } else if (res && Array.isArray(res)) {
            this.list = res;
            this.total = res.length || 0;
          } else if (res.data && res.data.content) {
            this.list = res.data.content;
            this.total = res.data.totalElements || 0;
          } else if (res.data && Array.isArray(res.data)) {
            this.list = res.data;
            this.total = res.data.length || 0;
          } else {
            this.list = [];
            this.total = 0;
          }
        })
        .catch(() => { this.list = []; this.total = 0; })
        .finally(() => { this.loading = false; });
    },
    selectCategory(id) {
      this.categoryId = id;
      this.page = 0;
      this.load();
    },
    doSearch() {
      const q = this.searchQuery.trim();
      if (!q) return;
      this.currentKeyword = q;
      this.isSearchResult = true;
      this.page = 0;
      this.categoryId = null;
      // 同步 URL query 参数
      // 通过路由变化驱动加载，避免重复请求
      if (this.$route.query.search === q) {
        // 关键词未变，直接加载
        this.load();
      } else {
        this.$router.replace({ path: "/products", query: { search: q } }).catch(() => {});
      }
    },
    clearSearch() {
      this.searchQuery = "";
      this.currentKeyword = "";
      this.isSearchResult = false;
      this.sortType = "default";
      this.page = 0;
      if (this.$route.query.search) {
        // 有旧查询参数，通过路由变化驱动加载
        this.$router.replace({ path: "/products" }).catch(() => {});
      } else {
        this.load();
      }
    },
    handleSortChange(sortType) {
      this.sortType = sortType;
      this.page = 0;
      this.load();
    },
    onPage(p) {
      this.page = p - 1;
      this.load();
      window.scrollTo({ top: 0, behavior: "smooth" });
    },
  },
  components: {
    ProductCard: () => import("@/components/ProductCard.vue"),
  },
};
</script>

<style scoped>
.page-block {
  max-width: 1400px;
  margin: 0 auto;
  padding: 30px 20px;
}

.page-title {
  margin-bottom: 24px;
}

.page-title h2 {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-title h2 i {
  color: #ff6900;
  font-size: 24px;
}

.keyword-title {
  color: #ff6900;
  font-weight: 800;
}

.page-title .sub {
  font-size: 14px;
  color: #999;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.search-input {
  flex: 1;
  max-width: 500px;
}

.search-btn {
  background: linear-gradient(135deg, #ff6900, #ff8533);
  border: none;
  border-radius: 20px;
  padding: 12px 24px;
  transition: all 0.3s;
}

.search-btn:hover {
  background: linear-gradient(135deg, #ff8533, #ffa040);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 105, 0, 0.3);
}

.search-result-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
  flex: 1;
}

.search-result-tip i {
  color: #ff6900;
  font-size: 16px;
}

.search-result-tip .count {
  color: #ff6900;
  font-weight: 700;
  font-size: 15px;
}

.search-result-tip .keyword {
  color: #ff6900;
  font-weight: 600;
}

.clear-btn {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  margin-left: 10px;
  padding: 3px 10px;
  background: #f5f5f5;
  border-radius: 20px;
  font-size: 12px;
  color: #999;
  cursor: pointer;
  transition: all 0.2s;
}
.clear-btn:hover {
  background: #ffe8d6;
  color: #ff6900;
}

/* 综合筛选栏 */
.filter-bar {
  display: flex;
  align-items: center;
  gap: 0;
  margin-bottom: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  overflow: hidden;
  border: 1px solid #f0f0f0;
  flex-wrap: wrap;
}

/* 分类区 */
.filter-category {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0;
  flex: 1;
  padding: 10px 16px;
  border-right: 1px solid #f0f0f0;
  min-width: 0;
}

.fcat-item {
  padding: 6px 14px;
  font-size: 13px;
  color: #555;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;
  white-space: nowrap;
  margin: 2px;
}
.fcat-item:hover {
  background: #fff3eb;
  color: #ff6900;
}
.fcat-item.active {
  background: #ff6900;
  color: white;
  font-weight: 600;
}

/* 排序区 */
.sort-group {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  white-space: nowrap;
  flex-shrink: 0;
}

.sort-label {
  font-size: 13px;
  color: #bbb;
}

.sort-tabs {
  display: flex;
  align-items: center;
  background: #f7f7f7;
  border-radius: 8px;
  padding: 3px;
  gap: 2px;
}

.sort-tab {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  padding: 5px 12px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;
  user-select: none;
}
.sort-tab:hover {
  color: #ff6900;
  background: rgba(255,105,0,0.06);
}
.sort-tab.active {
  background: white;
  color: #ff6900;
  font-weight: 600;
  box-shadow: 0 1px 6px rgba(0,0,0,0.1);
}
.active-icon {
  color: #ff6900;
  font-size: 11px;
}

.total-count {
  font-size: 12px;
  color: #bbb;
  margin-left: 4px;
}

.loading-wrap {
  text-align: center;
  padding: 60px 0;
  font-size: 16px;
  color: #999;
}

.loading-wrap i {
  font-size: 24px;
  color: #ff6900;
  margin-right: 8px;
}

.empty-tip {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 16px;
  margin: 20px 0;
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, #fff3eb, #ffe8d6);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-icon i {
  font-size: 36px;
  color: #ff6900;
}

.empty-text {
  font-size: 18px;
  color: #666;
  margin-bottom: 16px;
}

.empty-text span {
  color: #ff6900;
  font-weight: 600;
}

.empty-suggest {
  font-size: 14px;
  color: #999;
  margin-top: 16px;
}

.empty-suggest p {
  margin: 0 0 8px;
  font-weight: 600;
  color: #666;
}

.empty-suggest ul {
  list-style: none;
  padding: 0;
  margin: 0 0 20px;
}

.empty-suggest li {
  padding: 4px 0;
}

.empty-suggest li::before {
  content: '· ';
  color: #ff6900;
}



.pagination-center {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}


</style>
