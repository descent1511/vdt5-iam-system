<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">{{ isEdit ? 'Edit Resource' : 'Create Resource' }}</h1>
      
      <router-link to="/resources" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left me-2"></i> Back to Resources
      </router-link>
    </div>

    <div class="card">
      <div class="card-body">
        <form @submit.prevent="handleSubmit">
          <div class="mb-3">
            <label for="name" class="form-label">Name</label>
            <input
              type="text"
              class="form-control"
              id="name"
              v-model="form.name"
              required
              :disabled="isEdit"
            >
            <div class="form-text">Resource name cannot be changed after creation</div>
          </div>

          <div class="mb-3">
            <label for="path" class="form-label">Path</label>
            <input
              type="text"
              class="form-control"
              id="path"
              v-model="form.path"
              required
              :disabled="isEdit"
            >
            <div class="form-text">Resource path cannot be changed after creation</div>
          </div>

          <div class="mb-3">
            <label for="method" class="form-label">HTTP Method</label>
            <select
              class="form-select"
              id="method"
              v-model="form.method"
              required
              :disabled="isEdit"
            >
              <option value="GET">GET</option>
              <option value="POST">POST</option>
              <option value="PUT">PUT</option>
              <option value="DELETE">DELETE</option>
              <option value="PATCH">PATCH</option>
            </select>
            <div class="form-text">HTTP method cannot be changed after creation</div>
          </div>

          <div class="mb-3">
            <label for="description" class="form-label">Description</label>
            <textarea
              class="form-control"
              id="description"
              v-model="form.description"
              rows="3"
            ></textarea>
          </div>

          <div class="mb-3">
            <label class="form-label">Required Permissions</label>
            <div class="d-flex gap-2 flex-wrap">
              <div v-for="perm in form.requiredPermissions" :key="perm" class="badge bg-primary d-flex align-items-center">
                {{ perm }}
                <button 
                  v-if="!isEdit"
                  type="button" 
                  class="btn-close btn-close-white ms-2" 
                  @click="removePermission(perm)"
                  aria-label="Remove permission"
                ></button>
              </div>
            </div>
            <div v-if="!isEdit" class="input-group mt-2">
              <input
                type="text"
                class="form-control"
                v-model="newPermission"
                placeholder="Add permission"
                @keyup.enter="addPermission"
              >
              <button 
                class="btn btn-outline-primary" 
                type="button"
                @click="addPermission"
              >
                Add
              </button>
            </div>
            <div v-else class="form-text mt-2">
              Required permissions cannot be changed after creation
            </div>
          </div>

          <div class="d-flex justify-content-end gap-2">
            <router-link to="/resources" class="btn btn-outline-secondary">
              Cancel
            </router-link>
            <button 
              type="submit" 
              class="btn btn-primary"
              :disabled="resourceStore.loading"
            >
              <span v-if="resourceStore.loading" class="spinner-border spinner-border-sm me-2" role="status"></span>
              {{ isEdit ? 'Update Resource' : 'Create Resource' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useResourceStore } from '../../stores/resources'
import { useToast } from 'vue-toastification'

const route = useRoute()
const router = useRouter()
const resourceStore = useResourceStore()
const toast = useToast()

const isEdit = computed(() => !!route.params.id)

const form = ref({
  name: '',
  path: '',
  method: 'GET',
  description: '',
  requiredPermissions: []
})

const newPermission = ref('')

// Load resource data if in edit mode
onMounted(async () => {
  if (isEdit.value) {
    try {
      const resource = await resourceStore.fetchResourceById(route.params.id)
      if (resource) {
        form.value = { ...resource }
      }
    } catch (error) {
      toast.error('Failed to load resource')
      router.push('/resources')
    }
  }
})

function addPermission() {
  if (newPermission.value && !form.value.requiredPermissions.includes(newPermission.value)) {
    form.value.requiredPermissions.push(newPermission.value)
    newPermission.value = ''
  }
}

function removePermission(permission) {
  form.value.requiredPermissions = form.value.requiredPermissions.filter(p => p !== permission)
}

async function handleSubmit() {
  try {
    if (isEdit.value) {
      await resourceStore.updateResource(route.params.id, form.value)
      toast.success('Resource updated successfully')
    } else {
      await resourceStore.createResource(form.value)
      toast.success('Resource created successfully')
    }
    router.push('/resources')
  } catch (error) {
    toast.error(isEdit.value ? 'Failed to update resource' : 'Failed to create resource')
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

.form-control:disabled,
.form-select:disabled {
  background-color: #f8f9fa;
  cursor: not-allowed;
}

.badge {
  padding: 0.5em 0.75em;
  font-weight: 500;
  border-radius: 50rem;
}

.btn-close {
  font-size: 0.75rem;
}

.input-group .btn {
  padding-left: 1rem;
  padding-right: 1rem;
}
</style> 