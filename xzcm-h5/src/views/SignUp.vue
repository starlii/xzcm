<template>
  <div id="signUp" class="pd-10">
    <h2>
      选择上传1-5张图片，第一张为封面图。
    </h2>
    <div id="pict" class="aui-margin-t-10">
      <div class="icon-po fl" v-for="(item,index) in images">
        <div v-loading="loading"><img :src="item"></div>
        <div class="icon-close" @click="imgclose(index)">×</div>
      </div>
      <el-upload :action="uploadUrl"
                 list-type="picture-card"
                 :limit="5"
                 :on-exceed="handleExceed"
                 :on-change="imgPreview"
                 :on-success="handleSuccess"
      >
        <i class="el-icon-plus"></i>
      </el-upload>
    </div>
    <p class="aui-clearfix"></p>
    <div class="ruleForm aui-margin-t-15">
      <el-form status-icon :model="ruleForm" :rules="rules" ref="ruleForm" label-width="95px" label-position="left"
               label-suffix="："
               class="demo-ruleForm">
        <el-form-item label="商家名称" prop="name">
          <el-input type="text" v-model="ruleForm.name" placeholder="请输入商家名称"></el-input>
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input type="text" v-model="ruleForm.phone" placeholder="请输入手机号码" prop="phone"></el-input>
        </el-form-item>
        <el-form-item label="商家地址">
          <el-input type="textarea" v-model="ruleForm.declaration" placeholder="请输入商家地址"></el-input>
        </el-form-item>
        <div>
          <p>请如实填写报名信息，提交信息为唯一获奖凭证</p>
          <el-button type="success" class="aui-margin-t-15" @click="submitForm('ruleForm')">提交报名</el-button>
        </div>
      </el-form>
    </div>


  </div>
</template>

