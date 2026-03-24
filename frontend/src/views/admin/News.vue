<template>
  <div class="page-block">
    <div class="page-title">
      <h2>资讯管理</h2>
      <span class="sub">公告/资讯编辑与发布</span>
    </div>

    <div style="display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 12px">
      <el-button type="primary" @click="openCreateDialog">新增资讯</el-button>
      <el-button type="success" @click="openAnnouncementDialog"
        >发布公告</el-button
      >
    </div>

    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="type" label="类型" width="100" />
      <el-table-column prop="published" label="发布" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.published ? 'success' : 'info'">{{
            scope.row.published ? "是" : "否"
          }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="openEditDialog(scope.row)"
            >编辑</el-button
          >
          <el-button type="text" size="small" @click="deleteNews(scope.row.id)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      :title="isEdit ? '编辑资讯' : '新增资讯'"
      :visible.sync="dialogVisible"
      width="600px"
      append-to-body
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" rows="6" />
        </el-form-item>
        <el-form-item label="类型">
          <el-input v-model="form.type" />
        </el-form-item>
        <el-form-item label="发布">
          <el-switch v-model="form.published" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">提交</el-button>
      </span>
    </el-dialog>

    <el-dialog
      :title="'发布公告'"
      :visible.sync="announcementVisible"
      width="600px"
      append-to-body
    >
      <el-form :model="announcementForm" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="announcementForm.title" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="announcementForm.content"
            type="textarea"
            rows="6"
          />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="announcementVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAnnouncement">发布</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getNews,
  createNews,
  updateNews,
  deleteNews,
  createAnnouncement,
} from "@/api/admin";

export default {
  name: "AdminNews",
  data() {
    return {
      list: [],
      dialogVisible: false,
      isEdit: false,
      form: { title: "", content: "", type: "", published: false },
      announcementVisible: false,
      announcementForm: { title: "", content: "" },
    };
  },
  created() {
    this.loadNews();
  },
  methods: {
    loadNews() {
      getNews().then((res) => {
        if (res.data) this.list = res.data;
      });
    },
    openCreateDialog() {
      this.isEdit = false;
      this.form = { title: "", content: "", type: "", published: false };
      this.dialogVisible = true;
    },
    openAnnouncementDialog() {
      this.announcementForm = { title: "", content: "" };
      this.announcementVisible = true;
    },
    openEditDialog(row) {
      this.isEdit = true;
      // 只拷贝接口需要的字段，避免把多余字段一并提交
      this.form = {
        id: row.id,
        title: row.title,
        content: row.content,
        type: row.type,
        published: row.published,
      };
      this.dialogVisible = true;
    },
    submitForm() {
      const f = this.form;
      if (!f.title || !f.content) {
        this.$message.warning("请填写标题和内容");
        return;
      }
      const promise = this.isEdit ? updateNews(f.id, f) : createNews(f);
      promise.then((res) => {
        if (res.code === 200) {
          this.$message.success(this.isEdit ? "更新成功" : "创建成功");
          this.dialogVisible = false;
          this.loadNews();
        } else {
          this.$message.error(res.message || "操作失败");
        }
      });
    },
    submitAnnouncement() {
      const f = this.announcementForm;
      if (!f.title || !f.content) {
        this.$message.warning("请填写标题和内容");
        return;
      }
      createAnnouncement(f).then((res) => {
        if (res.code === 200) {
          this.$message.success("公告发布成功");
          this.announcementVisible = false;
          this.loadNews();
        } else {
          this.$message.error(res.message || "公告发布失败");
        }
      });
    },
    deleteNews(id) {
      this.$confirm("确认删除？", "提示", { type: "warning" })
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
  },
};
</script>
