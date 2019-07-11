<template>
  <star-page>
    <el-form ref="form" :model="formData" :rules="rules" status-icon label-width="120px" label-suffix=":" label-position="right">
      <el-form-item label="套餐名称" prop="name">
        <el-input v-model="formData.name" class="w400"/>
      </el-form-item>
      <el-form-item label="套餐状态">
        <el-radio v-model="formData.status" :label="0">启用</el-radio>
        <el-radio v-model="formData.status" :label="1">关闭</el-radio>
      </el-form-item>
      <el-form-item label="套餐代理系数" prop="agentValue">
        <div class="flex flex-column">
          <el-input v-model="formData.agentValue" type="number" min="0" max="1" class="w400"/>
          <span class="info">报表里给代理提成计算时的代理系数，要小于等于1，如填0.8代表给属于此套餐的代理报表上按提成80%计算</span>
        </div>
      </el-form-item>
      <el-form-item label="套餐备注">
        <el-input v-model="formData.description" class="w400"/>
      </el-form-item>
      <el-form-item label="">
        <el-button type="primary" @click="submitForm('form')">{{ packageId ? '更新' : '添加' }}</el-button>
        <el-button type="info" @click="resetForm('form')">取消</el-button>
      </el-form-item>
    </el-form>
  </star-page>
</template>

<script>
export default {
  name: 'Index',
  data() {
    const checkValue = (rule, value, callback) => {
      if (value === null) {
        return callback(new Error('套餐系数不能为空'))
      }

      if (Number(value) < 0 || Number(value) > 1) {
        callback(new Error('套餐系数必须大于0小于1'))
      } else {
        callback()
      }
    }
    return {
      packageId: null,
      formData: {
        'agentValue': 0,
        'description': null,
        'name': null,
        'status': 0
      },
      rules: {
        name: [
          { required: true, message: '请输入套餐名称', trigger: 'blur' },
          { min: 2, max: 15, message: '长度在 2 到 15 个字符', trigger: 'blur' }
        ],
        agentValue: [
          { required: true, message: '请输入套餐系数', trigger: 'blur' },
          { validator: checkValue, trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    if (this.$route.query.id) {
      this.packageId = this.$route.query.id
      this.getPackageInfo()
    }
  },
  methods: {
    async getPackageInfo() {
      const { error, data, msg } = await this.$api.userPackage.getUserPackage({ id: this.packageId, page: 1, size: 100 })
      if (error) return this.$message.error(msg)
      console.log('package info', data.list[0])
      const arr = ['agentValue', 'description', 'name', 'status']
      arr.map(item => { this.$set(this.formData, item, data.list[0][item]) })
    },
    async update() {
      const { error, msg } = await this.$api.userPackage.updateUserPackage({ id: this.packageId, ...this.formData })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.$router.push('setMeal')
    },
    async addUserPackage() {
      const { error, msg } = await this.$api.userPackage.addUserPackage(this.formData)
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.$router.push({ name: 'setMeal' })
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.packageId ? this.update() : this.addUserPackage()
        } else {
          this.$message.error('信息提交错误 !!')
          return false
        }
      })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
      this.$router.push({ name: 'setMeal' })
    }
  }
}
</script>

<style scoped>

</style>
