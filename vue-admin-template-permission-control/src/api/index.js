/**
 * Created by shunli on 12/23/18.
 */
import * as user from './login'
import * as activity from './activity'
import * as player from './player'
import * as report from './report'
import * as domain from './domain'
import * as importExcle from './import'
import * as platForm from './platform'
import * as userSetting from './userSetting'
import * as userPackage from './userPackage'
// import * as userSetting from. './userSetting'
// import * as role from './role'

const api = {
  user,
  activity,
  player,
  report,
  domain,
  importExcle,
  platForm,
  userSetting,
  userPackage
  // userSetting,
  // role
}

export default {
  install(Vue, options) {
    Vue.prototype.$api = api
  }
}
