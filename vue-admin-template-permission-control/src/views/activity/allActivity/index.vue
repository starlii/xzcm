<template>
  <star-page>
    <el-tabs v-model="currentTabName" type="border-card" @tab-click="handleTabClick">
      <el-tab-pane v-for="(item, index) in tabList" :label="item.label" :key="index" :name="item.name"/>
      <div class="mt10 search">
        <el-input v-model="search" class="w300 mr20" placeholder="搜索文章标题" @keyup.enter.native="getActivityList"><div slot="append" class="pointer" @click="getActivityList">搜索</div></el-input>
        <span v-if="currentTabName === '1'">
          <el-button v-for="(item, index) in searchTimes" :key="index" type="primary" @click="handleSearchTime(index)">{{ item }}</el-button>
        </span>
        <activity-table :table-list="tableData" :loading="showLoading" :total="total" @change="getActivityList" @update="getActivityList" @page-change="handlePageChange" @section-change="sections = $event"/>
      </div>
    </el-tabs>
  </star-page>
</template>

<script>
import dayjs from 'dayjs'
import activityTable from '../vote/tpls/activity-table'
export default {
  name: 'Index',
  components: { activityTable },
  data() {
    return {
      currentTabName: '1',
      searchTimes: [],
      tableData: [],
      tabList: [{ label: '全部活动', name: '1' }, { label: '进行中', name: '2' }, { label: '已结束', name: '3' }, { label: '今日开始', name: '4' }, { label: '今日结束', name: '5' }, { label: '未开始', name: '6' }]
    }
  },
  created() {
    this.searchTimes = this.getSearchTimes(7)
  },
  methods: {
    handleTabClick() {

    },
    getActivityList() {

    },
    handleSearchTime() {

    },
    getSearchTimes() {
      const arr = [0, 1, 2, 3, 4, 5, 6]
      const times = []
      arr.map(item => times.push(dayjs().add(item, 'day').format('YYYY-MM-DD')))

      return times
    }
  }
}
</script>

<style scoped>

</style>
