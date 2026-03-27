<template>
  <div class="login-page">
    <el-card class="login-card">
      <div class="login-banner">
        <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
          <circle cx="24" cy="24" r="24" fill="#ff6900" />
          <path
            d="M14 34c2-8 7-14 10-14s8 6 10 14"
            stroke="#fff"
            stroke-width="2.5"
            stroke-linecap="round"
          />
          <circle cx="24" cy="20" r="5" fill="#fff" />
        </svg>
        <div class="brand-title">体育电商平台</div>
        <div class="brand-sub">运动 · 健康 · 生活</div>
      </div>
      <h2 class="login-title">欢迎登录</h2>

      <!-- 角色选择 -->
      <div class="role-selector">
        <div class="role-title">选择登录角色</div>
        <div class="role-options">
          <div
            class="role-option"
            :class="{ active: selectedRole === 'USER' }"
            @click="selectedRole = 'USER'"
          >
            <i class="el-icon-user"></i>
            <span>用户</span>
          </div>
          <div
            class="role-option"
            :class="{ active: selectedRole === 'MERCHANT' }"
            @click="selectedRole = 'MERCHANT'"
          >
            <i class="el-icon-shop"></i>
            <span>运营</span>
          </div>
          <div
            class="role-option"
            :class="{ active: selectedRole === 'ADMIN' }"
            @click="selectedRole = 'ADMIN'"
          >
            <i class="el-icon-s-custom"></i>
            <span>管理员</span>
          </div>
        </div>
      </div>

      <el-form
        ref="loginForm"
        :model="form"
        :rules="loginRules"
        label-width="0"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名/手机号"
            prefix-icon="el-icon-user"
            size="large"
            clearable
            @keyup.enter="handleLogin"
            @input="validateField('username')"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            prefix-icon="el-icon-lock"
            show-password
            size="large"
            clearable
            @keyup.enter="handleLogin"
            @input="validateField('password')"
          />
          <!-- 密码强度指示器 -->
          <div v-if="form.password" class="password-strength">
            <div class="strength-bar">
              <div
                class="strength-fill"
                :class="passwordStrengthClass"
                :style="{ width: passwordStrengthWidth }"
              ></div>
            </div>
            <span class="strength-text">{{ passwordStrengthText }}</span>
          </div>
        </el-form-item>
        <div class="remember-forgot">
          <el-checkbox v-model="form.rememberMe">记住我</el-checkbox>
          <a
            href="javascript:;"
            @click="showForgotPassword = true"
            class="forgot-link"
            >忘记密码？</a
          >
        </div>
        <el-form-item>
          <el-button
            class="login-btn"
            type="primary"
            :loading="loading"
            @click="handleLoginWithValidation"
            size="large"
            :disabled="loading"
          >
            <span v-if="!loading">登录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form-item>
        <div class="login-actions">
          <span>没有账号？</span>
          <a
            href="javascript:;"
            @click="showRegister = true"
            class="register-link"
            >立即注册</a
          >
        </div>
      </el-form>
    </el-card>

    <el-dialog
      title="用户注册"
      :visible.sync="showRegister"
      width="520px"
      class="register-dialog"
      @close="resetRegForm"
    >
      <el-form
        ref="regForm"
        :model="regForm"
        :rules="regRules"
        label-width="88px"
        class="register-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="regForm.username"
            placeholder="用户名"
            prefix-icon="el-icon-user"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="regForm.password"
            type="password"
            placeholder="密码"
            prefix-icon="el-icon-lock"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="regForm.confirmPassword"
            type="password"
            placeholder="确认密码"
            prefix-icon="el-icon-lock"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input
            v-model="regForm.nickname"
            placeholder="昵称"
            prefix-icon="el-icon-star-off"
            size="large"
          />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="regForm.gender">
            <el-radio label="MALE">男</el-radio>
            <el-radio label="FEMALE">女</el-radio>
            <el-radio label="OTHER">其他</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="regForm.phone"
            placeholder="手机号"
            prefix-icon="el-icon-phone-outline"
            size="large"
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="regForm.email"
            placeholder="邮箱（选填）"
            prefix-icon="el-icon-message"
            size="large"
          />
        </el-form-item>
        <el-form-item label="收货人" prop="receiverName">
          <el-input
            v-model="regForm.receiverName"
            placeholder="默认收货人"
            size="large"
          />
        </el-form-item>
        <el-form-item label="收货电话" prop="receiverPhone">
          <el-input
            v-model="regForm.receiverPhone"
            placeholder="默认收货电话"
            size="large"
          />
        </el-form-item>
        <el-form-item label="收货地址" prop="receiverAddress">
          <el-input
            v-model="regForm.receiverAddress"
            type="textarea"
            :rows="2"
            maxlength="255"
            show-word-limit
            placeholder="默认收货地址"
          />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showRegister = false">取消</el-button>
        <el-button
          type="primary"
          :loading="regLoading"
          @click="handleRegisterWithValidation"
          >注册</el-button
        >
      </span>
    </el-dialog>

    <!-- 忘记密码对话框 -->
    <el-dialog
      title="重置密码"
      :visible.sync="showForgotPassword"
      width="420px"
      class="forgot-password-dialog"
      @close="resetForgotForm"
    >
      <el-form
        ref="forgotForm"
        :model="forgotForm"
        :rules="forgotRules"
        label-width="0"
        class="forgot-form"
      >
        <el-form-item prop="phone">
          <el-input
            v-model="forgotForm.phone"
            placeholder="输入注册手机号"
            prefix-icon="el-icon-phone-outline"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="newPassword">
          <el-input
            v-model="forgotForm.newPassword"
            type="password"
            placeholder="输入新密码"
            prefix-icon="el-icon-lock"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="forgotForm.confirmPassword"
            type="password"
            placeholder="确认新密码"
            prefix-icon="el-icon-lock"
            size="large"
          />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showForgotPassword = false">取消</el-button>
        <el-button type="primary" @click="handleForgotPassword"
          >重置密码</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { login, register } from "@/api/auth";

