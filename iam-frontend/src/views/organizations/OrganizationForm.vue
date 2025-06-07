<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">{{ isEdit ? 'Edit Organization' : 'Create Organization' }}</h1>
      
      <router-link to="/organizations" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left me-2"></i> Back to Organizations
      </router-link>
    </div>
    
    <div class="card">
      <div class="card-body">
        <form @submit.prevent="handleSubmit">
          <div class="row g-3">
            <!-- Basic Information -->
            <div class="col-12 mb-3">
              <h5 class="border-bottom pb-2">Basic Information</h5>
            </div>
            
            <div class="col-md-6">
              <div class="form-group">
                <label for="name" class="form-label">Organization Name*</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="name" 
                  v-model="form.name" 
                  required
                  placeholder="Enter organization name"
                >
                <div v-if="errors.name" class="text-danger small mt-1">
                  {{ errors.name }}
                </div>
              </div>
            </div>
            
            <div class="col-12">
              <div class="form-group">
                <label for="description" class="form-label">Description*</label>
                <textarea 
                  class="form-control" 
                  id="description" 
                  v-model="form.description" 
                  rows="3"
                  required
                  placeholder="Describe the organization"
                ></textarea>
                <div v-if="errors.description" class="text-danger small mt-1">
                  {{ errors.description }}
                </div>
              </div>
            </div>
            
            <div class="col-md-6">
              <div class="form-group">
                <label class="form-label">Active Status</label>
                <div class="form-check form-switch">
                  <input 
                    class="form-check-input" 
                    type="checkbox" 
                    role="switch" 
                    id="activeStatus" 
                    v-model="form.active"
                  >
                  <label class="form-check-label" for="activeStatus">
                    {{ form.active ? 'Active' : 'Inactive' }}
                  </label>
                </div>
              </div>
            </div>
            
            <!-- Submit buttons -->
            <div class="col-12 mt-4 d-flex justify-content-end gap-2">
              <router-link to="/organizations" class="btn btn-outline-secondary">
                Cancel
              </router-link>
              <button 
                type="submit" 
                class="btn btn-primary"
                :disabled="organizationStore.loading"
              >
                <span v-if="organizationStore.loading" class="spinner-border spinner-border-sm me-2" role="status"></span>
                {{ isEdit ? 'Update Organization' : 'Create Organization' }}
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useOrganizationStore } from '../../stores/organizations'
import { useToast } from 'vue-toastification'

// Stores
const organizationStore = useOrganizationStore()
const route = useRoute()
const router = useRouter()
const toast = useToast()

// Computed
const isEdit = computed(() => !!route.params.id)

// Form state
const form = reactive({
  name: '',
  description: '',
  active: true
})

const errors = reactive({
  name: '',
  description: ''
})

// Lifecycle hooks
onMounted(async () => {
  if (isEdit.value) {
    try {
      const organization = await organizationStore.fetchOrganization(route.params.id)
      
      // Populate form
      Object.keys(form).forEach(key => {
        if (organization[key] !== undefined) {
          form[key] = organization[key]
        }
      })
    } catch (error) {
      console.error('Failed to fetch organization:', error)
      toast.error('Failed to load organization data')
      router.push('/organizations')
    }
  }
})

// Methods
function validateForm() {
  let valid = true
  
  // Reset errors
  Object.keys(errors).forEach(key => {
    errors[key] = ''
  })
  
  // Validate name
  if (!form.name.trim()) {
    errors.name = 'Organization name is required'
    valid = false
  }
  
  // Validate description
  if (!form.description.trim()) {
    errors.description = 'Description is required'
    valid = false
  }
  
  return valid
}

async function handleSubmit() {
  if (!validateForm()) return
  
  try {
    if (isEdit.value) {
      await organizationStore.updateOrganization(route.params.id, form)
      toast.success('Organization updated successfully')
    } else {
      await organizationStore.createOrganization(form)
      toast.success('Organization created successfully')
    }
    router.push('/organizations')
  } catch (error) {
    console.error('Failed to save organization:', error)
    toast.error(`Failed to ${isEdit.value ? 'update' : 'create'} organization`)
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

.form-control:focus {
  border-color: #0d6efd;
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}

textarea.form-control {
  resize: vertical;
}
</style> 