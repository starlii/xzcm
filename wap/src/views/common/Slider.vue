<template>
  <div>
    <!--幻灯片 images:750*400-->
    <div v-if="list.length>0">
      <div id="aui-slide3" style="height: 260px">
        <div class="aui-slide-wrap">
          <div class="aui-slide-node bg-dark" v-for="(item,index) in list" v-loading="loading">
            <div v-if="hasStar==true">
              <p v-if="index==0">今日之星</p>
            </div>
            <img :src="item" width="100%" height="260">
          </div>
        </div>
        <div class="aui-slide-page-wrap"><!--分页容器--></div>
      </div>
    </div>
  </div>
</template>
<script>
  export default {
    name: "Slider",
    data() {
      return {
        msg: 'hello vue',
        list: [],
        activityId:this.getUrlKey('activityId'),
        hasStar: '',
        loading:true,
        includedComponents: "Slider"
      }
    },
    /*props: {
      list: {
        type: Array,
        required: false
      },
    },*/
    watch: {
      list: function (val) {
        this.renderSlider()
      }
    },
    methods: {
      renderSlider() {
        setTimeout(function () {
          var slide3 = new auiSlide({
            container: document.getElementById("aui-slide3"),
            // "width":300,
            "height": 260,
            "speed": 300,
            "autoPlay": 3000, //自动播放
            "pageShow": true,
            "loop": true,
            "pageStyle": 'dot',
            'dotPosition': 'center'
          });
        }, 500);

      },
      async getImages() {
        if (!this.activityId) {
          this.activityId =  this.$cookies.get("activityId")
        }
        const {error, msg, data} = await this.$api.h5.getImages({activityId: this.activityId})
        this.list = data.images
        this.loading=false
        this.hasStar = data.hasStar
        if (error) return this.$message.error(msg)
      },
      getUrlKey: function (name) { // 获取url 参数
        return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ''])[1].replace(/\+/g, '%20')) || null
      },
    },
    mounted: function () {
      this.$nextTick(function () {
        // 代码保证 this.$el 在 document 中
        let _this = this;
        this.getImages()
        if (!(_this.list && _this.list.length === 0)) {
          console.log((document.documentElement.clientWidth || document.body.clientWidth) * 400 / 750)
          _this.renderSlider();
        }
      })
    },

  }
</script>
<style type="text/css">
  .aui-slide-wrap {
    line-height: 220px
  }

  .aui-slide-wrap img {
    width: 100%;
  }

  .aui-slide-node p {
    width: 100%;
    text-align: center;
    color: #f00;
    position: absolute;
    top: 0;
    background: #000;
    opacity: 0.6;
  }
</style>