export default {
  name: "Login",
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.regForm.password) {
        callback(new Error("两次输入的密码不一致"));
      } else {
        callback();
      }
    };

    const validatePhone = (rule, value, callback) => {
      const reg = /^1[3-9]\d{9}$/;
      if (value && !reg.test(value)) {
        callback(new Error("请输入正确的手机号"));
      } else {
        callback();
      }
    };

    const validateEmail = (rule, value, callback) => {
      const reg = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (value && !reg.test(value)) {
        callback(new Error("请输入正确的邮箱格式"));
      } else {
        callback();
      }
    };

    return {
      form: {
        username: "",
        password: "",
        rememberMe: false,
      },
      selectedRole: "USER", // 默认选择用户角色
      regForm: {
        username: "",
        password: "",
        confirmPassword: "",
        nickname: "",
        gender: "MALE",
        phone: "",
        email: "",
        receiverName: "",
        receiverPhone: "",
        receiverAddress: "",
      },
      forgotForm: {
        phone: "",
        newPassword: "",
        confirmPassword: "",
      },
      loading: false,
      regLoading: false,
      showRegister: false,
      showForgotPassword: false,
      loginRules: {
        username: [
          { required: true, message: "请输入用户名或手机号", trigger: "blur" },
          {
            min: 3,
            max: 20,
            message: "长度在 3 到 20 个字符",
            trigger: "blur",
          },
        ],
        password: [
          { required: true, message: "请输入密码", trigger: "blur" },
          { min: 6, message: "密码长度不能少于 6 位", trigger: "blur" },
        ],
      },
      regRules: {
        username: [
          { required: true, message: "请输入用户名", trigger: "blur" },
          {
            min: 3,
            max: 20,
            message: "长度在 3 到 20 个字符",
            trigger: "blur",
          },
        ],
        password: [
          { required: true, message: "请输入密码", trigger: "blur" },
          { min: 6, message: "密码长度不能少于 6 位", trigger: "blur" },
        ],
        confirmPassword: [
          { required: true, message: "请确认密码", trigger: "blur" },
          { validator: validateConfirmPassword, trigger: "blur" },
        ],
        nickname: [
          { required: true, message: "请输入昵称", trigger: "blur" },
          {
            min: 2,
            max: 20,
            message: "长度在 2 到 20 个字符",
            trigger: "blur",
          },
        ],
        phone: [
          { required: true, message: "请输入手机号", trigger: "blur" },
          { validator: validatePhone, trigger: "blur" },
        ],
        email: [{ validator: validateEmail, trigger: "blur" }],
        receiverPhone: [{ validator: validatePhone, trigger: "blur" }],
      },
      forgotRules: {
        phone: [
          { required: true, message: "请输入手机号", trigger: "blur" },
          { validator: validatePhone, trigger: "blur" },
        ],
        newPassword: [
          { required: true, message: "请输入新密码", trigger: "blur" },
          { min: 6, message: "密码长度不能少于 6 位", trigger: "blur" },
        ],
        confirmPassword: [
          { required: true, message: "请确认密码", trigger: "blur" },
          {
            validator: (rule, value, callback) => {
              if (value !== this.forgotForm.newPassword) {
                callback(new Error("两次输入的密码不一致"));
              } else {
                callback();
              }
            },
            trigger: "blur",
          },
        ],
      },
    };
  },
  computed: {
    passwordStrength() {
      const password = this.form.password;
      if (!password) return 0;

      let strength = 0;
      if (password.length >= 6) strength += 1;
      if (password.length >= 8) strength += 1;
      if (/[a-z]/.test(password)) strength += 1;
      if (/[A-Z]/.test(password)) strength += 1;
      if (/[0-9]/.test(password)) strength += 1;
      if (/[^A-Za-z0-9]/.test(password)) strength += 1;

      return Math.min(strength, 5);
    },
    passwordStrengthWidth() {
      return `${(this.passwordStrength / 5) * 100}%`;
    },
    passwordStrengthClass() {
      const strength = this.passwordStrength;
      if (strength <= 1) return "weak";
      if (strength <= 3) return "medium";
      return "strong";
    },
    passwordStrengthText() {
      const strength = this.passwordStrength;
      if (strength <= 1) return "弱";
      if (strength <= 3) return "中";
      return "强";
    },
  },
  mounted() {
    // 从 localStorage 读取记住的账号信息
    const remembered = localStorage.getItem("rememberedAccount");
    if (remembered) {
      try {
        const data = JSON.parse(remembered);
        this.form.username = data.username || "";
        this.form.password = data.password || "";
        this.form.rememberMe = true;
        this.selectedRole = data.role || "USER";
      } catch (e) {
        console.error("读取记住的账号失败", e);
      }
    }
  },
  methods: {
    validateField(field) {
      if (this.$refs.loginForm) {
        // 清除之前的错误状态
        this.$refs.loginForm.clearValidate(field);
        // 重新验证
        setTimeout(() => {
          this.$refs.loginForm.validateField(field);
        }, 100);
      }
    },
    handleLoginWithValidation() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.handleLogin();
        } else {
          this.$message.warning("请填写完整的登录信息");
          return false;
        }
      });
    },
    async handleLogin() {
      this.loading = true;
      try {
        // 将选择的角色传递给后端
        const loginData = {
          ...this.form,
          role: this.selectedRole,
        };
        const res = await login(loginData);
        if (res.code !== 200) {
          this.$message.error(res.message || "登录失败");
          return;
        }
        // 验证返回的角色是否与选择的角色一致
        if (res.data.role !== this.selectedRole) {
          this.$message.error(`账号角色不匹配，请选择正确的角色登录`);
          return;
        }

        // 如果勾选了记住我，保存到 localStorage
        if (this.form.rememberMe) {
          localStorage.setItem(
            "rememberedAccount",
            JSON.stringify({
              username: this.form.username,
              password: this.form.password,
              role: this.selectedRole,
            }),
          );
        } else {
          localStorage.removeItem("rememberedAccount");
        }

        this.$store.dispatch("login", {
          token: res.data.token,
          userId: res.data.userId,
          username: res.data.username,
          role: res.data.role,
          nickname: res.data.nickname,
          avatar: res.data.avatar,
          gender: res.data.gender,
          merchantId: res.data.merchantId,
        });

        // 登录成功提示
        this.$message.success(
          `欢迎回来，${res.data.nickname || res.data.username}！`,
        );

        // 短暂延迟后跳转
        setTimeout(() => {
          if (res.data.role === "ADMIN") this.$router.push("/admin");
          else if (res.data.role === "MERCHANT") this.$router.push("/merchant");
          else this.$router.push("/");
        }, 800);
      } catch (e) {
        this.$message.error(e.response?.data?.message || "登录失败");
      } finally {
        this.loading = false;
      }
    },
    handleRegisterWithValidation() {
      this.$refs.regForm.validate((valid) => {
        if (valid) {
          this.handleRegister();
        } else {
          this.$message.warning("请填写完整的注册信息");
          return false;
        }
      });
    },
    async handleRegister() {
      if (
        !this.regForm.username ||
        !this.regForm.password ||
        !this.regForm.nickname ||
        !this.regForm.phone
      ) {
        this.$message.warning("请填写用户名、密码、昵称和手机号");
        return;
      }
      this.regLoading = true;
      try {
        const res = await register(this.regForm);
        if (res.code !== 200) {
          this.$message.error(res.message || "注册失败");
          return;
        }
        this.$message.success("注册成功，请登录");
        this.showRegister = false;
        this.resetRegForm();
      } catch (e) {
        this.$message.error(e.response?.data?.message || "注册失败");
      } finally {
        this.regLoading = false;
      }
    },
    resetRegForm() {
      if (this.$refs.regForm) {
        this.$refs.regForm.resetFields();
      }
      this.regForm = {
        username: "",
        password: "",
        confirmPassword: "",
        nickname: "",
        gender: "MALE",
        phone: "",
        email: "",
        receiverName: "",
        receiverPhone: "",
        receiverAddress: "",
      };
    },
    resetForgotForm() {
      if (this.$refs.forgotForm) {
        this.$refs.forgotForm.resetFields();
      }
      this.forgotForm = {
        phone: "",
        newPassword: "",
        confirmPassword: "",
      };
    },
    handleForgotPasswordWithValidation() {
      this.$refs.forgotForm.validate((valid) => {
        if (valid) {
          this.handleForgotPassword();
        } else {
          this.$message.warning("请填写完整的信息");
          return false;
        }
      });
    },
    async handleForgotPassword() {
      // TODO: 实现忘记密码功能，需要后端支持
      this.$message.info("忘记密码功能开发中，请联系管理员重置密码");
      this.showForgotPassword = false;
    },
  },
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(
      to right,
      rgba(0, 0, 0, 0.34),
      rgba(0, 0, 0, 0.12)
    ),
    url("https://images.unsplash.com/photo-1517649763962-0c623066013b?auto=format&fit=crop&w=1200&q=80");
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  font-family: ui-sans-serif, system-ui, -apple-system, "Segoe UI", Roboto,
    Arial, "PingFang SC", "Microsoft YaHei", sans-serif;
  padding: 20px;
}
.login-card {
  width: 440px;
  max-width: 100%;
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 14px 42px rgba(0, 0, 0, 0.2), 0 2px 8px rgba(0, 0, 0, 0.12);
  padding: 36px 36px 28px 36px;
  border: 1px solid rgba(255, 255, 255, 0.6);
  animation: slideIn 0.5s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-banner {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 10px;
}
.login-banner svg {
  margin-bottom: 6px;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.brand-title {
  font-size: 22px;
  font-weight: 900;
  color: #ff6900;
  letter-spacing: 1px;
  margin-bottom: 4px;
}

.brand-sub {
  font-size: 13px;
  color: #ff8a3d;
  margin-bottom: 2px;
  letter-spacing: 0.5px;
}
.login-title {
  text-align: center;
  margin-bottom: 20px;
  color: #0f172a;
  font-weight: 800;
  font-size: 20px;
  letter-spacing: 0.2px;
}

/* 角色选择器 */
.role-selector {
  margin-bottom: 22px;
}

.role-title {
  text-align: center;
  font-size: 14px;
  color: #64748b;
  margin-bottom: 14px;
  font-weight: 600;
}

.role-options {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.role-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 85px;
  height: 85px;
  border-radius: 14px;
  background: #f1f5f9;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid transparent;
  position: relative;
  overflow: hidden;
}

.role-option::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.4) 0%,
    transparent 100%
  );
  opacity: 0;
  transition: opacity 0.3s ease;
}

