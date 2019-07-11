<template>
  <div id="hm"></div>
</template>
<script>

  export default {
    data() {
      return {
        appId: '',
        getUrl: '',
        activityId: this.getUrlKey('activityId'),
        id: this.getUrlKey('id'),
        mualStatus: '',
        playerId: this.$cookies.get("playerId"),
        openid: (() => this.$cookies.get('openId'))(),

      }
    },
    created() {
      /* this.getWeChat()
       this.getBasic()*/
      this.getHomeMessage(this.getUrlKey('activityId'))
    },
    methods: {
      getUrlKey: function (name) {//获取url 参数
        return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ""])[1].replace(/\+/g, '%20')) || null;
      },
      async getHomeMessage(activityId) {
        const {error, msg, data} = await this.$api.h5.getHomeMessage({activityId})
        if (error) return this.$message.error(msg)
        document.title = data.activityName
        if (data.mualStatus == true) {
          window.location.replace('/#/EndActivity?activityId=' + activityId)
        } else {
          this.getWeChat().then(val => {
            this.getBasic().then(val => {
              this.init(this.appId, this.getUrl)
            })

          });

        }
      },
      async getWeChat() {
        const {error, msg, data} = await this.$api.weChat.getWeChat()
        if (error) return this.$message.error(msg)
        return Promise.resolve(this.appId = data.appId)
      },
      async getBasic() {
        const {error, msg, data} = await this.$api.weChat.getBasic()
        if (error) return this.$message.error(msg)

        return Promise.resolve(this.getUrl = data.platformDomain)
      },
      init(appId, getUrl) {
        if (this.openid) {
          this.$cookies.remove('openId')
        }
        //id 不等于空的时候跳转到选手页
        if (this.getUrlKey('id')) {
          let urlNow = encodeURIComponent(getUrl + "/#/weixin?activityId=" + this.getUrlKey('activityId') + "&id=" + this.getUrlKey('id'))
          let scope = 'snsapi_base';    //snsapi_userinfo   //静默授权 用户无感知
          let url = `https://open.weixin.qq.com/connect/oauth2/authorize?appid=${appId}&redirect_uri=${urlNow}&response_type=code&scope=${scope}&state=1#wechat_redirect`;
          window.location.replace(url);

        } else {
          let urlNow = encodeURIComponent(getUrl + "/#/weixin?activityId=" + this.getUrlKey('activityId'))
          let scope = 'snsapi_base';    //snsapi_userinfo   //静默授权 用户无感知
          let url = `https://open.weixin.qq.com/connect/oauth2/authorize?appid=${appId}&redirect_uri=${urlNow}&response_type=code&scope=${scope}&state=1#wechat_redirect`;
          window.location.replace(url);
        }
      },

    },

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

