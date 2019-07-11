<template>
  <footer class="aui-bar aui-bar-tab" id="footer">
    <router-link :to="{path:'/index/',query:{activityId:activityId}}" class="aui-bar-tab-item">
      <i class="iconfont icon-shouye"></i>
      <div class="aui-bar-tab-label">首页</div>
    </router-link>
    <router-link :to="{path:'/Introduce/',query:{activityId:activityId}}" class="aui-bar-tab-item ">
      <i class="iconfont icon-jieshao"></i>
      <div class="aui-bar-tab-label">介绍</div>
    </router-link>
    <router-link :to="{path:'/prize/',query:{activityId:activityId}}" class="aui-bar-tab-item ">
      <i class="iconfont icon-liwuhuodong"></i>
      <div class="aui-bar-tab-label">奖品</div>
    </router-link>
    <router-link :to="{path:'/player/',query:{id:this.backData.playerId,activityId:activityId}}"
                 class="aui-bar-tab-item "
                 v-if="this.backData.hasJoinStatus==0">
      <i class="iconfont icon-wode"></i>
      <div class="aui-bar-tab-label">我的</div>
    </router-link>
    <router-link :to="{path:'/SignUp/',query:{activityId:activityId}}" class="aui-bar-tab-item " v-else>
      <i class="iconfont icon-baoming"></i>
      <div class="aui-bar-tab-label">报名</div>
    </router-link>
    <router-link :to="{path:'/pink/',query:{activityId:activityId}}" class="aui-bar-tab-item ">
      <i class="iconfont icon-paihangbang"></i>
      <div class="aui-bar-tab-label">排名</div>
    </router-link>
    <router-link :to="{path:'/complaint/', query:{complaintInfo:this.$route.meta,activityId:activityId}}" class="aui-bar-tab-item ">
      <i class="iconfont icon-tousu"></i>
      <div class="aui-bar-tab-label">投诉</div>
    </router-link>
  </footer>
</template>
<script>

  export default {
    props: ['backData'],
    data() {
      return {
        activityId: this.getUrlKey('activityId'),
        voteStatus: this.$cookies.get("voteStatus"),
        playerId: ''
      }
    },
    created() {
      this.showComplaintInfo()
      // this.hasJoin(this.activityId, this.complaintInfo.openId)
    },
    methods: {
      getUrlKey: function (name) {//获取url 参数
        return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ""])[1].replace(/\+/g, '%20')) || null;
      },
      showComplaintInfo() {
        this.$emit("childToParent", this.complaintInfo)
      },
      /*async hasJoin(openId) {
        const {error, msg, data, code} = await this.$api.h5.hasJoin({activityId: this.activityId, openId: openId})
        this.hasJoinStatus = data.hasJoin
        this.playerId = data.playerId
      }*/
    }

  }
</script>
