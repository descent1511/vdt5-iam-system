<template>
  <div>
    <div v-if="serviceStore.loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-2">Loading services...</p>
    </div>
    <DataList
      v-else
      ref="dataList"
      title="Services"
      :items="serviceStore.services"
      :columns="columns"
      :can-create="authStore.can('SERVICE_CREATE')"
      create-route="/services/create"
      create-button-text="New Service"
      search-placeholder="Search by name, description, or URL..."
      :search-fields="['name', 'description', 'url']"
      empty-message="No services registered yet. Click 'New Service' to add one."
      :error="serviceStore.error"
      @clear-error="serviceStore.error = null"
      @delete="handleDelete"
    >
      <template #url="{ item }">
        <a :href="item.url" target="_blank" rel="noopener noreferrer">{{ item.url }}</a>
      </template>
      <template #actions="{ item }">
        <router-link
          v-if="authStore.can('SERVICE_UPDATE')"
          :to="`/services/${item.id}/edit`"
          class="btn btn-sm btn-outline-primary me-2"
        >
          <i class="bi bi-pencil"></i>
        </router-link>
        <button
          v-if="authStore.can('SERVICE_DELETE')"
          class="btn btn-sm btn-outline-danger"
          @click="confirmDelete(item)"
        >
          <i class="bi bi-trash"></i>
        </button>
      </template>
    </DataList>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useServiceRegistryStore } from '../../stores/serviceRegistry'
import { useAuthStore } from '../../stores/auth'
import { useToast } from 'vue-toastification'
import DataList from '../../components/layout/DataList.vue'

const serviceStore = useServiceRegistryStore()
const authStore = useAuthStore()
const toast = useToast()
const dataList = ref(null)
const itemToDelete = ref(null)

const columns = ref([
  { key: 'id', label: 'ID' },
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
    console.error('Failed to load services:', error)
  }
}

function confirmDelete(item) {
  itemToDelete.value = item
  dataList.value.showDeleteModal()
}

async function handleDelete() {
  if (itemToDelete.value) {
    await serviceStore.deleteService(itemToDelete.value.id)
    itemToDelete.value = null
    dataList.value.hideDeleteModal()
  }
}
</script>

<style scoped>
/* Scoped styles can be added here if needed */
</style> 