<template>
  <star-page>
    <el-tabs type="border-card">
      <el-tab-pane v-for="(item, index) in tabList" :key="index" :label="item.label">
        <div :is="item.com" :id="playerId"/>
      </el-tab-pane>
    </el-tabs>
  </star-page>
</template>

<script>
import voteLogs from './tpls/vote-logs'
import giftLogs from './tpls/gift-logs'
export default {
  name: 'Index',
  components: { voteLogs, giftLogs },
  data() {
    return {
      playerId: null,
      tabList: [{ name: 'voteLogs', com: 'voteLogs', label: '投票记录' }, { name: 'giftLogs', com: 'giftLogs', label: '礼物记录' }]
    }
  },
  created() {
    this.playerId = this.$route.query.playerId
  }
}
</script>

<style scoped>

</style>
