<template>
  <div class="page-block">
    <div class="page-title">
      <h2>分类管理</h2>
      <span class="sub">用于商品归类与展示</span>
    </div>

    <div class="toolbar">
      <el-button type="primary" @click="openCreateDialog">新增分类</el-button>
      <el-input
        v-model="keyword"
        clearable
        placeholder="搜索：分类名称 / ID"
        class="search"
      />
      <el-select
        v-model="parentFilter"
        clearable
        filterable
        placeholder="父级筛选"
        class="parent"
      >
        <el-option label="全部父级" :value="null" />
        <el-option label="顶级分类（无父级）" :value="0" />
        <template v-for="group in parentGroups">
          <el-option
            :key="'root-' + group.root.id"
            :label="group.root.name"
            :value="group.root.id"
          />
          <el-option-group
            :key="'group-' + group.root.id"
            :label="group.root.name + ' 的子分类'"
          >
            <el-option
              v-for="child in group.children"
              :key="child.id"
              :label="child.name"
              :value="child.id"
            />
          </el-option-group>
        </template>
      </el-select>
      <span class="muted count">共 {{ filteredList.length }} 个</span>
    </div>

    <el-table :data="filteredList" border>
      <el-table-column prop="name" label="名称" />
      <el-table-column label="父级" width="180">
        <template slot-scope="scope">
          <span v-if="!scope.row.parentId" class="muted">顶级</span>
          <span v-else
            >{{ categoryNameById(scope.row.parentId) }}（{{
              scope.row.parentId
            }}）</span
          >
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="100" sortable />
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
      append-to-body
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="父级分类">
          <el-select
            v-model="form.parentId"
            clearable
            filterable
            placeholder="不选则为顶级分类"
            style="width: 100%"
          >
            <el-option label="顶级分类（无父级）" :value="null" />
            <el-option
              v-for="c in parentOptionsForForm"
              :key="c.id"
              :label="c.name"
              :value="c.id"
            />
          </el-select>
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
      keyword: "",
      parentFilter: null,
      dialogVisible: false,
      isEdit: false,
      form: { name: "", parentId: null, sortOrder: 0 },
    };
  },
  computed: {
    idNameMap() {
      const map = {};
      (this.list || []).forEach((c) => {
        if (c && c.id != null) map[c.id] = c.name;
      });
      return map;
    },
    parentGroups() {
      const roots = (this.list || []).filter((c) => c.parentId == null);
      return roots.map((root) => ({
        root,
        children: (this.list || []).filter(
          (c) => c.parentId != null && Number(c.parentId) === Number(root.id),
        ),
      }));
    },
    parentOptions() {
      const roots = (this.list || []).filter((c) => c.parentId == null);
      const children = (this.list || []).filter((c) => {
        if (c.parentId == null) return false;
        const parent = this.list.find(
          (p) => Number(p.id) === Number(c.parentId),
        );
        return parent && parent.parentId == null;
      });
      return [...roots, ...children].sort((a, b) => {
        const sa = Number(a.sortOrder) || 0;
        const sb = Number(b.sortOrder) || 0;
        if (sa !== sb) return sa - sb;
        return (Number(a.id) || 0) - (Number(b.id) || 0);
      });
    },
    parentOptionsForForm() {
      const currentId = this.isEdit ? this.form.id : null;
      const excludedIds = currentId
        ? [currentId, ...this.descendantIds(currentId)]
        : [];
      return this.parentOptions.filter(
        (c) => c && c.id != null && !excludedIds.includes(c.id),
      );
    },
    filteredList() {
      let arr = (this.list || []).slice();
      const kw = (this.keyword || "").trim().toLowerCase();
      if (kw) {
        arr = arr.filter((c) => {
          const name = String(c.name || "").toLowerCase();
          const id = String(c.id != null ? c.id : "");
          return name.includes(kw) || id.includes(kw);
        });
      }
      if (this.parentFilter != null) {
        if (this.parentFilter === 0) arr = arr.filter((c) => !c.parentId);
        else
          arr = arr.filter(
            (c) => Number(c.parentId) === Number(this.parentFilter),
          );
      }
      arr.sort((a, b) => {
        const sa = Number(a.sortOrder) || 0;
        const sb = Number(b.sortOrder) || 0;
        if (sa !== sb) return sa - sb;
        return (Number(a.id) || 0) - (Number(b.id) || 0);
      });
      return arr;
    },
  },
  created() {
    this.loadCategories();
  },
  methods: {
    categoryNameById(id) {
      return this.idNameMap[id] || "未知";
    },
    descendantIds(categoryId) {
      const ids = [];
      const children = (this.list || []).filter(
        (c) => Number(c.parentId) === Number(categoryId),
      );
      children.forEach((child) => {
        if (child.id != null) {
          ids.push(child.id);
          ids.push(...this.descendantIds(child.id));
        }
      });
      return ids;
    },
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
      if (f.parentId === f.id) {
        this.$message.warning("父级分类不能选择自己");
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

<style scoped>
.toolbar {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: center;
  margin-bottom: 12px;
}
.search {
  width: 240px;
}
.parent {
  width: 240px;
}
.count {
  margin-left: auto;
}
</style>
