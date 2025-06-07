<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Create Service</h1>
      <router-link to="/services" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left me-2"></i> Back to Services
      </router-link>
    </div>
    <div class="card">
      <div class="card-body">
        <form @submit.prevent="handleSubmit">
          <div class="row g-3">
            <div class="col-12 mb-3">
              <h5 class="border-bottom pb-2">Service Information</h5>
            </div>
            <div class="col-md-6">
              <div class="form-group">
                <label for="name" class="form-label">Service Name*</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="name" 
                  v-model="form.name" 
                  required
                  placeholder="e.g., User Service, Product Service, etc."
                >
                <div v-if="errors.name" class="text-danger small mt-1">
                  {{ errors.name }}
                </div>
              </div>
            </div>
            <div class="col-md-6">
              <div class="form-group">
                <label for="url" class="form-label">Service URL*</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="url" 
                  v-model="form.url" 
                  required
                  placeholder="e.g., http://localhost:8081/api/users"
                >
                <div v-if="errors.url" class="text-danger small mt-1">
                  {{ errors.url }}
                </div>
              </div>
            </div>
            <div class="col-12">
              <div class="form-group">
                <label for="description" class="form-label">Description</label>
                <textarea 
                  class="form-control" 
                  id="description" 
                  v-model="form.description" 
                  rows="3"
                  placeholder="Describe the purpose of this service"
                ></textarea>
              </div>
            </div>
            <div class="col-12 mt-4 d-flex justify-content-end gap-2">
              <router-link to="/services" class="btn btn-outline-secondary">
                Cancel
              </router-link>
              <button 
                type="submit" 
                class="btn btn-primary"
                :disabled="serviceStore.loading"
              >
                <span v-if="serviceStore.loading" class="spinner-border spinner-border-sm me-2" role="status"></span>
                Create Service
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useServiceRegistryStore } from '../../stores/serviceRegistry'
import { useToast } from 'vue-toastification'

const serviceStore = useServiceRegistryStore()
const router = useRouter()
const toast = useToast()

const form = reactive({
  name: '',
  description: '',
  url: ''
})

const errors = reactive({
  name: '',
  url: ''
})

function validateForm() {
  let valid = true
  Object.keys(errors).forEach(key => {
    errors[key] = ''
  })
  
  if (!form.name.trim()) {
    errors.name = 'Service name is required'
    valid = false
  }
  
  if (!form.url.trim()) {
    errors.url = 'Service URL is required'
    valid = false
  } else {
    try {
      new URL(form.url)
    } catch (_) {
      errors.url = 'Invalid URL format'
      valid = false
    }
  }
  
  return valid
}

async function handleSubmit() {
  if (!validateForm()) return
  
  try {
    await serviceStore.createService(form)
    toast.success('Service created successfully')
    router.push('/services')
  } catch (error) {
    toast.error('Failed to create service')
    console.error('Failed to create service:', error)
  }
}
</script>

<style scoped>
.card {
  border: none;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.08);
  border-radius: 12px;
}
.form-label {
  font-weight: 500;
  color: #2c3e50;
}
</style> 