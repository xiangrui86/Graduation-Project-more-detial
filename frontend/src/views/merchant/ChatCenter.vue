<template>
  <div class="page-block">
    <div class="page-title">
      <h2>留言回复中心</h2>
      <span class="sub">集中查看所有商品留言，并对指定用户进行回复</span>
    </div>

    <div class="wrap">
      <div class="left">
        <div class="panel-title">会话列表</div>
        <div v-if="loading" style="padding: 16px">
          <el-skeleton :rows="6" animated />
        </div>
        <div v-else-if="threads.length === 0" class="empty">
          <i class="el-icon-chat-dot-round" style="font-size: 28px"></i>
          <div style="margin-top: 10px">暂无留言</div>
        </div>
        <div v-else class="list">
          <div
            v-for="t in threads"
            :key="t.key"
            :class="['item', selectedKey === t.key ? 'selected' : '']"
            @click="selectThread(t.key)"
          >
            <div class="row">
              <div class="name">
                <el-tag size="mini" type="info" style="margin-right: 8px"
                  >用户 {{ t.userId }}</el-tag
                >
                <span class="product">{{ t.productName || `商品ID：${t.productId}` }}</span>
              </div>
              <div class="time">{{ formatDate(t.lastAt) }}</div>
            </div>
            <div class="preview">{{ t.lastContent }}</div>
          </div>
        </div>
      </div>

      <div class="right">
        <div class="panel-title">会话内容</div>
        <div v-if="!selectedThread" class="hint">
          请选择左侧一条会话开始查看与回复
        </div>
        <div v-else class="chat">
          <div class="meta">
            <div>
              <span class="meta-label">当前会话：</span>
              <el-tag size="mini" type="success"
                >用户 {{ selectedThread.userId }}</el-tag
              >
              <el-tag size="mini" style="margin-left: 8px"
                >{{ selectedThread.productName || `商品ID：${selectedThread.productId}` }}</el-tag
              >
            </div>
            <el-button size="mini" @click="refresh">刷新</el-button>
          </div>

          <div class="msgs">
            <div
              v-for="m in selectedMessages"
              :key="m.id"
              :class="['msg', m.senderRole === 'MERCHANT' ? 'me' : 'other']"
            >
              <div class="label">
                {{ m.senderRole === "MERCHANT" ? "商家" : "买家" }}
                <span class="time">{{ formatDate(m.createdAt) }}</span>
              </div>
              <div class="body">{{ m.content }}</div>
            </div>
          </div>

          <div class="composer">
            <el-input
              v-model="reply"
              type="textarea"
              :rows="4"
              placeholder="请输入回复内容"
            />
            <div style="text-align: right; margin-top: 10px">
              <el-button type="primary" :loading="saving" @click="send">
                发送回复
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getProductChat, sendProductChat } from "@/api/merchant";

export default {
  name: "MerchantChatCenter",
  data() {
    return {
      loading: false,
      saving: false,
      allMessages: [],
      selectedKey: null,
      reply: "",
    };
  },
  created() {
    this.refresh();
  },
  computed: {
    threads() {
      const map = new Map();
      const sorted = [...this.allMessages].sort((a, b) => {
        const ta = new Date(a.createdAt || 0).getTime();
        const tb = new Date(b.createdAt || 0).getTime();
        return ta - tb;
      });

      sorted.forEach((m) => {
        if (!m.productId || !m.userId) return;
        const key = `${m.productId}-${m.userId}`;
        map.set(key, {
          key,
          productId: m.productId,
          productName: m.productName,
          userId: m.userId,
          lastAt: m.createdAt,
          lastContent: m.content,
        });
      });

      const list = Array.from(map.values());
      list.sort((a, b) => new Date(b.lastAt || 0) - new Date(a.lastAt || 0));
      if (!this.selectedKey && list.length) this.selectedKey = list[0].key;
      return list;
    },
    selectedThread() {
      return this.threads.find((t) => t.key === this.selectedKey) || null;
    },
    selectedMessages() {
      if (!this.selectedThread) return [];
      const { productId, userId } = this.selectedThread;
      return this.allMessages
        .filter((m) => m.productId === productId && m.userId === userId)
        .sort((a, b) => new Date(a.createdAt || 0) - new Date(b.createdAt || 0));
    },
  },
  methods: {
    selectThread(key) {
      this.selectedKey = key;
      this.reply = "";
    },
    refresh() {
      this.loading = true;
      // 后端：不传 productId 返回当前商家全部消息
      getProductChat(undefined)
        .then((res) => {
          const payload = Array.isArray(res)
            ? res
            : Array.isArray(res?.data)
            ? res.data
            : [];
          this.allMessages = payload;
        })
        .catch(() => {
          this.allMessages = [];
          this.$message.error("加载留言失败");
        })
        .finally(() => {
          this.loading = false;
        });
    },
    send() {
      if (!this.selectedThread) {
        this.$message.warning("请先选择一条会话");
        return;
      }
      if (!this.reply.trim()) {
        this.$message.warning("请输入回复内容");
        return;
      }
      this.saving = true;
      const { productId, userId } = this.selectedThread;
      sendProductChat(productId, userId, this.reply.trim())
        .then((res) => {
          if (res.code === 200) {
            this.allMessages.push(res.data);
            this.reply = "";
            this.$message.success("回复已发送");
          } else {
            this.$message.error(res.message || "回复失败");
          }
        })
        .catch(() => this.$message.error("回复失败"))
        .finally(() => (this.saving = false));
    },
    formatDate(str) {
      if (!str) return "";
      const d = new Date(str);
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(
        2,
        "0",
      )}-${String(d.getDate()).padStart(2, "0")}`;
    },
  },
};
</script>

<style scoped>
.wrap {
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 16px;
}
.left,
.right {
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 14px;
  background: #fff;
  overflow: hidden;
}
.panel-title {
  padding: 12px 14px;
  font-weight: 800;
  border-bottom: 1px solid rgba(15, 23, 42, 0.08);
  background: #fafbfd;
}
.list {
  max-height: 640px;
  overflow: auto;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.item {
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  cursor: pointer;
  background: #fbfbfd;
}
.item.selected {
  border-color: #2563eb;
  box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.08);
}
.row {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  align-items: center;
  margin-bottom: 8px;
}
.product {
  font-weight: 700;
  color: #111827;
}
.time {
  font-size: 12px;
  color: #6b7280;
}
.preview {
  font-size: 13px;
  color: #334155;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.hint,
.empty {
  padding: 22px;
  color: #64748b;
  text-align: center;
}
.chat {
  padding: 14px;
}
.meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.meta-label {
  color: #64748b;
  font-weight: 600;
  margin-right: 6px;
}
.msgs {
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: #f8fafc;
  padding: 12px;
  max-height: 420px;
  overflow: auto;
}
.msg {
  max-width: 82%;
  padding: 10px 12px;
  border-radius: 12px;
  margin: 10px 0;
  border: 1px solid rgba(15, 23, 42, 0.08);
}
.msg.me {
  margin-left: auto;
  background: #eef2ff;
}
.msg.other {
  background: #fff;
}
.label {
  font-size: 12px;
  color: #475569;
  font-weight: 700;
  display: flex;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 6px;
}
.label .time {
  font-weight: 500;
}
.body {
  white-space: pre-wrap;
  word-break: break-word;
  color: #111827;
}
.composer {
  margin-top: 12px;
}
@media (max-width: 980px) {
  .wrap {
    grid-template-columns: 1fr;
  }
}
</style>

