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
                  disabled
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
                    @input="validatePassword"
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
                <div v-if="form.newPassword" class="password-strength mt-2">
                  <div class="progress" style="height: 5px;">
                    <div 
                      class="progress-bar" 
                      :class="passwordStrengthClass"
                      :style="{ width: passwordStrength + '%' }"
                    ></div>
                  </div>
                  <small class="text-muted mt-1 d-block">
                    Password must contain:
                    <ul class="mb-0 ps-3">
                      <li :class="{ 'text-success': hasMinLength }">At least 8 characters</li>
                      <li :class="{ 'text-success': hasUpperCase }">One uppercase letter</li>
                      <li :class="{ 'text-success': hasLowerCase }">One lowercase letter</li>
                      <li :class="{ 'text-success': hasNumber }">One number</li>
                      <li :class="{ 'text-success': hasSpecialChar }">One special character</li>
                    </ul>
                  </small>
                </div>
              </div>
            </div>
            
            <div class="col-md-6">
              <div class="form-group">
                <label for="confirmPassword" class="form-label">Confirm New Password</label>
                <div class="input-group">
                  <input 
                    :type="showPassword ? 'text' : 'password'" 
                    class="form-control" 
                    id="confirmPassword" 
                    v-model="form.confirmPassword"
                    @input="validateConfirmPassword"
                  >
                </div>
                <div v-if="errors.confirmPassword" class="text-danger small mt-1">
                  {{ errors.confirmPassword }}
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
            
            <div class="col-md-6" v-if="authStore.isSuperAdmin">
              <div class="form-group">
                <label for="organization_id" class="form-label">Organization*</label>
                <select 
                  class="form-select" 
                  id="organization_id" 
                  v-model="form.organization_id" 
                  required
                >
                  <option value="" disabled>Select an organization</option>
                  <option v-for="organization in organizations" :key="organization.id" :value="organization.id">
                    {{ organization.name }}
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
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
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
const route = useRoute()
const router = useRouter()
const toast = useToast()

// Get user ID from route params
const userId = route.params.id

// State
const form = reactive({
  id: '',
  fullName: '',
  username: '',
  email: '',
  newPassword: '',
  confirmPassword: '',
  role: '',
  organization_id: null
})

const errors = reactive({
  fullName: '',
  username: '',
  email: '',
  newPassword: '',
  confirmPassword: '',
  role: '',
  organization_id: ''
})

const roles = ref([])
const organizations = ref([])
const showPassword = ref(false)
const loading = ref(false)
const isEdit = computed(() => !!route.params.id)

// Password validation state
const passwordStrength = ref(0)
const hasMinLength = ref(false)
const hasUpperCase = ref(false)
const hasLowerCase = ref(false)
const hasNumber = ref(false)
const hasSpecialChar = ref(false)

// Computed
const passwordStrengthClass = computed(() => {
  if (passwordStrength.value < 25) return 'bg-danger'
  if (passwordStrength.value < 50) return 'bg-warning'
  if (passwordStrength.value < 75) return 'bg-info'
  return 'bg-success'
})

// Validation rules
const rules = {
  username: [
    { required: true, message: 'Username is required' },
    { min: 3, message: 'Username must be at least 3 characters' }
  ],
  email: [
    { required: true, message: 'Email is required' },
    { type: 'email', message: 'Invalid email format' }
  ],
  password: [
    { required: !isEdit.value, message: 'Password is required' },
    { min: 6, message: 'Password must be at least 6 characters' }
  ],
  fullName: [
    { required: true, message: 'Full name is required' }
  ],
  role: [
    { required: true, message: 'Role is required' }
  ],
  organization_id: [
    { required: true, message: 'Organization is required' }
  ]
}

// Lifecycle hooks
onMounted(async () => {
  try {
    // Load user data
    await loadUserData()
    
    // Load roles and organizations
    const rolesData = await roleStore.fetchRoles()
    roles.value = rolesData
    
    const organizationsData = await organizationStore.fetchOrganizations()
    organizations.value = organizationsData
  } catch (error) {
    console.error('Failed to load form data:', error)
  }
})

// Methods
function togglePasswordVisibility() {
  showPassword.value = !showPassword.value
}

