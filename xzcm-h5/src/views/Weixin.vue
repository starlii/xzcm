<template>
  <div></div>
</template>

<script>
  export default {
    data() {
      return {
        code: this.getUrlKey('code')
      }
    },
    created() {
      this.getOpenid(this.code)
    },
    methods: {
      getUrlKey: function (name) { // 获取url 参数
        return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ''])[1].replace(/\+/g, '%20')) || null
      },
      async getOpenid(code) {
        if (this.code) {
          let {error, data, msg} = await this.$api.weChat.getOpenId({code: code})
          if (error) return this.$message.error(msg)
          data = JSON.parse(data)
          this.$cookies.set('openId', data.openid, '0')
          this.$cookies.set('activityId', this.getUrlKey('activityId'), '0')
          let id = this.getUrlKey('id')
          if (id) {
            this.$router.push({path: '/player', query: {activityId: this.getUrlKey('activityId'), id: id}})
          } else {
            this.$router.push({path: '/index', query: {activityId: this.getUrlKey('activityId')}})
          }

        }
      }
    }
  }
</script>

<style scoped>

</style>
