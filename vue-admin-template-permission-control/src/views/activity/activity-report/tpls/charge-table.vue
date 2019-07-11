<template>
  <section>
    <div class="mb10">
      <el-button v-for="item in timeList" :key="item.value" @click="handleTime(item.value)">{{ item.label }}</el-button>
      <span class="info ml20">日期:</span>
      <el-date-picker v-model="time" value-format="yyyy-MM-dd HH:mm:ss" type="daterange" range-separator="到" start-placeholder="开始日期" end-placeholder="结束日期" @change="handleDateChange"/>
      <el-input v-model="searchForm.username" class="w120 ml10" placeholder="用户名" @keyup.enter.native="getCharge"/>
      <el-button class="ml10" type="primary" @click="getCharge">查找</el-button>
    </div>
    <el-table :data="chargeObj.list" border>
      <el-table-column label="ID" prop="id" align="center" width="80"/>
      <el-table-column label="用户名" prop="username" align="center"/>
      <el-table-column label="日期" sortable prop="createTime" align="center"/>
      <el-table-column label="笔数" sortable prop="transactions" align="center"/>
      <el-table-column label="金额" sortable prop="amount" align="center"/>
    </el-table>
  </section>
</template>

<script>
export default {
  name: 'ChargeTable',
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
      chargeObj: {
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
    this.getCharge()
  },
  methods: {
    handleTime(val) {
      this.$set(this.searchForm, 'timeFlag', val)
      this.$set(this.searchForm, 'startTime', null)
      this.$set(this.searchForm, 'endTime', null)
      this.getCharge()
    },
    handleDateChange(val) {
      this.$set(this.searchForm, 'timeFlag', null)
      this.$set(this.searchForm, 'startTime', val[0])
      this.$set(this.searchForm, 'endTime', val[1])
      this.getCharge()
    },
    async getCharge() {
      const params = {}
      Object.keys(this.searchForm).map(item => {
        if (item === 'timeFlag' && this.searchForm.timeFlag) params.timeFlag = this.searchForm.timeFlag - 1
        else if (this.searchForm[item]) params[item] = this.searchForm[item]
      })
      const { error, data, msg } = await this.$api.report.getChargeList(params)
      if (error) return this.$message.error(msg)
      this.chargeObj = data
    }
  }
}
</script>

<style scoped>

</style>
