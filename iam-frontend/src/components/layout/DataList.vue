<template>
  <div class="container-fluid">
    <!-- Error Alert -->
    <div v-if="error" class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
      <i class="bi bi-exclamation-triangle-fill me-2"></i>
      {{ error }}
      <button type="button" class="btn-close" @click="clearError" aria-label="Close"></button>
    </div>

    <!-- Main Content -->
    <template v-if="!error">
      <!-- Header Section -->
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3 mb-0 text-body-emphasis">{{ title }}</h1>
        <button 
          v-if="canCreate && createRoute"
          class="btn btn-primary"
          @click="$router.push(createRoute)"
        >
          <i class="bi bi-plus-lg me-2"></i>{{ createButtonText }}
        </button>
      </div>

      <!-- Table Container -->
      <div class="table-container">
        <!-- Table Header: Search and Filters -->
        <div class="table-header">
          <div class="search-box">
                  <i class="bi bi-search"></i>
                <input
                  type="text"
                  class="form-control"
                  :placeholder="searchPlaceholder"
                  v-model="searchQuery"
                >
              </div>
          <div class="filter-box" v-if="filters && filters.length > 0">
              <select class="form-select filter-select-arrow" v-model="selectedFilter">
                <option value="">{{ filterPlaceholder }}</option>
                <option 
                  v-for="filter in filters" 
                  :key="filter.value" 
                  :value="filter.value"
                >
                  {{ filter.label }}
                </option>
              </select>
            </div>
          </div>

          <!-- Table Section -->
          <div class="table-responsive">
          <table class="table align-middle">
              <thead>
                <tr>
                  <th v-for="column in columns" :key="column.key" :class="column.class">
                    {{ column.label }}
                  </th>
                </tr>
              </thead>
              <tbody>
              <template v-if="filteredItems.length > 0">
                <tr v-for="item in filteredItems" :key="item.id">
                  <td v-for="column in columns" :key="column.key" :class="column.class">
                    <slot :name="column.key" :item="item">
                      {{ item[column.key] }}
                    </slot>
                  </td>
                </tr>
              </template>
              <tr v-else>
                <td :colspan="columns.length" class="text-center py-5">
                  <div class="text-secondary">
                    <i class="bi bi-inbox fs-2 d-block mb-3"></i>
                    <h5 class="mb-0">{{ emptyMessage }}</h5>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
        </div>
      </div>
    </template>

    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="deleteModalLabel">Confirm Delete</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            {{ deleteConfirmMessage }}
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
            <button type="button" class="btn btn-danger" @click="confirmDelete">Delete</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Modal } from 'bootstrap'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  items: {
    type: Array,
    required: true
  },
  columns: {
    type: Array,
    required: true
  },
  canCreate: {
    type: Boolean,
    default: false
  },
  createRoute: {
    type: String,
    default: ''
  },
  createButtonText: {
    type: String,
    default: 'Add New'
  },
  searchPlaceholder: {
    type: String,
    default: 'Search...'
  },
  emptyMessage: {
    type: String,
    default: 'No items found'
  },
  deleteConfirmMessage: {
    type: String,
    default: 'Are you sure you want to delete this item?'
  },
  filters: {
    type: Array,
    default: () => []
  },
  filterPlaceholder: {
    type: String,
    default: 'All'
  },
  searchFields: {
    type: Array,
    default: () => []
  },
  error: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['delete', 'clear-error'])

const searchQuery = ref('')
const selectedFilter = ref('')
let deleteModalInstance = null
const itemToDelete = ref(null)

function clearError() {
  emit('clear-error')
}

const filteredItems = computed(() => {
  let filtered = [...props.items]

  // Search filter
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    const fieldsToSearch = props.searchFields.length > 0 ? props.searchFields : Object.keys(filtered[0] || {})
    
    filtered = filtered.filter(item => 
      fieldsToSearch.some(field => 
        String(item[field]).toLowerCase().includes(query)
      )
    )
  }

  // Selection filter
  if (selectedFilter.value) {
    // Note: This assumes a 'status' field for filtering. 
    // You can make this more generic by passing a 'filterField' prop.
    filtered = filtered.filter(item => item.status === selectedFilter.value)
  }

  return filtered
})

function confirmDelete() {
  if (itemToDelete.value) {
    emit('delete', itemToDelete.value)
    hideDeleteModal()
  }
}

onMounted(() => {
  const modalEl = document.getElementById('deleteModal')
  if (modalEl) {
    deleteModalInstance = new Modal(modalEl)
  }
})

function showDeleteModal(item) {
  itemToDelete.value = item
  if (deleteModalInstance) {
    deleteModalInstance.show()
  }
}

function hideDeleteModal() {
  itemToDelete.value = null
  if (deleteModalInstance) {
    deleteModalInstance.hide()
  }
}

defineExpose({
  showDeleteModal,
  hideDeleteModal
})
</script>

<style scoped>
.table th {
  font-weight: 600;
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

.filter-select-arrow {
  background-size: 0.4rem;
  background-position: right 0.6rem center;
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