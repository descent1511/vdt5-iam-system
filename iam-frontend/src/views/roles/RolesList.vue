<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Roles</h1>
      
      <router-link to="/roles/create" class="btn btn-primary">
        <i class="bi bi-plus-lg me-2"></i> New Role
      </router-link>
    </div>
    
    <div class="row g-4">
      <div v-if="roleStore.loading" class="col-12 text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p class="mt-2">Loading roles...</p>
      </div>
      
      <div v-else-if="roleStore.error" class="col-12">
        <div class="alert alert-danger">
          {{ roleStore.error }}
        </div>
      </div>
      
      <div v-else-if="!roleStore.roles.length" class="col-12">
        <div class="empty-state">
          <i class="bi bi-person-badge"></i>
          <h3>No Roles Found</h3>
          <p>There are no roles defined yet.</p>
          <router-link to="/roles/create" class="btn btn-primary">
            <i class="bi bi-plus-lg me-2"></i> Add Role
          </router-link>
        </div>
      </div>
      
      <div v-else v-for="role in roleStore.roles" :key="role.id" class="col-md-6 col-lg-4">
        <div class="card h-100">
          <div class="card-header d-flex justify-content-between align-items-center">
            <h5 class="mb-0">{{ role.name }}</h5>
            <div class="badge bg-primary">{{ role.permissions.length }} permissions</div>
          </div>
          <div class="card-body">
            <p class="card-text">{{ role.description }}</p>
            
            <div class="mt-3">
              <h6 class="mb-2">Permissions:</h6>
              <div class="d-flex flex-wrap gap-1">
                <span 
                  v-for="(permission, index) in role.permissions" 
                  :key="index"
                  class="badge bg-light text-dark"
                >
                  {{ permission }}
                </span>
              </div>
            </div>
          </div>
          <div class="card-footer d-flex justify-content-between align-items-center">
            <small class="text-muted">
              Created {{ formatDate(role.createdAt) }}
            </small>
            
            <div class="btn-group">
              <router-link 
                :to="`/roles/${role.id}/edit`" 
                class="btn btn-sm btn-outline-primary"
              >
                <i class="bi bi-pencil me-1"></i> Edit
              </router-link>
              <button 
                class="btn btn-sm btn-outline-danger"
                @click="confirmDelete(role)"
                :disabled="role.name === 'admin'"
              >
                <i class="bi bi-trash me-1"></i> Delete
              </button>
            </div>
          </div>
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
          <div class="modal-body" v-if="roleToDelete">
            <p>Are you sure you want to delete the role <strong>{{ roleToDelete.name }}</strong>?</p>
            <p class="text-danger">This action cannot be undone and may affect users assigned to this role.</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
            <button 
              type="button" 
              class="btn btn-danger" 
              :disabled="roleStore.loading"
              @click="deleteRole"
            >
              <span v-if="roleStore.loading" class="spinner-border spinner-border-sm me-2" role="status"></span>
              Delete Role
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoleStore } from '../../stores/roles'
import * as bootstrap from 'bootstrap'

// Store
const roleStore = useRoleStore()

// State
const roleToDelete = ref(null)
let deleteModal = null

// Lifecycle hooks
onMounted(async () => {
  await loadRoles()
  
  // Initialize delete modal
  deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'))
})

// Methods
async function loadRoles() {
  await roleStore.fetchRoles()
}

function formatDate(dateString) {
  // Return early if dateString is invalid
  if (!dateString) return 'Unknown date'
  
  // Check if the date is valid
  const date = new Date(dateString)
  if (isNaN(date.getTime())) return 'Unknown date'
  
  return new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  }).format(date)
}

function confirmDelete(role) {
  // Prevent deleting the admin role
  if (role.name === 'admin') return
  
  roleToDelete.value = role
  deleteModal.show()
}

async function deleteRole() {
  if (!roleToDelete.value) return
  
  try {
    await roleStore.deleteRole(roleToDelete.value.id)
    deleteModal.hide()
    roleToDelete.value = null
  } catch (error) {
    console.error('Failed to delete role:', error)
  }
}
</script>

<style scoped>
.empty-state {
  padding: 3rem;
  text-align: center;
  color: var(--secondary);
}

.empty-state i {
  font-size: 3rem;
  margin-bottom: 1rem;
}
</style>