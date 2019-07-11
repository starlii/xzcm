/**
 * Created By shli on 2019/1/8
 */
import http from '@/plugins/http'

/**
 * 获取 h5 首页内容
 * @param activityId
 * @returns {*}
 */
export const getH5HomePage = ({activityId}) => {
  return http.get(`/api/h5/getH5HomePage?activityId=${activityId}`)
}

/**
 * 获取 h5 选手搜索
 * @param {data}
 * @returns {*}
 */
export const getPlayer = data => {
  return http.post('api/player/get', data)
}

/**
 * 获取 h5 查看选手详情
 * @param playerId
 * @returns {*}
 */
export const getH5PlayerDetail = ({playerId}) => {
  return http.get(`/api/h5/getH5PlayerDetail?playerId=${playerId}`)
}

/**
 * 获取轮播图信息和活动状态
 * @param activityId
 * @returns {*}
 */
export const getHomeMessage = ({activityId}) => {
  return http.get(`/api/h5/getHomeMessage?activityId=${activityId}`)
}

/**
 * 获取轮播图信息
 * @param activityId
 * @returns {*}
 */
export const getImages = ({activityId}) => {
  return http.get(`/api/h5/getImages?activityId=${activityId}`)
}

/**
 * 获取选手排名
 * @param activityId
 * @returns {*}
 */
export const getPlayerRank = ({activityId}) => {
  return http.get(`/api/h5/getPlayerRank?activityId=${activityId}`)
}

/**
 * 为选手送礼物
 * @param data
 * @returns {*}
 */
export const gift = data => {
  return http.put('/api/h5/gift', data)
}

/**
 * 分享后回调统计
 * @param data
 * @returns {*}
 */
export const shareCallback = data => {
  return http.put('/api/h5/shareCallback', data)
}

/**
 * 为选手投票
 * @param data
 * @returns {*}
 */
export const vote = data => {
  return http.put('/api/h5/vote', data)
}
/**
 * 礼物列表
 * @param data
 * @returns {*}
 */
export const getGift = ({activityId}) => {
  return http.get(`/api/h5/getGift?activityId=${activityId}`)
}
/**
 * 添加选手
 * @param data
 * @returns {*}
 */
export const addPlayer = data => {
  return http.post('/api/player/add', data)
}

/**
 * 活动奖品
 * @param data
 * @returns {*}
 */
export const getActivityDetail = ({activityId}) => {
  return http.get(`/api/activity/getActivityDetail?activityId=${activityId}`)
}

/**
 * 投诉
 * @param data
 * @returns {*}
 */
export const addReportPro = data => {
  return http.post('/api/reportPro/add', data)
}
/**
 * 选手-ip量统计
 * @param data
 * @returns {*}
 */
export const ipRecord = data => {
  return http.put('/api/h5/ipRecord', data)
}
/**
 * 选手-是否报名
 * @param data
 * @returns {*}
 */
export const hasJoin = ({activityId, openId}) => {
  return http.get(`/api/h5/hasJoin?activityId=${activityId}&openId=${openId}`)
}

