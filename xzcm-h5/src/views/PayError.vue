<template>
  <div class="pd-10" id="paySucc">
    <el-button type="danger" icon="el-icon-close" circle></el-button>
    <h2 class="aui-margin-t-15">支付失败</h2>
    <div class="opsBtn">
      <router-link :to="{path:'/player',query:{id: this.$route.query.id,activityId:this.activityId}}" class="aui-bar-tab-item">
        <el-button type="" class="aui-margin-t-15">关闭</el-button>
      </router-link>
      <router-link :to="{path:'/index/',query:{activityId:this.activityId}}" class="aui-bar-tab-item">
        <el-button type="success" class="aui-margin-t-15">返回首页</el-button>
      </router-link>
    </div>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        activityId: this.getUrlKey('activityId')
      }
    },
    methods: {
      close() {
        this.$wx.closeWindow()
      },
      getUrlKey: function (name) {//获取url 参数
        return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ""])[1].replace(/\+/g, '%20')) || null;
      },
    }
  }
</script>

<style lang="scss">
  #paySucc {
    .el-icon-check {
      font-size: 2rem;
      font-weight: bold
    }
    h2 {
      font-size: 0.6rem
    }
    p {
      font-size: 0.55rem;
      line-height: 0.8rem
    }
    .opsBtn .el-button {
      width: 40%;
    }
  }

</style>
