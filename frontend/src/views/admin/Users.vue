<template>
  <div class="page-block">
    <div class="page-title">
      <h2>用户管理</h2>
      <span class="sub">新增、编辑、启用状态管理</span>
    </div>

    <div style="display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 12px">
      <el-button type="primary" @click="openCreateDialog">新增用户</el-button>
    </div>

    <el-table :data="list" border>
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="phone" label="手机号" width="140" />
      <el-table-column prop="role" label="角色" width="100" />
      <el-table-column prop="enabled" label="启用" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.enabled ? 'success' : 'info'">{{
            scope.row.enabled ? "是" : "否"
          }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="openEditDialog(scope.row)"
            >编辑</el-button
          >
          <el-button
            type="text"
            size="small"
            @click="deleteUser(scope.row.id)"
            v-if="scope.row.role !== 'ADMIN'"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      :title="isEdit ? '编辑用户' : '新增用户'"
      :visible.sync="dialogVisible"
      width="500px"
      append-to-body
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" v-if="!isEdit">
          <el-input v-model="form.password" type="password" />
        </el-form-item>
        <el-form-item label="新密码" v-if="isEdit">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="留空表示不修改密码"
          />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" :disabled="form.role === 'ADMIN'">
            <el-option label="用户" value="USER" />
            <el-option label="商家" value="MERCHANT" />
            <el-option
              label="管理员"
              value="ADMIN"
              v-if="form.role === 'ADMIN'"
              disabled
            />
          </el-select>
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="form.enabled" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">提交</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getUsers, createUser, updateUser, deleteUser } from "@/api/admin";

export default {
  name: "AdminUsers",
  data() {
    return {
      list: [],
      dialogVisible: false,
      isEdit: false,
      form: {
        username: "",
        password: "",
        nickname: "",
        phone: "",
        role: "USER",
        enabled: true,
      },
    };
  },
  created() {
    this.loadUsers();
  },
  methods: {
    loadUsers() {
      getUsers().then((res) => {
        if (res.data && res.data.content) this.list = res.data.content;
        else if (Array.isArray(res.data)) this.list = res.data;
      });
    },
    openCreateDialog() {
      this.isEdit = false;
      this.form = {
        username: "",
        password: "",
        nickname: "",
        phone: "",
        role: "USER",
        enabled: true,
      };
      this.dialogVisible = true;
    },
    openEditDialog(row) {
      this.isEdit = true;
      this.form = { ...row };
      this.form.password = ""; // 清空密码字段
      this.dialogVisible = true;
    },
    submitForm() {
      const f = this.form;
      if (!f.username || !f.nickname || !f.role) {
        this.$message.warning("请填写必填项");
        return;
      }
      // 准备提交的数据
      const submitData = { ...f };
      if (this.isEdit && (!f.password || f.password.trim() === "")) {
        // 编辑时如果密码为空，不发送密码字段
        delete submitData.password;
      }
      const promise = this.isEdit
        ? updateUser(f.id, submitData)
        : createUser(submitData);
      promise.then((res) => {
        if (res.code === 200) {
          this.$message.success(this.isEdit ? "更新成功" : "创建成功");
          this.dialogVisible = false;
          this.loadUsers();
        } else {
          this.$message.error(res.message || "操作失败");
        }
      });
    },
    deleteUser(id) {
      this.$confirm("确认删除？", "提示", { type: "warning" })
        .then(() => {
          deleteUser(id).then((res) => {
            if (res.code === 200) {
              this.$message.success("删除成功");
              this.loadUsers();
            } else {
              this.$message.error(res.message || "删除失败");
            }
          });
        })
        .catch(() => {});
    },
  },
};
</script>
