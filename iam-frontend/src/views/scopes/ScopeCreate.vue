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
              
              <div class="table-responsive">
                <table class="table">
                  <thead>
                    <tr>
                      <th>Management Type</th>
                      <th class="text-center">
                        <span class="permission-type permission-read">Read</span>
                      </th>
                      <th class="text-center">
                        <span class="permission-type permission-write">Create</span>
                      </th>
                      <th class="text-center">
                        <span class="permission-type permission-info">Update</span>
                      </th>
                      <th class="text-center">
                        <span class="permission-type permission-delete">Delete</span>
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    <!-- User Management -->
                    <tr>
                      <td>
                        <i class="bi bi-people"></i>
                        User Management
                      </td>
                      <td class="text-center">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :checked="hasUserRead"
                          @change="togglePermission('users', 'read')"
                        >
                      </td>
                      <td class="text-center">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :checked="hasUserCreate"
                          @change="togglePermission('users', 'create')"
                        >
                      </td>
                      <td class="text-center">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :checked="hasUserUpdate"
                          @change="togglePermission('users', 'update')"
                        >
                      </td>
                      <td class="text-center">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :checked="hasUserDelete"
                          @change="togglePermission('users', 'delete')"
                        >
                      </td>
                    </tr>
                    
                    <!-- Role Management -->
                    <tr>
                      <td>
                        <i class="bi bi-shield-lock"></i>
                        Role Management
                      </td>
                      <td class="text-center">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :checked="hasRoleRead"
                          @change="togglePermission('roles', 'read')"
                        >
                      </td>
                      <td class="text-center">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :checked="hasRoleCreate"
                          @change="togglePermission('roles', 'create')"
                        >
                      </td>
                      <td class="text-center">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :checked="hasRoleUpdate"
                          @change="togglePermission('roles', 'update')"
                        >
                      </td>
                      <td class="text-center">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :checked="hasRoleDelete"
                          @change="togglePermission('roles', 'delete')"
                        >
                      </td>
                    </tr>
                    
                    <!-- Resource Management -->
                    <tr>
                      <td>
                        <i class="bi bi-box"></i>
                        Resource Management
                      </td>
                      <td class="text-center">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :checked="hasResourceRead"
                          @change="togglePermission('resources', 'read')"
                        >
                      </td>
                      <td class="text-center">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :checked="hasResourceCreate"
                          @change="togglePermission('resources', 'create')"
                        >
                      </td>
                      <td class="text-center">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :checked="hasResourceUpdate"
                          @change="togglePermission('resources', 'update')"
                        >
                      </td>
                      <td class="text-center">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :checked="hasResourceDelete"
                          @change="togglePermission('resources', 'delete')"
                        >
                      </td>
                    </tr>
                    
                    <!-- Policy Management -->
                    <tr>
                      <td>
                        <i class="bi bi-file-earmark-text"></i>
                        Policy Management
                      </td>
                      <td class="text-center">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :checked="hasPolicyRead"
                          @change="togglePermission('policies', 'read')"
                        >
                      </td>
                      <td class="text-center">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :checked="hasPolicyCreate"
                          @change="togglePermission('policies', 'create')"
                        >
                      </td>
                      <td class="text-center">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :checked="hasPolicyUpdate"
                          @change="togglePermission('policies', 'update')"
                        >
                      </td>
                      <td class="text-center">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :checked="hasPolicyDelete"
                          @change="togglePermission('policies', 'delete')"
                        >
                      </td>
                    </tr>

                    <!-- Organization Management -->
                    <tr>
                      <td>
                        <i class="bi bi-building"></i>
                        Organization Management
                      </td>
                      <td class="text-center">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :checked="hasOrganizationRead"
                          @change="togglePermission('organizations', 'read')"
                        >
                      </td>
                      <td class="text-center">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :checked="hasOrganizationCreate"
                          @change="togglePermission('organizations', 'create')"
                        >
                      </td>
                      <td class="text-center">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :checked="hasOrganizationUpdate"
                          @change="togglePermission('organizations', 'update')"
                        >
                      </td>
                      <td class="text-center">
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :checked="hasOrganizationDelete"
                          @change="togglePermission('organizations', 'delete')"
                        >
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
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
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useScopeStore } from '../../stores/scopes'
import { useToast } from 'vue-toastification'

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

