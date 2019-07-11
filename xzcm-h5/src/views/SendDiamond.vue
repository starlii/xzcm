<template>
  <div id="sendDiamond">
    <div class="headImg aui-margin-t-15">
      <img :src="this.$route.query.dataInfo.images[0]">
    </div>
    <!--<div>openId: {{openId}}</div>-->
    <!--<div>支付信息: {{payMessage}}</div>-->
    <p>{{this.num}}号 {{this.$route.query.dataInfo.name}} <br/>打赏 <span v-if="amount!=0">{{amount.giftPrice}}钻 = {{amount.giftPrice*3}}票</span>
      <span v-else>{{custom}}钻 = {{custom*3}}票</span>
    </p>
    <div class="amountList aui-margin-t-15">
      <el-radio-group v-model="amount" size="mini">
        <el-radio-button :label="item" v-for="(item,index) in giftList" :key="index"><span>{{item.giftPrice}}</span>钻
        </el-radio-button>
        <el-radio-button label="0">自定义</el-radio-button>
      </el-radio-group>
    </div>
    <h2 class="aui-margin-t-15"><b>注：</b>1元打赏1钻，1钻等于3票</h2>
    <div v-if="amount==0">
      <div class="customWrap aui-margin-t-15">
        <el-input v-model="custom" placeholder="请输入自定义金额" type="number" @change="checkCustom"></el-input>
      </div>
    </div>
    <div class="aui-content aui-margin-t-15">
      <ul class="aui-list aui-select-list">
        <li class="aui-list-item">
          <div class="aui-list-item-inner">
            <label><img src="../assets/images/wx.jpg" class="iconX aui-margin-r-10">使用微信支付<input
              class="aui-radio aui-text-right"
              type="radio" name="radio"
              checked></label>
          </div>
        </li>
      </ul>
    </div>
    <el-row class="pd-10">
      <el-button type="success" @click="handlePay()">打赏钻石</el-button>
    </el-row>
    <section class="text pd-10  aui-text-left " v-html="giftDes">
    </section>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        amount: '',
        custom: 1,
        num: this.$route.query.num,
        playerId: this.$route.query.id,
        openId: this.$cookies.get("openId"),
        activityId: this.getUrlKey('activityId'),
        giftList: [],
        giftDes: '',
        payMessage: null
      }
    },
    created() {
      this.handleGetGiftList()
      this.getActivityDetail()
      this.$parent.getHomeMessage(this.getUrlKey('activityId'))
    },
    methods: {
      getUrlKey: function (name) {//获取url 参数
        return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ""])[1].replace(/\+/g, '%20')) || null;
      },
      async handleGetGiftList() {
        const {error, data, msg} = await this.$api.h5.getGift({activityId: this.getUrlKey('activityId')})
        if (error) return this.$message.error(msg)
        this.giftList = data
        this.amount = this.giftList[1]
      },
      checkCustom: function () {
        if (this.custom < 1) {
          this.$message({
            message: '金额不能小于1',
            type: 'warning'
          });
          this.custom = 1
        }
      },
      async handlePay() {
        console.log('weChat pay test *********')
        if (this.$cookies.get("openId")) {
          console.log('has openId', this.$cookies.get("openId"))
          let params = {
            openId: this.$cookies.get("openId"),
            playerId: this.getUrlKey('id')
          }
          if (this.amount == 0) {
            params.giftAmount = this.custom
          } else {
            params.giftId = this.amount.id
          }
          const {error, data, msg} = await this.$api.h5.gift(params)
          if (error) return this.$message.error(msg)

          this.$wxPay({
            appId: data.appId,
            nonceStr: data.nonceStr,
            package: data.package,
            paySign: data.paySign,
            signType: data.signType,
            timeStamp: data.timeStamp
          }).then(res => {
            this.payMessage = res
            console.log('pay success', res)
            this.message = res
            if (res.success == false) {
              this.$router.push({
                path: '/payError',
                query: {id: this.getUrlKey('id'), activityId: this.getUrlKey('activityId')}
              });
            } else {
              this.$router.push({
                path: '/paySucc',
                query: {id: this.getUrlKey('id'), activityId: this.getUrlKey('activityId')}
              });
            }

          })
            .catch(err => {
              this.payMessage = err
              console.log('pay error', err)
              this.message = err

            })
        } else {
          this.message = 'not openId'
          console.log('not openId')
        }
      },
      async getActivityDetail() {
        const {error, data, msg} = await this.$api.h5.getActivityDetail({activityId:  this.getUrlKey('activityId')})
        if (error) return this.$message.error(msg)
        this.giftDes = data.voteSetting.giftDes
      }
    }
  }
</script>

<style lang="scss">
  #sendDiamond {
    .headImg img {
      width: 4rem;
      height: 4rem;
      border-radius: 4rem;
      border: 2px solid #dcdfe6;
    }

    h2 {
      font-size: 0.7rem
    }

    .amountList {
      .el-radio-button {
        width: 29.2%;
        margin: 8px 0 0 3%;
        float: left;

      }

      .el-radio-group {
        width: 100%
      }

      .el-radio-button--mini .el-radio-button__inner {
        width: 100%;
        padding: 0;
        border-left: 1px solid #dcdfe6;
        height: 1.8rem;
        line-height: 1.8rem;
        font-size: 0.7rem;
        box-shadow: 0 0 0 0 #409EFF;
        -webkit-box-shadow: 0 0 0 0 #409EFF;
        border-radius: 8px;
      }

      .el-radio-button:first-child .el-radio-button__inner, .el-radio-button:last-child .el-radio-button__inner {
        border-radius: 8px
      }

      .el-radio-button__orig-radio:checked + .el-radio-button__inner {
        background: #f85a5b;
        border-color: #f85a5b;
        color: #fff;

      }

      .el-radio-button__inner {
        font-size: 1rem;
        font-style: italic;
        padding-right: 5px;
        color: #f85a5b
      }

      #sendDiamond .amountList .is-active .el-radio-button__inner span {
        color: #fff
      }

      #sendDiamond .amountList .is-active span {
        color: #fff
      }

    }

    .customWrap {
      padding: 0 0.5rem;

      .el-input {
        border: 1px solid #ccc;
        border-radius: 5px;

        input {
          font-size: 0.55rem;
          min-height: auto;
          text-indent: 10px;
          height: 30px;
          line-height: 30px;
        }
      }
    }

    .el-button--success {
      width: 100%
    }

    .iconX {
      width: 22px;
      height: 22px;
      vertical-align: middle;
    }

    .aui-list-item-inner {
      font-size: 0.6rem;
      text-align: left
    }

    .aui-checkbox, .aui-radio {
      width: 0.8rem;
      height: 0.8rem;
      float: right;
      margin-top: 5px;
    }

    .aui-checkbox.aui-checked:after, .aui-checkbox.aui-checked:before, .aui-checkbox:checked:after, .aui-checkbox:checked:before, .aui-radio.aui-checked:after, .aui-radio.aui-checked:before, .aui-radio:checked:after, .aui-radio:checked:before {
      width: 0.3rem;
      margin-left: -0.18rem;
    }

    .aui-radio:checked {
      background: #10c818;
      border: 1px solid #10c818
    }
  }
</style>
