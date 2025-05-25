import api from './api'

export const permissionService = {
  async getPermissions() {
    const response = await api.get('/permissions')
    return response
  },

  async createPermission(permissionData) {
    const response = await api.post('/permissions', permissionData)
    return response
  },

  async updatePermission(id, permissionData) {
    const response = await api.put(`/permissions/${id}`, permissionData)
    return response
  },

  async deletePermission(id) {
    const response = await api.delete(`/permissions/${id}`)
    return response
  }
}