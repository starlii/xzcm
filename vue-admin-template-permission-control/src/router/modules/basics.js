/**
 * Created by shunli on 12/20/18.
 */
import Layout from '@/views/layout/Layout'

const meta = { requiresAuth: true }
export default {
  path: '',
  name: 'basics',
  redirect: 'userInfo',
  meta: { title: '基础设置', icon: 'basics' },
  component: Layout,
  children: [
    { path: 'userInfo', name: `userInfo`, component: () => import('@/views/basics/userInfo'), meta: { meta, title: '用户信息' }}
  ]
}
