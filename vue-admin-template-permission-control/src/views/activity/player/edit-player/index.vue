<template>
  <star-page>
    <el-form
      ref="form"
      :model="addForm"
      :rules="rules"
      status-icon
      label-position="right"
      label-suffix=":"
      label-width="150px">
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
        <div class="flex f-a-center" style="flex-wrap: wrap">
          <div v-for="(file, index) in fileList" :key="index" class="img-item align-center">
            <el-upload
              v-if="!file.url"
              ref="upload"
              :before-upload="beforeUpload"
              :on-success="handleSuccess"
              :headers="{authorization: token}"
              :action="uploadUrl"
              :file-list="[file]"
              :show-file-list="false"
            >
              <el-button v-loading="file.loading">上传<i class="el-icon-upload el-icon--right"/></el-button>
            </el-upload>
            <div v-else class="upload-img__content" style="width: 148px;height: 148px; margin-right: 20px">
              <img :src="file.url" style="width: 148px;height: 148px" alt="">
              <i class="el-icon-error delete" @click="handleRemove(index)"/>
            </div>
          </div>
          <el-dialog :visible.sync="dialogVisible">
            <img :src="dialogImageUrl" width="100%" alt="">
          </el-dialog>
        </div>
      </el-form-item>
      <!--TODO: 三级权限 -->
      <el-form-item v-permission label="票数" prop="votes">
        <el-input v-model="addForm.votes" class="w100">
          <template slot="append">票</template>
        </el-input>
      </el-form-item>
      <!--TODO: 三级权限 -->
      <el-form-item v-permission label="礼物" prop="gift">
        <el-input v-model="addForm.gift" class="w100">
          <template slot="append">元</template>
        </el-input>
      </el-form-item>
      <el-form-item label="选手介绍">
        <el-upload
          ref="quillUpload"
          :headers="{authorization: token}"
          :action="uploadUrl"
          :show-file-list="false"
          :on-success="uploadSuccessDes"
          :on-error="uploadErrorDes"
          :before-upload="beforeUpload"
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
        <span class="info">工审核后前台才显示！</span>
      </el-form-item>
      <el-form-item label="">
        <el-button :disabled="!validate" type="primary" @click="update">更新</el-button>
        <el-button @click="$router.go(-1)">取消</el-button>
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
      fileList: [{ url: null, loading: false }, { url: null, loading: false }, { url: null, loading: false }, {
        url: null,
        loading: false
      }, { url: null, loading: false }, { url: null, loading: false }, { url: null, loading: false }, {
        url: null,
        loading: false
      }],
      playerId: null,
      dialogImageUrl: null,
      dialogVisible: false,
      token: getToken(),
      addForm: {
        'declaration': null,
        'gift': 0,
        'images': [],
        'name': null,
        'phone': null,
        'playerDesc': null,
        approvalStatus: 0,
        'votes': 0
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
          /* { validator: checkNum, trigger: 'blur' }*/

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
    this.playerId = this.$route.query.playerId
    this.getDetail(this.playerId)
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
    beforeUpload(file) {
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
    async getDetail(playerId) {
      const { error, data, msg } = await this.$api.player.detail({ playerId })
      if (error) return this.$message.error(msg)
      const formArr = ['activityId', 'declaration', 'gift', 'images', 'name', 'phone', 'playerDesc', 'votes']
      formArr.map(item => {
        this.$set(this.addForm, item, data[item])
      })
      this.$set(this.addForm, 'approvalStatus', data['approvalStatus'] ? 1 : 0)
      data.images ? data.images.forEach((item, index) => {
        this.fileList.splice(index, 1, { name: Math.random() * 5 + 'img', url: item, loading: false })
      }) : []
      // if (data.image) this.fileList.push({ name: Math.random() * 5 + 'img', url: data['image'] })
    },
    async update() {
      this.addForm.images = this.fileList.filter(i => !!i.url).map(i => i.url)
      const { error, msg } = await this.$api.player.update({ ...this.addForm, ...{ playerId: this.playerId }})
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.$router.go(-1)
    },
    handleRemove(index) {
      if (index > -1) return this.fileList.splice(index, 1, { url: null })
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url
      this.dialogVisible = true
    },
    handleSuccess(res, file) {
      const index = this.fileList.findIndex(item => item.url === null)
      console.log('*****', res, file, index)
      const { error, msg, data } = res
      if (error) return this.$message.error(msg)
      this.fileList.splice(index, 1, { name: Math.random() * 5 + 'img', url: data.url, loading: false })
      // if (!this.addForm.images) this.$set(this.addForm, 'images', [])
      //
      // this.addForm.images.push(data.url)
    }
  }
}
</script>

<style lang="scss">
  .img-item {
    background-color: #fbfdff;
    border: 1px dashed #c0ccda;
    border-radius: 6px;
    box-sizing: border-box;
    width: 148px;
    height: 148px;
    cursor: pointer;
    line-height: 146px;
    vertical-align: top;
    margin-right: 15px;
  }

  .avatar-uploader {
    line-height: 0
  }

  .upload-img__content {
    width: 148px;
    height: 148px;
    position: relative;
    .delete {
      position: absolute;
      color: red;
      top: -10px;
      right: -10px;
      font-size: 28px;
    }
  }

  .align-center {
    text-align: center
  }
  .el-textarea__inner{padding: 5px}
</style>
