<template>
  <DataList
    ref="dataList"
    title="Scopes"
    :items="scopeStore.scopes"
    :columns="columns"
    :can-create="hasPermission('SCOPE_CREATE')"
    create-route="/scopes/create"
    create-button-text="New Scope"
    search-placeholder="Search scopes..."
    :search-fields="['name', 'description']"
    :loading="scopeStore.loading"
    :error="scopeStore.error"
    @delete="deleteScope"
  >
    <!-- Custom column slots -->
    <template #name="{ item }">
                    <div class="d-flex align-items-center">
                      <i class="bi bi-shield-lock text-primary me-2"></i>
        <span class="fw-medium">{{ item.name }}</span>
                    </div>
    </template>

    <template #permissions="{ item }">
                    <div class="permissions-container">
        <div class="permissions-wrapper" :class="{ 'expanded': item.showAllPermissions }">
                        <span class="permission-count">
            {{ item.permissions.length }} permissions
                        </span>
          <div class="permissions-list" :class="{ 'show': item.showAllPermissions }">
                          <span 
              v-for="permission in item.permissions" 
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
          @click="togglePermissions(item)"
                      >
                        <span class="toggle-content">
            <span v-if="!item.showAllPermissions">
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
    </template>

    <template #createdAt="{ item }">
      {{ formatDate(item.createdAt) }}
    </template>

    <template #actions="{ item }">
      <div class="d-flex justify-content-end gap-2">
        <router-link 
          v-if="hasPermission('SCOPE_UPDATE')"
          :to="`/scopes/${item.id}/edit`" 
          class="btn btn-sm btn-outline-primary"
          title="Edit Scope"
        >
          <i class="bi bi-pencil"></i>
        </router-link>
        <button 
          v-if="hasPermission('SCOPE_DELETE') && deletingScopeId !== item.id"
          class="btn btn-sm btn-outline-danger"
          @click="confirmDelete(item)"
          title="Delete Scope"
        >
          <i class="bi bi-trash"></i>
        </button>
      </div>
    </template>
  </DataList>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useScopeStore } from '../../stores/scopes'
import { useAuthStore } from '../../stores/auth'
import DataList from '../../components/layout/DataList.vue'

const scopeStore = useScopeStore()
const authStore = useAuthStore()
const dataList = ref(null)
const deletingScopeId = ref(null)

const columns = [
  { key: 'name', label: 'Name' },
  { key: 'description', label: 'Description' },
  { key: 'permissions', label: 'Permissions' },
  { key: 'createdAt', label: 'Created At' },
  { key: 'actions', label: 'Actions', class: 'text-end' }
]

function formatDate(dateString) {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

function confirmDelete(scope) {
  if (!hasPermission('SCOPE_DELETE')) {
    console.warn('User does not have SCOPE_DELETE permission')
    return
  }
  scopeStore.selectedScope = scope
  deletingScopeId.value = scope.id
  dataList.value.showDeleteModal()
}

async function deleteScope() {
  if (!scopeStore.selectedScope) {
    console.warn('No scope selected for deletion')
    return
  }
  if (!hasPermission('SCOPE_DELETE')) {
    console.warn('User does not have SCOPE_DELETE permission')
    return
  }

 
  try {
    const result = await scopeStore.deleteScope(scopeStore.selectedScope.id)
   
    if (result) {
      await loadScopes()
      dataList.value.hideDeleteModal()
      scopeStore.selectedScope = null
      deletingScopeId.value = null
    } else {
      console.error('Delete operation returned false')
    }
  } catch (error) {
    console.error('Failed to delete scope:', error)
    deletingScopeId.value = null
  }
}

function hasPermission(permission) {
  return authStore.can(permission)
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

async function loadScopes() {
  await scopeStore.fetchScopes()
}

onMounted(loadScopes)
</script>

<style scoped>
/* Permissions container styles */
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

/* Action buttons */
:deep(.btn-group) {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
}

:deep(.btn-group .btn) {
  padding: 0.375rem 0.5rem;
  line-height: 1;
}

:deep(.btn-group .btn i) {
  font-size: 0.875rem;
}

/* Card styles */
:deep(.card) {
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border-radius: 12px;
  margin-bottom: 1.5rem;
}

:deep(.card-body) {
  padding: 1.5rem;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  :deep(.table td),
  :deep(.table th) {
    padding: 0.75rem;
  }
  
  :deep(.table td:nth-child(1)) {
    min-width: 150px;
  }
  
  :deep(.table td:nth-child(2)) {
    min-width: 200px;
  }
  
  :deep(.table td:nth-child(3)) {
    min-width: 250px;
  }
}
</style> 