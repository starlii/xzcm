<template>
  <star-page>
    <div class="mb10 flex f-between f-a-center">
      <div>
        <span>活动名称:</span>
        <el-button type="primary" class="ml10" @click="$router.push({name: 'vote'})">{{ name }}</el-button>
        <div class="mt10">
          <span>活动结束时间:</span>
          <el-button type="primary" class="ml10">{{ endTime | formatDate('yyyy-MM-dd') }}</el-button>
          <span>剩余时间:</span>
          <el-button type="primary" class="ml10"><span v-if="overStatus==true || maulStatus==true"> 0天0小时0分0秒</span><span v-else>{{ endDate | formatDuring }}</span>
          </el-button>
        </div>
      </div>
      <div>
        <el-button type="primary" @click="$router.push({name: 'addPlayer', query: {activityId}})">添加选手</el-button>
        <el-button v-loading="downloadLoading" type="primary" @click="handleDownload">数据导出</el-button>
        <el-button :disabled="!selectList.length" type="primary" @click="handleBatchDelete">批量删除</el-button>
        <!--TODO: 三级权限 -->
        <span v-permission>
          <el-button type="primary" @click="oneClickAdded">一键当页随机加票</el-button>
        </span>
        <el-button type="primary" @click="dialogShow = !dialogShow">批量导入</el-button>
        <span
          class="front-link"
          style="font-size: 12px;position: relative;top: 5px;"
          @click="handleDownLoad">下载模板</span>
      </div>
    </div>
    <el-tabs v-model="currentTabName" type="border-card" @tab-click="getPlayerList">
      <el-tab-pane v-for="(item, index) in tabList" :label="item.label" :key="index" :name="item.name">
        <div class="search">
          <div class="w300">
            <el-input
              v-model.trim="keyword"
              class="pointer"
              placeholder="搜索选手姓名/编号"
              @keyup.enter.native="getPlayerList">
              <el-button slot="append" type="primary" @click="getPlayerList">搜索</el-button>
            </el-input>
          </div>
        </div>
        <player-list
          :loading="tableLoading"
          :table-data="tableData"
          :empty-text="emptyText"
          :status="item.status"
          :activity-id="$route.params.activityId"
          :activity-disable="defaultStatus || maulStatus || overStatus"
          @change="getPlayerList"
          @vote-change="handleVoteChange"
          @table-sort="handleTableSort"
          @section-change="selectList = $event"/>
      </el-tab-pane>
    </el-tabs>
    <el-pagination
      :current-page="pageNum"
      :page-sizes="[50, 100, 150]"
      :page-size="pageSize"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"/>
    <el-dialog :visible.sync="dialogShow" title="批量导入选手数据">
      <el-form label-width="120px" label-position="right" label-suffix=":">
        <el-form-item label="选择图片文件">
          <el-upload
            ref="zipFile"
            :before-upload="imgBeforeUpload"
            :limit="1"
            :on-success="imgSuccess"
            :headers="{authorization: token}"
            :action="uploadUrl2"
          >
            <el-button type="primary">上传图片压缩文件</el-button>
            <div slot="tip" class="el-upload__tip">将多张选手图片压缩成.zip格式的压缩文件上传 ，且不超过50M !</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="选择表格文件">
          <el-upload
            ref="exclFile"
            :before-upload="exclBeforeUpload"
            :limit="1"
            :on-success="exclSuccess"
            :headers="{authorization: token}"
            :action="uploadUrl"
          >
            <el-button type="primary">上传表格</el-button>
            <div slot="tip" class="el-upload__tip">参照模板文件上传正确格式的 .xlsx 表格文件, 且不超过50M !</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="">
          <el-button :disabled="!importData.fileId" type="primary" @click="handleImportExcle"> 上传</el-button>
          <el-button type="info" @click="handleCancelDialog"> 取消</el-button>
        </el-form-item>
      </el-form>

    </el-dialog>
  </star-page>
</template>

