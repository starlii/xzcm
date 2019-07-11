<template>
  <div id="introduce" class="w">
    <!--<Slider/>-->
    <div class="tit aui-margin-t-10">
      <span>活动介绍</span>
    </div>
    <section class="text pd-10  aui-text-left" v-html="data">
    </section>
    <!--<BottomNav :complaintInfo="complaintInfo" ref="btm"/>-->
  </div>

</template>
<script>
  import Slider from './common/Slider.vue';
  import BottomNav from './common/BottomNav.vue'
  // 生成商品列数据

  export default {
    data() {
      return {
        data: '',
        activityId: this.$cookies.get("activityId"),
        complaintInfo: {
          title: "活动介绍"
        }
      }
    },
    components: {
      Slider,
      BottomNav
    },
    methods: {
      getUrlKey: function (name) { // 获取url 参数
        return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ''])[1].replace(/\+/g, '%20')) || null
      },
      async getActivityDetail() {
        const {error, data, msg} = await this.$api.h5.getActivityDetail({activityId: this.getUrlKey('activityId')})
        if (error) return this.$message.error(msg)
        this.data = data.voteSetting.activityRule
      }
    },
    mounted: function () {
      this.getActivityDetail()
      this.$parent.getHomeMessage(this.getUrlKey('activityId'))
      //this.$refs.btm.hasJoin(this.$cookies.get("openId"))
    }
  }
</script>

<style lang="scss">
  #introduce {
    .el-button {
      width: 90%
    }
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
  }
</style>
