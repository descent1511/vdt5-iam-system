<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Edit User</h1>
      
      <router-link to="/users" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left me-2"></i> Back to Users
      </router-link>
    </div>
    
    <div v-if="userStore.loading && !form.id" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-2">Loading user data...</p>
    </div>
    
    <div v-else-if="userStore.error" class="alert alert-danger">
      {{ userStore.error }}
    </div>
    
    <div v-else class="card">
      <div class="card-body">
        <form @submit.prevent="handleSubmit">
          <div class="row g-3">
            <!-- Personal Information -->
            <div class="col-12 mb-3">
              <h5 class="border-bottom pb-2">Personal Information</h5>
            </div>
            
            <div class="col-md-6">
              <div class="form-group">
                <label for="firstName" class="form-label">First Name*</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="firstName" 
                  v-model="form.firstName" 
                  required
                >
                <div v-if="errors.firstName" class="text-danger small mt-1">
                  {{ errors.firstName }}
                </div>
              </div>
            </div>
            
            <div class="col-md-6">
              <div class="form-group">
                <label for="lastName" class="form-label">Last Name*</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="lastName" 
                  v-model="form.lastName" 
                  required
                >
                <div v-if="errors.lastName" class="text-danger small mt-1">
                  {{ errors.lastName }}
                </div>
              </div>
            </div>
            
            <!-- Account Information -->
            <div class="col-12 mb-3 mt-4">
              <h5 class="border-bottom pb-2">Account Information</h5>
            </div>
            
            <div class="col-md-6">
              <div class="form-group">
                <label for="username" class="form-label">Username*</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="username" 
                  v-model="form.username" 
                  required
                >
                <div v-if="errors.username" class="text-danger small mt-1">
                  {{ errors.username }}
                </div>
              </div>
            </div>
            
            <div class="col-md-6">
              <div class="form-group">
                <label for="email" class="form-label">Email*</label>
                <input 
                  type="email" 
                  class="form-control" 
                  id="email" 
                  v-model="form.email" 
                  required
                >
                <div v-if="errors.email" class="text-danger small mt-1">
                  {{ errors.email }}
                </div>
              </div>
            </div>
            
            <div class="col-md-6">
              <div class="form-group">
                <label for="newPassword" class="form-label">New Password <small class="text-muted">(leave blank to keep current)</small></label>
                <div class="input-group">
                  <input 
                    :type="showPassword ? 'text' : 'password'" 
                    class="form-control" 
                    id="newPassword" 
                    v-model="form.newPassword"
                  >
                  <button 
                    class="btn btn-outline-secondary" 
                    type="button"
                    @click="togglePasswordVisibility"
                  >
                    <i class="bi" :class="showPassword ? 'bi-eye-slash' : 'bi-eye'"></i>
                  </button>
                </div>
                <div v-if="errors.newPassword" class="text-danger small mt-1">
                  {{ errors.newPassword }}
                </div>
              </div>
            </div>
            
            <div class="col-md-6">
              <div class="form-group">
                <label for="role" class="form-label">Role*</label>
                <select 
                  class="form-select" 
                  id="role" 
                  v-model="form.role" 
                  required
                >
                  <option value="" disabled>Select a role</option>
                  <option v-for="role in roles" :key="role.id" :value="role.name">
                    {{ role.name }}
                  </option>
                </select>
                <div v-if="errors.role" class="text-danger small mt-1">
                  {{ errors.role }}
                </div>
              </div>
            </div>
            
            <!-- Organization Information -->
            <div class="col-12 mb-3 mt-4">
              <h5 class="border-bottom pb-2">Organization Information</h5>
            </div>
            
            <div class="col-md-6">
              <div class="form-group">
                <label for="department" class="form-label">Department</label>
                <select 
                  class="form-select" 
                  id="department" 
                  v-model="form.department"
                >
                  <option value="" disabled>Select a department</option>
                  <option v-for="dept in departments" :key="dept.id" :value="dept.name">
                    {{ dept.name }}
                  </option>
                </select>
              </div>
            </div>
            
            <div class="col-md-6">
              <div class="form-group">
                <label for="status" class="form-label">Status</label>
                <select 
                  class="form-select" 
                  id="status" 
                  v-model="form.status"
                >
                  <option value="active">Active</option>
                  <option value="inactive">Inactive</option>
                </select>
              </div>
            </div>
            
            <!-- Permissions -->
            <div class="col-12 mb-3 mt-4">
              <h5 class="border-bottom pb-2">Permissions</h5>
              <p class="text-muted small">These permissions will be added in addition to role-based permissions.</p>
            </div>
            
            <div class="col-12">
              <div class="row g-3">
                <div v-for="(permission, index) in availablePermissions" :key="index" class="col-md-4">
                  <div class="form-check">
                    <input 
                      class="form-check-input" 
                      type="checkbox" 
                      :id="`permission-${index}`" 
                      :value="permission"
                      v-model="form.permissions"
                    >
                    <label class="form-check-label" :for="`permission-${index}`">
                      {{ permission }}
                    </label>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- Submit buttons -->
            <div class="col-12 mt-4 d-flex justify-content-end gap-2">
              <router-link to="/users" class="btn btn-outline-secondary">
                Cancel
              </router-link>
              <button 
                type="submit" 
                class="btn btn-primary"
                :disabled="userStore.loading"
              >
                <span v-if="userStore.loading" class="spinner-border spinner-border-sm me-2" role="status"></span>
                Update User
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
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../../stores/users'
import { useRoleStore } from '../../stores/roles'
import { useOrganizationStore } from '../../stores/organization'

