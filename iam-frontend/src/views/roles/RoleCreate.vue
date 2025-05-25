<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Create Role</h1>
      
      <router-link to="/roles" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left me-2"></i> Back to Roles
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
                <label for="name" class="form-label">Role Name*</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="name" 
                  v-model="form.name" 
                  required
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
            </div>
            
            <div class="col-12">
              <div class="row g-4">
                <!-- User Management -->
                <div class="col-md-6 col-lg-4">
                  <div class="card h-100">
                    <div class="card-header">
                      <h6 class="mb-0">User Management</h6>
                    </div>
                    <div class="card-body">
                      <div class="form-check mb-2">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          id="permission-read-users" 
                          value="read:users"
                          v-model="form.permissions"
                        >
                        <label class="form-check-label" for="permission-read-users">
                          View Users
                        </label>
                      </div>
                      <div class="form-check mb-2">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          id="permission-write-users" 
                          value="write:users"
                          v-model="form.permissions"
                        >
                        <label class="form-check-label" for="permission-write-users">
                          Create/Edit Users
                        </label>
                      </div>
                      <div class="form-check">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          id="permission-delete-users" 
                          value="delete:users"
                          v-model="form.permissions"
                        >
                        <label class="form-check-label" for="permission-delete-users">
                          Delete Users
                        </label>
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- Role Management -->
                <div class="col-md-6 col-lg-4">
                  <div class="card h-100">
                    <div class="card-header">
                      <h6 class="mb-0">Role Management</h6>
                    </div>
                    <div class="card-body">
                      <div class="form-check mb-2">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          id="permission-read-roles" 
                          value="read:roles"
                          v-model="form.permissions"
                        >
                        <label class="form-check-label" for="permission-read-roles">
                          View Roles
                        </label>
                      </div>
                      <div class="form-check mb-2">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          id="permission-write-roles" 
                          value="write:roles"
                          v-model="form.permissions"
                        >
                        <label class="form-check-label" for="permission-write-roles">
                          Create/Edit Roles
                        </label>
                      </div>
                      <div class="form-check">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          id="permission-delete-roles" 
                          value="delete:roles"
                          v-model="form.permissions"
                        >
                        <label class="form-check-label" for="permission-delete-roles">
                          Delete Roles
                        </label>
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- Resource Management -->
                <div class="col-md-6 col-lg-4">
                  <div class="card h-100">
                    <div class="card-header">
                      <h6 class="mb-0">Resource Management</h6>
                    </div>
                    <div class="card-body">
                      <div class="form-check mb-2">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          id="permission-read-resources" 
                          value="read:resources"
                          v-model="form.permissions"
                        >
                        <label class="form-check-label" for="permission-read-resources">
                          View Resources
                        </label>
                      </div>
                      <div class="form-check mb-2">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          id="permission-write-resources" 
                          value="write:resources"
                          v-model="form.permissions"
                        >
                        <label class="form-check-label" for="permission-write-resources">
                          Create/Edit Resources
                        </label>
                      </div>
                      <div class="form-check">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          id="permission-delete-resources" 
                          value="delete:resources"
                          v-model="form.permissions"
                        >
                        <label class="form-check-label" for="permission-delete-resources">
                          Delete Resources
                        </label>
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- Policy Management -->
                <div class="col-md-6 col-lg-4">
                  <div class="card h-100">
                    <div class="card-header">
                      <h6 class="mb-0">Policy Management</h6>
                    </div>
                    <div class="card-body">
                      <div class="form-check mb-2">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          id="permission-read-policies" 
                          value="read:policies"
                          v-model="form.permissions"
                        >
                        <label class="form-check-label" for="permission-read-policies">
                          View Policies
                        </label>
                      </div>
                      <div class="form-check mb-2">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          id="permission-write-policies" 
                          value="write:policies"
                          v-model="form.permissions"
                        >
                        <label class="form-check-label" for="permission-write-policies">
                          Create/Edit Policies
                        </label>
                      </div>
                      <div class="form-check">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          id="permission-delete-policies" 
                          value="delete:policies"
                          v-model="form.permissions"
                        >
                        <label class="form-check-label" for="permission-delete-policies">
                          Delete Policies
                        </label>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- Submit buttons -->
            <div class="col-12 mt-4 d-flex justify-content-end gap-2">
              <router-link to="/roles" class="btn btn-outline-secondary">
                Cancel
              </router-link>
              <button 
                type="submit" 
                class="btn btn-primary"
                :disabled="roleStore.loading"
              >
                <span v-if="roleStore.loading" class="spinner-border spinner-border-sm me-2" role="status"></span>
                Create Role
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
import { useRoleStore } from '../../stores/roles'

// Store
const roleStore = useRoleStore()
const router = useRouter()

// State
const form = reactive({
  name: '',
  description: '',
  permissions: []
})

const errors = reactive({
  name: '',
  description: ''
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
    await roleStore.createRole(form)
    router.push('/roles')
  } catch (error) {
    console.error('Failed to create role:', error)
  }
}
</script>

<style scoped>
/* Component-specific styles */
</style>