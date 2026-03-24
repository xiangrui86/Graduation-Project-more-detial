<template>
  <div class="page-block">
    <div class="page-title">
      <h2>全部商品</h2>
      <span class="sub">浏览与挑选心仪装备</span>
    </div>

    <div class="toolbar">
      <el-select
        v-model="categoryId"
        placeholder="选择分类"
        clearable
        filterable
        class="category"
        @change="onCategoryChange"
      >
        <el-option label="全部分类" :value="null" />
        <el-option
          v-for="c in categories"
          :key="c.id"
          :label="c.name"
          :value="c.id"
        />
      </el-select>
      <span class="muted" v-if="categoryId != null"
        >已筛选：{{ categoryName }}</span
      >
    </div>

    <el-row :gutter="16">
      <el-col v-for="p in list" :key="p.id" :xs="12" :sm="8" :md="6">
        <product-card :product="p" />
      </el-col>
    </el-row>

    <el-pagination
      v-if="total > pageSize"
      :current-page="page + 1"
      :page-size="pageSize"
      :total="total"
      layout="prev, pager, next"
      @current-change="onPage"
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
      page: 0,
      pageSize: 12,
      total: 0,
    };
  },
  computed: {
    categoryName() {
      const c = this.categories.find((x) => x.id === this.categoryId);
      return c ? c.name : "";
    },
  },
  created() {
    this.loadCategories();
    this.load();
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
      const params = { page: this.page, size: this.pageSize };
      if (this.categoryId != null) params.categoryId = this.categoryId;

      getProducts(params).then((res) => {
        if (res.data && res.data.content) {
          this.list = res.data.content;
          this.total = res.data.totalElements || 0;
        }
      });
    },
    onCategoryChange() {
      this.page = 0;
      this.load();
    },
    onPage(p) {
      this.page = p - 1;
      this.load();
    },
  },
  components: {
    ProductCard: () => import("@/components/ProductCard.vue"),
  },
};
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}
.category {
  width: 260px;
}
</style>
