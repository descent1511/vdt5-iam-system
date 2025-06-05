import { defineStore } from 'pinia'
import { ref } from 'vue'
import { userService } from '../services/userService'
import { useToast } from 'vue-toastification'

export const useUserStore = defineStore('users', () => {
  const toast = useToast()
  
  // State
  const users = ref([])
  const currentUser = ref(null)
  const loading = ref(false)
  const error = ref(null)
  const totalUsers = ref(0)
  const pagination = ref({
    page: 1,
    limit: 10,
    totalPages: 0
  })

  // Actions
  async function fetchUsers(params = {}) {
    try {
      loading.value = true
      error.value = null
      
      const data = await userService.getUsers(params)
      users.value =  data
      totalUsers.value =  data.length
      pagination.value = {
        page: params.page || 1,
        limit: params.limit || 10,
        totalPages: Math.ceil((data.total || data.length) / (params.limit || 10))
      }
      
      return data
    } catch (err) {
      error.value = err.message || 'Failed to fetch users'
      toast.error(error.value)
    } finally {
      loading.value = false
    }
  }

  async function fetchUserById(id) {
    try {
      loading.value = true
      error.value = null
      
      const data = await userService.getUserById(id)
      currentUser.value = data
      return data
    } catch (err) {
      error.value = err.message || 'Failed to fetch user'
      toast.error(error.value)
    } finally {
      loading.value = false
    }
  }

  async function createUser(userData) {
    try {
      loading.value = true
      error.value = null
      
      const data = await userService.createUser(userData)
      users.value = [...users.value, data]
      totalUsers.value++
      toast.success('User created successfully')
      return data
    } catch (err) {
      error.value = err.message || 'Failed to create user'
      toast.error(error.value)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updateUser(id, userData) {
    try {
      loading.value = true
      error.value = null
      const data = await userService.updateUser(id, userData)
      
      // Update the user in the users array
      const index = users.value.findIndex(user => user.id === id)
      if (index !== -1) {
        users.value = [
          ...users.value.slice(0, index),
          { ...users.value[index], ...userData },
          ...users.value.slice(index + 1)
        ]
      }
      
      toast.success('User updated successfully')
      return data
    } catch (err) {
      error.value = err.message || 'Failed to update user'
      toast.error(error.value)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function deleteUser(id) {
    try {
      loading.value = true
      error.value = null
      
      await userService.deleteUser(id)
      
      // Remove the user from the users array
      users.value = users.value.filter(user => user.id !== id)
      totalUsers.value--
      
      toast.success('User deleted successfully')
      return true
    } catch (err) {
      error.value = err.message || 'Failed to delete user'
      toast.error(error.value)
      return false
    } finally {
      loading.value = false
    }
  }
  
  // Reset store state
  function resetState() {
    users.value = []
    currentUser.value = null
    loading.value = false
    error.value = null
    totalUsers.value = 0
    pagination.value = {
      page: 1,
      limit: 10,
      totalPages: 0
    }
  }

  return {
    // State
    users,
    currentUser,
    loading,
    error,
    totalUsers,
    pagination,
    
    // Actions
    fetchUsers,
    fetchUserById,
    createUser,
    updateUser,
    deleteUser,
    resetState
  }
})