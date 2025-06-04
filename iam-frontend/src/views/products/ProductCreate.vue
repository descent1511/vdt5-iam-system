<template>
  <div class="container-fluid">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h3 mb-0">Create Product</h1>
      <button 
        class="btn btn-outline-secondary"
        @click="$router.push('/products')"
      >
        <i class="bi bi-arrow-left me-2"></i>Back to Products
      </button>
    </div>

    <div class="card">
      <div class="card-body">
        <form @submit.prevent="createProduct">
          <div class="row">
            <div class="col-md-6 mb-3">
              <label for="name" class="form-label">Name</label>
              <input
                type="text"
                class="form-control"
                id="name"
                v-model="form.name"
                required
              >
            </div>

            <div class="col-md-6 mb-3">
              <label for="price" class="form-label">Price</label>
              <div class="input-group">
                <span class="input-group-text">$</span>
                <input
                  type="number"
                  class="form-control"
                  id="price"
                  v-model="form.price"
                  step="0.01"
                  min="0"
                  required
                >
              </div>
            </div>

            <div class="col-md-6 mb-3">
              <label for="stock" class="form-label">Stock</label>
              <input
                type="number"
                class="form-control"
                id="stock"
                v-model="form.stock"
                required
                min="0"
              >
            </div>

            <div class="col-12 mb-3">
              <label for="description" class="form-label">Description</label>
              <textarea
                class="form-control"
                id="description"
                v-model="form.description"
                rows="3"
              ></textarea>
            </div>

            <div class="col-md-6 mb-3">
              <label for="status" class="form-label">Status</label>
              <select
                class="form-select"
                id="status"
                v-model="form.status"
                required
              >
                <option value="ACTIVE">Active</option>
                <option value="INACTIVE">Inactive</option>
              </select>
            </div>
          </div>

          <div class="d-flex justify-content-end gap-2">
            <button 
              type="button"
              class="btn btn-outline-secondary"
              @click="$router.push('/products')"
            >
              Cancel
            </button>
            <button 
              type="submit"
              class="btn btn-primary"
              :disabled="productsStore.getLoading"
            >
              <span 
                v-if="productsStore.getLoading" 
                class="spinner-border spinner-border-sm me-2"
                role="status"
              ></span>
              Create Product
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useProductsStore } from '../../stores/products'

const router = useRouter()
const productsStore = useProductsStore()

const form = ref({
  name: '',
  description: '',
  price: 0,
  stock: 0,
  status: 'ACTIVE'
})

async function createProduct() {
  try {
    await productsStore.createProduct(form.value)
    router.push('/products')
  } catch (error) {
    console.error('Error creating product:', error)
  }
}
</script>

<style scoped>
.form-label {
  font-weight: 500;
}

.input-group-text {
  background-color: var(--bs-light);
}
</style> 