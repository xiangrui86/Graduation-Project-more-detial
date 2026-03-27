<template>
  <el-dialog
    :title="editingProduct ? '编辑商品' : '上架新品'"
    :visible.sync="dialogVisible"
    width="720px"
    :append-to-body="true"
    @close="handleClose"
    class="product-upload-dialog"
  >
    <el-form
      ref="productForm"
      :model="form"
      :rules="rules"
      label-width="100px"
      class="product-form"
    >
      <!-- 商品名称 -->
      <el-form-item label="商品名称" prop="name">
        <el-input
          v-model="form.name"
          placeholder="请输入商品名称"
          maxlength="128"
          show-word-limit
          clearable
        />
      </el-form-item>

      <!-- 商品分类 -->
      <el-form-item label="商品分类" prop="categoryId">
        <el-select
          v-model="form.categoryId"
          placeholder="请选择商品分类"
          filterable
          clearable
          class="category-select"
          @visible-change="handleCategoryDropDown"
        >
          <el-option
            v-for="c in categories"
            :key="c.id"
            :label="c.name"
            :value="c.id"
          />
        </el-select>
        <div class="category-tip">或者在下方输入自定义分类</div>
      </el-form-item>

      <!-- 自定义分类 -->
      <el-form-item label="自定义分类">
        <el-input
          v-model="form.categoryName"
          placeholder="如果没有合适的分类，可以输入新的分类名称"
          clearable
        />
      </el-form-item>

      <!-- 商品简介 -->
      <el-form-item label="商品简介" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="3"
          placeholder="请输入商品简介，简短描述商品特点"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>

      <!-- 商品详情（富文本编辑器） -->
      <el-form-item label="商品详情" prop="detailDescription">
        <div class="editor-wrapper">
          <div ref="editor" class="wang-editor-container"></div>
        </div>
      </el-form-item>

      <!-- 商品主图 -->
      <el-form-item label="商品主图" prop="image">
        <div class="image-upload-container">
          <div class="main-image-section">
            <div v-if="form.image" class="image-preview">
              <img :src="form.image" alt="商品主图" />
              <div class="image-actions">
                <el-button
                  type="danger"
                  size="mini"
                  icon="el-icon-delete"
                  circle
                  @click="form.image = ''"
                />
              </div>
            </div>
            <div
              v-else
              class="upload-placeholder"
              @click="triggerMainImageUpload"
            >
              <i class="el-icon-plus"></i>
              <span>上传主图</span>
            </div>
            <input
              ref="mainImageInput"
              type="file"
              accept="image/*"
              style="display: none"
              @change="handleMainImageChange"
            />
          </div>
          <div class="image-tip">
            <p>建议尺寸：800×800 像素</p>
            <p>支持 JPG、PNG 格式</p>
          </div>
        </div>
      </el-form-item>

      <!-- 详情图片 -->
      <el-form-item label="详情图片">
        <div class="detail-images-container">
          <div
            v-for="(img, index) in form.images"
            :key="index"
            class="detail-image-item"
          >
            <img :src="img" alt="详情图片" />
            <div class="detail-image-actions">
              <el-button
                type="danger"
                size="mini"
                icon="el-icon-delete"
                circle
                @click="removeDetailImage(index)"
              />
            </div>
          </div>
          <div class="add-detail-image" @click="triggerDetailImagesUpload">
            <i class="el-icon-plus"></i>
            <span>添加图片</span>
          </div>
          <input
            ref="detailImagesInput"
            type="file"
            accept="image/*"
            multiple
            style="display: none"
            @change="handleDetailImagesChange"
          />
        </div>
        <div class="image-tip">
          <p>上传商品详情页展示图片</p>
          <p>支持多张图片</p>
        </div>
      </el-form-item>

      <!-- 价格和库存 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="商品价格" prop="price">
            <el-input-number
              v-model="form.price"
              :min="0.01"
              :precision="2"
              :controls="false"
              placeholder="0.00"
              class="price-input"
            >
              <template slot="append">元</template>
            </el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="价格单位">
            <el-input
              v-model="form.unit"
              placeholder="如：件、个、箱"
              clearable
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="原价">
            <el-input-number
              v-model="form.originalPrice"
              :min="0"
              :precision="2"
              :controls="false"
              placeholder="0.00"
              class="price-input"
            >
              <template slot="append">元</template>
            </el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="库存数量" prop="stock">
            <el-input-number
              v-model="form.stock"
              :min="0"
              :precision="0"
              :controls="false"
              placeholder="0"
              class="stock-input"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 商品属性 -->
      <el-form-item label="商品品牌">
        <el-input
          v-model="form.brand"
          placeholder="请输入商品品牌（可选）"
          clearable
        />
      </el-form-item>

      <!-- 上架状态 -->
      <el-form-item label="商品属性">
        <div class="switch-group">
          <el-switch v-model="form.isNew" active-text="新品" inactive-text="" />
          <el-switch
            v-model="form.onSale"
            active-text="上架"
            inactive-text="下架"
          />
        </div>
      </el-form-item>
    </el-form>

    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">
        确认提交
      </el-button>
    </span>

    <!-- 图片上传进度 -->
    <el-dialog
      title="上传中..."
      :visible.sync="uploadingDialog"
      width="300px"
      :append-to-body="true"
      :close-on-click-modal="false"
      class="uploading-dialog"
    >
      <el-progress :percentage="uploadProgress" :format="formatProgress" />
    </el-dialog>
  </el-dialog>
