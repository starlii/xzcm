<template>
  <div class="w">
    <div id="player">
      <div class="pd-10">
        <div class="playerIntro aui-margin-t-15">
          <h2>选手介绍</h2>
          <p><span class="aui-col-xs-6">名称：{{data.name}}</span><span
            class="aui-col-xs-5 aui-text-right">编号：{{data.num}}</span>
          </p>
          <p class="aui-margin-t-5">地址：{{data.declaration}}</p>
        </div>
        <div class="aui-bar aui-bar-btn aui-margin-t-10">
          <div class="aui-bar-btn-item"><p>当前票数</p>
            <p><span v-if="data.currentVotes<0">0</span><span v-else>{{data.currentVotes}}</span>票</p></div>
          <div class="aui-bar-btn-item"><p>居上一名差</p>
            <p>{{data.gapPrevious}}票</p></div>
          <div class="aui-bar-btn-item"><p>票数榜</p>
            <p>第{{data.rank}}名</p></div>
        </div>
        <div class=" bg-white playerDesc">
          <div class="aui-margin-t-10 aui-text-left" v-html="data.playerDesc"></div>
        </div>
        <div class="playerPic" v-for="item in data.images">
          <img :src="item">
        </div>
        <div class="w100 aui-margin-t-15">
          <div class="aui-bar-tab-item" @click="signBtn()">
            <el-button type="danger">我也要参加</el-button>
          </div>
        </div>
      </div>
      <div class="giftMask"></div>
      <div class="gift">
        <span class="aui-col-xs-4"><el-button icon="iconfont icon-toupiao" @click="vote()">给ta投票</el-button></span>
        <div class="aui-bar-tab-item" @click="signBtn()"><span class="aui-col-xs-4 aui-pull-right"><el-button
          icon="iconfont icon-wode">我要报名</el-button></span></div>
        <div class="giftWrap" @click="sendDiamond()">
          <div class="giftIcon">
            <img src="../assets/images/diamond.png">
          </div>
          <!-- <p>送钻石</p>-->
        </div>
      </div>
      <div class="aui-mask" v-bind="{height: activeHeight}"></div>
    </div>
    <!--<BottomNav :complaintInfo="complaintInfo" ref="btm"/>-->
  </div>
