import { createLocalApi, initLocalData } from '@/utils/storage'

initLocalData('flow_definition', [
  { id: '1', definitionId: 'leaveProcess:1', name: '请假流程', key: 'leaveProcess', category: 'HR', version: 1, deploymentId: 'd1', suspended: false, tenantId: '', deploymentTime: '2026-03-20 10:00:00', xmlResourceName: 'process.bpmn', graphicResourceName: 'process.png' },
  { id: '2', definitionId: 'expenseProcess:1', name: '报销流程', key: 'expenseProcess', category: 'FINANCE', version: 1, deploymentId: 'd2', suspended: false, tenantId: '', deploymentTime: '2026-03-18 14:30:00', xmlResourceName: 'expense.bpmn', graphicResourceName: 'expense.png' },
  { id: '3', definitionId: 'purchaseProcess:1', name: '采购流程', key: 'purchaseProcess', category: 'OFFICE', version: 1, deploymentId: 'd3', suspended: false, tenantId: '', deploymentTime: '2026-03-15 09:00:00', xmlResourceName: 'purchase.bpmn', graphicResourceName: 'purchase.png' },
  { id: '4', definitionId: 'leaveProcess:2', name: '请假流程', key: 'leaveProcess', category: 'HR', version: 2, deploymentId: 'd4', suspended: false, tenantId: '', deploymentTime: '2026-03-25 11:00:00', xmlResourceName: 'process.bpmn', graphicResourceName: 'process.png' }
])

export const definitionApi = createLocalApi('flow_definition')

export default definitionApi
