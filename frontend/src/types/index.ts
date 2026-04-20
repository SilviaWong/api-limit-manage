// 全局基础响应类型
export interface Result<T = any> {
  code: number;
  msg: string;
  data: T;
}

// 分页响应包裹类型
export interface PageData<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

// ApiLog 实体定义
export interface ApiLog {
  id: number;
  apiId: number;
  authId: number;
  uKey: string;
  ip: string;
  requestParams: string;
  resultSummary: string;
  errorCode: string;
  costTime: number;
  createTime: string; // 后端返回的时间通常是字符串
}

// 查询 ApiLog 的参数
export interface ApiLogQuery {
  current?: number;
  size?: number;
  apiId?: number;
  uKey?: string;
  ip?: string;
  errorCode?: string;
}

export interface ApiInfo {
  id: number;
  name: string;
  directory: string;
  method: string;
  url: string;
  appKey: string;
  appSecret: string;
  serviceCode: string;
  inputDesc: string;
  outputDesc: string;
  description: string;
  requestLimit: number;
  status: number;
  whiteList: string;
  createTime: string;
  updateTime: string;
}

export interface UnitInfo {
  id: number;
  unitName: string;
  unitCode: string;
  description: string;
  status: number;
  createTime: string;
  updateTime: string;
}

export interface ApiAuth {
  id?: number;
  apiId: number | undefined;
  unitId: number | undefined;
  uKey?: string;
  mKey?: string;
  whiteList?: string;
  cryptoPolicy?: string;
  status?: number;
  createTime?: string;
  updateTime?: string;
}
