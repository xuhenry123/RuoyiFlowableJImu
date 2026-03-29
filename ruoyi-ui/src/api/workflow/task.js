import { createLocalApi, initLocalData } from '@/utils/storage'

initLocalData('flow_task', [
  { id: '1', taskId: 'task_001', taskName: '部门经理审批', taskDefKey: 'deptManagerAudit', procDefName: '请假流程', procInstId: 'pi_001', procDefKey: 'leaveProcess', businessKey: 'leave_001', createTime: '2026-03-29 09:00:00', assignee: 'manager01', assigneeName: '王经理', delegationState: null, suspensionState: '1' },
  { id: '2', taskId: 'task_002', taskName: '财务审批', taskDefKey: 'financeAudit', procDefName: '报销流程', procInstId: 'pi_002', procDefKey: 'expenseProcess', businessKey: 'expense_001', createTime: '2026-03-28 14:00:00', assignee: 'finance01', assigneeName: '李财务', delegationState: null, suspensionState: '1' },
  { id: '3', taskId: 'task_003', taskName: 'HR审批', taskDefKey: 'hrAudit', procDefName: '请假流程', procInstId: 'pi_004', procDefKey: 'leaveProcess', businessKey: 'leave_002', createTime: '2026-03-29 08:00:00', assignee: 'hr01', assigneeName: '赵HR', delegationState: null, suspensionState: '1' }
])

export const taskApi = createLocalApi('flow_task')

export default taskApi
