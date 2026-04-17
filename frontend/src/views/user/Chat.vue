<template>
  <div class="page-block">
    <div class="page-title">
      <h2>联系客服</h2>
      <span class="sub">如有疑问请留言，客服会尽快回复</span>
    </div>
    <div class="chat-box">
      <div class="chat-topbar" v-if="threadList.length > 0">
        <el-tag
          v-for="thread in threadList"
          :key="thread.key"
          :type="thread.key === selectedThreadKey ? 'success' : 'info'"
          @click="selectThread(thread.key)"
          style="cursor: pointer; margin-right: 8px"
        >
          {{ thread.label }}
        </el-tag>
      </div>
      <div class="msgs">
        <div
          v-for="m in filteredMessages"
          :key="m.id"
          :class="['msg', m.senderRole === 'USER' ? 'me' : 'other']"
        >
          <span class="role" v-if="m.senderRole === 'MERCHANT'">客服：</span>
          {{ m.content }}
          <div v-if="m.productName" class="msg-product">
            商品：{{ m.productName }}
          </div>
        </div>
      </div>
      <div class="composer">
        <el-input
          v-model="inputMsg"
          type="textarea"
          placeholder="输入消息"
          class="input"
        />
        <el-button type="primary" @click="send">发送</el-button>
      </div>
    </div>
  </div>
</template>

<script>
// 假设有 /api/user/chat/list, /api/user/chat/send
import { getUserChatList, sendUserChat } from "@/api/user";

export default {
  name: "UserChat",
  data() {
    return {
      messages: [],
      inputMsg: "",
      selectedThreadKey: null,
      fixedProductId: null,
    };
  },
  created() {
    const qPid = this.$route?.query?.productId;
    const parsed =
      qPid === undefined || qPid === null || qPid === "" ? null : Number(qPid);
    this.fixedProductId = Number.isFinite(parsed) ? parsed : null;
    this.loadChat();
  },
  computed: {
    threadList() {
      const threadsMap = new Map();
      this.messages.forEach((m) => {
        const key = m.productId != null ? `product-${m.productId}` : "general";
        if (!threadsMap.has(key)) {
          const label = m.productName
            ? `商品：${m.productName}`
            : m.productId != null
            ? `商品ID：${m.productId}`
            : "通用客服";
          threadsMap.set(key, { key, label, productId: m.productId });
        }
      });

      // 允许从商品页带 productId 进入，但尚无历史消息时也能直接发送给对应商家
      if (this.fixedProductId != null) {
        const key = `product-${this.fixedProductId}`;
        if (!threadsMap.has(key)) {
          threadsMap.set(key, {
            key,
            label: `商品ID：${this.fixedProductId}`,
            productId: this.fixedProductId,
          });
        }
      }

      const threads = Array.from(threadsMap.values());
      if (!this.selectedThreadKey && threads.length) {
        this.selectedThreadKey = threads[0].key;
      }
      return threads;
    },
    filteredMessages() {
      if (!this.selectedThreadKey) return this.messages;
      return this.messages.filter((m) => {
        if (this.selectedThreadKey === "general") {
          return m.productId == null;
        }
        return `product-${m.productId}` === this.selectedThreadKey;
      });
    },
  },
  methods: {
    loadChat() {
      const params =
        this.fixedProductId != null ? { productId: this.fixedProductId } : {};
      getUserChatList(params).then((res) => {
        if (res.data) {
          this.messages = res.data;
        }
      });
    },
    selectThread(key) {
      this.selectedThreadKey = key;
    },
    send() {
      if (!this.inputMsg.trim()) return;
      const selected = this.threadList.find(
        (thread) => thread.key === this.selectedThreadKey,
      );
      const productId =
        selected && selected.productId ? selected.productId : this.fixedProductId;
      sendUserChat({ productId, content: this.inputMsg }).then((res) => {
        if (res.code === 200) {
          this.messages.push(res.data);
          this.inputMsg = "";
        }
      });
    },
  },
};
</script>

<style scoped>
.chat-box {
  border: 1px solid rgba(15, 23, 42, 0.1);
  border-radius: 16px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.55);
}
.msgs {
  padding: 14px;
  min-height: 260px;
  max-height: 520px;
  overflow: auto;
}
.msg {
  margin: 8px 0;
  padding: 10px 12px;
  border-radius: 14px;
  max-width: 80%;
  line-height: 1.45;
  border: 1px solid rgba(15, 23, 42, 0.08);
}
.msg.me {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  color: #fff;
  margin-left: auto;
  border-color: rgba(37, 99, 235, 0.2);
}
.msg.other {
  background: rgba(255, 255, 255, 0.75);
  color: rgba(15, 23, 42, 0.92);
}
.chat-topbar {
  padding: 12px 14px;
  border-bottom: 1px solid rgba(15, 23, 42, 0.08);
  background: rgba(249, 250, 251, 0.9);
}
.composer {
  display: flex;
  gap: 10px;
  align-items: flex-end;
  padding: 12px;
  border-top: 1px solid rgba(15, 23, 42, 0.08);
  background: rgba(255, 255, 255, 0.6);
}
.input {
  flex: 1;
}
.role {
  color: #2563eb;
  font-weight: 700;
  margin-right: 4px;
}
.msg-product {
  margin-top: 6px;
  color: rgba(107, 114, 128, 1);
  font-size: 12px;
}
</style>
