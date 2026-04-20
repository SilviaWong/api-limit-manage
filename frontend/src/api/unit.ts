import request from '@/utils/request'
import type { UnitInfo } from '@/types'

export const getUnitList = () => {
  return request.get<UnitInfo[]>('/unit/list')
}
