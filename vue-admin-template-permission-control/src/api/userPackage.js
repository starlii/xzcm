/**
 * Created by shunli on 1/10/19.
 */
import http from '@/utils/http'

/**
 * 获取用户套餐列表
 * @param page
 * @param size
 * @returns {*}
 */
export const getUserPackage = ({ id, page, size }) => {
  const param = id ? `id=${id}&` : ''
  return http.get(`/api/package/get?${param}page=${page}&size=${size}`)
}

/**
 * 删除用户套餐
 * @param packageId
 * @returns {*}
 */
export const deleteUserPackage = ({ packageId }) => {
  return http.delete(`/api/package/deleteUser?packageId=${packageId}`)
}

/**
 * 添加新的用户套餐
 * @param data
 * @returns {*}
 */
export const addUserPackage = (data) => {
  return http.post('/api/package/save', data)
}

/**
 * 更新用户套餐
 * @param data
 * @returns {*}
 */
export const updateUserPackage = (data) => {
  return http.put('/api/package/update', data)
}

/**
 * 一次获取所有用户套餐
 * @returns {*}
 */
export const getNoPage = () => {
  return http.get('/api/package/getNoPage')
}
