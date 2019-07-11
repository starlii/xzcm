<template>
   <div class="aui-font-size-14 pd-10 cl-999 aui-text-center"  v-show="show"> <span class="ring-1"></span> {{tips}}</div>
</template>
<style lang="less">
  .ring-1 {
    display: inline-block;
    width: .8rem;
    height: .8rem;
    border: 0.1rem dashed #53a7fe;
    border-radius: 100%;
    vertical-align: middle;
  }
  @-webkit-keyframes myloading{
    0% {-webkit-transform: rotate(0deg);}
    50% {-webkit-transform: rotate(180deg);}
    100% {-webkit-transform: rotate(360deg);}
  }
  @keyframes myloading {
    0% {transform: rotate(0deg);}
    50% {transform: rotate(180deg);}
    100% {transform: rotate(360deg);}
  }

  .ring-1 {animation: myloading 1.5s linear infinite ;}
</style>

<script>
    export default{
        data(){
            return{

            }
        },
        props:{
          tips:{
            type:String,
            default:"正在加载中"
          	},
           show:{
            type:Boolean,
            default:true,
  	      }
        },
         mounted: function () {
           console.log(self.show)
         }
    }
</script>
