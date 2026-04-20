import request from '@/utils/request'

export const login = (data: any) => {
  return request.post<any>('/sys/login', data)
}
