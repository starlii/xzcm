<template>
  <div>
    <!--<el-button @click="shareWechat">测试分享</el-button>-->
    <div v-if="tips!=null">
      <div id="app" :class="themeClass">
        <Marquee :tips="tips"></Marquee>
        <div class="wrap">
          <div
            v-if="$route.name=='index' ||$route.name=='introduce' || $route.name=='prize' || $route.name=='pink' || $route.name=='endActivity' || $route.name=='player'">
            <Slider/>
          </div>
          <router-view/>
          <div
            v-if="$route.name=='index' ||$route.name=='introduce' || $route.name=='prize' || $route.name=='pink' || $route.name=='player'|| $route.name=='voteSuccess'">
            <BottomNav ref="btm" :backData="backData"/>
          </div>
        </div>
      </div>
    </div>
    <div v-else>
      <div id="app" :class="themeClass" style="padding: 0" class="noTips">
        <div class="wrap">
          <div
            v-if="$route.name=='index' ||$route.name=='introduce' || $route.name=='prize' || $route.name=='pink' || $route.name=='endActivity' || $route.name=='player'">
            <Slider/>
          </div>
          <router-view/>
          <div
            v-if="$route.name=='index' ||$route.name=='introduce' || $route.name=='prize' || $route.name=='pink' || $route.name=='player'|| $route.name=='voteSuccess'">
            <BottomNav ref="btm" :backData="backData"/>
          </div>
        </div>
      </div>
    </div>
  </div>

</template>

<script>
  import Marquee from './views/common/Marquee.vue'
  import BottomNav from './views/common/BottomNav.vue'
  import Slider from './views/common/Slider.vue'

  const swx = require('weixin-js-sdk')

  export default {
    name: 'App',
    components: {
      Marquee,
      BottomNav,
      Slider
    },
    data() {
      return {
        tips: null,
        theme: '6281fd',
        openid: (() => this.$cookies.get('openId'))(),
        backData: {
          "hasJoinStatus": 1,
          "playerId": ''
        }
      }
    },
    computed: {
      themeClass() {
        return `theme-${this.theme}`
      }
    },
    mounted() {

    },
    created() {
      //if (this.$cookies.get("openId")) {this.$cookies.remove('openId')}
    },
    methods: {
      async hasJoin(activityId, openid) {
        const {error, msg, data, code} = await this.$api.h5.hasJoin({activityId: activityId, openId: openid})
        this.backData.hasJoinStatus = data.hasJoin
        this.backData.playerId = data.playerId
        this.$cookies.set('voteStatus', data.hasJoin, '0')
      },
      getUrlKey: function (name) { // 获取url 参数
        return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ''])[1].replace(/\+/g, '%20')) || null
      },
      async shareCallback() {
        const {error, msg, data} = await this.$api.h5.shareCallback({activityId: this.getUrlKey('activityId')})
        if (error) return this.$message.error(msg)
      },
      async getHomeMessage(activityId) {
        const {error, msg, data} = await this.$api.h5.getHomeMessage({activityId})
        if (error) return this.$message.error(msg)
        this.tips = data.homeMessage
        this.theme = data.themeColor
        document.title = data.activityName
        this.$cookies.set('activityName', data.activityName, '0')
        this.$cookies.set('overStatus', data.overStatus, '0')
        this.$cookies.set('defaultStatus', data.defaultStatus, '0')
        this.shareWechat(data.activityName, '', '', data.image, "/#/home?activityId=" + this.getUrlKey('activityId'), this.getUrlKey('activityId'))
      },
      async shareWechat(title, name, playerId, imgUrl, linkUrl, activityId) {
        let _this = this
        const url = encodeURIComponent(location.href.split('#')[0])
        const {error, data, msg} = await this.$api.weChat.sign({url})
        if (error) return this.$message.error(msg)
        this.$wx.config({
          debug: !1,
          appId: data.appId,
          timestamp: data.timestamp,
          nonceStr: data.noncestr,
          signature: data.signature,
          jsApiList: ['checkJsApi', 'updateAppMessageShareData', 'updateTimelineShareData ', 'onMenuShareTimeline', 'onMenuShareAppMessage', 'onMenuShareQQ', 'onMenuShareWeibo', 'hideMenuItems', 'showMenuItems', 'hideAllNonBaseMenuItem', 'showAllNonBaseMenuItem', 'translateVoice', 'startRecord', 'stopRecord', 'onRecordEnd', 'playVoice', 'pauseVoice', 'stopVoice', 'uploadVoice', 'downloadVoice', 'chooseImage', 'previewImage', 'uploadImage', 'downloadImage', 'getNetworkType', 'openLocation', 'getLocation', 'hideOptionMenu', 'showOptionMenu', 'closeWindow', 'scanQRCode', 'chooseWXPay', 'openProductSpecificView', 'addCard', 'chooseCard', 'openCard']
        })
        this.$wx.ready(function () {
          let str = location.href.split('#')[0];
          let num = str.indexOf('.com')
          let curUrl = str.substr(0, num + 5)
          swx.onMenuShareAppMessage({
            title: title,
            desc: "我正在参加" + title + "请为我加油",
            link: curUrl + linkUrl,
            imgUrl: imgUrl,
            success: function () {
              // 设置成功
              _this.shareCallback(activityId)
            }
          }),
            swx.updateTimelineShareData({
              title: title,
              desc: "我正在参加" + title + "请为我加油",
              link: curUrl + linkUrl,
              imgUrl: imgUrl,
              success: function () {
                // 设置成功
                _this.shareCallback(activityId)
              }
            })
        })
        this.$wx.error(function (res) {
          // config信息验证失败会执行error函数，如签名过期导致验证失败，具体错误信息可以打开config的debug模式查看，也可以在返回的res参数中查看，对于SPA可以在这里更新签名。
          console.log('$wx config error', res)
        });
      },
    }
  }
</script>

<style lang="scss">
  @import '/assets/css/theme.scss';

  body {
    background: #fff
  }

  #app {
    text-align: center;
    color: #333;
    padding-top: 1.6rem;
  }

  img {
    display: inline-block;
    max-width: 100%;
  }

  /*场景切换*/
  .wrap {
    padding-bottom: 2.8rem;
  }

</style>
