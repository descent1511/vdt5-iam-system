import api from './api'

export const resourceService = {
  async getResources(params = {}) {
    const response = await api.get('/resources', { params })
    return response
  },
  
  async getResourceById(id) {
    const response = await api.get(`/resources/${id}`)
    return response
  },
  
  async createResource(resourceData) {
    const response = await api.post('/resources', resourceData)
    return response
  },
  
  async updateResource(id, resourceData) {
    const response = await api.put(`/resources/${id}`, resourceData)
    return response
  },
  
  async deleteResource(id) {
    const response = await api.delete(`/resources/${id}`)
    return response
  },

  async discoverResources() {
    const response = await api.post('/resources/discover')
    return response
  }
}