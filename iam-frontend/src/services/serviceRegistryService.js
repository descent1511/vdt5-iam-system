import api from './api'

export const serviceRegistryService = {
  async getServices(params = {}) {
    try {
      const response = await api.get('/services', { params })
      return response
    } catch (error) {
      console.error('Error fetching services:', error)
      throw error
    }
  },

  async createService(serviceData) {
    try {
      const response = await api.post('/services', serviceData)
      return response
    } catch (error) {
      console.error('Error creating service:', error)
      throw error
    }
  },

  async getServiceById(id) {
    try {
      const response = await api.get(`/services/${id}`)
      return response
    } catch (error) {
      console.error('Error fetching service:', error)
      throw error
    }
  },

  async updateService(id, serviceData) {
    try {
      const response = await api.put(`/services/${id}`, serviceData)
      return response
    } catch (error) {
      console.error('Error updating service:', error)
      throw error
    }
  },

  async deleteService(id) {
    try {
      const response = await api.delete(`/services/${id}`)
      return response
    } catch (error) {
      console.error('Error deleting service:', error)
      throw error
    }
  },
} 