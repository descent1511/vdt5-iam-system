<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Scopes</h1>
      
      <router-link to="/scopes/create" class="btn btn-primary">
        <i class="bi bi-plus-lg me-2"></i> New Scope
      </router-link>
    </div>

    <!-- Loading State -->
    <div v-if="scopeStore.loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-2">Loading scopes...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="scopeStore.error" class="alert alert-danger">
      {{ scopeStore.error }}
    </div>

    <!-- Empty State -->
    <div v-else-if="scopeStore.scopes.length === 0" class="card">
      <div class="card-body">
        <div class="table-responsive">
          <table class="table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Description</th>
                <th>Permissions</th>
                <th>Created At</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td colspan="5">
                  <div class="text-center py-4">
                    <div class="text-muted">
                      <i class="bi bi-shield-lock fs-4 d-block mb-2"></i>
                      No scopes found
                    </div>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Scopes Table -->
    <div v-else>
      <div class="card">
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-hover">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Description</th>
                  <th>Permissions</th>
                  <th>Created At</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="scope in scopeStore.scopes" :key="scope.id">
                  <td>
                    <div class="d-flex align-items-center">
                      <i class="bi bi-shield-lock text-primary me-2"></i>
                      <span class="fw-medium">{{ scope.name }}</span>
                    </div>
                  </td>
                  <td>{{ scope.description }}</td>
                  <td>
                    <div class="permissions-container">
                      <div class="permissions-wrapper" :class="{ 'expanded': scope.showAllPermissions }">
                        <span class="permission-count">
                          {{ scope.permissions.length }} permissions
                        </span>
                        <div class="permissions-list" :class="{ 'show': scope.showAllPermissions }">
                          <span 
                            v-for="permission in scope.permissions" 
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
                        @click="togglePermissions(scope)"
                      >
                        <span class="toggle-content">
                          <span v-if="!scope.showAllPermissions">
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
                  <td>{{ formatDate(scope.createdAt) }}</td>
                  <td>
                    <div class="btn-group">
                      <router-link 
                        :to="'/scopes/' + scope.id + '/edit'" 
                        class="btn btn-sm btn-outline-primary"
                      >
                        <i class="bi bi-pencil"></i>
                      </router-link>
                      <button 
                        class="btn btn-sm btn-outline-danger"
                        @click="confirmDelete(scope)"
                      >
                        <i class="bi bi-trash"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal fade" ref="deleteModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Delete Scope</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <p>Are you sure you want to delete the scope "{{ scopeToDelete?.name }}"?</p>
            <p class="text-danger mb-0">This action cannot be undone.</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
            <button 
              type="button" 
              class="btn btn-danger"
              @click="deleteScope"
              :disabled="scopeStore.loading"
            >
              <span v-if="scopeStore.loading" class="spinner-border spinner-border-sm me-2"></span>
              Delete Scope
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useScopeStore } from '../../stores/scopes'
import { useToast } from 'vue-toastification'
import { Modal } from 'bootstrap'

const scopeStore = useScopeStore()
const toast = useToast()

const deleteModal = ref(null)
const scopeToDelete = ref(null)
let modal = null

onMounted(async () => {
  await loadScopes()
  modal = new Modal(deleteModal.value)
})

async function loadScopes() {
  try {
    await scopeStore.fetchScopes()
  } catch (error) {
    toast.error('Failed to load scopes')
  }
}

function formatDate(dateString) {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

function confirmDelete(scope) {
  scopeToDelete.value = scope
  modal.show()
}

async function deleteScope() {
  if (!scopeToDelete.value) return

  try {
    await scopeStore.deleteScope(scopeToDelete.value.id)
    toast.success('Scope deleted successfully')
    modal.hide()
    scopeToDelete.value = null
  } catch (error) {
    toast.error('Failed to delete scope')
  }
}

function togglePermissions(scope) {
  scope.showAllPermissions = !scope.showAllPermissions
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
.card {
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border-radius: 12px;
}

.table {
  margin-bottom: 0;
}

.table th {
  font-weight: 500;
  color: #2c3e50;
  border-top: none;
  padding: 1rem;
  background-color: #f8f9fa;
}

.table td {
  padding: 1rem;
  vertical-align: middle;
}

.badge {
  font-weight: 500;
  padding: 0.35em 0.65em;
  font-size: 0.75rem;
  background-color: #e9ecef;
  color: #495057;
}

.btn-group .btn {
  padding: 0.25rem 0.5rem;
  transition: all 0.2s ease;
}

.btn-group .btn:hover {
  transform: translateY(-1px);
}

.btn-group .btn i {
  font-size: 0.875rem;
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