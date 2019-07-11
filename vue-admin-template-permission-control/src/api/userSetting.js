/**
 * Created by shli on 2019/1/10.
 */
import http from '@/utils/http'

/**
 * 用户管理查询列表
 * @param username
 * @param page
 * @param size
 * @returns {*}
 */
export const getUserList = ({ userPackage, username, id, page, size }) => {
  const name = username ? `username=${username}&` : ''
  const idParam = id ? `id=${id}&` : ''
  const packageParam = userPackage ? `userPackage=${userPackage}&` : ''
  return http.get(`/api/user/get?${name}${idParam}${packageParam}page=${page}&size=${size}`)
}

/**
 * 新增用户
 * @param data
 * @returns {*}
 */
export const addUser = (data) => {
  return http.post('/api/user/save', data)
}

/**
 * 更改用户信息
 * @param data
 * @returns {*}
 */
export const update = (data) => {
  return http.put('/api/user/update', data)
}

/**
 * 删除用户
 * @param userId
 * @returns {*}
 */
export const deleteUser = ({ userId }) => {
  return http.delete(`/api/user/deleteUser?userId=${userId}`)
}

/**
 * 修改当前用户的密码
 * @param data
 * @returns {*}
 */
export const modifyPassword = (data) => {
  return http.put('/api/user/modifyPassword', data)
}
