import axios from 'axios'

// Create axios instance with default config
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  timeout: 10000
})

// Request interceptor
api.interceptors.request.use(
  (config) => {
    // Get organization ID from localStorage
    const organizationId = localStorage.getItem('organizationId')
    
    // If organization ID exists, add it to headers
    if (organizationId) {
      config.headers['X-Organization-ID'] = organizationId
    }
    
    // Get token from localStorage
    const token = localStorage.getItem('token')
    
    // If token exists, add it to headers
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    
    return config
  },
  (error) => {
    console.error('Request interceptor error:', error)
    return Promise.reject(error)
  }
)

// Response interceptor
api.interceptors.response.use(
  (response) => {
    return response.data
  },
  async (error) => {
    const originalRequest = error.config

    // If error is 401 and we haven't tried to refresh token yet
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      try {
        // Try to refresh token
        const refreshToken = localStorage.getItem('refreshToken')
        if (!refreshToken) {
          throw new Error("No refresh token found");
        }
        const response = await api.post('/auth/refresh-token', { refreshToken })
        
        // Update tokens in localStorage
        localStorage.setItem('token', response.accessToken)
        localStorage.setItem('refreshToken', response.refreshToken)
        
        // Update authorization header
        originalRequest.headers['Authorization'] = `Bearer ${response.accessToken}`
        
        // Retry the original request
        return api(originalRequest)
      } catch (refreshError) {
        console.error('Failed to refresh token:', refreshError)
        // If refresh token fails, redirect to login
        localStorage.removeItem('token')
        localStorage.removeItem('refreshToken')
        localStorage.removeItem('organizationId')
        window.location.href = '/login'
        return Promise.reject(refreshError)
      }
    }

    // Handle specific JWT algorithm mismatch error
    if (error.response?.data?.message?.includes('HS256')) {
        console.warn('Mismatched JWT algorithm. Clearing tokens and redirecting to login.')
        localStorage.removeItem('token')
        localStorage.removeItem('refreshToken')
        localStorage.removeItem('organizationId')
        window.location.href = '/login'
        return Promise.reject(error);
    }

    console.error('API Error:', error)
    console.error('Error response:', error.response)
    
    // Extract error message from response
    const errorMessage = error.response?.data?.message || error.message || 'An error occurred'
    return Promise.reject(new Error(errorMessage))
  }
)

export default api