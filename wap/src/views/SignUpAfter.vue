<template>
  <div class="signUpAfter">
    <div class="tu">
      <img src="../assets/images/error.png">
      <h2>您已报名</h2>
      <p>{{count}}秒后跳转 </p>
    </div>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        count: 3
      }
    },
    created() {
      this.goGrdoupRecor()
    },
    methods: {
      goGrdoupRecor() {
        const time_count = 3;
        if (!this.timer) {
          this.count = time_count;
          this.timer = setInterval(() => {
            if (this.count > 0 && this.count <= time_count) {
              this.count--;
            } else {
              clearInterval(this.timer);
              this.timer = null;
              if (this.$route.query.name == 'index') {
                this.$router.push({path: '/index', query: {activityId: this.$route.query.activityId}});
              } else {
                this.$router.push({path: '/player', query: {id: this.$route.query.id,activityId: this.$route.query.activityId}});
              }
            }
          }, 1000)
        }
      },
    }
  }
</script>

<style lang="scss">
  .signUpAfter {
    background: url("../assets/images/tip.jpg") no-repeat;
    position: fixed;
    width: 100%;
    height: 100%;
    .tu {
      top: 50%;
      position: fixed;
      margin-top: -130px;
      width: 100%
    }
    h2 {
      font-size: 0.6rem
    }
  }
</style>