function validatePassword() {
  const password = form.newPassword
  
  // Reset validation states
  hasMinLength.value = password.length >= 8
  hasUpperCase.value = /[A-Z]/.test(password)
  hasLowerCase.value = /[a-z]/.test(password)
  hasNumber.value = /[0-9]/.test(password)
  hasSpecialChar.value = /[!@#$%^&*(),.?":{}|<>]/.test(password)
  
  // Calculate password strength
  let strength = 0
  if (hasMinLength.value) strength += 20
  if (hasUpperCase.value) strength += 20
  if (hasLowerCase.value) strength += 20
  if (hasNumber.value) strength += 20
  if (hasSpecialChar.value) strength += 20
  
  passwordStrength.value = strength
  
  // Validate password
  if (password && password.length < 8) {
    errors.newPassword = 'Password must be at least 8 characters long'
  } else if (password && !hasUpperCase.value) {
    errors.newPassword = 'Password must contain at least one uppercase letter'
  } else if (password && !hasLowerCase.value) {
    errors.newPassword = 'Password must contain at least one lowercase letter'
  } else if (password && !hasNumber.value) {
    errors.newPassword = 'Password must contain at least one number'
  } else if (password && !hasSpecialChar.value) {
    errors.newPassword = 'Password must contain at least one special character'
  } else {
    errors.newPassword = ''
  }
  
  // If confirm password is set, validate it
  if (form.confirmPassword) {
    validateConfirmPassword()
  }
}

function validateConfirmPassword() {
  if (form.newPassword && form.confirmPassword !== form.newPassword) {
    errors.confirmPassword = 'Passwords do not match'
  } else {
    errors.confirmPassword = ''
  }
}

function validateForm() {
  let valid = true
  
  // Reset errors
  Object.keys(errors).forEach(key => {
    errors[key] = ''
  })
  
  // Validate full name
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
  
  // Validate password if it's being changed
  if (form.newPassword) {
    if (form.newPassword.length < 6) {
      errors.newPassword = 'Password must be at least 6 characters'
      valid = false
    }
    
    if (form.newPassword !== form.confirmPassword) {
      errors.confirmPassword = 'Passwords do not match'
      valid = false
    }
  }
  
  // Validate role
  if (!form.role) {
    errors.role = 'Role is required'
    valid = false
  }
  
  // Validate organization
  if (!form.organization_id) {
    errors.organization_id = 'Organization is required'
    valid = false
  }
  
  return valid
}

async function loadUserData() {
  if (isEdit.value) {
    try {
      loading.value = true
      const userData = await userStore.fetchUserById(userId)
      if (userData) {
        // Map user data to form
        form.id = userData.id
        form.fullName = userData.fullName
        form.username = userData.username
        form.email = userData.email
        form.role = userData.roles[0] // Assuming first role is primary
        
        // Set organization_id from the organization object if it exists
        if (userData.organization) {
          form.organization_id = userData.organization.id
        } else if (userData.organization_id) {
          form.organization_id = userData.organization_id
        }
      }
    } catch (error) {
      toast.error('Failed to load user data')
      router.push('/users')
    } finally {
      loading.value = false
    }
  }
}

async function handleSubmit() {
  if (!validateForm()) return
  
  try {
    loading.value = true
    const updateData = {
      fullName: form.fullName,
      email: form.email,
      roles: [form.role],
      organization_id: form.organization_id
    }
    
    // Only include password if it was changed
    if (form.newPassword) {
      updateData.password = form.newPassword
    }
    
    await userStore.updateUser(userId, updateData)
    toast.success('User updated successfully')
    router.push('/users')
  } catch (error) {
    console.error('Failed to update user:', error)
    toast.error(error.response?.data?.message || 'Failed to update user')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.card {
  border: none;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.08);
  border-radius: 12px;
}

.card-body {
  padding: 2rem;
}

.form-label {
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.form-control, .form-select {
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  padding: 0.75rem 1rem;
  transition: all 0.3s ease;
}

.form-control:focus, .form-select:focus {
  border-color: #4a90e2;
  box-shadow: 0 0 0 0.2rem rgba(74, 144, 226, 0.25);
}

.input-group .btn {
  border-radius: 0 8px 8px 0;
}

.btn {
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-primary {
  background-color: #4a90e2;
  border-color: #4a90e2;
}

.btn-primary:hover {
  background-color: #357abd;
  border-color: #357abd;
}

.btn-outline-secondary {
  color: #6c757d;
  border-color: #e0e0e0;
}

.btn-outline-secondary:hover {
  background-color: #f8f9fa;
  color: #495057;
  border-color: #e0e0e0;
}

h5 {
  color: #2c3e50;
  font-weight: 600;
  margin-bottom: 1.5rem;
}

.border-bottom {
  border-color: #e0e0e0 !important;
}

.text-danger {
  color: #dc3545 !important;
  font-size: 0.875rem;
  margin-top: 0.5rem;
}

.spinner-border {
  width: 1.5rem;
  height: 1.5rem;
  border-width: 0.2em;
}

.alert {
  border-radius: 8px;
  padding: 1rem;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .card-body {
    padding: 1.5rem;
  }
  
  .btn {
    padding: 0.5rem 1rem;
  }
}

/* Animation for loading state */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card {
  animation: fadeIn 0.3s ease-out;
}

.password-strength {
  margin-top: 0.5rem;
}

.password-strength .progress {
  height: 5px;
  border-radius: 2px;
  background-color: #e9ecef;
}

.password-strength ul {
  list-style-type: none;
  padding-left: 0;
  margin-top: 0.5rem;
}

.password-strength li {
  font-size: 0.75rem;
  color: #6c757d;
  margin-bottom: 0.25rem;
}

.password-strength li.text-success {
  color: #28a745;
}
</style>