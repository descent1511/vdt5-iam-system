<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Create Scope</h1>
      
      <router-link to="/scopes" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left me-2"></i> Back to Scopes
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
                <label for="name" class="form-label">Scope Name*</label>
                <input
                  type="text"
                  class="form-control"
                  id="name"
                  v-model="form.name"
                  required
                  placeholder="e.g., user_management, resource_access"
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
                  placeholder="Describe the purpose and access level of this scope"
                ></textarea>
                <div v-if="errors.description" class="text-danger small mt-1">
                  {{ errors.description }}
                </div>
              </div>
            </div>

            <!-- Permissions -->
            <div class="col-12 mb-3 mt-4">
              <h5 class="border-bottom pb-2">Permissions</h5>
              <p class="text-muted small">Select the permissions that will be included in this scope.</p>
              <PermissionTable v-model="form.permissions" />
            </div>

            <!-- Submit buttons -->
            <div class="col-12 mt-4 d-flex justify-content-end gap-2">
              <router-link to="/scopes" class="btn btn-outline-secondary">
                Cancel
              </router-link>
              <button 
                type="submit" 
                class="btn btn-primary"
                :disabled="scopeStore.loading"
              >
                <span v-if="scopeStore.loading" class="spinner-border spinner-border-sm me-2" role="status"></span>
                Create Scope
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
import { useScopeStore } from '../../stores/scopes'
import { useToast } from 'vue-toastification'
import PermissionTable from '../../components/PermissionTable.vue'

const router = useRouter()
const scopeStore = useScopeStore()
const toast = useToast()

const form = reactive({
  name: '',
  description: '',
  permissions: []
})

const errors = reactive({
  name: '',
  description: ''
})

function validateForm() {
  let valid = true
  
  // Reset errors
  Object.keys(errors).forEach(key => {
    errors[key] = ''
  })
  
  // Validate name
  if (!form.name.trim()) {
    errors.name = 'Scope name is required'
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
    await scopeStore.createScope(form)
    toast.success('Scope created successfully')
    router.push('/scopes')
  } catch (error) {
    toast.error('Failed to create scope')
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