<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Resources</h1>
      
      <button class="btn btn-primary">
        <i class="bi bi-plus-lg me-2"></i> New Resource
      </button>
    </div>

    <div class="card">
      <div class="card-body">
        <div class="table-responsive">
          <table class="table table-hover align-middle">
            <thead>
              <tr>
                <th>Name</th>
                <th>Path</th>
                <th>Description</th>
                <th>Required Permissions</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="resource in resources" :key="resource.id">
                <td>{{ resource.name }}</td>
                <td><code>{{ resource.path }}</code></td>
                <td>{{ resource.description }}</td>
                <td>
                  <span 
                    v-for="perm in resource.requiredPermissions" 
                    :key="perm"
                    class="badge bg-primary me-1"
                  >
                    {{ perm }}
                  </span>
                </td>
                <td>
                  <div class="btn-group">
                    <button class="btn btn-sm btn-outline-primary">
                      <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger">
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { mockResources } from '../../services/mockData'

const resources = ref([])

onMounted(() => {
  resources.value = mockResources
})
</script>