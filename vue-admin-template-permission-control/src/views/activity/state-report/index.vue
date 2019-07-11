<template>
  <star-page>
    <div class="opt-content">
      <div>
        <el-button type="primary" @click="$router.push({name: 'vote'})">活动列表</el-button>
        <span v-permission2>
          <el-button type="primary" @click="handleAll">{{ self ? '全部活动' : '查看当前' }}</el-button>
        </span>
      </div>
      <span>
        <span class="info">自动刷新：</span>
        <el-switch
          v-model="autoRefresh"
          name="switch"
          active-color="#13ce66"
          inactive-color="#ff4949"
          active-text="开启"
          inactive-text="关闭"/>
      </span>
    </div>
    <div v-loading="loading" class="flex" style="justify-content: space-around">
      <div class="report-item history">
        <svg-icon icon-class="history" style="width: 65px;height: 75px;"/>
        <span><span class="item-label">今日交易:</span>{{ detail.todayDeals|| 0 }}</span>
        <div class="flex f-center">
          <span class="mr5"><span class="item-label">总交易:</span> {{ detail.totalDeals|| 0 }}</span>
          <span><span class="item-label">昨日交易:</span>{{ detail.yesterdayDeals|| 0 }}</span>
        </div>
      </div>
      <div class="report-item bars">
        <svg-icon icon-class="bars" style="width: 65px;height: 75px;"/>
        <span><span class="item-label">进行中:</span>{{ detail.processings || 0 }}</span>
        <div class="flex f-center">
          <span class="mr5"><span class="item-label">今日开始:</span> {{ detail.todayStarts || 0 }}</span>
          <span><span class="item-label">今日结束:</span>{{ detail.todayEnds || 0 }}</span>
        </div>
      </div>
      <div class="report-item briefcase">
        <svg-icon icon-class="briefcase" style="width: 65px;height: 75px;"/>
        <span><span class="item-label">投票活动:</span>{{ detail.activitys || 0 }}</span>
        <div class="flex f-center">
          <span class="mr5"><span class="item-label">未开始:</span> {{ detail.notStarts || 0 }}</span>
          <span><span class="item-label">已结束:</span>{{ detail.overs || 0 }}</span>
        </div>
      </div>
    </div>
    <div v-loading="loading">
      <div class="report-table-content gift">
        <div class="header-bar gift">最新礼物</div>
        <div class="report-table-body">
          <el-table :data="detail.giftLogs" border style="width: 100%">
            <el-table-column label="投票活动" prop="activityName">
              <template slot-scope="scope">
                <router-link :to="{path: 'player', query: {activityId: scope.row.activityId}}" class="front-link">{{ scope.row.activityName }}</router-link>
              </template>
            </el-table-column>
            <el-table-column label="选手" prop="playerName">
              <template slot-scope="scope">
                <router-link :to="{path: 'player', query: {activityId: scope.row.activityId, keyword: scope.row.playerName}}" class="front-link">{{ scope.row.playerName }}</router-link>
              </template>
            </el-table-column>
            <el-table-column label="时间" prop="createTime" sortable>
              <template slot-scope="scope">
                {{ scope.row.createTime }}
              </template>
            </el-table-column>
            <el-table-column label="IP" prop="ip"/>
            <el-table-column label="票数" prop="giftAmount" sortable>
              <template slot-scope="scope">
                {{ scope.row.giftAmount * 3 }}
              </template>
            </el-table-column>
            <!--<el-table-column label="礼物" prop="gift"/>-->
            <el-table-column label="金额" prop="giftAmount" sortable>
              <template slot-scope="scope">
                <el-tag type="danger">￥{{ scope.row.giftAmount || 0 }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <div class="report-table-content vote">
        <div class="header-bar vote">最新投票</div>
        <div class="report-table-body">
          <el-table :data="detail.voteLogs" border>
            <el-table-column label="投票活动" prop="activityName">
              <template slot-scope="scope">
                <router-link :to="{path: 'player', query: {activityId: scope.row.activityId}}" class="front-link">{{ scope.row.activityName }}</router-link>
              </template>
            </el-table-column>
            <el-table-column label="选手" prop="playerName">
              <template slot-scope="scope">
                <router-link :to="{path: 'player', query: {activityId: scope.row.activityId, keyword: scope.row.playerName}}" class="front-link">{{ scope.row.playerName }}</router-link>
              </template>
            </el-table-column>
            <el-table-column label="时间" prop="createTime" sortable>
              <template slot-scope="scope">
                {{ scope.row.createTime }}
              </template>
            </el-table-column>
            <el-table-column label="IP" prop="ip"/>
            <el-table-column label="票数">
              <template slot-scope="scope">
                1
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </star-page>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  name: 'Index',
  data() {
    return {
      detail: {
        'todayDeals': 0,
        'yesterdayDeals': 0,
        'totalDeals': 0,
        'processings': 0,
        'todayStarts': 0,
        'todayEnds': 0,
        'activitys': 0,
        'notStarts': 0,
        'overs': 0,
        'giftLogs': [],
        'voteLogs': []
      },
      autoRefresh: false,
      self: 1,
      loading: false,
      timer: null
    }
  },
  computed: {
    ...mapGetters([
      'roles'
    ])
  },
  watch: {
    autoRefresh(val) {
      if (val) {
        this.timer = setInterval(() => {
          this.getStatistics()
        }, 5000)
      } else {
        clearInterval(this.timer)
        this.timer = null
      }
    }
  },
  created() {
    if (this.roles[0] === 'admin') this.self = 0
    this.getStatistics()
  },
  methods: {
    async getStatistics() {
      this.loading = true
      const { error, data, msg } = await this.$api.report.statistics(this.self)
      this.loading = false
      if (error) return this.$message.error(msg)
      this.detail = data
    },
    handleAll() {
      this.self = this.self ? 0 : 1
      this.getStatistics()
    }
  }
}
</script>

<style lang="scss" scoped>
  .opt-content {
    border-bottom: 1px solid #ccc;
    margin-bottom: 15px;
    padding-bottom: 10px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .report-item {
    min-width: 200px;
    height: 150px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 20px 10px;
    margin-bottom: 20px;
    &.history{
      background-color: #d9edf7;
      border-color:#bce8f1;
      color: #31708f;
    }
    &.bars{
      color: #3c763d;
      background-color: #dff0d8;
      border-color: #d6e9c6;
    }
    &.briefcase{
      color: #8a6d3b;
      background-color: #fcf8e3;
      border-color: #faebcc;
    }
    &>span{margin: 10px 0}
    .item-label {
      font-weight: bolder;
    }
  }
  .report-table-content{
    width: 49%;
    border-radius: 4px;
    border: 1px solid transparent;
    &.gift {
      border-color: #ed5565;
      float: left;
    }
    &.vote {
      border-color: #1c84c6;
      float: right;
    }
    .header-bar {
      color: #fff;
      border: 1px solid transparent;
      padding: 10px 15px;
      border-bottom: 1px solid transparent;
      box-sizing: border-box;
      &.gift {
        background-color: #ed5565;
        border-color: #ed5565;
      }
      &.vote {
        background-color: #1c84c6;
        border-color: #1c84c6;
      }
    }
    .report-table-body {
      padding: 15px;
    }
  }
</style>
