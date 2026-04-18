<template>
  <div class="page-block">
    <div class="page-title">
      <h2>商品详情管理</h2>
      <span class="sub">编辑商品详情和款式</span>
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
          <h3>{{ productName || "商品详情" }}</h3>
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
                    style="
                      width: 100%;
                      height: 100%;
                      object-fit: cover;
                      border-radius: 4px;
                    "
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
                  style="
                    width: 100px;
                    height: 100px;
                    border: 2px dashed #ccc;
                    border-radius: 4px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    cursor: pointer;
                  "
                  @click="$refs.imgUpload.$el.click()"
                >
                  <i
                    class="el-icon-plus"
                    style="font-size: 24px; color: #ccc"
                  ></i>
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
    </el-tabs>
  </div>
</template>

<script>
import { getProduct, updateProduct } from "@/api/admin";

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
    };
  },
  created() {
    this.productId = this.$route.params.id;
    this.loadProductDetail();
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
</style>
