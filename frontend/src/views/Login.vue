<template>
  <div class="login-page">
    <el-card class="login-card">
      <div class="login-banner">
        <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
          <circle cx="24" cy="24" r="24" fill="#2563eb" />
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
      <el-form :model="form" label-width="0" class="login-form">
        <el-form-item>
          <el-input
            v-model="form.username"
            placeholder="用户名/手机号"
            prefix-icon="el-icon-user"
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            prefix-icon="el-icon-lock"
            show-password
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            class="login-btn"
            type="primary"
            :loading="loading"
            @click="handleLogin"
            size="large"
            >登录</el-button
          >
        </el-form-item>
        <div class="login-actions">
          <span>没有账号？</span>
          <a href="javascript:;" @click="showRegister = true">立即注册</a>
        </div>
      </el-form>
    </el-card>

    <el-dialog
      title="用户注册"
      :visible.sync="showRegister"
      width="400px"
      class="register-dialog"
    >
      <el-form :model="regForm" label-width="0" class="register-form">
        <el-form-item>
          <el-input
            v-model="regForm.username"
            placeholder="用户名"
            prefix-icon="el-icon-user"
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="regForm.password"
            type="password"
            placeholder="密码"
            prefix-icon="el-icon-lock"
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="regForm.nickname"
            placeholder="昵称"
            prefix-icon="el-icon-star-off"
            size="large"
          />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showRegister = false">取消</el-button>
        <el-button type="primary" :loading="regLoading" @click="handleRegister"
          >注册</el-button
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
    return {
      form: { username: "", password: "" },
      regForm: { username: "", password: "", nickname: "" },
      loading: false,
      regLoading: false,
      showRegister: false,
    };
  },
  methods: {
    async handleLogin() {
      this.loading = true;
      try {
        const res = await login(this.form);
        if (res.code !== 200) {
          this.$message.error(res.message || "登录失败");
          return;
        }
        this.$store.dispatch("login", {
          token: res.data.token,
          userId: res.data.userId,
          username: res.data.username,
          role: res.data.role,
          nickname: res.data.nickname,
          merchantId: res.data.merchantId,
        });
        if (res.data.role === "ADMIN") this.$router.push("/admin");
        else if (res.data.role === "MERCHANT") this.$router.push("/merchant");
        else this.$router.push("/");
      } catch (e) {
        this.$message.error(e.response?.data?.message || "登录失败");
      } finally {
        this.loading = false;
      }
    },
    async handleRegister() {
      this.regLoading = true;
      try {
        const res = await register(this.regForm);
        if (res.code !== 200) {
          this.$message.error(res.message || "注册失败");
          return;
        }
        this.$message.success("注册成功，请登录");
        this.showRegister = false;
      } catch (e) {
        this.$message.error(e.response?.data?.message || "注册失败");
      } finally {
        this.regLoading = false;
      }
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
      rgba(0, 0, 0, 0.25),
      rgba(0, 0, 0, 0.05)
    ),
    url("https://images.unsplash.com/photo-1517649763962-0c623066013b?auto=format&fit=crop&w=1200&q=80");
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  font-family: ui-sans-serif, system-ui, -apple-system, "Segoe UI", Roboto,
    Arial, "PingFang SC", "Microsoft YaHei", sans-serif;
}
.login-card {
  width: 400px;
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(14px);
  border-radius: 18px;
  box-shadow: 0 10px 36px rgba(37, 99, 235, 0.1), 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 32px 32px 24px 32px;
  border: 1.5px solid rgba(37, 99, 235, 0.08);
}
.login-banner {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 10px;
}
.login-banner svg {
  margin-bottom: 6px;
}
.brand-title {
  font-size: 20px;
  font-weight: 900;
  color: #2563eb;
  letter-spacing: 1px;
}

.brand-sub {
  font-size: 13px;
  color: #1d4ed8;
  margin-bottom: 2px;
  letter-spacing: 0.5px;
}
.login-title {
  text-align: center;
  margin-bottom: 18px;
  color: #0f172a;
  font-weight: 800;
  font-size: 18px;
  letter-spacing: 0.2px;
}
.login-form {
  margin-top: 0;
}
.login-form .el-form-item {
  margin-bottom: 18px;
}
.login-btn {
  width: 100%;
  border-radius: 999px !important;
  font-size: 16px;
  font-weight: 700;
  background: linear-gradient(90deg, #2563eb 0%, #1d4ed8 100%);
  box-shadow: 0 6px 18px rgba(37, 99, 235, 0.13);
  border: none;
}
.login-btn:hover {
  filter: brightness(1.04);
}
.login-actions {
  text-align: right;
  font-size: 13px;
  color: #64748b;
  margin-top: -8px;
}
.login-actions a {
  color: #2563eb;
  margin-left: 6px;
  font-weight: 700;
  text-decoration: none;
}
.login-actions a:hover {
  text-decoration: underline;
}
.register-dialog .el-dialog__body {
  padding-top: 10px;
}
.register-form .el-form-item {
  margin-bottom: 18px;
}
.register-form .el-input {
  border-radius: 999px;
}
</style>
