<template>
  <div class="page-block">
    <div class="page-title">
      <h2>商家管理</h2>
      <span class="sub">商家信息与启用状态</span>
    </div>

    <div style="display:flex; gap: 10px; flex-wrap: wrap; margin-bottom: 12px;">
      <el-button type="primary" @click="openCreateDialog">新增商家</el-button>
    </div>

    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="enabled" label="启用" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.enabled ? 'success' : 'info'">{{
            scope.row.enabled ? "是" : "否"
          }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="openEditDialog(scope.row)"
            >编辑</el-button
          >
          <el-button
            type="text"
            size="small"
            @click="deleteMerchant(scope.row.id)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      :title="isEdit ? '编辑商家' : '新增商家'"
      :visible.sync="dialogVisible"
      width="500px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
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
import {
  getMerchants,
  createMerchant,
  updateMerchant,
  deleteMerchant,
} from "@/api/admin";

export default {
  name: "AdminMerchants",
  data() {
    return {
      list: [],
      dialogVisible: false,
      isEdit: false,
      form: { name: "", description: "", enabled: true },
    };
  },
  created() {
    this.loadMerchants();
  },
  methods: {
    loadMerchants() {
      getMerchants().then((res) => {
        if (res.data) this.list = res.data;
      });
    },
    openCreateDialog() {
      this.isEdit = false;
      this.form = { name: "", description: "", enabled: true };
      this.dialogVisible = true;
    },
    openEditDialog(row) {
      this.isEdit = true;
      this.form = { ...row };
      this.dialogVisible = true;
    },
    submitForm() {
      const f = this.form;
      if (!f.name) {
        this.$message.warning("请填写商家名称");
        return;
      }
      const promise = this.isEdit ? updateMerchant(f.id, f) : createMerchant(f);
      promise.then((res) => {
        if (res.code === 200) {
          this.$message.success(this.isEdit ? "更新成功" : "创建成功");
          this.dialogVisible = false;
          this.loadMerchants();
        } else {
          this.$message.error(res.message || "操作失败");
        }
      });
    },
    deleteMerchant(id) {
      this.$confirm("确认删除？", "提示", { type: "warning" })
        .then(() => {
          deleteMerchant(id).then((res) => {
            if (res.code === 200) {
              this.$message.success("删除成功");
              this.loadMerchants();
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
