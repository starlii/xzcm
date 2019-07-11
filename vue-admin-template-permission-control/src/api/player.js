/**
 * Created by shunli on 12/28/18.
 */
import http from '@/utils/http'

/**
 * 根据活动 ID  获取选手列表
 * @param data
 * @returns {*}
 */
export const getPlayerList = (data) => {
  return http.post('/api/player/get', data)
}

/**
 * 添加选手
 * @param data
 * @returns {*}
 */
export const addPlayer = (data) => {
  return http.post('/api/player/add', data)
}

/**
 * 获取选手的链接和二维码
 * @param id
 * @returns {*}
 */
export const getPlayerAccessLink = ({ id }) => {
  return http.get(`/api/player/getPlayerAccessLink?playerId=${id}`)
}

/**
 * 手动更改选手的票数、浏览、礼物
 * @param data
 * @returns {*}
 */
export const muaUpdate = (data) => {
  return http.put('/api/player/muaUpdate', data)
}

/**
 * 批量更新选手备注
 * @param data
 * @returns {*}
 */
export const batchUpdate = (data) => {
  return http.put('/api/player/batchUpdate', data)
}

/**
 * 更改选手的状态
 * @param data
 * @returns {*}
 */
export const changeStatus = (data) => {
  return http.put('/api/player/status', data)
}

/**
 * 将选手设置为今日之星
 * @param playerId
 * @returns {*}
 */
export const setStar = ({ playerId }) => {
  return http.put(`/api/player/star?playerId=${playerId}`)
}

/**
 * 通过选手 ID 删除选手
 * @param playerId
 * @returns {*}
 */
export const deletePlayer = ({ playerId }) => {
  return http.delete(`/api/player/delete/${playerId}`)
}

export const batchDelete = (data) => {
  return http.put('/api/player/batchDelete', data)
}

/**
 * 获取选手的礼物记录
 * @param playerId
 * @returns {*}
 */
export const getGiftLog = ({ playerId, pageSize, pageNum, sort }) => {
  return http.get(`/api/player/getGiftLog?playerId=${playerId}&pageNum=${pageNum}&pageSize=${pageSize}&sort=${sort}`)
}

/**
 * 获取选手的投票记录
 * @param palyerId
 * @returns {*}
 */
export const getVoteLog = ({ palyerId, pageSize, pageNum, sort }) => {
  return http.get(`/api/player/getVoteLog?playerId= ${palyerId}&pageNum=${pageNum}&pageSize=${pageSize}&sort=${sort}`)
}

/**
 * 一键给当前页的选手随机加票
 * @param data
 * @returns {*}
 */
export const oneClickAdded = (data) => {
  return http.put('/api/player/oneClickAdded', data)
}

/**
 * 给选手设置多少秒后定时投票
 * @param data
 * @returns {*}
 */
export const scheduled = (data) => {
  return http.put('/api/player/scheduled', data)
}

/**
 * 关闭投票定时任务
 * @param playerId
 * @returns {*}
 */
export const scheduledDown = ({ playerId }) => {
  return http.put(`/api/player/scheduledDown?playerId= ${playerId}`)
}

/**
 * 选手详情
 * @param playerId
 * @returns {*}
 */
export const detail = ({ playerId }) => {
  return http.get(`/api/player/detail?playerId=${playerId}`)
}

/**
 * 更新选手
 * @param data
 * @returns {*}
 */
export const update = (data) => {
  return http.put('/api/player/update', data)
}

/**
 * 待审核选手列表
 * @param data
 * @returns {*}
 */
export const getPending = (data) => {
  return http.post('/api/player/approvaling', data)
}
