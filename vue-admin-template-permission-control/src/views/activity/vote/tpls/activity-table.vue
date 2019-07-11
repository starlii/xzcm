<template>
  <section class="activity">
    <div class="mt10 mb10">
      <el-input v-model="search" class="w300 mr20 pointer" placeholder="搜索文章标题" @keyup.enter.native="getActivityList">
        <el-button slot="append" type="primary" @click="getActivityList">搜索</el-button>
      </el-input>
      <span v-if="!status">
        <el-button v-for="(item, index) in searchTimes" :key="index" :type="time === index +1 ? 'primary' : 'info'" @click="handleSearchTime(index + 1)">{{ item }}</el-button>
      </span>
    </div>
    <div/>
    <el-table
      v-loading="showLoading"
      ref="table"
      :data="tableData"
      :empty-text="emptyText"
      border
      stripe
      class="mt10"
      @sort-change="handleSortChange"
      @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="40" align="center"/>
      <el-table-column label="图片" align="center" min-width="120">
        <template slot-scope="scope">
          <img v-lazy="scope.row.activityImage" :key="scope.row.activityImage" class="activity-img">
        </template>
      </el-table-column>
      <el-table-column prop="activityName" label="活动名称" header-align="center" width="180">
        <template slot-scope="scope">
          <router-link :to="{path: '/activity/player', query: {activityId: scope.row.activityId, name: scope.row.activityName, endTime: scope.row.activityEndTime}}" tag="a" target="_blank">
            <span class="front-link">{{ scope.row.activityName }}</span>
          </router-link>
        </template>
      </el-table-column>
      <el-table-column label="投票时间" prop="activityStartTime" sortable width="175" align="center">
        <template slot-scope="scope">
          <span v-if="scope.row.activityStartTime" class="mb5 time-tag">{{ scope.row.activityStartTime | formatDate('yyyy-MM-dd hh:mm:ss') }}</span><br>
          <span v-if="scope.row.activityEndTime" class="time-tag">{{ scope.row.activityEndTime | formatDate('yyyy-MM-dd hh:mm:ss') }}</span><br>
          <time-down :start-time="scope.row.activityStartTime" :end-time="scope.row.activityEndTime"/>
          <!--<span class="time-tag">{{ dayjs().format('YYYY-MM-DD') }}</span>-->
          <!--<el-button type="success">倒计时: TODO </el-button>-->
        </template>
      </el-table-column>
      <el-table-column label="选手数量" sortable prop="activityPlayers" width="100" align="center">
        <template slot-scope="scope">
          <el-tag type="success" size="medium">{{ scope.row.activityPlayers || 0 }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="金额" min-width="80" prop="activityAmount" sortable align="center">
        <template slot-scope="scope">
          <el-tag type="danger" size="medium">￥ {{ scope.row.activityAmount || 0 }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="浏览量" min-width="130" header-align="center">
        <template slot-scope="scope">
          <el-tag type="success" class="mb10">{{ `${scope.row.actVotes + scope.row.mualVotes || 0}/总：${scope.row.activityViews || 0}` }}</el-tag>
          <br>
          <span v-permission><add-num @change="handleAddView(scope.row.activityId,$event)"/></span>
        </template>
      </el-table-column>
      <el-table-column prop="activityPlayers" label="分享量" align="center" sortable min-width="120">
        <template slot-scope="scope">
          <el-tag type="success" size="medium">{{ `${scope.row.actVotes || 0}/总：${scope.row.activityShares || 0}` }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="链接" width="100px" align="center">
        <template slot-scope="scope">
          <span class="front-link" @click="handleShow(scope.row.activityName, scope.row.activityId)">查看活动链接</span>
        </template>
      </el-table-column>
      <el-table-column prop="activityStatus" label="状态" align="center" sortable>
        <template slot-scope="scope">
          {{ scope.row.statusName }}
        </template>
      </el-table-column>
      <el-table-column prop="activityRemark" label="备注">
        <template slot-scope="scope">
          <add-remark :remark="scope.row.activityRemark" :activity-name="scope.row.activityName" :uid="scope.row.activityId" @success="getActivityList"/>
        </template>
      </el-table-column>
      <el-table-column prop="activityRemarkSec" label="备注2">
        <template slot-scope="scope">
          <add-remark-sec :remark="scope.row.activityRemarkSec" :activity-name="scope.row.activityName" :uid="scope.row.activityId" @success="getActivityList"/>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="管理操作" min-width="150" align="left">
        <template slot-scope="scope">
          <div class="ops">
            <el-button type="text" @click="$router.push({path: '/activity/hostSet',query: {activityIid: scope.row.activityId}})"> 域名</el-button>
            <el-button type="text" @click="$router.push({path: '/activity/reportLogs', query: {activityId: scope.row.activityId}})">举报</el-button>
            <el-button type="text" @click="$router.push({path: '/activity/gift', query: {activityId: scope.row.activityId}})">礼物</el-button>
            <el-button type="text" @click="$router.push({path: '/activity/voteLogs', query: {activityId: scope.row.activityId}})">投票</el-button>
            <el-button type="text" @click="$router.push({path: '/activity/addActivity', query: {activityId: scope.row.activityId, flag: 'add'}})">复制添加</el-button>
            <el-button type="text"><router-link :to="{name:'addActivity',query: {activityId: scope.row.activityId, flag: 'edit'}}" tag="a" target="_blank">修改</router-link></el-button>
            <el-button :type="scope.row.activityStatus !== 0 ? 'primary' : 'text'" @click="updateStatus(scope.row.activityId, scope.row.activityStatus)">{{ scope.row.activityStatus !== 0 ? '关闭活动' : '开启活动' }}</el-button>
            <el-button type="text" @click="confrimDeleteActivity(scope.row.activityId)">删除</el-button>
          </div>
        </template>
      </el-table-column>
      <el-dialog :title="`活动“${dialogTitle}”的访问连接 `" :visible.sync="show" append-to-body @open="onUrlShow">
        <url-dialog ref="urlDialog" :uid="uid" type="activity"/>
      </el-dialog>
    </el-table>
    <el-pagination
      :current-page="pageNum"
      :page-sizes="[50,100, 150]"
      :page-size="pageSize"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"/>
  </section>
</template>
<script>
import addNum from '../../components/add-num'
import urlDialog from '../../components/url-dialog'
import timeDown from '../../components/time-down'
import addRemark from './add-remark'
import addRemarkSec from './add-remark-sec'
import { mapGetters } from 'vuex'
import dayjs from 'dayjs'

export default {
  name: 'ActivityTable',
  components: { addNum, urlDialog, timeDown, addRemark, addRemarkSec },
  data() {
    return {
      sort: 'activityStartTime desc, createTime desc',
      tableData: [],
      total: 0,
      emptyText: '加载中...',
      show: false,
      uid: null,
      dialogTitle: null,
      search: null,
      pageSize: 50,
      pageNum: 1,
      status: null,
      time: null,
      screenHeight: window.innerHeight - 250,
      searchTimes: [],
      showLoading: false,
      section: []
    }
  },
  computed: {
    ...mapGetters([
      'roles'
    ])
  },
  watch: {
    '$route': {
      handler(val) {
        this.status = val.query.status
        this.time = val.query.time || null
        this.search = this.$route.query.search || null
        if (val.name === 'allActivityList') {
          this.isAll = true
          this.$parent.$parent.isAll = true
        } else {
          this.isAll = false
          this.$parent.$parent.isAll = false
        }
        if (this.roles[0] === 'admin') this.isAll = true
        this.isAll = val.name === 'allActivityList'
        if (!this.status) this.searchTimes = this.getSearchTimes(7)
        this.getActivityList()
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    handleSortChange(params) {
      const { prop, order } = params
      if (prop === 'activityStartTime') {
        if (order === 'descending') {
          this.sort = 'activityStartTime desc, createTime desc'
        }
        this.sort = 'activityStartTime asc, createTime desc'
      } else {
        if (order === 'descending') {
          this.sort = `${prop} desc,activityStartTime desc, createTime desc`
        } else {
          this.sort = `${prop} asc,activityStartTime desc, createTime desc`
        }
      }
      this.getActivityList()
    },
    async updateStatus(activityId, activityStatus) {
      // if (activityStatus === 3) return this.$notify.warning('活动已经结束！')
      activityStatus = activityStatus ? 0 : 1
      console.log('update status', activityId, activityStatus)
      const { error, msg } = await this.$api.activity.update({ activityId, activityStatus })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.getActivityList()
    },
    handleShow(name, uid) {
      this.show = !this.show
      this.dialogTitle = name
      this.uid = uid
    },
    onUrlShow() {
      this.$nextTick(() => {
        this.$refs.urlDialog.loadLink()
      })
    },
    async handleAddView(activityId, views) {
      const { error, msg } = await this.$api.activity.updateViews({
        activityId,
        views
      })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      const index = this.tableData.findIndex(item => item.activityId === activityId)
      this.$set(this.tableData[index], 'activityViews', Number(this.tableData[index].activityViews) + Number(views))
      // this.getActivityList()
    },
    confrimDeleteActivity(activityId) {
      this.$confirm('此操作将永久删除, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteActivity(activityId)
        this.$message({
          type: 'success',
          message: '删除成功!'
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    async deleteActivity(activityId) {
      const { error, msg } = await this.$api.activity.deleteActivity({ activityId })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.getActivityList()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.getActivityList()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.getActivityList()
    },
    handleSelectionChange(section) {
      this.$parent.$parent.sections = section
    },
    async getActivityList() {
      const params = {
        search: this.search,
        page: this.pageNum,
        size: this.pageSize,
        time: this.time,
        sort: this.sort
      }
      if (this.status) params.status = this.status
      this.showLoading = true
      const { error, msg, data } = await this.$api.activity[this.isAll ? 'getAllActivityList' : 'getActivityList'](params)
      this.showLoading = false
      if (error) return this.$message.error(msg)
      this.tableData = data.list
      if (this.tableData.length < 1) {
        this.emptyText = '暂无数据'
      }
      this.total = data.total
      this.$parent.$parent.activityNum = data.total
      this.$parent.$parent.activityIndex = {
        index: this.$route.query.status,
        isAll: this.isAll
      }
    },
    getSearchTimes() {
      const arr = [1, 2, 3, 4, 5, 6, 7]
      const times = []
      arr.map(item => times.push(dayjs().add(item, 'day').format('YYYY-MM-DD')))

      return times
    },
    handleSearchTime(val) {
      this.$router.push({ path: this.$route.path, query: { status: this.status, time: val }})
    }
  }
}
</script>

<style lang="scss">
  .time-tag {
    background-color: #409EFF;
    padding: 0px 10px;
    display: inline-block;
    border-radius: 5px;
    color: #fff;
  }

  .activity {
    .activity-img {
      width: 80px;
      height: 50px;
    }
    .ops {
      .el-button--text {
        border: 1px solid #409EFF;
        padding: 5px;
        margin-right: 5px;
      }
      .el-button + .el-button {
        margin: 5px 5px 0 0
      }
    }
    .el-input-group__append button.el-button {
      color: #fff;
      background-color: #409EFF;
      border-color: #409EFF;
      border-bottom: 1px solid #409EFF;
    }
    .el-input-group__append button.el-button:hover, .el-input-group__append button.el-button:focus {
      background: #66b1ff;
      border-color: #66b1ff;
    }
  }
</style>
