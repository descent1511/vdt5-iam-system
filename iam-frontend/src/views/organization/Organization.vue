<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Organization Settings</h1>
      
      <button class="btn btn-primary" @click="saveChanges" :disabled="loading">
        <i class="bi bi-save me-2"></i> Save Changes
      </button>
    </div>

    <div class="row g-4">
      <!-- Organization Profile -->
      <div class="col-lg-8">
        <div class="card">
          <div class="card-header">
            <h5 class="card-title mb-0">Organization Profile</h5>
          </div>
          <div class="card-body">
            <form @submit.prevent="saveChanges">
              <div class="mb-3">
                <label class="form-label">Organization Name</label>
                <input 
                  type="text" 
                  class="form-control" 
                  v-model="organization.name"
                  required
                >
              </div>
              
              <div class="mb-3">
                <label class="form-label">Description</label>
                <textarea 
                  class="form-control" 
                  v-model="organization.description"
                  rows="3"
                ></textarea>
              </div>
              
              <div class="row mb-3">
                <div class="col-md-6">
                  <label class="form-label">Email</label>
                  <input 
                    type="email" 
                    class="form-control" 
                    v-model="organization.email"
                    required
                  >
                </div>
                
                <div class="col-md-6">
                  <label class="form-label">Phone</label>
                  <input 
                    type="tel" 
                    class="form-control" 
                    v-model="organization.phone"
                  >
                </div>
              </div>
              
              <div class="mb-3">
                <label class="form-label">Website</label>
                <input 
                  type="url" 
                  class="form-control" 
                  v-model="organization.website"
                >
              </div>
              
              <div class="mb-3">
                <label class="form-label">Address</label>
                <input 
                  type="text" 
                  class="form-control" 
                  v-model="organization.address"
                >
              </div>
            </form>
          </div>
        </div>
      </div>
      
      <!-- Organization Info -->
      <div class="col-lg-4">
        <div class="card mb-4">
          <div class="card-header">
            <h5 class="card-title mb-0">Organization Info</h5>
          </div>
          <div class="card-body">
            <div class="mb-3">
              <small class="text-muted d-block">Created</small>
              <div>{{ formatDate(organization.createdAt) }}</div>
            </div>
            
            <div class="mb-3">
              <small class="text-muted d-block">Last Updated</small>
              <div>{{ formatDate(organization.updatedAt) }}</div>
            </div>
            
            <div>
              <small class="text-muted d-block">Organization ID</small>
              <div>{{ organization.id }}</div>
            </div>
          </div>
        </div>
        
        <div class="card">
          <div class="card-header">
            <h5 class="card-title mb-0">Danger Zone</h5>
          </div>
          <div class="card-body">
            <p class="text-muted small">
              These actions are destructive and should be used with caution.
            </p>
            
            <button class="btn btn-outline-danger w-100">
              <i class="bi bi-trash me-2"></i> Delete Organization
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useToast } from 'vue-toastification'
import { mockOrganization } from '../../services/mockData'

const toast = useToast()
const organization = ref({})
const loading = ref(false)

onMounted(() => {
  organization.value = { ...mockOrganization }
})

function formatDate(dateString) {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

async function saveChanges() {
  try {
    loading.value = true
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000))
    toast.success('Organization settings updated successfully')
  } catch (error) {
    toast.error('Failed to update organization settings')
  } finally {
    loading.value = false
  }
}
</script>