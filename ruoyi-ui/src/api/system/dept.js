import { createLocalApi, initLocalData } from '@/utils/storage'

initLocalData('dept', [
  { id: '1', parentId: '0', deptName: '总公司', orderNum: 1, leader: 'admin', phone: '021-12345678', email: 'admin@company.com', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '100', parentId: '1', deptName: '研发部', orderNum: 1, leader: 'dev', phone: '021-12345679', email: 'dev@company.com', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '101', parentId: '1', deptName: '市场部', orderNum: 2, leader: 'market', phone: '021-12345680', email: 'market@company.com', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '102', parentId: '100', deptName: '前端组', orderNum: 1, leader: 'fe', phone: '021-12345681', email: 'fe@company.com', status: '0', createTime: '2026-01-01 10:00:00' },
  { id: '103', parentId: '100', deptName: '后端组', orderNum: 2, leader: 'be', phone: '021-12345682', email: 'be@company.com', status: '0', createTime: '2026-01-01 10:00:00' }
])

export const deptApi = createLocalApi('dept')

export default deptApi
