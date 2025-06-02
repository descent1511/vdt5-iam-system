import api from './api'

export const organizationService = {
  async getAllOrganizations() {
    const response = await api.get('/organizations')
    return response
  },
  
  async getOrganization(id) {
    const response = await api.get(`/organizations/${id}`)
    return response
  },
  
  async createOrganization(organizationData) {
    const response = await api.post('/organizations', organizationData)
    return response
  },
  
  async updateOrganization(id, organizationData) {
    const response = await api.put(`/organizations/${id}`, organizationData)
    return response
  },
  
  async deleteOrganization(id) {
    const response = await api.delete(`/organizations/${id}`)
    return response
  },
  
}
