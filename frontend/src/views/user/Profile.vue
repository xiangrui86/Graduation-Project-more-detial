<template>
  <div class="profile-page">
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">个人中心</h2>
        <p class="page-subtitle">管理您的个人资料、账户安全和收货信息</p>
      </div>
    </div>

    <div class="profile-container">
      <div class="profile-sidebar">
        <el-card class="user-info-card">
          <div class="user-avatar-section">
            <div class="avatar-display">
              <img
                v-if="form.avatar"
                :src="form.avatar"
                class="avatar-large"
                alt="avatar"
              />
              <div v-else class="avatar-large avatar-fallback">
                {{
                  (form.nickname || form.username || "U")
                    .slice(0, 1)
                    .toUpperCase()
                }}
              </div>
            </div>
            <div class="user-name-display">
              <h3>{{ form.nickname || form.username }}</h3>
              <p class="user-id">ID: {{ form.username }}</p>
            </div>
          </div>

          <el-divider />

          <div class="upload-section">
            <el-upload
              class="avatar-uploader"
              action="/api/pub/images/upload"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :on-error="handleAvatarError"
              :before-upload="beforeAvatarUpload"
              :headers="uploadHeaders"
            >
              <el-button type="primary" block icon="el-icon-upload">
                {{ form.avatar ? "更换头像" : "上传头像" }}
              </el-button>
            </el-upload>
            <p class="upload-tip">支持 JPG/PNG 格式，大小不超过 2MB</p>
          </div>
        </el-card>

        <el-card class="quick-links-card">
          <div class="card-title">
            <i class="el-icon-menu"></i>
            <span>快捷操作</span>
          </div>
          <div class="quick-links">
            <div class="link-item" @click="$router.push('/orders')">
              <div class="link-icon">
                <i class="el-icon-shopping-bag-1"></i>
              </div>
              <div class="link-info">
                <div class="link-name">我的订单</div>
                <div class="link-desc">查看订单状态</div>
              </div>
              <i class="el-icon-arrow-right link-arrow"></i>
            </div>
            <div class="link-item" @click="$router.push('/favorite')">
              <div class="link-icon">
                <i class="el-icon-star-on"></i>
              </div>
              <div class="link-info">
                <div class="link-name">我的收藏</div>
                <div class="link-desc">管理收藏商品</div>
              </div>
              <i class="el-icon-arrow-right link-arrow"></i>
            </div>
            <div class="link-item" @click="$router.push('/address-book')">
              <div class="link-icon">
                <i class="el-icon-s-address-book"></i>
              </div>
              <div class="link-info">
                <div class="link-name">收货地址</div>
                <div class="link-desc">管理收货地址</div>
              </div>
              <i class="el-icon-arrow-right link-arrow"></i>
            </div>
          </div>
        </el-card>
      </div>

      <div class="profile-main">
        <el-card class="form-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">
                <i class="el-icon-user"></i>
                基本信息
              </span>
            </div>
          </template>

          <el-form
            ref="profileForm"
            :model="form"
            :rules="formRules"
            label-width="100px"
            class="profile-form"
          >
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" disabled class="disabled-input">
                <template #prefix>
                  <i class="el-icon-user"></i>
                </template>
              </el-input>
            </el-form-item>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="昵称" prop="nickname">
                  <el-input
                    v-model="form.nickname"
                    placeholder="请输入昵称"
                    maxlength="32"
                    show-word-limit
                  >
                    <template #prefix>
                      <i class="el-icon-edit-outline"></i>
                    </template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="form.gender" class="gender-group">
                    <el-radio-button label="MALE">
                      <i class="el-icon-male"></i> 男
                    </el-radio-button>
                    <el-radio-button label="FEMALE">
                      <i class="el-icon-female"></i> 女
                    </el-radio-button>
                    <el-radio-button label="OTHER">
                      <i class="el-icon-question"></i> 其他
                    </el-radio-button>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="手机号" prop="phone">
                  <el-input
                    v-model="form.phone"
                    placeholder="请输入手机号"
                    maxlength="11"
                  >
                    <template #prefix>
                      <i class="el-icon-mobile-phone"></i>
                    </template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input
                    v-model="form.email"
                    placeholder="请输入邮箱"
                    maxlength="64"
                  >
                    <template #prefix>
                      <i class="el-icon-message"></i>
                    </template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <el-divider content-position="left">
              <i class="el-icon-location"></i>
              默认收货信息
            </el-divider>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="收货人" prop="receiverName">
                  <el-input
                    v-model="form.receiverName"
                    placeholder="请输入收货人姓名"
                    maxlength="32"
                  >
                    <template #prefix>
                      <i class="el-icon-user"></i>
                    </template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="收货电话" prop="receiverPhone">
                  <el-input
                    v-model="form.receiverPhone"
                    placeholder="请输入联系电话"
                    maxlength="20"
                  >
                    <template #prefix>
                      <i class="el-icon-mobile-phone"></i>
                    </template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="收货地址" prop="receiverAddress">
              <el-input
                v-model="form.receiverAddress"
                type="textarea"
                :rows="3"
                placeholder="请输入详细地址，如省市区、街道、小区、门牌号等"
                maxlength="255"
                show-word-limit
              />
            </el-form-item>

            <div class="form-actions">
              <el-button @click="loadProfile" icon="el-icon-refresh">
                重置
              </el-button>
              <el-button
                type="primary"
                :loading="saving"
                @click="saveProfile"
                icon="el-icon-check"
              >
                {{ saving ? "保存中..." : "保存修改" }}
              </el-button>
            </div>
          </el-form>
        </el-card>

        <el-card class="address-book-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">
                <i class="el-icon-s-address-book"></i>
                收货地址管理
              </span>
              <el-button
                type="primary"
                size="small"
                icon="el-icon-plus"
                @click="$router.push('/address-book')"
              >
                管理地址簿
              </el-button>
            </div>
          </template>

          <div class="address-book-content">
            <div class="address-book-desc">
              <i class="el-icon-info"></i>
              <div class="desc-text">
                <p><strong>多地址管理功能</strong></p>
                <p>您可以添加多个收货地址，支持设置默认地址</p>
                <p>下单时可以自由选择不同的收货地址</p>
              </div>
            </div>
            <div class="address-book-stats" v-if="form.receiverName">
              <div class="stat-item">
                <div class="stat-label">当前默认收货人</div>
                <div class="stat-value">{{ form.receiverName }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">联系电话</div>
                <div class="stat-value">{{ form.receiverPhone }}</div>
              </div>
            </div>
            <div class="address-book-empty" v-else>
              <i class="el-icon-location-outline"></i>
              <p>暂无默认收货信息</p>
              <span>请在上方填写或前往地址簿添加</span>
            </div>
          </div>
        </el-card>

        <el-card class="security-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">
                <i class="el-icon-lock"></i>
                账户安全
              </span>
            </div>
          </template>

          <div class="security-items">
            <div class="security-item">
              <div class="security-info">
                <div class="security-icon">
                  <i class="el-icon-key"></i>
                </div>
                <div class="security-detail">
                  <div class="security-label">登录密码</div>
                  <div class="security-desc">
                    定期修改密码可以提高账户安全性
                  </div>
                </div>
              </div>
              <el-button
                type="primary"
                plain
                size="small"
                icon="el-icon-edit"
                @click="showPasswordDialog = true"
              >
                修改密码
              </el-button>
            </div>

            <div class="security-item">
              <div class="security-info">
                <div class="security-icon">
                  <i class="el-icon-phone"></i>
                </div>
                <div class="security-detail">
                  <div class="security-label">手机绑定</div>
                  <div class="security-desc">
                    <span v-if="form.phone">{{ form.phone }}</span>
                    <span v-else>未绑定</span>
                  </div>
                </div>
              </div>
              <el-button
                type="primary"
                plain
                size="small"
                icon="el-icon-edit"
                @click="openPhoneDialog"
              >
                {{ form.phone ? "更换" : "绑定" }}
              </el-button>
            </div>

            <div class="security-item">
              <div class="security-info">
                <div class="security-icon">
                  <i class="el-icon-message"></i>
                </div>
                <div class="security-detail">
                  <div class="security-label">邮箱绑定</div>
                  <div class="security-desc">
                    <span v-if="form.email">{{ form.email }}</span>
                    <span v-else>未绑定</span>
                  </div>
                </div>
              </div>
              <el-button
                type="primary"
                plain
                size="small"
                icon="el-icon-edit"
                @click="openEmailDialog"
              >
                {{ form.email ? "更换" : "绑定" }}
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 修改密码弹窗 -->
    <el-dialog
      title="修改密码"
      :visible.sync="showPasswordDialog"
      width="500px"
      append-to-body
      :close-on-click-modal="false"
    >
      <el-form
        ref="passwordForm"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入当前密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template slot="footer">
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button
          type="primary"
          :loading="changingPassword"
          @click="handleChangePassword"
        >
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 绑定手机弹窗 -->
    <el-dialog
      :title="form.phone ? '更换手机号' : '绑定手机号'"
      :visible.sync="showPhoneDialog"
      width="500px"
      append-to-body
      :close-on-click-modal="false"
    >
      <el-form
        ref="phoneForm"
        :model="phoneForm"
        :rules="phoneFormRules"
        label-width="100px"
      >
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="phoneForm.phone"
            placeholder="请输入手机号"
            maxlength="11"
          >
            <template #prefix>
              <i class="el-icon-mobile-phone"></i>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
      <template slot="footer">
        <el-button @click="showPhoneDialog = false">取消</el-button>
        <el-button
          type="primary"
          :loading="updatingPhone"
          @click="handleUpdatePhone"
        >
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 绑定邮箱弹窗 -->
    <el-dialog
      :title="form.email ? '更换邮箱' : '绑定邮箱'"
      :visible.sync="showEmailDialog"
      width="500px"
      append-to-body
      :close-on-click-modal="false"
    >
      <el-form
        ref="emailForm"
        :model="emailForm"
        :rules="emailFormRules"
        label-width="100px"
      >
        <el-form-item label="邮箱地址" prop="email">
          <el-input v-model="emailForm.email" placeholder="请输入邮箱地址">
            <template #prefix>
              <i class="el-icon-message"></i>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
      <template slot="footer">
        <el-button @click="showEmailDialog = false">取消</el-button>
        <el-button
          type="primary"
          :loading="updatingEmail"
          @click="handleUpdateEmail"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getProfile, updateProfile } from "@/api/user";

