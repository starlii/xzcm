/**
 * Created by shli on 2019/1/10.
 */
import http from '@/utils/http'

/**
 * 获取平台基础信息设置
 * @returns {*}
 */
export const getBasicInfo = () => {
  return http.get('/api/setting/basic')
}

/**
 * 更新平台设置
 * @param data
 * @returns {*}
 */
export const update = (data) => {
  return http.post('/api/setting/update', data)
}

/**
 * 获取微信设置
 * @returns {*}
 */
export const getWeChatInfo = () => {
  return http.get('/api/setting/weChat')
}

/**
 * 一键清除 用户图片
 * @returns {*}
 */
export const deleteAll = () => {
  return http.post('/api/fileUploadController/deleteAll')
}
