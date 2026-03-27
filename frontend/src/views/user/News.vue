<template>
  <div class="page-block">
    <div class="page-title">
      <h2>通知与资讯</h2>
      <span class="sub">平台动态、新品与系统更新</span>
    </div>

    <!-- Tab切换 -->
    <div class="filter-bar">
      <el-button-group>
        <el-button
          :type="activeType === 'ALL' ? 'primary' : 'default'"
          @click="
            activeType = 'ALL';
            loadNews();
          "
          icon="el-icon-tickets"
        >
          全部
        </el-button>
        <el-button
          :type="activeType === 'NEWS' ? 'primary' : 'default'"
          @click="
            activeType = 'NEWS';
            loadNews();
          "
          icon="el-icon-document"
        >
          资讯
        </el-button>
        <el-button
          :type="activeType === 'ANNOUNCEMENT' ? 'primary' : 'default'"
          @click="
            activeType = 'ANNOUNCEMENT';
            loadNews();
          "
          icon="el-icon-bell"
        >
          公告
        </el-button>
      </el-button-group>
    </div>

    <!-- 列表 -->
    <div v-if="list.length === 0" class="empty-state">
      <i class="el-icon-document"></i>
      <p>暂无相关内容</p>
    </div>

    <el-card
      v-for="item in list"
      :key="item.id"
      :class="[
        'news-item',
        'card-hover',
        item.type === 'ANNOUNCEMENT' ? 'announcement-card' : 'news-card',
      ]"
    >
      <div class="item-header">
        <div class="badge-wrapper">
          <el-tag
            v-if="item.type === 'ANNOUNCEMENT'"
            type="danger"
            size="small"
            effect="light"
          >
            <i class="el-icon-bell-outline"></i> 公告
          </el-tag>
          <el-tag v-else type="info" size="small" effect="light">
            <i class="el-icon-document-copy"></i> 资讯
          </el-tag>
        </div>
        <div class="title">{{ item.title }}</div>
      </div>
      <div class="time">
        <i class="el-icon-date"></i>
        {{ formatDate(item.createdAt) }}
      </div>
      <div class="content">{{ item.content }}</div>
    </el-card>
  </div>
</template>

<script>
import { getNews, getAnnouncements } from "@/api/pub";

export default {
  name: "News",
  data() {
    return {
      list: [],
      activeType: "ALL",
    };
  },
  created() {
    this.loadNews();
  },
  methods: {
    loadNews() {
      if (this.activeType === "ANNOUNCEMENT") {
        // 只加载公告
        getAnnouncements(20).then((res) => {
          if (res.data) this.list = res.data;
        });
      } else if (this.activeType === "NEWS") {
        // 只加载资讯 (/pub/news排除了公告)
        getNews({ page: 0, size: 20 }).then((res) => {
          if (res.data && res.data.content) this.list = res.data.content;
          else if (Array.isArray(res.data)) this.list = res.data;
        });
      } else {
        // 加载全部 - 需要合并两个API的结果
        Promise.all([
          getNews({ page: 0, size: 20 }),
          getAnnouncements(20),
        ]).then(([newsRes, announcRes]) => {
          let newsList = [];
          let announcList = [];

          if (newsRes.data && newsRes.data.content)
            newsList = newsRes.data.content;
          else if (Array.isArray(newsRes.data)) newsList = newsRes.data;

          if (announcRes.data) announcList = announcRes.data;

          // 合并并按时间排序
          this.list = [...newsList, ...announcList].sort(
            (a, b) => new Date(b.createdAt) - new Date(a.createdAt),
          );
        });
      }
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
.filter-bar {
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.3;
}

.empty-state p {
  margin: 0;
  font-size: 16px;
}

.news-item {
  margin-bottom: 16px;
  transition: all 0.3s ease;
  border: 1px solid #ebeef5;
  border-radius: 6px;
}

.news-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-color: #d9d9d9;
}

.announcement-card {
  background: linear-gradient(135deg, #fff7e6 0%, #fff9ed 100%);
  border-left: 4px solid #e6a23c;
}

.announcement-card:hover {
  background: linear-gradient(135deg, #fff5d6 0%, #fff7dd 100%);
  border-left-color: #d99f2d;
}

.news-card {
  background: #fafafa;
}

.news-card:hover {
  background: #f5f7fa;
}

.item-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
}

.badge-wrapper {
  flex-shrink: 0;
  margin-top: 2px;
}

.title {
  font-weight: 700;
  font-size: 16px;
  color: #1f2937;
  letter-spacing: 0.2px;
  flex: 1;
  line-height: 1.4;
}

.time {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #909399;
  font-size: 12px;
  margin-bottom: 12px;
}

.time i {
  font-size: 11px;
}

.content {
  margin-top: 10px;
  color: #606266;
  line-height: 1.7;
  font-size: 14px;
  white-space: pre-wrap;
  word-break: break-word;
  max-height: 150px;
  overflow: hidden;
  position: relative;
}

.content::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 30px;
  background: linear-gradient(transparent, rgba(255, 255, 255, 0.8));
}

/deep/ .el-tag--light {
  border-radius: 3px;
  font-size: 11px;
  padding: 2px 6px;
}

/deep/ .el-button-group > .el-button {
  border-radius: 20px;
}

/deep/ .el-button-group > .el-button:first-child {
  border-top-left-radius: 20px;
  border-bottom-left-radius: 20px;
}

/deep/ .el-button-group > .el-button:last-child {
  border-top-right-radius: 20px;
  border-bottom-right-radius: 20px;
}
</style>
