import api from './api'

export const resourceService = {
  async getResources(params = {}) {
    try {
      const response = await api.get('/resources', { params })
      return response
    } catch (error) {
      console.error('Error fetching resources:', error)
      throw error
    }
  },
  
  async getResourceById(id) {
    try {
      const response = await api.get(`/resources/${id}`)
      return response
    } catch (error) {
      console.error('Error fetching resource:', error)
      throw error
    }
  },
  
  async createResource(resourceData) {
    try {
      const response = await api.post('/resources', resourceData)
      return response
    } catch (error) {
      console.error('Error creating resource:', error)
      throw error
    }
  },
  
  async updateResource(id, resourceData) {
    try {
      const response = await api.put(`/resources/${id}`, resourceData)
      return response
    } catch (error) {
      console.error('Error updating resource:', error)
      throw error
    }
  },
  
  async deleteResource(id) {
    try {
      const response = await api.delete(`/resources/${id}`)
      return response
    } catch (error) {
      console.error('Error deleting resource:', error)
      throw error
    }
  },

  async discoverResources() {
    try {
      const response = await api.post('/resources/discover')
      return response
    } catch (error) {
      console.error('Error discovering resources:', error)
      throw error
    }
  }
}