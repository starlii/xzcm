<template>
  <div id="voteSuccess">
    <h1 class="successBtn aui-text-center"><img src="../assets/images/success.png"></h1>
    <p class="aui-margin-t-5">恭喜，已经为{{data.name}}投票成功！</p>
    <div class="voteInfo aui-margin-t-10">
      <dl>
        <dt>当前排名</dt>
        <dd><b>{{data.rank}}</b>

        </dd>
      </dl>
      <dl>
        <dt>当前票数</dt>
        <dd><b v-if="data.currentVotes<0">1</b>
          <b v-else>{{data.currentVotes}}</b></dd>
      </dl>
    </div>
    <div class="aui-text-right pd-10">
      <el-button type="text" @click="myRefresh()" class="aui-text-right">刷新排名</el-button>
    </div>
    <div class="pd-10">
      <el-button type="success" class="aui-margin-t-15 w100" @click="sendDiamond()">亲，再给我送个钻石呗</el-button>
      <router-link :to="{path:'/pink/',query:{activityId:this.activityId}}" class="aui-bar-tab-item">
        <el-button type="" class="aui-margin-t-15 w100">查看排名</el-button>
      </router-link>
      <router-link :to="{path:'/index/',query:{activityId:this.activityId}}" class="aui-bar-tab-item">
        <el-button type="" class="aui-margin-t-15 w100">返回首页</el-button>
      </router-link>
    </div>
    <section class="text pd-10  aui-text-left" v-html="giftDes">
    </section>
    <!--<BottomNav :complaintInfo="complaintInfo" ref="btm"/>-->
  </div>
</template>

<script>
  import BottomNav from './common/BottomNav.vue'

  export default {
    data() {
      return {
        name: '',
        activityId: this.getUrlKey('activityId'),
        data: this.$route.query.backData,
        giftDes: '',
        complaintInfo: {
          title: "报名成功"
        },
        overStatus: this.$cookies.get("overStatus"),
        defaultStatus: this.$cookies.get("defaultStatus")
      }
    },
    created() {
      this.getH5PlayerDetail()
      this.getActivityDetail()
      this.$parent.getHomeMessage(this.activityId)
      //this.$refs.btm.hasJoin(this.$cookies.get("openId"))
    },
    components: {
      BottomNav,
    },
    methods: {
      getUrlKey: function (name) {//获取url 参数
        return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ""])[1].replace(/\+/g, '%20')) || null;
      },
      sendDiamond() {
        if (this.overStatus == "true") {
          this.$message.error("活动已结束,无法送钻石！")
        } else if (this.defaultStatus == "true") {
          this.$message.error("活动还未开启,无法送钻石！")
        } else {
          this.$router.push({
            path: '/SendDiamond',
            query: {id: this.$route.query.id, dataInfo: this.data, num: this.data.num}
          });
        }
      },
      async getH5PlayerDetail() {
        const {error, data, msg} = await this.$api.h5.getH5PlayerDetail({playerId: this.$route.query.id})
        if (error) return this.$message.error(msg)
        this.data = data
      },
      myRefresh() {
        window.location.reload();
      },
      async getActivityDetail() {
        const {error, data, msg} = await this.$api.h5.getActivityDetail({activityId: this.getUrlKey('activityId')})
        if (error) return this.$message.error(msg)
        this.giftDes = data.voteSetting.giftDes
      }
    }
  }
</script>

<style lang="scss">
  #voteSuccess {
    .successBtn {
      width: 30%;
      margin: 1rem auto 0;
    }

    padding: 0.5rem 0;

    .voteInfo {
      background: #f9f9f9;
      border-top: 1px dashed #e3e3e3;
      border-bottom: 1px dashed #e3e3e3;
      overflow: hidden;
      padding: 5px 0;
    }

    .voteInfo dl {
      width: 50%;
      display: inline-block;
      float: left;
      text-align: center;
      font-size: 0.55rem;

      dd {
        position: relative
      }

      .el-button {
        position: absolute;
        padding: 3px 0 0 0;
        left: 50%;
        margin-left: 30px;
      }
    }

    .w100 {
      width: 100%
    }

    .el-button + .el-button {
      margin-left: 0
    }
  }
</style>
