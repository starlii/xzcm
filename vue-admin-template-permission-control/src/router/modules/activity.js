/**
 * Created by shunli on 12/22/18.
 */
import Layout from '@/views/layout/Layout'

const meta = { requiresAuth: true }

export default {
  path: '/activity',
  component: Layout,
  redirect: '/activity/vote',
  name: 'activity',
  meta: { title: '微活动', icon: 'scroll' },
  children: [
    {
      path: 'vote',
      name: `vote`,
      component: () => import('@/views/activity/vote'),
      meta: { meta, title: '钻石投票' },
      hidden: true,
      children: [
        {
          path: 'allActivityList',
          name: 'allActivityList',
          component: () => import('@/views/activity/vote/tpls/activity-table'),
          meta: { meta, title: '全部活动列表' }
        },
        {
          path: 'activityList',
          name: 'activityList',
          component: () => import('@/views/activity/vote/tpls/activity-table'),
          meta: { meta, title: '活动列表' }
        },
        {
          path: 'pending',
          name: 'pending',
          component: () => import('@/views/activity/vote/tpls/pending-table'),
          meta: { meta, title: '待审核选手' }
        }
      ]
    },
    {
      path: 'player',
      name: `player`,
      component: () => import('@/views/activity/player'),
      meta: { meta, title: '选手列表' },
      hidden: true
    },
    {
      path: 'addPlayer',
      name: `addPlayer`,
      component: () => import('@/views/activity/player/addPlayer'),
      meta: { meta, title: '添加选手' },
      hidden: true
    },
    {
      path: 'editPlayer',
      name: `editPlayer`,
      component: () => import('@/views/activity/player/edit-player'),
      meta: { meta, title: '修改选手' },
      hidden: true
    },
    {
      path: 'addActivity',
      name: `addActivity`,
      component: () => import('@/views/activity/add-vote'),
      meta: { meta, title: '添加活动' },
      hidden: true
    },
    {
      path: 'gift',
      name: `gift`,
      component: () => import('@/views/activity/gift'),
      meta: { meta, title: '礼物列表' },
      hidden: true
    },
    {
      path: 'voteLogs',
      name: `voteLogs`,
      component: () => import('@/views/activity/vote-logs'),
      meta: { meta, title: ' 投票记录' },
      hidden: true
    },
    {
      path: 'reportLogs',
      name: `reportLogs`,
      component: () => import('@/views/activity/report-logs'),
      meta: { meta, title: '举报记录' },
      hidden: true
    },
    {
      path: 'hostSet',
      name: `hostSet`,
      component: () => import('@/views/activity/hostset'),
      meta: { meta, title: '域名设置' },
      hidden: true
    },
    {
      path: 'playerLog',
      name: `playerLog`,
      component: () => import('@/views/activity/player/logs'),
      meta: { meta, title: '数据记录' },
      hidden: true
    },
    {
      path: 'stateReport',
      name: `stateReport`,
      component: () => import('@/views/activity/state-report'),
      meta: { meta, title: '统计报表' },
      hidden: true
    },
    {
      path: 'activityReport',
      name: `activityReport`,
      component: () => import('@/views/activity/activity-report'),
      meta: { meta, title: '投票报表' },
      hidden: true
    },
    {
      path: 'allActivity',
      name: `allActivity`,
      component: () => import('@/views/activity/vote'),
      meta: { meta, title: '全部活动' },
      hidden: true
    }
  ]
}
