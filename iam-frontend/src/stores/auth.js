import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import { authService } from '../services/authService'

export const useAuthStore = defineStore('auth', () => {
  const router = useRouter()
  const toast = useToast()

  // State
  const user = ref(null)
  const token = ref(localStorage.getItem('token'))
  const loading = ref(false)
  const error = ref(null)

  // Getters
  const isAuthenticated = computed(() => !!token.value)

  // Permission checking
  function can(permissions) {
    console.log('Checking permission:', permissions)
    console.log('Current user:', user.value)
    console.log('User permissions:', user.value?.permissions)
    
    if (!user.value?.permissions) {
      console.log('No permissions found for user')
      return false
    }
    if (Array.isArray(permissions)) {
      const result = permissions.every(permission => user.value.permissions.includes(permission))
      console.log('Array permission check result:', result)
      return result
    }
    const result = user.value.permissions.includes(permissions)
    console.log('Single permission check result:', result)
    return result
  }

  // Get current user info
  async function getCurrentUser() {
    try {
      const response = await authService.getCurrentUser()
      user.value = response
      return response
    } catch (err) {
      console.error('Failed to get current user:', err)
      throw err
    }
  }

  // Initialize auth state
  async function init() {
    const storedToken = localStorage.getItem('token')
    if (storedToken==null) {
      return
    }

    try {
      // Get current user info
      await getCurrentUser()
    } catch (err) {
      // If getting user info fails, try to refresh token
      try {
        await refreshToken()
        // After refreshing token, get user info again
        await getCurrentUser()
      } catch (refreshErr) {
        console.error('Failed to refresh token:', refreshErr)
        logout()
      }
    }
  }

  // Actions
  async function login(credentials) {
    try {
      loading.value = true
      error.value = null
      const response = await authService.login(credentials)
      if (!response || !response.accessToken) {
        throw new Error('Invalid response format - missing access token')
      }

      localStorage.setItem('token', response.accessToken)
      localStorage.setItem('refreshToken', response.refreshToken)
      token.value = response.accessToken

      // Get user info after successful login
      await getCurrentUser()

      toast.success('Login successful')
      router.push('/')
      return true
    } catch (err) {
      console.error('Login error in store:', err)
      error.value = err.message || 'Login failed'
      toast.error(error.value)
      return false
    } finally {
      loading.value = false
    }
  }

  async function signup(userData) {
    try {
      loading.value = true
      error.value = null

      await authService.signup(userData)
      toast.success('Registration successful! Please login.')
      router.push('/login')
      return true
    } catch (err) {
      error.value = err.message || 'Registration failed'
      toast.error(error.value)
      return false
    } finally {
      loading.value = false
    }
  }

  async function logout() {
    try {
      if (token.value) {
        await authService.logout()
      }
    } catch (err) {
      console.warn('Logout error:', err)
    } finally {
      // Clear state and storage
      user.value = null
      token.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      router.push('/login')
      toast.info('Logged out successfully')
    }
  }

  async function refreshToken() {
    try {
      const response = await authService.refreshToken()
      if (response.accessToken) {
        token.value = response.accessToken
        localStorage.setItem('token', response.accessToken)
        localStorage.setItem('refreshToken', response.refreshToken)
      }
      return true
    } catch (err) {
      throw err
    }
  }

  return {
    // State
    user,
    token,
    loading,
    error,

    // Getters
    isAuthenticated,

    // Methods
    init,
    login,
    signup,
    logout,
    refreshToken,
    can,
    getCurrentUser
  }
})