import api from './api'

export const organizationService = {
  async getAllOrganizations() {
    try {
      const response = await api.get('/organizations')
      return response
    } catch (error) {
      console.error('Error fetching organizations:', error)
      throw error
    }
  },
  
  async getOrganization(id) {
    try {
      const response = await api.get(`/organizations/${id}`)
      return response
    } catch (error) {
      console.error('Error fetching organization:', error)
      throw error
    }
  },
  
  async createOrganization(organizationData) {
    try {
      const response = await api.post('/organizations', organizationData)
      return response
    } catch (error) {
      console.error('Error creating organization:', error)
      throw error
    }
  },
  
  async updateOrganization(id, organizationData) {
    try {
      const response = await api.put(`/organizations/${id}`, organizationData)
      return response
    } catch (error) {
      console.error('Error updating organization:', error)
      throw error
    }
  },
  
  async deleteOrganization(id) {
    try {
      const response = await api.delete(`/organizations/${id}`)
      return response
    } catch (error) {
      console.error('Error deleting organization:', error)
      throw error
    }
  },
  
}
