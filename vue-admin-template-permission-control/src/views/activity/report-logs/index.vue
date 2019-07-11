<template>
  <star-page>
    <el-table :data="reportList" border>
      <el-table-column label="ID" prop="id" align="center"/>
      <el-table-column label="IP" prop="ip" align="center"/>
      <el-table-column label="举报页面" header-align="center" >
        <template slot-scope="scope">
          <div class="flex flex-column">
            <router-link :to="{name: 'vote', params: {activityId: scope.row.activityId}}" class="front-link">{{ scope.row.activityName }}</router-link>
            <!--<router-link :to="{name: 'player', params: {activityId: scope.row.activityId, playerId: scope.row.playerId}}">{{ scope.row.playerName }}</router-link>-->
            <span>{{ scope.row.reportPage }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="共举报次数" prop="reportTimes" align="center" sortable/>
      <el-table-column label="举报内容" prop="reportContent" align="center"/>
      <el-table-column label="时间" prop="createTime" align="center" sortable/>
    </el-table>
    <div class="center">
      <el-pagination
        :current-page="pageNum"
        :page-sizes="[50,100,150]"
        :page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"/>
    </div>
  </star-page>
</template>

<script>
export default {
  name: 'Index',
  data() {
    return {
      reportList: [],
      pageSize: 50,
      pageNum: 1,
      total: 0
    }
  },
  created() {
    // if (!this.$route.query.activityId) return this.$router.go(-1)
    this.getReportList()
  },
  methods: {
    async getReportList() {
      const { error, data, msg } = await this.$api.report.getReportList({
        activityId: this.$route.query.activityId,
        pageSize: this.pageSize,
        pageNum: this.pageNum
      })
      if (error) return this.$message.error(msg)
      this.reportList = data.list
      this.total = data.total
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.getReportList()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.getReportList()
    }
  }
}
</script>

<style scoped>

</style>
