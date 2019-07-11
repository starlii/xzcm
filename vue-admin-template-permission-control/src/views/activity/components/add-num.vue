<template>
  <span>
    <el-input v-model="value" class="w50 input-num" type="number" @keyup.enter.native="handleChange"/>
    <el-button class="ml5" type="primary" @click="handleChange">加</el-button>
  </span>
</template>

<script>
export default {
  name: 'AddNum',
  data() {
    return {
      value: 1
    }
  },
  methods: {
    handleChange() {
      this.$emit('change', this.value)
      this.value = 1
    }
  }
}
</script>

<style scoped>

</style>
