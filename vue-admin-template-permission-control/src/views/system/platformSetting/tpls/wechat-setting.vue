<template>
  <el-form label-suffix=":" label-width="150px" label-position="right">
    <el-form-item label="公众号名称">
      <el-input v-model="weChatInfo.officialAccount" class="w400"/>
      <span class="ml20 info">本服务器的 IP 地址为 xxxxxxxxx</span>
    </el-form-item>
    <!--<el-form-item label="公众号二维码">-->
    <!--<img v-lazy="weChatInfo.ccountImage" style="width: 150px;height: 150px" alt="">-->
    <!--</el-form-item>-->
    <el-form-item label="AppID">
      <el-input v-model="weChatInfo.appId" class="w400" />
    </el-form-item>
    <el-form-item label="AppSecret">
      <el-input v-model="weChatInfo.appSecret" class="w400" />
    </el-form-item>
    <el-form-item label="微支付商户号">
      <el-input v-model="weChatInfo.payAccount" class="w400" />
    </el-form-item>
    <el-form-item label="微支付API密钥">
      <el-input v-model="weChatInfo.payRsa" class="w400" />
    </el-form-item>
    <el-form-item label="">
      <el-button type="primary" @click="updateInfo">保存</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
export default {
  name: 'WechatSetting',
  data() {
    return {
      weChatInfo: {
        apiclientCert: null,
        apiclientKey: null,
        payRsa: null,
        appId: null,
        appSecret: null,
        rootca: null,
        ccountImage: null,
        payAccount: null,
        follow: null,
        officialAccount: null,
        orderMessage: null
      }
    }
  },
  created() {
    this.getWeChatInfo()
  },
  methods: {
    async getWeChatInfo() {
      const { error, data, msg } = await this.$api.platForm.getWeChatInfo()
      if (error) return this.$message.error(msg)
      this.weChatInfo = data
    },
    async updateInfo() {
      const { error, msg } = await this.$api.platForm.update({
        wechatSetting: this.weChatInfo
      })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.getWeChatInfo()
    }
  }
}
</script>

<style scoped>

</style>
