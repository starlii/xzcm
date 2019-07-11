/**
 * Created By shli on 2019/1/8
 */
import http from '@/plugins/http'

/**
 * 获取 openId
 * @param code
 * @returns {*}
 */
export const getOpenId = ({code}) => {
  return http.post(`/api/wx/getOpenId?code=${code}`)
}

/**
 * 获取支付与订单号
 */
export const getPay = (data) => {
  return http.post('/api/wx/pay', data)
}

/**
 * 支付成功的回调地址
 * @returns {*}
 */
export const notify = () => {
  return http.post('/api/wx/notify')
}

/**
 * 分享参数签名
 * @param data
 * @returns {*}
 */
export const sign = ({ url }) => {
  return http.post(`/api/wx/sign?url=${url}`)
}

/**
 * 获取微信设置
 * @returns {*}
 */
export const getWeChat = ()=> {
  return http.get(`/api/setting/weChat`)
}

/**
 * 平台设置
 * @returns {*}
 */
export const getBasic = ()=> {
  return http.get(`/api/setting/basic`)
}