<script>
  const BASE_API = process.env.BASE_API
  export default {
    data() {
      var checkPhone = (rule, value, callback) => {
        if (!value) {
          return callback(new Error('请输入手机号码'));
        } else {
          const reg = /^1[3|4|5|7|8][0-9]\d{8}$/
          console.log(reg.test(value));
          if (reg.test(value)) {
            callback();
          } else {
            return callback(new Error('请输入正确的手机号'));
          }
        }
      };
      return {
        ruleForm: {
          name: '',
          phone: '',
          declaration: ''
        },
        rules: {
          name: [
            {required: true, message: '请输入商家名称', trigger: 'blur'}
          ],
          phone: [
            {required: true, message: '请输入手机号码', trigger: 'blur'},
            {validator: checkPhone, trigger: 'blur'}
          ]
        },
        declaration: '',
        images: [],
        dialogImageUrl: '',
        dialogVisible: false,
        fileList: [],
        formMovie: {
          posterURL: ''
        },
        activityId: this.$cookies.get("activityId"),
        overStatus: this.$cookies.get("overStatus"),
        defaultStatus: this.$cookies.get("defaultStatus"),
        loading: true,
        openId: this.$cookies.get("openId"),
      };
    },
    computed: {
      uploadUrl() {
        return BASE_API + '/api/fileUploadController/uploadFileGrid?type=0'
      }
    },
    created() {
      this.$parent.getHomeMessage( this.getUrlKey('activityId'))
    },
    methods: {
      getUrlKey: function (name) {//获取url 参数
        return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ""])[1].replace(/\+/g, '%20')) || null;
      },
      handleRemove(file, fileList) {
        console.log(file, fileList);
      },
      handlePictureCardPreview(file) {
        this.dialogImageUrl = file.url;
        this.dialogVisible = true;
      },
      imgPreview(file, fileList) {
        let fileName = file.name;
        let regex = /(.jpg|.jpeg|.gif|.png|.bmp)$/;
        if (regex.test(fileName.toLowerCase())) {
          this.formMovie.posterURL = file.url;
          this.fileList = fileList

        } else {
          this.$message.error('请选择图片文件');
        }
      },
      handleExceed(files, fileList) {
        this.$message.warning(`当前限制选择 5 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
      },
      handleSuccess(res) {
        const {error, data, msg} = res
        if (error) return this.$message.error(msg)
        this.images.push(data.url)
        this.loading = false
      },
      imgclose(e) {
        this.images.splice(e, 1);
      },
      submitForm(ruleForm) {
        this.$refs[ruleForm].validate((valid) => {
          if (valid) {
            if (this.overStatus == "true") {
              this.$message.error("活动已结束,无法报名！")
            } else if (this.defaultStatus == "true") {
              this.$message.error("活动还未开启,无法报名！")
            } else {
              this.addPlayer()
            }
          } else {
            console.log('error submit!!');
            return false;
          }
        });
      },
      async addPlayer() {
        let params = {
          "activityId":  this.getUrlKey('activityId'),
          "name": this.ruleForm.name,
          "phone": this.ruleForm.phone,
          "declaration": this.ruleForm.declaration,
          "images": this.images,
          "openId": this.openId
        }
        const {error, msg, data, code} = await this.$api.h5.addPlayer(params)
        if (code == 200) {
          this.$message.success('报名成功，请耐心等待审核');
          this.$router.push({path: '/player', query: {id: data.id,activityId: this.getUrlKey('activityId')}});
        }
        if (error) return this.$message.error(msg)
      },
      /*async hasJoin(activityId, openId, id) {
        const {error, msg, data, code} = await this.$api.h5.hasJoin({activityId: activityId, openId: openId})
        if (data.hasJoin == 0) {
          this.$router.push({path: '/player', query: {id: id}});
        }
      }*/
    }
  }
</script>

<style lang="scss">
  #signUp {
    h2 {
      font-size: 0.6rem;
      border-bottom: 1px solid #ccc;
      height: 1.5rem;
      line-height: 1.5rem;
    }

    text-align: left;
    font-size: 0.55rem;

    p {
      font-size: 0.55rem
    }

    .el-upload--picture-card {
      height: 2rem;
      width: 2rem;
      line-height: 2rem;
    }

    .ruleForm {
      input[type=datetime-local], input[type=time], input[type=number], input[type=text], input[type=password], input[type=search], input[type=email], input[type=tel], input[type=url], input[type=date], select, textarea {
        min-height: auto;
      }

      .el-input__inner {
        margin: 0;
        height: 20px;
        line-height: 20px;
        font-size: 0.55rem;
      }

      .el-form-item {
        border-bottom: 1px solid #dcdfe6;
      }

      .el-textarea__inner {
        border: 0;
        padding: 0
      }

      .el-form-item__content {
        margin: 0
      }

      .el-textarea {
        margin-top: 10px
      }

      .el-form-item {
        margin: 10px 0
      }
    }

    .el-button--success {
      width: 100%
    }

    .el-upload--picture-card i {
      margin-top: 0.4rem
    }

    .el-upload-list--picture-card {
      display: none
    }

    .el-upload-list--picture-card .el-upload-list__item {
      width: 2rem;
      height: 2rem
    }

    .fd_list2 {
      font-size: 15px;
      line-height: 1.6em;
      margin: 0 auto;
      color: #555
    }

    .fd_list2 li {
      padding: 10px 15px 8px;
      overflow: hidden;
      border-bottom: 1px #d7d7d7 solid;
      position: relative;
      padding-left: 6em
    }

    @media(-webkit-min-device-pixel-ratio: 2) {
      .fd_list2 li {
        position: relative;
        border: 0 none;
        padding: 10px 15px 9px;
        padding-left: 6em
      }
      .fd_list2 li:before {
        content: "";
        position: absolute;
        bottom: 0;
        left: 0;
        width: 200%;
        border-top: 1px #d7d7d7 solid;
        -webkit-transform: scale(0.5);
        -ms-transform: scale(0.5);
        transform: scale(0.5);
        -webkit-transform-origin: 0 0;
        -ms-transform-origin: 0 0;
        transform-origin: 0 0;
        -webkit-box-sizing: border-box;
        box-sizing: border-box
      }
    }

    .fd_list2 li:last-child {
      border: none
    }

    .fd_list2 li .tlt {
      position: absolute;
      width: 5em;
      top: 10px;
      left: 5px;
    }

    .fd_list2 li .cont {
      color: #ccc;
      word-wrap: break-word;
      min-height: 26px;
      float: left;
    }

    .fd_list2 li .cont select {
      width: 100%;
      color: #999;
      font-size: 15px;
      border: none;
      height: 24px;
      outline: none;
      -webkit-appearance: none;
      -webkit-border-radius: 0px;
      background-size: auto 100%
    }

    .fd_list2 li .cont .tx {
      border: none;
      height: 24px;
      line-height: 24px;
      width: 100%;
      padding: 0;
      outline: none;
      vertical-align: top;
      font-size: 15px;
      min-height: auto;
      margin: 0;
    }

    .fd_list2 li .cont .ta {
      border: none;
      height: 3.6em;
      line-height: 1.2em;
      width: 100%;
      padding: 3px 0 !important;
      outline: none;
      margin: 0;
      font-size: 15px
    }

    .icon-add {
      width: 200px;
      height: 200px;
      border: 1px dashed #9a9ba3;
      overflow: hidden;
      display: flex;
      margin-top: 20px;
    }

    .icon-add:before {
      content: '';
      position: absolute;
      width: 50px;
      height: 2px;
      left: 50%;
      top: 50%;
      margin-left: -25px;
      margin-top: -1px;
      background-color: #aaabb2;
    }

    .icon-add:after {
      content: '';
      position: absolute;
      width: 2px;
      height: 50px;
      left: 50%;
      top: 50%;
      margin-left: -1px;
      margin-top: -25px;
      background-color: #aaabb2;
    }

    .icon-po {
      position: relative;
      width: 2rem;
      height: 2rem;
      margin-right: 10px;

      img {
        width: 2rem;
        height: 2rem;

      }
    }

    .icon-close {
      position: absolute;
      right: 5%;
      top: -10px;
      width: 20px;
      height: 20px;
      border-radius: 50%;
      background: #586067;
      color: #fff;
      font-size: 20px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .fl {
      float: left
    }

  }

</style>
