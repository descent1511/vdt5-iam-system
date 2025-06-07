<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Edit Role</h1>
      
      <router-link to="/roles" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left me-2"></i> Back to Roles
      </router-link>
    </div>
    
    <div v-if="roleStore.loading && !form.id" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-2">Loading role data...</p>
    </div>
    
    <div v-else-if="roleStore.error" class="alert alert-danger">
      {{ roleStore.error }}
    </div>
    
    <div v-else class="card">
      <div class="card-body">
        <form @submit.prevent="handleSubmit">
          <div class="row g-3">
            <!-- Basic Information -->
            <div class="col-12 mb-3">
              <h5 class="border-bottom pb-2">Basic Information</h5>
            </div>
            
            <div class="col-md-6">
              <div class="form-group">
                <label for="name" class="form-label">Role Name*</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="name" 
                  v-model="form.name" 
                  required
                  :disabled="form.name === 'admin'"
                  placeholder="e.g., Editor, Viewer, etc."
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
                  :disabled="form.name === 'admin'"
                  placeholder="Describe the purpose and responsibilities of this role"
                ></textarea>
                <div v-if="errors.description" class="text-danger small mt-1">
                  {{ errors.description }}
                </div>
              </div>
            </div>
            
            <!-- Permissions -->
            <div class="col-12 mb-3 mt-4">
              <h5 class="border-bottom pb-2">Permissions</h5>
              <p class="text-muted small">Select the permissions that will be granted to users with this role.</p>
              <PermissionTable v-model="form.permissions" :disabled="form.name === 'admin'" />
            </div>
            
            <!-- Submit buttons -->
            <div class="col-12 mt-4 d-flex justify-content-end gap-2">
              <router-link to="/roles" class="btn btn-outline-secondary">
                Cancel
              </router-link>
              <button 
                type="submit" 
                class="btn btn-primary"
                :disabled="roleStore.loading || form.name === 'admin'"
              >
                <span v-if="roleStore.loading" class="spinner-border spinner-border-sm me-2" role="status"></span>
                Update Role
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted, toRaw } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useRoleStore } from '../../stores/roles'
import { useToast } from 'vue-toastification'
import PermissionTable from '../../components/PermissionTable.vue'

// Store
const roleStore = useRoleStore()
const route = useRoute()
const router = useRouter()
const toast = useToast()

// Get role ID from route params
const roleId = route.params.id

// State
const form = reactive({
  id: null,
  name: '',
  description: '',
  permissions: []
})

const errors = reactive({
  name: '',
  description: ''
})

// Lifecycle hooks
onMounted(async () => {
  try {
    const roleData = await roleStore.fetchRoleById(roleId)
    
    // Populate form
    form.id = roleData.id;
    form.name = roleData.name;
    form.description = roleData.description;
    // This is the crucial part: extract permission names to pre-select checkboxes
    if (roleData.permissions) {
      form.permissions = roleData.permissions.map(p => p.name);
    }
    console.log("form", form);

  } catch (error) {
    toast.error('Failed to load role data.');
    console.error('Failed to load role data:', error)
  }
})

// Methods
function validateForm() {
  let valid = true
  
  // Reset errors
  Object.keys(errors).forEach(key => {
    errors[key] = ''
  })
  
  // Skip validation for admin role
  if (form.name === 'admin') return true
  
  // Validate name
  if (!form.name.trim()) {
    errors.name = 'Role name is required'
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
    // Skip update for admin role
    if (form.name === 'admin') return
    
    await roleStore.updateRole(roleId, {
      name: form.name,
      description: form.description,
      permissions: toRaw(form.permissions)
    })
    
    toast.success('Role updated successfully');
    router.push('/roles')
  } catch (error) {
    toast.error('Failed to update role');
    console.error('Failed to update role:', error)
  }
}
</script>

<style scoped>
/* Component-specific styles */
.card-header {
  border-bottom: none;
  padding: 1rem;
}

.card-header h6 {
  font-weight: 600;
}

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