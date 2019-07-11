/**
 * Created by shunli on 1/1/19.
 */

import http from '@/utils/http'

/**
 * 获取举报列表
 * @param activityId
 * @param pageNum
 * @param pageSize
 * @returns {*}
 */
export const getReportList = ({ activityId, pageNum, pageSize }) => {
  const url = activityId ? `/api/reportPro/get?activityId=${activityId}&pageNum=${pageNum}&pageSize=${pageSize}` : `/api/reportPro/get?pageNum=${pageNum}&pageSize=${pageSize}`
  return http.get(url)
}

/**
 * 获取充值报表
 * @param data
 * @returns {*}
 */
export const getChargeList = (data) => {
  return http.post('/api/reports/charge', data)
}

/**
 * 获取结算报表
 * @param data
 * @returns {*}
 */
export const settlement = (data) => {
  return http.post('/api/reports/settlement', data)
}

/**
 * 更新支付状态
 * @param id
 * @returns {*}
 */
export const updateStatus = ({ id }) => {
  return http.put(`/api/reports/updateStatus?id=${id}`)
}

/**
 * 获取今日交易
 * @returns {*}
 */
export const todayDeals = () => {
  return http.get('/api/reports/todayDeals')
}

/**
 * 获取统计数据
 * @param self
 * @returns {*}
 */
export const statistics = (self) => {
  const url = self ? `/api/reports/statistics?self=${self}` : '/api/reports/statistics'
  return http.get(url)
}
