<template>
  <div>
    <el-tabs v-model="activeName" type="border-card" @tab-click="handleClick">
      <el-tab-pane v-for="(item, index) in tabList" :key="index" :name="item.name">
        <span slot="label">
          <div v-if="index === 1">{{ item.label }} <el-button type="danger">{{ `礼物:${giftAmount}元` }}</el-button></div>
          <span v-else>{{ item.label }}</span>
        </span>
        <gift-table :table-data="tableData" :loading="tableLoading" :activity-id="$route.params.activityId"/>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import giftTable from './tpls/gift-table'
export default {
  components: { giftTable },
  data() {
    return {
      activeName: '1',
      tableData: [],
      originTableDate: [],
      giftAmount: 0,
      tabList: [
        { name: '1', label: '全部记录' },
        { name: '0', label: '已支付记录' }
      ],
      tableLoading: false
    }
  },
  created() {
    this.getGiftLog()
  },
  methods: {
    handleClick() {
      this.tableData = this.activeName === '1' ? this.originTableDate : this.originTableDate.filter(item => item.status === 0)
    },
    async getGiftLog() {
      const params = {}
      if (this.$route.query.activityId) params.activityId = this.$route.query.activityId
      this.tableLoading = true
      const { error, data, msg } = await this.$api.activity.getGiftLog(params)
      this.tableLoading = false
      if (error) return this.$message.error(msg)
      let num = 0
      const giftList = data.filter(i => i.status === 0)
      for (const i of giftList) {
        if (i.giftAmount) num += i.giftAmount
      }
      this.giftAmount = num
      this.originTableDate = data
      this.tableData = data
    }
  }
}
</script>

<style scoped>

</style>
