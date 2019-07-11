<template>
  <star-page>
    <el-table :loading="loading" :data="tableList" :empty-text="emptyText" border>
      <el-table-column label="投票活动">
        <template slot-scope="scope">
          <router-link :to="{path: 'player', query: {activityId: scope.row.activityId}}" class="front-link">{{ scope.row.activityName }}</router-link>
        </template>
      </el-table-column>
      <el-table-column label="选手">
        <template slot-scope="scope">
          <router-link :to="{path: 'player', query: {activityId: scope.row.activityId,keyword: scope.row.playerName}}" class="front-link">{{ scope.row.playerName }}</router-link>
        </template>
      </el-table-column>
      <el-table-column label="微信 OPENID" prop="openId"/>
      <el-table-column label="IP" prop="ip"/>
      <el-table-column label="时间" prop="createTime" sortable>
        <template slot-scope="scope">
          {{ scope.row.createTime | formatDate('yyyy-MM-dd hh:mm:ss') }}
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      :current-page="pageNum"
      :page-sizes="[50, 100, 150, 200]"
      :page-size="pageSize"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"/>
  </star-page>
</template>

<script>
export default {
  name: 'Index',
  data() {
    return {
      voteLogList: [],
      tableList: [],
      activityId: null,
      pageNum: 1,
      pageSize: 50,
      total: 0,
      loading: false,
      emptyText: '加载中。。。'
    }
  },
  created() {
    this.activityId = this.$route.query.activityId
    this.getVoteLogList()
  },
  methods: {
    async getVoteLogList() {
      this.loading = true
      const { error, data, msg } = await this.$api.activity.getVoteLog(this.activityId)
      this.emptyText = '暂无数据'
      this.loading = false
      if (error) return this.$message.error(msg)
      this.voteLogList = data
      this.total = data.length
      this.filterDataList(Object.assign([], this.voteLogList))
    },
    filterDataList(list, pageSize = this.pageSize, pageNum = this.pageNum) {
      const arr = list.splice((pageNum - 1) * pageSize, pageSize)
      this.tableList = arr
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.filterDataList(Object.assign([], this.voteLogList))
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.filterDataList(Object.assign([], this.voteLogList))
    }
  }
}
</script>

<style scoped>

</style>
