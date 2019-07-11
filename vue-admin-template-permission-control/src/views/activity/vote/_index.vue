<template>
  <star-page class="vote">
    <div v-if="!isAll" class="mb10">
      <el-button :loading="downloadLoading" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
      <el-button type="primary" @click="$router.push({ name: 'addActivity' })">添加活动</el-button>
      <span v-permission>
        <el-button type="primary" @click="$router.push({name: 'allActivity'})">查看所有活动</el-button>
      </span>
      <el-button type="primary" @click="$router.push({ name: 'stateReport' })">数据统计</el-button>
      <el-button type="primary" @click="$router.push({ name: 'activityReport' })">报表</el-button>
    </div>
    <el-tabs v-model="currentTabName" type="border-card" @tab-click="handleTabClick">
      <el-tab-pane v-for="(item, index) in tabList" :label="item.label" :key="index" :name="item.name">
        <div class="mt10 search">
          <el-input v-model="search" class="w300 mr20 pointer" placeholder="搜索文章标题" @keyup.enter.native="getActivityList"><div slot="append" class="pointer" @click="getActivityList">搜索</div></el-input>

          <span v-if="currentTabName === '1'">
            <el-button v-for="(item, index) in searchTimes" :key="index" type="primary" @click="handleSearchTime(index)">{{ item }}</el-button>
          </span>
        </div>
        <activity-table v-if="currentTabName< 8" :empty-text="emptyText" :table-list="tableData" :loading="showLoading" :total="total" @change="getActivityList" @update="getActivityList" @page-change="handlePageChange" @section-change="sections = $event"/>
      </el-tab-pane>

    </el-tabs>
  </star-page>
</template>

<script>
import gift from '@/views/activity/gift'
import activityTable from './tpls/activity-table'
import dayjs from 'dayjs'
export default {
  components: {
    gift,
    activityTable
  },
  data() {
    return {
      isAll: false,
      time: 0,
      searchTimes: [],
      downloadLoading: false,
      currentTabName: '1',
      tableData: [],
      pageSize: 30,
      pageNum: 1,
      status: null,
      search: null,
      showLoading: false,
      total: 0,
      sections: [],
      emptyText: ' '
    }
  },
  computed: {
    tabList() {
      return this.isAll ? [
        { label: '全部活动', name: '1', com: activityTable },
        { label: '进行中', name: '2', com: activityTable },
        { label: '已结束', name: '3', com: activityTable },
        { label: '今日开始', name: '4', com: activityTable },
        { label: '今日结束', name: '5', com: activityTable },
        { label: '未开始', name: '6', com: activityTable }
      ] : [
        { label: '全部活动', name: '1', com: activityTable },
        { label: '进行中', name: '2', com: activityTable },
        { label: '已结束', name: '3', com: activityTable },
        { label: '今日开始', name: '4', com: activityTable },
        { label: '今日结束', name: '5', com: activityTable },
        { label: '未开始', name: '6', com: activityTable },
        { label: '待审核', name: '7', com: activityTable },
        { label: '礼物记录', name: '8' },
        { label: '投票记录', name: '9' },
        { label: '举报记录', name: '10' }
      ]
    }
  },
  watch: {
    currentTabName(val) {
      switch (val) {
        case '8':
          return this.$router.push({ name: 'gift' })
        case '9':
          return this.$router.push({ path: 'voteLogs' })
        case '10':
          return this.$router.push({ name: 'reportLogs' })
      }
    },
    '$route': {
      handler(val) {
        this.isAll = val.name === 'allActivity'
        this.search = this.$route.query.search || null
        this.getActivityList()
      },
      immediate: true,
      deep: true
    }
  },
  created() {
    this.searchTimes = this.getSearchTimes(7)
  },

  methods: {
    handleSearchTime(time) {
      this.time = time
      this.getActivityList()
      location.href = location.href + '#' + time
    },
    getSearchTimes() {
      const arr = [0, 1, 2, 3, 4, 5, 6]
      const times = []
      arr.map(item => times.push(dayjs().add(item, 'day').format('YYYY-MM-DD')))

      return times
    },
    async getActivityList() {
      const params = {
        search: this.search,
        page: this.pageNum,
        size: this.pageSize,
        time: this.time
      }
      if (this.currentTabName !== '1') params.status = this.currentTabName
      this.showLoading = true
      const { error, msg, data } = await this.$api.activity[this.isAll ? 'getAllActivityList' : 'getActivityList'](params)
      this.showLoading = false
      if (error) return this.$message.error(msg)
      this.tableData = data.list
      if (this.tableData.length < 1) {
        this.emptyText = '暂无数据'
      }
      this.total = data.total
    },
    handleTabClick() {
      if (this.currentTabName === '1') {
        location.reload()
      } else {
        this.status = this.currentTabName
        this.getActivityList()
      }
    },
    handlePageChange({ pageSize, pageNum }) {
      this.pageSize = pageSize
      this.pageNum = pageNum
      this.getActivityList()
    },
    handleDownload() {
      this.downloadLoading = true
        import('@/vendor/Export2Excel').then(excel => {
          const tHeader = ['活动名称', '礼物总金额', '备注1', '备注2']
          const data = this.formatJson(this.sections)
          excel.export_json_to_excel({
            header: tHeader,
            data,
            filename: `activity-list-${Math.random()}`
          })
          this.downloadLoading = false
        })
    },
    formatJson(jsonData) {
      return jsonData.map(v => [v.activityName, v.activityAmount, v.activityRemark, v.activityRemarkSec])
    }
  }
}
</script>

<style lang="scss">
  .vote {
    .ops {
      .el-button--text{border:1px solid #409EFF; padding: 5px}
      .el-button{margin:5px 5px 0 0}
      .el-button+.el-button{margin-right:5px; margin-left: 0}
    }
    .view{
      .el-input__inner{height: 24px; line-height: 24px; padding: 0 5px; text-align: center}
      .el-button{padding:4px 5px}
      .w40{width: 40px}
    }
  }
</style>
