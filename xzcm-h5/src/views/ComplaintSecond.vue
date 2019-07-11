<template>
  <div id="complaint"  v-wechat-title="$route.meta.title">
    <div class="aui-content aui-margin-b-15 aui-text-left">
      <ul class="aui-list aui-list-in">
        <li class="aui-list-header">
          你可以
        </li>
        <li class="aui-list-item" @click="addReportPro">
          <div class="aui-list-item-inner aui-list-item-arrow">
            <div class="aui-list-item-title">提交给微信团队审核</div>
          </div>
        </li>
      </ul>
    </div>
    <div class="complaintNotice aui-font-size-12 aui-text-center"><a
      href="https://weixin110.qq.com/security/readtemplate?t=w_security_center_website/report_agreement&lang=zh_CN">投诉须知</a>
    </div>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        ips: '',
      }
    },
    methods: {

      async addReportPro() {
        let params = {
          "activityId": this.getUrlKey('activityId'),
          "reportType": this.$route.query.reportType,
          "reportPage": this.$route.query.complaintInfo.title,
          "playerId": this.$route.query.complaintInfo.playerId,
        }
        const {error, msg, data, code} = await this.$api.h5.addReportPro(params)
        if (code == 200) {
          this.$router.push({path: '/complaintDone'});
        }
        if (error) return this.$message.error(msg)
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
    .aui-list .aui-list-header {
      background: #efeef3;
      text-align: left;
      font-size: 0.6rem;
      border-bottom: 1px solid #ddd;
      color: #999;
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
