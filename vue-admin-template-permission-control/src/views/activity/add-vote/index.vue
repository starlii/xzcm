<template>
  <star-page>
    <el-collapse v-model="activeNames">
      <el-collapse-item title="活动设置" name="1">
        <el-form label-width="200px" label-position="right" label-suffix=":">
          <el-form-item label="活动标题">
            <el-input v-model.trim="activityName" style="width: 600px"/>
            <span class="red">*</span>
            <span v-if="!activityName" class="info red">(必填字段)</span>
          </el-form-item>
          <el-form-item label="报名时间">
            <el-date-picker
              v-model="activitySetting.applyTime"
              type="datetimerange"
              value-format="yyyy-MM-dd HH:mm:ss"
              range-separator="到"
              start-placeholder="开始日期"
              end-placeholder="结束日期"/>
            <span class="red">*</span>
            <span v-if="!activitySetting.applyTime" class="info red">(必填字段)</span>
          </el-form-item>
          <el-form-item label="投票时间">
            <el-date-picker
              v-model="activitySetting.voteTime"
              type="datetimerange"
              value-format="yyyy-MM-dd HH:mm:ss"
              range-separator="到"
              start-placeholder="开始日期"
              end-placeholder="结束日期"/>
            <span class="red">*</span>
            <span v-if="!activitySetting.voteTime" class="info red">(必填字段)</span>
          </el-form-item>
          <el-form-item label="投票时间结束后可送达礼物">
            <div class="flex flex-column">
              <el-input v-model="activitySetting.endGiftTime" class="w200 input-num" type="number" min="0">
                <template slot="append">分钟</template>
              </el-input>
              <span class="info">投票时间结束后还可以送几分钟的礼物并加票！必填或 为 0 不启动此功能</span>
            </div>
          </el-form-item>
          <el-form-item label="备注1">
            <div class="flex flex-column">
              <el-input v-model.trim="activityRemark" class="w400 input-num"/>
              <span class="info">活动备注。</span>
            </div>
          </el-form-item>
          <el-form-item label="备注2">
            <div class="flex flex-column">
              <el-input v-model.trim="activityRemarkSec" class="w400 input-num"/>
              <span class="info">活动备注。</span>
            </div>
          </el-form-item>
        </el-form>
      </el-collapse-item>
      <el-collapse-item title="规则设置" name="2">
        <el-form label-width="200px" label-position="right" label-suffix=":">
          <el-form-item label="最小人数">
            <div class="flex flex-column">
              <el-input v-model="ruleSetting.minPlayers" type="number" min="0" class="w200 input-num">
                <template slot="append">人</template>
              </el-input>
              <span class="info">到达最小报名人数后，才能够投票！ 建议到达参赛人数后，把上面设置为 0， 提高性能！</span>
            </div>
          </el-form-item>
          <el-form-item label="每人每日每用户">
            <div class="flex flex-column">
              <el-input v-model="ruleSetting.userLimit" type="number" min="0" class="w200 input-num">
                <template slot="append">票</template>
              </el-input>
              <span class="info">每人每天最多给每个用户投多少票。</span>
            </div>
          </el-form-item>
          <el-form-item label="每日每人">
            <div class="flex flex-column">
              <el-input v-model="ruleSetting.dayLimit" type="number" min="0" class="w200 input-num">
                <template slot="append">票</template>
              </el-input>
              <span class="info">每人每天最多投多少票， 当天给所有人投票的总数， 填 0 为不限制（送礼物投票不影响）</span>
            </div>
          </el-form-item>
          <el-form-item label="每人最多">
            <div class="flex flex-column">
              <el-input v-model="ruleSetting.sumLimit" type="number" min="0" class="w200 input-num">
                <template slot="append">票</template>
              </el-input>
              <span class="info">每人最多投票总数， 填 0 为不限制。</span>
            </div>
          </el-form-item>
          <el-form-item label="投票时间间隔">
            <div class="flex flex-column">
              <el-input v-model="ruleSetting.voteInterval" type="number" min="0" class="w200 input-num">
                <template slot="append">分钟</template>
              </el-input>
              <span class="info">一个微信给同一个选手投票时间最小间隔， 填 0 为不限制。</span>
            </div>
          </el-form-item>
          <el-form-item label="每人最多送礼物">
            <div class="flex flex-column">
              <el-input v-model="ruleSetting.giftLimit" type="number" min="0" class="w200 input-num">
                <template slot="append">元</template>
              </el-input>
              <span class="info">每人最多投票总数， 填 0 为不限制。</span>
            </div>
          </el-form-item>
          <el-form-item label="自动锁定">
            <div class="flex flex-column">
              <div>
                <span class="star-input-label">每</span><input v-model="ruleSetting.lockCondition" type="number" class="star-input"><span class="star-input-label">分钟超过</span><input v-model="ruleSetting.localVotes" class="star-input" type="number"><span class="star-input-label">票，锁定</span><input v-model="ruleSetting.lockTime" type="number" class="star-input"><span class="star-input-label">分钟</span>
              </div>
              <span class="info">防刷票,达到条件后自动锁定设定的分钟数,锁定时不能投票. 留空为关闭此功能</span>
            </div>
          </el-form-item>
        </el-form>
      </el-collapse-item>
      <el-collapse-item title="投票设置" name="3">
        <el-form label-width="200px" label-suffix=":" label-position="right">
          <el-form-item label="轮播图">
            <el-upload
              ref="upload"
              :before-upload="beforeAvatarUpload"
              :on-preview="handlePictureCardPreview"
              :on-remove="handleRemove"
              :on-success="handleSuccess"
              :headers="{authorization: token}"
              :action="uploadUrl"
              :limit="8"
              :file-list="fileList"
              list-type="picture-card">
              <i class="el-icon-plus"/>
            </el-upload>
            <el-dialog :visible.sync="dialogVisible">
              <img :src="dialogImageUrl" width="100%" alt="">
            </el-dialog>
          </el-form-item>
          <el-form-item label="每日之星功能">
            <div class="flex flex-column">
              <el-switch
                v-model="voteSetting.isStar"
                active-text="开启"
                inactive-text="关闭"
                active-color="#13ce66"
                inactive-color="#ff4949"/>
              <span>选择"ON"时,每天早上10点会把当天票数最高的选手的第一张图片替换到上面设置的轮播图的第二个轮播图位置,选择"OFF"不启用此功能</span>
            </div>
          </el-form-item>
          <el-form-item label="活动规则">
            <!--<el-input v-model="voteSetting.activityRule"/>-->
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
              v-model="voteSetting.activityRule"
              :options="editorOption"
            />
          </el-form-item>
          <el-form-item label="奖品介绍">
            <!--<el-input v-model="voteSetting.giftDes"/>-->
            <el-upload
              ref="quillUpload2"
              :headers = "{authorization: token}"
              :action="uploadUrl"
              :show-file-list="false"
              :on-success="uploadSuccessDes2"
              :on-error="uploadErrorDes"
              :before-upload="beforeAvatarUpload"
              class="avatar-uploader"
            >
              <el-button v-show="false" id="quillUpload2" size="small" type="primary">点击上传</el-button>
            </el-upload>
            <quill-editor
              ref="myQuillEditor2"
              v-model="voteSetting.giftDes"
              :options="editorOption2"
            />
          </el-form-item>
        </el-form>
      </el-collapse-item>
      <el-collapse-item title="界面设置" name="4">
        <el-form label-width="200px" label-suffix=":" label-position="right">
          <!--todo: 三级权限-->
          <el-form-item v-permission label="虚拟浏览量">
            <div class="flex flex-column">
              <el-input v-model="interfaceSetting.visualViews" class="w200 input-num" type="number" min="0">
                <template slot="append">流量</template>
              </el-input>
              <span class="info">首页显示 = 真实显示 + 虚拟浏览量</span>
            </div>
          </el-form-item>
          <el-form-item label="排行榜数量">
            <div class="flex flex-column">
              <el-input v-model="interfaceSetting.leaderboardNum" class="w200 input-num" type="number" min="0">
                <template slot="append">个</template>
              </el-input>
              <span class="info">设置排行榜显示选手最大数量</span>
            </div>
          </el-form-item>
          <el-form-item label="滚动广告">
            <div class="flex flex-column">
              <el-input v-model="interfaceSetting.announcement" class="w400" type="textarea" min="0"/>
              <span class="info">活动首页最上方滚动公告， 不填不显示。</span>
            </div>
          </el-form-item>
          <el-form-item label="主题颜色">
            <div class="flex f-a-center">
              <span :style="{'backgroundColor': '#' + interfaceSetting.themeColor}" class="h5-theme-color mr50 center">{{ interfaceSetting.themeColor }}</span>
              <span
                v-for="(item, index) in colorList"
                :key="index"
                :style="{'backgroundColor': '#' + item}"
                class="color-item"
                @click="interfaceSetting.themeColor = item"/>
            </div>
          </el-form-item>
        </el-form>
      </el-collapse-item>
      <el-collapse-item title="报名设置" name="5">
        <el-form label-width="200px" label-suffix=":" label-position="right">
          <el-form-item label="最少上传图片">
            <el-input v-model="applySetting.miniImage" type="number" class="w200 input-num">
              <template slot="append">张</template>
            </el-input>
          </el-form-item>
          <el-form-item label="最多上传图片">
            <el-input v-model="applySetting.maxImage" type="number" class="w200 input-num">
              <template slot="append">张</template>
            </el-input>
          </el-form-item>
          <!--<el-form-item label="报名字段">-->
          <!--<el-input v-model="applySetting.applyField" class="w200" placeholder="姓名"/>-->
          <!--</el-form-item>-->
          <el-form-item label="报名页面提示文字">
            <el-input v-model="applySetting.applyTips" type="textarea" placeholder="请如实填写报名信息，提交信息为唯一获奖凭证"/>
          </el-form-item>
        </el-form>
      </el-collapse-item>
      <el-collapse-item title="礼物设置" name="6">
        <el-form label-position="right" label-suffix=":" label-width="200px" class="posR">
          <el-button icon="el-icon-plus" type="primary" class="addGift" @click="giftSetting.giftPrice.push(1)"> 添加
          </el-button>
          <el-form-item label="礼物设置">
            <div class="flex">
              <div v-for="(item, index) in giftSetting.giftPrice" :key="index" class="flex flex-column gift-item">
                <span>名称： <span>钻石</span></span>
                <span class="mt5 mb5">价格： <el-input-number v-model="giftSetting.giftPrice[index]" :min="1" type="number" class="w100"/></span>
                <span>票数： <el-button type="text">{{ giftSetting.giftPrice[index] * 3 }}</el-button></span>
                <div v-if="giftSetting.giftPrice.length>1" class="w50">
                  <el-button icon="el-icon-minus" type="danger" @click="giftSetting.giftPrice.splice(index,1)"> 删除
                  </el-button>
                </div>
              </div>
            </div>
          </el-form-item>
        </el-form>
      </el-collapse-item>
      <el-form class="mt20" label-width="200px" label-suffix=":" label-position="right">
        <el-form-item label="活动状态">
          <div class="flex flex-column">
            <el-switch
              v-model="approvalStatus"
              active-color="#13ce66"
              inactive-color="#ff4949"
              active-text="开启"
              inactive-text="关闭"/>
            <span class="info">活动开启或者关闭</span>
          </div>
        </el-form-item>
        <el-form-item label="">
          <el-button :disabled="!validate" type="primary" @click="handleSubmit">{{ pageType === 'add' ? '添加' : '更改' }}
          </el-button>
          <el-button type="info" @click="$router.go(-1)">取消</el-button>
        </el-form-item>
      </el-form>
    </el-collapse>
  </star-page>
