import { defineStore } from 'pinia'

export const useStore = defineStore('app', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: null,
    roles: [],
    permissions: []
  }),
  getters: {
    isLoggedIn: state => !!state.token
  },
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },
    setUser(user) {
      this.user = user
    },
    setRoles(roles) {
      this.roles = roles
    },
    setPermissions(permissions) {
      this.permissions = permissions
    },
    logout() {
      this.token = ''
      this.user = null
      this.roles = []
      this.permissions = []
      localStorage.removeItem('token')
    },
    getInfo() {
      if (this.token) {
        this.user = { userName: 'admin', nickName: '管理员', roles: ['admin'] }
        this.roles = ['admin']
        this.permissions = ['*']
      }
    }
  }
})
