import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { resourceService } from '../services/resourceService'
import { useToast } from 'vue-toastification'

export const useResourceStore = defineStore('resources', () => {
  const toast = useToast()
  
  // State
  const resources = ref([])
  const currentResource = ref(null)
  const loading = ref(false)
  const error = ref(null)
  const totalResources = ref(0)

  // Actions
  async function fetchResources() {
    try {
      loading.value = true
      error.value = null
      
      const data = await resourceService.getResources()
      resources.value = data
      totalResources.value = data.length
      return data
    } catch (err) {
      error.value = err.message || 'Failed to fetch resources'
      toast.error(error.value)
    } finally {
      loading.value = false
    }
  }

  async function fetchResourceById(id) {
    try {
      loading.value = true
      error.value = null
      
      const data = await resourceService.getResourceById(id)
      currentResource.value = data
      return data
    } catch (err) {
      error.value = err.message || 'Failed to fetch resource'
      toast.error(error.value)
    } finally {
      loading.value = false
    }
  }

  async function createResource(resourceData) {
    try {
      loading.value = true
      error.value = null
      
      const data = await resourceService.createResource(resourceData)
      resources.value.push(data)
      
      toast.success('Resource created successfully')
      return data
    } catch (err) {
      error.value = err.message || 'Failed to create resource'
      toast.error(error.value)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updateResource(id, resourceData) {
    try {
      loading.value = true
      error.value = null
      
      const data = await resourceService.updateResource(id, resourceData)
      
      // Update the resource in the resources array
      const index = resources.value.findIndex(resource => resource.id === id)
      if (index !== -1) {
        resources.value[index] = { ...resources.value[index], ...resourceData }
      }
      
      toast.success('Resource updated successfully')
      return data
    } catch (err) {
      error.value = err.message || 'Failed to update resource'
      toast.error(error.value)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function deleteResource(id) {
    try {
      loading.value = true
      error.value = null
      
      await resourceService.deleteResource(id)
      
      // Remove the resource from the resources array
      resources.value = resources.value.filter(resource => resource.id !== id)
      
      toast.success('Resource deleted successfully')
      return true
    } catch (err) {
      error.value = err.message || 'Failed to delete resource'
      toast.error(error.value)
      return false
    } finally {
      loading.value = false
    }
  }
  
  // Reset store state
  function resetState() {
    resources.value = []
    currentResource.value = null
    loading.value = false
    error.value = null
  }

  return {
    // State
    resources,
    currentResource,
    loading,
    error,
    
    // Getters
    totalResources,

    
    // Actions
    fetchResources,
    fetchResourceById,
    createResource,
    updateResource,
    deleteResource,
    resetState
  }
})