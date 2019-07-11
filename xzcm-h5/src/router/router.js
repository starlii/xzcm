/**
 * Created by wty on 2017/10/10.
 */

import Home from '@/views/Home.vue'// 同步加载方式
const routes = [
  {
    path: '/home',
    name: 'home',
    component: Home,
    meta: {
      title: '首页'
    },
  },

  {
    path: '/index',
    name: 'index',
    meta: {
      title: '首页'
    },
    component: resolve => {
      require(['../views/Index.vue'], resolve)
    } //异步加载方式
  },
  {
    path: '/pink',
    name: 'pink',
    meta: {
      title: '排名'
    },
    component: resolve => {
      require(['../views/Pink.vue'], resolve)
    } //异步加载方式
  },
  {
    path: '/Introduce',
    name: 'introduce',
    meta: {
      title: '活动介绍'
    },
    component: resolve => {
      require(['../views/Introduce.vue'], resolve)
    } //异步加载方式
  },
  {
    path: '/Prize',
    name: 'prize',
    meta: {
      title: '活动奖品'
    },
    component: resolve => {
      require(['../views/Prize.vue'], resolve)
    } //异步加载方式
  },
  {
    path: '/player',
    name: 'player',
    meta: {
      title: '选手介绍'
    },
    component: resolve => {
      require(['../views/Player.vue'], resolve)
    } //异步加载方式
  },
  {
    path: '/Complaint',
    name: 'complaint',
    meta: {
      title: '投诉'
    },
    component: resolve => {
      require(['../views/Complaint.vue'], resolve)
    } //异步加载方式
  },
  {
    path: '/ComplaintSecond',
    name: 'complaintSecond',
    meta: {
      title: '投诉'
    },
    component: resolve => {
      require(['../views/ComplaintSecond.vue'], resolve)
    } //异步加载方式
  },
  {
    path: '/ComplaintDone',
    name: 'complaintDone',
    meta: {
      title: '投诉'
    },
    component: resolve => {
      require(['../views/ComplaintDone.vue'], resolve)
    } //异步加载方式
  },
  {
    path: '/SendDiamond',
    name: 'sendDiamond',
    meta: {
      title: '打赏钻石'
    },
    component: resolve => {
      require(['../views/SendDiamond.vue'], resolve)
    } //异步加载方式
  },
  {
    path: '/SignUp',
    name: 'signUp',
    meta: {
      title: '报名'
    },
    component: resolve => {
      require(['../views/SignUp.vue'], resolve)
    } //异步加载方式
  },
  {
    path: '/VoteSuccess',
    name: 'VoteSuccess',
    meta: {
      title: '投票成功'
    },
    component: resolve => {
      require(['../views/VoteSuccess.vue'], resolve)
    } //异步加载方式
  },
  {
    path: '/EndActivity',
    name: 'endActivity',
    meta: {
      title: '活动结束'
    },
    component: resolve => {
      require(['../views/EndActivity.vue'], resolve)
    } //异步加载方式
  },
  {
    path: '/weixin',
    name: 'Weixin',
    meta: {
      title: ''
    },
    component: resolve => {
      require(['../views/Weixin.vue'], resolve)
    } //异步加载方式
  },
  {
    path: '/PayError',
    name: 'PayError',
    meta: {
      title: '支付失败'
    },
    component: resolve => {
      require(['../views/PayError.vue'], resolve)
    } //异步加载方式
  },
  {
    path: '/PaySucc',
    name: 'PaySucc',
    meta: {
      title: '支付成功'
    },
    component: resolve => {
      require(['../views/PaySucc.vue'], resolve)
    } //异步加载方式
  },
  {
    path: '/SignUpAfter',
    name: 'SignUpAfter',
    meta: {
      title: '已经报过名'
    },
    component: resolve => {
      require(['../views/SignUpAfter.vue'], resolve)
    } //异步加载方式
  },
  //404页面
  {
    path: '*',
    name: '404',
    component: resolve => {
      require(['../views/common/Error.vue'], resolve)
    }
  },
]

export default routes
