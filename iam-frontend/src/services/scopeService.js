import api from './api'

export const scopeService = {
  async getScopes() {
    const response = await api.get('/scopes')
    return response
  },

  async getScopeById(id) {
    const response = await api.get(`/scopes/${id}`)
    return response
  },

  async createScope(scopeData) {
    const response = await api.post('/scopes', scopeData)
    return response
  },

  async updateScope(id, scopeData) {
    console.log(scopeData)
    const response = await api.put(`/scopes/${id}`, scopeData)
    return response
  },

  async deleteScope(id) {
    console.log('Making DELETE request to /scopes/' + id)
    const response = await api.delete(`/scopes/${id}`)
    console.log('DELETE response:', response)
    return response
  }
}