<template>
  <div class="layout">
    <!-- Sidebar -->
    <aside 
      class="sidebar"
      :class="{ 'collapsed': !isExpanded }"
    >
      <!-- Logo Section -->
      <router-link to="/" class="sidebar-header-link">
      <div class="sidebar-header">
        <div class="logo" :class="{ 'logo-collapsed': !isExpanded }">
          <i class="bi bi-shield-lock-fill"></i>
        </div>
        <h1 class="site-title" v-show="isExpanded">Access Manager</h1>
          <button 
            class="theme-toggle"
            @click.prevent="themeStore.toggleTheme()"
            :title="themeStore.isDark ? 'Switch to light mode' : 'Switch to dark mode'"
            v-show="isExpanded"
          >
            <i class="bi" :class="themeStore.isDark ? 'bi-sun' : 'bi-moon'"></i>
          </button>
      </div>
      </router-link>

      <!-- Toggle Button -->
      <button 
        class="sidebar-toggle" 
        @click="toggleSidebar"
        :class="{ 'collapsed': !isExpanded }"
      >
        <i class="bi" :class="isExpanded ? 'bi-chevron-left' : 'bi-chevron-right'"></i>
      </button>

      <!-- Navigation Menu -->
      <nav class="sidebar-nav">
        <div class="nav-section">
          <router-link 
            to="/" 
            class="nav-link"
            :class="{ 'collapsed': !isExpanded }"
            exact-active-class="router-link-exact-active"
          >
            <i class="bi bi-grid-1x2-fill"></i>
            <span v-show="isExpanded">Dashboard</span>
          </router-link>
        </div>

        <div class="nav-section" v-if="authStore.isSuperAdmin || authStore.isAdmin">
          <router-link 
            to="/users" 
            class="nav-link"
            :class="{ 'collapsed': !isExpanded }"
          >
            <i class="bi bi-people"></i>
            <span v-show="isExpanded">Users</span>
          </router-link>
        </div>

        <div class="nav-section" v-if="authStore.isSuperAdmin || authStore.isAdmin">
          <router-link 
            to="/roles" 
            class="nav-link"
            :class="{ 'collapsed': !isExpanded }"
          >
            <i class="bi bi-shield-lock"></i>
            <span v-show="isExpanded">Roles</span>
          </router-link>
        </div>

        <div class="nav-section" v-if="authStore.isSuperAdmin || authStore.isAdmin">
          <router-link 
            to="/resources" 
            class="nav-link"
            :class="{ 'collapsed': !isExpanded }"
          >
            <i class="bi bi-box"></i>
            <span v-show="isExpanded">Resources</span>
          </router-link>
        </div>

        <div class="nav-section" v-if="authStore.isSuperAdmin || authStore.isAdmin">
          <router-link 
            to="/scopes" 
            class="nav-link"
            :class="{ 'collapsed': !isExpanded }"
          >
            <i class="bi bi-diagram-3"></i>
            <span v-show="isExpanded">Scopes</span>
          </router-link>
        </div>

        <div class="nav-section" v-if="authStore.isSuperAdmin || authStore.isAdmin">
          <router-link 
            to="/policies" 
            class="nav-link"
            :class="{ 'collapsed': !isExpanded }"
          >
            <i class="bi bi-file-earmark-text"></i>
            <span v-show="isExpanded">Policies</span>
          </router-link>
        </div>

        <div class="nav-section" v-if="authStore.isSuperAdmin">
          <router-link 
            to="/organizations" 
            class="nav-link"
            :class="{ 'collapsed': !isExpanded }"
          >
            <i class="bi bi-building"></i>
            <span v-show="isExpanded">Organizations</span>
          </router-link>
        </div>

        <div class="nav-section" v-if="authStore.isSuperAdmin || authStore.isAdmin">
          <router-link 
            to="/clients" 
            class="nav-link"
            :class="{ 'collapsed': !isExpanded }"
          >
            <i class="bi bi-app"></i>
            <span v-show="isExpanded">Clients</span>
          </router-link>
        </div>

        <div class="nav-section" v-if="authStore.isSuperAdmin || authStore.isAdmin">
          <router-link
            to="/services"
            class="nav-link"
            :class="{ 'collapsed': !isExpanded }"
          >
            <i class="bi bi-hdd-rack"></i>
            <span v-show="isExpanded">Services</span>
          </router-link>
        </div>

        <div class="nav-section" v-if="authStore.can('PRODUCT_READ')">
          <router-link 
            to="/products" 
            class="nav-link"
            :class="{ 'collapsed': !isExpanded }"
          >
            <i class="bi bi-bag"></i>
            <span v-show="isExpanded">Products</span>
          </router-link>
        </div>
      </nav>

      <!-- Footer -->
      <div class="sidebar-footer">
        <div v-if="isExpanded" class="user-section">
          <div class="user-info">
            <div class="user-avatar">
              {{ getUserInitials() }}
            </div>
            <div class="user-details">
              <span class="user-name">{{ authStore.user?.fullName }}</span>
              <span class="user-role">{{ getUserRole() }}</span>
            </div>
          </div>
          <button 
            class="logout-button" 
            @click="authStore.logout"
          >
            <i class="bi bi-box-arrow-right"></i>
          </button>
        </div>
        <div v-else class="user-section-collapsed">
          <button 
            class="logout-button" 
            @click="authStore.logout"
          >
            <i class="bi bi-box-arrow-right"></i>
          </button>
        </div>
        <p v-show="isExpanded" class="copyright">
          © {{ new Date().getFullYear() }} Access Manager
        </p>
      </div>
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

    <!-- AI Chatbox Component -->
    <AIChatbox />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useThemeStore } from '../stores/theme'
