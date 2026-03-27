<template>
  <div class="page-block">
    <div class="page-title">
      <h2>商品详情管理</h2>
      <span class="sub">编辑商品详情、管理规格和款式</span>
    </div>

    <!-- 返回列表按钮 -->
    <div style="margin-bottom: 12px">
      <el-button @click="$router.back()">← 返回</el-button>
    </div>

    <!-- 标签页 -->
    <el-tabs v-model="activeTab" type="card">
      <!-- 详情页管理 -->
      <el-tab-pane label="商品详情" name="detail">
        <div style="max-width: 800px; margin-top: 20px">
          <h3>{{ productName || '商品详情' }}</h3>
          <el-form :model="detailForm" label-width="100px">
            <el-form-item label="详细描述">
              <el-input
                v-model="detailForm.detailDescription"
                type="textarea"
                :rows="6"
                placeholder="请输入商品详细描述"
              />
            </el-form-item>
            <el-form-item label="详情图片">
              <div style="display: flex; gap: 10px; flex-wrap: wrap">
                <div
                  v-for="(img, idx) in detailImages"
                  :key="idx"
                  style="position: relative; width: 100px; height: 100px"
                >
                  <img
                    :src="img"
                    style="width: 100%; height: 100%; object-fit: cover; border-radius: 4px"
                  />
                  <el-button
                    icon="el-icon-delete"
                    type="danger"
                    size="mini"
                    circle
                    @click="detailImages.splice(idx, 1)"
                    style="position: absolute; right: -10px; top: -10px"
                  />
                </div>
                <div
                  style="width: 100px; height: 100px; border: 2px dashed #ccc; border-radius: 4px; display: flex; align-items: center; justify-content: center; cursor: pointer"
                  @click="$refs.imgUpload.$el.click()"
                >
                  <span style="font-size: 24px; color: #909399">+</span>
                </div>
                <el-upload
                  ref="imgUpload"
                  :auto-upload="true"
                  accept="image/*"
                  :on-success="handleDetailImgUploadSuccess"
                  :on-error="handleUploadError"
                  action="/api/pub/images/upload"
                  :show-file-list="false"
                  style="display: none"
                >
                </el-upload>
              </div>
              <div style="font-size: 12px; color: #909399; margin-top: 10px">
                上传商品详情页展示的轮播图
              </div>
            </el-form-item>
            <el-form-item label="品牌">
              <el-input v-model="detailForm.brand" placeholder="请输入品牌名称" />
            </el-form-item>
            <el-form-item label="商品参数">
              <el-input
                v-model="detailForm.attributes"
                type="textarea"
                :rows="4"
                placeholder="输入商品参数信息，如：材质：纯棉，尺寸：S/M/L/XL等"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveDetail" :loading="detailSaving">
                保存详情
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>

      <!-- 规格管理 -->
      <el-tab-pane label="规格管理" name="specs">
        <div style="margin-top: 20px">
          <div style="margin-bottom: 12px">
            <el-button type="primary" @click="showSpecDialog = true">
              + 添加规格
            </el-button>
          </div>
          <el-table :data="specs" border>
            <el-table-column prop="specName" label="规格名称" width="120" />
            <el-table-column prop="specValue" label="规格值" width="120" />
            <el-table-column prop="stock" label="库存" width="80" />
            <el-table-column prop="priceDelta" label="价格差" width="100">
              <template slot-scope="scope">
                <span v-if="scope.row.priceDelta">
                  +￥{{ scope.row.priceDelta.toFixed(2) }}
                </span>
                <span v-else>无</span>
              </template>
            </el-table-column>
            <el-table-column prop="enabled" label="启用" width="80">
              <template slot-scope="scope">
                <el-tag :type="scope.row.enabled ? 'success' : 'info'">
                  {{ scope.row.enabled ? "是" : "否" }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="图片" width="100">
              <template slot-scope="scope">
                <img
                  v-if="scope.row.image"
                  :src="scope.row.image"
                  style="max-width: 60px; max-height: 60px; border-radius: 4px"
                />
                <span v-else style="color: #909399">无</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="editSpec(scope.row)">
                  编辑
                </el-button>
                <el-button
                  type="text"
                  size="small"
                  @click="deleteSpec(scope.row.id)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 规格编辑对话框 -->
    <el-dialog
      :title="editingSpecId ? '编辑规格' : '添加规格'"
      :visible.sync="showSpecDialog"
      width="500px"
      @close="resetSpecForm"
      :append-to-body="true"
    >
      <el-form :model="specForm" label-width="100px">
        <el-form-item label="规格名称">
          <el-input v-model="specForm.specName" placeholder="如: 颜色, 尺码, 容量" />
        </el-form-item>
        <el-form-item label="规格值">
          <el-input v-model="specForm.specValue" placeholder="如: 红色, M, 128GB" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input
            v-model.number="specForm.stock"
            type="number"
            placeholder="此规格的库存数量"
          />
        </el-form-item>
        <el-form-item label="价格差">
          <el-input
            v-model.number="specForm.priceDelta"
            type="number"
            step="0.01"
            placeholder="在基础价格上增加的金额（可为负）"
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input
            v-model.number="specForm.sortOrder"
            type="number"
            placeholder="显示顺序"
          />
        </el-form-item>
        <el-form-item label="规格图片">
          <div style="display: flex; gap: 10px; align-items: flex-start">
            <div style="flex: 1">
              <el-input v-model="specForm.image" placeholder="规格图片URL" />
            </div>
            <el-upload
              :auto-upload="true"
              accept="image/*"
              :on-success="handleSpecImgUploadSuccess"
              :on-error="handleUploadError"
              action="/api/pub/images/upload"
              :show-file-list="false"
            >
              <el-button type="primary" size="small">上传</el-button>
            </el-upload>
          </div>
          <div v-if="specForm.image" style="margin-top: 10px">
            <img
              :src="specForm.image"
              style="max-width: 150px; max-height: 150px; border-radius: 4px"
            />
          </div>
        </el-form-item>
            <i class="el-icon-plus"></i>
          </el-upload>
          <div style="font-size: 12px; color: #909399; margin-top: 5px">
            支持多图上传，拖拽排序，首图为主图
          </div>
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="specForm.enabled" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showSpecDialog = false">取消</el-button>
        <el-button type="primary" @click="saveSpec" :loading="specSaving">
          保存
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getProduct,
  updateProduct,
  getProductSpecs,
  createProductSpec,
  updateProductSpec,
  deleteProductSpec,
} from "@/api/merchant";

