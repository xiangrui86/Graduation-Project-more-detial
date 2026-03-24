<template>
  <div class="page-block">
    <div class="page-title">
      <h2>商品管理</h2>
      <span class="sub">价格、库存、上架状态维护</span>
    </div>

    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="price" label="价格" width="100" />
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="sales" label="销量" width="80" />
      <el-table-column prop="onSale" label="上架" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.onSale ? 'success' : 'info'">{{
            scope.row.onSale ? "是" : "否"
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
            @click="deleteProduct(scope.row.id)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      :title="isEdit ? '编辑商品' : '编辑商品信息'"
      :visible.sync="dialogVisible"
      width="600px"
      append-to-body
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="form.name" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="销量">
          <el-input-number v-model="form.sales" :min="0" :disabled="true" />
        </el-form-item>
        <el-form-item label="上架">
          <el-switch v-model="form.onSale" />
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
import { getProducts, updateProduct, deleteProduct } from "@/api/admin";

export default {
  name: "AdminProducts",
  data() {
    return {
      list: [],
      dialogVisible: false,
      isEdit: false,
      form: { name: "", price: 0, stock: 0, sales: 0, onSale: true },
    };
  },
  created() {
    this.loadProducts();
  },
  methods: {
    loadProducts() {
      getProducts({ page: 0, size: 50 }).then((res) => {
        if (res.data && res.data.content) this.list = res.data.content;
      });
    },
    openEditDialog(row) {
      this.isEdit = true;
      this.form = { ...row };
      this.dialogVisible = true;
    },
    submitForm() {
      const f = this.form;
      if (!f.name || f.price < 0) {
        this.$message.warning("请填写正确的商品信息");
        return;
      }
      updateProduct(f.id, f).then((res) => {
        if (res.code === 200) {
          this.$message.success("更新成功");
          this.dialogVisible = false;
          this.loadProducts();
        } else {
          this.$message.error(res.message || "操作失败");
        }
      });
    },
    deleteProduct(id) {
      this.$confirm("确认删除？", "提示", { type: "warning" })
        .then(() => {
          deleteProduct(id).then((res) => {
            if (res.code === 200) {
              this.$message.success("删除成功");
              this.loadProducts();
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
