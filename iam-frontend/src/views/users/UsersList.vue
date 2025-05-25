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
                  <div class="d-flex flex-wrap gap-1">
                    <span 
                      v-for="permission in user.permissions.slice(0, 3)" 
                      :key="permission"
                      class="badge rounded-pill bg-info"
                    >
                      {{ permission }}
                    </span>
                    <span 
                      v-if="user.permissions.length > 3" 
                      class="badge rounded-pill bg-secondary"
                    >
                      +{{ user.permissions.length - 3 }} more
                    </span>
                  </div>
                </td>
                <td>
                  <div class="d-flex gap-2">
                    <router-link 
                      :to="`/users/${user.id}/edit`" 
                      class="btn btn-sm btn-outline-primary"
                    >
                      <i class="bi bi-pencil"></i>
                    </router-link>
                    <button 
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
import * as bootstrap from 'bootstrap'

// Store
const userStore = useUserStore()

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
}
</style>