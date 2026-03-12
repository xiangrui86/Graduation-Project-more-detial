<template>
  <div class="page-block">
    <div class="page-title">
      <h2>分类管理</h2>
      <span class="sub">用于商品归类与展示</span>
    </div>

    <div style="display:flex; gap: 10px; flex-wrap: wrap; margin-bottom: 12px;">
      <el-button type="primary" @click="openCreateDialog">新增分类</el-button>
    </div>

    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="parentId" label="父级ID" width="100" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="openEditDialog(scope.row)"
            >编辑</el-button
          >
          <el-button
            type="text"
            size="small"
            @click="deleteCategory(scope.row.id)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      :title="isEdit ? '编辑分类' : '新增分类'"
      :visible.sync="dialogVisible"
      width="500px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="父级ID">
          <el-input-number v-model="form.parentId" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" />
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
  getCategories,
  createCategory,
  updateCategory,
  deleteCategory,
} from "@/api/admin";

export default {
  name: "AdminCategories",
  data() {
    return {
      list: [],
      dialogVisible: false,
      isEdit: false,
      form: { name: "", parentId: null, sortOrder: 0 },
    };
  },
  created() {
    this.loadCategories();
  },
  methods: {
    loadCategories() {
      getCategories().then((res) => {
        if (res.data) this.list = res.data;
      });
    },
    openCreateDialog() {
      this.isEdit = false;
      this.form = { name: "", parentId: null, sortOrder: 0 };
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
        this.$message.warning("请填写分类名称");
        return;
      }
      const promise = this.isEdit ? updateCategory(f.id, f) : createCategory(f);
      promise.then((res) => {
        if (res.code === 200) {
          this.$message.success(this.isEdit ? "更新成功" : "创建成功");
          this.dialogVisible = false;
          this.loadCategories();
        } else {
          this.$message.error(res.message || "操作失败");
        }
      });
    },
    deleteCategory(id) {
      this.$confirm("确认删除？", "提示", { type: "warning" })
        .then(() => {
          deleteCategory(id).then((res) => {
            if (res.code === 200) {
              this.$message.success("删除成功");
              this.loadCategories();
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
