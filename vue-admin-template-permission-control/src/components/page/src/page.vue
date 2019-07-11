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
  .sst-page{
    padding: 15px;
    height: 100%;
    display: flex;
    flex-direction: column;
    .page-content{
      flex: 1;
    }
    .page-footer {
      height: 32px;
      text-align: center;
    }
  }
</style>
