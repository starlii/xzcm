<template>
  <marquee class="notice"  direction="left" align="middle"> <i class="iconfont icon-laba cl-white" style="margin-top: 5px;">{{tips}}</i> </marquee>
</template>
<script>
    export default{
        data(){
            return{ }
        },
        props:{
           tips:String,
        }
    }
</script>
