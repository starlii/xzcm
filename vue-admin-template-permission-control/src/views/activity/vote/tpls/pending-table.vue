<template>
  <section>
    <div class="mt10 mb10">
      <el-input v-model="search" class="w300 mr20 pointer" placeholder="搜索选手姓名或编号" @keyup.enter.native="getPending">
        <div slot="append" class="pointer" @click="getPending">搜索</div>
      </el-input>
    </div>
    <el-table :data="pendingInfo.list" border stripe class="mt10 playerList">
      <el-table-column prop="activityName" label="活动名称" min-width="120"/>
      <el-table-column prop="num" align="center" label="选手编号" width="80"/>
      <el-table-column prop="name" align="center" label="姓名">
        <template slot-scope="scope">
          <div class="flex flex-column">
            <span>{{ scope.row.name }}</span>
            <img v-lazy="scope.row.image" :key="scope.row.image" style="width: 80px; height: 50px; margin:0 auto">
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="phone" align="center" label="手机号码"/>
      <el-table-column label="金额" align="center" width="80" prop="gift" sortable/>
      <el-table-column label="票数" width="130" prop="votes" sortable>
        <template slot-scope="scope">
          <div class="flex flex-column">
            <span>总票数： {{ scope.row.votes || 0 }}</span>
            <!--TODO: 权限按钮-->
            <add-num v-permission v-if="!scope.row.voteStatus" @change="handleAddVote(scope.row.playerId, $event)"/>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="浏览" prop="ipAmount" sortable>
        <template slot-scope="scope">
          <div class="flex flex-column">
            <span>IP量: {{ scope.row.ipAmount }}</span>
            <span>分享量: {{ scope.row.shares }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="" label="参与时间" align="center" width="175" sortable>
        <template slot-scope="scope">
          <span class="mb5 time-tag"> {{ scope.row.createTime || formatDate }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" align="center">
        <template slot-scope="scope">
          <add-remark
            :remark="scope.row.remark"
            :player="scope.row.name"
            :uid="scope.row.playerId"
            @success="getPending"/>
        </template>
      </el-table-column>
      <el-table-column prop="" label="状态" width="120">
        <template slot-scope="scope">
          <div>
            <el-tag
              :type="scope.row.approvalStatus ? 'danger' : 'success'"
              class="pointer"
              @click.native="changeStatus(scope.row.playerId, 'approvalStatus', scope.row.approvalStatus ? 0 : 1)"
            >
              {{ scope.row.approvalStatus ? '待审核' : '已审核' }}
            </el-tag>
            <br>
            <el-tag
              :type="scope.row.lockStatus ? 'success' : 'danger'"
              class="mt5 mb5 pointer"
              @click.native="changeStatus(scope.row.playerId, 'lockStatus', scope.row.lockStatus ? 0 : 1)">{{ scope.row.lockStatus ? '非锁定' : '已锁定' }}
            </el-tag>
            <br>
            <el-tag
              :type="scope.row.voteStatus ? 'danger' : 'success'"
              class="pointer"
              @click.native="changeStatus(scope.row.playerId, 'voteStatus', scope.row.voteStatus ? 0 : 1)">{{ scope.row.voteStatus ? '禁止投票' :
              '允许投票' }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="管理操作" min-width="130" align="left">
        <template slot-scope="scope">
          <div class="ops">
            <el-button type="text" @click="showLink(scope.row.name,scope.row.playerId)">查看选手链接</el-button>
            <el-button
              type="text"
              @click="$router.push({path: 'playerLog', query: {playerId: scope.row.playerId}})">数据记录
            </el-button>
            <el-button :type="scope.row.star ? 'text' : 'primary'" @click="setPlayerToStar(scope.row.playerId)">
              {{ scope.row.star ? '今日之星' : '取消今日之星' }}
            </el-button>
            <el-button type="text" @click="$router.push({name: 'editPlayer', query:{playerId: scope.row.playerId}})">修改 </el-button>
            <el-button type="text" @click="confrimDeletePlayer(scope.row.playerId)">删除</el-button>
            <span v-permission v-if="scope.row.schedulStatus==1">
              <el-button :type="scope.row.schedulStatus ? 'text' : 'primary'" @click="handlerAutoVote(scope.row.playerId)">自动投票</el-button>
            </span>
            <span v-permission v-else>
              <el-button v-if="!scope.row.schedulStatus" type="primary" @click="scheduledDown(scope.row.playerId)">取消自动投票</el-button>
            </span>
          </div>
        </template>
      </el-table-column>
      <el-dialog :title="`选手“${dialogTitle}”的访问连接 `" :visible.sync="show" append-to-body @open="onUrlDialogShow">
        <url-dialog ref="urlDialog" :uid="uid"/>
      </el-dialog>
      <el-dialog :visible.sync="autoVoteDialog" append-to-body width="300px" title="设置自动投票">
        <el-form label-width="80px" label-position="right" label-suffix=":">
          <el-form-item label="时间间隔">
            <div class="w100 displayIb mr10">
            <el-input v-model="seconds" type="number" min="0"/></div>分
          </el-form-item>
          <el-form-item label="票数">
            <div class="w100 displayIb mr10">
            <el-input v-model="autoVotes" type="number" min="0"/></div>票
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleScheduled">确定</el-button>
            <el-button type="info" @click="autoVoteDialog = !autoVoteDialog">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
    </el-table>
  </section>
</template>

<script>
import addNum from '../../components/add-num'
import addRemark from './add-remark'
import urlDialog from '../../components/url-dialog'
export default {
  name: 'PendingTable',
  components: { addNum, addRemark, urlDialog },
  data() {
    return {
      search: null,
      show: false,
      dialogTitle: null,
      pendingInfo: {
        list: [],
        total: 0
      },
      autoVoteDialog: false,
      autoVotes: 0,
      page: 1,
      size: 50,
      uid: null,
      autoPlayerId: null,
      seconds: 1
    }
  },
  created() {
    this.getPending()
  },
  methods: {
    async getPending() {
      const params = {
        page: this.page,
        size: this.size
      }
      if (this.search) params.search = this.search
      const { error, data, msg } = await this.$api.player.getPending(params)
      if (error) return this.$message.error(msg)
      this.pendingInfo = data
    },
    async handleAddVote(playerId, votes) {
      const { error, msg } = await this.$api.player.muaUpdate({ playerId, votes })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.getPending()
    },
    async changeStatus(playerId, flag, value) {
      if (flag === 'lockStatus' && !value) {
        return this.addLockTime(playerId, value)
      }
      const { error, msg } = await this.$api.player.changeStatus({
        playerId,
        [flag]: value
      })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.getPending()
    },
    addLockTime(playerId, val) {
      this.$prompt('请输入锁定分钟数', '', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async({ value }) => {
        const { error, msg } = await this.$api.player.changeStatus({
          playerId,
          lockStatus: val,
          lockTime: value
        })
        if (error) return this.$message.error(msg)
        this.$message.success(msg)
        this.getPending()
      }).catch(() => {
      })
    },
    showLink(name, id) {
      this.show = !this.show
      this.dialogTitle = name
      this.uid = id
    },
    onUrlDialogShow() {
      this.$nextTick(() => {
        this.$refs.urlDialog.loadLink()
      })
    },
    async setPlayerToStar(playerId) {
      const { error, msg } = await this.$api.player.setStar({ playerId })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.getPending()
    },
    confrimDeletePlayer(playerId) {
      this.$confirm('此操作将永久删除, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deletePlayer(playerId)
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
    async deletePlayer(playerId) {
      const { error, msg } = await this.$api.player.deletePlayer({ playerId })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.getPending()
    },
    handlerAutoVote(autoPlayerId) {
      this.autoPlayerId = autoPlayerId
      this.autoVoteDialog = !this.autoVoteDialog
    },
    async scheduledDown(playerId) {
      const { error, msg } = await this.$api.player.scheduledDown({ playerId })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.getPending()
    },
    async handleScheduled() {
      const { error, msg } = await this.$api.player.scheduled({
        playerId: this.autoPlayerId,
        'seconds': this.seconds,
        'votes': this.autoVotes
      })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.autoVoteDialog = !this.autoVoteDialog
      this.getPending()
    }
  }
}
</script>

<style lang="scss" scoped>
  .playerList{
  .ops{
  .el-button {
    margin: 5px 5px 0 0;}
  }
  .el-button--text {
    border: 1px solid #409EFF;
    padding: 5px;
  }
  .time-tag {
    background-color: #409EFF;
    padding: 0px 10px;
    display: inline-block;
    border-radius: 5px;
    color: #fff;
  }
  }
  .displayIb{display: inline-block}
</style>
