/**
 * Created by shli on 2019/1/3.
 */
import http from '@/utils/http'

export const setDomian = (data) => {
  return http.post('/api/domain/set', data)
}
