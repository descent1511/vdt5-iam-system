import api from './api'

export const roleService = {
  async getRoles(params = {}) {
    try {
      const response = await api.get('/roles', { params })
      return response
    } catch (error) {
      console.error('Error fetching roles:', error)
      throw error
    }
  },
  
  async getRoleById(id) {
    try {
      const response = await api.get(`/roles/${id}`)
      return response
    } catch (error) {
      console.error('Error fetching role:', error)
      throw error
    }
  },
  
  async createRole(roleData) {
    try {

      const response = await api.post('/roles', roleData)
      return response
    } catch (error) {
      console.error('Error creating role:', error)
      throw error
    }
  },
  
  async updateRole(id, roleData) {
    try {
      const response = await api.put(`/roles/${id}`, roleData)
      return response
    } catch (error) {
      console.error('Error updating role:', error)
      throw error
    }
  },
  
  async deleteRole(id) {
    try {
      const response = await api.delete(`/roles/${id}`)
      return response
    } catch (error) {
      console.error('Error deleting role:', error)
      throw error
    }
  }
}