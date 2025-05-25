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
      console.log('Fetching user by id:', id)
      const response = await api.get(`/users/${id}`)
      console.log('User response:', response)
      return response
    } catch (error) {
      console.error('Error fetching user:', error)
      throw error
    }
  },
  
  async createUser(userData) {
    try {
      console.log('Creating user:', userData)
      const response = await api.post('/users', userData)
      console.log('Create user response:', response)
      return response
    } catch (error) {
      console.error('Error creating user:', error)
      throw error
    }
  },
  
  async updateUser(id, userData) {
    try {
      console.log('Updating user:', id, userData)
      const response = await api.put(`/users/${id}`, userData)
      console.log('Update user response:', response)
      return response
    } catch (error) {
      console.error('Error updating user:', error)
      throw error
    }
  },
  
  async deleteUser(id) {
    try {
      console.log('Deleting user:', id)
      const response = await api.delete(`/users/${id}`)
      console.log('Delete user response:', response)
      return response
    } catch (error) {
      console.error('Error deleting user:', error)
      throw error
    }
  }
}