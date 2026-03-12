<template>
  <div class="page-block">
    <div class="page-title">
      <h2>商品管理</h2>
      <span class="sub">上架新品、编辑信息与图片</span>
    </div>

    <div style="display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 12px">
      <el-button type="primary" @click="showCreateDialog = true"
        >上架新品</el-button
      >
    </div>

    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="price" label="价格" width="100" />
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="onSale" label="上架" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.onSale ? 'success' : 'info'">{{
            scope.row.onSale ? "是" : "否"
          }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="editProduct(scope.row)"
            >编辑</el-button
          >
          <el-button
            type="text"
            size="small"
            @click="deleteProduct(scope.row.id)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 创建/编辑产品对话框 -->
    <el-dialog
      :title="editingId ? '编辑商品' : '上架新品'"
      :visible.sync="showCreateDialog"
      width="500px"
      @close="resetForm"
      :append-to-body="true"
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="商品名称">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入商品描述"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="选择分类">
            <el-option label="足球用品" :value="1" />
            <el-option label="篮球用品" :value="2" />
            <el-option label="健身器材" :value="3" />
            <el-option label="羽毛球用品" :value="4" />
            <el-option label="乒乓球用品" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="自定义分类">
          <el-input
            v-model="form.categoryName"
            placeholder="或输入新的分类名称（可选）"
          />
        </el-form-item>
        <el-form-item label="价格">
          <el-input
            v-model.number="form.price"
            type="number"
            placeholder="请输入价格"
          />
        </el-form-item>
        <el-form-item label="原价">
          <el-input
            v-model.number="form.originalPrice"
            type="number"
            placeholder="请输入原价"
          />
        </el-form-item>
        <el-form-item label="库存">
          <el-input
            v-model.number="form.stock"
            type="number"
            placeholder="请输入库存"
          />
        </el-form-item>
        <el-form-item label="图片">
          <div style="display: flex; gap: 10px; align-items: flex-start">
            <div style="flex: 1">
              <el-select
                v-model="form.image"
                placeholder="选择已有的图片"
                filterable
                allow-create
                default-first-option
                @focus="loadImages"
              >
                <el-option
                  v-for="img in imageList"
                  :key="img.url"
                  :label="img.name"
                  :value="img.url"
                />
              </el-select>
              <div style="font-size: 12px; color: #909399; margin-top: 5px">
                或选择/输入图片路径
              </div>
            </div>
            <div>
              <el-upload
                :auto-upload="true"
                accept="image/*"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                action="/api/pub/images/upload"
                :show-file-list="false"
              >
                <el-button type="primary" size="small">上传</el-button>
              </el-upload>
            </div>
          </div>
          <div v-if="form.image" style="margin-top: 10px">
            <img
              :src="form.image"
              style="max-width: 150px; max-height: 150px; border-radius: 4px"
            />
          </div>
        </el-form-item>
        <el-form-item label="新品">
          <el-switch v-model="form.isNew" />
        </el-form-item>
        <el-form-item label="上架">
          <el-switch v-model="form.onSale" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="saveProduct" :loading="saving"
          >保存</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getProducts,
  createProduct,
  updateProduct,
  deleteProduct,
  listImages,
  uploadImage,
} from "@/api/merchant";

export default {
  name: "MerchantProducts",
  data() {
    return {
      list: [],
      imageList: [],
      showCreateDialog: false,
      editingId: null,
      saving: false,
      form: {
        name: "",
        description: "",
        categoryId: 1,
        categoryName: "",
        price: 0,
        originalPrice: 0,
        stock: 100,
        image: "",
        isNew: true,
        onSale: true,
      },
    };
  },
  created() {
    this.loadProducts();
  },
  methods: {
    loadProducts() {
      getProducts({ page: 0, size: 50 })
        .then((res) => {
          if (res.data && res.data.content) this.list = res.data.content;
        })
        .catch(() => this.$message.error("加载商品失败"));
    },
    editProduct(product) {
      this.editingId = product.id;
      this.form = { ...product };
      this.showCreateDialog = true;
    },
    async saveProduct() {
      if (!this.form.name) {
        this.$message.error("请输入商品名称");
        return;
      }
      if (!this.form.image) {
        this.$message.error("请输入图片URL");
        return;
      }
      this.saving = true;
      try {
        if (this.editingId) {
          await updateProduct(this.editingId, this.form);
          this.$message.success("编辑成功");
        } else {
          await createProduct(this.form);
          this.$message.success("上架成功");
        }
        this.showCreateDialog = false;
        this.loadProducts();
      } catch (e) {
        this.$message.error(e.response?.data?.message || "操作失败");
      } finally {
        this.saving = false;
      }
    },
    async deleteProduct(id) {
      this.$confirm("确定删除该商品吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          deleteProduct(id)
            .then(() => {
              this.$message.success("删除成功");
              this.loadProducts();
            })
            .catch(() => this.$message.error("删除失败"));
        })
        .catch(() => {});
    },
    resetForm() {
      this.editingId = null;
      this.form = {
        name: "",
        description: "",
        categoryId: 1,
        categoryName: "",
        price: 0,
        originalPrice: 0,
        stock: 100,
        image: "",
        isNew: true,
        onSale: true,
      };
    },
    loadImages() {
      if (this.imageList.length === 0) {
        listImages()
          .then((res) => {
            if (res.data) this.imageList = res.data;
          })
          .catch(() => this.$message.error("加载图片列表失败"));
      }
    },
    handleUploadSuccess(response) {
      if (response.code === 200 && response.data) {
        this.form.image = response.data.url;
        this.imageList.push(response.data);
        this.$message.success("图片上传成功");
      } else {
        this.$message.error(response.message || "上传失败");
      }
    },
    handleUploadError() {
      this.$message.error("图片上传失败");
    },
  },
};
</script>
