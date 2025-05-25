<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Scopes</h1>
      
      <button class="btn btn-primary">
        <i class="bi bi-plus-lg me-2"></i> New Scope
      </button>
    </div>

    <div class="card">
      <div class="card-body">
        <div class="table-responsive">
          <table class="table table-hover align-middle">
            <thead>
              <tr>
                <th>Name</th>
                <th>Description</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="scope in scopes" :key="scope.id">
                <td><code>{{ scope.name }}</code></td>
                <td>{{ scope.description }}</td>
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
import { mockScopes } from '../../services/mockData'

const scopes = ref([])

onMounted(() => {
  scopes.value = mockScopes
})
</script>