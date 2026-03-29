import { createLocalApi, initLocalData } from '@/utils/storage'

initLocalData('post', [
  { id: '1', postCode: 'ceo', postName: '首席执行官', postSort: 1, status: '0', createTime: '2026-01-01 10:00:00', remark: '公司最高管理者' },
  { id: '2', postCode: 'cto', postName: '技术总监', postSort: 2, status: '0', createTime: '2026-01-01 10:00:00', remark: '技术负责人' },
  { id: '3', postCode: 'dev', postName: '开发工程师', postSort: 3, status: '0', createTime: '2026-01-01 10:00:00', remark: '研发人员' },
  { id: '4', postCode: 'test', postName: '测试工程师', postSort: 4, status: '0', createTime: '2026-01-01 10:00:00', remark: '测试人员' },
  { id: '5', postCode: 'pm', postName: '产品经理', postSort: 5, status: '0', createTime: '2026-01-01 10:00:00', remark: '产品负责人' }
])

export const postApi = createLocalApi('post')

export default postApi
