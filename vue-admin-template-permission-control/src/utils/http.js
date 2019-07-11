/**
 * Created by shli on 2019/1/2.
 */
// 创建axios实例
import axios from 'axios'
import { Message } from 'element-ui'
import { getToken } from '@/utils/auth'

// 创建一个 axios 实例
const service = axios.create({
  baseURL: process.env.BASE_API,
  timeout: 10000 // 请求超时时间
})

function _h(verb) {
  return function(path, params) {
    let result = {
      error: null,
      data: {},
      msg: null,
      code: 0
    }

    return service({
      method: verb,
      url: path,
      data: params,
      headers: { authorization: getToken() || '' }
    })
      .then(response => {
        const res = response.data
        result = res
        if (result.error) {
          Message.error(result.msg)
          return Promise.resolve(result)
        }
        return Promise.resolve(result)
      })
      .catch((error) => {
        console.log('error', error)
        // Message.error('服务器请求出错！')
        return Promise.resolve({
          error: true,
          data: null,
          msg: '请求出错！',
          code: 0 })
      })
  }
}
export default {
  get: _h('get'),
  post: _h('post'),
  put: _h('put'),
  delete: _h('delete')
}
