<template>
  <star-page>
    <el-form ref="form" :model="addForm" :rules="rules" status-icon label-position="right" label-suffix=":" label-width="150px">
      <el-form-item label="选手姓名" prop="name">
        <el-input v-model="addForm.name" placeholder="请输入选手姓名"/>
      </el-form-item>
      <el-form-item label="选手手机号码" prop="phone">
        <el-input v-model="addForm.phone" placeholder="请输入选手手机号码"/>
      </el-form-item>
      <el-form-item label="选手地址" prop="declaration">
        <el-input v-model="addForm.declaration" type="textarea" placeholder="请输入选手地址"/>
      </el-form-item>
      <el-form-item label="选手图片">
        <el-upload
          ref="upload"
          :before-upload="beforeAvatarUpload"
          :on-preview="handlePictureCardPreview"
          :on-remove="handleRemove"
          :on-success="handleSuccess"
          :headers = "{authorization: token}"
          :action="uploadUrl"
          list-type="picture-card">
          <i class="el-icon-plus"/>
        </el-upload>
        <el-dialog :visible.sync="dialogVisible">
          <img :src="dialogImageUrl" width="100%" alt="">
        </el-dialog>
      </el-form-item>
      <!--TODO: 三级权限 -->
      <el-form-item v-permission label="票数" prop="votes">
        <el-input v-model="addForm.votes" class="w150 input-num"><template slot="append">票</template></el-input>
      </el-form-item>
      <!--TODO: 三级权限 -->
      <el-form-item v-permission label="礼物" prop="gift">
        <el-input v-model="addForm.gift" class="w150 input-num"><template slot="append">元</template></el-input>
      </el-form-item>
      <el-form-item label="选手介绍">
        <!--<el-input v-model="addForm.playerDesc" type="textarea"/>-->
        <el-upload
          ref="quillUpload"
          :headers = "{authorization: token}"
          :action="uploadUrl"
          :show-file-list="false"
          :on-success="uploadSuccessDes"
          :on-error="uploadErrorDes"
          :before-upload="beforeAvatarUpload"
          class="avatar-uploader"
        >
          <el-button v-show="false" id="quillUpload" size="small" type="primary">点击上传</el-button>
        </el-upload>
        <quill-editor
          ref="myQuillEditor"
          v-model="addForm.playerDesc"
          :options="editorOption"
        />

      </el-form-item>
      <el-form-item label="审核通过">
        <el-radio-group v-model="addForm.approvalStatus">
          <el-radio :label="0">是</el-radio>
          <el-radio :label="1">否</el-radio>
        </el-radio-group>
        <span class="info">人工审核后前台才显示！</span>
      </el-form-item>
      <el-form-item label="">
        <el-button type="primary" @click="submitForm('form')">添加</el-button>
        <el-button type="info" @click="$router.go(-1)">取消</el-button>
      </el-form-item>
    </el-form>
  </star-page>
</template>

<script>
import { getToken } from '@/utils/auth'
const BASE_API = process.env.BASE_API
export default {
  name: 'Index',
  data() {
    const checkNum = (rule, value, callback) => {
      if (Number(value) < 0) {
        callback(new Error('必须大于0'))
      } else {
        callback()
      }
    }
    const options = [
      [{ 'size': ['small', false, 'large'] }],
      ['bold', 'italic'],
      [{ 'list': 'ordered' }, { 'list': 'bullet' }],
      ['link', 'image']
    ]
    return {
      dialogImageUrl: null,
      dialogVisible: false,
      token: getToken(),
      addForm: {
        'activityId': null,
        'declaration': null,
        'gift': 0,
        'images': [],
        'name': null,
        'phone': null,
        'playerDesc': null,
        approvalStatus: 0,
        deviceType: 1,
        votes: 0
      },
      rules: {
        name: [
          { required: true, message: '请输入选手名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号码', trigger: 'blur' }
          // { len: 11, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        declaration: [
          { required: true, message: '请输入选手地址', trigger: 'blur' }
        ],
        votes: [
          { required: true, message: '请输入票数', trigger: 'blur' }
          // { validator: checkNum, trigger: 'blur' }

        ],
        gift: [
          { required: true, message: '请输入礼物金额', trigger: 'blur' },
          { validator: checkNum, trigger: 'blur' }
        ]
      },
      editorOption: {
        modules: {
          toolbar: {
            container: options, // 工具栏
            handlers: {
              'image': function(value) {
                if (value) {
                  document.querySelector('#quillUpload').click()
                } else {
                  this.quill.format('image', false)
                }
              }
            }
          },
          history: {
            delay: 1000,
            maxStack: 50,
            userOnly: false
          }
        }
      }
    }
  },
  computed: {
    validate() {
      return !!this.addForm.name && !!this.addForm.phone && !!this.addForm.declaration
    },
    uploadUrl() {
      return BASE_API + '/api/fileUploadController/uploadFileGrid?type=0'
    }
  },
  created() {
    this.$set(this.addForm, 'activityId', this.$route.query.activityId)
  },
  methods: {
    uploadSuccessDes(res) {
      const quill = this.$refs.myQuillEditor.quill
      const { error, data } = res
      // 如果上传成功
      if (!error) {
        // 获取光标所在位置
        const length = quill.getSelection().index
        // 插入图片  res.info为服务器返回的图片地址
        quill.insertEmbed(length, 'image', data.url)
        // 调整光标到最后
        quill.setSelection(length + 1)
      } else {
        this.$message.error('图片插入失败')
      }
    },
    uploadErrorDes() {

    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 1

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG 格式 或者 PNG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 1MB!')
      }
      return isJPG && isLt2M
    },
    async addPlayer() {
      const { error, msg } = await this.$api.player.addPlayer(this.addForm)
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.$router.go(-1)
    },
    handleRemove(file, fileList) {
      const index = this.addForm.images.findIndex(item => item === file.response.data.url)
      if (index > -1) return this.addForm.images.splice(index, 1)
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url
      this.dialogVisible = true
    },
    handleSuccess(res) {
      const { error, data, msg } = res
      if (error) return this.$message.error(msg)
      this.addForm.images.push(data.url)
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.addPlayer()
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

<style>
  .avatar-uploader{line-height: 0}
  .el-textarea__inner{padding: 5px}
</style>
