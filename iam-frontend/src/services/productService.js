import api from './api'

export const productService = {
  async getAll() {
    console.log('Request: GET /products')
    const response = await api.get('/products')
    console.log('Response:', response)
    return response
  },

  async getById(id) {
    console.log('Request: GET /products/' + id)
    const response = await api.get(`/products/${id}`)
    console.log('Response:', response)
    return response.data || response
  },

  async create(productData) {
    console.log('Request: POST /products', productData)
    const response = await api.post('/products', productData)
    console.log('Response:', response)
    return response
  },

  async update(id, productData) {
    console.log('Request: PUT /products/' + id, productData)
    const response = await api.put(`/products/${id}`, productData)
    console.log('Response:', response)
    return response
  },

  async delete(id) {
    console.log('Request: DELETE /products/' + id)
    const response = await api.delete(`/products/${id}`)
    console.log('Response:', response)
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