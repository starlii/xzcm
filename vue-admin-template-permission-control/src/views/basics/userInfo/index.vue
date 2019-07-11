<template>
  <star-page>
    <el-form ref="form" :model="formDate" :rules="rules" label-width="120px" label-position="right" label-suffix=":">
      <el-form-item label="用户名称" prop="username">
        <div class="flex flex-column">
          <el-input v-model="formDate.username" class="w400" placeholder="若不修改用户名，不需要填写"/>
          <span class="info">若不修改用户名，不需要填写</span>
        </div>
      </el-form-item>
      <el-form-item label="旧密码" prop="oldPwd">
        <el-input v-model="formDate.oldPwd" type="password" class="w400"/>
      </el-form-item>
      <el-form-item label="新密码" prop="newPwd">
        <el-input v-model="formDate.newPwd" type="password" class="w400"/>
      </el-form-item>
      <el-form-item label="">
        <el-button type="primary" @click="submitForm('form')">保存</el-button>
        <el-button type="info" @click="resetForm('form')">取消</el-button>
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
        callback(new Error('请输入旧密码'))
      } else {
        callback()
      }
    }
    const validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次新密码'))
      } else {
        callback()
      }
    }
    return {
      formDate: {
        username: null,
        oldPwd: null,
        newPwd: null
      },
      rules: {
        username: [
          // { required: true, message: '请输入用户名称', trigger: 'blur' },
          { min: 2, max: 15, message: '长度在 2 到 15 个字符', trigger: 'blur' }
        ],
        oldPwd: [
          { required: true, message: '请输入用户密码', trigger: 'blur' },
          { validator: validatePass, trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        newPwd: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validatePass2, trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters([
      'name',
      'roles'
    ])
  },
  watch: {
    // name: {
    //   handler(val) {
    //     this.$set(this.formDate, 'username', val)
    //   },
    //   immediate: true
    // }
  },
  methods: {
    async modifyPassword() {
      const { error, msg } = await this.$api.userSetting.modifyPassword(this.formDate)
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.loginOut()
    },
    loginOut() {
      this.$store.dispatch('LogOut').then(() => {
        location.reload() // 为了重新实例化vue-router对象 避免bug
      })
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.modifyPassword()
        } else {
          this.$message.error('信息提交错误 !!')
          return false
        }
      })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
    }
  }
}
</script>

<style scoped>

</style>
