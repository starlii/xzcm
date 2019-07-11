<template>
  <el-table v-loading="loading" ref="table" :data="tableData" :empty-text="emptyText" border stripe class="mt10 playerList" @selection-change="$emit('section-change', $event)" @sort-change="handleSortChange">
    <el-table-column type="selection" width="40" align="center"/>
    <el-table-column sortable="custom" prop="num" align="center" label="选手编号" width="80"/>
    <el-table-column prop="name" align="center" label="姓名">
      <template slot-scope="scope">
        <div class="flex flex-column">
          <span>{{ scope.row.name }}</span>
          <img v-lazy="scope.row.image" :key="scope.row.image" style="width: 80px; height: 50px; margin:0 auto">
        </div>
      </template>
    </el-table-column>
    <el-table-column prop="phone" align="center" label="手机号码"/>
    <el-table-column label="票数" width="130" prop="votes" sortable="custom">
      <template slot-scope="scope">
        <div class="flex flex-column">
          <span>总票数： {{ scope.row.votes || 0 }}</span>
          <!--TODO: 权限按钮-->
          <add-num v-permission v-if="!scope.row.voteStatus" @change="handleAddVote( scope.row.playerId, $event)"/>
        </div>
      </template>
    </el-table-column>
    <el-table-column label="金额" align="center" width="80" prop="gift" sortable="custom">
      <template slot-scope="scope">
        <el-tag type="danger">￥{{ scope.row.gift || 0 }}</el-tag>
      </template>
    </el-table-column>
    <el-table-column label="浏览" prop="ipAmount" sortable="custom">
      <template slot-scope="scope">
        <div class="flex flex-column">
          <span>IP量: {{ scope.row.ipAmount || 0 }}</span>
          <span>分享量: {{ scope.row.shares || 0 }}</span>
        </div>
      </template>
    </el-table-column>
    <el-table-column prop="" label="参与时间" align="center" width="175">
      <template slot-scope="scope">
        <span class="mb5 time-tag"> {{ scope.row.createTime || formatDate }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="remark" label="备注" align="center">
      <template slot-scope="scope">
        <add-remark :remark="scope.row.remark" :player="scope.row.name" :uid="scope.row.playerId" @success="$emit('change')" />
      </template>
    </el-table-column>
    <el-table-column prop="" label="状态" width="120">
      <template slot-scope="scope">
        <div>
          <el-tag :type="scope.row.approvalStatus ? 'danger' : 'success'" class="pointer" @click.native="changeStatus(scope.row.playerId, 'approvalStatus', scope.row.approvalStatus ? 0 : 1)">
            {{ scope.row.approvalStatus ? '待审核' : '已审核' }}
          </el-tag>
          <br>
          <el-tag :type="scope.row.lockStatus ? 'success' : 'danger'" class="mt5 mb5 pointer" @click.native="changeStatus(scope.row.playerId, 'lockStatus', scope.row.lockStatus ? 0 : 1)">{{ scope.row.lockStatus ? '非锁定' : '已锁定' }}</el-tag>
          <br>
          <el-tag :type="scope.row.voteStatus ? 'danger' : 'success'" class="pointer" @click.native="changeStatus(scope.row.playerId, 'voteStatus', scope.row.voteStatus ? 0 : 1)">{{ scope.row.voteStatus ? '禁止投票' : '允许投票' }}</el-tag>
        </div>
      </template>
    </el-table-column>
    <el-table-column label="管理操作" min-width="130" align="left">
      <template slot-scope="scope">
        <div class="ops">
          <el-button type="text" @click="showLink(scope.row.name,scope.row.playerId)">查看选手链接</el-button>
          <el-button type="text" @click="$router.push({path: 'playerLog', query: {playerId: scope.row.playerId}})">数据记录</el-button>
          <el-button :type="scope.row.star ? 'text' : 'primary'" :disabled="activityDisable" @click="setPlayerToStar(scope.row.playerId)">{{ scope.row.star ? '今日之星' : '取消今日之星' }}</el-button>
          <el-button type="text" @click="$router.push({name: 'editPlayer', query:{playerId: scope.row.playerId}})">修改</el-button>
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
            <el-input v-model="seconds" type="number" min="0"/>
          </div>
          分
        </el-form-item>
        <el-form-item label="票数">
          <div class="w100 displayIb mr10">
            <el-input v-model="autoVotes" type="number" min="0"/>
          </div>
          票
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleScheduled">确定</el-button>
          <el-button type="info" @click="autoVoteDialog = !autoVoteDialog">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </el-table>
</template>

<script>
import addNum from '../../components/add-num'
import addRemark from './add-remark'
import urlDialog from '../../components/url-dialog'

export default {
  name: 'PlayerList',
  components: { addNum, addRemark, urlDialog },
  props: {
    status: { type: String, default: null },
    activityId: { type: Number, default: null },
    tableData: { type: Array, default: () => [] },
    loading: { type: Boolean, default: false },
    activityDisable: { type: Boolean, default: false },
    emptyText: { type: String, default: '加载中...' }
  },
  data() {
    return {
      show: false,
      dialogTitle: null,
      uid: null,
      autoVoteDialog: false,
      seconds: 1,
      autoVotes: 1,
      autoPlayerId: null
    }
  },
  methods: {
    async handleAddVote(playerId, votes) {
      const { error, msg } = await this.$api.player.muaUpdate({
        playerId,
        votes
      })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.$emit('vote-change', playerId, votes)
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
        this.$emit('change')
      }).catch(() => {
      })
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
      this.$emit('change')
    },
    showLink(name, id) {
      this.show = !this.show
      this.dialogTitle = name
      this.uid = id
    },
    async setPlayerToStar(playerId) {
      const { error, msg } = await this.$api.player.setStar({ playerId })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.$emit('change')
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
      this.$emit('change')
    },
    onUrlDialogShow() {
      this.$nextTick(() => {
        this.$refs.urlDialog.loadLink()
      })
    },
    handlerAutoVote(autoPlayerId) {
      this.autoPlayerId = autoPlayerId
      this.autoVoteDialog = !this.autoVoteDialog
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
      this.$emit('change')
    },
    async scheduledDown(playerId) {
      const { error, msg } = await this.$api.player.scheduledDown({ playerId })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.$emit('change')
    },
    handleSortChange(params) {
      const { prop, order } = params
      this.$emit('table-sort', prop, order)
    }
  }
}
</script>

<style lang="scss">
  .playerList {
    .ops {
      .el-button {
        margin: 5px 5px 0 0;
      }
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

  .displayIb {
    display: inline-block
  }
</style>
