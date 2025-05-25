<template>
  <header class="navbar navbar-expand-lg navbar-light bg-white shadow-sm sticky-top py-2">
    <div class="container-fluid">
      <!-- Brand/Logo -->
      <router-link class="navbar-brand d-flex align-items-center" to="/">
        <i class="bi bi-shield-lock fs-4 me-2 text-primary"></i>
        <span class="fw-bold text-primary">Access Manager</span>
      </router-link>
      
      <!-- Mobile toggle button -->
      <button 
        class="navbar-toggler" 
        type="button" 
        data-bs-toggle="collapse" 
        data-bs-target="#navbarNav"
      >
        <span class="navbar-toggler-icon"></span>
      </button>
      
      <!-- Navbar content -->
      <div class="collapse navbar-collapse" id="navbarNav">
        <!-- Search -->
        <div class="ms-auto me-2 d-none d-lg-block">
          <div class="input-group">
            <input 
              type="text" 
              class="form-control" 
              placeholder="Search..." 
              aria-label="Search"
            >
            <button class="btn btn-outline-secondary" type="button">
              <i class="bi bi-search"></i>
            </button>
          </div>
        </div>
        
        <!-- User menu -->
        <ul class="navbar-nav ms-auto">
          <li class="nav-item dropdown">
            <a 
              class="nav-link dropdown-toggle d-flex align-items-center" 
              href="#" 
              role="button" 
              data-bs-toggle="dropdown" 
              aria-expanded="false"
            >
              <div 
                class="avatar me-2 d-flex align-items-center justify-content-center bg-primary text-white rounded-circle"
              >
                {{ userInitials }}
              </div>
              <span class="d-none d-sm-inline">{{ userName }}</span>
            </a>
            <ul class="dropdown-menu dropdown-menu-end shadow">
              <li>
                <div class="dropdown-item-text">
                  <div class="fw-bold">{{ userName }}</div>
                  <small class="text-muted">{{ userEmail }}</small>
                </div>
              </li>
              <li><hr class="dropdown-divider"></li>
              <li>
                <router-link class="dropdown-item" to="/profile">
                  <i class="bi bi-person me-2"></i> My Profile
                </router-link>
              </li>
              <li><hr class="dropdown-divider"></li>
              <li>
                <button class="dropdown-item text-danger" @click="handleLogout">
                  <i class="bi bi-box-arrow-right me-2"></i> Logout
                </button>
              </li>
            </ul>
          </li>
        </ul>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

// Computed properties
const userName = computed(() => {
  const user = authStore.user
  return user?.fullName || 'Guest'
})

const userEmail = computed(() => {
  return authStore.user?.email || ''
})

const userInitials = computed(() => {
  const user = authStore.user
  if (!user?.fullName) return 'G'
  
  const names = user.fullName.split(' ')
  if (names.length >= 2) {
    return `${names[0].charAt(0)}${names[names.length - 1].charAt(0)}`
  }
  return names[0].charAt(0)
})

// Methods
async function handleLogout() {
  try {
    await authStore.logout()
    router.push('/login')
  } catch (error) {
    console.error('Logout failed:', error)
  }
}
</script>

<style scoped>
.navbar {
  z-index: 1020;
  height: 56px;
}

.avatar {
  width: 36px;
  height: 36px;
  font-size: 14px;
  font-weight: 600;
}

.dropdown-item-text {
  padding: 0.5rem 1rem;
}

.navbar-brand {
  font-size: 1.3rem;
  margin-right: 2rem;
}

@media (min-width: 992px) {
  .navbar {
    padding-left: 270px;
    transition: padding-left 0.3s ease;
  }
  
  .navbar-brand {
    position: fixed;
    left: 0;
    width: 250px;
    padding-left: 1rem;
    margin-right: 0;
    background: white;
    height: 56px;
    display: flex;
    align-items: center;
    z-index: 1032;
    border-right: 1px solid rgba(0, 0, 0, 0.1);
  }
  
  body[data-sidebar="collapsed"] .navbar {
    padding-left: 90px;
  }
  
  body[data-sidebar="collapsed"] .navbar-brand {
    width: 70px;
    padding-left: 0.5rem;
  }
  
  body[data-sidebar="collapsed"] .navbar-brand span {
    display: none;
  }
}
</style>