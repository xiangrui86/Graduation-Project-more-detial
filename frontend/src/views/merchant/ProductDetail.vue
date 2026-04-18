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
          <div v-if="product.reviewStatus" style="margin-bottom: 12px">
            <el-tag :type="reviewTagType(product.reviewStatus)" size="medium">
              {{ reviewLabel(product.reviewStatus) }}
            </el-tag>
            <div
              v-if="product.reviewStatus === 'REJECTED' && product.reviewReason"
              style="margin-top: 8px; color: #f56c6c"
            >
              拒绝原因：{{ product.reviewReason }}
            </div>
          </div>
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
              <el-input
                v-model="detailForm.brand"
                placeholder="请输入品牌名称"
              />
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
              <el-button
                type="primary"
                @click="saveDetail"
                :loading="detailSaving"
              >
                保存详情
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { getProduct, updateProduct } from "@/api/merchant";

export default {
  name: "MerchantProductDetail",
  data() {
    return {
      productId: null,
      productName: "",
      product: {},
      activeTab: "detail",
      detailForm: {
        detailDescription: "",
        brand: "",
        attributes: "",
      },
      detailImages: [],
      detailSaving: false,
    };
  },
  created() {
    this.productId = this.$route.params.id;
    if (!this.productId) {
      this.$message.error("缺少商品ID");
      return;
    }
    this.loadProductDetail();
  },
  methods: {
    loadProductDetail() {
      getProduct(this.productId)
        .then((res) => {
          const product = res.data;
          this.product = product;
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
    handleDetailImgUploadSuccess(response) {
      if (response.code === 200 && response.data) {
        this.detailImages.push(response.data.url);
        this.$message.success("图片上传成功");
      }
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
    handleUploadError() {
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

.merchant-chat-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.merchant-chat-item {
  padding: 12px 14px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  background: #fbfbfd;
}

.merchant-chat-item-user {
  background: #f8fafc;
}

.merchant-chat-item-merchant {
  background: #eef2ff;
}

.merchant-chat-label {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  font-weight: 600;
}

.merchant-chat-body {
  white-space: pre-wrap;
  word-break: break-word;
  margin-bottom: 8px;
}

.merchant-chat-time {
  font-size: 12px;
  color: #8b8f96;
}

.merchant-chat-reply-panel {
  margin-top: 20px;
  padding: 18px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #ffffff;
}

.merchant-chat-reply-context {
  margin-bottom: 12px;
  padding: 12px;
  border-radius: 10px;
  background: #f8fafc;
  border: 1px solid #dbeafe;
}

.reply-context-title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
}

.reply-context-content {
  font-size: 14px;
  color: #334155;
  line-height: 1.6;
}

.merchant-chat-area {
  display: flex;
  gap: 18px;
  flex-wrap: wrap;
}

.merchant-chat-list-panel,
.merchant-chat-reply-panel {
  flex: 1;
  min-width: 320px;
  padding: 18px;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  background: #ffffff;
}

.merchant-chat-list-title {
  font-weight: 700;
  margin-bottom: 14px;
  color: #111827;
}

.merchant-chat-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 520px;
  overflow-y: auto;
}

.merchant-chat-item {
  padding: 14px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  background: #f8fafc;
}

.merchant-chat-item-user {
  background: #f8fafc;
}

.merchant-chat-item-merchant {
  background: #eef2ff;
}

.merchant-chat-item-selected {
  border-color: #2563eb;
  box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.08);
}

.merchant-chat-label {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 13px;
  color: #334155;
}

.merchant-chat-meta {
  font-size: 12px;
  color: #6b7280;
}

.merchant-chat-body {
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 14px;
  color: #111827;
  margin-bottom: 10px;
}

.merchant-chat-actions {
  display: flex;
  justify-content: flex-end;
}

.merchant-chat-reply-hint {
  padding: 14px;
  border-radius: 10px;
  background: #f9fafb;
  color: #475569;
  font-size: 13px;
  margin-bottom: 12px;
}
</style>
