import { defineStore } from 'pinia'
import { clientService } from '../services/clientService'

export const useClientStore = defineStore('clients', {
  state: () => ({
    clients: [],
    loading: false,
    error: null
  }),

  getters: {
    totalClients: (state) => state.clients?.length || 0
  },

  actions: {
    async fetchClients() {
      this.loading = true
      this.error = null
      try {
        const response = await clientService.getAllClients()
        this.clients = response || []
        return true
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to fetch clients'
        console.error('Error fetching clients:', error)
        this.clients = [] // Reset clients on error
        return false
      } finally {
        this.loading = false
      }
    },

    async fetchClientById(id) {
      this.loading = true
      this.error = null
      try {
        const response = await clientService.getClientById(id)
        return response
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to fetch client'
        console.error('Error fetching client:', error)
        return null
      } finally {
        this.loading = false
      }
    },

    async createClient(clientData) {
      this.loading = true
      this.error = null
      try {
        const response = await clientService.createClient(clientData)
        if (response) {
          this.clients = [...this.clients, response]
        }
        return true
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to create client'
        console.error('Error creating client:', error)
        return false
      } finally {
        this.loading = false
      }
    },

    async updateClient(id, clientData) {
      this.loading = true
      this.error = null
      try {
        const response = await clientService.updateClient(id, clientData)
        if (response) {
          const index = this.clients.findIndex(client => client.id === id)
          if (index !== -1) {
            this.clients = [
              ...this.clients.slice(0, index),
              response,
              ...this.clients.slice(index + 1)
            ]
          }
        }
        return true
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to update client'
        console.error('Error updating client:', error)
        return false
      } finally {
        this.loading = false
      }
    },

    async deleteClient(id) {
      this.loading = true
      this.error = null
      try {
        await clientService.deleteClient(id)
        this.clients = this.clients.filter(client => client.id !== id)
        return true
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to delete client'
        console.error('Error deleting client:', error)
        return false
      } finally {
        this.loading = false
      }
    }
  }
}) 