<template>
  <div>
    <h2 class="text-center mb-4">Sign Up</h2>
    
    <form @submit.prevent="handleSignup">
      <div class="mb-3">
        <label for="organization" class="form-label">Organization</label>
        <div class="input-group">
          <span class="input-group-text">
            <i class="bi bi-building"></i>
          </span>
          <select 
            class="form-select" 
            id="organization" 
            v-model="form.organizationId"
            required
            :disabled="loading || loadingOrganizations"
          >
            <option value="">Select Organization</option>
            <option v-for="org in organizations" :key="org.id" :value="org.id">
              {{ org.name }}
            </option>
          </select>
        </div>
        <div v-if="errors.organizationId" class="text-danger small mt-1">
          {{ errors.organizationId }}
        </div>
      </div>

      <div class="mb-3">
        <label for="username" class="form-label">Username</label>
        <input 
          type="text" 
          class="form-control" 
          id="username" 
          v-model="form.username"
          required
          :disabled="loading"
        >
        <div v-if="errors.username" class="text-danger small mt-1">
          {{ errors.username }}
        </div>
      </div>

      <div class="mb-3">
        <label for="email" class="form-label">Email</label>
        <input 
          type="email" 
          class="form-control" 
          id="email" 
          v-model="form.email"
          required
          :disabled="loading"
        >
        <div v-if="errors.email" class="text-danger small mt-1">
          {{ errors.email }}
        </div>
      </div>

      <div class="mb-3">
        <label for="fullName" class="form-label">Full Name</label>
        <input 
          type="text" 
          class="form-control" 
          id="fullName" 
          v-model="form.fullName"
          required
          :disabled="loading"
        >
        <div v-if="errors.fullName" class="text-danger small mt-1">
          {{ errors.fullName }}
        </div>
      </div>

      <div class="mb-3">
        <label for="password" class="form-label">Password</label>
        <input 
          type="password" 
          class="form-control" 
          id="password" 
          v-model="form.password"
          required
          :disabled="loading"
        >
        <div v-if="errors.password" class="text-danger small mt-1">
          {{ errors.password }}
        </div>
      </div>

      <div class="mb-3">
        <label for="confirmPassword" class="form-label">Confirm Password</label>
        <input 
          type="password" 
          class="form-control" 
          id="confirmPassword" 
          v-model="form.confirmPassword"
          required
          :disabled="loading"
        >
        <div v-if="errors.confirmPassword" class="text-danger small mt-1">
          {{ errors.confirmPassword }}
        </div>
      </div>

      <div class="d-grid gap-2">
        <button 
          type="submit" 
          class="btn btn-primary"
          :disabled="loading || loadingOrganizations"
        >
          <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
          Sign Up
        </button>
        
        <router-link to="/login" class="btn btn-link">
          Already have an account? Login
        </router-link>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { useOrganizationStore } from '../../stores/organizations'

const authStore = useAuthStore()
const organizationStore = useOrganizationStore()
const loading = ref(false)
const loadingOrganizations = ref(false)

const form = reactive({
  username: '',
  email: '',
  fullName: '',
  password: '',
  confirmPassword: '',
  organizationId: ''
})

const errors = reactive({
  username: '',
  email: '',
  fullName: '',
  password: '',
  confirmPassword: '',
  organizationId: ''
})

const organizations = ref([])

async function loadOrganizations() {
  loadingOrganizations.value = true
  try {
    organizations.value = await organizationStore.fetchOrganizations()
  } catch (error) {
    console.error('Failed to load organizations:', error)
  } finally {
    loadingOrganizations.value = false
  }
}

function validateForm() {
  let isValid = true
  
  // Reset errors
  Object.keys(errors).forEach(key => errors[key] = '')
  
  if (!form.organizationId) {
    errors.organizationId = 'Please select an organization'
    isValid = false
  }
  
  if (!form.username) {
    errors.username = 'Username is required'
    isValid = false
  }
  
  if (!form.email) {
    errors.email = 'Email is required'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.email = 'Invalid email format'
    isValid = false
  }
  
  if (!form.fullName) {
    errors.fullName = 'Full name is required'
    isValid = false
  }
  
  if (!form.password) {
    errors.password = 'Password is required'
    isValid = false
  } else if (form.password.length < 6) {
    errors.password = 'Password must be at least 6 characters'
    isValid = false
  }
  
  if (!form.confirmPassword) {
    errors.confirmPassword = 'Please confirm your password'
    isValid = false
  } else if (form.password !== form.confirmPassword) {
    errors.confirmPassword = 'Passwords do not match'
    isValid = false
  }
  
  return isValid
}

async function handleSignup() {
  if (!validateForm()) return
  
  try {
    loading.value = true
    
    const signupData = {
      username: form.username,
      email: form.email,
      fullName: form.fullName,
      password: form.password,
      organizationId: form.organizationId
    }
    
    await authStore.signup(signupData)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadOrganizations()
})
</script>