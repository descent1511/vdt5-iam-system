<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Create Client</h1>
      
      <router-link to="/clients" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left me-2"></i> Back to Clients
      </router-link>
    </div>

    <div class="card">
      <div class="card-body">
        <form @submit.prevent="handleSubmit">
          <div class="row g-3">
            <!-- Basic Information -->
            <div class="col-12 mb-3">
              <h5 class="border-bottom pb-2">Basic Information</h5>
            </div>
            
            <div class="col-md-6">
              <div class="form-group">
                <label for="name" class="form-label">Client Name*</label>
                <input
                  type="text"
                  class="form-control"
                  id="name"
                  v-model="form.name"
                  required
                  placeholder="Enter client name"
                >
                <div v-if="errors.name" class="text-danger small mt-1">
                  {{ errors.name }}
                </div>
              </div>
            </div>
            
            <div class="col-12">
              <div class="form-group">
                <label for="description" class="form-label">Description*</label>
                <textarea
                  class="form-control"
                  id="description"
                  v-model="form.description"
                  rows="3"
                  required
                  placeholder="Describe the purpose of this client"
                ></textarea>
                <div v-if="errors.description" class="text-danger small mt-1">
                  {{ errors.description }}
                </div>
              </div>
            </div>

            <!-- Scope Selection -->
            <div class="col-12">
              <div class="form-group">
                <label class="form-label">Scopes*</label>
                <div class="table-responsive">
                  <table class="table table-hover">
                    <thead>
                      <tr>
                        <th>Scope Name</th>
                        <th>Description</th>
                        <th>Select</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="scope in availableScopes" :key="scope.id">
                        <td>{{ scope.name }}</td>
                        <td>{{ scope.description }}</td>
                        <td>
                          <div class="form-check">
                            <input
                              class="form-check-input"
                              type="checkbox"
                              :id="'scope-' + scope.id"
                              :value="scope.id"
                              v-model="form.scopes"
                            >
                          </div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <div v-if="errors.scopes" class="text-danger small mt-1">
                  {{ errors.scopes }}
                </div>
              </div>
            </div>

            <div class="col-12 mt-4 d-flex justify-content-end gap-2">
              <router-link to="/clients" class="btn btn-outline-secondary">
                Cancel
              </router-link>
              <button 
                type="submit" 
                class="btn btn-primary"
                :disabled="clientStore.loading"
              >
                <span v-if="clientStore.loading" class="spinner-border spinner-border-sm me-2" role="status"></span>
                Create Client
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useClientStore } from '../../stores/clients'
import { useScopeStore } from '../../stores/scopes'
import { useToast } from 'vue-toastification'

const router = useRouter()
const clientStore = useClientStore()
const scopeStore = useScopeStore()
const toast = useToast()

const form = reactive({
  name: '',
  description: '',
  scopes: []
})

const errors = reactive({
  name: '',
  description: '',
  scopes: ''
})

const availableScopes = ref([])

onMounted(async () => {
  try {
    await scopeStore.fetchScopes()
    availableScopes.value = scopeStore.scopes

  } catch (error) {
    console.error('Error loading scopes:', error)
    toast.error('Failed to load scopes')
  }
})

function validateForm() {
  let valid = true
  
  // Reset errors
  Object.keys(errors).forEach(key => {
    errors[key] = ''
  })
  
  // Validate name
  if (!form.name.trim()) {
    errors.name = 'Client name is required'
    valid = false
  }
  
  // Validate description
  if (!form.description.trim()) {
    errors.description = 'Description is required'
    valid = false
  }

  // Validate scopes
  if (form.scopes.length === 0) {
    errors.scopes = 'At least one scope must be selected'
    valid = false
  }
  
  return valid
}

async function handleSubmit() {
  if (!validateForm()) return
  try {
    await clientStore.createClient(form)
    toast.success('Client created successfully')
    router.push('/clients')
  } catch (error) {
    console.error('Error creating client:', error)
    toast.error('Failed to create client')
  }
}
</script>

<style scoped>
.card {
  border: none;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.08);
  border-radius: 12px;
}

.form-label {
  font-weight: 500;
  color: #2c3e50;
}

.table {
  margin-bottom: 0;
}

.table th {
  font-weight: 500;
  color: #2c3e50;
  border-top: none;
  padding: 1rem;
  background-color: #f8f9fa;
}

.table td {
  padding: 1rem;
  vertical-align: middle;
}

.form-check-input {
  width: 1.2rem;
  height: 1.2rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.form-check-input:checked {
  background-color: #0d6efd;
  border-color: #0d6efd;
}
</style> 