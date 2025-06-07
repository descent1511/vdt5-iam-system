import { defineStore } from 'pinia'
import { ref } from 'vue'
import { serviceRegistryService } from '../services/serviceRegistryService'
import { useToast } from 'vue-toastification'

export const useServiceRegistryStore = defineStore('serviceRegistry', () => {
  const toast = useToast()

  const services = ref([])
  const loading = ref(false)
  const error = ref(null)
  const totalServices = ref(0)

  async function fetchServices() {
    try {
      loading.value = true
      error.value = null
    
      const data = await serviceRegistryService.getServices()
      services.value = data
      totalServices.value = data.length
      return data
    } catch (err) {
      error.value = err.message || 'Failed to fetch services'
      toast.error(error.value)
    } finally {
      loading.value = false
    }
  }

  async function getServiceById(id) {
    try {
      loading.value = true
      error.value = null
      const data = await serviceRegistryService.getServiceById(id)
      return data
    } catch (err) {
      error.value = err.message || 'Failed to fetch service'
      toast.error(error.value)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updateService(id, serviceData) {
    try {
      loading.value = true
      error.value = null

      const data = await serviceRegistryService.updateService(id, serviceData)

      const index = services.value.findIndex(s => s.id === id)
      if (index !== -1) {
        services.value[index] = { ...services.value[index], ...data }
      }

      toast.success('Service updated successfully')
      return data
    } catch (err) {
      error.value = err.message || 'Failed to update service'
      toast.error(error.value)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function createService(serviceData) {
    try {
      loading.value = true
      error.value = null

      const data = await serviceRegistryService.createService(serviceData)
      services.value.push(data)

      toast.success('Service created successfully')
      return data
    } catch (err) {
      error.value = err.message || 'Failed to create service'
      toast.error(error.value)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function deleteService(id) {
    try {
      loading.value = true
      error.value = null

      await serviceRegistryService.deleteService(id)
      
      const index = services.value.findIndex(s => s.id === id)
      if (index !== -1) {
        services.value.splice(index, 1)
        totalServices.value--
      }

      toast.success('Service deleted successfully')
      return true
    } catch (err) {
      error.value = err.message || 'Failed to delete service'
      toast.error(error.value)
      return false
    } finally {
      loading.value = false
    }
  }

  function resetState() {
    services.value = []
    loading.value = false
    error.value = null
    totalServices.value = 0
  }

  return {
    services,
    loading,
    error,
    totalServices,
    fetchServices,
    getServiceById,
    createService,
    updateService,
    deleteService,
    resetState
  }
}) 