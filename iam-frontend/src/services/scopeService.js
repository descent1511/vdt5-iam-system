import api from './api'

export const scopeService = {
  async getScopes() {
    try {
      const response = await api.get('/scopes')
      return response
    } catch (error) {
      console.error('Error fetching scopes:', error)
      throw error
    }
  },

  async getScopeById(id) {
    try {
      const response = await api.get(`/scopes/${id}`)
      return response
    } catch (error) {
      console.error('Error fetching scope:', error)
      throw error
    }
  },

  async createScope(scopeData) {
    try {
      const response = await api.post('/scopes', scopeData)
      return response
    } catch (error) {
      console.error('Error creating scope:', error)
      throw error
    }
  },

  async updateScope(id, scopeData) {
    try {
      const response = await api.put(`/scopes/${id}`, scopeData)
      return response
    } catch (error) {
      console.error('Error updating scope:', error)
      throw error
    }
  },

  async deleteScope(id) {
    try {
      const response = await api.delete(`/scopes/${id}`)
      return response
    } catch (error) {
      console.error('Error deleting scope:', error)
      throw error
    }
  }
}