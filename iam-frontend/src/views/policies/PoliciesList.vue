<template>
  <DataList
    ref="dataList"
    title="Policies"
    :items="policyStore.policies"
    :columns="columns"
    :can-create="hasPermission('POLICY_CREATE')"
    create-route="/policies/create"
    create-button-text="New Policy"
    search-placeholder="Search policies..."
    :search-fields="['subjectType', 'subjectId', 'action']"
    :loading="policyStore.loading"
    :error="policyStore.error"
    @delete="deletePolicy"
  >
    <!-- Custom column slots -->
    <template #subject="{ item }">
      <div class="d-flex align-items-center">
        <i class="bi bi-person-badge text-primary me-2"></i>
        <div class="d-flex flex-column">
          <span class="fw-medium text-dark">{{ item.subjectType }}</span>
          <small class="text-muted">{{ getSubjectName(item) }}</small>
        </div>
      </div>
    </template>

    <template #effect="{ item }">
      <span 
        class="badge"
        :class="item.effect === 'ALLOW' ? 'bg-success' : 'bg-danger'"
      >
        {{ item.effect }}
      </span>
    </template>

    <template #action="{ item }">
      <span class="badge bg-info">{{ item.action }}</span>
    </template>

    <template #resource="{ item }">
      <span class="text-muted small">{{ getResourcePath(item.resourceId) }}</span>
    </template>

    <template #actions="{ item }">
      <div class="d-flex justify-content-end gap-2">
        <router-link 
          v-if="hasPermission('POLICY_UPDATE')"
          :to="`/policies/${item.id}/edit`" 
          class="btn btn-sm btn-outline-primary"
          title="Edit"
        >
          <i class="bi bi-pencil"></i>
        </router-link>
        <button 
          v-if="hasPermission('POLICY_DELETE')"
          class="btn btn-sm btn-outline-danger"
          @click="confirmDelete(item)"
          title="Delete"
        >
          <i class="bi bi-trash"></i>
        </button>
      </div>
    </template>
  </DataList>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { usePolicyStore } from '../../stores/policies'
import { useAuthStore } from '../../stores/auth'
import DataList from '../../components/layout/DataList.vue'

const policyStore = usePolicyStore()
const authStore = useAuthStore()
const dataList = ref(null)

const columns = [
  { key: 'subject', label: 'Subject' },
  { key: 'effect', label: 'Effect' },
  { key: 'action', label: 'Action' },
  { key: 'resource', label: 'Resource' },
  { key: 'actions', label: 'Actions', class: 'text-end' }
]

function getSubjectName(item) {
  if (item.subjectType === 'USER') {
    return item.username || `User ${item.subjectId}`
  } else if (item.subjectType === 'CLIENT') {
    return item.clientName || `Client ${item.subjectId}`
  }
  return `ID: ${item.subjectId}`
}

function getResourcePath(resourceId) {
  // TODO: Implement resource path lookup
  return `/api/resources/${resourceId}`
}

function confirmDelete(policy) {
  policyStore.selectedPolicy = policy
  dataList.value.showDeleteModal()
}

async function deletePolicy() {
  if (!policyStore.selectedPolicy) return

  try {
    await policyStore.deletePolicy(policyStore.selectedPolicy.id)
    await loadPolicies()
    dataList.value.hideDeleteModal()
  } catch (error) {
    console.error('Failed to delete policy:', error)
  }
}

function hasPermission(permission) {
  return authStore.can(permission)
}

async function loadPolicies() {
  await policyStore.fetchPolicies()
}

onMounted(loadPolicies)
</script>

<style scoped>
/* Table styles */
:deep(.table) {
  margin-bottom: 0;
}

:deep(.table th) {
  font-weight: 600;
  color: #495057;
  border-top: none;
  padding: 0.75rem 1rem;
  background-color: #f8f9fa;
  white-space: nowrap;
}

:deep(.table td) {
  padding: 0.75rem 1rem;
  vertical-align: middle;
  border-bottom: 1px solid #e9ecef;
}

:deep(.table tr:hover) {
  background-color: #f8f9fa;
}

/* Column specific styles */
:deep(.table td:nth-child(1)) { /* Subject column */
  min-width: 200px;
}

:deep(.table td:nth-child(2)) { /* Effect column */
  min-width: 80px;

}

:deep(.table td:nth-child(3)) { /* Action column */
  min-width: 120px;
}

:deep(.table td:nth-child(4)) { /* Resource column */
  min-width: 200px;
}

:deep(.table td:nth-child(5)) { /* Actions column */
  min-width: 80px;
 
  text-align: right;
}

/* Badge styles */
.badge {
  font-size: 0.75rem;
  padding: 0.35em 0.65em;
  font-weight: 500;
  border-radius: 4px;
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
  border-radius: 8px;
  margin-bottom: 1.5rem;
}

:deep(.card-body) {
  padding: 1.25rem;
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
  
  :deep(.table td:nth-child(4)) {
    min-width: 150px;
  }
}

</style>