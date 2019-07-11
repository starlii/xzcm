<template>
  <span class="front-link" @click="show = !show">{{ remark || '添加备注' }}
    <el-dialog :title="`选手${player}的备注修改`" :visible.sync="show" append-to-body>
      <el-input v-model="remarkCopy"/>
      <div class="center mt10">
        <el-button type="primary" @click="handleSave"> 保存</el-button>
        <el-button type="info" @click="show = !show">取消</el-button>
      </div>
    </el-dialog>
  </span>
</template>

<script>
export default {
  name: 'AddRemark',
  props: {
    remark: { type: String, default: '添加备注' },
    player: { type: String, default: null },
    uid: { type: Number, default: null }
  },
  data() {
    return {
      show: false,
      remarkCopy: null
    }
  },
  watch: {
    remark: {
      handler(val) {
        this.remarkCopy = val
      },
      immediate: true
    }
  },
  methods: {
    async handleSave() {
      const { error, msg } = await this.$api.player.batchUpdate({
        ids: [this.uid],
        remark: this.remarkCopy
      })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.$emit('success')
      this.show = !this.show
    }
  }
}
</script>

<style scoped>

</style>
