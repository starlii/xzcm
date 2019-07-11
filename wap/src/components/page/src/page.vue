<template>
  <div class="sst-page">
    <div class="page-title">
      <slot name="page-title"/>
    </div>
    <!--default slot-->
    <div class="page-content"><slot/></div>
  </div>
</template>

<script>
export default {
  name: 'StarPage',
  data() {
    return {

    }
  }
}
</script>

<style lang="scss" scoped>
  .el-pagination{width: 100%; padding: 0}
</style>