</template>

<script>
import { getToken } from '@/utils/auth'

const BASE_API = process.env.BASE_API
export default {
  data() {
    const options = [
      [{ 'size': ['small', false, 'large'] }],
      [{ 'color': [] }, { 'background': [] }],
      [{ 'font': [] }],
      [{ 'align': [] }],
      ['bold', 'italic'],
      [{ 'list': 'ordered' }, { 'list': 'bullet' }],
      ['link', 'image']
    ]
    return {
      fileList: [],
      activityId: null,
      pageType: 'add',
      colorList: ['6281fd', '29abdd', 'ff7f24', 'ff6a6a', 'ff4040', 'a4d3ee', 'ffb6c1', '473cbb', 'a2cd5a'],
      token: getToken(),
      dialogImageUrl: null,
      dialogVisible: false,
      activeNames: ['1', '2', '3', '4', '5', '6'],
      activityName: null,
      activitySetting: {
        applyTime: null,
        voteTime: null,
        endGiftTime: null
      },
      activityRemark: null,
      activityRemarkSec: null,
      ruleSetting: {
        minPlayers: 0,
        userLimit: 1,
        dayLimit: 3,
        sumLimit: 0,
        voteInterval: 0,
        giftLimit: 0,
        lockCondition: null,
        localVotes: null,
        lockTime: null
      },
      voteSetting: {
        'activityRule': null,
        'giftDes': null,
        'isStar': true
      },
      interfaceSetting: {
        announcement: null,
        visualViews: null,
        leaderboardNum: null,
        isPoster: false,
        themeColor: '6281fd'
      },
      applySetting: {
        'applyTips': null,
        'maxImage': 8,
        'miniImage': 1
      },
      giftSetting: {
        'giftPrice': [1]
      },
      images: [],
      approvalStatus: false,
      content: null,
      imageLoading: false,
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
      },
      editorOption2: {
        modules: {
          toolbar: {
            container: options, // 工具栏
            handlers: {
              'image': function(value) {
                if (value) {
                  document.querySelector('#quillUpload2').click()
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
      return !!this.activitySetting.applyTime && !!this.activitySetting.voteTime
    },
    uploadUrl() {
      return BASE_API + '/api/fileUploadController/uploadFileGrid?type=0'
    }
  },
  created() {
    this.activityId = this.$route.query.activityId

    if (this.activityId) {
      this.pageType = this.$route.query.flag
      this.getActivityDetail({ activityId: this.$route.query.activityId })
    }
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
    uploadSuccessDes2(res) {
      const quill = this.$refs.myQuillEditor2.quill
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
    handleSubmit() {
      const params = {
        images: this.images,
        activityName: this.activityName,
        activityRemark: this.activityRemark,
        activityRemarkSec: this.activityRemarkSec,
        activitySetting: {
          'applyEndTime': this.activitySetting.applyTime[1],
          'applyStartTime': this.activitySetting.applyTime[0],
          'endGiftTime': this.activitySetting.endGiftTime,
          'voteEndTime': this.activitySetting.voteTime[1],
          'voteStartTime': this.activitySetting.voteTime[0]
        },
        ruleSetting: this.ruleSetting,
        voteSetting: {
          'activityRule': this.voteSetting.activityRule,
          'giftDes': this.voteSetting.giftDes,
          'isStar': this.voteSetting.isStar ? 0 : 1
        },
        interfaceSetting: {
          announcement: this.interfaceSetting.announcement,
          visualViews: this.interfaceSetting.visualViews,
          leaderboardNum: this.interfaceSetting.leaderboardNum,
          isPoster: this.interfaceSetting.isPoster ? 0 : 1,
          themeColor: this.interfaceSetting.themeColor
        },
        applySetting: this.applySetting,
        giftSetting: this.giftSetting,
        approvalStatus: this.approvalStatus ? 0 : 1
      }
      this.addActivity(params)
    },

    async addActivity(params) {
      const apiProp = this.pageType === 'add' ? 'addActivity' : 'update'
      params = this.pageType === 'add' ? params : { ...params, ...{ activityId: this.activityId }}
      const { error, data, msg } = await this.$api.activity[apiProp](params)
      if (error) return this.$message.error(data)
      this.$message.success(msg)
      this.$router.push({ name: 'vote' })
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 5

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG 格式 或者 PNG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 5MB!')
      }
      return isJPG && isLt2M
    },
    handleRemove(file) {
      const index = this.images.findIndex(item => item === file.url)
      if (index > -1) return this.images.splice(index, 1)
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url
      this.dialogVisible = true
    },
    handleSuccess(res) {
      const { error, data, msg } = res
      if (error) return this.$message.error(msg)
      this.images.push(data.url)
    },
    async getActivityDetail({ activityId }) {
      const { error, data, msg } = await this.$api.activity.getActivityDetail({ activityId, type: 1 })
      if (error) return this.$message.error(msg)
      const rootData = ['images', 'activityName', 'activityRemark', 'activityRemarkSec']
      rootData.map(item => {
        this[item] = data[item]
      })
      this.fileList = data.images.map(item => ({ name: Math.random() * 5 + 'img', url: item }))

      this.approvalStatus = data.approvalStatus === 0
      // 活动设置
      this.$set(this.activitySetting, 'applyTime', [data.activitySetting.applyStartTime, data.activitySetting.applyEndTime])
      this.$set(this.activitySetting, 'voteTime', [data.activitySetting.voteStartTime, data.activitySetting.voteEndTime])
      this.$set(this.activitySetting, 'endGiftTime', data.activitySetting.endGiftTime)

      // 规则设置 ruleSetting
      const ruleArr = ['minPlayers', 'userLimit', 'dayLimit', 'sumLimit', 'voteInterval', 'giftLimit', 'lockCondition', 'localVotes', 'lockTime']
      ruleArr.map(item => {
        this.$set(this.ruleSetting, item, data.ruleSetting[item])
      })

      // 投票设置 voteSetting
      const voteArr = ['activityRule', 'giftDes']
      voteArr.map(item => {
        this.$set(this.voteSetting, item, data.voteSetting[item])
      })
      this.$set(this.voteSetting, 'isStar', !data.voteSetting.isStar)

      // 界面设置 interfaceSetting
      const interArr = ['announcement', 'visualViews', 'leaderboardNum', 'themeColor']
      interArr.map(item => {
        this.$set(this.interfaceSetting, item, data.interfaceSetting[item])
      })
      this.$set(this.interfaceSetting, 'isStar', !data.interfaceSetting.isStar)

      // 选手设置 applySetting
      const applyArr = ['applyTips', 'maxImage', 'miniImage']
      applyArr.map(item => {
        this.$set(this.applySetting, item, data.applySetting[item])
      })

      // 礼物设置 giftSetting
      this.$set(this.giftSetting, 'giftPrice', data.giftSetting['giftPrice'] || [])
    }
  }
}
</script>

<style lang="scss" scoped>
  .h5-theme-color {
    width: 180px;
    height: 34px;
    line-height: 34px;
  }

  .color-item {
    width: 20px;
    height: 20px;
    border-radius: 5px;
    cursor: pointer;
    margin-left: 5px;
  }

  .gift-item:not(:first-of-type) {
    margin-left: 30px;
  }

  .posR {
    position: relative
  }

  .addGift {
    position: absolute;
    left: 110px;
    top: 30px
  }
</style>
