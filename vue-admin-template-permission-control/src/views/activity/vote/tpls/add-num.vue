<template>
  <span>
    <el-input v-model="value" class="w50 input-num" type="number" min="0"/>
    <el-button class="ml5" type="primary" @click="handleChange">加</el-button>
  </span>
</template>

<script>
export default {
  name: 'AddNum',
  data() {
    return {
      value: 0
    }
  },
  methods: {
    handleChange() {
      this.$emit('change', this.value)
    }
  }
}
</script>

<style scoped>

</style>
