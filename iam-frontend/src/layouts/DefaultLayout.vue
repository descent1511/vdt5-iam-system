<template>
  <div class="layout">
    <!-- Sidebar -->
    <aside 
      class="sidebar"
      :class="{ 'collapsed': !isExpanded }"
    >
      <!-- Logo Section -->
      <div class="sidebar-header">
        <div class="logo">
          <i class="bi bi-shield-lock-fill"></i>
        </div>
        <h1 class="site-title" v-show="isExpanded">Access Manager</h1>
      </div>

      <!-- Navigation Menu -->
      <nav class="sidebar-nav">
        <div class="nav-section" v-if="authStore.can('USER_READ')">
          <h6 class="nav-section-title" v-show="isExpanded">User Management</h6>
          <router-link 
            to="/users" 
            class="nav-link"
            :class="{ 'collapsed': !isExpanded }"
          >
            <i class="bi bi-people"></i>
            <span v-show="isExpanded">Users</span>
          </router-link>
        </div>

        <div class="nav-section" v-if="authStore.can('ROLE_READ')">
          <h6 class="nav-section-title" v-show="isExpanded">Role Management</h6>
          <router-link 
            to="/roles" 
            class="nav-link"
            :class="{ 'collapsed': !isExpanded }"
          >
            <i class="bi bi-shield-lock"></i>
            <span v-show="isExpanded">Roles</span>
          </router-link>
        </div>

        <div class="nav-section" v-if="authStore.can('RESOURCE_READ')">
          <h6 class="nav-section-title" v-show="isExpanded">Resource Management</h6>
          <router-link 
            to="/resources" 
            class="nav-link"
            :class="{ 'collapsed': !isExpanded }"
          >
            <i class="bi bi-box"></i>
            <span v-show="isExpanded">Resources</span>
          </router-link>
        </div>

        <div class="nav-section" v-if="authStore.can('SCOPE_READ')">
          <h6 class="nav-section-title" v-show="isExpanded">Scope Management</h6>
          <router-link 
            to="/scopes" 
            class="nav-link"
            :class="{ 'collapsed': !isExpanded }"
          >
            <i class="bi bi-diagram-3"></i>
            <span v-show="isExpanded">Scopes</span>
          </router-link>
        </div>

        <div class="nav-section" v-if="authStore.can('POLICY_READ')">
          <h6 class="nav-section-title" v-show="isExpanded">Policy Management</h6>
          <router-link 
            to="/policies" 
            class="nav-link"
            :class="{ 'collapsed': !isExpanded }"
          >
            <i class="bi bi-file-earmark-text"></i>
            <span v-show="isExpanded">Policies</span>
          </router-link>
        </div>

        <div class="nav-section" v-if="authStore.can('ORGANIZATION_READ')">
          <h6 class="nav-section-title" v-show="isExpanded">Organization</h6>
          <router-link 
            to="/organizations" 
            class="nav-link"
            :class="{ 'collapsed': !isExpanded }"
          >
            <i class="bi bi-building"></i>
            <span v-show="isExpanded">Organizations</span>
          </router-link>
        </div>
      </nav>

      <!-- Footer -->
      <div class="sidebar-footer">
        <p class="copyright" v-show="isExpanded">
          © {{ new Date().getFullYear() }} Access Manager
        </p>
        <div class="user-info" v-show="isExpanded">
          <div class="user-avatar">
            {{ getUserInitials() }}
          </div>
          <div class="user-details">
            <span class="user-name">{{ authStore.user?.fullName }}</span>
            <span class="user-role">{{ getUserRole() }}</span>
          </div>
        </div>
      </div>

      <!-- Toggle Button -->
      <button 
        class="sidebar-toggle" 
        @click="toggleSidebar"
        :class="{ 'collapsed': !isExpanded }"
      >
        <i class="bi" :class="isExpanded ? 'bi-chevron-left' : 'bi-chevron-right'"></i>
      </button>
    </aside>

    <!-- Main Content -->
    <main class="main-content" :class="{ 'expanded': !isExpanded }">
      <div class="container-fluid mb-4">
        <Breadcrumb />
      </div>
      
      <div class="container-fluid">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import AppHeader from '../components/layout/AppHeader.vue'
