/**
 * Created By shli on 2019/1/8
 */
import * as weChat from './weChat'
import * as h5 from './h5'

const api = {
  weChat,
  h5
}

export default {
  install (Vue, options) {
    Vue.prototype.$api = api
  }
}
