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
    @delete="handleDelete"
    @clear-error="clientStore.clearError()"
  >
    <!-- Custom column slots -->
    <template #clientName="{ item }">
      <div class="d-flex align-items-center">
        <div class="avatar-initial me-3">
          <i class="bi bi-box"></i>
        </div>
        <span class="fw-bold">{{ item.clientName }}</span>
      </div>
    </template>

    <template #clientId="{ item }">
      <div class="d-flex align-items-center gap-2">
        <code class="font-monospace text-body-secondary">{{ item.clientId }}</code>
        <button 
          class="btn btn-sm btn-action"
          @click="copyToClipboard(item.clientId)"
          title="Copy Client ID"
        >
          <i class="bi bi-clipboard"></i>
        </button>
      </div>
    </template>

    <template #scopes="{ item }">
      <div class="d-flex flex-wrap gap-1">
        <span v-for="scope in item.scopes" :key="scope" class="badge bg-info-subtle text-info-emphasis border border-info-subtle">
          {{ scope }}
        </span>
      </div>
    </template>

     <template #redirectUris="{ item }">
      <div class="d-flex flex-wrap gap-1">
        <span v-for="uri in item.redirectUris" :key="uri" class="badge bg-secondary-subtle text-secondary-emphasis border border-secondary-subtle">
          {{ uri }}
      </span>
      </div>
    </template>

    <template #actions="{ item }">
      <div class="d-flex gap-2 justify-content-end">
        <router-link 
          :to="`/clients/${item.clientId}/edit`"
          class="btn btn-action btn-edit"
          title="Edit"
          v-if="hasPermission('CLIENT_UPDATE')"
        >
          <i class="bi bi-pencil-fill"></i>
        </router-link>
        <button 
          class="btn btn-action btn-delete"
          @click="confirmDelete(item)"
          title="Delete"
          v-if="hasPermission('CLIENT_DELETE')"
        >
          <i class="bi bi-trash-fill"></i>
        </button>
      </div>
    </template>
  </DataList>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useClientStore } from '@/stores/clients'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'
import DataList from '@/components/layout/DataList.vue'

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
  dataList.value?.showDeleteModal(client)
}

async function handleDelete(client) {
  if (!client) return
  try {
    await clientStore.deleteClient(client.clientId)
    toast.success(`Client "${client.clientName}" deleted successfully.`)
    await loadClients()
  } catch (error) {
    toast.error(error.message || 'Failed to delete client.')
  }
}

function hasPermission(permission) {
  return authStore.can(permission)
  }

async function copyToClipboard(text) {
  try {
    await navigator.clipboard.writeText(text)
    toast.success('Client ID copied to clipboard')
  } catch (err) {
    toast.error('Failed to copy Client ID.')
  }
}

async function loadClients() {
  await clientStore.fetchClients()
}

onMounted(loadClients)
</script>