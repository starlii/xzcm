<template>
  <star-page>
    <el-tabs v-model="currentTab" type="border-card">
      <el-tab-pane v-for="(item, index) in tabList" :key="index" :label="item.label" :name="item.name">
        <div :is="item.com" />
      </el-tab-pane>
    </el-tabs>
  </star-page>
</template>

<script>
import reportTable from './tpls/report-table'
import chargeTable from './tpls/charge-table'
export default {
  name: 'Index',
  components: { reportTable, chargeTable },
  data() {
    return {
      tabList: [
        { label: '结算报表', name: '1', com: 'reportTable' },
        { label: '充值报表', name: '2', com: 'chargeTable' }
      ],
      currentTab: '1',
      searchForm: {
        time: null
      }
    }
  }
}
</script>

<style scoped>

</style>