// Permission mapping
const permissionMapping = {
  users: {
    read: 'USER_READ',
    create: 'USER_CREATE',
    update: 'USER_UPDATE',
    delete: 'USER_DELETE'
  },
  roles: {
    read: 'ROLE_READ',
    create: 'ROLE_CREATE',
    update: 'ROLE_UPDATE',
    delete: 'ROLE_DELETE'
  },
  resources: {
    read: 'RESOURCE_READ',
    create: 'RESOURCE_CREATE',
    update: 'RESOURCE_UPDATE',
    delete: 'RESOURCE_DELETE'
  },
  policies: {
    read: 'POLICY_READ',
    create: 'POLICY_CREATE',
    update: 'POLICY_UPDATE',
    delete: 'POLICY_DELETE'
  },
  organizations: {
    read: 'ORGANIZATION_READ',
    create: 'ORGANIZATION_CREATE',
    update: 'ORGANIZATION_UPDATE',
    delete: 'ORGANIZATION_DELETE'
  }
}

// Computed properties for permission checks
const hasUserRead = computed(() => form.permissions.includes(permissionMapping.users.read))
const hasUserCreate = computed(() => form.permissions.includes(permissionMapping.users.create))
const hasUserUpdate = computed(() => form.permissions.includes(permissionMapping.users.update))
const hasUserDelete = computed(() => form.permissions.includes(permissionMapping.users.delete))

const hasRoleRead = computed(() => form.permissions.includes(permissionMapping.roles.read))
const hasRoleCreate = computed(() => form.permissions.includes(permissionMapping.roles.create))
const hasRoleUpdate = computed(() => form.permissions.includes(permissionMapping.roles.update))
const hasRoleDelete = computed(() => form.permissions.includes(permissionMapping.roles.delete))

const hasResourceRead = computed(() => form.permissions.includes(permissionMapping.resources.read))
const hasResourceCreate = computed(() => form.permissions.includes(permissionMapping.resources.create))
const hasResourceUpdate = computed(() => form.permissions.includes(permissionMapping.resources.update))
const hasResourceDelete = computed(() => form.permissions.includes(permissionMapping.resources.delete))

const hasPolicyRead = computed(() => form.permissions.includes(permissionMapping.policies.read))
const hasPolicyCreate = computed(() => form.permissions.includes(permissionMapping.policies.create))
const hasPolicyUpdate = computed(() => form.permissions.includes(permissionMapping.policies.update))
const hasPolicyDelete = computed(() => form.permissions.includes(permissionMapping.policies.delete))

const hasOrganizationRead = computed(() => form.permissions.includes(permissionMapping.organizations.read))
const hasOrganizationCreate = computed(() => form.permissions.includes(permissionMapping.organizations.create))
const hasOrganizationUpdate = computed(() => form.permissions.includes(permissionMapping.organizations.update))
const hasOrganizationDelete = computed(() => form.permissions.includes(permissionMapping.organizations.delete))

// Methods to handle permission changes
function togglePermission(entity, action) {
  const permission = permissionMapping[entity][action]
  const index = form.permissions.indexOf(permission)
  
  if (index === -1) {
    form.permissions.push(permission)
  } else {
    form.permissions.splice(index, 1)
  }
}

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

/* Table styles */
.table {
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  overflow: hidden;
}

.table thead th {
  background-color: #f8f9fa;
  border-bottom: 2px solid #dee2e6;
  padding: 1rem;
  font-weight: 600;
  text-transform: uppercase;
  font-size: 0.85rem;
  letter-spacing: 0.5px;
}

.table tbody td {
  padding: 1rem;
  vertical-align: middle;
}

.table tbody tr {
  transition: background-color 0.2s ease;
}

.table tbody tr:hover {
  background-color: #f8f9fa;
}

.table tbody td:first-child {
  font-weight: 500;
  color: #495057;
}

.table tbody td:first-child i {
  color: #6c757d;
  font-size: 1.1rem;
  margin-right: 0.5rem;
}

.form-check-input {
  width: 1.2rem;
  height: 1.2rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.form-check-input:checked {
  background-color: #0d6efd;
  border-color: #0d6efd;
}

.form-check-input:focus {
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}

/* Permission type colors */
.permission-type {
  font-size: 0.75rem;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-weight: 500;
}

.permission-read {
  background-color: #e3f2fd;
  color: #0d6efd;
}

.permission-write {
  background-color: #e8f5e9;
  color: #198754;
}

.permission-info {
  background-color: #fff3e0;
  color: #fd7e14;
}

.permission-delete {
  background-color: #fbe9e7;
  color: #dc3545;
}
</style> 