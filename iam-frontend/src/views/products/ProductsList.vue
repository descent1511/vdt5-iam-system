<template>
  <DataList
    ref="dataList"
    title="Products"
    :items="productsStore.getProducts"
    :loading="productsStore.getLoading"
    :error="productsStore.getError"
    :columns="columns"
    :can-create="authStore.can('PRODUCT_CREATE')"
    create-route="/products/create"
    create-button-text="Add Product"
    search-placeholder="Search products..."
    :search-fields="['name', 'description']"
    :filters="statusFilters"
    filter-placeholder="All Status"
    @delete="handleDelete"
    @clear-error="productsStore.clearError()"
  >
    <!-- Custom column slots -->
    <template #price="{ item }">
      {{ formatPrice(item.price) }}
    </template>

    <template #status="{ item }">
      <span 
        class="badge"
        :class="{
          'bg-success-subtle text-success-emphasis border border-success-subtle': item.status === 'ACTIVE',
          'bg-danger-subtle text-danger-emphasis border border-danger-subtle': item.status === 'INACTIVE'
        }"
      >
        {{ item.status }}
      </span>
    </template>

    <template #actions="{ item }">
      <div class="d-flex justify-content-end gap-2">
        <button 
          v-if="authStore.can('PRODUCT_UPDATE')"
          class="btn btn-action btn-edit"
          @click="editProduct(item.id)"
          title="Edit"
        >
          <i class="bi bi-pencil-fill"></i>
        </button>
        <button 
          v-if="authStore.can('PRODUCT_DELETE')"
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
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useProductsStore } from '@/stores/products'
import { useToast } from 'vue-toastification'
import DataList from '@/components/layout/DataList.vue'

const router = useRouter()
const authStore = useAuthStore()
const productsStore = useProductsStore()
const dataList = ref(null)
const toast = useToast()

const columns = [
  { key: 'name', label: 'Name' },
  { key: 'description', label: 'Description' },
  { key: 'price', label: 'Price' },
  { key: 'stock', label: 'Stock' },
  { key: 'status', label: 'Status' },
  { key: 'actions', label: 'Actions', class: 'text-end' }
]

const statusFilters = [
  { value: 'ACTIVE', label: 'Active' },
  { value: 'INACTIVE', label: 'Inactive' }
]

onMounted(async () => {
  await productsStore.fetchProducts()
})

function formatPrice(price) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(price)
}

function editProduct(id) {
  router.push(`/products/${id}/edit`)
}

function confirmDelete(product) {
  dataList.value?.showDeleteModal(product)
}

async function handleDelete(product) {
  if (!product) return

  try {
    await productsStore.deleteProduct(product.id)
    toast.success(`Product "${product.name}" deleted successfully.`)
    await productsStore.fetchProducts()
  } catch (error) {
    toast.error(error.message || 'Failed to delete product.')
  }
}
</script>