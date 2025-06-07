import { defineStore } from 'pinia'
import { productService } from '../services/productService'

export const useProductsStore = defineStore('products', {
  state: () => ({
    products: [],
    loading: false,
    error: null,
    selectedProduct: null
  }),

  getters: {
    getProducts: (state) => state.products,
    getLoading: (state) => state.loading,
    getError: (state) => state.error,
    getSelectedProduct: (state) => state.selectedProduct
  },

  actions: {
    async fetchProducts() {
      this.loading = true
      this.error = null
      try {
        const response = await productService.getAll()
        this.products = response
      } catch (error) {
        this.error = error.message
        console.error('Error fetching products:', error)
      } finally {
        this.loading = false
      }
    },

    async fetchProduct(id) {
      this.loading = true
      this.error = null
      try {
        const response = await productService.getById(id)
        this.selectedProduct = response
        return response
      } catch (error) {
        this.error = error.message
        console.error('Store: Error fetching product:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async createProduct(productData) {
      this.loading = true
      this.error = null
      try {
        const response = await productService.create(productData)
        this.products.push(response)
        return response
      } catch (error) {
        this.error = error.message
        console.error('Error creating product:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateProduct(id, productData) {
      this.loading = true
      this.error = null
      try {
        const response = await productService.update(id, productData)
        const index = this.products.findIndex(p => p.id === id)
        if (index !== -1) {
          this.products[index] = response.data
        }
        return response.data
      } catch (error) {
        this.error = error.message
        console.error('Error updating product:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async deleteProduct(id) {
      this.loading = true
      this.error = null
      try {
        await productService.delete(id)
        this.products = this.products.filter(p => p.id !== id)
      } catch (error) {
        this.error = error.message
        console.error('Error deleting product:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    clearError() {
      this.error = null
    },

    clearSelectedProduct() {
      this.selectedProduct = null
    }
  }
}) 