<script>
import dayjs from 'dayjs'
import gift from '@/views/activity/gift'
import urlDialog from '../components/url-dialog'
import addNum from '../components/add-num'
import addRemark from './tpls/add-remark'
import playerList from './tpls/player-list'
import { getToken } from '@/utils/auth'

const BASE_API = process.env.BASE_API
export default {
  components: {
    gift,
    urlDialog,
    addNum,
    addRemark,
    playerList
  },
  data() {
    return {
      sort: null,
      dialogShow: false,
      token: getToken(),
      activityId: null,
      name: null,
      endTime: null,
      endDate: null,
      tableLoading: false,
      tabList: [
        { label: '全部选手', name: '0', status: '3' },
        { label: '待审核', name: '1', status: '1' },
        { label: '已审核', name: '2', status: '0' }
      ],
      hasVotes: false,
      selectList: [],
      total: null,
      pageSize: 50,
      pageNum: 1,
      tableData: [],
      allPlayerList: [],
      keyword: null,
      currentTabName: '0',
      downloadLoading: false,
      importData: {
        'activityId': 0,
        batchNum: null,
        'fileId': null
      },
      canUpload: false,
      emptyText: '加载中...',
      defaultStatus: false,
      maulStatus: false,
      overStatus: false
    }
  },
  computed: {
    uploadUrl() {
      return BASE_API + '/api/fileUploadController/uploadFileGrid?type=0'
    },
    uploadUrl2() {
      return BASE_API + '/api/fileUploadController/uploadFileGrid?type=1'
    }
  },
  async created() {
    if (this.$route.query.activityId) {
      this.activityId = this.$route.query.activityId || null
      this.keyword = this.$route.query.keyword || null
      await this.getH5HomePage()
      await this.getHomeMessage()
      this.getActivityDetail(this.activityId)
      this.getPlayerList()
    } else {
      this.$router.push({ name: 'vote' })
    }
  },
  methods: {
    async getActivityDetail(activityId) {
      this.tableLoading = true
      const { error, data, msg } = await this.$api.activity.getActivityDetail({ activityId })
      this.tableLoading = false
      if (error) return this.$message.error(msg)
      this.name = data.activityName
      this.endTime = data.activitySetting.applyEndTime
      this.endDate = dayjs(this.endTime).diff(dayjs())
    },
    handleVoteChange(playerId, votes) {
      const index = this.tableData.findIndex(item => item.playerId === playerId)
      if (index < -1) return
      const item = JSON.parse(JSON.stringify(this.tableData[index]))
      item.votes = item.votes + Number(votes)
      this.tableData.splice(index, 1, item)
    },
    handleTableSort(prop, order) {
      if (prop === 'num') {
        if (order === 'descending') {
          this.sort = 'num desc'
          return this.getPlayerList()
        }
        this.sort = 'num asc'
        this.getPlayerList()
      } else {
        if (order === 'descending') {
          this.sort = `${prop} desc,num asc`
          this.getPlayerList()
        } else {
          this.sort = `${prop} asc,num asc`
          return this.getPlayerList()
        }
      }
    },
    async getPlayerList() {
      const params = {
        activityId: this.activityId,
        page: this.pageNum,
        size: this.pageSize
      }
      if (this.keyword) params.search = this.keyword
      if (this.sort) params.sort = this.sort
      if (this.currentTabName !== '0') params.approvalStatus = this.currentTabName === '1' ? this.currentTabName : 0
      const { error, msg, data } = await this.$api.player.getPlayerList(params)
      if (error) return this.$message.error(msg)
      this.tableData = data.list
      this.emptyText = ''
    },
    async getAllPlayerList() {
      const params = {
        activityId: this.activityId,
        page: 1,
        size: this.total
      }
      if (this.keyword) params.search = this.keyword
      if (this.currentTabName !== '0') params.approvalStatus = this.currentTabName === '1' ? this.currentTabName : 0
      const { error, msg, data } = await this.$api.player.getPlayerList(params)
      if (error) return this.$message.error(msg)
      this.allPlayerList = data.list
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.getPlayerList()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.getPlayerList()
    },
    async oneClickAdded() {
      const params = {
        activityId: this.activityId,
        page: this.pageNum,
        size: this.pageSize
      }
      if (this.currentTabName !== '0') params.approvalStatus = this.currentTabName === '1' ? this.currentTabName : 0

      const { error, msg } = await this.$api.player.oneClickAdded(params)
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.getPlayerList()
    },
    async handleBatchDelete() {
      const { error, msg } = await this.$api.player.batchDelete({ ids: this.selectList.map(item => item.playerId) })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.getPlayerList()
    },
    handleDownload() {
      this.downloadLoading = true
      this.getAllPlayerList()
        import('@/vendor/Export2Excel').then(excel => {
          const tHeader = ['', this.name, '', '']
          const data = this.formatJson(this.allPlayerList)
          data.unshift(['选手编号', '名称', '手机号码', '地址'])
          excel.export_json_to_excel({
            header: tHeader,
            data,
            filename: `${this.name}`
          })
          this.downloadLoading = false
        })
    },
    formatJson(jsonData) {
      const data = jsonData.map(v => [v.num, v.name, v.phone, v.declaration]).sort((a, b) => a[0] - b[0])
      return data
    },
    handleDownLoad() {
        import('@/vendor/Export2Excel').then(excel => {
          const tHeader = ['名称', '手机号码', '地址']
          const data = []
          excel.export_json_to_excel({
            header: tHeader,
            data,
            filensame: `${this.name}选手批量上传模板`
          })
        })
    },
    async handleImportExcle(id) {
      const { error, msg } = await this.$api.importExcle.importExcelData({
        activityId: this.activityId,
        fileId: this.importData.fileId,
        batchNum: this.importData.batchNum
      })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.getPlayerList()
      this.$refs.exclFile.clearFiles()
      this.$refs.zipFile.clearFiles()
      this.dialogShow = !this.dialogShow
      location.reload()
    },
    async exclSuccess(res) {
      const { error, data, msg } = res
      if (error) return this.$message.error(msg)
      this.$set(this.importData, 'fileId', data.id)
    },
    exclBeforeUpload(file) {
      const isExcl = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || file.type === '.csv' || file.type === 'application/vnd.ms-excel' || file.type === ''
      const isLt50M = file.size / 1024 / 1024 < 5
      if (!isExcl) {
        this.$message.error('上传表格应该是 .xlsx 格式 ！')
      }
      if (!isLt50M) {
        this.$message.error('上传表格大小不能超过 5MB!')
      }
      return isExcl && isLt50M
    },
    imgBeforeUpload(file) {
      const isZip = file.type === 'application/x-zip-compressed' || file.type === 'application/zip' || file.type === ''

      const isLt5M = file.size / 1024 / 1024 < 50
      if (!isZip) {
        this.$message.error('上传图片的压缩包应该是 .zip 格式 ！')
      }
      if (!isLt5M) {
        this.$message.error('上传图片压缩包大小不能超过 50MB!')
      }
      return isZip && isLt5M
    },
    async imgSuccess(res) {
      const { error, data, msg } = res
      if (error) return this.$message.error(msg)
      this.$set(this.importData, 'batchNum', data.batchNum)
    },
    handleCancelDialog() {
      // this.$refs.zipFile.clearFiles()
      // this.$refs.exclFile.clearFiles()
      this.dialogShow = !this.dialogShow
    },
    async getH5HomePage() {
      const { error, msg, data } = await this.$api.activity.getH5HomePage({ activityId: this.activityId })
      if (error) return this.$message.error(msg)
      this.total = data.playerNum
      this.hasVotes = !!data.votes
      this.sort = data.votes ? 'votes desc,num asc' : 'num asc'
    },
    async getHomeMessage() {
      const { error, msg, data } = await this.$api.activity.getHomeMessage({ activityId: this.activityId })
      if (error) return this.$message.error(msg)
      this.defaultStatus = data.defaultStatus
      this.maulStatus = data.mualStatus
      this.overStatus = data.overStatus
    }
  }
}
</script>

<style lang="scss">
  .search {
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
