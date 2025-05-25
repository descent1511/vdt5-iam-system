import api from './api'

export const policyService = {
  async getPolicies(params = {}) {
    const response = await api.get('/policies', { params })
    return response
  },
  
  async getPolicyById(id) {
    const response = await api.get(`/policies/${id}`)
    return response
  },
  
  async createPolicy(policyData) {
    const response = await api.post('/policies', policyData)
    return response
  },
  
  async updatePolicy(id, policyData) {
    const response = await api.put(`/policies/${id}`, policyData)
    return response
  },
  
  async deletePolicy(id) {
    const response = await api.delete(`/policies/${id}`)
    return response
  }
}