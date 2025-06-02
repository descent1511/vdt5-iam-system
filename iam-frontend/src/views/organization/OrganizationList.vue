<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Organizations</h1>
      
      <router-link 
        to="/organizations/create" 
        class="btn btn-primary"
        v-if="authStore.can('ORGANIZATION_CREATE')"
      >
        <i class="bi bi-plus-lg me-2"></i>Create Organization
      </router-link>
    </div>
    
    <div class="card">
      <div class="card-body">
        <!-- Loading state -->
        <div v-if="organizationStore.loading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
        </div>
        
        <!-- Error state -->
        <div v-else-if="organizationStore.error" class="alert alert-danger">
          {{ organizationStore.error }}
        </div>
        
        <!-- Organizations table -->
        <div v-else class="table-responsive">
          <table class="table table-hover">
            <thead>
              <tr>
                <th>Name</th>
                <th>Description</th>
                <th>Contact</th>
                <th>Created At</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="org in organizationStore.organizations" :key="org.id">
                <td>
                  <div class="d-flex align-items-center">
                    <i class="bi bi-building me-2 text-primary"></i>
                    {{ org.name }}
                  </div>
                </td>
                <td>{{ org.description }}</td>
                <td>
                  <div class="d-flex flex-column">
                    <small class="text-muted">
                      <i class="bi bi-envelope me-1"></i>{{ org.email }}
                    </small>
                    <small class="text-muted">
                      <i class="bi bi-telephone me-1"></i>{{ org.phone }}
                    </small>
                  </div>
                </td>
                <td>{{ formatDate(org.createdAt) }}</td>
                <td>
                  <div class="btn-group">
                    <router-link 
                      :to="`/organizations/${org.id}/edit`"
                      class="btn btn-sm btn-outline-primary"
                      v-if="authStore.can('ORGANIZATION_UPDATE')"
                    >
                      <i class="bi bi-pencil"></i>
                    </router-link>
                    
                    <button 
                      class="btn btn-sm btn-outline-danger"
                      @click="confirmDelete(org)"
                      v-if="authStore.can('ORGANIZATION_DELETE')"
                    >
                      <i class="bi bi-trash"></i>
                    </button>
                  </div>
                </td>
              </tr>
              
              <!-- Empty state -->
              <tr v-if="organizationStore.organizations.length === 0">
                <td colspan="5" class="text-center py-4">
                  <div class="text-muted">
                    <i class="bi bi-building fs-4 d-block mb-2"></i>
                    No organizations found
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    
    <!-- Delete confirmation modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Confirm Delete</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            Are you sure you want to delete the organization "{{ organizationToDelete?.name }}"?
            This action cannot be undone.
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
            <button 
              type="button" 
              class="btn btn-danger"
              @click="handleDelete"
              :disabled="organizationStore.loading"
            >
              <span v-if="organizationStore.loading" class="spinner-border spinner-border-sm me-2"></span>
              Delete
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useOrganizationStore } from '../../stores/organizations'
import { useAuthStore } from '../../stores/auth'
import { useToast } from 'vue-toastification'
import { Modal } from 'bootstrap'

// Stores
const organizationStore = useOrganizationStore()
const authStore = useAuthStore()
const toast = useToast()

// State
const organizationToDelete = ref(null)
let deleteModal = null

// Lifecycle hooks
onMounted(async () => {
  try {
    await organizationStore.fetchOrganizations()
  } catch (error) {
    console.error('Failed to fetch organizations:', error)
  }
  
  // Initialize delete modal
  deleteModal = new Modal(document.getElementById('deleteModal'))
})

// Methods
function formatDate(date) {
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

function confirmDelete(organization) {
  organizationToDelete.value = organization
  deleteModal.show()
}

async function handleDelete() {
  if (!organizationToDelete.value) return
  
  try {
    await organizationStore.deleteOrganization(organizationToDelete.value.id)
    toast.success('Organization deleted successfully')
    deleteModal.hide()
  } catch (error) {
    console.error('Failed to delete organization:', error)
    toast.error('Failed to delete organization')
  }
}
</script>

<style scoped>
.card {
  border: none;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.08);
  border-radius: 12px;
}

.table {
  margin-bottom: 0;
}

.table th {
  font-weight: 600;
  color: #495057;
  border-bottom-width: 1px;
}

.table td {
  vertical-align: middle;
}

.btn-group {
  gap: 0.5rem;
}

.btn-group .btn {
  padding: 0.25rem 0.5rem;
}

.modal-content {
  border: none;
  border-radius: 12px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

.modal-header {
  border-bottom: 1px solid #dee2e6;
  padding: 1rem 1.5rem;
}

.modal-footer {
  border-top: 1px solid #dee2e6;
  padding: 1rem 1.5rem;
}
</style> 