export default {
  name: "UserProfile",
  data() {
    const phoneValidator = (rule, value, callback) => {
      if (!value) {
        callback();
        return;
      }
      const reg = /^1[3-9]\d{9}$/;
      if (reg.test(value)) {
        callback();
      } else {
        callback(new Error("请输入正确的手机号"));
      }
    };

    const emailValidator = (rule, value, callback) => {
      if (!value) {
        callback();
        return;
      }
      const reg = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (reg.test(value)) {
        callback();
      } else {
        callback(new Error("请输入正确的邮箱地址"));
      }
    };

    return {
      saving: false,
      form: {
        username: "",
        nickname: "",
        avatar: "",
        gender: "",
        phone: "",
        email: "",
        receiverName: "",
        receiverPhone: "",
        receiverAddress: "",
      },
      formRules: {
        nickname: [
          { required: true, message: "请输入昵称", trigger: "blur" },
          {
            min: 2,
            max: 32,
            message: "昵称长度在 2-32 个字符之间",
            trigger: "blur",
          },
        ],
        phone: [{ validator: phoneValidator, trigger: "blur" }],
        email: [{ validator: emailValidator, trigger: "blur" }],
        receiverName: [
          { required: true, message: "请输入收货人姓名", trigger: "blur" },
        ],
        receiverPhone: [
          { required: true, message: "请输入收货电话", trigger: "blur" },
        ],
        receiverAddress: [
          { required: true, message: "请输入收货地址", trigger: "blur" },
        ],
      },
      uploadHeaders: {
        Authorization: `Bearer ${localStorage.getItem("token") || ""}`,
      },

      // 密码修改相关
      showPasswordDialog: false,
      changingPassword: false,
      passwordForm: {
        oldPassword: "",
        newPassword: "",
        confirmPassword: "",
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: "请输入当前密码", trigger: "blur" },
        ],
        newPassword: [
          { required: true, message: "请输入新密码", trigger: "blur" },
          {
            min: 6,
            max: 20,
            message: "密码长度在 6-20 个字符之间",
            trigger: "blur",
          },
        ],
        confirmPassword: [
          { required: true, message: "请再次输入新密码", trigger: "blur" },
          {
            validator: (rule, value, callback) => {
              if (value !== this.passwordForm.newPassword) {
                callback(new Error("两次输入的密码不一致"));
              } else {
                callback();
              }
            },
            trigger: "blur",
          },
        ],
      },

      // 手机绑定相关
      showPhoneDialog: false,
      updatingPhone: false,
      phoneForm: {
        phone: "",
      },
      phoneFormRules: {
        phone: [{ required: true, validator: phoneValidator, trigger: "blur" }],
      },

      // 邮箱绑定相关
      showEmailDialog: false,
      updatingEmail: false,
      emailForm: {
        email: "",
      },
      emailFormRules: {
        email: [{ required: true, validator: emailValidator, trigger: "blur" }],
      },
    };
  },
  created() {
    this.loadProfile();
  },
  methods: {
    // 打开手机绑定弹窗
    openPhoneDialog() {
      this.phoneForm = {
        phone: this.form.phone || "",
      };
      this.showPhoneDialog = true;
      this.$nextTick(() => {
        this.$refs.phoneForm?.clearValidate();
      });
    },

    // 打开邮箱绑定弹窗
    openEmailDialog() {
      this.emailForm = {
        email: this.form.email || "",
      };
      this.showEmailDialog = true;
      this.$nextTick(() => {
        this.$refs.emailForm?.clearValidate();
      });
    },

    // 修改密码
    handleChangePassword() {
      this.$refs.passwordForm.validate((valid) => {
        if (!valid) return;

        this.changingPassword = true;
        // TODO: 调用修改密码 API
        setTimeout(() => {
          this.$message.success("密码修改成功");
          this.showPasswordDialog = false;
          this.passwordForm = {
            oldPassword: "",
            newPassword: "",
            confirmPassword: "",
          };
          this.changingPassword = false;
        }, 1000);
      });
    },

    // 更新手机号
    handleUpdatePhone() {
      this.$refs.phoneForm.validate((valid) => {
        if (!valid) return;

        this.updatingPhone = true;
        // TODO: 调用更新手机号 API
        setTimeout(() => {
          this.$message.success("手机号更新成功");
          this.showPhoneDialog = false;
          this.form.phone = this.phoneForm.phone;
          this.updatingPhone = false;
        }, 1000);
      });
    },

    // 更新邮箱
    handleUpdateEmail() {
      this.$refs.emailForm.validate((valid) => {
        if (!valid) return;

        this.updatingEmail = true;
        // TODO: 调用更新邮箱 API
        setTimeout(() => {
          this.$message.success("邮箱更新成功");
          this.showEmailDialog = false;
          this.form.email = this.emailForm.email;
          this.updatingEmail = false;
        }, 1000);
      });
    },
    loadProfile() {
      getProfile().then((res) => {
        if (res.code !== 200 || !res.data) {
          this.$message.error(res.message || "获取个人信息失败");
          return;
        }
        this.form = {
          username: res.data.username || "",
          nickname: res.data.nickname || "",
          avatar: res.data.avatar || "",
          gender: res.data.gender || "MALE",
          phone: res.data.phone || "",
          email: res.data.email || "",
          receiverName: res.data.receiverName || "",
          receiverPhone: res.data.receiverPhone || "",
          receiverAddress: res.data.receiverAddress || "",
        };
      });
    },
    beforeAvatarUpload(file) {
      const isImage = file.type.startsWith("image/");
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isImage) {
        this.$message.error("只能上传 JPG/PNG 格式的图片!");
        return false;
      }
      if (!isLt2M) {
        this.$message.error("图片大小不能超过 2MB!");
        return false;
      }
      return true;
    },
    handleAvatarSuccess(res) {
      if (res.code === 200 && res.data && res.data.url) {
        this.form.avatar = res.data.url;
        this.$message.success("头像上传成功");
      } else {
        this.$message.error(res.message || "头像上传失败");
      }
    },
    handleAvatarError(err) {
      this.$message.error("头像上传失败: " + (err.message || "网络错误"));
    },
    saveProfile() {
      this.$refs.profileForm.validate((valid) => {
        if (!valid) {
          this.$message.warning("请填写完整并正确的信息");
          return;
        }

        this.saving = true;
        updateProfile({
          nickname: this.form.nickname,
          avatar: this.form.avatar,
          gender: this.form.gender,
          phone: this.form.phone,
          email: this.form.email,
          receiverName: this.form.receiverName,
          receiverPhone: this.form.receiverPhone,
          receiverAddress: this.form.receiverAddress,
        })
          .then((res) => {
            if (res.code !== 200) {
              this.$message.error(res.message || "保存失败");
              return;
            }
            this.$message.success("保存成功");
            const localUser = JSON.parse(
              localStorage.getItem("user") || "null",
            );
            if (localUser) {
              localUser.nickname = this.form.nickname;
              localStorage.setItem("user", JSON.stringify(localUser));
              this.$store.commit("setUser", localUser);
            }
          })
          .catch((err) => {
            this.$message.error("保存失败，请稍后重试");
            console.error(err);
          })
          .finally(() => {
            this.saving = false;
          });
      });
    },
  },
};
</script>

