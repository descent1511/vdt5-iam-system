import api from './api'

export const permissionService = {
  async getPermissions() {
    try {
      const response = await api.get('/permissions')
      return response
    } catch (error) {
      console.error('Error fetching permissions:', error)
      throw error
    }
  },

  async createPermission(permissionData) {
    try {
      const response = await api.post('/permissions', permissionData)
      return response
    } catch (error) {
      console.error('Error creating permission:', error)
      throw error
    }
  },

  async updatePermission(id, permissionData) {
    try {
      const response = await api.put(`/permissions/${id}`, permissionData)
      return response
    } catch (error) {
      console.error('Error updating permission:', error)
      throw error
    }
  },

  async deletePermission(id) {
    try {
      const response = await api.delete(`/permissions/${id}`)
      return response
    } catch (error) {
      console.error('Error deleting permission:', error)
      throw error
    }
  }
}