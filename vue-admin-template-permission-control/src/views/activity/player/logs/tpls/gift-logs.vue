<template>
  <section>
    <el-table ref="table" :data="giftLogList" border @sort-change="handleSortChange">
      <el-table-column label="ID" prop="id" width="80"/>
      <el-table-column label="微信OPENID" prop="openId"/>
      <!--<el-table-column label="礼物" prop="giftAmount"/>-->
      <el-table-column label="礼物价格" sortable="custom" prop="giftAmount"/>
      <el-table-column label="订单号" prop="orderId"/>
      <el-table-column label="商户订单号" prop="businessId"/>
      <el-table-column label="状态" sortable="custom" prop="status">
        <template slot-scope="scope">
          <el-button :type="scope.row.status ? 'warning' : 'success'">{{ scope.row.status ? '未支付' : '已支付' }}</el-button>
        </template>
      </el-table-column>
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
  name: 'GiftLogs',
  props: { id: { type: [Number, String], default: 0 }},
  data() {
    return {
      giftLogList: [],
      pageNum: 1,
      pageSize: 50,
      total: 0,
      sort: 'id desc'
    }
  },
  created() {
    this.getGiftLogs()
    // this.$nextTick(() => {
    //   this.$refs.table.sort('createTime', 'descending')
    // })
  },
  methods: {
    handleSizeChange(val) {
      this.pageSize = val
      this.getGiftLogs()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.getGiftLogs()
    },
    async getGiftLogs() {
      const { error, data, msg } = await this.$api.player.getGiftLog({ playerId: this.id, pageSize: this.pageSize, pageNum: this.pageNum, sort: this.sort })
      if (error) return this.$message.error(msg)
      this.giftLogList = data.list
      this.total = data.total
    },
    handleSortChange(params) {
      const { prop, order } = params
      if (prop === 'createTime') {
        this.sort = order === 'descending' ? 'id desc' : 'id asc'
      } else {
        this.sort = order === 'descending' ? `${prop} desc` : `${prop} asc`
      }

      this.getGiftLogs()
    }
  }
}
</script>

<style scoped>

</style>
