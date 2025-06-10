<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Resources</h1>
      
      <button 
        class="btn btn-primary" 
        @click="reloadResources"
        :disabled="resourceStore.loading"
      >
        <i class="bi bi-arrow-clockwise me-2"></i>
        <span v-if="resourceStore.loading" class="spinner-border spinner-border-sm me-2" role="status"></span>
        Reload Resources
      </button>
    </div>

    <div class="row g-4">
      <div v-if="resourceStore.loading" class="col-12 text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p class="mt-2">Loading resources...</p>
      </div>
      
      <div v-else-if="resourceStore.error" class="col-12">
        <div class="alert alert-danger">
          {{ resourceStore.error }}
        </div>
      </div>
      
      <div v-else-if="!resourceStore.totalResources" class="col-12">
        <div class="empty-state">
          <div class="empty-state-icon">
            <i class="bi bi-box"></i>
          </div>
          <h3>No Resources Found</h3>
        </div>
      </div>
      
      <div v-else v-for="resource in resourceStore.resources" :key="resource.id" class="col-md-6 col-lg-4">
        <div class="card h-100">
          <div class="card-header d-flex justify-content-between align-items-center">
            <h5 class="mb-0">{{ resource.name }}</h5>
            <div class="badge bg-primary">{{ resource.method }}</div>
          </div>
          <div class="card-body">
            <p class="card-text text-muted mb-3">{{ resource.description || 'No description' }}</p>
            
            <div class="mt-3">
              <h6 class="mb-2 text-muted">Path:</h6>
              <code class="d-block p-2 rounded">{{ resource.path }}</code>
            </div>

            <div class="mt-3" v-if="resource.requiredPermissions?.length">
              <h6 class="mb-2 text-muted">Required Permissions:</h6>
              <PermissionDisplay :permissions="resource.requiredPermissions" />
            </div>
          </div>
          <div class="card-footer d-flex justify-content-between align-items-center">
            <small class="text-muted">
              Created {{ formatDate(resource.createdAt) }}
            </small>
            
            <div class="btn-group">
              <router-link 
                :to="`/resources/${resource.id}/edit`" 
                class="btn btn-sm btn-outline-primary"
              >
                <i class="bi bi-pencil me-1"></i> Edit
              </router-link>
              <button 
                class="btn btn-sm btn-outline-danger"
                @click="confirmDelete(resource)"
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
          <div class="modal-body" v-if="resourceToDelete">
            <p>Are you sure you want to delete the resource <strong>{{ resourceToDelete.name }}</strong>?</p>
            <p class="text-danger">This action cannot be undone and may affect roles that use this resource.</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
            <button 
              type="button" 
              class="btn btn-danger" 
              :disabled="resourceStore.loading"
              @click="deleteResource"
            >
              <span v-if="resourceStore.loading" class="spinner-border spinner-border-sm me-2" role="status"></span>
              Delete Resource
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
import { useResourceStore } from '../../stores/resources'
import { useToast } from 'vue-toastification'
import PermissionDisplay from '../../components/PermissionDisplay.vue'
import * as bootstrap from 'bootstrap'

// Store
const resourceStore = useResourceStore()
const router = useRouter()
const toast = useToast()

// State
const resourceToDelete = ref(null)
let deleteModal = null

// Lifecycle hooks
onMounted(async () => {
  await loadResources()
  
  // Initialize delete modal
  deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'))
})

// Methods
async function loadResources() {
  try {
    await resourceStore.fetchResources()
  } catch (error) {
    toast.error('Failed to load resources')
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

function confirmDelete(resource) {
  resourceToDelete.value = resource
  deleteModal.show()
}

async function deleteResource() {
  if (!resourceToDelete.value) return
  
  try {
    const success = await resourceStore.deleteResource(resourceToDelete.value.id)
    if (success) {
      deleteModal.hide()
      resourceToDelete.value = null
    }
  } catch (error) {
    toast.error('Failed to delete resource')
  }
}

async function reloadResources() {
  try {
    const success = await resourceStore.discoverResources()
    if (success) {
      await loadResources() // Reload the list after discovery
    }
  } catch (error) {
    toast.error('Failed to reload resources')
  }
}
</script>

<style scoped>
.card {
  background-color: var(--bs-card-bg);
  border: 1px solid var(--bs-border-color-translucent);
  box-shadow: var(--bs-box-shadow);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: var(--bs-box-shadow-lg);
}

.card-header {
  background-color: transparent;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  padding: 1.25rem;
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
  font-size: 0.75rem;
}

.empty-state {
  padding: 4rem 2rem;
  text-align: center;
  background-color: #f8f9fa;
  border-radius: 12px;
  margin: 2rem 0;
}

.empty-state-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 1.5rem;
  background-color: #e9ecef;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-state-icon i {
  font-size: 2.5rem;
  color: #6c757d;
}

.empty-state h3 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 0.75rem;
}

.empty-state p {
  font-size: 1rem;
  margin-bottom: 1.5rem;
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
  transition: all 0.2s ease;
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
  transform: translateY(-1px);
}

.btn-outline-danger {
  color: #dc3545;
  border-color: #dc3545;
}

.btn-outline-danger:hover {
  background-color: #dc3545;
  border-color: #dc3545;
  color: white;
  transform: translateY(-1px);
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

/* Loading state */
.spinner-border {
  width: 3rem;
  height: 3rem;
}

.loading-text {
  margin-top: 1rem;
  color: #6c757d;
  font-size: 1rem;
}
</style>