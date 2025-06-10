<template>
    <DataList
      ref="dataList"
      title="Services"
      :items="serviceStore.services"
      :columns="columns"
    :loading="serviceStore.loading"
    :error="serviceStore.error"
      :can-create="authStore.can('SERVICE_CREATE')"
      create-route="/services/create"
      create-button-text="New Service"
      search-placeholder="Search by name, description, or URL..."
      :search-fields="['name', 'description', 'url']"
      empty-message="No services registered yet. Click 'New Service' to add one."
    @clear-error="serviceStore.clearError()"
      @delete="handleDelete"
    >
      <template #url="{ item }">
        <a :href="item.url" target="_blank" rel="noopener noreferrer">{{ item.url }}</a>
      </template>
      <template #actions="{ item }">
      <div class="d-flex justify-content-end gap-2">
        <router-link
          v-if="authStore.can('SERVICE_UPDATE')"
          :to="`/services/${item.id}/edit`"
          class="btn btn-action btn-edit"
          title="Edit"
        >
          <i class="bi bi-pencil-fill"></i>
        </router-link>
        <button
          v-if="authStore.can('SERVICE_DELETE')"
          class="btn btn-action btn-delete"
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
import { useServiceRegistryStore } from '@/stores/serviceRegistry'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'
import DataList from '@/components/layout/DataList.vue'

const serviceStore = useServiceRegistryStore()
const authStore = useAuthStore()
const toast = useToast()
const dataList = ref(null)

const columns = ref([
  { key: 'name', label: 'Name' },
  { key: 'description', label: 'Description' },
  { key: 'url', label: 'URL' },
  { key: 'actions', label: 'Actions', class: 'text-end' },
])

onMounted(async () => {
  await loadServices()
})

async function loadServices() {
  try {
    await serviceStore.fetchServices()
  } catch (error) {
    toast.error(error.message || 'Failed to load services.')
  }
}

function confirmDelete(item) {
  dataList.value?.showDeleteModal(item)
}

async function handleDelete(item) {
  if (!item) return
  try {
    await serviceStore.deleteService(item.id)
    toast.success(`Service "${item.name}" deleted successfully.`)
    await loadServices()
  } catch (error) {
    toast.error(error.message || 'Failed to delete service.')
  }
}
</script>