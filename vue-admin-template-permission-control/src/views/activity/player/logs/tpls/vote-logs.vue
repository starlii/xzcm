<template>
  <section>
    <el-table ref="table" :data="voteLogList" border @sort-change="handleSortChange">
      <el-table-column label="ID" prop="id" width="80"/>
      <el-table-column label="微信OPENID" prop="openId"/>
      <el-table-column label="备注" prop="remark"/>
      <el-table-column label="ip" prop="ip"/>
      <el-table-column label="时间" sortable="custom" prop="createTime">
        <template slot-scope="scope">
          {{ scope.row.createTime | formatDate('yyyy-MM-dd hh:mm:ss') }}
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      :current-page="pageNum"
      :page-sizes="[50, 100, 150]"
      :page-size="pageSize"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"/>
  </section>
</template>

<script>
export default {
  name: 'VoteLogs',
  props: { id: { type: [Number, String], default: 0 }},
  data() {
    return {
      voteLogList: [],
      pageNum: 1,
      pageSize: 50,
      total: 0,
      sort: 'id desc'
    }
  },
  created() {
    this.getVotLogs()
    // this.$nextTick(() => {
    //   this.$refs.table.sort('createTime', 'descending')
    // })
  },
  methods: {
    handleSizeChange(val) {
      this.pageSize = val
      this.getVotLogs()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.getVotLogs()
    },
    async getVotLogs() {
      const { error, msg, data } = await this.$api.player.getVoteLog({ palyerId: this.id, pageSize: this.pageSize, pageNum: this.pageNum, sort: this.sort })
      if (error) return this.$message.error(msg)
      this.voteLogList = data.list
      this.total = data.total
    },
    handleSortChange(params) {
      const { prop, order } = params
      if (prop === 'createTime') {
        this.sort = order === 'descending' ? 'id desc' : 'id asc'
      } else {
        this.sort = order === 'descending' ? `${prop} desc` : `${prop} asc`
      }

      this.getVotLogs()
    }
  }
}
</script>

<style scoped>

</style>
