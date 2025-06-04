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
    @delete="deleteProduct"
    @clear-error="clearError"
  >
    <!-- Custom column slots -->
    <template #price="{ item }">
      {{ formatPrice(item.price) }}
    </template>

    <template #status="{ item }">
      <span 
        class="badge"
        :class="item.status === 'ACTIVE' ? 'bg-success' : 'bg-danger'"
      >
        {{ item.status }}
      </span>
    </template>

    <template #actions="{ item }">
      <div class="d-flex justify-content-end gap-2">
        <button 
          v-if="authStore.can('PRODUCT_UPDATE')"
          class="btn btn-sm btn-outline-primary"
          @click="editProduct(item.id)"
          title="Edit"
        >
          <i class="bi bi-pencil"></i>
        </button>
        <button 
          v-if="authStore.can('PRODUCT_DELETE')"
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
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { useProductsStore } from '../../stores/products'
import DataList from '../../components/layout/DataList.vue'

const router = useRouter()
const authStore = useAuthStore()
const productsStore = useProductsStore()
const dataList = ref(null)

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
  productsStore.selectedProduct = product
  dataList.value.showDeleteModal()
}

async function deleteProduct() {
  if (!productsStore.selectedProduct) return

  try {
    await productsStore.deleteProduct(productsStore.selectedProduct.id)
    productsStore.clearSelectedProduct()
    dataList.value.hideDeleteModal()
  } catch (error) {
    console.error('Error deleting product:', error)
  }
}

function clearError() {
  productsStore.clearError()
}
</script>

<style scoped>
.table th {
  font-weight: 600;
  background-color: var(--bs-light);
  white-space: nowrap;
}

.table td {
  vertical-align: middle;
}

.badge {
  font-weight: 500;
  padding: 0.5em 0.75em;
}

.input-group-text {
  background-color: var(--bs-light);
}

.btn-group {
  gap: 0.5rem;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .table-responsive {
    margin: 0 -1rem;
  }
  
  .table th, .table td {
    padding: 0.75rem;
  }
}
</style> 