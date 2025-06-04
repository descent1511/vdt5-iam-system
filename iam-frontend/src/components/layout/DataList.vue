<template>
  <div class="container-fluid">
    <!-- Error Alert -->
    <div v-if="error" class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
      <i class="bi bi-exclamation-triangle-fill me-2"></i>
      {{ error }}
      <button type="button" class="btn-close" @click="clearError"></button>
    </div>

    <!-- Main Content - Only show when no error -->
    <template v-if="!error">
      <!-- Header Section -->
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3 mb-0">{{ title }}</h1>
        <button 
          v-if="canCreate && createRoute"
          class="btn btn-primary"
          @click="$router.push(createRoute)"
        >
          <i class="bi bi-plus-lg me-2"></i>{{ createButtonText }}
        </button>
      </div>

      <!-- Main Content -->
      <div class="card">
        <div class="card-body">
          <!-- Search and Filter Section -->
          <div class="row mb-4">
            <div class="col-md-6">
              <div class="input-group">
                <span class="input-group-text bg-light">
                  <i class="bi bi-search"></i>
                </span>
                <input
                  type="text"
                  class="form-control"
                  :placeholder="searchPlaceholder"
                  v-model="searchQuery"
                >
              </div>
            </div>
            <div class="col-md-3" v-if="filters">
              <select class="form-select" v-model="selectedFilter">
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
            <table class="table table-hover align-middle">
              <thead>
                <tr>
                  <th v-for="column in columns" :key="column.key" :class="column.class">
                    {{ column.label }}
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in filteredItems" :key="item.id">
                  <td v-for="column in columns" :key="column.key" :class="column.class">
                    <slot :name="column.key" :item="item">
                      {{ item[column.key] }}
                    </slot>
                  </td>
                </tr>
                <tr v-if="filteredItems.length === 0">
                  <td :colspan="columns.length" class="text-center py-4">
                    <div class="text-muted">
                      <i class="bi bi-inbox fs-4 d-block mb-2"></i>
                      {{ emptyMessage }}
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </template>

    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Confirm Delete</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            {{ deleteConfirmMessage }}
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
            <button type="button" class="btn btn-danger" @click="$emit('delete')">Delete</button>
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
let deleteModal = null

function clearError() {
  emit('clear-error')
}

const filteredItems = computed(() => {
  let filtered = [...props.items]

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(item => 
      props.searchFields.some(field => 
        String(item[field]).toLowerCase().includes(query)
      )
    )
  }

  if (selectedFilter.value) {
    filtered = filtered.filter(item => item.status === selectedFilter.value)
  }

  return filtered
})

onMounted(() => {
  deleteModal = new Modal(document.getElementById('deleteModal'))
})

function showDeleteModal() {
  deleteModal.show()
}

function hideDeleteModal() {
  deleteModal.hide()
}

defineExpose({
  showDeleteModal,
  hideDeleteModal
})
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