import { defineStore } from 'pinia'
import { organizationService } from '../services/organizationService'

export const useOrganizationStore = defineStore('organizations', {
  state: () => ({
    organizations: [],
    loading: false,
    error: null
  }),

  getters: {
    totalOrganizations: (state) => state.organizations.length
  },

  actions: {
    async fetchOrganizations() {
      this.loading = true
      this.error = null
      try {
        this.organizations = await organizationService.getAllOrganizations()
        return this.organizations
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to fetch organizations'
        throw error
      } finally {
        this.loading = false
      }
    },

    async fetchOrganization(id) {
      this.loading = true
      this.error = null
      try {
        return await organizationService.getOrganization(id)
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to fetch organization'
        throw error
      } finally {
        this.loading = false
      }
    },

    async createOrganization(organization) {
      this.loading = true
      this.error = null
      try {
        console.log('Creating organization:', organization)
        const newOrganization = await organizationService.createOrganization(organization)
        this.organizations.push(newOrganization)
        return newOrganization
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to create organization'
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateOrganization(id, organization) {
      this.loading = true
      this.error = null
      try {
        const updatedOrganization = await organizationService.updateOrganization(id, organization)
        const index = this.organizations.findIndex(org => org.id === id)
        if (index !== -1) {
          this.organizations[index] = updatedOrganization
        }
        return updatedOrganization
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to update organization'
        throw error
      } finally {
        this.loading = false
      }
    },

    async deleteOrganization(id) {
      this.loading = true
      this.error = null
      try {
        await organizationService.deleteOrganization(id)
        this.organizations = this.organizations.filter(org => org.id !== id)
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to delete organization'
        throw error
      } finally {
        this.loading = false
      }
    }
  }
}) 