<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Create User</h1>
      
      <router-link to="/users" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left me-2"></i> Back to Users
      </router-link>
    </div>
    
    <div class="card">
      <div class="card-body">
        <form @submit.prevent="handleSubmit">
          <div class="row g-3">
            <!-- Personal Information -->
            <div class="col-12 mb-3">
              <h5 class="border-bottom pb-2">Personal Information</h5>
            </div>
            
            <div class="col-md-6">
              <div class="form-group">
                <label for="fullName" class="form-label">Full Name*</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="fullName" 
                  v-model="form.fullName" 
                  required
                >
                <div v-if="errors.fullName" class="text-danger small mt-1">
                  {{ errors.fullName }}
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
                <label for="password" class="form-label">Password*</label>
                <div class="input-group">
                  <input 
                    :type="showPassword ? 'text' : 'password'" 
                    class="form-control" 
                    id="password" 
                    v-model="form.password" 
                    required
                  >
                  <button 
                    class="btn btn-outline-secondary" 
                    type="button"
                    @click="togglePasswordVisibility"
                  >
                    <i class="bi" :class="showPassword ? 'bi-eye-slash' : 'bi-eye'"></i>
                  </button>
                </div>
                <div v-if="errors.password" class="text-danger small mt-1">
                  {{ errors.password }}
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

            <div class="col-md-6" v-if="authStore.isSuperAdmin ">
              <div class="form-group">
                <label for="organization" class="form-label">Organization*</label>
                <select 
                  class="form-select" 
                  id="organization" 
                  v-model="form.organization_id" 
                  required
                >
                  <option value="" disabled>Select an organization</option>
                  <option v-for="org in organizations" :key="org.id" :value="org.id">
                    {{ org.name }}
                  </option>
                </select>
                <div v-if="errors.organization_id" class="text-danger small mt-1">
                  {{ errors.organization_id }}
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
                Create User
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
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/users'
import { useRoleStore } from '../../stores/roles'
import { useOrganizationStore } from '../../stores/organizations'
import { useToast } from 'vue-toastification'
import { useAuthStore } from '../../stores/auth'


// Stores
const authStore = useAuthStore()
const userStore = useUserStore()
const roleStore = useRoleStore()
const organizationStore = useOrganizationStore()
const router = useRouter()
const toast = useToast()

// State
const form = reactive({
  username: '',
  email: '',
  password: '',
  fullName: '',
  role: '',
  organization_id: null
})

const errors = reactive({
  fullName: '',
  username: '',
  email: '',
  password: '',
  role: '',
  organization_id: ''
})

const roles = ref([])
const organizations = ref([])
const showPassword = ref(false)

// Lifecycle hooks
onMounted(async () => {
  try {
    const rolesData = await roleStore.fetchRoles()
    roles.value = rolesData
    
    const  organizationsData = await organizationStore.fetchOrganizations()
    organizations.value = organizationsData
  } catch (error) {
    console.error('Failed to load form data:', error)
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
  if (!form.fullName.trim()) {
    errors.fullName = 'Full name is required'
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
  
  // Validate password
  if (!form.password) {
    errors.password = 'Password is required'
    valid = false
  } else if (form.password.length < 6) {
    errors.password = 'Password must be at least 6 characters'
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
  if (form.organization_id === null) {
    form.organization_id = authStore.currentOrganizationId
  }
  try {
    const userData = {
      username: form.username,
      email: form.email,
      password: form.password,
      fullName: form.fullName,
      roles: [form.role],
      organization_id: form.organization_id
    }
    
    await userStore.createUser(userData)
    toast.success('User created successfully')
    router.push('/users')
  } catch (error) {
    console.error('Failed to create user:', error)
    toast.error(error.response?.data?.message || 'Failed to create user')
  }
}
</script>

<style scoped>
/* Component-specific styles */
</style>