<template>
  <DataList
    ref="dataList"
    title="Organizations"
    :items="orgStore.organizations"
    :columns="columns"
    :can-create="hasPermission('ORGANIZATION_CREATE')"
    create-route="/organizations/create"
    create-button-text="New Organization"
    search-placeholder="Search organizations..."
    :search-fields="['name', 'description']"
    :loading="orgStore.loading"
    :error="orgStore.error"
    @delete="deleteOrganization"
  >
    <!-- Custom column slots -->
    <template #name="{ item }">
      <div class="d-flex align-items-center">
        <i class="bi bi-building text-primary me-2"></i>
        <span class="fw-medium">{{ item.name }}</span>
      </div>
    </template>

    <template #status="{ item }">
      <span 
        class="badge"
        :class="{
          'bg-success': item.status === 'ACTIVE',
          'bg-danger': item.status === 'INACTIVE',
          'bg-warning': item.status === 'PENDING'
        }"
      >
        {{ item.status }}
      </span>
    </template>

    <template #memberCount="{ item }">
      <div class="d-flex align-items-center">
        <i class="bi bi-people text-muted me-2"></i>
        <span>{{ item.memberCount || 0 }} members</span>
      </div>
    </template>

    <template #actions="{ item }">
      <div class="action-buttons">
        <router-link 
          v-if="hasPermission('ORGANIZATION_UPDATE')"
          :to="`/organizations/${item.id}/edit`" 
          class="btn btn-sm btn-outline-primary action-btn"
        >
          <i class="bi bi-pencil"></i>
        </router-link>
        <button 
          v-if="hasPermission('ORGANIZATION_DELETE')"
          class="btn btn-sm btn-outline-danger action-btn"
          @click="confirmDelete(item)"
        >
          <i class="bi bi-trash"></i>
        </button>
      </div>
    </template>
  </DataList>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useOrganizationStore } from '../../stores/organizations'
import { useAuthStore } from '../../stores/auth'
import DataList from '../../components/layout/DataList.vue'

const orgStore = useOrganizationStore()
const authStore = useAuthStore()
const dataList = ref(null)

const columns = [
  { key: 'name', label: 'Name' },
  { key: 'description', label: 'Description' },
  { key: 'status', label: 'Status' },
  { key: 'actions', label: 'Actions', class: 'text-end' }
]

function confirmDelete(organization) {
  orgStore.selectedOrganization = organization
  dataList.value.showDeleteModal()
}

async function deleteOrganization() {
  if (!orgStore.selectedOrganization) return

  try {
    await orgStore.deleteOrganization(orgStore.selectedOrganization.id)
    await loadOrganizations()
  } catch (error) {
    console.error('Failed to delete organization:', error)
  }
}

function hasPermission(permission) {
  return authStore.can(permission)
}

async function loadOrganizations() {
  await orgStore.fetchOrganizations()
}

onMounted(loadOrganizations)
</script>

<style scoped>
/* Table styles */
:deep(.table) {
  margin-bottom: 0;
  table-layout: fixed; /* Đảm bảo chiều rộng cột cố định */
  width: 100%; /* Đảm bảo bảng chiếm toàn bộ chiều rộng */
}

:deep(.table th) {
  font-weight: 600;
  color: #495057;
  border-top: none;
  padding: 0.75rem 1rem;
  background-color: #f8f9fa;
  white-space: nowrap;
  text-align: left; /* Căn chỉnh tiêu đề */
}

:deep(.table td) {
  padding: 0.75rem 1rem;
  vertical-align: middle;
  border-bottom: 1px solid #e9ecef;
  text-align: left; /* Căn chỉnh nội dung cột */
  overflow: hidden; /* Xử lý nội dung tràn */
  text-overflow: ellipsis; /* Thêm dấu ba chấm cho văn bản dài */
}

:deep(.table tr:hover) {
  background-color: #f8f9fa;
}

/* Column specific styles */
:deep(.table th:nth-child(1), .table td:nth-child(1)) { /* Name column */
  min-width: 250px;
  width: 25%;
}

:deep(.table th:nth-child(2), .table td:nth-child(2)) { /* Description column */
  min-width: 300px;
  width: 35%;
}

:deep(.table th:nth-child(3), .table td:nth-child(3)) { /* Status column */
  min-width: 100px;
  width: 10%;
}


:deep(.table th:nth-child(5), .table td:nth-child(5)) { /* Actions column */
  min-width: 100px;
  width: 15%;
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
.action-buttons {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
  align-items: center;
}

.action-btn {
  width: 32px;
  height: 32px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-btn i {
  font-size: 0.875rem;
  line-height: 1;
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
  :deep(.table th:nth-child(1), .table td:nth-child(1)) { /* Name column */
    min-width: 200px;
    width: 30%;
  }
  
  :deep(.table th:nth-child(2), .table td:nth-child(2)) { /* Description column */
    min-width: 200px;
    width: 30%;
  }
  
  :deep(.table th:nth-child(3), .table td:nth-child(3)) { /* Status column */
    min-width: 80px;
    width: 10%;
  }
  
  
  :deep(.table th:nth-child(5), .table td:nth-child(5)) { /* Actions column */
    min-width: 80px;
    width: 15%;
  }
}
</style>