export default {
  name: "MerchantProductDetail",
  data() {
    return {
      productId: null,
      productName: "",
      activeTab: "detail",
      detailForm: {
        detailDescription: "",
        brand: "",
        attributes: "",
      },
      detailImages: [],
      detailSaving: false,
      specs: [],
      showSpecDialog: false,
      editingSpecId: null,
      specSaving: false,
      specForm: {
        specName: "",
        specValue: "",
        stock: 0,
        priceDelta: 0,
        image: "",
        sortOrder: 0,
        enabled: true,
      },
    };
  },
  created() {
    this.productId = this.$route.params.id;
    if (!this.productId) {
      this.$message.error("缺少商品ID");
      return;
    }
    this.loadProductDetail();
    this.loadSpecs();
  },
  methods: {
    loadProductDetail() {
      getProduct(this.productId)
        .then((res) => {
          const product = res.data;
          this.productName = product.name;
          this.detailForm = {
            detailDescription: product.detailDescription || "",
            brand: product.brand || "",
            attributes: product.attributes || "",
          };
          if (product.detailImages) {
            this.detailImages = product.detailImages
              .split(",")
              .filter((img) => img.trim());
          }
        })
        .catch(() => this.$message.error("加载商品详情失败"));
    },
    loadSpecs() {
      getProductSpecs(this.productId)
        .then((res) => {
          this.specs = res.data || [];
        })
        .catch(() => this.$message.error("加载规格列表失败"));
    },
    saveDetail() {
      this.detailSaving = true;
      const data = {
        ...this.detailForm,
        detailImages: this.detailImages.join(","),
      };
      updateProduct(this.productId, data)
        .then(() => {
          this.$message.success("详情保存成功");
          this.loadProductDetail();
        })
        .catch((e) => {
          this.$message.error(e.response?.data?.message || "保存失败");
        })
        .finally(() => {
          this.detailSaving = false;
        });
    },
    editSpec(spec) {
      this.editingSpecId = spec.id;
      this.specForm = { ...spec };
      this.showSpecDialog = true;
    },
    saveSpec() {
      if (!this.specForm.specName) {
        this.$message.error("请输入规格名称");
        return;
      }
      if (!this.specForm.specValue) {
        this.$message.error("请输入规格值");
        return;
      }
      if (this.specForm.stock === null || this.specForm.stock === "") {
        this.$message.error("请输入库存");
        return;
      }
      this.specSaving = true;
      const promise = this.editingSpecId
        ? updateProductSpec(this.editingSpecId, this.specForm)
        : createProductSpec(this.productId, this.specForm);

      promise
        .then(() => {
          this.$message.success(
            this.editingSpecId ? "规格更新成功" : "规格添加成功"
          );
          this.showSpecDialog = false;
          this.loadSpecs();
        })
        .catch((e) => {
          this.$message.error(e.response?.data?.message || "操作失败");
        })
        .finally(() => {
          this.specSaving = false;
        });
    },
    async deleteSpec(specId) {
      this.$confirm("确定删除该规格吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          deleteProductSpec(specId)
            .then(() => {
              this.$message.success("删除成功");
              this.loadSpecs();
            })
            .catch(() => this.$message.error("删除失败"));
        })
        .catch(() => {});
    },
    resetSpecForm() {
      this.editingSpecId = null;
      this.specForm = {
        specName: "",
        specValue: "",
        stock: 0,
        priceDelta: 0,
        image: "",
        sortOrder: 0,
        enabled: true,
      };
    },
    handleDetailImgUploadSuccess(response) {
      if (response.code === 200 && response.data) {
        this.detailImages.push(response.data.url);
        this.$message.success("图片上传成功");
      }
    },
    handleSpecImgUploadSuccess(response) {
      if (response.code === 200 && response.data) {
        this.specForm.image = response.data.url;
        this.$message.success("图片上传成功");
      }
    },
    handleUploadError(err) {
      this.$message.error("图片上传失败");
    },
  },
};
</script>

<style scoped>
.page-block {
  padding: 20px;
  background: #fff;
}

.page-title {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 10px;
}

.page-title h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.page-title .sub {
  font-size: 12px;
  color: #909399;
}
</style>
