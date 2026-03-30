<template>
  <div class="page-block admin-products">
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">商品审核</h2>
        <p class="page-subtitle">审核商家提交的商品，拒绝后退回修改</p>
      </div>
    </div>

    <el-table
      :data="list"
      border
      stripe
      v-loading="loading"
      class="product-table"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="商品信息" min-width="200">
        <template slot-scope="scope">
          <div class="product-info">
            <img
              v-if="scope.row.image"
              :src="scope.row.image"
              class="product-thumb"
              alt="商品图片"
            />
            <div class="product-detail">
              <div class="product-name">{{ scope.row.name }}</div>
              <div class="product-desc" v-if="scope.row.description">
                {{ scope.row.description | ellipsis(30) }}
              </div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格" width="100">
        <template slot-scope="scope">
          <span class="price">¥{{ scope.row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column label="上架状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.onSale ? 'success' : 'info'" size="small">
            {{ scope.row.onSale ? "上架" : "下架" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" width="140">
        <template slot-scope="scope">
          <el-tag :type="reviewTagType(scope.row.reviewStatus)" size="small">
            {{ reviewLabel(scope.row.reviewStatus) }}
          </el-tag>
          <div v-if="scope.row.reviewStatus === 'REJECTED' && scope.row.reviewReason" class="review-reason">
            {{ scope.row.reviewReason }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260" fixed="right">
        <template slot-scope="scope">
          <div class="action-buttons">
            <el-button
              type="text"
              size="small"
              @click="viewDetail(scope.row)"
              class="detail-btn"
            >
              详情
            </el-button>
            <el-button
              v-if="scope.row.reviewStatus !== 'APPROVED'"
              type="text"
              size="small"
              @click="approveProduct(scope.row)"
              class="approve-btn"
            >
              通过
            </el-button>
            <el-button
              v-if="scope.row.reviewStatus !== 'APPROVED'"
              type="text"
              size="small"
              @click="rejectProduct(scope.row)"
              class="reject-btn"
            >
              拒绝
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper" v-if="total > pageSize">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        @current-change="handlePageChange"
        layout="prev, pager, next, jumper"
      />
    </div>
  </div>
</template>

<script>
import {
  getProducts,
  approveProduct,
  rejectProduct,
} from "@/api/admin";

export default {
  name: "AdminProducts",
  components: {},
  data() {
    return {
      list: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
    };
  },
  filters: {
    ellipsis(value, length) {
      if (!value) return "";
      return value.length > length ? value.substring(0, length) + "..." : value;
    },
  },
  created() {
    this.loadProducts();
  },
  methods: {
    loadProducts(page = 0) {
      this.loading = true;
      getProducts({ page, size: this.pageSize })
        .then((res) => {
          if (res.data && res.data.content) {
            this.list = res.data.content;
            this.total = res.data.totalElements || 0;
          } else if (Array.isArray(res.data)) {
            this.list = res.data;
            this.total = res.data.length;
          }
        })
        .catch((err) => {
          this.$message.error("加载商品失败");
          console.error(err);
        })
        .finally(() => {
          this.loading = false;
        });
    },


    async approveProduct(product) {
      try {
        await approveProduct(product.id);
        this.$message.success("商品已通过审核");
        this.loadProducts(this.currentPage - 1);
      } catch (error) {
        this.$message.error(error.response?.data?.message || "审核失败");
      }
    },

    async rejectProduct(product) {
      try {
        const reason = await this.$prompt("请输入拒绝原因", "审核不通过", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          inputPlaceholder: "请输入拒绝原因",
          inputPattern: /\S+/, 
          inputErrorMessage: "请输入拒绝原因",
        });
        await rejectProduct(product.id, reason.value);
        this.$message.success("商品已退回修改");
        this.loadProducts(this.currentPage - 1);
      } catch (error) {
        if (error === "cancel") return;
        this.$message.error(error.response?.data?.message || "操作失败");
      }
    },

    viewDetail(product) {
      this.$router.push(`/admin/products/${product.id}/detail`);
    },


    handlePageChange(page) {
      this.currentPage = page;
      this.loadProducts(page - 1);
    },

    reviewTagType(status) {
      if (status === "APPROVED") return "success";
      if (status === "REJECTED") return "danger";
      return "warning";
    },

    reviewLabel(status) {
      if (status === "APPROVED") return "已通过";
      if (status === "REJECTED") return "已拒绝";
      return "待审核";
    },

    deleteProduct(id) {
      this.$confirm("确定删除该商品吗？删除后不可恢复。", "删除确认", {
        confirmButtonText: "确定删除",
        cancelButtonText: "取消",
        type: "warning",
        customClass: "delete-confirm-dialog",
      })
        .then(async () => {
          try {
            await deleteProduct(id);
            this.$message.success("删除成功");
            this.loadProducts(this.currentPage - 1);
          } catch (error) {
            this.$message.error("删除失败");
          }
        })
        .catch(() => {});
    },
  },
};
</script>

<style scoped>
.admin-products {
  padding: 24px;
  background: #f5f7fa;
  min-height: calc(100vh - 120px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 20px 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.header-content {
  display: flex;
  flex-direction: column;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.page-subtitle {
  margin: 4px 0 0 0;
  font-size: 14px;
  color: #909399;
}

.add-btn {
  height: 40px;
  padding: 0 24px;
}

.product-table {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-thumb {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
}

.product-detail {
  flex: 1;
  min-width: 0;
}

.product-name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-desc {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}

.price {
  font-weight: 600;
  color: #f56c6c;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.edit-btn {
  color: #409eff;
}

.detail-btn {
  color: #67c23a;
}

.delete-btn {
  color: #f56c6c;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}
</style></content>
<parameter name="filePath">d:\code\code\frontend\src\views\admin\Products.vue