</template>
<script>
  import Slider from './common/Slider.vue';
  import BottomNav from './common/BottomNav.vue'

  const pwx = require('weixin-js-sdk')
  //   import OtherComponent from './components/other.vue'
  export default {
    data() {
      return {
        data: [],
        userData: {
          voteNum: 12,
          hot: 12,
          gift: 12,
          id: 12
        },
        giftActive: false,
        maskShow: false,
        activeHeight: '',
        opacity: 9,
        activityId: this.$route.query.activityId,
        complaintInfo: {
          title: "选手详情",
          playerId: this.$route.query.id
        },
        playerId: this.$route.query.id,
        overStatus: this.$cookies.get("overStatus"),
        defaultStatus: this.$cookies.get("defaultStatus"),
        code: this.getUrlKey('code'),
        voteStatus: this.$cookies.get("voteStatus"),
        openid: (() => this.$cookies.get('openId'))()
      }
    },
    watch: {
      playerId: {
        handler(playerId, oldPlayerId) {
          this.init()
        },
        immediate: true
      }
    },
    components: {
      Slider,
      BottomNav
    },
    created() {

      this.ipRecord()
    },
    mounted() {
      this.init()
    },

    methods: {
      getUrlKey: function (name) { // 获取url 参数
        return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ''])[1].replace(/\+/g, '%20')) || null
      },
      init() {
        this.getHomeMessage(this.activityId)
        if(!this.$cookies.get('openId')){
          window.location.replace('/#/home/?activityId='+this.getUrlKey('activityId')+'&id='+this.getUrlKey('id'))
        }
      },
      async getHomeMessage(activityId) {
        const {error, msg, data} = await this.$api.h5.getHomeMessage({activityId: this.getUrlKey('activityId')})
        if (error) return this.$message.error(msg)

        document.title = data.activityName
        this.tips = data.homeMessage
        this.theme = data.themeColor

        if (data.status == true) {
          window.location.replace('/#/EndActivity?activityId=' + this.getUrlKey('activityId'))
          //this.$router.push({path: '/EndActivity'})
        } else {
          this.$cookies.set('overStatus', data.overStatus, '0')
          this.$cookies.set('defaultStatus', data.defaultStatus, '0')
          this.getH5PlayerDetail(this.getUrlKey('id'), data.activityName, this.getUrlKey('activityId'))

        }
      },
      async getH5PlayerDetail(playerId, activityName, activityId) {
        this.$parent.hasJoin(this.activityId, this.$cookies.get("openId"))
        const {error, msg, data} = await this.$api.h5.getH5PlayerDetail({playerId})
        if (error) return this.$message.error(msg)
        this.data = data
        // this.$cookies.remove('openId')

        this.playerShareWechat(activityName, data.name, data.num, data.images[0], "#/home?activityId=" + this.activityId + "&id=" + playerId, this.getUrlKey('activityId'))


      },
      async shareCallback(activityId) {
        const {error, msg, data} = await this.$api.h5.shareCallback({activityId: activityId, playerId: this.getUrlKey('id')})
        if (error) return this.$message.error(msg)
      },
      async playerShareWechat(title, name, playerId, imgUrl, linkUrl, activityId) {
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
          jsApiList: ['checkJsApi', 'updateAppMessageShareData', 'updateTimelineShareData', 'onMenuShareTimeline', 'onMenuShareAppMessage', 'onMenuShareQQ', 'onMenuShareWeibo', 'hideMenuItems', 'showMenuItems', 'hideAllNonBaseMenuItem', 'showAllNonBaseMenuItem', 'translateVoice', 'startRecord', 'stopRecord', 'onRecordEnd', 'playVoice', 'pauseVoice', 'stopVoice', 'uploadVoice', 'downloadVoice', 'chooseImage', 'previewImage', 'uploadImage', 'downloadImage', 'getNetworkType', 'openLocation', 'getLocation', 'hideOptionMenu', 'showOptionMenu', 'closeWindow', 'scanQRCode', 'chooseWXPay', 'openProductSpecificView', 'addCard', 'chooseCard', 'openCard']
        })
        this.$wx.ready(function () {
          let str = location.href.split('#')[0];
          let num = str.indexOf('.com')
          let curUrl = str.substr(0, num + 5)
          pwx.onMenuShareAppMessage({
            title: title,
            desc: "我是" + name + "编号" + playerId + "我正在参加" + title + "请为我加油",
            link: curUrl + linkUrl,
            imgUrl: imgUrl,
            success: function () {
              // 设置成功
              _this.shareCallback(activityId)
            }
          }),
            pwx.onMenuShareTimeline({
              title: title,
              desc: "我是" + name + "编号" + playerId + "我正在参加" + title + "请为我加油",
              link: curUrl + linkUrl,
              imgUrl: imgUrl,
              success: function () {
                // 设置成功
                _this.shareCallback(activityId,)
              }
            })
        }),
          this.$wx.error(function (res) {
            // config信息验证失败会执行error函数，如签名过期导致验证失败，具体错误信息可以打开config的debug模式查看，也可以在返回的res参数中查看，对于SPA可以在这里更新签名。
            console.log('$wx config error', res)
          });
      },
      sendDiamond() {
        if (this.data.approvalStatus == 1) {
          this.$message.error("该选手还未审核,无法送钻石！")
        } else if (this.overStatus == "true") {
          this.$message.error("活动已结束,无法送钻石！")
        } else if (this.defaultStatus == "true") {
          this.$message.error("活动还未开启,无法送钻石！")
        } else {
          this.$router.push({
            path: '/SendDiamond',
            query: {id: this.getUrlKey('id'), dataInfo: this.data, num: this.data.num,activityId: this.activityId}
          });
        }
      },
      signBtn() {
        this.$parent.getHomeMessage(this.activityId).then(val => {
          if (this.overStatus == "true") {
            this.$message.error("活动已结束,无法报名！")
          } else if (this.defaultStatus == "true") {
            this.$message.error("活动还未开启,无法报名！")
          } else {
            if (this.voteStatus == 0) {
              this.$router.push({path: '/SignUpAfter', query: {id: this.getUrlKey('id'),activityId: this.getUrlKey('activityId')}});
            } else {
              this.$router.push({path: '/signUp', query: {activityId: this.activityId}});
            }

          }
        });


      },
      vote() {
        if (this.data.approvalStatus == 1) {
          this.$message.error("该选手还未审核,无法投票！")
        } else if (this.overStatus == "true") {
          this.$message.error("活动已结束,无法投票！")
        } else if (this.defaultStatus == "true") {
          this.$message.error("活动还未开启,无法投票！")
        } else {
          this.getVote()
        }
      },

      async getVote() {
        let params = {
          "openId": this.$cookies.get("openId"),
          /*"openId": "ojF891K1jAvFXr-3EjEe4uvG0La0",*/
          "playerId": this.getUrlKey('id')
        }
        const {error, msg, data, code} = await this.$api.h5.vote(params)
        if (code == 200) {
          this.$router.push({path: '/VoteSuccess', query: {id: this.getUrlKey('id'), backData: data,activityId: this.getUrlKey('activityId')}});
        }
        if (error) return this.$message.error(msg)
      },
      async ipRecord() {
        let params = {

          "playerId": this.getUrlKey('id')
        }
        const {error, msg, data, code} = await this.$api.h5.ipRecord(params)
        if (error) return this.$message.error(msg)

      },

    }
  }
