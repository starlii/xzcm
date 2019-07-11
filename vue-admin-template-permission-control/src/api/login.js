/**
 * Created by shli on 2019/1/2.
 */
import http from '@/utils/http'

/**
 * 登陆接口
 * @param data
 * @returns {*}
 */
export const userLogin = (data) => {
  return http.post('/api/admin/login', data)
}

/**
 * 获取当前用户
 * @returns {*}
 */
export const getCurrentUser = () => {
  return http.get('/api/user/getCurrentUser')
}

/**
 * 修改密码
 * @param data
 * @returns {*}
 */
export const modifyPassword = (data) => {
  return http.put('/api/user/modifyPassword', data)
}

/**
 * 用户登陆
 * @returns {*}
 */
export const signOut = () => {
  return http.post('/api/admin/signOut')
}

/**
 * 切换用户
 * @param data
 * @returns {*}
 */
export const switchUser = (data) => {
  return http.post('/api/admin/switchUser', data)
}
