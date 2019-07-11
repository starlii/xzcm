/**
 * Created by shunli on 12/23/18.
 */

import http from '@/utils/http'

/**
 * 获取活动列表
 * @param data
 * @returns {*}
 */
export const getAllActivityList = (data) => {
  return http.post('/api/activity/list', data)
}

/**
 * 获取所有活动
 * @returns {*}
 */
export const getActivityList = (data) => {
  return http.post('/api/activity/listOnlySelf', data)
}

/**
 * 跟新浏览量
 * @param data
 * @returns {*}
 */
export const updateViews = (data) => {
  return http.put('/api/activity/updateViews', data)
}

/**
 * 添加活动
 * @param data
 * @returns {*}
 */
export const addActivity = (data) => {
  return http.post('/api/activity/add', data)
}

/**
 * 获取活动链接和二维码
 * @param activityIid
 * @returns {*}
 */
export const getActivityAccessLink = ({ id }) => {
  return http.get(`/api/activity/getActivityAccessLink?activityId=${id}`)
}

/**
 * 查看活动所有礼物记录
 * @param activityId
 * @returns {*}
 */
export const getGiftLog = ({ activityId }) => {
  const url = activityId ? `/api/activity/getGiftLog?activityId=${activityId}` : '/api/activity/getGiftLog'
  return http.get(url)
}

/**
 * 删除活动
 * @param activityIds
 * @returns {*}
 */
export const deleteActivity = ({ activityId }) => {
  return http.delete(`/api/activity/delete?activityIds=${activityId}`)
}

/**
 * 查看活动的投票记录
 * @param data
 * @returns {*}
 */
export const getVoteLog = (activityId) => {
  const url = activityId ? '/api/activity/getVoteLog?activityId=' + activityId : '/api/activity/getVoteLog'
  return http.get(url)
}

/**
 * 获取活动详情页
 * @param activityId
 * @returns {*}
 */
export const getActivityDetail = ({ activityId }) => {
  return http.get(`/api/activity/getActivityDetail?activityId=${activityId}`)
}

/**
 * 批量跟新活动的备注
 * @param data
 * @returns {*}
 */
export const bachUpdate = (data) => {
  return http.put('/api/activity/batchUpdate', data)
}

/**
 * 更新活动
 * @param data
 * @returns {*}
 */
export const update = (data) => {
  return http.put('/api/activity/update', data)
}

/**
 * 获取 h5 首页内容， 后台用于获取活动是否有投票纪录
 * @param activity
 * @returns {*}
 */
export const getH5HomePage = ({ activityId }) => {
  return http.get(`/api/h5/getH5HomePage?activityId=${activityId}`)
}

/**
 * 获取首页轮播信息
 * @param activity
 * @returns {*}
 */
export const getHomeMessage = ({ activityId }) => {
  return http.get(`/api/h5/getHomeMessage?activityId=${activityId}`)
}

