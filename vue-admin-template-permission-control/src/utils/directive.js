/**
 * Created By shli on 2019/1/15
 */
import store from '@/store'
const permission = {
  bind(el, binding, vnode) {
    const role = store.getters.roles[0]
    if (role === 'manager') {
      el.innerHTML = ''
    }
  }
}
const permission2 = {
  bind(el, binding, vnode) {
    const role = store.getters.roles[0]
    if (role !== 'system') {
      el.innerHTML = ''
    }
  }
}
export default {
  install(Vue, opt) {
    Vue.directive('permission', permission)
    Vue.directive('permission2', permission2)
  }
}
