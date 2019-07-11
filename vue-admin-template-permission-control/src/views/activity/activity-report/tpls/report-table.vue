<template>
  <section>
    <div class="mb10">
      <el-button v-for="item in timeList" :key="item.value" @click="handleTime(item.value)">{{ item.label }}</el-button>
      <span class="info ml20">日期:</span>
      <el-date-picker v-model="time" value-format="yyyy-MM-dd HH:mm:ss" type="daterange" range-separator="到" start-placeholder="开始日期" end-placeholder="结束日期" @change="handleDateChange"/>
      <el-input v-model="searchForm.username" class="w120 ml10" placeholder="用户名" @keyup.enter.native="getList"/>
      <el-button class="ml10" type="primary" @click="getList">查找</el-button>
    </div>
    <el-table :data="settlementObj.list" border>
      <el-table-column label="ID" align="center" prop="id" width="80"/>
      <el-table-column label="用户名" align="center" prop="username"/>
      <el-table-column label="日期" sortable align="center" prop="createTime"/>
      <el-table-column label="活动数" sortable align="center" prop="activitys"/>
      <el-table-column label="总金额" sortable align="center" prop="amount"/>
      <el-table-column label="代理系数" align="center" prop="agentValue"/>
      <el-table-column label="代理金额" sortable align="center" prop="agentAmount"/>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-button :type="scope.row.status ? 'danger' : 'success'" @click="updateStatus(scope.row.id)">{{ scope.row.status ? '未支付' : '已支付' }}</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script>
export default {
  name: 'ReportTable',
  data() {
    return {
      timeList: [
        { label: '全部', value: 0 },
        { label: '昨天', value: 1 },
        { label: '本周', value: 2 },
        { label: '上周', value: 3 },
        { label: '本月', value: 4 },
        { label: '上月', value: 5 }
      ],
      time: [],
      settlementObj: {
        total: 0,
        list: []
      },
      searchForm: {
        'endTime': null,
        'page': 1,
        'size': 50,
        'startTime': null,
        'timeFlag': 0,
        'username': null
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    handleTime(val) {
      this.$set(this.searchForm, 'timeFlag', val)
      this.time = []
      this.$set(this.searchForm, 'startTime', null)
      this.$set(this.searchForm, 'endTime', null)
      this.getList()
    },
    handleDateChange(val) {
      this.$set(this.searchForm, 'timeFlag', null)
      this.$set(this.searchForm, 'startTime', val[0])
      this.$set(this.searchForm, 'endTime', val[1])
      this.getList()
    },
    async getList() {
      const params = {}
      Object.keys(this.searchForm).map(item => {
        if (item === 'timeFlag' && this.searchForm.timeFlag) params.timeFlag = this.searchForm.timeFlag - 1
        else if (this.searchForm[item]) params[item] = this.searchForm[item]
      })
      const { error, data, msg } = await this.$api.report.settlement(params)
      if (error) return this.$message.error(msg)
      this.settlementObj = data
    },
    async updateStatus(id) {
      const { error, msg } = await this.$api.report.updateStatus({ id })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.getList()
    }
  }
}
</script>

<style scoped>

</style>
