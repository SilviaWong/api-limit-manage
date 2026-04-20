import request from '@/utils/request'

export interface TrendPoint {
  date: string
  count: number
}

export interface DataItem {
  name: string
  value: number
}

export interface DashboardStats {
  apiCount: number
  unitCount: number
  todayCallCount: number
  todayErrorCount: number
  trendData: TrendPoint[]
  topApis: DataItem[]
  errorDistribution: DataItem[]
}

export const getDashboardStats = () => {
  return request.get<DashboardStats>('/api/dashboard/stats')
}
