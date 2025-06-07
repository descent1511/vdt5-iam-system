import api from './api'

export const productService = {
  async getAll() {
    const response = await api.get('/products')
    return response
  },

  async getById(id) {
    const response = await api.get(`/products/${id}`)
    return response.data || response
  },

  async create(productData) {
    const response = await api.post('/products', productData)
    return response
  },

  async update(id, productData) {
    const response = await api.put(`/products/${id}`, productData)
    return response
  },

  async delete(id) {
    const response = await api.delete(`/products/${id}`)
    return response
  },

  handleError(error) {
    if (error.response) {
      // The request was made and the server responded with a status code
      // that falls out of the range of 2xx
      const message = error.response.data?.message || 'An error occurred'
      return new Error(message)
    } else if (error.request) {
      // The request was made but no response was received
      return new Error('No response from server')
    } else {
      // Something happened in setting up the request that triggered an Error
      return new Error('Error setting up request')
    }
  }
} 