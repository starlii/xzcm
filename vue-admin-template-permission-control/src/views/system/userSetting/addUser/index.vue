<template>
  <star-page>
    <el-form ref="searchForm" :model="searchForm" :rules="rules" status-icon label-suffix=":" label-width="120px" label-position="right">
      <el-form-item label="用户名称" prop="username">
        <el-input v-model="searchForm.username" class="w400"/>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="searchForm.password" type="password" auto-complete="off" class="w400"/>
      </el-form-item>
      <el-form-item label="确认密码" prop="repassword">
        <el-input v-model="searchForm.repassword" type="password" auto-complete="off" class="w400"/>
      </el-form-item>
      <el-form-item label="用户角色" prop="role">
        <el-select v-model="searchForm.role" placeholder="用户角色" prop="role">
          <el-option v-for="item in roleList" :key="item.value" :value="item.value" :label="item.label"/>
        </el-select>
      </el-form-item>
      <el-form-item label="用户套餐选择" prop="package">
        <el-select v-model="searchForm.userPackage" placeholder="用户套餐">
          <el-option v-for="item in packageList" :key="item.id" :value="item.id" :label="item.name"/>
        </el-select>
      </el-form-item>
      <el-form-item label="用户状态">
        <el-radio v-model="searchForm.status" :label="0">启用</el-radio>
        <el-radio v-model="searchForm.status" :label="1">关闭</el-radio>
      </el-form-item>
      <el-form-item label="备注说明" prop="remark">
        <el-input v-model="searchForm.remark" class="w600"/>
      </el-form-item>
      <el-form-item label="">
        <el-button type="primary" @click="submitForm('searchForm')">{{ userId ? '更新' : '添加' }}</el-button>
        <el-button type="info" @click="resetForm('searchForm')">取消</el-button>
      </el-form-item>
    </el-form>
  </star-page>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  name: 'Index',
  data() {
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else {
        if (this.searchForm.repassword !== '') {
          this.$refs.searchForm.validateField('repassword')
        }
        callback()
      }
    }
    const validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.searchForm.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      userId: null,
      radio: '0',
      searchForm: {
        username: null,
        password: null,
        repassword: null,
        userPackage: null,
        status: 0,
        remark: null,
        role: null
      },
      roleList: [],
      packageList: [],
      rules: {
        username: [
          { required: true, message: '请输入用户名称', trigger: 'blur' },
          { min: 2, max: 15, message: '长度在 2 到 15 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入用户密码', trigger: 'blur' },
          { validator: validatePass, trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        repassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validatePass2, trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        role: [
          { required: true, message: '请选择创建用户角色', trigger: 'blur' }
        ],
        userPackage: [
          { required: true, message: '请选择创建用户套餐', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters([
      'roles'
    ])
  },
  watch: {
    roles: {
      handler(val) {
        if (val[0] === 'system') this.roleList = [{ value: 0, label: '一级用户' }, { value: 1, label: '二级用户' }, { value: 2, label: '三级用户' }]
        if (val[0] === 'admin') this.roleList = [{ value: 2, label: '三级用户' }]
      },
      immediate: true
    }
  },
  created() {
    this.getUserPackage()
    if (this.$route.query.id) {
      this.userId = this.$route.query.id
      this.getUserInfo()
    }
  },
  methods: {
    async add() {
      const { error, msg } = await this.$api.userSetting.addUser(this.searchForm)
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.$router.push({ name: 'userSetting' })
    },
    async update() {
      const { error, msg } = await this.$api.userSetting.update({ id: this.userId, ...this.searchForm })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.$router.push({ name: 'userSetting' })
    },
    async getUserPackage() {
      const { error, msg, data } = await this.$api.userPackage.getNoPage()
      if (error) return this.$message.error(msg)
      this.packageList = data
    },
    async getUserInfo() {
      const { error, data, msg } = await this.$api.userSetting.getUserList({ id: this.userId, page: 1, size: 1 })
      if (error) return this.$message.error(msg)
      const arr = ['username', 'userPackage', 'remark', 'status']
      arr.map(item => { this.$set(this.searchForm, item, data.list[0][item]) })
      //  'role'
      this.$set(this.searchForm, 'role', data.list[0]['role'] === 'system' ? 0 : 1)
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.userId ? this.update() : this.add()
        } else {
          this.$message.error('信息提交错误 !!')
          return false
        }
      })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
      this.$router.push({ name: 'userSetting' })
    }
  }
}
</script>

<style scoped>

</style>
