import { defineStore } from 'pinia'
import { scopeService } from '../services/scopeService'

export const useScopeStore = defineStore('scopes', {
  state: () => ({
    scopes: [],
    loading: false,
    error: null
  }),

  getters: {
    totalScopes: (state) => state.scopes?.length || 0
  },

  actions: {
    async fetchScopes() {
      this.loading = true
      this.error = null
      try {
        const response = await scopeService.getScopes()
        this.scopes = response|| []
        return true
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to fetch scopes'
        console.error('Error fetching scopes:', error)
        this.scopes = [] // Reset scopes on error
        return false
      } finally {
        this.loading = false
      }
    },

    async fetchScopeById(id) {
      this.loading = true
      this.error = null
      try {
        const response = await scopeService.getScopeById(id)
        return response
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to fetch scope'
        console.error('Error fetching scope:', error)
        return null
      } finally {
        this.loading = false
      }
    },

    async createScope(scopeData) {
      this.loading = true
      this.error = null
      try {
        const response = await scopeService.createScope(scopeData)
        if (response.data) {
          this.scopes = [...this.scopes, response.data]
        }
        return true
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to create scope'
        console.error('Error creating scope:', error)
        return false
      } finally {
        this.loading = false
      }
    },

    async updateScope(id, scopeData) {
      this.loading = true
      this.error = null
      try {
        const response = await scopeService.updateScope(id, scopeData)
        console.log(scopeData)
        if (response.data) {
          const index = this.scopes.findIndex(scope => scope.id === id)
          if (index !== -1) {
            this.scopes = [
              ...this.scopes.slice(0, index),
              response.data,
              ...this.scopes.slice(index + 1)
            ]
          }
        }
        return true
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to update scope'
        console.error('Error updating scope:', error)
        return false
      } finally {
        this.loading = false
      }
    },

    async deleteScope(id) {
      this.loading = true
      this.error = null
      try {
        await scopeService.deleteScope(id)
        this.scopes = this.scopes.filter(scope => scope.id !== id)
        return true
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to delete scope'
        console.error('Error deleting scope:', error)
        return false
      } finally {
        this.loading = false
      }
    }
  }
}) 