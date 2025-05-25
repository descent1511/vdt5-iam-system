import { defineStore } from 'pinia'
import { ref } from 'vue'
import { organizationService } from '../services/organizationService'
import { useToast } from 'vue-toastification'

export const useOrganizationStore = defineStore('organization', () => {
  const toast = useToast()
  
  // State
  const organization = ref(null)
  const departments = ref([])
  const loading = ref(false)
  const error = ref(null)

  // Actions
  async function fetchOrganization() {
    try {
      loading.value = true
      error.value = null
      
      const data = await organizationService.getOrganization()
      organization.value = data
      return data
    } catch (err) {
      error.value = err.message || 'Failed to fetch organization'
      toast.error(error.value)
    } finally {
      loading.value = false
    }
  }

  async function updateOrganization(orgData) {
    try {
      loading.value = true
      error.value = null
      
      const data = await organizationService.updateOrganization(orgData)
      organization.value = { ...organization.value, ...orgData }
      
      toast.success('Organization updated successfully')
      return data
    } catch (err) {
      error.value = err.message || 'Failed to update organization'
      toast.error(error.value)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function fetchDepartments() {
    try {
      loading.value = true
      error.value = null
      
      const data = await organizationService.getDepartments()
      departments.value = data
      return data
    } catch (err) {
      error.value = err.message || 'Failed to fetch departments'
      toast.error(error.value)
    } finally {
      loading.value = false
    }
  }

  async function createDepartment(departmentData) {
    try {
      loading.value = true
      error.value = null
      
      const data = await organizationService.createDepartment(departmentData)
      departments.value.push(data)
      
      toast.success('Department created successfully')
      return data
    } catch (err) {
      error.value = err.message || 'Failed to create department'
      toast.error(error.value)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updateDepartment(id, departmentData) {
    try {
      loading.value = true
      error.value = null
      
      const data = await organizationService.updateDepartment(id, departmentData)
      
      // Update the department in the departments array
      const index = departments.value.findIndex(dept => dept.id === id)
      if (index !== -1) {
        departments.value[index] = { ...departments.value[index], ...departmentData }
      }
      
      toast.success('Department updated successfully')
      return data
    } catch (err) {
      error.value = err.message || 'Failed to update department'
      toast.error(error.value)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function deleteDepartment(id) {
    try {
      loading.value = true
      error.value = null
      
      await organizationService.deleteDepartment(id)
      
      // Remove the department from the departments array
      departments.value = departments.value.filter(dept => dept.id !== id)
      
      toast.success('Department deleted successfully')
      return true
    } catch (err) {
      error.value = err.message || 'Failed to delete department'
      toast.error(error.value)
      return false
    } finally {
      loading.value = false
    }
  }
  
  // Reset store state
  function resetState() {
    organization.value = null
    departments.value = []
    loading.value = false
    error.value = null
  }

  return {
    // State
    organization,
    departments,
    loading,
    error,
    
    // Actions
    fetchOrganization,
    updateOrganization,
    fetchDepartments,
    createDepartment,
    updateDepartment,
    deleteDepartment,
    resetState
  }
})