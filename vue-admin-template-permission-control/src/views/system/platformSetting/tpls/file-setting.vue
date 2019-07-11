<template>
  <star-page class="clearPic">
    <el-form label-width="150px" label-suffix=":" label-position="right">
      <el-form-item label="一键清除图片缓存">
        <div class="flex flex-column">
          <span class="info">一键清除功能将会清理当前用户上传的所有图片，这将会影响到活动和选手的展示效果 ！</span>
        </div>
      </el-form-item>
      <el-form-item label="">
        <el-button type="primary" @click="dialogFormVisible = true">确定清除</el-button>
      </el-form-item>
    </el-form>
    <el-dialog :visible.sync="dialogFormVisible" title="一键清除图片缓存" width="600px">
      <el-form>
        <el-form-item label="输入密码:" label-width="100">
          <el-input v-model="pass" autocomplete="off"/>
          <div class="el-form-item__error">
            {{ errorTips }}
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="confrimDelete">确 定</el-button>
      </div>
    </el-dialog>
  </star-page>
</template>

<script>
export default {
  name: 'FileSetting',
  data() {
    return {
      dialogFormVisible: false,
      pass: '',
      errorTips: ''
    }
  },
  methods: {
    confrimDelete() {
      if (!this.pass) {
        this.errorTips = '密码不能为空！'
      } else if (this.pass !== 'jiangxi@zhengshaoyi') {
        this.errorTips = '请输入正确的密码！'
      } else {
        this.errorTips = ''
        this.$confirm('此操作将永久删除所有图片, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.handleClear()
          this.dialogFormVisible = false
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
      }
    },
    async handleClear() {
      const { error, msg } = await this.$api.platForm.deleteAll()
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
    }
  }
}
</script>

<style lang="scss">
  .clearPic {
    .el-form-item__content {
      float: left;
      width: 400px;
    }
  }
</style>