// Stores
const userStore = useUserStore()
const roleStore = useRoleStore()
const organizationStore = useOrganizationStore()
const route = useRoute()
const router = useRouter()

// Get user ID from route params
const userId = route.params.id

// State
const form = reactive({
  id: '',
  firstName: '',
  lastName: '',
  username: '',
  email: '',
  newPassword: '',
  role: '',
  department: '',
  status: 'active',
  permissions: []
})

const errors = reactive({
  firstName: '',
  lastName: '',
  username: '',
  email: '',
  newPassword: '',
  role: ''
})

const roles = ref([])
const departments = ref([])
const showPassword = ref(false)
const availablePermissions = ref([
  'read:users',
  'write:users',
  'delete:users',
  'read:roles',
  'write:roles',
  'delete:roles',
  'read:resources',
  'write:resources',
  'delete:resources',
  'read:policies',
  'write:policies',
  'delete:policies'
])

// Lifecycle hooks
onMounted(async () => {
  try {
    // Load user data
    const userData = await userStore.fetchUserById(userId)
    
    // Populate form
    Object.keys(form).forEach(key => {
      if (key !== 'newPassword' && userData[key] !== undefined) {
        form[key] = userData[key]
      }
    })
    
    // Load roles and departments
    const rolesData = await roleStore.fetchRoles()
    roles.value = rolesData
    
    const departmentsData = await organizationStore.fetchDepartments()
    departments.value = departmentsData
  } catch (error) {
    console.error('Failed to load user data:', error)
  }
})

// Methods
function togglePasswordVisibility() {
  showPassword.value = !showPassword.value
}

function validateForm() {
  let valid = true
  
  // Reset errors
  Object.keys(errors).forEach(key => {
    errors[key] = ''
  })
  
  // Validate first name
  if (!form.firstName.trim()) {
    errors.firstName = 'First name is required'
    valid = false
  }
  
  // Validate last name
  if (!form.lastName.trim()) {
    errors.lastName = 'Last name is required'
    valid = false
  }
  
  // Validate username
  if (!form.username.trim()) {
    errors.username = 'Username is required'
    valid = false
  } else if (form.username.length < 3) {
    errors.username = 'Username must be at least 3 characters'
    valid = false
  }
  
  // Validate email
  if (!form.email.trim()) {
    errors.email = 'Email is required'
    valid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.email = 'Please enter a valid email address'
    valid = false
  }
  
  // Validate new password (only if provided)
  if (form.newPassword && form.newPassword.length < 6) {
    errors.newPassword = 'Password must be at least 6 characters'
    valid = false
  }
  
  // Validate role
  if (!form.role) {
    errors.role = 'Role is required'
    valid = false
  }
  
  return valid
}

async function handleSubmit() {
  if (!validateForm()) return
  
  try {
    // Prepare update data
    const updateData = { ...form }
    
    // Remove id from update data
    delete updateData.id
    
    // Only include new password if it was changed
    if (!updateData.newPassword) {
      delete updateData.newPassword
    } else {
      updateData.password = updateData.newPassword
      delete updateData.newPassword
    }
    
    // Update user
    await userStore.updateUser(userId, updateData)
    router.push('/users')
  } catch (error) {
    console.error('Failed to update user:', error)
  }
}
</script>

<style scoped>
/* Component-specific styles */
</style>