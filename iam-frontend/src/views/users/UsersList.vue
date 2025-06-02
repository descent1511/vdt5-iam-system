<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Users</h1>
      
      <router-link to="/users/create" class="btn btn-primary">
        <i class="bi bi-plus-lg me-2"></i> New User
      </router-link>
    </div>
    
    <!-- Filters -->
    <div class="card mb-4">
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-4">
            <div class="form-group">
              <label for="searchInput" class="form-label">Search</label>
              <input 
                type="text" 
                class="form-control" 
                id="searchInput" 
                v-model="filters.search"
                placeholder="Search users..."
                @input="applyFilters"
              >
            </div>
          </div>
          
          <div class="col-md-3">
            <div class="form-group">
              <label for="roleFilter" class="form-label">Role</label>
              <select 
                class="form-select" 
                id="roleFilter" 
                v-model="filters.role"
                @change="applyFilters"
              >
                <option value="">All Roles</option>
                <option value="ROLE_ADMIN">Admin</option>
                <option value="ROLE_MANAGER">Manager</option>
                <option value="ROLE_USER">User</option>
              </select>
            </div>
          </div>
          
          <div class="col-md-3">
            <div class="form-group">
              <label for="statusFilter" class="form-label">Status</label>
              <select 
                class="form-select" 
                id="statusFilter" 
                v-model="filters.status"
                @change="applyFilters"
              >
                <option value="">All Status</option>
                <option value="active">Active</option>
                <option value="inactive">Inactive</option>
              </select>
            </div>
          </div>
          
          <div class="col-md-2 d-flex align-items-end">
            <button 
              class="btn btn-outline-secondary w-100" 
              @click="resetFilters"
            >
              <i class="bi bi-x-circle me-2"></i> Reset
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Users Table -->
    <div class="card">
      <div class="card-body">
        <div v-if="userStore.loading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
          <p class="mt-2">Loading users...</p>
        </div>
        
        <div v-else-if="userStore.error" class="alert alert-danger">
          {{ userStore.error }}
        </div>
        
        <div v-else-if="!userStore.totalUsers" class="empty-state">
          <i class="bi bi-people"></i>
          <h3>No Users Found</h3>
          <p>There are no users matching your criteria.</p>
          <router-link to="/users/create" class="btn btn-primary">
            <i class="bi bi-plus-lg me-2"></i> Add User
          </router-link>
        </div>
        
        <div v-else class="table-responsive">
          <table class="table table-hover align-middle">
            <thead>
              <tr>
                <th scope="col">Name</th>
                <th scope="col">Username</th>
                <th scope="col">Email</th>
                <th scope="col">Roles</th>
                <th scope="col">Permissions</th>
                <th scope="col">Action</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in userStore.users" :key="user.id">
                <td>
                  <div class="d-flex align-items-center">
                    <div class="avatar me-2 d-flex align-items-center justify-content-center bg-primary text-white rounded-circle">
                      {{ getInitials(user.fullName) }}
                    </div>
                    <div>
                      {{ user.fullName }}
                    </div>
                  </div>
                </td>
                <td>{{ user.username }}</td>
                <td>{{ user.email }}</td>
                <td>
                  <div class="d-flex gap-1">
                    <span 
                      v-for="role in user.roles" 
                      :key="role"
                      :class="`badge rounded-pill bg-${getRoleBadgeColor(role)}`"
                    >
                      {{ role.replace('ROLE_', '') }}
                    </span>
                  </div>
                </td>
                <td>
                  <div class="permissions-container">
                    <div class="permissions-wrapper" :class="{ 'expanded': user.showAllPermissions }">
                      <span class="permission-count">
                        {{ user.permissions.length }} permissions
                      </span>
                      <div class="permissions-list" :class="{ 'show': user.showAllPermissions }">
                        <span 
                          v-for="permission in user.permissions" 
                          :key="permission"
                          class="permission-badge"
                        >
                          <span class="permission-type">{{ formatPermissionType(permission) }}</span>
                          <span class="permission-action">{{ formatPermissionAction(permission) }}</span>
                        </span>
                      </div>
                    </div>
                    <button 
                      class="btn btn-sm btn-link toggle-btn"
                      @click="togglePermissions(user)"
                    >
                      <span class="toggle-content">
                        <span v-if="!user.showAllPermissions">
                          Detail
                          <i class="bi bi-chevron-down"></i>
                        </span>
                        <span v-else>
                          Hide
                          <i class="bi bi-chevron-up"></i>
                        </span>
                      </span>
                    </button>
                  </div>
                </td>
                <td>
                  <div class="d-flex gap-2">
                    <router-link 
                      v-if="hasPermission('USER_UPDATE')"
                      :to="`/users/${user.id}/edit`" 
                      class="btn btn-sm btn-outline-primary"
                    >
                      <i class="bi bi-pencil"></i>
                    </router-link>
                    <button 
                      v-if="hasPermission('USER_DELETE')"
                      class="btn btn-sm btn-outline-danger"
                      @click="confirmDelete(user)"
                    >
                      <i class="bi bi-trash"></i>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <!-- Pagination -->
        <div v-if="userStore.users.length" class="d-flex justify-content-between align-items-center mt-4">
          <div>
            Showing {{ ((userStore.pagination.page - 1) * userStore.pagination.limit) + 1 }} 
            to {{ Math.min(userStore.pagination.page * userStore.pagination.limit, userStore.totalUsers) }} 
            of {{ userStore.totalUsers }} users
          </div>
          
          <nav aria-label="Page navigation">
            <ul class="pagination mb-0">
              <li class="page-item" :class="{ disabled: userStore.pagination.page === 1 }">
                <a class="page-link" href="#" @click.prevent="changePage(userStore.pagination.page - 1)">
                  <i class="bi bi-chevron-left"></i>
                </a>
              </li>
              
              <li 
                v-for="page in getPageNumbers()" 
                :key="page" 
                class="page-item"
                :class="{ active: page === userStore.pagination.page }"
              >
                <a class="page-link" href="#" @click.prevent="changePage(page)">
                  {{ page }}
                </a>
              </li>
              
              <li class="page-item" :class="{ disabled: userStore.pagination.page === userStore.pagination.totalPages }">
                <a class="page-link" href="#" @click.prevent="changePage(userStore.pagination.page + 1)">
                  <i class="bi bi-chevron-right"></i>
                </a>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>
    
    <!-- Delete Confirmation Modal -->
    <div 
      class="modal fade" 
      id="deleteModal" 
      tabindex="-1" 
      aria-labelledby="deleteModalLabel" 
      aria-hidden="true"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="deleteModalLabel">Confirm Delete</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body" v-if="userToDelete">
            <p>Are you sure you want to delete the user <strong>{{ userToDelete.fullName }}</strong>?</p>
            <p class="text-danger">This action cannot be undone.</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
            <button 
              type="button" 
              class="btn btn-danger" 
              :disabled="userStore.loading"
              @click="deleteUser"
            >
              <span v-if="userStore.loading" class="spinner-border spinner-border-sm me-2" role="status"></span>
              Delete User
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '../../stores/users'
import { useAuthStore } from '../../stores/auth'
import * as bootstrap from 'bootstrap'

