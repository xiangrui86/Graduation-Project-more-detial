<template>
  <div class="page-block">
    <div class="page-title">
      <h2>商家管理</h2>
      <span class="sub">商家信息与启用状态</span>
    </div>

    <div style="display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 12px">
      <el-button type="primary" @click="openCreateDialog">新增商家</el-button>
    </div>

    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column label="所属用户" min-width="160">
        <template slot-scope="scope">
          <span>{{ scope.row.ownerUsername || "-" }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="enabled" label="启用" width="100">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.enabled"
            @change="toggleEnabled(scope.row)"
          />
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
      append-to-body
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="所属用户">
          <el-select v-model="form.ownerUserId" filterable clearable style="width: 100%">
            <el-option
              v-for="u in merchantUsers"
              :key="u.id"
              :label="u.nickname ? `${u.username}（${u.nickname}）` : u.username"
              :value="u.id"
            />
          </el-select>
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
  getUsers,
  setMerchantOwner,
} from "@/api/admin";

export default {
  name: "AdminMerchants",
  data() {
    return {
      list: [],
      merchantUsers: [],
      dialogVisible: false,
      isEdit: false,
      form: { name: "", ownerUserId: null, description: "", enabled: true },
    };
  },
  created() {
    this.loadMerchants();
    this.loadMerchantUsers();
  },
  methods: {
    toMerchantPayload(row) {
      return {
        name: row.name,
        description: row.description,
        enabled: row.enabled,
        logo: row.logo,
        contactPhone: row.contactPhone,
        contactPerson: row.contactPerson,
      };
    },
    loadMerchants() {
      getMerchants().then((res) => {
        if (res.data) this.list = res.data;
      });
    },
    loadMerchantUsers() {
      getUsers({ role: "MERCHANT" }).then((res) => {
        if (Array.isArray(res.data)) this.merchantUsers = res.data;
        else if (res.data && res.data.content) this.merchantUsers = res.data.content;
      });
    },
    openCreateDialog() {
      this.isEdit = false;
      this.form = { name: "", ownerUserId: null, description: "", enabled: true };
      this.dialogVisible = true;
    },
    openEditDialog(row) {
      this.isEdit = true;
      this.form = {
        id: row.id,
        ownerUserId: row.ownerUserId || null,
        ...this.toMerchantPayload(row),
      };
      this.dialogVisible = true;
    },
    toggleEnabled(row) {
      updateMerchant(row.id, this.toMerchantPayload(row)).then((res) => {
        if (res.code === 200) {
          this.$message.success(row.enabled ? "已启用" : "已禁用");
        } else {
          this.$message.error(res.message || "操作失败");
          row.enabled = !row.enabled;
        }
      });
    },
    submitForm() {
      const f = this.form;
      if (!f.name) {
        this.$message.warning("请填写商家名称");
        return;
      }
      const payload = this.toMerchantPayload(f);
      const promise = this.isEdit
        ? updateMerchant(f.id, payload)
        : createMerchant(payload);
      promise.then((res) => {
        if (res.code === 200) {
          const merchantId = this.isEdit ? f.id : res.data && res.data.id;
          const doBind =
            merchantId != null
              ? setMerchantOwner(merchantId, f.ownerUserId || null)
              : Promise.resolve({ code: 200 });

          doBind.then((r2) => {
            if (r2.code === 200) {
              this.$message.success(this.isEdit ? "更新成功" : "创建成功");
              this.dialogVisible = false;
              this.loadMerchants();
            } else {
              this.$message.error(r2.message || "绑定所属用户失败");
            }
          });
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
