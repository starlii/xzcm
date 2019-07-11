<template>
  <span>{{ time }}</span>
</template>

<script>
// import dayjs from 'dayjs'
export default {
  name: 'TimeDown',
  props: {
    start: { type: String, default: null },
    end: { type: String, default: null }
  },
  data() {
    return {
      time: null
    }
  },
  created() {
    // const date1 = dayjs('2019-01-25')
    // const date2 = dayjs('2018-06-05')
    // this.time = dayjs() // 20214000000
    // setInterval(() => {
    //   const date1 = dayjs('2019-01-25');
    //   const date2 = dayjs('2018-06-05');
    //   date1.diff(date2); // 20214000000
    //   this.time = dayjs(this.end).diff(dayjs(this.end))
    // }, 1000)
  }
}
</script>

<style scoped>

</style>