<style scoped>
.profile-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.page-header {
  margin-bottom: 24px;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
  color: #333;
}

.page-subtitle {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.profile-container {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.profile-sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.user-info-card {
  border-radius: 12px;
}

.user-avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px 0;
}

.avatar-display {
  margin-bottom: 16px;
}

.avatar-large {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #fff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.avatar-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  color: #fff;
  font-weight: 700;
  background: linear-gradient(135deg, #ff6900 0%, #ff8f3f 100%);
  box-shadow: 0 8px 24px rgba(255, 105, 0, 0.35);
}

.user-name-display {
  text-align: center;
  margin-bottom: 10px;
}

.user-name-display h3 {
  margin: 0 0 6px 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.user-id {
  margin: 0;
  font-size: 13px;
  color: #909399;
}

.upload-section {
  width: 100%;
  padding: 0 10px;
}

.upload-tip {
  margin: 10px 0 0 0;
  font-size: 12px;
  color: #909399;
  text-align: center;
  line-height: 1.5;
}

.quick-links-card {
  border-radius: 12px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.card-title i {
  font-size: 18px;
  color: #409eff;
}

.quick-links {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.link-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.link-item:hover {
  background: #f5f7fa;
  transform: translateX(4px);
}

.link-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  border-radius: 50%;
  margin-right: 12px;
}

.link-icon i {
  font-size: 18px;
  color: #fff;
}

.link-info {
  flex: 1;
}

.link-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 2px;
}

.link-desc {
  font-size: 12px;
  color: #909399;
}

.link-arrow {
  font-size: 14px;
  color: #c0c4cc;
}

.profile-main {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-card,
.security-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.header-title i {
  font-size: 18px;
  color: #409eff;
}

.profile-form {
  padding: 10px 10px 0 10px;
}

.disabled-input {
  background: #f5f7fa;
}

.gender-group {
  display: flex;
  gap: 10px;
}

.gender-group .el-radio-button {
  flex: 1;
}

.address-book-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #606266;
  background: #f0f9ff;
  padding: 12px 16px;
  border-radius: 8px;
  border: 1px solid #d9ecff;
}

.address-book-tip i {
  font-size: 16px;
  color: #409eff;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 0 10px 0;
  border-top: 1px solid #ebeef5;
  margin-top: 20px;
}

.address-book-card {
  border-radius: 12px;
}

.address-book-content {
  padding: 10px;
}

.address-book-desc {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f4ff 100%);
  border-radius: 8px;
  border: 1px solid #d9ecff;
  margin-bottom: 20px;
}

.address-book-desc i {
  font-size: 24px;
  color: #409eff;
  flex-shrink: 0;
}

.desc-text p {
  margin: 4px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.desc-text p:first-child {
  font-size: 15px;
  color: #303133;
  margin-bottom: 8px;
}

.address-book-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.stat-label {
  font-size: 13px;
  color: #909399;
}

.stat-value {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.address-book-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #909399;
  text-align: center;
}

.address-book-empty i {
  font-size: 48px;
  color: #c0c4cc;
  margin-bottom: 12px;
}

.address-book-empty p {
  margin: 0;
  font-size: 15px;
  color: #606266;
  font-weight: 500;
}

.address-book-empty span {
  font-size: 13px;
  color: #909399;
  margin-top: 6px;
}

.security-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 10px;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  transition: all 0.3s;
}

.security-item:hover {
  background: #f0f2f5;
  transform: translateX(4px);
}

.security-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.security-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #67c23a 0%, #13ce66 100%);
  border-radius: 50%;
}

.security-icon i {
  font-size: 22px;
  color: #fff;
}

.security-detail {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.security-label {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.security-desc {
  font-size: 13px;
  color: #909399;
}

@media (max-width: 992px) {
  .profile-container {
    grid-template-columns: 1fr;
  }

  .profile-sidebar {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .profile-page {
    padding: 12px;
  }

  .profile-sidebar {
    grid-template-columns: 1fr;
  }

  .page-title {
    font-size: 24px;
  }

  .el-col {
    width: 100% !important;
  }

  .gender-group {
    flex-direction: column;
  }

  .address-book-stats {
    grid-template-columns: 1fr;
  }

  .security-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .security-item .el-button {
    width: 100%;
  }
}
</style>
