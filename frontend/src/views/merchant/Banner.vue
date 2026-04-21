<template>
  <div class="page-block merchant-banner">
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">轮播图管理</h2>
        <p class="page-subtitle">设置首页轮播图，选择已上架商品</p>
      </div>
      <el-button type="primary" icon="el-icon-plus" @click="openDialog"
        >新增轮播图</el-button
      >
    </div>

    <el-table
      :data="list"
      border
      stripe
      v-loading="loading"
      class="banner-table"
    >
      <el-table-column prop="imageUrl" label="图片预览" width="120">
        <template slot-scope="scope">
          <img
            :src="scope.row.productImage"
            style="
              width: 100px;
              height: 60px;
              object-fit: cover;
              border-radius: 4px;
            "
            @click="previewImage(scope.row.productImage)"
            class="img-preview"
          />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="120" />
      <el-table-column prop="productName" label="关联商品" min-width="150" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column prop="enabled" label="状态" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.enabled ? 'success' : 'info'">{{
            scope.row.enabled ? "启用" : "禁用"
          }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="editBanner(scope.row)"
            >编辑</el-button
          >
          <el-button
            type="text"
            size="small"
            @click="deleteBanner(scope.row.id)"
            class="delete-btn"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      :visible.sync="dialogVisible"
      :title="form.id ? '编辑轮播图' : '新增轮播图'"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" label-width="90px" ref="form">
        <el-form-item label="关联商品" prop="productId" required>
          <el-select
            v-model="form.productId"
            filterable
            placeholder="选择已上架商品"
            @change="onProductChange"
          >
            <el-option
              v-for="item in productList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
              <span>{{ item.name }}</span>
              <el-tag size="mini" style="margin-left: 8px; float: right"
                >¥{{ item.price }}</el-tag
              >
            </el-option>
          </el-select>
        </el-form-item>

        <!-- 商品预览 -->
        <el-form-item v-if="selectedProduct" label="商品预览">
          <div style="display: flex; gap: 10px; align-items: flex-start">
            <img
              :src="selectedProduct.image"
              style="
                width: 100px;
                height: 100px;
                object-fit: cover;
                border-radius: 4px;
              "
            />
            <div style="flex: 1">
              <p>
                <strong>{{ selectedProduct.name }}</strong>
              </p>
              <p style="color: #666; font-size: 12px">
                {{ selectedProduct.description }}
              </p>
              <p style="color: #ff6900">
                <strong>¥{{ selectedProduct.price }}</strong>
              </p>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="排序权重">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>

        <el-form-item label="启用状态">
          <el-switch v-model="form.enabled" />
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveBanner" :loading="saving"
          >保存</el-button
        >
      </span>
    </el-dialog>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="showImageViewer"
      :on-close="() => (showImageViewer = false)"
      :url-list="[previewImageUrl]"
    />
  </div>
</template>

<script>
import { getProducts } from "@/api/merchant";
import request from "@/api/request";

export default {
  name: "MerchantBanner",
  data() {
    return {
      list: [],
      loading: false,
      saving: false,
      dialogVisible: false,
      showImageViewer: false,
      previewImageUrl: "",
      selectedProduct: null,
      form: {
        id: null,
        title: "",
        productId: null,
        sortOrder: 0,
        enabled: true,
      },
      rules: {
        productId: [
          { required: true, message: "请选择商品", trigger: "change" },
        ],
      },
      productList: [],
    };
  },
  created() {
    this.loadBannerList();
    this.loadProducts();
  },
  methods: {
    loadBannerList() {
      this.loading = true;
      request
        .get("/merchant/banner")
        .then((data) => {
          this.list = data || [];
          this.loading = false;
        })
        .catch((err) => {
          this.$message.error("加载轮播图列表失败");
          this.loading = false;
        });
    },

    loadProducts() {
      getProducts({ onSale: true, page: 0, size: 100 })
        .then((data) => {
          this.productList = data && data.content ? data.content : [];
        })
        .catch(() => {
          this.$message.error("加载商品列表失败");
        });
    },

    openDialog() {
      this.resetForm();
      this.dialogVisible = true;
    },

    editBanner(row) {
      this.form = { ...row };
      // 填充选中的商品信息用于预览
      const product = this.productList.find((p) => p.id === row.productId);
      if (product) {
        this.selectedProduct = product;
      }
      this.dialogVisible = true;
    },

    onProductChange(productId) {
      const product = this.productList.find((p) => p.id === productId);
      this.selectedProduct = product || null;
    },

    saveBanner() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return;
        }

        this.saving = true;
        const api = this.form.id ? request.put : request.post;
        const url = this.form.id
          ? `/merchant/banner/${this.form.id}`
          : "/merchant/banner";

        api(url, this.form)
          .then(() => {
            this.$message.success(this.form.id ? "编辑成功" : "新增成功");
            this.dialogVisible = false;
            this.loadBannerList();
          })
          .catch((err) => {
            this.$message.error(err.message || "保存失败");
          })
          .finally(() => {
            this.saving = false;
          });
      });
    },

    deleteBanner(id) {
      this.$confirm("确定删除该轮播图吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          request
            .delete(`/merchant/banner/${id}`)
            .then(() => {
              this.$message.success("删除成功");
              this.loadBannerList();
            })
            .catch((err) => {
              this.$message.error(err.message || "删除失败");
            });
        })
        .catch(() => {});
    },

    previewImage(url) {
      this.previewImageUrl = url;
      this.showImageViewer = true;
    },

    resetForm() {
      this.form = {
        id: null,
        title: "",
        productId: null,
        sortOrder: 0,
        enabled: true,
      };
      this.selectedProduct = null;
    },
  },
};
</script>

<style scoped>
.page-block.merchant-banner {
  max-width: 1200px;
  margin: 0 auto;
}

.banner-table {
  margin-top: 20px;
}

.img-preview {
  cursor: pointer;
  transition: transform 0.2s;
}

.img-preview:hover {
  transform: scale(1.1);
}

.delete-btn {
  color: #f56c6c;
}

.el-tag {
  margin-right: 0;
}
</style>
