import request from '@/utils/request'
import type { ApiInfo } from '@/types'

export const getApiInfoList = () => {
  return request.get<ApiInfo[]>('/api/list')
}

export const addApiInfo = (data: ApiInfo) => {
  return request.post<boolean>('/api/add', data)
}

export const updateApiInfo = (data: ApiInfo) => {
  return request.put<boolean>('/api/update', data)
}

export const deleteApiInfo = (id: number) => {
  return request.delete<boolean>(`/api/delete/${id}`)
}
