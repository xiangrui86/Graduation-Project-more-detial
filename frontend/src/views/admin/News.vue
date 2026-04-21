<template>
  <div class="page-block">
    <div class="page-title">
      <h2>资讯与公告管理</h2>
      <span class="sub">创建和管理平台的资讯和公告</span>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="openCreateDialog" icon="el-icon-plus">
        发布资讯
      </el-button>
      <el-button
        type="warning"
        @click="openAnnouncementDialog"
        icon="el-icon-bell"
      >
        发布公告
      </el-button>
      <div style="flex: 1"></div>
      <el-input
        v-model="searchTitle"
        placeholder="搜索标题..."
        style="width: 200px"
        clearable
        @change="loadNews"
      />
    </div>

    <!-- 列表 -->
    <el-table
      :data="filteredList"
      border
      style="width: 100%"
      :default-sort="{ prop: 'createdAt', order: 'descending' }"
    >
      <el-table-column prop="title" label="标题" min-width="200" />
      <el-table-column prop="type" label="类型" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.type === 'ANNOUNCEMENT' ? 'danger' : 'info'">
            {{ scope.row.type === "ANNOUNCEMENT" ? "公告" : "资讯" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="published"
        label="发布状态"
        width="100"
        align="center"
      >
        <template slot-scope="scope">
          <el-tag :type="scope.row.published ? 'success' : 'info'">
            {{ scope.row.published ? "已发布" : "草稿" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="createdAt"
        label="创建时间"
        width="180"
        sortable
        align="center"
      >
        <template slot-scope="scope">
          {{ formatDate(scope.row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="small"
            @click="openEditDialog(scope.row)"
          >
            编辑
          </el-button>
          <el-divider direction="vertical"></el-divider>
          <el-button type="text" size="small" @click="deleteNews(scope.row.id)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 创建/编辑对话框 -->
    <el-dialog
      :title="isEdit ? '编辑内容' : '发布新内容'"
      :visible.sync="dialogVisible"
      width="700px"
      append-to-body
      @close="resetForm"
    >
      <el-form :model="form" label-width="80px" :rules="rules" ref="form">
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio label="NEWS">资讯</el-radio>
            <el-radio label="ANNOUNCEMENT">公告</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            rows="8"
            placeholder="请输入内容"
            show-word-limit
            maxlength="2000"
          />
        </el-form-item>
        <el-form-item label="发布" prop="published">
          <el-switch v-model="form.published" />
          <span style="margin-left: 10px; color: #909399; font-size: 12px">
            关闭则保存为草稿
          </span>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm($refs.form)">
          {{ isEdit ? "更新" : "发布" }}
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getNews, createNews, updateNews, deleteNews } from "@/api/admin";

export default {
  name: "AdminNews",
  data() {
    const validateTitle = (rule, value, callback) => {
      if (!value) {
        callback(new Error("标题不能为空"));
      } else if (value.length > 128) {
        callback(new Error("标题长度不能超过128字符"));
      } else {
        callback();
      }
    };
    const validateContent = (rule, value, callback) => {
      if (!value) {
        callback(new Error("内容不能为空"));
      } else if (value.length > 2000) {
        callback(new Error("内容长度不能超过2000字符"));
      } else {
        callback();
      }
    };

    return {
      list: [],
      searchTitle: "",
      dialogVisible: false,
      isEdit: false,
      form: { title: "", content: "", type: "NEWS", published: false },
      rules: {
        title: [{ validator: validateTitle, trigger: "blur" }],
        content: [{ validator: validateContent, trigger: "blur" }],
      },
    };
  },
  computed: {
    filteredList() {
      return this.list.filter(
        (item) => !this.searchTitle || item.title.includes(this.searchTitle),
      );
    },
  },
  created() {
    this.loadNews();
  },
  methods: {
    loadNews() {
      getNews().then((res) => {
        if (res.data) {
          // 数据可能是数组或分页对象
          this.list = Array.isArray(res.data)
            ? res.data
            : res.data.content || [];
        }
      });
    },
    openCreateDialog() {
      this.isEdit = false;
      this.form = { title: "", content: "", type: "NEWS", published: false };
      this.dialogVisible = true;
    },
    openAnnouncementDialog() {
      this.isEdit = false;
      this.form = {
        title: "",
        content: "",
        type: "ANNOUNCEMENT",
        published: true,
      };
      this.dialogVisible = true;
    },
    openEditDialog(row) {
      this.isEdit = true;
      this.form = {
        id: row.id,
        title: row.title,
        content: row.content,
        type: row.type,
        published: row.published,
      };
      this.dialogVisible = true;
    },
    submitForm(form) {
      form.validate((valid) => {
        if (valid) {
          const f = this.form;
          const promise = this.isEdit ? updateNews(f.id, f) : createNews(f);
          promise
            .then((res) => {
              if (res.code === 200) {
                this.$message.success(this.isEdit ? "更新成功" : "发布成功");
                this.dialogVisible = false;
                this.loadNews();
              } else {
                this.$message.error(res.message || "操作失败");
              }
            })
            .catch(() => {
              this.$message.error("操作失败");
            });
        }
      });
    },
    deleteNews(id) {
      this.$confirm("确认删除此内容？删除后不可恢复。", "提示", {
        type: "warning",
        confirmButtonText: "确认删除",
        cancelButtonText: "取消",
      })
        .then(() => {
          deleteNews(id).then((res) => {
            if (res.code === 200) {
              this.$message.success("删除成功");
              this.loadNews();
            } else {
              this.$message.error(res.message || "删除失败");
            }
          });
        })
        .catch(() => {});
    },
    resetForm() {
      this.$refs.form && this.$refs.form.clearValidate();
    },
    formatDate(dateStr) {
      if (!dateStr) return "";
      const date = new Date(dateStr);
      return date.toLocaleDateString("zh-CN", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
      });
    },
  },
};
</script>

<style scoped>
.action-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  align-items: center;
}

/deep/ .el-divider--vertical {
  margin: 0 8px;
  background-color: #dcdfe6;
  height: 16px;
}

/deep/ .el-tag {
  border-radius: 3px;
}

/deep/ .el-table td {
  padding: 12px 0;
}

/deep/ .el-form-item {
  margin-bottom: 16px;
}

/deep/ .el-dialog__body {
  padding: 20px;
}

/deep/ .el-input__textarea textarea {
  font-family: "Monaco", "Menlo", "Ubuntu Mono", "Consolas", "source-code-pro",
    monospace;
}
</style>