// Store
const userStore = useUserStore()
const authStore = useAuthStore()

// State
const filters = ref({
  search: '',
  role: '',
  status: ''
})

const userToDelete = ref(null)
let deleteModal = null

// Lifecycle hooks
onMounted(async () => {
  await loadUsers()
  
  // Initialize delete modal
  deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'))
})

// Methods
async function loadUsers() {
  const params = {
    page: userStore.pagination.page,
    limit: userStore.pagination.limit,
    ...filters.value
  }
  
  await userStore.fetchUsers(params)
}

function getInitials(fullName) {
  if (!fullName) return ''
  return fullName.split(' ')
    .map(name => name.charAt(0))
    .join('')
    .toUpperCase()
}

function getRoleBadgeColor(role) {
  switch (role) {
    case 'ROLE_ADMIN':
      return 'danger'
    case 'ROLE_MANAGER':
      return 'info'
    case 'ROLE_USER':
      return 'secondary'
    default:
      return 'secondary'
  }
}

function applyFilters() {
  // Reset to first page when filters change
  userStore.pagination.page = 1
  loadUsers()
}

function resetFilters() {
  filters.value = {
    search: '',
    role: '',
    status: ''
  }
  
  applyFilters()
}

function changePage(page) {
  if (page < 1 || page > userStore.pagination.totalPages) return
  
  userStore.pagination.page = page
  loadUsers()
}

