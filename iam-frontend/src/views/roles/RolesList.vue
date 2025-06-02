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
      
      <div v-else-if="!roleStore.totalRoles" class="col-12">
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
            <p class="card-text text-muted mb-3">{{ role.description || 'No description' }}</p>
            
            <div class="mt-3">
              <h6 class="mb-2 text-muted">Permissions:</h6>
              <PermissionDisplay :permissions="role.permissions" />
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
                :disabled="role.name === 'admin'"
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
import { useRouter } from 'vue-router'
import { useRoleStore } from '../../stores/roles'
import { useToast } from 'vue-toastification'
import PermissionDisplay from '../../components/PermissionDisplay.vue'
import * as bootstrap from 'bootstrap'

// Store
const roleStore = useRoleStore()
const router = useRouter()
const toast = useToast()

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
  try {
  await roleStore.fetchRoles()
  } catch (error) {
    toast.error('Failed to load roles')
  }
}

function formatDate(dateString) {
  if (!dateString) return 'Unknown date'
  
  const date = new Date(dateString)
  if (isNaN(date.getTime())) return 'Unknown date'
  
  return new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  }).format(date)
}

function confirmDelete(role) {
  if (role.name === 'admin') {
    toast.warning('Cannot delete admin role')
    return
  }
  
  roleToDelete.value = role
  deleteModal.show()
}

async function deleteRole() {
  if (!roleToDelete.value) return
  
  try {
    await roleStore.deleteRole(roleToDelete.value.id)
    toast.success('Role deleted successfully')
    deleteModal.hide()
    roleToDelete.value = null
  } catch (error) {
    toast.error('Failed to delete role')
  }
}
</script>

<style scoped>
.card {
  border: none;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.08);
  border-radius: 12px;
  transition: transform 0.2s ease-in-out;
}

.card:hover {
  transform: translateY(-5px);
}

.card-header {
  background-color: transparent;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  padding: 1.25rem;
}

.card-header h5 {
  font-weight: 600;
  color: #2c3e50;
}

.card-body {
  padding: 1.25rem;
}

.card-footer {
  background-color: transparent;
  border-top: 1px solid rgba(0, 0, 0, 0.08);
  padding: 1rem 1.25rem;
}

.badge {
  padding: 0.5em 0.75em;
  font-weight: 500;
  border-radius: 50rem;
}

.empty-state {
  padding: 3rem;
  text-align: center;
  color: #6c757d;
  background-color: #f8f9fa;
  border-radius: 12px;
}

.empty-state i {
  font-size: 3rem;
  margin-bottom: 1rem;
  color: #dee2e6;
}

.btn-group {
  gap: 0.5rem;
}

.btn-sm {
  padding: 0.35rem 0.75rem;
  font-size: 0.875rem;
  line-height: 1.5;
  border-radius: 8px;
  font-weight: 500;
}

.btn-sm i {
  font-size: 0.875rem;
}

.btn-outline-primary {
  color: #0d6efd;
  border-color: #0d6efd;
}

.btn-outline-primary:hover {
  background-color: #0d6efd;
  border-color: #0d6efd;
  color: white;
}

.btn-outline-danger {
  color: #dc3545;
  border-color: #dc3545;
}

.btn-outline-danger:hover {
  background-color: #dc3545;
  border-color: #dc3545;
  color: white;
}

.btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

/* Modal styles */
.modal-content {
  border: none;
  border-radius: 12px;
  box-shadow: 0 0 30px rgba(0, 0, 0, 0.1);
}

.modal-header {
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  padding: 1.25rem;
}

.modal-footer {
  border-top: 1px solid rgba(0, 0, 0, 0.08);
  padding: 1.25rem;
}

.modal-title {
  font-weight: 600;
  color: #2c3e50;
}
</style>