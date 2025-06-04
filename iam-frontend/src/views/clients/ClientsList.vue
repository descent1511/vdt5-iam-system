<template>
  <DataList
    ref="dataList"
    title="Clients"
    :items="clientStore.clients"
    :columns="columns"
    :can-create="hasPermission('CLIENT_CREATE')"
    create-route="/clients/create"
    create-button-text="New Client"
    search-placeholder="Search clients..."
    :search-fields="['name', 'clientId', 'description']"
    :loading="clientStore.loading"
    :error="clientStore.error"
    @delete="deleteClient"
  >
    <!-- Custom column slots -->
    <template #name="{ item }">
      <div class="d-flex align-items-center">
        <i class="bi bi-box text-primary me-2"></i>
        <span class="fw-medium">{{ item.name }}</span>
      </div>
    </template>

    <template #clientId="{ item }">
      <div class="client-id-container">
        <code class="client-id" :title="item.accessToken || 'No access token'">
          {{ item.accessToken ? truncateToken(item.accessToken) : 'No access token' }}
        </code>
        <button 
          v-if="item.accessToken"
          class="btn btn-sm btn-link copy-btn"
          @click="copyToClipboard(item.accessToken)"
          title="Copy access token"
        >
          <i class="bi bi-clipboard"></i>
        </button>
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

    <template #actions="{ item }">
      <div class="action-btns">
        <router-link 
          v-if="hasPermission('CLIENT_UPDATE')"
          :to="`/clients/${item.id}/edit`" 
          class="btn btn-sm btn-outline-primary"
          title="Edit"
        >
          <i class="bi bi-pencil"></i>
        </router-link>
        <button 
          v-if="hasPermission('CLIENT_DELETE')"
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
import { useClientStore } from '../../stores/clients'
import { useAuthStore } from '../../stores/auth'
import { useToast } from 'vue-toastification'
import DataList from '../../components/layout/DataList.vue'

const clientStore = useClientStore()
const authStore = useAuthStore()
const toast = useToast()
const dataList = ref(null)

const columns = [
  { key: 'name', label: 'Name' },
  { key: 'clientId', label: 'Access Token' },
  { key: 'description', label: 'Description' },
  { key: 'status', label: 'Status' },
  { key: 'actions', label: 'Actions', class: 'text-end' }
]

function confirmDelete(client) {
  clientStore.selectedClient = client
  dataList.value.showDeleteModal()
}

async function deleteClient() {
  if (!clientStore.selectedClient) return

  try {
    await clientStore.deleteClient(clientStore.selectedClient.id)
    await loadClients()
    dataList.value.hideDeleteModal()
  } catch (error) {
    console.error('Failed to delete client:', error)
  }
}

function hasPermission(permission) {
  return authStore.can(permission)
  }

async function copyToClipboard(text) {
  try {
    await navigator.clipboard.writeText(text)
    toast.success('Access token copied to clipboard')
  } catch (error) {
    console.error('Failed to copy:', error)
    toast.error('Failed to copy to clipboard')
  }
}

function truncateToken(token) {
  if (!token) return 'No access token'
  if (token.length <= 20) return token
  return token.substring(0, 10) + '...' + token.substring(token.length - 10)
}

async function loadClients() {
  await clientStore.fetchClients()
}

onMounted(loadClients)
</script>

<style scoped>
/* Table styles */
:deep(.table) {
  margin-bottom: 0;
}

:deep(.table th) {
  font-weight: 500;
  color: #2c3e50;
  border-top: none;
  padding: 1rem;
  background-color: #f8f9fa;
  white-space: nowrap;
}

:deep(.table td) {
  padding: 1rem;
  vertical-align: middle;
  border-bottom: 1px solid #e9ecef;
}

:deep(.table tr:hover) {
  background-color: #f8f9fa;
}

/* Column specific styles */
:deep(.table td:nth-child(1)) { /* Name column */
  min-width: 200px;
}

:deep(.table td:nth-child(2)) { /* Client ID column */
  min-width: 250px;
}

:deep(.table td:nth-child(3)) { /* Description column */
  min-width: 300px;
}

:deep(.table td:nth-child(4)) { /* Status column */
  min-width: 100px;
}

:deep(.table td:nth-child(5)) { /* Actions column */
  min-width: 100px;
  text-align: right;
}

/* Client ID container styles */
.client-id-container {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  max-width: 100%;
}

.client-id {
  font-size: 0.875rem;
  color: #0c5460;
  background-color: #f8f9fa;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: help;
}

.copy-btn {
  padding: 0;
  color: #6c757d;
  transition: all 0.2s ease-in-out;
}

.copy-btn:hover {
  color: #0c5460;
}

/* Badge styles */
.badge {
  font-size: 0.75rem;
  padding: 0.35em 0.65em;
  font-weight: 500;
}

/* Action buttons */
.action-btns {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
  align-items: center;
  height: 32px; /* Đảm bảo cùng chiều cao với các dòng khác */
}

.action-btns .btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 32px;
  width: 32px;
  padding: 0;
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