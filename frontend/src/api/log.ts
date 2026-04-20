import request from '@/utils/request'
import type { PageData, ApiLog, ApiLogQuery } from '@/types'

/**
 * 分页查询审计日志
 * @param params 查询条件
 */
export const getApiLogList = (params: ApiLogQuery) => {
  return request.get<any, PageData<ApiLog>>('/api/log/list', { params })
}
