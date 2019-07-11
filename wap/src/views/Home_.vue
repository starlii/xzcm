<template>
  <div id="hm"></div>
</template>
<script>

  export default {
    data() {
      return {
        openid: '',
        getUrl: ''
      }
    },
    methods: {
      getUrlKey: function (name) {//获取url 参数
        return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ""])[1].replace(/\+/g, '%20')) || null;
      },
      async getWeChat() {
        const {error, msg, data} = await this.$api.weChat.getWeChat()
        if (error) return this.$message.error(msg)
        this.checkIsOpenid(data.appId, this.getUrl)
      },
      async getBasic() {
        const {error, msg, data} = await this.$api.weChat.getBasic()
        if (error) return this.$message.error(msg)
        this.getUrl = data.platformDomain
      },
      checkIsOpenid(appId, getUrl) {
        if (this.openid == null || this.openid == "") {
          let urlNow = encodeURIComponent(getUrl + "#/weixin?activityId=" + this.getUrlKey('activityId') + "&id=" + this.getUrlKey('id'));
          console.log(urlNow)
          let scope = 'snsapi_base';    //snsapi_userinfo   //静默授权 用户无感知
          let url = `https://open.weixin.qq.com/connect/oauth2/authorize?appid=${appId}&redirect_uri=${urlNow}&response_type=code&scope=${scope}&state=1#wechat_redirect`;
          window.location.replace(url);

        }
        //window.location.replace("/#/weixin?activityId=" + this.getUrlKey('activityId')+"&id="+this.getUrlKey('id'));
      },
    },
    created() {
      this.getBasic()
      this.getWeChat()
    }
  }
</script>
<style lang="scss">
  #hm {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    z-index: 999;
    background: #fff;
  }

</style>

