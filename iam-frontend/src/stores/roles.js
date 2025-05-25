import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { roleService } from '../services/roleService'
import { useToast } from 'vue-toastification'

export const useRoleStore = defineStore('roles', () => {
  const toast = useToast()
  
  // State
  const roles = ref([])
  const currentRole = ref(null)
  const loading = ref(false)
  const error = ref(null)
  const totalRoles = ref(0)

  // Actions
  async function fetchRoles() {
    try {
      loading.value = true
      error.value = null
    
      const data = await roleService.getRoles()
      roles.value = data
      totalRoles.value = data.length
      return data
    } catch (err) {
      error.value = err.message || 'Failed to fetch roles'
      toast.error(error.value)
    } finally {
      loading.value = false
    }
  }

  async function fetchRoleById(id) {
    try {
      loading.value = true
      error.value = null
      
      const data = await roleService.getRoleById(id)
      currentRole.value = data
      return data
    } catch (err) {
      error.value = err.message || 'Failed to fetch role'
      toast.error(error.value)
    } finally {
      loading.value = false
    }
  }

  async function createRole(roleData) {
    try {
      loading.value = true
      error.value = null
      
      const data = await roleService.createRole(roleData)
      roles.value.push(data)
      
      toast.success('Role created successfully')
      return data
    } catch (err) {
      error.value = err.message || 'Failed to create role'
      toast.error(error.value)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updateRole(id, roleData) {
    try {
      loading.value = true
      error.value = null
      
      const data = await roleService.updateRole(id, roleData)
      
      // Update the role in the roles array
      const index = roles.value.findIndex(role => role.id === id)
      if (index !== -1) {
        roles.value[index] = { ...roles.value[index], ...roleData }
      }
      
      toast.success('Role updated successfully')
      return data
    } catch (err) {
      error.value = err.message || 'Failed to update role'
      toast.error(error.value)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function deleteRole(id) {
    try {
      loading.value = true
      error.value = null
      
      await roleService.deleteRole(id)
      
      // Remove the role from the roles array
      roles.value = roles.value.filter(role => role.id !== id)
      
      toast.success('Role deleted successfully')
      return true
    } catch (err) {
      error.value = err.message || 'Failed to delete role'
      toast.error(error.value)
      return false
    } finally {
      loading.value = false
    }
  }
  
  // Reset store state
  function resetState() {
    roles.value = []
    currentRole.value = null
    loading.value = false
    error.value = null
  }

  return {
    // State
    roles,
    currentRole,
    loading,
    error,
    
    // Getters
    totalRoles,
    
    // Actions
    fetchRoles,
    fetchRoleById,
    createRole,
    updateRole,
    deleteRole,
    resetState
  }
})