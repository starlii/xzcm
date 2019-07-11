<template>
  <div id="complaint" class="w" v-wechat-title="$route.meta.title">
    <div class="aui-content aui-margin-b-15 aui-text-left">
      <ul class="aui-list aui-list-in">
        <li class="aui-list-header">
          请选择投诉该网页的原因
        </li>
        <li class="aui-list-item">
          <router-link
            :to="{path:'/ComplaintSecond/', query:{complaintInfo:this.$route.query.complaintInfo,reportType:1,activityId:activityId}}"
            class="aui-bar-tab-item">
            <div class="aui-list-item-inner aui-list-item-arrow">
              <div class="aui-list-item-title">网页包含欺诈信息（如：假红包）</div>
            </div>
          </router-link>
        </li>
        <li class="aui-list-item">
          <router-link
            :to="{path:'/ComplaintSecond/', query:{complaintInfo:this.$route.query.complaintInfo,reportType:2,activityId:activityId}}"
            class="aui-bar-tab-item">
            <div class="aui-list-item-inner aui-list-item-arrow">
              <div class="aui-list-item-title">网页包含色情信息</div>
            </div>
          </router-link>
        </li>
        <li class="aui-list-item">
          <router-link
            :to="{path:'/ComplaintSecond/', query:{complaintInfo:this.$route.query.complaintInfo,reportType:3,activityId:activityId}}"
            class="aui-bar-tab-item">
            <div class="aui-list-item-inner aui-list-item-arrow">
              <div class="aui-list-item-title">网页包含暴力恐怖信息</div>
            </div>
          </router-link>
        </li>
        <li class="aui-list-item">
          <router-link
            :to="{path:'/ComplaintSecond/', query:{complaintInfo:this.$route.query.complaintInfo,reportType:4,activityId:activityId}}"
            class="aui-bar-tab-item">
            <div class="aui-list-item-inner aui-list-item-arrow">
              <div class="aui-list-item-title">网页包含政治敏感信息</div>
            </div>
          </router-link>
        </li>
        <li class="aui-list-item">
          <router-link
            :to="{path:'/ComplaintSecond/', query:{complaintInfo:this.$route.query.complaintInfo,reportType:5,activityId:activityId}}"
            class="aui-bar-tab-item">
            <div class="aui-list-item-inner aui-list-item-arrow">
              <div class="aui-list-item-title">网页在收集个人隐私信息（如：钓鱼链接）</div>
            </div>
          </router-link>
        </li>
        <li class="aui-list-item">
          <router-link
            :to="{path:'/ComplaintSecond/', query:{complaintInfo:this.$route.query.complaintInfo,reportType:6,activityId:activityId}}"
            class="aui-bar-tab-item">
            <div class="aui-list-item-inner aui-list-item-arrow">
              <div class="aui-list-item-title">网页包含诱导分享／关注性质的內容</div>
            </div>
          </router-link>
        </li>
        <li class="aui-list-item">
          <router-link
            :to="{path:'/ComplaintSecond/', query:{complaintInfo:this.$route.query.complaintInfo,reportType:7,activityId:activityId}}"
            class="aui-bar-tab-item">
            <div class="aui-list-item-inner aui-list-item-arrow">
              <div class="aui-list-item-title">网页可能包含谣言信息</div>
            </div>
          </router-link>
        </li>
        <li class="aui-list-item">
          <router-link
            :to="{path:'/ComplaintSecond/', query:{complaintInfo:this.$route.query.complaintInfo,reportType:8,activityId:activityId}}"
            class="aui-bar-tab-item">
            <div class="aui-list-item-inner aui-list-item-arrow">
              <div class="aui-list-item-title">网页包含赌博信息</div>
            </div>
          </router-link>
        </li>
      </ul>
      <div class="el-button--text aui-margin-t-15 aui-font-size-12">
        <a href="https://mp.weixin.qq.com/s/TbX1CcZhQNReneXVc3At9Q"> 遇到网页流量被劫持怎么办</a>
      </div>
    </div>
    <div class="complaintNotice aui-font-size-12 aui-text-center"><a
      href="https://weixin110.qq.com/security/readtemplate?t=w_security_center_website/report_agreement&lang=zh_CN">投诉须知</a>
    </div>
  </div>
</template>

<script>

  export default {
    name: 'Pink',
    data() {
      return {
        activityId: this.getUrlKey('activityId')
      }
    },
    methods: {
      getUrlKey: function (name) {//获取url 参数
        return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ""])[1].replace(/\+/g, '%20')) || null;
      }
    }
  }
</script>
<style lang="scss">
  #complaint {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    z-index: 999;
    height: 100%;
    background: #efeff3;
    .aui-list .aui-list-header {
      background: #efeff3;
      text-align: left;
      font-size: 0.6rem;
      border-bottom: 1px solid #e5e5e5;
      color: #999;
    }

    .aui-list-item a {
      width: 100%;
      color: #5c6b90;
    }

    .aui-list .aui-list-item-title {
      font-size: 0.6rem
    }

    .aui-list .aui-list-item-inner, .aui-list .aui-list-item {
      min-height: 1.8rem
    }

    .el-button--text {
      padding-left: .75rem
    }

    .complaintNotice {
      position: fixed;
      bottom: 1rem;
      width: 100%
    }
  }
</style>
