import api from './api'

export const clientService = {
  async getAllClients() {
    console.log('Request: GET /clients')
    const response = await api.get('/clients')
    console.log('Response:', response)
    return response
  },

  async getClientById(id) {
    console.log('Request: GET /clients/' + id)
    const response = await api.get(`/clients/${id}`)
    console.log('Response:', response)
    return response
  },

  async createClient(clientData) {
    console.log('Request: POST /clients', clientData)
    const response = await api.post('/clients', clientData)
    console.log('Response:', response)
    return response
  },

  async updateClient(id, clientData) {
    console.log('Request: PUT /clients/' + id, clientData)
    const response = await api.put(`/clients/${id}`, clientData)
    console.log('Response:', response)
    return response
  },

  async deleteClient(id) {
    console.log('Request: DELETE /clients/' + id)
    const response = await api.delete(`/clients/${id}`)
    console.log('Response:', response)
    return response
  }
} 