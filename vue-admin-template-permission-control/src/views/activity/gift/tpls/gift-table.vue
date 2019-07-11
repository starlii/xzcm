<template>
  <section>

    <el-table v-loading="loading" :data="tableList" class="mt10" border>
      <el-table-column label="投票活动" min-width="280">
        <template slot-scope="scope">
          <router-link :to="{path: 'player', query: {activityId: scope.row.activityId}}" class="front-link">{{ scope.row.activityName }}</router-link>
        </template>
      </el-table-column>
      <el-table-column label="选手" prop="playerId">
        <template slot-scope="scope">
          <router-link :to="{path: 'player', query: {activityId: scope.row.activityId, keyword: scope.row.playerName}}" class="front-link">{{ scope.row.playerName }}</router-link>
        </template>
      </el-table-column>
      <el-table-column label="时间" prop="createTime" sortable>
        <template slot-scope="scope">{{ scope.row.createTime | formatDate('yyyy-MM-dd hh:mm:ss') }}</template>
      </el-table-column>
      <el-table-column label="IP" prop="ip"/>
      <!--<el-table-column label="礼物" prop="gift"/>-->
      <el-table-column label="金额" prop="giftAmount" sortable>
        <template slot-scope="scope">
          <el-tag type="danger">￥{{ scope.row.giftAmount || 0 }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" sortable>
        <template slot-scope="scope">
          <div v-if="scope.row.status==0">
            <el-button type="success">已支付加{{ scope.row.giftAmount * 3 }}票</el-button>
          </div>
          <div v-else>
            <el-button type="danger">未付款</el-button>
          </div>
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
  </section>
</template>

<script>
export default {
  name: 'GiftTable',
  props: {
    tableData: { type: Array, default: () => [] },
    loading: { type: Boolean, default: false },
    activityId: { type: Number, default: null }
  },
  data() {
    return {
      tableList: [],
      pageSize: 50,
      pageNum: 1,
      total: 0
    }
  },
  watch: {
    tableData: {
      handler(val) {
        this.total = val.length
        this.filterDataList(Object.assign([], val))
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    filterDataList(list, pageSize = this.pageSize, pageNum = this.pageNum) {
      const arr = list.splice((pageNum - 1) * pageSize, pageSize)
      this.tableList = arr
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.filterDataList(Object.assign([], this.tableData))
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.filterDataList(Object.assign([], this.tableData))
    }
  }
}
</script>

<style scoped>

</style>
