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
                  <i class="el-icon-plus" style="font-size: 24px; color: #ccc"></i>
                </div>
              </div>
              <input
                ref="imgUpload"
                type="file"
                multiple
                accept="image/*"
                style="display: none"
                @change="handleImageUpload"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveDetail">保存详情</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>

      <!-- 规格管理 -->
      <el-tab-pane label="商品规格" name="specs">
        <div style="margin-top: 20px">
          <el-button type="primary" @click="addSpec" style="margin-bottom: 20px">
            添加规格
          </el-button>
          <el-table :data="specs" border stripe>
            <el-table-column prop="name" label="规格名称" width="200" />
            <el-table-column prop="value" label="规格值" width="200" />
            <el-table-column prop="priceAdjustment" label="价格调整" width="150">
              <template slot-scope="scope">
                <span v-if="scope.row.priceAdjustment > 0">+¥{{ scope.row.priceAdjustment }}</span>
                <span v-else-if="scope.row.priceAdjustment < 0">-¥{{ Math.abs(scope.row.priceAdjustment) }}</span>
                <span v-else>无调整</span>
              </template>
            </el-table-column>
            <el-table-column prop="stock" label="库存" width="100" />
            <el-table-column label="操作" width="200">
              <template slot-scope="scope">
                <el-button type="text" @click="editSpec(scope.row)">编辑</el-button>
                <el-button type="text" @click="deleteSpec(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 规格编辑弹窗 -->
    <el-dialog :title="specDialogTitle" :visible.sync="specDialogVisible" width="500px">
      <el-form :model="specForm" label-width="100px">
        <el-form-item label="规格名称">
          <el-input v-model="specForm.name" placeholder="如：颜色、尺寸" />
        </el-form-item>
        <el-form-item label="规格值">
          <el-input v-model="specForm.value" placeholder="如：红色、XL" />
        </el-form-item>
        <el-form-item label="价格调整">
          <el-input-number
            v-model="specForm.priceAdjustment"
            :precision="2"
            :min="-999999"
            :max="999999"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number
            v-model="specForm.stock"
            :min="0"
            :max="999999"
            controls-position="right"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="specDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSpec">保存</el-button>
      </div>
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
} from "@/api/admin";

export default {
  name: "AdminProductDetail",
  data() {
    return {
      activeTab: "detail",
      productId: null,
      productName: "",
      detailForm: {
        detailDescription: "",
      },
      detailImages: [],
      specs: [],
      specDialogVisible: false,
      specDialogTitle: "添加规格",
      editingSpec: null,
      specForm: {
        name: "",
        value: "",
        priceAdjustment: 0,
        stock: 0,
      },
    };
  },
  created() {
    this.productId = this.$route.params.id;
    this.loadProductDetail();
    this.loadSpecs();
  },
  methods: {
    async loadProductDetail() {
      try {
        const res = await getProduct(this.productId);
        const product = res.data;
        this.productName = product.name;
        this.detailForm.detailDescription = product.detailDescription || "";
        this.detailImages = product.detailImages || [];
      } catch (error) {
        this.$message.error("加载商品详情失败");
      }
    },

    async loadSpecs() {
      try {
        const res = await getProductSpecs(this.productId);
        this.specs = res.data || [];
      } catch (error) {
        this.$message.error("加载规格失败");
      }
    },

    handleImageUpload(event) {
      const files = event.target.files;
      for (let file of files) {
        const reader = new FileReader();
        reader.onload = (e) => {
          this.detailImages.push(e.target.result);
        };
        reader.readAsDataURL(file);
      }
      event.target.value = "";
    },

    async saveDetail() {
      try {
        await updateProduct(this.productId, {
          detailDescription: this.detailForm.detailDescription,
          detailImages: this.detailImages,
        });
        this.$message.success("保存成功");
      } catch (error) {
        this.$message.error("保存失败");
      }
    },

    addSpec() {
      this.editingSpec = null;
      this.specDialogTitle = "添加规格";
      this.specForm = {
        name: "",
        value: "",
        priceAdjustment: 0,
        stock: 0,
      };
      this.specDialogVisible = true;
    },

    editSpec(spec) {
      this.editingSpec = spec;
      this.specDialogTitle = "编辑规格";
      this.specForm = { ...spec };
      this.specDialogVisible = true;
    },

    async saveSpec() {
      try {
        if (this.editingSpec) {
          await updateProductSpec(this.editingSpec.id, this.specForm);
          this.$message.success("规格更新成功");
        } else {
          await createProductSpec(this.productId, this.specForm);
          this.$message.success("规格添加成功");
        }
        this.specDialogVisible = false;
        this.loadSpecs();
      } catch (error) {
        this.$message.error("操作失败");
      }
    },

    async deleteSpec(specId) {
      try {
        await this.$confirm("确定删除该规格吗？", "删除确认", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
        await deleteProductSpec(specId);
        this.$message.success("删除成功");
        this.loadSpecs();
      } catch (error) {
        if (error !== "cancel") {
          this.$message.error("删除失败");
        }
      }
    },
  },
};
</script>

<style scoped>
.page-block {
  padding: 24px;
  background: #f5f7fa;
  min-height: calc(100vh - 120px);
}

.page-title {
  margin-bottom: 20px;
}

.page-title h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.page-title .sub {
  color: #909399;
  font-size: 14px;
}
</style></content>
<parameter name="filePath">d:\code\code\frontend\src\views\admin\ProductDetail.vue