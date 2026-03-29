import { createLocalApi, initLocalData } from '@/utils/storage'

initLocalData('flow_instance', [
  { id: '1', instanceId: 'pi_001', procDefId: 'leaveProcess:1', procDefName: '请假流程', procDefKey: 'leaveProcess', businessKey: 'leave_001', startTime: '2026-03-29 09:00:00', startUserId: 'admin', startUserName: '管理员', currentActivityId: 'UserTask_1', currentActivityName: '部门经理审批', suspensionState: '1' },
  { id: '2', instanceId: 'pi_002', procDefId: 'expenseProcess:1', procDefName: '报销流程', procDefKey: 'expenseProcess', businessKey: 'expense_001', startTime: '2026-03-28 14:00:00', startUserId: 'user01', startUserName: '张三', currentActivityId: 'UserTask_2', currentActivityName: '财务审批', suspensionState: '1' },
  { id: '3', instanceId: 'pi_003', procDefId: 'purchaseProcess:1', procDefName: '采购流程', procDefKey: 'purchaseProcess', businessKey: 'purchase_001', startTime: '2026-03-27 10:30:00', startUserId: 'admin', startUserName: '管理员', currentActivityId: 'EndEvent_1', currentActivityName: '结束', suspensionState: '2' }
])

export const instanceApi = createLocalApi('flow_instance')

export default instanceApi
