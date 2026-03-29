import { createLocalApi, initLocalData } from '@/utils/storage'

initLocalData('login_log', [
  { id: '1', infoId: '1', userName: 'admin', ipaddr: '127.0.0.1', loginLocation: '内网IP', browser: 'Chrome', os: 'Windows 10', status: '0', msg: '登录成功', loginTime: '2026-03-29 10:00:00' },
  { id: '2', infoId: '2', userName: 'user01', ipaddr: '192.168.1.100', loginLocation: '内网IP', browser: 'Firefox', os: 'macOS', status: '0', msg: '登录成功', loginTime: '2026-03-29 09:30:00' },
  { id: '3', infoId: '3', userName: 'test', ipaddr: '10.0.0.50', loginLocation: '内网IP', browser: 'Safari', os: 'iOS', status: '0', msg: '登录成功', loginTime: '2026-03-29 08:45:00' },
  { id: '4', infoId: '4', userName: 'admin', ipaddr: '192.168.1.50', loginLocation: '内网IP', browser: 'Chrome', os: 'Windows 10', status: '5', msg: '密码错误', loginTime: '2026-03-28 16:20:00' },
  { id: '5', infoId: '5', userName: 'guest', ipaddr: '192.168.1.80', loginLocation: '内网IP', browser: 'Edge', os: 'Windows 11', status: '5', msg: '账号不存在', loginTime: '2026-03-28 15:10:00' }
])

export const loginLogApi = createLocalApi('login_log')

export default loginLogApi
