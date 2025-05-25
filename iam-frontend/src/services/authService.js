import api from './api'

export const authService = {
  async signup(userData) {
    const response = await api.post('/auth/signup', userData)
    return response
  },

  async login(credentials) {
    try {
      const response = await api.post('/auth/login', credentials)
      return response
    } catch (error) {
      console.error('Login error:', error)
      throw error
    }
  },

  async logout() {
    const response = await api.post('/auth/logout')
    return response
  },
  async getCurrentUser() {
    const response = await api.get('/users/me')
    return response
  }
  // async refreshToken() {
  //   const refreshToken = localStorage.getItem('refreshToken')
  //   if (!refreshToken) {
  //     throw new Error('No refresh token available')
  //   }
    
  //   const response = await api.post('/auth/refresh', { refreshToken })
  //   return response
  // }
}