</script>
<style lang="scss">
  #player {
    background: #fff;
    padding-bottom: 2.5rem;

    .aui-bar-btn-item {
      border-color: #ccc;
      padding: 5px 0;

      p {
        font-size: 0.55rem;
        line-height: 0.8rem
      }

    }

    .aui-bar-btn-item:first-child, .aui-bar-btn-item:last-child {
      border-radius: 0
    }

    .playerIntro {
      border: 1px solid #ccc;
      margin: 0 auto;
      border-radius: 10px;
      text-align: left;
      padding: 20px 10px 10px;
      position: relative;

      h2 {
        position: absolute;
        left: 30px;
        top: -15px;
        color: #2dcc70;
        font-size: .7rem;
        font-weight: 800;
        background: #fff;
        padding: 0 20px;
        letter-spacing: 2px;
      }

      p {
        font-size: 0.6rem;
        line-height: 0.8rem;
        overflow: hidden;
      }
    }

    .giftMask {
      background: #000;
      opacity: 0.9;
      z-index: 98;
    }

    .gift, .giftMask {
      height: 2.2rem;
      position: fixed;
      bottom: 2.2rem;
      left: 0;
      width: 100%;
      z-index: 99;

      .el-button {
        padding: 5px 8px;
        margin-top: 0.5rem;
      }

      .giftWrap {
        position: fixed;
        bottom: 2.5rem;
        left: 50%;
        margin-left: -1.8rem;

        .giftIcon {
          width: 3.6rem;
          height: 3.6rem;

          img {
            width: 100%;
            display: block
          }
        }

        p {
          color: #fff;
          line-height: 0.9rem;
        }
      }
    }

    .playerDesc {
      font-size: 0.55rem;
      text-align: left
    }

    .playerPic img {
      width: 100%;
      border-radius: 5px;
      margin: 10px auto 0;
      background: #000;
    }

    .w100 .el-button {
      width: 100%
    }
  }
</style>
