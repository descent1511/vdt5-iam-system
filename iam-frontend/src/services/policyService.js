import api from './api'

export const policyService = {
  async getPolicies(params = {}) {
    try {
      const response = await api.get('/policies', { params })
      return response
    } catch (error) {
      console.error('Error fetching policies:', error)
      throw error
    }
  },
  
  async getPolicyById(id) {
    try {
      const response = await api.get(`/policies/${id}`)
      return response
    } catch (error) {
      console.error('Error fetching policy:', error)
      throw error
    }
  },
  
  async createPolicy(policyData) {
    try {
      const response = await api.post('/policies', policyData)
      return response
    } catch (error) {
      console.error('Error creating policy:', error)
      throw error
    }
  },
  
  async updatePolicy(id, policyData) {
    try {
      const response = await api.put(`/policies/${id}`, policyData)
      return response
    } catch (error) {
      console.error('Error updating policy:', error)
      throw error
    }
  },
  
  async deletePolicy(id) {
    try {
      const response = await api.delete(`/policies/${id}`)
      return response
    } catch (error) {
      console.error('Error deleting policy:', error)
      throw error
    }
  }
}