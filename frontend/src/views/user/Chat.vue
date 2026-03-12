<template>
  <div class="page-block">
    <div class="page-title">
      <h2>联系客服</h2>
      <span class="sub">如有疑问请留言，客服会尽快回复</span>
    </div>
    <div class="chat-box">
      <div class="msgs">
        <div
          v-for="m in messages"
          :key="m.id"
          :class="['msg', m.senderRole === 'USER' ? 'me' : 'other']"
        >
          <span class="role" v-if="m.senderRole === 'MERCHANT'">客服：</span>
          {{ m.content }}
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
    };
  },
  created() {
    this.loadChat();
  },
  methods: {
    loadChat() {
      getUserChatList().then((res) => {
        if (res.data) this.messages = res.data;
      });
    },
    send() {
      if (!this.inputMsg.trim()) return;
      sendUserChat({ content: this.inputMsg }).then((res) => {
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
</style>
