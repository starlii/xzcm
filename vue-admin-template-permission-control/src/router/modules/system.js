import Layout from '@/views/layout/Layout'

const meta = { requiresAuth: true }

export default {
  path: '/system',
  name: 'system',
  redirect: '/system/platformSetting',
  meta: { title: '系统', icon: 'system', roles: ['system', 'admin'] },
  component: Layout,
  children: [
    {
      path: 'platformSetting',
      name: `platformSetting`,
      component: () => import('@/views/system/platformSetting'),
      meta: { meta, title: '平台设置', roles: ['system']
      }
    },
    {
      path: 'userSetting',
      name: `userSetting`,
      component: () => import('@/views/system/userSetting'),
      meta: { meta, title: '用户设置' }
    },
    {
      path: 'addUser',
      name: `addUser`,
      component: () => import('@/views/system/userSetting/addUser/index'),
      meta: { meta, title: '新增用户' },
      hidden: true
    },
    {
      path: 'editUser',
      name: `editUser`,
      component: () => import('@/views/system/userSetting/addUser'),
      meta: { meta, title: '编辑用户' },
      hidden: true
    },
    {
      path: 'setMeal',
      name: `setMeal`,
      component: () => import('@/views/system/setMeal'),
      meta: { meta, title: '用户套餐', roles: ['system', 'admin']
      }
    },
    {
      path: 'addPackage',
      name: `addPackage`,
      component: () => import('@/views/system/setMeal/addPackage'),
      meta: { meta, title: '添加套餐' },
      hidden: true
    },
    {
      path: 'editPackage',
      name: 'editPackage',
      component: () => import('@/views/system/setMeal/addPackage'),
      meta: { meta, title: '编辑套餐' },
      hidden: true
    }
  ]
}