</template>

<script>
import E from "wangeditor";
import { getAllCategories } from "@/api/pub";
import { uploadImage } from "@/api/merchant";

export default {
  name: "ProductUploadDialog",
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    editingProduct: {
      type: Object,
      default: null,
    },
  },
  data() {
    const validatePrice = (rule, value, callback) => {
      if (value === null || value === undefined || value === "") {
        callback(new Error("请输入商品价格"));
      } else if (value <= 0) {
        callback(new Error("商品价格必须大于0"));
      } else {
        callback();
      }
    };

    const validateStock = (rule, value, callback) => {
      if (value === null || value === undefined) {
        callback(new Error("请输入库存数量"));
      } else if (value < 0) {
        callback(new Error("库存数量不能为负数"));
      } else {
        callback();
      }
    };

    return {
      dialogVisible: false,
      submitting: false,
      uploadingDialog: false,
      uploadProgress: 0,
      categories: [],
      editor: null,
      form: {
        name: "",
        description: "",
        detailDescription: "",
        categoryId: null,
        categoryName: "",
        price: 0,
        originalPrice: 0,
        stock: 0,
        unit: "件",
        brand: "",
        image: "",
        images: [],
        isNew: true,
        onSale: true,
      },
      rules: {
        name: [
          { required: true, message: "请输入商品名称", trigger: "blur" },
          {
            min: 2,
            max: 128,
            message: "商品名称长度为 2-128 个字符",
            trigger: "blur",
          },
        ],
        categoryId: [
          {
            validator: (rule, value, callback) => {
              if (!value && !this.form.categoryName) {
                callback(new Error("请选择分类或输入自定义分类"));
              } else {
                callback();
              }
            },
            trigger: "change",
          },
        ],
        price: [{ required: true, validator: validatePrice, trigger: "blur" }],
        stock: [{ required: true, validator: validateStock, trigger: "blur" }],
      },
    };
  },
  watch: {
    visible: {
      immediate: true,
      handler(val) {
        this.dialogVisible = val;
        if (val) {
          this.loadCategories();
          if (this.editingProduct) {
            this.initEditData();
          }
          this.$nextTick(() => {
            this.initEditor();
          });
        }
      },
    },
    dialogVisible(val) {
      this.$emit("update:visible", val);
    },
  },
  mounted() {
    this.$nextTick(() => {
      this.initEditor();
    });
  },
  beforeDestroy() {
    if (this.editor) {
      this.editor.destroy();
      this.editor = null;
    }
  },
  methods: {
    initEditor() {
      if (this.editor) {
        this.editor.destroy();
      }

      this.editor = new E(this.$refs.editor);
      this.editor.config.zIndex = 1000;
      this.editor.config.uploadImgServer = "/api/pub/images/upload";
      this.editor.config.uploadFileName = "file";
      
      // 自定义上传图片处理
      const self = this;
      this.editor.config.customUploadImg = function (resultFiles, insertImgFn) {
        console.log("开始上传图片，文件数：", resultFiles?.length);
        
        if (!resultFiles || resultFiles.length === 0) {
          self.$message.warning("请选择图片");
          return;
        }

        resultFiles.forEach((file, index) => {
          console.log(`上传第 ${index + 1} 张图片:`, file.name);
          
          const formData = new FormData();
          formData.append("file", file);

          const token = localStorage.getItem("token");
          const headers = token ? { Authorization: `Bearer ${token}` } : {};
          
          console.log("发送上传请求，Headers:", headers);

          fetch("/api/pub/images/upload", {
            method: "POST",
            headers: headers,
            body: formData,
          })
            .then((res) => {
              console.log("上传响应状态:", res.status, res.statusText);
              return res.json();
            })
            .then((res) => {
              console.log("后端返回数据:", res);
              
              if (res.code === 200 && res.data && res.data.url) {
                console.log("图片上传成功，URL:", res.data.url);
                // insertImgFn 用来插入图片
                insertImgFn(res.data.url);
                self.$message.success("图片上传成功");
              } else {
                console.error("上传失败，错误信息:", res.message);
                self.$message.error(res.message || "图片上传失败");
              }
            })
            .catch((error) => {
              console.error("上传异常:", error);
              self.$message.error("图片上传异常: " + (error.message || "网络错误"));
            });
        });
      };

      // 配置其他选项
      this.editor.config.zIndex = 1000;
      this.editor.config.height = 300;
      
      this.editor.create();

      if (this.editingProduct && this.form.detailDescription) {
        this.editor.txt.html(this.form.detailDescription);
      }
    },

    initEditData() {
      const product = this.editingProduct;
      this.form = {
        name: product.name || "",
        description: product.description || "",
        detailDescription: product.detailDescription || "",
        categoryId: product.categoryId || null,
        categoryName: product.categoryName || "",
        price: product.price || 0,
        originalPrice: product.originalPrice || 0,
        stock: product.stock || 0,
        unit: product.unit || "件",
        brand: product.brand || "",
        image: product.image || "",
        images: product.detailImages ? product.detailImages.split(",") : [],
        isNew: product.isNew || false,
        onSale: product.onSale !== undefined ? product.onSale : true,
      };

      this.$nextTick(() => {
        if (this.editor && this.form.detailDescription) {
          this.editor.txt.html(this.form.detailDescription);
        }
      });
    },

    handleCategoryDropDown(show) {
      if (show && !this.categories.length) {
        this.loadCategories();
      }
    },

    loadCategories() {
      getAllCategories()
        .then((res) => {
          if (Array.isArray(res.data)) {
            this.categories = res.data;
          } else if (res.data && Array.isArray(res.data.content)) {
            this.categories = res.data.content;
          }
        })
        .catch(() => {
          this.$message.error("加载分类失败");
        });
    },

    triggerMainImageUpload() {
      this.$refs.mainImageInput.click();
    },

    handleMainImageChange(event) {
      const file = event.target.files[0];
      if (file) {
        this.uploadSingleImage(file, (url) => {
          this.form.image = url;
          this.$message.success("主图上传成功");
        });
      }
      event.target.value = "";
    },

    triggerDetailImagesUpload() {
      this.$refs.detailImagesInput.click();
    },

    handleDetailImagesChange(event) {
      const files = Array.from(event.target.files);
      if (files.length) {
        this.uploadMultipleImages(files);
      }
      event.target.value = "";
    },

    async uploadSingleImage(file, callback) {
      const formData = new FormData();
      formData.append("file", file);

      try {
        const token = localStorage.getItem("token");
        const response = await fetch("/api/pub/images/upload", {
          method: "POST",
          headers: token ? { Authorization: `Bearer ${token}` } : {},
          body: formData,
        });

        const res = await response.json();
        if (res.code === 200 && res.data) {
          callback(res.data.url);
        } else {
          this.$message.error(res.message || "上传失败");
        }
      } catch (error) {
        this.$message.error("图片上传失败");
      }
    },

    async uploadMultipleImages(files) {
      this.uploadingDialog = true;
      this.uploadProgress = 0;

      const total = files.length;
      let completed = 0;

      for (const file of files) {
        try {
          const formData = new FormData();
          formData.append("file", file);

          const token = localStorage.getItem("token");
          const response = await fetch("/api/pub/images/upload", {
            method: "POST",
            headers: token ? { Authorization: `Bearer ${token}` } : {},
            body: formData,
          });

          const res = await response.json();
          if (res.code === 200 && res.data) {
            this.form.images.push(res.data.url);
            completed++;
            this.uploadProgress = Math.round((completed / total) * 100);
          } else {
            this.$message.warning(`${file.name} 上传失败`);
            completed++;
            this.uploadProgress = Math.round((completed / total) * 100);
          }
        } catch (error) {
          this.$message.error(`${file.name} 上传失败`);
          completed++;
          this.uploadProgress = Math.round((completed / total) * 100);
        }
      }

      setTimeout(() => {
        this.uploadingDialog = false;
        this.uploadProgress = 0;
        this.$message.success("所有图片上传完成");
      }, 500);
    },

    removeDetailImage(index) {
      this.form.images.splice(index, 1);
    },

    formatProgress(percentage) {
      return percentage + "%";
    },

    handleClose() {
      this.dialogVisible = false;
      this.resetForm();
      this.$emit("close");
    },

    resetForm() {
      this.form = {
        name: "",
        description: "",
        detailDescription: "",
        categoryId: null,
        categoryName: "",
        price: 0,
        originalPrice: 0,
        stock: 0,
        unit: "件",
        brand: "",
        image: "",
        images: [],
        isNew: true,
        onSale: true,
      };

      if (this.editor) {
        this.editor.txt.clear();
      }

      this.$refs.productForm && this.$refs.productForm.resetFields();
    },

    handleSubmit() {
      this.form.detailDescription = this.editor ? this.editor.txt.html() : "";

      this.$refs.productForm.validate(async (valid) => {
        if (!valid) {
          this.$message.warning("请完善表单信息");
          return;
        }

        this.submitting = true;

        try {
          const submitData = {
            name: this.form.name,
            description: this.form.description,
            detailDescription: this.form.detailDescription,
            categoryId: this.form.categoryId,
            categoryName: this.form.categoryName,
            price: this.form.price,
            originalPrice: this.form.originalPrice,
            stock: this.form.stock,
            unit: this.form.unit,
            brand: this.form.brand,
            image: this.form.image,
            images: this.form.images,
            isNew: this.form.isNew,
            onSale: this.form.onSale,
          };

          this.$emit("submit", submitData);
          this.dialogVisible = false;
        } catch (error) {
          this.$message.error("提交失败");
        } finally {
          this.submitting = false;
        }
      });
    },
  },
};
</script>