.role-option:hover::before {
  opacity: 1;
}

.role-option i {
  font-size: 30px;
  color: #64748b;
  margin-bottom: 8px;
  transition: all 0.3s ease;
  z-index: 1;
}

.role-option span {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
  transition: all 0.3s ease;
  z-index: 1;
}

.role-option:hover {
  background: #e2e8f0;
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.role-option.active {
  background: linear-gradient(135deg, #ff6900 0%, #ff8a3d 100%);
  border-color: #ff6900;
  box-shadow: 0 8px 24px rgba(255, 105, 0, 0.35);
  transform: translateY(-4px) scale(1.05);
  animation: activePulse 2s ease-in-out infinite;
}

.role-option.active i {
  color: #fff;
  animation: bounce 0.5s ease;
}

.role-option.active span {
  color: #fff;
  font-weight: 700;
}

@keyframes activePulse {
  0%,
  100% {
    box-shadow: 0 8px 24px rgba(255, 105, 0, 0.35);
  }
  50% {
    box-shadow: 0 12px 32px rgba(255, 105, 0, 0.5);
  }
}

@keyframes bounce {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
}

.login-form {
  margin-top: 0;
}
.login-form .el-form-item {
  margin-bottom: 20px;
}

.login-form .el-input__inner {
  border-radius: 8px;
  transition: all 0.3s ease;
  border: 1px solid #d1d5db;
}

.login-form .el-input__inner:focus {
  border-color: #ff6900;
  box-shadow: 0 0 0 3px rgba(255, 105, 0, 0.1);
  transform: translateY(-1px);
}

.login-form .el-input__prefix {
  color: #9ca3af;
  transition: color 0.3s ease;
}

.login-form .el-input:focus .el-input__prefix {
  color: #ff6900;
}

/* 记住我和忘记密码 */
.remember-forgot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  font-size: 13px;
}

.remember-forgot .el-checkbox {
  color: #64748b;
}

.forgot-link {
  color: #ff6900;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.2s ease;
}

.forgot-link:hover {
  color: #ff8a3d;
  text-decoration: underline;
}

/* 密码强度指示器 */
.password-strength {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.strength-bar {
  flex: 1;
  height: 4px;
  background: #e2e8f0;
  border-radius: 2px;
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  transition: all 0.3s ease;
  border-radius: 2px;
}

.strength-fill.weak {
  background: #ef4444;
}

.strength-fill.medium {
  background: #f59e0b;
}

.strength-fill.strong {
  background: #10b981;
}

.strength-text {
  font-size: 12px;
  color: #64748b;
  min-width: 20px;
  text-align: right;
}

.login-btn {
  width: 100%;
  border-radius: 10px !important;
  font-size: 16px;
  font-weight: 700;
  background: linear-gradient(135deg, #ff6900 0%, #ff8a3d 100%);
  box-shadow: 0 8px 20px rgba(255, 105, 0, 0.3);
  border: none;
  transition: all 0.3s ease;
  height: 46px;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 28px rgba(255, 105, 0, 0.4);
}

.login-btn:active {
  transform: translateY(0);
}

.login-actions {
  text-align: center;
  font-size: 14px;
  color: #64748b;
  margin-top: 8px;
}

.login-actions a {
  color: #ff6900;
  margin-left: 8px;
  font-weight: 700;
  text-decoration: none;
  transition: all 0.2s ease;
}

.login-actions a:hover {
  color: #ff8a3d;
  text-decoration: underline;
}

/* 注册对话框 */
.register-dialog .el-dialog__body {
  padding-top: 16px;
  padding-bottom: 16px;
}

.register-form .el-form-item {
  margin-bottom: 18px;
}

/* 忘记密码对话框 */
.forgot-password-dialog .el-dialog__body {
  padding-top: 16px;
  padding-bottom: 16px;
}

.forgot-form .el-form-item {
  margin-bottom: 18px;
}

/* 表单验证错误样式 */
.login-form .el-form-item.is-error .el-input__inner {
  border-color: #f56c6c;
  box-shadow: 0 0 0 1px rgba(245, 108, 108, 0.2);
}

.login-form .el-form-item .el-form-item__error {
  color: #f56c6c;
  font-size: 12px;
  line-height: 1.2;
  margin-top: 4px;
  animation: errorShake 0.3s ease-in-out;
}

@keyframes errorShake {
  0%,
  100% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-2px);
  }
  75% {
    transform: translateX(2px);
  }
}
@media (max-width: 768px) {
  .login-card {
    width: 100%;
    max-width: 400px;
    padding: 28px 24px 24px 24px;
  }

  .role-options {
    gap: 8px;
  }

  .role-option {
    width: 75px;
    height: 75px;
  }

  .role-option i {
    font-size: 26px;
  }

  .role-option span {
    font-size: 13px;
  }

  .brand-title {
    font-size: 20px;
  }

  .login-title {
    font-size: 18px;
  }
}

@media (max-width: 480px) {
  .login-card {
    padding: 24px 20px 20px 20px;
  }

  .role-option {
    width: 70px;
    height: 70px;
    border-radius: 12px;
  }

  .role-option i {
    font-size: 24px;
    margin-bottom: 6px;
  }

  .role-option span {
    font-size: 12px;
  }

  .remember-forgot {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .login-btn {
    height: 44px;
    font-size: 15px;
  }
}

/* 输入框圆角优化 */
:deep(.el-input__inner) {
  border-radius: 10px;
  height: 46px;
  font-size: 14px;
  transition: all 0.3s ease;
}

:deep(.el-input__inner:focus) {
  border-color: #ff6900;
  box-shadow: 0 0 0 2px rgba(255, 105, 0, 0.1);
}

/* 按钮加载动画 */
:deep(.el-button.is-loading) {
  opacity: 0.8;
}

/* 对话框样式优化 */
:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #ff6900 0%, #ff8a3d 100%);
  padding: 18px 20px;
}

:deep(.el-dialog__title) {
  color: #fff;
  font-weight: 700;
  font-size: 18px;
}

:deep(.el-dialog__close) {
  color: #fff;
}

:deep(.el-dialog__close:hover) {
  color: rgba(255, 255, 255, 0.8);
}
</style>
