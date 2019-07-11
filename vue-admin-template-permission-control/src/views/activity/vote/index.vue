<template>
  <star-page class="vote">
    <div v-if="!isAll || roles[0] !== 'system'" class="mb10">
      <el-button :disabled="!sections.length" :loading="downloadLoading" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
      <el-button type="primary" @click="$router.push({ name: 'addActivity' })">添加活动</el-button>
      <span v-permission2>
        <el-button type="primary" @click="$router.push({name: 'allActivityList'})">查看所有活动</el-button>
      </span>
      <el-button type="primary" @click="$router.push({ name: 'stateReport' })">数据统计</el-button>
      <el-button type="primary" @click="$router.push({ name: 'activityReport' })">报表</el-button>
    </div>
    <star-tab :tab-list="tabList" :active="currentCom" @change="handleItem"/>
    <keep-alive>
      <router-view />
    </keep-alive>
  </star-page>
</template>

<script>
import starTab from '@/components/star-tab'
import { mapGetters } from 'vuex'
export default {
  components: {
    starTab
  },
  data() {
    return {
      search: null,
      isAll: false,
      downloadLoading: false,
      activityNum: null,
      activityIndex: {
        index: null,
        isAll: null
      },
      currentCom: 0,
      tabList: [
        { value: 0, label: '全部活动', com: 'activityList', status: null, num: 0 },
        { value: 1, label: '进行中', com: 'activityList', status: 2, num: null },
        { value: 2, label: '已结束', com: 'activityList', status: 3, num: null },
        { value: 3, label: '今日开始', com: 'activityList', status: 4, num: null },
        { value: 4, label: '今日结束', com: 'activityList', status: 5, num: null },
        { value: 5, label: '未开始', com: 'activityList', status: 6, num: null },
        { value: 6, label: '待审核', com: 'pending' },
        { value: 7, label: '礼物记录', com: 'gift' },
        { value: 8, label: '投票记录', com: 'voteLogs' },
        { value: 9, label: '举报记录', com: 'reportLogs' }
      ],
      sections: [],
      pendingNum: 0
    }
  },
  computed: {
    ...mapGetters([
      'roles'
    ])
  },
  watch: {
    isAll(val) {
      if (val) {
        [0, 1, 2, 3, 4, 5].forEach(item => {
          this.tabList.splice(item, 1, {
            value: this.tabList[item].value,
            label: this.tabList[item].label,
            com: 'allActivityList',
            status: this.tabList[item].status
          })
        })
        if (this.roles[0] === 'system') this.tabList.splice(6, 3)
      } else {
        [0, 1, 2, 3, 4, 5].forEach(item => {
          this.tabList.splice(item, 1, {
            value: this.tabList[item].value,
            label: this.tabList[item].label,
            com: 'activityList',
            status: this.tabList[item].status
          })
        })
        this.tabList.splice(6, 1, { value: 6, label: `待审核(${this.pendingNum})`, com: 'pending' })
        this.tabList.splice(7, 1, { value: 7, label: '礼物记录', com: 'gift' })
        this.tabList.splice(8, 1, { value: 8, label: '投票记录', com: 'voteLogs' })
        this.tabList.splice(9, 1, { value: 9, label: '举报记录', com: 'reportLogs' })
      }
    },
    '$route': {
      handler(val) {
        this.search = val.query.search
        if (val.name === 'activityList' || val.name === 'allActivityList') {
          if (val.query.status) this.currentCom = val.query.status - 1
          else this.currentCom = 0
        }
        if (val.name === 'vote') {
          if (this.isAll) this.$router.push({ name: 'allActivityList' })
          else this.$router.push({ name: 'activityList' })
        }
        if (val.name === 'pending') this.currentCom = 6
      },
      immediate: true,
      deep: true
    },
    activityIndex: {
      handler(val) {
        const arr = [0, 1, 2, 3, 4, 5]
        arr.map(i => {
          this.$set(this.tabList[i], 'num', 0)
        })

        if (!val.index) {
          this.$set(this.tabList[0], 'num', this.activityNum + '')
        } else {
          this.$set(this.tabList[val.index - 1], 'num', this.activityNum + '')
        }
      },
      immediate: true,
      deep: true
    }
  },

  created() {
    this.getPending()
    this.roles[0] === 'admin' ? this.$router.push('/activity/vote/allActivityList') : this.$router.push('/activity/vote/activityList')
  },
  methods: {
    handleItem(item) {
      this.currentCom = item.value
      if (item.com === 'activityList' || item.com === 'allActivityList') return this.$router.push({ path: '/activity/vote/' + item.com, query: { status: item.status }})
      else return this.$router.push({ name: item.com })
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
    },
    async getPending() {
      const { error, data, msg } = await this.$api.player.getPending({
        'page': 1,
        'size': 30
      })
      if (error) return this.$message.error(msg)
      this.pendingNum = data.total
      if (this.tabList[6].com === 'pending') {
        this.tabList.splice(6, 1, { value: 6, label: `待审核(${data.total})`, com: 'pending' })
      }
    }
  }
}
</script>

<style lang="scss">

</style>
