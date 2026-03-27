<template>
  <div class="inventory-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">库存管理</h1>
      <p class="page-subtitle">实时监控和管理商品库存</p>
    </div>

    <!-- 库存概览卡片 -->
    <div class="overview-cards">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon">
            <i class="el-icon-box"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ totalProducts }}</div>
            <div class="stat-label">总商品数</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon warning">
            <i class="el-icon-warning"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ lowStockCount }}</div>
            <div class="stat-label">低库存商品</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon danger">
            <i class="el-icon-remove"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ outOfStockCount }}</div>
            <div class="stat-label">缺货商品</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon success">
            <i class="el-icon-check"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ totalStock }}</div>
            <div class="stat-label">总库存量</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-filter-section">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="搜索">
          <el-input
            v-model="searchForm.keyword"
            placeholder="商品名称"
            prefix-icon="el-icon-search"
            class="search-input"
            @keyup.enter="loadInventory"
          />
        </el-form-item>
        <el-form-item label="库存状态">
          <el-select
            v-model="searchForm.stockStatus"
            placeholder="选择状态"
            clearable
          >
            <el-option label="全部" :value="null" />
            <el-option label="缺货" :value="0" />
            <el-option label="低库存(1-10)" :value="1" />
            <el-option label="正常库存" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadInventory">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" @click="batchUpdateDialog = true"
            >批量调整</el-button
          >
        </el-form-item>
      </el-form>
    </div>

    <!-- 库存列表 -->
    <el-card class="inventory-table-card">
      <div slot="header" class="table-header">
        <span class="table-title">库存列表</span>
        <el-button
          size="small"
          type="primary"
          @click="loadInventory"
          icon="el-icon-refresh"
        >
          刷新
        </el-button>
      </div>

      <el-table
        :data="inventoryList"
        v-loading="loading"
        style="width: 100%"
        :height="tableHeight"
        stripe
        border
      >
        <el-table-column label="商品信息" min-width="240">
          <template slot-scope="scope">
            <div class="product-info">
              <img
                v-if="scope.row.image"
                :src="scope.row.image"
                :alt="scope.row.name"
                class="product-image"
              />
              <div v-else class="product-image placeholder">
                <i class="el-icon-picture-outline"></i>
              </div>
              <div class="product-details">
                <div class="product-name">{{ scope.row.name }}</div>
                <div class="product-meta">
                  <span class="product-id">ID: {{ scope.row.id }}</span>
                  <span class="product-price">¥{{ scope.row.price }}</span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="当前库存" width="100" align="center">
          <template slot-scope="scope">
            <div class="stock-display">
              <span class="stock-number">{{ scope.row.stock }}</span>
              <el-tag
                :type="getStockStatusType(scope.row.stock)"
                size="mini"
                effect="plain"
              >
                {{ getStockStatusText(scope.row.stock) }}
              </el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="月销量" width="90" align="center">
          <template slot-scope="scope">
            <span class="sales-badge">{{ scope.row.sales || 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column label="销售转化率" width="110" align="center">
          <template slot-scope="scope">
            <el-progress
              :percentage="
                Math.min(
                  Math.round(
                    ((scope.row.sales || 0) / (scope.row.stock || 1)) * 100,
                  ),
                  100,
                )
              "
              :color="scope.row.sales > 10 ? '#67c23a' : '#409eff'"
              :show-text="false"
              :stroke-width="4"
            />
          </template>
        </el-table-column>

        <el-table-column
          label="快速操作"
          width="160"
          align="center"
          fixed="right"
        >
          <template slot-scope="scope">
            <div class="quick-actions">
              <el-button
                size="mini"
                @click="quickAdjust(scope.row, -1)"
                :disabled="scope.row.stock <= 0"
                icon="el-icon-minus"
                circle
              />
              <span class="action-divider">{{ scope.row.stock }}</span>
              <el-button
                size="mini"
                type="warning"
                @click="quickAdjust(scope.row, 1)"
                icon="el-icon-plus"
                circle
              />
            </div>
          </template>
        </el-table-column>

        <el-table-column
          label="详细操作"
          width="100"
          align="center"
          fixed="right"
        >
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              plain
              @click="editStock(scope.row)"
            >
              调整
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
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalElements"
        />
      </div>
    </el-card>

    <!-- 调整库存对话框 -->
    <el-dialog
      title="调整库存"
      :visible.sync="stockDialog"
      width="400px"
      @close="resetStockForm"
    >
      <el-form
        ref="stockForm"
        :model="stockForm"
        :rules="stockRules"
        label-width="80px"
      >
        <el-form-item label="商品">
          <span>{{ currentProduct ? currentProduct.name : "" }}</span>
        </el-form-item>
        <el-form-item label="当前库存">
          <span>{{ currentProduct ? currentProduct.stock : 0 }}</span>
        </el-form-item>
        <el-form-item label="新库存" prop="newStock">
          <el-input-number
            v-model="stockForm.newStock"
            :min="0"
            :max="99999"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>

      <div slot="footer">
        <el-button @click="stockDialog = false">取消</el-button>
        <el-button
          type="primary"
          @click="confirmStockUpdate"
          :loading="updating"
        >
          确认调整
        </el-button>
      </div>
    </el-dialog>

    <!-- 批量调整对话框 -->
    <el-dialog
      title="批量调整库存"
      :visible.sync="batchUpdateDialog"
      width="600px"
      @close="resetBatchForm"
    >
      <el-form label-width="80px">
        <el-form-item label="调整方式">
          <el-radio-group v-model="batchForm.adjustType">
            <el-radio label="fixed">固定值</el-radio>
            <el-radio label="percentage">百分比</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item
          :label="
            batchForm.adjustType === 'fixed' ? '增加/减少数量' : '调整百分比'
          "
        >
          <el-input-number
            v-model="batchForm.adjustValue"
            :min="batchForm.adjustType === 'fixed' ? -99999 : -100"
            :max="batchForm.adjustType === 'fixed' ? 99999 : 1000"
            controls-position="right"
            style="width: 100%"
          />
          <div class="form-tip">
            {{
              batchForm.adjustType === "fixed"
                ? "正数增加库存，负数减少库存"
                : "正数增加百分比，负数减少百分比"
            }}
          </div>
        </el-form-item>

        <el-form-item label="应用范围">
          <el-checkbox-group v-model="batchForm.targetProducts">
            <el-checkbox label="all">全部商品</el-checkbox>
            <el-checkbox label="low_stock">低库存商品(≤10)</el-checkbox>
            <el-checkbox label="out_of_stock">缺货商品(=0)</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>

      <div slot="footer">
        <el-button @click="batchUpdateDialog = false">取消</el-button>
        <el-button
          type="primary"
          @click="confirmBatchUpdate"
          :loading="batchUpdating"
        >
          确认批量调整
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getInventory,
  updateProductStock,
  batchUpdateStock,
  getStockWarnings,
} from "@/api/merchant";

export default {
  name: "Inventory",
  data() {
    return {
      // 统计数据
      totalProducts: 0,
      lowStockCount: 0,
      outOfStockCount: 0,
      totalStock: 0,

      // 搜索表单
      searchForm: {
        keyword: "",
        stockStatus: null,
      },

      // 库存列表
      inventoryList: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      totalElements: 0,
      tableHeight: 600,

      // 调整库存
      stockDialog: false,
      currentProduct: null,
      stockForm: {
        newStock: 0,
      },
      stockRules: {
        newStock: [
          { required: true, message: "请输入新库存数量", trigger: "blur" },
          { type: "number", min: 0, message: "库存不能小于0", trigger: "blur" },
        ],
      },
      updating: false,

      // 批量调整
      batchUpdateDialog: false,
      batchForm: {
        adjustType: "fixed",
        adjustValue: 0,
        targetProducts: [],
      },
      batchUpdating: false,
    };
  },
  mounted() {
    this.loadInventory();
    this.loadStatistics();
    this.calculateTableHeight();
    window.addEventListener("resize", this.calculateTableHeight);
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.calculateTableHeight);
  },
  methods: {
    // 加载库存列表
    async loadInventory() {
      this.loading = true;
      try {
        const params = {
          page: this.currentPage - 1,
          size: this.pageSize,
          ...this.searchForm,
        };

        const res = await getInventory(params);
        if (res.code === 200) {
          this.inventoryList = res.data.content;
          this.totalElements = res.data.totalElements;
        } else {
          this.$message.error(res.message || "加载库存列表失败");
        }
      } catch (error) {
        this.$message.error("加载库存列表失败");
      } finally {
        this.loading = false;
      }
    },

    // 加载统计数据
    async loadStatistics() {
      try {
        // 获取低库存商品数量
        const lowStockRes = await getStockWarnings(10);
        if (lowStockRes.code === 200) {
          this.lowStockCount = lowStockRes.data.length;
        }

        // 获取缺货商品数量
        const outOfStockRes = await getStockWarnings(0);
        if (outOfStockRes.code === 200) {
          this.outOfStockCount = outOfStockRes.data.filter(
            (p) => p.stock === 0,
          ).length;
        }

        // 计算总商品数和总库存
        const allRes = await getInventory({ page: 0, size: 1000 });
        if (allRes.code === 200) {
          this.totalProducts = allRes.data.totalElements;
          this.totalStock = allRes.data.content.reduce(
            (sum, item) => sum + item.stock,
            0,
          );
        }
      } catch (error) {
        console.error("加载统计数据失败", error);
      }
    },

    // 重置搜索
    resetSearch() {
      this.searchForm.keyword = "";
      this.searchForm.stockStatus = null;
      this.currentPage = 1;
      this.loadInventory();
    },

    // 获取库存状态类型
    getStockStatusType(stock) {
      if (stock === 0) return "danger";
      if (stock <= 10) return "warning";
      return "success";
    },

    // 获取库存状态文本
    getStockStatusText(stock) {
      if (stock === 0) return "缺货";
      if (stock <= 10) return "低库存";
      return "正常";
    },

    // 编辑库存
    editStock(product) {
      this.currentProduct = product;
      this.stockForm.newStock = product.stock;
      this.stockDialog = true;
    },

    // 快速调整库存
    async quickAdjust(product, change) {
      const newStock = product.stock + change;
      if (newStock < 0) {
        this.$message.warning("库存不能小于0");
        return;
      }

      try {
        const res = await updateProductStock(product.id, newStock);
        if (res.code === 200) {
          product.stock = newStock;
          this.$message.success(
            `库存调整成功：${res.data.oldStock} → ${res.data.newStock}`,
          );
          this.loadStatistics();
        } else {
          this.$message.error(res.message || "调整库存失败");
        }
      } catch (error) {
        this.$message.error("调整库存失败");
      }
    },

    // 确认库存调整
    async confirmStockUpdate() {
      this.$refs.stockForm.validate(async (valid) => {
        if (!valid) return;

        this.updating = true;
        try {
          const res = await updateProductStock(
            this.currentProduct.id,
            this.stockForm.newStock,
          );
          if (res.code === 200) {
            this.currentProduct.stock = this.stockForm.newStock;
            this.stockDialog = false;
            this.$message.success(
              `库存调整成功：${res.data.oldStock} → ${res.data.newStock}`,
            );
            this.loadStatistics();
          } else {
            this.$message.error(res.message || "调整库存失败");
          }
        } catch (error) {
          this.$message.error("调整库存失败");
        } finally {
          this.updating = false;
        }
      });
    },

    // 重置库存表单
    resetStockForm() {
      this.$refs.stockForm.resetFields();
      this.currentProduct = null;
    },

    // 确认批量调整
    async confirmBatchUpdate() {
      if (this.batchForm.targetProducts.length === 0) {
        this.$message.warning("请选择应用范围");
        return;
      }

      if (this.batchForm.adjustValue === 0) {
        this.$message.warning("调整值不能为0");
        return;
      }

      this.batchUpdating = true;
      try {
        // 获取所有商品数据
        const allRes = await getInventory({ page: 0, size: 10000 });
        if (allRes.code !== 200) {
          this.$message.error("获取商品列表失败");
          return;
        }

        const allProducts = allRes.data.content;
        const stockUpdates = {};

        // 根据应用范围筛选商品并计算新库存
        allProducts.forEach((product) => {
          let shouldApply = false;

          // 检查是否应该应用调整
          if (this.batchForm.targetProducts.includes("all")) {
            shouldApply = true;
          } else if (
            this.batchForm.targetProducts.includes("low_stock") &&
            product.stock > 0 &&
            product.stock <= 10
          ) {
            shouldApply = true;
          } else if (
            this.batchForm.targetProducts.includes("out_of_stock") &&
            product.stock === 0
          ) {
            shouldApply = true;
          }

          if (shouldApply) {
            let newStock = product.stock;

            if (this.batchForm.adjustType === "fixed") {
              newStock += this.batchForm.adjustValue;
            } else {
              newStock = Math.round(
                newStock * (1 + this.batchForm.adjustValue / 100),
              );
            }

            if (newStock >= 0) {
              stockUpdates[product.id] = newStock;
            }
          }
        });

        if (Object.keys(stockUpdates).length === 0) {
          this.$message.warning("没有符合条件的商品");
          return;
        }

        this.$confirm(
          `确认批量调整 ${Object.keys(stockUpdates).length} 个商品的库存吗？`,
          "警告",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          },
        )
          .then(async () => {
            const res = await batchUpdateStock(stockUpdates);
            if (res.code === 200) {
              this.batchUpdateDialog = false;
              this.$message.success("批量调整库存成功");
              this.loadInventory();
              this.loadStatistics();
            } else {
              this.$message.error(res.message || "批量调整失败");
            }
          })
          .catch(() => {
            // 用户取消
          });
      } catch (error) {
        this.$message.error("批量调整失败");
      } finally {
        this.batchUpdating = false;
      }
    },

    // 重置批量表单
    resetBatchForm() {
      this.batchForm.adjustType = "fixed";
      this.batchForm.adjustValue = 0;
      this.batchForm.targetProducts = [];
    },

    // 分页处理
    handleSizeChange(size) {
      this.pageSize = size;
      this.currentPage = 1;
      this.loadInventory();
    },

    handleCurrentChange(page) {
      this.currentPage = page;
      this.loadInventory();
    },

    // 计算表格高度
    calculateTableHeight() {
      this.tableHeight = window.innerHeight - 400;
    },
  },
};
</script>

<style scoped>
.inventory-management {
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

.overview-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  background: #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #6b7280;
}

.stat-icon.warning {
  background: #fef3c7;
  color: #f59e0b;
}

.stat-icon.danger {
  background: #fee2e2;
  color: #ef4444;
}

.stat-icon.success {
  background: #d1fae5;
  color: #10b981;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
}

.search-filter-section {
  margin-bottom: 24px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-end;
}

.search-input {
  width: 200px;
}

.inventory-table-card {
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-image {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  object-fit: cover;
}

.product-details {
  flex: 1;
}

.product-name {
  font-weight: 500;
  color: #1f2937;
  margin-bottom: 2px;
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 12px;
  color: #ef4444;
  font-weight: 500;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: right;
}

.form-tip {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}
</style>
