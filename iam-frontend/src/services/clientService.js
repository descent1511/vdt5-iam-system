import api from './api'

export const clientService = {
  async getAllClients() {
    const response = await api.get('/clients')
    return response
  },

  async getClientById(id) {
    const response = await api.get(`/clients/${id}`)
    return response
  },

  async createClient(clientData) {
    const response = await api.post('/clients', clientData)
    return response
  },

  async updateClient(id, clientData) {
    const response = await api.put(`/clients/${id}`, clientData)
    return response
  },

  async deleteClient(id) {
    const response = await api.delete(`/clients/${id}`)
    return response
  }
} 