<style scoped>
.product-upload-dialog /deep/ .el-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.product-upload-dialog /deep/ .el-dialog__header {
  background: linear-gradient(135deg, #ff6900 0%, #ff8a3d 100%);
  padding: 18px 20px;
}

.product-upload-dialog /deep/ .el-dialog__title {
  color: #fff;
  font-weight: 700;
  font-size: 18px;
}

.product-upload-dialog /deep/ .el-dialog__close {
  color: #fff;
}

.product-form {
  padding: 10px 20px;
  max-height: 65vh;
  overflow-y: auto;
}

.product-form /deep/ .el-form-item {
  margin-bottom: 20px;
}

.category-select {
  width: 100%;
}

.category-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
  line-height: 1.4;
}

.editor-wrapper {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.wang-editor-container {
  min-height: 250px;
  max-height: 400px;
  overflow-y: auto;
}

.wang-editor-container /deep/ .w-e-text-container {
  min-height: 200px;
}

.image-upload-container {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.main-image-section {
  flex-shrink: 0;
}

.image-preview {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  border: 1px solid #dcdfe6;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-actions {
  position: absolute;
  top: 4px;
  right: 4px;
}

.upload-placeholder {
  width: 120px;
  height: 120px;
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  background: #fafafa;
}

.upload-placeholder:hover {
  border-color: #ff6900;
  background: #fff5f0;
}

.upload-placeholder i {
  font-size: 32px;
  color: #dcdfe6;
  margin-bottom: 8px;
}

.upload-placeholder span {
  font-size: 13px;
  color: #909399;
}

.image-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.6;
}

.image-tip p {
  margin: 0;
}

.detail-images-container {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.detail-image-item {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  border: 1px solid #dcdfe6;
}

.detail-image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-image-actions {
  position: absolute;
  top: 4px;
  right: 4px;
}

.add-detail-image {
  width: 100px;
  height: 100px;
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  background: #fafafa;
}

.add-detail-image:hover {
  border-color: #ff6900;
  background: #fff5f0;
}

.add-detail-image i {
  font-size: 28px;
  color: #dcdfe6;
  margin-bottom: 6px;
}

.add-detail-image span {
  font-size: 12px;
  color: #909399;
}

.price-input,
.stock-input {
  width: 100%;
}

.price-input /deep/ .el-input__inner,
.stock-input /deep/ .el-input__inner {
  text-align: left;
}

.switch-group {
  display: flex;
  gap: 24px;
}

.switch-group /deep/ .el-switch {
  margin-right: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
}

.dialog-footer .el-button {
  padding: 12px 32px;
  border-radius: 8px;
}

.dialog-footer .el-button--primary {
  background: linear-gradient(135deg, #ff6900 0%, #ff8a3d 100%);
  border: none;
}

.dialog-footer .el-button--primary:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.uploading-dialog /deep/ .el-dialog__body {
  padding: 30px 20px;
}

@media (max-width: 768px) {
  .product-upload-dialog /deep/ .el-dialog {
    width: 95% !important;
    margin: 10px auto;
  }

  .product-form {
    padding: 10px;
    max-height: 60vh;
  }

  .image-upload-container {
    flex-direction: column;
  }

  .detail-images-container {
    justify-content: center;
  }

  .switch-group {
    flex-direction: column;
    gap: 12px;
  }
}
</style>
