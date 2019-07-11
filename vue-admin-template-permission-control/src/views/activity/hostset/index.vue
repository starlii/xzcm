<template>
  <star-page>
    <template slot="header">域名设置</template>
    <el-form label-width="160px" label-suffix=":" label-position="right">
      <el-form-item label="多域名跳转 泛解析域名(以英文的逗号隔开)">
        <el-input v-model="domains" class="w400"/>
      </el-form-item>
      <el-form-item label="">
        <el-button type="primary" @click="handleSetDomain">确定</el-button>
        <el-button type="info" @click="$router.go(-1)">取消</el-button>
      </el-form-item>
      <el-form-item label="说明">
        <div class="description info">
          1, 泛解析域名可以随便添加,添加前要先把域名解析到本系统服务器上并绑定本系统目录. <br>
          2, 泛解析域名设置时，随机生成多级域名，每位用户每次访问随机一个域名，比如：设置泛域名为abc.qq.com ，随机域名会变为：xxx.abc.qq.com <br>
          3, 设置多域名随机跳转后分享自定义图标和描述将不能使用,除非绑定多域名的js接口. <br>
          4, 不用输入http:// <br>
          <span class="bold">(需要把*.abc.qq.com解析到本系统服务器上并绑定本系统目录.)
          随机域名作用：尽量减少主域名暴露给用户，结合泛域名，每位用户访问的域名都不一样，被举报时，官方收到的域名不一样，减少被封的可能，被封时，也是封其中一个随机域名，保障主域名的安全。</span>
        </div>
        <!--<div class="mt10 info warning">-->
        <!--永久二维码域名:单独使用一个主域名做泛解析到本服务器上并绑定本系统目录,每个活动设置泛解析的具体域名,就会生成此活动的永久二维码,这样让选手保存此二维码后,如果活动绑定的域名被封,通过扫这个二维码就会自动跳转到活动的新域名上,以便选手正常参与活动. 如添加泛解析域名 *.aaa.com,这里设置 bbb.aaa.com,每个活动设置的永久二维码域名要唯一. 不用输入http://-->
        <!--</div>-->
      </el-form-item>
    </el-form>
  </star-page>
</template>

<script>
export default {
  name: 'Index',
  data() {
    return {
      domains: null
    }
  },
  methods: {
    async handleSetDomain() {
      const { error, msg } = await this.$api.domain.setDomian({
        domains: this.domains,
        activityId: this.$route.query.activityIid
      })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.$router.go(-1)
    }
  }
}
</script>

<style lang="scss" scoped>
.description{
    background-color: #dff0d8;
    padding: 5px;
    .bold{
        font-weight: bold;
        color: #303133;
    }
}
    .warning{
        padding: 5px;
        background-color: #FCF8E3;
    }
</style>
