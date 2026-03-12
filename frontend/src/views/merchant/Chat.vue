<template>
  <div class="page-block">
    <div class="page-title">
      <h2>客服消息</h2>
      <span class="sub">输入用户 ID 加载对话</span>
    </div>
    <div class="toolbar">
      <el-input v-model="targetUserId" placeholder="用户ID" class="uid" />
      <el-button type="primary" @click="loadChat">加载对话</el-button>
    </div>
    <div v-if="targetUserId" class="chat-box">
      <div class="msgs">
        <div
          v-for="m in messages"
          :key="m.id"
          :class="['msg', m.senderId === meId ? 'me' : 'other']"
        >
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
import { getChatList, sendChat } from "@/api/merchant";

export default {
  name: "MerchantChat",
  data() {
    return {
      targetUserId: "",
      messages: [],
      inputMsg: "",
      meId: null,
    };
  },
  created() {
    this.meId = this.$store.state.user && this.$store.state.user.userId;
  },
  methods: {
    loadChat() {
      if (!this.targetUserId) return;
      getChatList(this.targetUserId).then((res) => {
        if (res.data) this.messages = res.data;
      });
    },
    send() {
      if (!this.inputMsg.trim()) return;
      sendChat({
        receiverId: Number(this.targetUserId),
        content: this.inputMsg,
      }).then((res) => {
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
.toolbar {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: center;
  margin-bottom: 12px;
}
.uid {
  width: 220px;
}
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
</style>
