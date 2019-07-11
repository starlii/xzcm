<template>
  <div id="complaintDone" v-wechat-title="$route.meta.title">
    <div class="pd-10">
      <div class="aui-margin-t-10">
        <el-button type="success" icon="el-icon-check" circle></el-button>
      </div>
      <h2>投诉已提交</h2>
      <p class="aui-margin-t-15">微信团队会尽快核实，并通过“微信团队”通知你审核结果。<br>感谢你的支持。</p>
      <el-button type="success" class="aui-margin-t-15 w100" @click="close()">关闭</el-button>
    </div>
  </div>
</template>

<script>
  export default {
    name: "ComplaintDone",
    methods: {
      close() {
        this.$wx.closeWindow()
      }
    }
  }
</script>

<style lang="scss">
  #complaintDone {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    z-index: 999;
    height: 100%;
    background: #efeef3;
    .el-icon-check {

      font-size: 2.5rem;
      font-weight: bold
    }

    h2 {
      font-size: 0.7rem;
      color: #010005;
      margin-top: 20px;
    }

    p {
      font-size: 0.55rem;
      line-height: 0.8rem;
      color: #a2a1a6;
    }

    .w100 {
      width: 100%
    }
    .el-button--success {
      background: #49ba2c;
    }
    .pd-10 {
      padding: 0.55rem
    }
  }

</style>
