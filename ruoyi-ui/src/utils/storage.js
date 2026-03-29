const STORAGE_PREFIX = 'ruoyi_'

const storage = {
  get(key) {
    const data = localStorage.getItem(STORAGE_PREFIX + key)
    return data ? JSON.parse(data) : null
  },
  
  set(key, value) {
    localStorage.setItem(STORAGE_PREFIX + key, JSON.stringify(value))
  },
  
  remove(key) {
    localStorage.removeItem(STORAGE_PREFIX + key)
  },
  
  clear() {
    Object.keys(localStorage)
      .filter(key => key.startsWith(STORAGE_PREFIX))
      .forEach(key => localStorage.removeItem(key))
  }
}

export function createLocalApi(storeName, options = {}) {
  const { idField = 'id', autoIncrement = true } = options
  
  return {
    async list(params = {}) {
      let data = storage.get(storeName) || []
      
      if (params.name) {
        data = data.filter(item => item.name && item.name.includes(params.name))
      }
      if (params.status && params.status !== '') {
        data = data.filter(item => item.status === params.status)
      }
      if (params.type) {
        data = data.filter(item => item.type === params.type)
      }
      
      return {
        code: 200,
        msg: '操作成功',
        data: {
          list: data,
          total: data.length
        }
      }
    },
    
    async get(id) {
      const data = storage.get(storeName) || []
      const item = data.find(item => item[idField] == id)
      return {
        code: 200,
        msg: '操作成功',
        data: item || null
      }
    },
    
    async add(params) {
      let data = storage.get(storeName) || []
      
      const newItem = { ...params }
      if (autoIncrement) {
        const maxId = data.length > 0 ? Math.max(...data.map(item => parseInt(item[idField] || 0))) : 0
        newItem[idField] = String(maxId + 1)
      }
      newItem.createTime = new Date().toLocaleString()
      newItem.createBy = 'admin'
      
      data.push(newItem)
      storage.set(storeName, data)
      
      return {
        code: 200,
        msg: '新增成功',
        data: newItem
      }
    },
    
    async update(params) {
      let data = storage.get(storeName) || []
      const index = data.findIndex(item => item[idField] == params[idField])
      
      if (index !== -1) {
        data[index] = { ...data[index], ...params, updateTime: new Date().toLocaleString(), updateBy: 'admin' }
        storage.set(storeName, data)
        return { code: 200, msg: '更新成功' }
      }
      
      return { code: 500, msg: '记录不存在' }
    },
    
    async delete(id) {
      let data = storage.get(storeName) || []
      const filtered = data.filter(item => item[idField] != id)
      storage.set(storeName, filtered)
      return { code: 200, msg: '删除成功' }
    },
    
    async batchDelete(ids) {
      let data = storage.get(storeName) || []
      const filtered = data.filter(item => !ids.includes(item[idField]))
      storage.set(storeName, filtered)
      return { code: 200, msg: '批量删除成功' }
    }
  }
}

export function initLocalData(storeName, initialData) {
  if (!storage.get(storeName)) {
    storage.set(storeName, initialData)
  }
}

export function clearLocalData(storeName) {
  storage.remove(storeName)
}

export default storage
