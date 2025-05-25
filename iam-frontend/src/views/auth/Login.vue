<template>
  <div>
    <h2 class="text-center mb-4">Login</h2>
    
    <form @submit.prevent="handleLogin">
      <div class="mb-3">
        <label for="username" class="form-label">Username</label>
        <div class="input-group">
          <span class="input-group-text">
            <i class="bi bi-person"></i>
          </span>
          <input 
            type="text" 
            class="form-control" 
            id="username" 
            v-model="form.username" 
            placeholder="Enter your username"
            required
            :disabled="authStore.loading"
          >
        </div>
        <div v-if="errors.username" class="text-danger small mt-1">
          {{ errors.username }}
        </div>
      </div>
      
      <div class="mb-3">
        <label for="password" class="form-label">Password</label>
        <div class="input-group">
          <span class="input-group-text">
            <i class="bi bi-lock"></i>
          </span>
          <input 
            :type="showPassword ? 'text' : 'password'" 
            class="form-control" 
            id="password" 
            v-model="form.password" 
            placeholder="Enter your password"
            required
            :disabled="authStore.loading"
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
      
      <div class="mb-3 form-check">
        <input 
          type="checkbox" 
          class="form-check-input" 
          id="rememberMe" 
          v-model="form.rememberMe"
          :disabled="authStore.loading"
        >
        <label class="form-check-label" for="rememberMe">Remember me</label>
      </div>
      
      <div class="d-grid gap-2 mt-4">
        <button 
          type="submit" 
          class="btn btn-primary btn-lg"
          :disabled="authStore.loading"
        >
          <span v-if="authStore.loading" class="spinner-border spinner-border-sm me-2" role="status"></span>
          <span>Sign In</span>
        </button>

        <router-link to="/signup" class="btn btn-link">
          Don't have an account? Sign up
        </router-link>
      </div>
      
      <div v-if="authStore.error" class="alert alert-danger mt-3">
        {{ authStore.error }}
      </div>
    </form>
    
    <div class="text-center mt-4 text-muted">
      <p>
        <small>
          Demo credentials: admin / password
        </small>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()

// Form state
const form = reactive({
  username: '',
  password: '',
  rememberMe: false
})

const errors = reactive({
  username: '',
  password: ''
})

const showPassword = ref(false)

// Methods
function togglePasswordVisibility() {
  showPassword.value = !showPassword.value
}

async function handleLogin() {
  // Reset errors
  errors.username = ''
  errors.password = ''
  
  // Validate
  let valid = true
  
  if (!form.username.trim()) {
    errors.username = 'Username is required'
    valid = false
  }
  
  if (!form.password) {
    errors.password = 'Password is required'
    valid = false
  }
  
  if (!valid) return
  
  // Submit login
  await authStore.login({
    username: form.username,
    password: form.password,
    rememberMe: form.rememberMe
  })
}
</script>

<style scoped>
/* Add any component-specific styles here */
</style>