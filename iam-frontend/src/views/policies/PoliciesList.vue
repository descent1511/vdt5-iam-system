<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Policies</h1>
      <router-link to="/policies/create" class="btn btn-primary">
        <i class="bi bi-plus-lg me-2"></i> New Policy
      </router-link>
    </div>

    <!-- Filters -->
    <div class="card mb-4">
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-4">
            <input
              v-model="filters.search"
              type="text"
              class="form-control"
              placeholder="Search policies..."
              @input="debounceSearch"
            />
          </div>
          <div class="col-md-3">
            <select v-model="filters.subjectType" class="form-select" @change="applyFilters">
              <option value="">All Subject Types</option>
              <option value="USER">User</option>
              <option value="ROLE">Role</option>
              <option value="CLIENT">Client</option>
              <option value="SCOPE">Scope</option>
            </select>
          </div>
          <div class="col-md-3">
            <select v-model="filters.effect" class="form-select" @change="applyFilters">
              <option value="">All Effects</option>
              <option value="ALLOW">Allow</option>
              <option value="DENY">Deny</option>
            </select>
          </div>
          <div class="col-md-2">
            <button class="btn btn-outline-secondary w-100" @click="resetFilters">
              Reset
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Policies Table -->
    <div class="card">
      <div class="card-body">
        <div class="table-responsive">
          <table class="table table-hover align-middle">
            <thead>
              <tr>
                <th>Subject</th>
                <th>Resource</th>
                <th>Action</th>
                <th>Effect</th>
                <th>Conditions</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="policy in policies" :key="policy.id">
                <td>
                  <span class="badge bg-secondary">
                    {{ policy.subjectType }}: {{ policy.subjectId }}
                  </span>
                </td>
                <td>{{ policy.resource?.name || 'N/A' }}</td>
                <td>{{ policy.action }}</td>
                <td>
                  <span :class="`badge bg-${policy.effect === 'ALLOW' ? 'success' : 'danger'}`">
                    {{ policy.effect }}
                  </span>
                </td>
                <td>
                  <span v-if="policy.conditionJson" class="badge bg-info">
                    Has Conditions
                  </span>
                  <span v-else class="text-muted">None</span>
                </td>
                <td>
                  <div class="btn-group">
                    <button 
                      class="btn btn-sm btn-outline-primary" 
                      @click="handleEdit(policy)"
                      :disabled="loading"
                    >
                      <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" @click="confirmDelete(policy)">
                      <i class="bi bi-trash"></i>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Confirm Delete</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            Are you sure you want to delete this policy?
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
            <button type="button" class="btn btn-danger" @click="deletePolicy">Delete</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { policyService } from '../../services/policyService'
import { Modal } from 'bootstrap'
import { useToast } from 'vue-toastification'
import { useRouter } from 'vue-router'

const toast = useToast()
const policies = ref([])
const deleteModal = ref(null)
const selectedPolicy = ref(null)
const router = useRouter()
const loading = ref(false)

const filters = ref({
  search: '',
  subjectType: '',
  effect: ''
})

onMounted(async () => {
  deleteModal.value = new Modal(document.getElementById('deleteModal'))
  await loadPolicies()
})

const loadPolicies = async () => {
  try {
    console.log('Loading policies...')
    const response = await policyService.getPolicies()
    console.log('Policies loaded:', response)
    policies.value = response
  } catch (error) {
    console.error('Error loading policies:', error)
    toast.error('Failed to load policies')
  }
}

const confirmDelete = (policy) => {
  selectedPolicy.value = policy
  deleteModal.value.show()
}

const deletePolicy = async () => {
  try {
    await policyService.deletePolicy(selectedPolicy.value.id)
    toast.success('Policy deleted successfully')
    deleteModal.value.hide()
    await loadPolicies()
  } catch (error) {
    toast.error('Failed to delete policy')
  }
}

const debounceSearch = debounce(() => {
  applyFilters()
}, 300)

const applyFilters = () => {
  loadPolicies()
}

const resetFilters = () => {
  filters.value = {
    search: '',
    subjectType: '',
    effect: ''
  }
  loadPolicies()
}

const handleEdit = (policy) => {
  console.log('Edit button clicked for policy:', policy)
  if (!policy || !policy.id) {
    console.error('Invalid policy or missing ID:', policy)
    toast.error('Invalid policy data')
    return
  }
  try {
    console.log('Attempting to navigate to:', `/policies/${policy.id}/edit`)
    router.push(`/policies/${policy.id}/edit`)
  } catch (error) {
    console.error('Navigation error:', error)
    toast.error('Failed to navigate to edit page')
  }
}

function debounce(fn, delay) {
  let timeoutId
  return function (...args) {
    clearTimeout(timeoutId)
    timeoutId = setTimeout(() => fn.apply(this, args), delay)
  }
}
</script>