import AppHeader from '../components/layout/AppHeader.vue'
import AppFooter from '../components/layout/AppFooter.vue'
import Breadcrumb from '../components/layout/Breadcrumb.vue'
import AIChatbox from '../components/AIChatbox.vue'

const authStore = useAuthStore()
const themeStore = useThemeStore()
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
  themeStore.initTheme()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
  background-color: var(--bs-body-bg);
  color: var(--bs-body-color);
}

.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  width: 16rem;
  background-color: var(--bs-sidebar-bg);
  color: var(--bs-sidebar-color);
  transition: all 0.3s ease;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
}

.sidebar.collapsed {
  width: 5rem;
}

.sidebar-header {
  display: flex;
  align-items: center;
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid var(--bs-border-color-translucent);
}

.logo {
  font-size: 2rem;
  color: var(--bs-primary);
  margin-right: 0.75rem;
}

.site-title {
  font-size: 1.25rem;
  font-weight: 600;
  white-space: nowrap;
}

.sidebar-nav {
  flex-grow: 1;
  padding: 1rem 0;
  overflow-y: auto;
}

.nav-section {
  margin-bottom: 0.25rem;
}

.nav-section-title {
  padding: 0 1.5rem;
  font-size: 0.75rem;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.5);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 0.75rem;
}

.nav-link {
  display: flex;
  align-items: center;
  padding: 0.75rem 1.5rem;
  color: var(--bs-sidebar-color);
  text-decoration: none;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.nav-link:hover,
.nav-link.router-link-exact-active {
  background-color: var(--bs-sidebar-hover);
  color: var(--bs-primary);
  border-left: 3px solid var(--bs-primary);
  padding-left: calc(1.5rem - 3px);
}

.nav-link i {
  font-size: 1.25rem;
  margin-right: 1rem;
  width: 2rem;
  text-align: center;
}

.nav-link.collapsed i {
  margin-right: 0;
}

.sidebar-footer {
  padding: 1rem;
  border-top: 1px solid var(--bs-border-color-translucent);
}

.user-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.user-info {
  display: flex;
  align-items: center;
  min-width: 0;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: var(--bs-primary);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  margin-right: 0.75rem;
}

.user-details {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
}

.user-name {
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  font-size: 0.8rem;
  color: var(--bs-sidebar-color);
  opacity: 0.7;
  text-transform: capitalize;
  white-space: nowrap;
}

.logout-button {
  background: none;
  border: none;
  color: var(--bs-sidebar-color);
  font-size: 1.5rem;
  cursor: pointer;
  transition: color 0.2s ease;
}

.logout-button:hover {
  color: var(--bs-primary);
}

.user-section-collapsed {
  display: flex;
  justify-content: center;
  padding: 0.5rem 0;
}

.copyright {
  font-size: 0.75rem;
  color: var(--bs-body-color-muted);
  text-align: center;
  margin-top: 1rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
  
.sidebar-toggle {
  position: absolute;
  top: 1.25rem;
  right: -2rem;
  width: 30px;
  height: 30px;
  background-color: var(--bs-tertiary-bg);
  border: none;
  border-radius: 0;
  color: var(--bs-emphasis-color);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 1100;
  transition: all 0.3s ease;
  box-shadow: var(--bs-box-shadow-sm);
}

.sidebar-toggle:hover {
  background-color: var(--bs-secondary-bg);
  transform: scale(1.1);
}

.sidebar.collapsed .sidebar-toggle {
  right: -30px;
  top: 1.25rem;
  background-color: var(--bs-tertiary-bg);
  border: none;
  color: var(--bs-emphasis-color);
  border-radius: 0;
  left: auto;
  transform: none;
}

.main-content {
  flex-grow: 1;
  padding: 1.5rem;
  margin-left: 16rem;
  transition: margin-left 0.3s ease;
}

.main-content.expanded {
  margin-left: 5rem;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.sidebar-header-link {
  text-decoration: none;
}

.theme-toggle {
  background: none;
  border: none;
  color: inherit;
  cursor: pointer;
  padding: 0.5rem;
  font-size: 1.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease;
  margin-left: auto;
}

.theme-toggle:hover {
  transform: scale(1.1);
}

.theme-toggle i {
  transition: transform 0.3s ease;
}

.theme-toggle:active i {
  transform: rotate(180deg);
}
</style>