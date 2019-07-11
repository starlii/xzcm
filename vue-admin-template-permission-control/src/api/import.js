/**
 * Created by shli on 2019/1/5.
 */
import http from '@/utils/http'

/**
 * 批量导入选手
 * @param data
 * @returns {*}
 */
export const importExcelData = (data) => {
  return http.post('/api/import/importExcelData', data)
}

/**
 * 导出活动选手
 * @param data
 * @returns {*}
 */
export const exportExcelData = (data) => {
  return http.post('/api/import/exportExcelData', data)
}
