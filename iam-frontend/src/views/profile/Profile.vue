<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">My Profile</h1>
    </div>

    <div class="row">
      <!-- Profile Info -->
      <div class="col-lg-4 mb-4">
        <div class="card">
          <div class="card-body text-center">
            <div 
              class="avatar-initials avatar-lg mx-auto mb-3"
            >
              {{ userInitials }}
            </div>
            <h5 class="card-title mb-1">{{ userName }}</h5>
            <p class="text-muted mb-3">{{ userRole }}</p>
            <div class="d-grid">
              <button class="btn btn-outline-primary" @click="editProfile">
                <i class="bi bi-pencil me-2"></i> Edit Profile
              </button>
            </div>
          </div>
          <ul class="list-group list-group-flush">
            <li class="list-group-item">
              <small class="text-muted d-block">Email</small>
              {{ userEmail }}
            </li>
            <li class="list-group-item">
              <small class="text-muted d-block">Department</small>
              {{ userDepartment }}
            </li>
            <li class="list-group-item">
              <small class="text-muted d-block">Member Since</small>
              {{ formatDate(userCreatedAt) }}
            </li>
          </ul>
        </div>
      </div>

      <!-- Activity & Settings -->
      <div class="col-lg-8">
        <!-- Recent Activity -->
        <div class="card mb-4">
          <div class="card-header">
            <h5 class="card-title mb-0">Recent Activity</h5>
          </div>
          <div class="card-body p-0">
            <div class="list-group list-group-flush">
              <div v-for="(activity, index) in recentActivities" :key="index" class="list-group-item py-3">
                <div class="d-flex">
                  <div class="flex-shrink-0">
                    <div :class="`bg-${activity.color} bg-opacity-10 p-2 rounded`">
                      <i :class="`bi bi-${activity.icon} text-${activity.color}`"></i>
                    </div>
                  </div>
                  <div class="flex-grow-1 ms-3">
                    <div class="d-flex justify-content-between">
                      <p class="mb-0">{{ activity.message }}</p>
                      <small class="text-muted">{{ activity.time }}</small>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Security Settings -->
        <div class="card">
          <div class="card-header">
            <h5 class="card-title mb-0">Security Settings</h5>
          </div>
          <div class="card-body">
            <div class="mb-4">
              <label class="form-label">Change Password</label>
              <div class="input-group mb-3">
                <input 
                  :type="showPassword ? 'text' : 'password'" 
                  class="form-control" 
                  v-model="password.current" 
                  placeholder="Current Password"
                >
                <button 
                  class="btn btn-outline-secondary" 
                  type="button"
                  @click="togglePasswordVisibility"
                >
                  <i class="bi" :class="showPassword ? 'bi-eye-slash' : 'bi-eye'"></i>
                </button>
              </div>
              <div class="input-group mb-3">
                <input 
                  :type="showPassword ? 'text' : 'password'" 
                  class="form-control" 
                  v-model="password.new" 
                  placeholder="New Password"
                >
              </div>
              <div class="input-group mb-3">
                <input 
                  :type="showPassword ? 'text' : 'password'" 
                  class="form-control" 
                  v-model="password.confirm" 
                  placeholder="Confirm New Password"
                >
              </div>
              <button 
                class="btn btn-primary" 
                @click="changePassword"
                :disabled="!canChangePassword"
              >
                Update Password
              </button>
            </div>

            <hr>

            <!-- Two-Factor Authentication -->
            <div class="mb-4">
              <div class="d-flex justify-content-between align-items-center mb-3">
                <div>
                  <h6 class="mb-1">Two-Factor Authentication</h6>
                  <p class="text-muted mb-0 small">Add an extra layer of security to your account</p>
                </div>
                <div class="form-check form-switch">
                  <input class="form-check-input" type="checkbox" v-model="twoFactorEnabled">
                </div>
              </div>
            </div>

            <!-- Session Management -->
            <div>
              <h6 class="mb-3">Active Sessions</h6>
              <div class="list-group">
                <div v-for="(session, index) in activeSessions" :key="index" class="list-group-item">
                  <div class="d-flex justify-content-between align-items-center">
                    <div>
                      <h6 class="mb-1">{{ session.device }}</h6>
                      <small class="text-muted">
                        {{ session.location }} · Last active {{ session.lastActive }}
                      </small>
                    </div>
                    <button 
                      class="btn btn-sm btn-outline-danger"
                      @click="terminateSession(session.id)"
                    >
                      Terminate
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { useToast } from 'vue-toastification'

const authStore = useAuthStore()
const toast = useToast()

// State
const showPassword = ref(false)
const twoFactorEnabled = ref(false)
const password = ref({
  current: '',
  new: '',
  confirm: ''
})

// Mock data
const recentActivities = ref([
  {
    message: 'Password changed successfully',
    time: '2 hours ago',
    icon: 'key',
    color: 'success'
  },
  {
    message: 'New login from Chrome on Windows',
    time: '1 day ago',
    icon: 'box-arrow-in-right',
    color: 'primary'
  },
  {
    message: 'Profile information updated',
    time: '3 days ago',
    icon: 'person',
    color: 'info'
  }
])

const activeSessions = ref([
  {
    id: 1,
    device: 'Chrome on Windows',
    location: 'San Francisco, US',
    lastActive: 'Just now'
  },
  {
    id: 2,
    device: 'Mobile App on iPhone',
    location: 'San Francisco, US',
    lastActive: '2 hours ago'
  }
])

// Computed
const userName = computed(() => {
  const user = authStore.user
  return user ? `${user.firstName} ${user.lastName}` : ''
})

const userInitials = computed(() => {
  const user = authStore.user
  if (!user) return ''
  return `${user.firstName.charAt(0)}${user.lastName.charAt(0)}`
})

const userEmail = computed(() => authStore.user?.email || '')
const userRole = computed(() => authStore.user?.role || '')
const userDepartment = computed(() => authStore.user?.department || '')
const userCreatedAt = computed(() => authStore.user?.createdAt || '')

const canChangePassword = computed(() => {
  return password.value.current && 
         password.value.new && 
         password.value.confirm && 
         password.value.new === password.value.confirm
})

// Methods
function togglePasswordVisibility() {
  showPassword.value = !showPassword.value
}

function formatDate(date) {
  if (!date) return ''
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

async function changePassword() {
  if (!canChangePassword.value) return

  try {
    // Call API to change password
    await authStore.changePassword(password.value)
    toast.success('Password updated successfully')
    
    // Reset form
    password.value = {
      current: '',
      new: '',
      confirm: ''
    }
  } catch (error) {
    toast.error(error.message || 'Failed to update password')
  }
}

function editProfile() {
  // Implement edit profile logic
}

function terminateSession(sessionId) {
  // Implement session termination logic
  activeSessions.value = activeSessions.value.filter(session => session.id !== sessionId)
  toast.success('Session terminated successfully')
}
</script>

<style scoped>
.avatar {
  width: 36px;
  height: 36px;
  font-size: 14px;
  font-weight: 600;
}
</style>