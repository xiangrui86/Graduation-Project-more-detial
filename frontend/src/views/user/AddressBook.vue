<template>
  <div class="address-book-page">
    <div class="page-header">
      <h2>收货地址管理</h2>
      <el-button type="primary" icon="el-icon-plus" @click="openDialog()">
        新增地址
      </el-button>
    </div>

    <div class="address-list">
      <div v-loading="loading" element-loading-text="加载中...">
        <div v-if="addresses.length === 0" class="empty-state">
          <i class="el-icon-location-outline"></i>
          <p>暂无收货地址</p>
          <el-button type="primary" @click="openDialog()">
            添加第一个地址
          </el-button>
        </div>

        <div v-else class="address-grid">
          <div
            v-for="addr in addresses"
            :key="addr.id"
            class="address-card"
            :class="{ 'is-default': addr.isDefault }"
          >
            <div class="address-header">
              <div class="receiver-info">
                <span class="receiver-name">{{ addr.receiverName }}</span>
                <span class="receiver-phone">{{ addr.receiverPhone }}</span>
              </div>
              <el-tag
                v-if="addr.isDefault"
                type="success"
                size="small"
                effect="dark"
              >
                默认地址
              </el-tag>
            </div>

            <div class="address-detail">
              <i class="el-icon-location"></i>
              <span>{{ addr.receiverAddress }}</span>
              <span v-if="addr.detailAddress" class="detail">{{
                addr.detailAddress
              }}</span>
            </div>

            <div class="address-actions">
              <el-button
                v-if="!addr.isDefault"
                type="text"
                size="small"
                icon="el-icon-check"
                @click="setDefault(addr.id)"
              >
                设为默认
              </el-button>
              <el-button
                type="text"
                size="small"
                icon="el-icon-edit"
                @click="openDialog(addr)"
              >
                编辑
              </el-button>
              <el-button
                type="text"
                size="small"
                icon="el-icon-delete"
                class="delete-btn"
                @click="deleteAddress(addr.id)"
              >
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog
      :title="dialogMode === 'add' ? '新增地址' : '编辑地址'"
      :visible.sync="dialogVisible"
      width="600px"
      append-to-body
      :close-on-click-modal="false"
    >
      <el-form
        ref="addressForm"
        :model="formData"
        :rules="formRules"
        label-width="80px"
      >
        <el-form-item label="收货人" prop="receiverName">
          <el-input
            v-model="formData.receiverName"
            placeholder="请输入收货人姓名"
            maxlength="32"
          />
        </el-form-item>
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input
            v-model="formData.receiverPhone"
            placeholder="请输入联系电话"
            maxlength="20"
          />
        </el-form-item>
        <el-form-item label="所在地区" prop="region">
          <el-cascader
            v-model="formData.region"
            :options="regionOptions"
            placeholder="请选择省/市/区"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="详细地址" prop="receiverAddress">
          <el-input
            v-model="formData.receiverAddress"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址，如街道、小区、门牌号等"
            maxlength="255"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="门牌号">
          <el-input
            v-model="formData.detailAddress"
            placeholder="如：3 栋 2 单元 501 室"
            maxlength="64"
          />
        </el-form-item>
        <el-form-item label="默认地址">
          <el-switch
            v-model="formData.isDefault"
            active-text="设为默认收货地址"
          />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">
          确定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getAddresses,
  createAddress,
  updateAddress,
  deleteAddress,
  setDefaultAddress,
} from "@/api/user";

