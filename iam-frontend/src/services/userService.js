import api from './api'

export const userService = {
  async getUsers(params = {}) {
    try {
      const response = await api.get('/users', { params })
      return response
    } catch (error) {
      console.error('Error fetching users:', error)
      throw error
    }
  },
  
  async getUserById(id) {
    try {
      const response = await api.get(`/users/${id}`)
      return response
    } catch (error) {
      console.error('Error fetching user:', error)
      throw error
    }
  },
  
  async createUser(userData) {
    try {
      const response = await api.post('/users', userData)
      return response
    } catch (error) {
      console.error('Error creating user:', error)
      throw error
    }
  },
  
  async updateUser(id, userData) {
    try {
      const response = await api.put(`/users/${id}`, userData)
      return response
    } catch (error) {
      console.error('Error updating user:', error)
      throw error
    }
  },
  
  async deleteUser(id) {
    try {
      const response = await api.delete(`/users/${id}`)
      return response
    } catch (error) {
      console.error('Error deleting user:', error)
      throw error
    }
  }
}