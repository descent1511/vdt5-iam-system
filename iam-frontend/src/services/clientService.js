import api from './api'

export const clientService = {
  async getAllClients() {
    try {
      const response = await api.get('/clients')
      console.log(response)
      return response
    } catch (error) {
      console.error('Error fetching clients:', error)
      throw error
    }
  },

  async getClientById(id) {
    try {
      const response = await api.get(`/clients/${id}`)
      return response
    } catch (error) {
      console.error('Error fetching client:', error)
      throw error
    }
  },

  async createClient(clientData) {
    try {
      const response = await api.post('/clients', clientData)
      return response
    } catch (error) {
      console.error('Error creating client:', error)
      throw error
    }
  },

  async updateClient(id, clientData) {
    try {
      const response = await api.put(`/clients/${id}`, clientData)
      return response
    } catch (error) {
      console.error('Error updating client:', error)
      throw error
    }
  },

  async deleteClient(id) {
    try {
      const response = await api.delete(`/clients/${id}`)
      return response
    } catch (error) {
      console.error('Error deleting client:', error)
      throw error
    }
  }
} 