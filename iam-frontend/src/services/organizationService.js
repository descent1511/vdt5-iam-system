import api from './api'

export const organizationService = {
  async getOrganization() {
    const response = await api.get('/organization')
    return response
  },
  
  async updateOrganization(orgData) {
    const response = await api.put('/organization', orgData)
    return response
  },
  
  async getDepartments() {
    const response = await api.get('/departments')
    return response
  },
  
  async createDepartment(departmentData) {
    const response = await api.post('/departments', departmentData)
    return response
  },
  
  async updateDepartment(id, departmentData) {
    const response = await api.put(`/departments/${id}`, departmentData)
    return response
  },
  
  async deleteDepartment(id) {
    const response = await api.delete(`/departments/${id}`)
    return response
  }
}