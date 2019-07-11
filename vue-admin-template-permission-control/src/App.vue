<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App',
  async created() {
    const { error, data } = await this.$api.platForm.getBasicInfo()
    if (error) return this.$message.error('获取平台信息失败！')
    document.title = data.platformName
  }
}
</script>
