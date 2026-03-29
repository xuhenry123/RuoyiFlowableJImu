import { createLocalApi, initLocalData } from '@/utils/storage'

initLocalData('online_user', [
  { id: '1', tokenId: 'abc123', userName: 'admin', ipaddr: '127.0.0.1', loginLocation: '内网IP', browser: 'Chrome', os: 'Windows 10', status: '在线', loginTime: '2026-03-29 10:00:00' },
  { id: '2', tokenId: 'def456', userName: 'user01', ipaddr: '192.168.1.100', loginLocation: '内网IP', browser: 'Firefox', os: 'macOS', status: '在线', loginTime: '2026-03-29 09:30:00' },
  { id: '3', tokenId: 'ghi789', userName: 'test', ipaddr: '10.0.0.50', loginLocation: '内网IP', browser: 'Safari', os: 'iOS', status: '在线', loginTime: '2026-03-29 08:45:00' }
])

export const onlineApi = createLocalApi('online_user')

export default onlineApi
