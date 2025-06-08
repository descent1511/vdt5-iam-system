<template>
  <DataList
    ref="dataList"
    title="Clients"
    :items="clientStore.clients"
    :columns="columns"
    :can-create="hasPermission('CLIENT_CREATE')"
    create-route="/clients/create"
    create-button-text="New Client"
    search-placeholder="Search by name or client ID..."
    :search-fields="['clientName', 'clientId']"
    :loading="clientStore.loading"
    :error="clientStore.error"
    @delete="deleteClient"
  >
    <!-- Custom column slots -->
    <template #clientName="{ item }">
      <div class="d-flex align-items-center">
        <div class="icon-circle bg-light-primary text-primary me-3">
          <i class="bi bi-box"></i>
        </div>
        <span class="fw-bold">{{ item.clientName }}</span>
      </div>
    </template>

    <template #clientId="{ item }">
      <div class="client-id-container">
        <code class="font-monospace">{{ item.clientId }}</code>
        <button
          class="btn btn-sm btn-icon"
          @click="copyToClipboard(item.clientId)"
          title="Copy Client ID"
        >
          <i class="bi bi-clipboard"></i>
        </button>
      </div>
    </template>

    <template #scopes="{ item }">
      <div class="tag-container">
        <span v-for="scope in item.scopes" :key="scope" class="badge bg-light-info text-info-dark">
          {{ scope }}
        </span>
      </div>
    </template>

     <template #redirectUris="{ item }">
      <div class="tag-container">
        <span v-for="uri in item.redirectUris" :key="uri" class="badge bg-light-secondary text-secondary-dark">
          {{ uri }}
        </span>
      </div>
    </template>

    <template #actions="{ item }">
      <div class="action-btns">
        <router-link
          :to="`/clients/${item.clientId}/edit`"
          class="btn btn-sm btn-outline-primary"
          title="Edit"
        >
          <i class="bi bi-pencil-fill"></i>
        </router-link>
        <button
          class="btn btn-sm btn-outline-danger"
          @click="confirmDelete(item)"
          title="Delete"
        >
          <i class="bi bi-trash-fill"></i>
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
  { key: 'clientName', label: 'Name', class: 'w-25' },
  { key: 'clientId', label: 'Client ID', class: 'w-25' },
  { key: 'scopes', label: 'Scopes' },
  { key: 'redirectUris', label: 'Redirect URIs' },
  { key: 'actions', label: 'Actions', class: 'text-end' }
]

function confirmDelete(client) {
  clientStore.selectedClient = client
  dataList.value.showDeleteModal()
}

async function deleteClient() {
  if (!clientStore.selectedClient) return
  try {
    await clientStore.deleteClient(clientStore.selectedClient.clientId)
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
    toast.success('Client ID copied to clipboard')
  } catch (error) {
    console.error('Failed to copy:', error)
    toast.error('Failed to copy to clipboard')
  }
}

async function loadClients() {
  await clientStore.fetchClients()
}

onMounted(loadClients)
</script>

<style scoped>
.icon-circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
}

.client-id-container {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-family: 'Courier New', Courier, monospace;
}

.client-id-container code {
  background-color: #f1f3f5;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.tag-container {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.badge {
  font-weight: 500;
  padding: 0.4em 0.75em;
  font-size: 0.8rem;
}

/* Custom light badge colors */
.bg-light-primary { background-color: #cfe2ff; }
.text-primary { color: #0d6efd !important; }

.bg-light-info { background-color: #cff4fc; }
.text-info-dark { color: #055160 !important; }

.bg-light-secondary { background-color: #e2e3e5; }
.text-secondary-dark { color: #41464b !important; }

.action-btns {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
}

.action-btns .btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style> 