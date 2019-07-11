<template>
  <div id="error">
    <div class="aui-padded-t-15 cl-666">404找不到页面。。。</div>
  </div>
</template>
<style lang="scss">
#error{
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 999;
  background: #fff;
}
</style>
<script>
  import BottomNav from './BottomNav.vue';

  export default {
    name: 'Error',
    data() {
      return {
        msg: 'hello vue'
      }
    },
    components: {
      BottomNav
    }
  }
</script>
