<template>
  <div class="review-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">评价管理</h1>
      <p class="page-subtitle">查看和管理用户对商品的评价</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stat-cards">
      <el-card class="stat-card">
        <div class="stat-item">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总评价数</div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-item">
          <div class="stat-value">{{ stats.fiveStar }}</div>
          <div class="stat-label">5星评价</div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-item">
          <div class="stat-value">{{ stats.avgRating }}</div>
          <div class="stat-label">平均评分</div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-item">
          <div class="stat-value">{{ stats.lowRating }}</div>
          <div class="stat-label">低评价</div>
        </div>
      </el-card>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="商品">
          <el-select
            v-model="filterForm.productId"
            placeholder="选择商品"
            clearable
            filterable
            style="width: 200px"
            @change="loadReviews"
          >
            <el-option
              v-for="product in productList"
              :key="product.id"
              :label="product.name"
              :value="product.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="评分">
          <el-select
            v-model="filterForm.minRating"
            placeholder="最低评分"
            clearable
            style="width: 120px"
            @change="loadReviews"
          >
            <el-option label="全部" :value="null" />
            <el-option label="5星" :value="5" />
            <el-option label="4星及以上" :value="4" />
            <el-option label="3星及以上" :value="3" />
            <el-option label="低于3星" :value="-3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button
            type="danger"
            @click="deleteSelected"
            :disabled="selectedReviews.length === 0"
          >
            删除选中({{ selectedReviews.length }})
          </el-button>
          <el-button @click="loadReviews">刷新</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 评价列表 -->
    <el-card class="review-table-card">
      <div slot="header" class="card-header">
        <span>评价列表</span>
      </div>

      <el-table
        :data="reviewList"
        v-loading="loading"
        style="width: 100%"
        stripe
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" />

        <el-table-column label="商品" min-width="150">
          <template slot-scope="scope">
            <div class="product-info">
              <img
                v-if="scope.row.productImage"
                :src="scope.row.productImage"
                class="product-thumb"
                alt="商品图片"
              />
              <div class="product-text">
                <div class="product-name">{{ scope.row.productName }}</div>
                <div class="product-price">¥{{ scope.row.productPrice }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="用户" width="120">
          <template slot-scope="scope">
            <div class="user-info">
              <div class="user-name">{{ scope.row.userNickname }}</div>
              <div class="user-username">@{{ scope.row.userUsername }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="评分" width="80" align="center">
          <template slot-scope="scope">
            <el-rate
              v-model="scope.row.rating"
              disabled
              show-score
              text-color="#ff9800"
            />
          </template>
        </el-table-column>

        <el-table-column label="内容" min-width="300">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="top" width="400">
              <p>{{ scope.row.content }}</p>
              <span slot="reference" class="review-content">
                {{
                  scope.row.content.length > 50
                    ? scope.row.content.substring(0, 50) + "..."
                    : scope.row.content
                }}
              </span>
            </el-popover>
          </template>
        </el-table-column>

        <el-table-column label="时间" width="160">
          <template slot-scope="scope">
            {{ formatTime(scope.row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="danger"
              plain
              @click="deleteReview(scope.row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalElements"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
import {
  getReviews,
  deleteReview,
  deleteReviews,
  getProducts,
} from "@/api/merchant";

export default {
  name: "ReviewManagement",
  data() {
    return {
      // 统计数据
      stats: {
        total: 0,
        fiveStar: 0,
        avgRating: 0,
        lowRating: 0,
      },

      // 过滤表单
      filterForm: {
        productId: null,
        minRating: null,
      },

      // 评价列表
      reviewList: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      totalElements: 0,
      selectedReviews: [],

      // 商品列表
      productList: [],
    };
  },

  mounted() {
    this.loadProductList();
    this.loadReviews();
  },

  methods: {
    // 加载商品列表
    async loadProductList() {
      try {
        const res = await getProducts({ page: 0, size: 1000 });
        if (res.code === 200) {
          this.productList = res.data.content || [];
        }
      } catch (error) {
        console.error("加载商品列表失败", error);
      }
    },

    // 加载评价列表
    async loadReviews() {
      this.loading = true;
      try {
        const params = {
          page: this.currentPage - 1,
          size: this.pageSize,
          ...this.filterForm,
        };

        const res = await getReviews(params);
        if (res.code === 200) {
          this.reviewList = res.data.content || [];
          this.totalElements = res.data.totalElements || 0;
          this.calculateStats(this.reviewList);
        } else {
          this.$message.error(res.message || "加载评价失败");
        }
      } catch (error) {
        this.$message.error("加载评价失败");
      } finally {
        this.loading = false;
      }
    },

    // 计算统计数据
    calculateStats(reviews) {
      if (reviews.length === 0) {
        this.stats = { total: 0, fiveStar: 0, avgRating: 0, lowRating: 0 };
        return;
      }

      const totalReviews = reviews.length;
      const fiveStarCount = reviews.filter((r) => r.rating === 5).length;
      const avgRating = (
        reviews.reduce((sum, r) => sum + r.rating, 0) / totalReviews
      ).toFixed(1);
      const lowRatingCount = reviews.filter((r) => r.rating < 3).length;

      this.stats = {
        total: totalReviews,
        fiveStar: fiveStarCount,
        avgRating: avgRating,
        lowRating: lowRatingCount,
      };
    },

    // 获取商品名称
    getProductName(productId) {
      const product = this.productList.find((p) => p.id === productId);
      return product ? product.name : "商品已删除";
    },

    // 格式化时间
    formatTime(time) {
      if (!time) return "-";
      return new Date(time).toLocaleString("zh-CN");
    },

    // 删除单条评价
    deleteReview(reviewId) {
      this.$confirm("确认删除此评价吗？", "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(async () => {
          try {
            const res = await deleteReview(reviewId);
            if (res.code === 200) {
              this.$message.success("评价已删除");
              this.loadReviews();
            } else {
              this.$message.error(res.message || "删除失败");
            }
          } catch (error) {
            this.$message.error("删除失败");
          }
        })
        .catch(() => {});
    },

    // 批量删除评价
    deleteSelected() {
      this.$confirm(
        `确认删除选中的 ${this.selectedReviews.length} 条评价吗？`,
        "警告",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        },
      )
        .then(async () => {
          try {
            const res = await deleteReviews(this.selectedReviews);
            if (res.code === 200) {
              this.$message.success("删除成功");
              this.loadReviews();
              this.selectedReviews = [];
            } else {
              this.$message.error(res.message || "删除失败");
            }
          } catch (error) {
            this.$message.error("删除失败");
          }
        })
        .catch(() => {});
    },

    // 表格选择变化
    handleSelectionChange(selection) {
      this.selectedReviews = selection.map((item) => item.id);
    },

    // 分页处理
    handleSizeChange(size) {
      this.pageSize = size;
      this.currentPage = 1;
      this.loadReviews();
    },

    handleCurrentChange(page) {
      this.currentPage = page;
      this.loadReviews();
    },
  },
};
</script>

<style scoped>
.review-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.page-subtitle {
  color: #6b7280;
  margin: 0;
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
}

.filter-section {
  margin-bottom: 24px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-end;
}

.review-table-card {
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.product-thumb {
  width: 40px;
  height: 40px;
  object-fit: cover;
  border-radius: 4px;
}

.product-text {
  flex: 1;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
  margin-bottom: 2px;
  display: block;
}

.product-price {
  font-size: 12px;
  color: #ef4444;
  font-weight: 500;
}

.user-info {
  text-align: center;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
  margin-bottom: 2px;
  display: block;
}

.user-username {
  font-size: 12px;
  color: #6b7280;
}

.review-content {
  color: #6b7280;
  cursor: pointer;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: right;
}
</style>
