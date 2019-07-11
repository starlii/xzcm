<template>
  <div class="star-tab">
    <div v-for="(item, index) in tabList" :key="index" :class="current === item.value ? 'active' : ''" class="star-tab-item" @click="handleItem(item)">
      {{ item.num ? `${item.label} (${item.num})` : item.label }}
    </div>
  </div>
</template>

<script>
export default {
  name: 'Index',
  props: { tabList: { type: Array, default: () => [] }, active: { type: [String, Number], default: null }},
  data() {
    return {
      current: null
    }
  },
  watch: {
    active: {
      handler(val) {
        this.current = val
      },
      immediate: true
    }
  },
  methods: {
    handleItem(val) {
      this.current = val.value
      this.$emit('change', val)
    }
  }
}
</script>

<style lang="scss" scoped>
  .star-tab{
    background-color: #f5f7fa;
    border: 1px solid #e4e7ed;
    margin: 0;
    display: flex;
    .star-tab-item{
      cursor: pointer;
      padding:0px 20px;
      border: 1px solid transparent;
      margin: -1px -1px 0;
      color: #909399;
      height: 40px;
      box-sizing: border-box;
      line-height: 40px;
      display: inline-block;
      list-style: none;
      font-size: 14px;
      font-weight: 500;
      &.active{
        color: #409EFF;
        background-color: #fff;
        border-right-color: #dcdfe6;
        border-left-color: #dcdfe6;
        border-top-color: #dcdfe6;
        border-bottom-color: #fff;
      }
    }
  }
</style>
