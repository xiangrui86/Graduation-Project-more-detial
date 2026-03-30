<template>
  <div class="page-block merchant-products">
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">商品管理</h2>
        <p class="page-subtitle">上架新品、编辑信息与图片</p>
      </div>
      <el-button
        type="primary"
        icon="el-icon-plus"
        @click="openCreateDialog"
        class="add-btn"
      >
        上架新品
      </el-button>
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
          <div
            v-if="
              scope.row.reviewStatus === 'REJECTED' && scope.row.reviewReason
            "
            class="review-reason"
          >
            {{ scope.row.reviewReason }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template slot-scope="scope">
          <div class="action-buttons">
            <el-button
              type="text"
              size="small"
              @click="editProduct(scope.row)"
              class="edit-btn"
            >
              编辑
            </el-button>
            <el-button
              type="text"
              size="small"
              @click="viewDetail(scope.row)"
              class="detail-btn"
            >
              详情
            </el-button>
            <el-button
              type="text"
              size="small"
              @click="deleteProduct(scope.row.id)"
              class="delete-btn"
            >
              删除
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

    <!-- 商品上架/编辑弹窗 -->
    <ProductUploadDialog
      :visible.sync="showCreateDialog"
      :editing-product="editingProduct"
      @submit="handleProductSubmit"
      @close="handleDialogClose"
    />
  </div>
</template>

<script>
import {
  getProducts,
  createProduct,
  updateProduct,
  deleteProduct,
} from "@/api/merchant";
import ProductUploadDialog from "@/components/merchant/ProductUploadDialog.vue";

export default {
  name: "MerchantProducts",
  components: {
    ProductUploadDialog,
  },
  data() {
    return {
      list: [],
      categories: [],
      loading: false,
      showCreateDialog: false,
      editingProduct: null,
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

    openCreateDialog() {
      this.editingProduct = null;
      this.showCreateDialog = true;
    },

    editProduct(product) {
      this.editingProduct = { ...product };
      this.showCreateDialog = true;
    },

    viewDetail(product) {
      this.$router.push(`/merchant/products/${product.id}/detail`);
    },

    async handleProductSubmit(formData) {
      try {
        if (this.editingProduct) {
          await updateProduct(this.editingProduct.id, formData);
          this.$message.success("商品编辑成功");
        } else {
          await createProduct(formData);
          this.$message.success("商品上架成功");
        }
        this.showCreateDialog = false;
        this.loadProducts(this.currentPage - 1);
      } catch (error) {
        this.$message.error(
          error.response?.data?.message || "操作失败，请重试",
        );
      }
    },

    handleDialogClose() {
      this.editingProduct = null;
      this.showCreateDialog = false;
    },

    handlePageChange(page) {
      this.currentPage = page;
      this.loadProducts(page - 1);
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
.merchant-products {
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
  gap: 4px;
}

.page-title {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: #303133;
}

.page-subtitle {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.add-btn {
  background: linear-gradient(135deg, #ff6900 0%, #ff8a3d 100%);
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 105, 0, 0.35);
}

.product-table {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.product-table /deep/ .el-table__header th {
  background: #fafafa;
  color: #606266;
  font-weight: 600;
  padding: 14px 0;
}

.product-table /deep/ .el-table__row {
  transition: background 0.3s ease;
}

.product-table /deep/ .el-table__row:hover > td {
  background: #f5f7fa !important;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-thumb {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
  border: 1px solid #ebeef5;
}

.product-detail {
  flex: 1;
  min-width: 0;
}

.product-name {
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
  font-size: 14px;
}

.product-desc {
  font-size: 12px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price {
  color: #ff6900;
  font-weight: 700;
  font-size: 15px;
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
}

.pagination-wrapper /deep/ .el-pagination {
  background: #fff;
  padding: 16px 24px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

@media (max-width: 768px) {
  .merchant-products {
    padding: 12px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
    padding: 16px;
  }

  .add-btn {
    width: 100%;
  }

  .product-info {
    flex-direction: column;
    align-items: flex-start;
  }

  .action-buttons {
    flex-direction: column;
    gap: 4px;
  }
}
</style>
