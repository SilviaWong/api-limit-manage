import request from '@/utils/request'
import type { ApiAuth } from '@/types'

export const getAuthList = () => {
  return request.get<ApiAuth[]>('/auth/list')
}

export const addAuth = (data: ApiAuth) => {
  return request.post<boolean>('/auth/add', data)
}

export const updateAuth = (data: ApiAuth) => {
  return request.put<boolean>('/auth/update', data)
}

export const deleteAuth = (id: number) => {
  return request.delete<boolean>(`/auth/delete/${id}`)
}