import AppFooter from '../components/layout/AppFooter.vue'
import Breadcrumb from '../components/layout/Breadcrumb.vue'

const authStore = useAuthStore()
const isExpanded = ref(true)

// Toggle sidebar
function toggleSidebar() {
  isExpanded.value = !isExpanded.value
}

// Get user initials for avatar
function getUserInitials() {
  const user = authStore.user
  if (!user?.fullName) return ''
  
  return user.fullName
    .split(' ')
    .map(name => name.charAt(0))
    .join('')
    .toUpperCase()
}

// Get user's primary role
function getUserRole() {
  const user = authStore.user
  if (!user?.roles?.length) return ''
  
  return user.roles[0].replace('ROLE_', '')
}

// Handle window resize
function handleResize() {
  if (window.innerWidth < 768) {
    isExpanded.value = false
  }
}

onMounted(() => {
  handleResize()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  width: 16rem; /* w-64 */
  background-color: var(--bs-dark);
  color: var(--bs-light);
  transition: all 0.3s ease;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
}

.sidebar.collapsed {
  width: 4rem; /* w-16 */
}

.sidebar-toggle {
  position: absolute;
  top: 20px;
  right: -15px;
  width: 30px;
  height: 30px;
  background-color: var(--bs-dark);
  border-radius: 50%;
  color: var(--bs-light);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1001;
  padding: 0;
  border: 2px solid var(--bs-light);
  transition: all 0.3s ease;
  cursor: pointer;
}

.sidebar-toggle:hover {
  background-color: var(--bs-primary);
  transform: scale(1.1);
}

.sidebar-toggle.collapsed {
  right: -15px;
}

.sidebar-header {
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-collapsed {
  width: 32px;
  height: 32px;
  font-size: 1.25rem;
}

.site-title {
  font-size: 1.25rem;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  font-weight: 600;
}

.sidebar-nav {
  flex: 1;
  overflow-y: auto;
  padding: 1rem 0;
}

.nav-section {
  margin-bottom: 1.5rem;
}

.nav-section-title {
  padding: 0 1.5rem;
  margin-bottom: 0.5rem;
  font-size: 0.75rem;
  text-transform: uppercase;
  color: var(--bs-gray-400);
  letter-spacing: 0.5px;
}

.nav-link {
  display: flex;
  align-items: center;
  padding: 0.75rem 1.5rem;
  color: var(--bs-light);
  text-decoration: none;
  transition: all 0.2s ease;
  gap: 1rem;
  border-left: 3px solid transparent;
}

.nav-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: var(--bs-light);
  border-left-color: var(--bs-primary);
}

.nav-link.router-link-active {
  background-color: rgba(255, 255, 255, 0.1);
  color: var(--bs-light);
  border-left-color: var(--bs-primary);
  font-weight: 500;
}

.nav-link i {
  font-size: 1.25rem;
  min-width: 24px;
  text-align: center;
}

.nav-link.collapsed {
  justify-content: center;
  padding: 0.75rem;
}

.sidebar-footer {
  padding: 1.5rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  background-color: rgba(0, 0, 0, 0.2);
}

.copyright {
  font-size: 0.75rem;
  color: var(--bs-gray-400);
  margin-bottom: 1rem;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.user-avatar {
  width: 32px;
  height: 32px;
  background-color: var(--bs-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.875rem;
  font-weight: 600;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 0.875rem;
  font-weight: 500;
}

.user-role {
  font-size: 0.75rem;
  color: var(--bs-gray-400);
}

/* Main Content */
.main-content {
  flex: 1;
  margin-left: 16rem; /* w-64 */
  transition: margin-left 0.3s ease;
  min-height: 100vh;
  background-color: var(--bs-light);
}

.main-content.expanded {
  margin-left: 4rem; /* w-16 */
}

/* Scrollbar Styles */
.sidebar-nav::-webkit-scrollbar {
  width: 4px;
}

.sidebar-nav::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
}

.sidebar-nav::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
}

.sidebar-nav::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* Responsive Styles */
@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
  }
  
  .sidebar.collapsed {
    transform: translateX(0);
  }
  
  .main-content {
    margin-left: 0;
  }
  
  .main-content.expanded {
    margin-left: 0;
  }
  
  .sidebar-toggle {
    display: none;
  }
}
</style>