export default {
  name: "AddressBook",
  data() {
    const phoneValidator = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入手机号"));
        return;
      }
      const reg = /^1[3-9]\d{9}$/;
      if (reg.test(value)) {
        callback();
      } else {
        callback(new Error("请输入正确的手机号"));
      }
    };

    return {
      loading: false,
      saving: false,
      addresses: [],
      dialogVisible: false,
      dialogMode: "add",
      formData: {
        id: null,
        receiverName: "",
        receiverPhone: "",
        receiverAddress: "",
        detailAddress: "",
        region: [],
        province: "",
        city: "",
        district: "",
        isDefault: false,
      },
      formRules: {
        receiverName: [
          { required: true, message: "请输入收货人姓名", trigger: "blur" },
        ],
        receiverPhone: [
          { required: true, validator: phoneValidator, trigger: "blur" },
        ],
        receiverAddress: [
          { required: true, message: "请输入详细地址", trigger: "blur" },
        ],
      },
      regionOptions: [
        {
          value: "广东省",
          label: "广东省",
          children: [
            {
              value: "广州市",
              label: "广州市",
              children: [
                { value: "天河区", label: "天河区" },
                { value: "越秀区", label: "越秀区" },
                { value: "海珠区", label: "海珠区" },
              ],
            },
            {
              value: "深圳市",
              label: "深圳市",
              children: [
                { value: "南山区", label: "南山区" },
                { value: "福田区", label: "福田区" },
                { value: "宝安区", label: "宝安区" },
              ],
            },
          ],
        },
        {
          value: "北京市",
          label: "北京市",
          children: [
            {
              value: "北京市",
              label: "北京市",
              children: [
                { value: "朝阳区", label: "朝阳区" },
                { value: "海淀区", label: "海淀区" },
                { value: "东城区", label: "东城区" },
              ],
            },
          ],
        },
        {
          value: "上海市",
          label: "上海市",
          children: [
            {
              value: "上海市",
              label: "上海市",
              children: [
                { value: "浦东新区", label: "浦东新区" },
                { value: "黄浦区", label: "黄浦区" },
                { value: "徐汇区", label: "徐汇区" },
              ],
            },
          ],
        },
      ],
    };
  },
  created() {
    this.loadAddresses();
  },
  methods: {
    loadAddresses() {
      this.loading = true;
      getAddresses()
        .then((res) => {
          if (res.code === 200) {
            this.addresses = res.data || [];
          } else {
            this.$message.error(res.message || "加载地址列表失败");
          }
        })
        .catch(() => {
          this.$message.error("加载地址列表失败");
        })
        .finally(() => {
          this.loading = false;
        });
    },
    openDialog(address = null) {
      this.dialogMode = address ? "edit" : "add";
      if (address) {
        this.formData = {
          id: address.id,
          receiverName: address.receiverName || "",
          receiverPhone: address.receiverPhone || "",
          receiverAddress: address.receiverAddress || "",
          detailAddress: address.detailAddress || "",
          region: [address.province, address.city, address.district].filter(
            (item) => item,
          ),
          province: address.province || "",
          city: address.city || "",
          district: address.district || "",
          isDefault: address.isDefault || false,
        };
      } else {
        this.formData = {
          id: null,
          receiverName: "",
          receiverPhone: "",
          receiverAddress: "",
          detailAddress: "",
          region: [],
          province: "",
          city: "",
          district: "",
          isDefault: false,
        };
      }
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.$refs.addressForm?.clearValidate();
      });
    },
    submitForm() {
      this.$refs.addressForm.validate((valid) => {
        if (!valid) return;

        this.saving = true;
        const data = {
          receiverName: this.formData.receiverName,
          receiverPhone: this.formData.receiverPhone,
          receiverAddress: this.formData.receiverAddress,
          detailAddress: this.formData.detailAddress,
          province: this.formData.region[0] || "",
          city: this.formData.region[1] || "",
          district: this.formData.region[2] || "",
          isDefault: this.formData.isDefault,
        };

        const request =
          this.dialogMode === "add"
            ? createAddress(data)
            : updateAddress(this.formData.id, data);

        request
          .then((res) => {
            if (res.code === 200) {
              this.$message.success(
                this.dialogMode === "add" ? "添加成功" : "修改成功",
              );
              this.dialogVisible = false;
              this.loadAddresses();
            } else {
              this.$message.error(res.message || "操作失败");
            }
          })
          .catch(() => {
            this.$message.error("操作失败");
          })
          .finally(() => {
            this.saving = false;
          });
      });
    },
    setDefault(id) {
      this.$confirm("确定要设为默认收货地址吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        setDefaultAddress(id)
          .then((res) => {
            if (res.code === 200) {
              this.$message.success("设置成功");
              this.loadAddresses();
            } else {
              this.$message.error(res.message || "设置失败");
            }
          })
          .catch(() => {
            this.$message.error("设置失败");
          });
      });
    },
    deleteAddress(id) {
      this.$confirm("确定要删除该收货地址吗？", "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        deleteAddress(id)
          .then((res) => {
            if (res.code === 200) {
              this.$message.success("删除成功");
              this.loadAddresses();
            } else {
              this.$message.error(res.message || "删除失败");
            }
          })
          .catch(() => {
            this.$message.error("删除失败");
          });
      });
    },
  },
};
</script>

<style scoped>
.address-book-page {
  padding: 16px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.address-list {
  min-height: 400px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #999;
}

.empty-state i {
  font-size: 64px;
  margin-bottom: 16px;
  color: #ddd;
}

.empty-state p {
  margin: 0 0 20px;
  font-size: 14px;
}

.address-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}

.address-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 2px solid transparent;
  transition: all 0.3s;
}

.address-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.address-card.is-default {
  border-color: #67c23a;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2f1 100%);
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.receiver-info {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.receiver-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.receiver-phone {
  font-size: 14px;
  color: #666;
}

.address-detail {
  margin-bottom: 16px;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
}

.address-detail i {
  margin-right: 6px;
  color: #909399;
}

.address-detail .detail {
  display: block;
  margin-top: 4px;
  color: #999;
  font-size: 13px;
}

.address-actions {
  display: flex;
  gap: 12px;
  border-top: 1px solid #f0f0f0;
  padding-top: 12px;
}

.address-actions .el-button {
  padding: 4px 0;
  font-size: 13px;
}

.address-actions .delete-btn {
  color: #f56c6c;
}

.address-actions .delete-btn:hover {
  color: #f78989;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .address-grid {
    grid-template-columns: 1fr;
  }
}
</style>
