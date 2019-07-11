/**
 * Created By shli on 2019/1/8
 */
import axios from 'axios'
import { Message } from 'element-ui'

// 创建一个 axios 实例
const service = axios.create({
  baseURL: process.env.BASE_API,
  timeout: 5000 // 请求超时时间
})

function _h (verb) {
  return function (path, params) {
    let result = {
      error: null,
      data: {},
      msg: null,
      code: 0
    }

    return service({
      method: verb,
      url: path,
      data: params
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
      .catch(error => {
        console.log('请求出错', error)
        Message.error('服务器请求出错！')
        return Promise.resolve({ error: true,
          data: null,
          msg: '服务器请求出错！',
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