function getPageNumbers() {
  const totalPages = userStore.pagination.totalPages
  const currentPage = userStore.pagination.page
  
  if (totalPages <= 5) {
    return Array.from({ length: totalPages }, (_, i) => i + 1)
  }
  
  if (currentPage <= 3) {
    return [1, 2, 3, 4, 5]
  }
  
  if (currentPage >= totalPages - 2) {
    return [totalPages - 4, totalPages - 3, totalPages - 2, totalPages - 1, totalPages]
  }
  
  return [currentPage - 2, currentPage - 1, currentPage, currentPage + 1, currentPage + 2]
}

function confirmDelete(user) {
  userToDelete.value = user
  deleteModal.show()
}

async function deleteUser() {
  if (!userToDelete.value) return
  
  try {
    await userStore.deleteUser(userToDelete.value.id)
    deleteModal.hide()
    userToDelete.value = null
    await loadUsers() // Reload users after deletion
  } catch (error) {
    console.error('Failed to delete user:', error)
  }
}

function hasPermission(permission) {
  return authStore.can(permission)
}

function togglePermissions(user) {
  user.showAllPermissions = !user.showAllPermissions
}

function formatPermissionType(permission) {
  const type = permission.split('_')[0].toLowerCase()
  return type.charAt(0).toUpperCase() + type.slice(1)
}

function formatPermissionAction(permission) {
  const action = permission.split('_')[1].toLowerCase()
  return action.charAt(0).toUpperCase() + action.slice(1)
}
</script>

<style scoped>
.avatar {
  width: 36px;
  height: 36px;
  font-size: 14px;
  font-weight: 600;
}

.empty-state {
  padding: 3rem;
  text-align: center;
  color: var(--secondary);
}

.empty-state i {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.badge {
  font-size: 0.75rem;
  padding: 0.35em 0.65em;
}

.btn-link {
  font-size: 0.75rem;
  color: var(--bs-info);
}

.btn-link:hover {
  color: var(--bs-info-dark);
}

.permissions-container {
  position: relative;
  min-width: 150px;
}

.permissions-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.permission-count {
  font-size: 0.75rem;
  color: #6c757d;
  font-weight: 500;
}

.permissions-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.35rem;
  max-height: 0;
  opacity: 0;
  transform: translateY(-10px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.permissions-list.show {
  max-height: 500px;
  opacity: 1;
  transform: translateY(0);
}

.permission-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.25em 0.5em;
  font-size: 0.7rem;
  font-weight: 500;
  color: #0c5460;
  background-color: #d1ecf1;
  border: 1px solid #bee5eb;
  border-radius: 50rem;
  transition: all 0.2s ease-in-out;
  gap: 0.35rem;
  white-space: nowrap;
  transform: scale(0.95);
  opacity: 0;
  animation: fadeIn 0.3s cubic-bezier(0.4, 0, 0.2, 1) forwards;
}

.permissions-list.show .permission-badge {
  transform: scale(1);
  opacity: 1;
}

@keyframes fadeIn {
  from {
    transform: scale(0.95);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

.permission-type {
  color: #0a3d42;
  font-weight: 600;
}

.permission-action {
  color: #0c5460;
  opacity: 0.9;
}

.permission-badge:hover {
  background-color: #bee5eb;
  transform: translateY(-1px);
}

.toggle-btn {
  position: absolute;
  right: 0;
  top: 0;
  padding: 0;
  font-size: 0.75rem;
  color: #0c5460;
  background: linear-gradient(90deg, transparent, #fff 30%);
  border: none;
  transition: all 0.2s ease-in-out;
}

.toggle-content {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.25rem 0.5rem;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 50rem;
  transition: all 0.2s ease-in-out;
}

.toggle-btn:hover {
  color: #0a3d42;
}

.toggle-btn:hover .toggle-content {
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.toggle-btn i {
  font-size: 0.7rem;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.permissions-list.show + .toggle-btn i {
  transform: rotate(180deg);
}
</style>