<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Policies</h1>
      
      <button class="btn btn-primary">
        <i class="bi bi-plus-lg me-2"></i> New Policy
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
                <th>Effect</th>
                <th>Subjects</th>
                <th>Resources</th>
                <th>Actions</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="policy in policies" :key="policy.id">
                <td>{{ policy.name }}</td>
                <td>{{ policy.description }}</td>
                <td>
                  <span :class="`badge bg-${policy.effect === 'allow' ? 'success' : 'danger'}`">
                    {{ policy.effect }}
                  </span>
                </td>
                <td>
                  <span 
                    v-for="subject in policy.subjects" 
                    :key="subject"
                    class="badge bg-secondary me-1"
                  >
                    {{ subject }}
                  </span>
                </td>
                <td>
                  <span 
                    v-for="resource in policy.resources" 
                    :key="resource"
                    class="badge bg-info me-1"
                  >
                    {{ resource }}
                  </span>
                </td>
                <td>
                  <span 
                    v-for="action in policy.actions" 
                    :key="action"
                    class="badge bg-primary me-1"
                  >
                    {{ action }}
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
import { mockPolicies } from '../../services/mockData'

const policies = ref([])

onMounted(() => {
  policies.value = mockPolicies
})
</script>