<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Edit Service</h1>
      <router-link to="/services" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left me-2"></i> Back to Services
      </router-link>
    </div>
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>
    <div v-else class="card">
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
                Update Service
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useServiceRegistryStore } from '../../stores/serviceRegistry'
import { useToast } from 'vue-toastification'

const serviceStore = useServiceRegistryStore()
const router = useRouter()
const route = useRoute()
const toast = useToast()
const loading = ref(true)
const serviceId = route.params.id

const form = reactive({
  name: '',
  description: '',
  url: ''
})

const errors = reactive({
  name: '',
  url: ''
})

onMounted(async () => {
  try {
    const service = await serviceStore.getServiceById(serviceId)
    if (service) {
      form.name = service.name
      form.description = service.description
      form.url = service.url
    }
  } catch (error) {
    toast.error('Failed to load service details')
    router.push('/services')
  } finally {
    loading.value = false
  }
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
    await serviceStore.updateService(serviceId, form)
    toast.success('Service updated successfully')
    router.push('/services')
  } catch (error) {
    toast.error('Failed to update service')
    console.error('Failed to update service:', error)
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