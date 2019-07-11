<template>
  <div id="pink" class="w endActivity" v-wechat-title="$route.meta.title">
    <!--<Slider/>-->
    <!-- <p class="red">恭喜您，中奖</p>-->
    <div class="tit aui-margin-t-10">
      <span>最新排名</span>
    </div>
    <section class="text pd-10">
      <ul>
        <li><span class="pinkRank aui-pull-left">排名</span><span
          class="pinkPlayer aui-text-center aui-pull-left">选手</span><span class="pinkVotes aui-pull-left"> 票数</span>
        </li>
        <li v-for="item in pinkList">
          <span class="pinkRank aui-pull-left">
          <span v-if="item.rank==1"><img src="../assets/images/number1.jpg"></span>
        <span v-else-if="item.rank==2"><img
          src="../assets/images/number2.jpg"></span><span v-else-if="item.rank==3"><img
            src="../assets/images/number3.jpg"></span><span v-else>{{item.rank}}</span></span><span
          class="pinkPlayer aui-pull-left"><span v-if="item.image"> <img :src="item.image"></span><span v-else><img
          src="../../static/photo.png"></span>{{item.name}}</span><span class="pinkVotes"
                                                                        v-if="item.votes<0">0</span><span
          class="pinkVotes" v-else>{{item.votes}}</span>
        </li>
      </ul>
    </section>
  </div>

</template>
<script>
  import Slider from './common/Slider.vue';
  import BottomNav from './common/BottomNav.vue'
  import loading from './common/loading.vue'

  export default {
    data() {
      return {
        loading: true,
        scroll: true,
        pinkList: [],
        activityId: this.getUrlKey('activityId'),
        complaintInfo: {
          title: this.$route.meta.title
        }
      }
    },
    components: {
      Slider,
      BottomNav,
      loading
    },
    methods: {
      init() {
        if (!this.activityId) {
          this.activityId = this.$cookies.get("activityId")
        }
      },
      getUrlKey: function (name) { // 获取url 参数
        return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ''])[1].replace(/\+/g, '%20')) || null
      },
      async getPlayerRank() {
        const {error, data, msg} = await this.$api.h5.getPlayerRank({activityId: this.activityId})
        if (error) return this.$message.error(msg)
        this.pinkList = data
      }
    },
    mounted: function () {
      this.init()
      this.getPlayerRank()
    }
  }
</script>

<style lang="scss">
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

  .endActivity {
    top: 0;
    left: 0;
    width: 100%;
    z-index: 999;
    background: #fff;
  }

  .wrap {
    padding-bottom: 0
  }

  #pink {
    .el-button {
      width: 90%
    }

    .text {
      text-align: left;
      font-size: 0.6rem;
    }

    .text p {
      color: #000;
      font-size: 0.5rem;
      line-height: 1rem;
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
