<template>
  <el-card :body-style="{ padding: '0px' }">
    <div class="flex flex-column f-a-center">
      <div class="dialog-url">{{ accessUrl || '无活动 url' }}</div>
      <img :src="qrCodeUrl" class="qrCode">
    </div>
  </el-card>
</template>

<script>
export default {
  name: 'UrlDialog',
  props: {
    type: { type: String, default: null },
    uid: { type: Number, default: null }
  },
  data() {
    return {
      url: null,
      accessUrl: null,
      qrCodeUrl: null
    }
  },
  methods: {
    async loadLink() {
      const { error, data, msg } = await this.$api[this.type ? 'activity' : 'player'][this.type ? 'getActivityAccessLink' : 'getPlayerAccessLink']({ id: this.uid })
      if (error) return this.$message.error(msg)
      this.accessUrl = data.accessUrl
      this.qrCodeUrl = data.qrCodeUrl
    }
  }
}
</script>

<style scoped>
    .dialog-url {
        width: 90%;
        background-color: #dddee1;
        border: 1px solid #67C23A;
        border-radius: 5px;
        padding: 5px 10px;
        margin: 10px auto;
    }
    .qrCode {
        width: 200px;
        height: 200px;
    }
</style>
