<template>
  <div id="pink" class="w">
    <!--<Slider :list="pics"/>-->
    <div class="tit aui-margin-t-10">
      <span>最新排名</span>
    </div>
    <section class="text pd-10">
      <ul>
        <li><span class="pinkRank aui-pull-left">排名</span><span
          class="pinkPlayer aui-text-center aui-pull-left">选手</span><span class="pinkVotes aui-pull-left"> 票数</span>
        </li>
        <li v-for="item in pinkList">
          <router-link :to="{path:'/player',query:{id:item.playerId,activityId:activityId}}"
                       :key="item.playerId"><span
            class="pinkRank aui-pull-left">
          <span v-if="item.rank==1"><img
            src="../assets/images/number1.jpg"></span>
        <span v-else-if="item.rank==2"><img
          src="../assets/images/number2.jpg"></span><span v-else-if="item.rank==3"><img
            src="../assets/images/number3.jpg"></span><span v-else>{{item.rank}}</span></span><span
            class="pinkPlayer aui-pull-left"><span v-if="item.image"> <img :src="item.image"></span><span v-else><img
            src="../../static/photo.png"></span>{{item.name}}</span><span class="pinkVotes" v-if="item.votes<0">0</span><span
            class="pinkVotes" v-else>{{item.votes}}</span>
          </router-link>
        </li>
      </ul>
    </section>
    <!--<BottomNav :complaintInfo="complaintInfo" ref="btm"/>-->
  </div>

</template>
<script>
  import Slider from './common/Slider.vue';
  import BottomNav from './common/BottomNav.vue'
  import loading from './common/loading.vue'

  export default {
    data() {
      return {
        pics: [],
        loading: true,
        scroll: true,
        pinkList: [],
        activityId: this.getUrlKey('activityId'),
        complaintInfo: {
          title: "排行榜"
        }
      }
    },
    components: {
      Slider,
      BottomNav,
      loading
    },
    methods: {
      getUrlKey: function (name) {//获取url 参数
        return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ""])[1].replace(/\+/g, '%20')) || null;
      },
      async getPlayerRank(activityId) {
        const {error, msg, data} = await this.$api.h5.getPlayerRank({activityId})
        this.pinkList = data
        if (error) return this.$message.error(msg)
      },

    },
    created() {
      this.getPlayerRank( this.getUrlKey('activityId'))
      this.$parent.getHomeMessage( this.getUrlKey('activityId'))

    },
    mounted: function () {
      //this.$refs.btm.hasJoin(this.$cookies.get("openId"))
    }
  }
</script>

<style lang="scss">
  #pink {
    .tit {
      height: 1.5rem;
      line-height: 1.5rem;
      background: url("../assets/images/line.jpg") repeat-x;
      width: 96%;
      margin: 0 auto
    }
    .tit span {
      background: #fff;
      padding: 0 15px;
      color: #000;
      font-size: 0.8rem;
      font-weight: 600
    }
    .el-button {
      width: 90%
    }

    li {
      border-bottom: 1px solid #ccc;
      height: 2rem;
      line-height: 2rem;
      overflow: hidden;
      box-sizing: inherit;

      a {
        color: #000
      }

      span {
        display: inline-block;
        font-size: 0.5rem
      }

      .pinkRank {
        width: 22%;
        text-align: center;

        img {
          width: 1.2rem;
          height: 1.2rem;
          vertical-align: middle;
        }
      }

      .pinkPlayer {
        width: 60%;
        text-align: left;
        height: 2rem;
        line-height: 2rem;
        overflow: hidden;
        img {
          width: 1.4rem;
          height: 1.4rem;
          border-radius: 1.4rem;
          margin: 0;
          vertical-align: middle;
          margin-right: 8px;
        }
      }
      .pinkVotes {
        width: 18%;
        text-align: center;
      }
    }

  }

</style>
