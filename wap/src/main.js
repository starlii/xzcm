// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.

require('./assets/css/aui.2.0.css');
require('./assets/css/aui-slide.css');
require('./assets/css/iconfont.css');
require("./assets/css/main.less");
require('./assets/css/blue.less');
require('./assets/lib/rem');
require('./assets/lib/aui-slide');

import Vue from 'vue'
import App from './App'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import VueWechatTitle from 'vue-wechat-title'
import WeChat from '@/plugins/weChat'
import api from '@/api'
Vue.use(VueWechatTitle)
Vue.use(WeChat)
Vue.use(api)


import VueLazyLoad from 'vue-lazyload'
Vue.use(VueLazyLoad,{
  preLoad: 1,
  error: '/static/lazy1.png',
  loading: '/static/lazy1.png',
  attempt: 100
})

Vue.use(ElementUI);
//import router from './router'
import routerConfig from './router/router'
import Router from 'vue-router'
Vue.use(Router)
import VueResource from 'vue-resource'; //vue-router路由
Vue.use(VueResource)
Vue.config.productionTip = false
import VueCookies from 'vue-cookies'
Vue.use(VueCookies)


//路由
const router = new Router({
  hashbang: true,
  history: false,
  saveScrollPosition: true,
  transitionOnLoad: true,
  linkActiveClass: 'aui-active',
  routes :routerConfig// （缩写）相当于 routes: routes
});
//路由控制
router.beforeEach((to, from, next) => {
  window.scrollTo(0, 0);
  next()
});
router.afterEach((to, from, next) => {
  window.scrollTo(0, 0);

})

Vue.filter('formatDate',function (value,format) {
  if (!value) return ''
  value = value.toString();
  return formatDate(value,format);
  function formatDate(value,format){
    var newServerTime = new Date(value.replace("-", "/").replace("-", "/"));
    if(format == undefined){
      format = "MM/dd/yyyy hh:mm:ss";
    }
    var utc = newServerTime.getTime();
    var tempDate = utc +(360000 * (new Date().getTimezoneOffset()/(-60)));
    var newDate = new Date(tempDate);
    newServerTime = newDate;
    var o = {
      "M+": newServerTime.getMonth() + 1, //month
      "d+": newServerTime.getDate(), //day
      "h+": newServerTime.getHours(), //house
      "m+": newServerTime.getMinutes(), //min
      "s+": newServerTime.getSeconds(), //second
      "q+": Math.floor((newServerTime.getMonth() + 3) / 3), //Quarterly
      "S": newServerTime.getMilliseconds() //millisecond
    };
    if (/(y+)/.test(format))
      format = format.replace(RegExp.$1, (newServerTime.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
      if (new RegExp("(" + k + ")").test(format))
        format = format.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    if(value != null){
      return format;
    }else{
      return "";
    }
  }
})

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  template: '<App/>',
  components: { App }
})


