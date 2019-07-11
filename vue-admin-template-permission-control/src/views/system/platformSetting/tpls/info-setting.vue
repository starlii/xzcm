<template>
  <el-form ref="form" :model="platformInfo" :rules="rules" label-width="150px" label-position="right" label-suffix=":">
    <el-form-item label="平台名称" prop="platformName">
      <el-input v-model="platformInfo.platformName" class="w400" placeholder="平台名称"/>
    </el-form-item>
    <el-form-item label="平台域名" prop="platformDomain">
      <el-input v-model="platformInfo.platformDomain" class="w400" placeholder="平台域名"/>
    </el-form-item>
    <el-form-item label="新用户初始套餐" prop="newUserPackage">
      <el-select v-model="platformInfo.newUserPackage" placeholder="用户套餐">
        <el-option v-for="item in packageList" :key="item.id" :value="String(item.id)" :label="item.name"/>
      </el-select>
    </el-form-item>
    <el-form-item label="">
      <el-button type="primary" @click="submitForm('form')">保存</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
export default {
  name: 'InfoSetting',
  data() {
    return {
      packageList: [],
      platformInfo: {
        platformDomain: null,
        newUserPackage: null,
        platformName: null
      },
      rules: {
        platformName: [
          { required: true, message: '请输入平台名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        platformDomain: [
          { required: true, message: '请输入平台域名', trigger: 'blur' },
          { min: 4, max: 50, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        newUserPackage: [
          { required: true, message: '请选择用户套餐', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getBasicInfo()
    this.getUserPackage()
  },
  methods: {
    async getUserPackage() {
      const { error, msg, data } = await this.$api.userPackage.getNoPage()
      if (error) return this.$message.error(msg)
      this.packageList = data
    },
    async getBasicInfo() {
      const { error, data, msg } = await this.$api.platForm.getBasicInfo()
      if (error) return this.$message.error(msg)
      this.platformInfo = data
    },
    async updateInfo() {
      const { error, msg } = await this.$api.platForm.update({
        basicSetting: this.platformInfo
      })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.$router.go(0)
      this.getBasicInfo()
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.updateInfo()
        } else {
          this.$message.error('信息提交错误 !!')
          return false
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
