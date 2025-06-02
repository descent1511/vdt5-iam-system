<template>
  <div>
    <!-- Breadcrumb -->
    <nav aria-label="breadcrumb" class="mb-4">
      <ol class="breadcrumb">
        <li class="breadcrumb-item">
          <router-link to="/policies">Policies</router-link>
        </li>
        <li class="breadcrumb-item active" aria-current="page">Edit Policy</li>
      </ol>
    </nav>

    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Edit Policy</h1>
      <div>
        <button type="button" class="btn btn-outline-secondary me-2" @click="confirmDiscard">
          <i class="bi bi-x-lg me-2"></i>Discard Changes
        </button>
        <button type="button" class="btn btn-primary" @click="savePolicy" :disabled="loading || !isFormValid">
          <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status"></span>
          <i v-else class="bi bi-check-lg me-2"></i>Save Changes
        </button>
      </div>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-3 text-muted">Loading policy details...</p>
    </div>

    <div v-else class="card">
      <div class="card-body">
        <form @submit.prevent="savePolicy" class="needs-validation" novalidate>
          <div class="row g-4">
            <!-- Left Column -->
            <div class="col-md-6">
              <div class="card h-100">
                <div class="card-header bg-light">
                  <h5 class="card-title mb-0">Basic Information</h5>
                </div>
                <div class="card-body">
                  <div class="mb-3">
                    <label class="form-label required">Subject Type</label>
                    <select 
                      v-model="form.subjectType" 
                      class="form-select" 
                      :class="{ 'is-invalid': errors.subjectType }"
                      required
                      @change="validateField('subjectType')"
                    >
                      <option value="">Select a subject type</option>
                      <option value="USER">User</option>
                      <option value="ROLE">Role</option>
                      <option value="CLIENT">Client</option>
                      <option value="SCOPE">Scope</option>
                    </select>
                    <div class="invalid-feedback">{{ errors.subjectType }}</div>
                  </div>

                  <div class="mb-3">
                    <label class="form-label required">Subject ID</label>
                    <input 
                      v-model="form.subjectId" 
                      type="number" 
                      class="form-control" 
                      :class="{ 'is-invalid': errors.subjectId }"
                      required
                      @input="validateField('subjectId')"
                    />
                    <div class="invalid-feedback">{{ errors.subjectId }}</div>
                  </div>

                  <div class="mb-3">
                    <label class="form-label required">Resource</label>
                    <select 
                      v-model="form.resourceId" 
                      class="form-select" 
                      :class="{ 'is-invalid': errors.resourceId }"
                      required
                      @change="validateField('resourceId')"
                    >
                      <option value="">Select a resource</option>
                      <option v-for="resource in resources" :key="resource.id" :value="resource.id">
                        {{ resource.name }}
                      </option>
                    </select>
                    <div class="invalid-feedback">{{ errors.resourceId }}</div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Right Column -->
            <div class="col-md-6">
              <div class="card h-100">
                <div class="card-header bg-light">
                  <h5 class="card-title mb-0">Policy Details</h5>
                </div>
                <div class="card-body">
                  <div class="mb-3">
                    <label class="form-label required">Action</label>
                    <input 
                      v-model="form.action" 
                      type="text" 
                      class="form-control" 
                      :class="{ 'is-invalid': errors.action }"
                      required
                      @input="validateField('action')"
                    />
                    <div class="invalid-feedback">{{ errors.action }}</div>
                  </div>

                  <div class="mb-3">
                    <label class="form-label required">Effect</label>
                    <select 
                      v-model="form.effect" 
                      class="form-select" 
                      :class="{ 'is-invalid': errors.effect }"
                      required
                      @change="validateField('effect')"
                    >
                      <option value="">Select an effect</option>
                      <option value="ALLOW">Allow</option>
                      <option value="DENY">Deny</option>
                    </select>
                    <div class="invalid-feedback">{{ errors.effect }}</div>
                  </div>

                  <div class="mb-3">
                    <label class="form-label">Conditions</label>
                    <div class="card">
                      <div class="card-body">
                        <div class="mb-3">
                          <label class="form-label">Condition Type</label>
                          <select 
                            v-model="condition.type" 
                            class="form-select" 
                            @change="updateConditionJson"
                          >
                            <option value="">No Condition</option>
                            <option value="time">Time</option>
                            <option value="date">Date</option>
                            <option value="ip">IP Address</option>
                            <option value="role">Role</option>
                            <option value="organization">Organization</option>
                          </select>
                        </div>

                        <template v-if="condition.type">
                          <div class="mb-3">
                            <label class="form-label">Operator</label>
                            <select 
                              v-model="condition.operator" 
                              class="form-select" 
                              @change="updateConditionJson"
                            >
                              <option v-if="['time', 'date'].includes(condition.type)" value="between">Between</option>
                              <option v-if="['time', 'date'].includes(condition.type)" value="before">Before</option>
                              <option v-if="['time', 'date'].includes(condition.type)" value="after">After</option>
                              <option v-if="condition.type === 'ip'" value="in">In Range</option>
                              <option v-if="condition.type === 'ip'" value="not_in">Not In Range</option>
                              <option v-if="condition.type === 'role'" value="has">Has Role</option>
                              <option v-if="condition.type === 'role'" value="has_any">Has Any Role</option>
                              <option v-if="condition.type === 'role'" value="has_all">Has All Roles</option>
                              <option v-if="condition.type === 'organization'" value="is">Is Organization</option>
                              <option v-if="condition.type === 'organization'" value="in">In Organizations</option>
                            </select>
                          </div>

                          <!-- Time/Date Between -->
                          <div class="mb-3" v-if="['time', 'date'].includes(condition.type) && condition.operator === 'between'">
                            <div class="row g-3">
                              <div class="col-6">
                                <label class="form-label">Start</label>
                                <input 
                                  v-model="condition.start" 
                                  :type="condition.type === 'time' ? 'time' : 'date'" 
                                  class="form-control" 
                                  @change="updateConditionJson" 
                                />
                              </div>
                              <div class="col-6">
                                <label class="form-label">End</label>
                                <input 
                                  v-model="condition.end" 
                                  :type="condition.type === 'time' ? 'time' : 'date'" 
                                  class="form-control" 
                                  @change="updateConditionJson" 
                                />
                              </div>
                            </div>
                          </div>

                          <!-- Time/Date Before/After -->
                          <div class="mb-3" v-if="['time', 'date'].includes(condition.type) && ['before', 'after'].includes(condition.operator)">
                            <label class="form-label">Value</label>
                            <input 
                              v-model="condition.value" 
                              :type="condition.type === 'time' ? 'time' : 'date'" 
                              class="form-control" 
                              @change="updateConditionJson" 
                            />
                          </div>

                          <!-- IP Ranges -->
                          <div class="mb-3" v-if="condition.type === 'ip'">
                            <label class="form-label">IP Ranges (CIDR)</label>
                            <div v-for="(ip, index) in condition.ips" :key="index" class="input-group mb-2">
                              <input 
                                v-model="condition.ips[index]" 
                                type="text" 
                                class="form-control" 
                                placeholder="192.168.1.0/24" 
                                @change="updateConditionJson" 
                              />
                              <button 
                                type="button" 
                                class="btn btn-outline-danger" 
                                @click="removeIp(index)"
                                :disabled="condition.ips.length === 1"
                              >
                                <i class="bi bi-trash"></i>
                              </button>
                            </div>
                            <button type="button" class="btn btn-outline-primary btn-sm" @click="addIp">
                              <i class="bi bi-plus-lg me-2"></i>Add IP Range
                            </button>
                          </div>

                          <!-- Roles -->
                          <div class="mb-3" v-if="condition.type === 'role'">
                            <label class="form-label">Roles</label>
                            <div v-for="(role, index) in condition.roles" :key="index" class="input-group mb-2">
                              <input 
                                v-model="condition.roles[index]" 
                                type="text" 
                                class="form-control" 
                                @change="updateConditionJson" 
                              />
                              <button 
                                type="button" 
                                class="btn btn-outline-danger" 
                                @click="removeRole(index)"
                                :disabled="condition.roles.length === 1"
                              >
                                <i class="bi bi-trash"></i>
                              </button>
                            </div>
                            <button type="button" class="btn btn-outline-primary btn-sm" @click="addRole">
                              <i class="bi bi-plus-lg me-2"></i>Add Role
                            </button>
                          </div>

                          <!-- Organization IDs -->
                          <div class="mb-3" v-if="condition.type === 'organization'">
                            <label class="form-label">Organization IDs</label>
                            <div v-for="(orgId, index) in condition.orgIds" :key="index" class="input-group mb-2">
                              <input 
                                v-model="condition.orgIds[index]" 
                                type="number" 
                                class="form-control" 
                                @change="updateConditionJson" 
                              />
                              <button 
                                type="button" 
                                class="btn btn-outline-danger" 
                                @click="removeOrgId(index)"
                                :disabled="condition.orgIds.length === 1"
                              >
                                <i class="bi bi-trash"></i>
                              </button>
                            </div>
                            <button type="button" class="btn btn-outline-primary btn-sm" @click="addOrgId">
                              <i class="bi bi-plus-lg me-2"></i>Add Organization ID
                            </button>
                          </div>
                        </template>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </form>
      </div>
    </div>

    <!-- Discard Changes Modal -->
    <div class="modal fade" id="discardModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Discard Changes?</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            Are you sure you want to discard your changes? This action cannot be undone.
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
            <button type="button" class="btn btn-danger" @click="discardChanges">Discard Changes</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { usePolicyStore } from '../../stores/policies'
import { resourceService } from '../../services/resourceService'
import { Modal } from 'bootstrap'

const router = useRouter()
const route = useRoute()
const policyStore = usePolicyStore()
const loading = ref(true)
const resources = ref([])
const discardModal = ref(null)

const form = ref({
  subjectType: '',
  subjectId: null,
  resourceId: null,
  action: '',
  effect: '',
  conditionJson: ''
})

const errors = ref({
  subjectType: '',
  subjectId: '',
  resourceId: '',
  action: '',
  effect: ''
})

const condition = ref({
  type: '',
  operator: '',
  start: '',
  end: '',
  value: '',
  ips: [''],
  roles: [''],
  orgIds: [null]
})

const isFormValid = computed(() => {
  return form.value.subjectType &&
         form.value.subjectId &&
         form.value.resourceId &&
         form.value.action &&
         form.value.effect
})

onMounted(async () => {
  discardModal.value = new Modal(document.getElementById('discardModal'))
  await Promise.all([loadResources(), loadPolicy()])
  loading.value = false
})

const loadResources = async () => {
  try {
    resources.value = await resourceService.getResources()
  } catch (error) {
    console.error('Failed to load resources')
  }
}

const loadPolicy = async () => {
  try {
    const policy = await policyStore.fetchPolicyById(route.params.id)
    form.value = {
      subjectType: policy.subjectType,
      subjectId: policy.subjectId,
      resourceId: policy.resource.id,
      action: policy.action,
      effect: policy.effect,
      conditionJson: policy.conditionJson
    }

    if (policy.conditionJson) {
      try {
        const conditionData = JSON.parse(policy.conditionJson)
        condition.value = {
          type: conditionData.type || '',
          operator: conditionData.operator || '',
          start: conditionData.value?.start || '',
          end: conditionData.value?.end || '',
          value: typeof conditionData.value === 'string' ? conditionData.value : '',
          ips: Array.isArray(conditionData.value) ? conditionData.value : [''],
          roles: Array.isArray(conditionData.value) ? conditionData.value : [''],
          orgIds: Array.isArray(conditionData.value) ? conditionData.value : [null]
        }
      } catch (e) {
        console.error('Error parsing condition JSON:', e)
      }
    }
  } catch (error) {
    router.push('/policies')
  }
}

const validateField = (field) => {
  errors.value[field] = ''
  
  switch (field) {
    case 'subjectType':
      if (!form.value.subjectType) {
        errors.value.subjectType = 'Subject type is required'
      }
      break
    case 'subjectId':
      if (!form.value.subjectId) {
        errors.value.subjectId = 'Subject ID is required'
      }
      break
    case 'resourceId':
      if (!form.value.resourceId) {
        errors.value.resourceId = 'Resource is required'
      }
      break
    case 'action':
      if (!form.value.action) {
        errors.value.action = 'Action is required'
      }
      break
    case 'effect':
      if (!form.value.effect) {
        errors.value.effect = 'Effect is required'
      }
      break
  }
}

const updateConditionJson = () => {
  if (!condition.value.type) {
    form.value.conditionJson = ''
    return
  }

  const conditionData = {
    type: condition.value.type,
    operator: condition.value.operator
  }

  switch (condition.value.type) {
    case 'time':
    case 'date':
      if (condition.value.operator === 'between') {
        conditionData.value = {
          start: condition.value.start,
          end: condition.value.end
        }
      } else {
        conditionData.value = condition.value.value
      }
      break
    case 'ip':
      conditionData.value = condition.value.ips.filter(ip => ip)
      break
    case 'role':
      conditionData.value = condition.value.roles.filter(role => role)
      break
    case 'organization':
      conditionData.value = condition.value.orgIds.filter(id => id)
      break
  }

  form.value.conditionJson = JSON.stringify(conditionData)
}

const addIp = () => {
  condition.value.ips.push('')
}

const removeIp = (index) => {
  condition.value.ips.splice(index, 1)
  updateConditionJson()
}

const addRole = () => {
  condition.value.roles.push('')
}

const removeRole = (index) => {
  condition.value.roles.splice(index, 1)
  updateConditionJson()
}

const addOrgId = () => {
  condition.value.orgIds.push(null)
}

const removeOrgId = (index) => {
  condition.value.orgIds.splice(index, 1)
  updateConditionJson()
}

const savePolicy = async () => {
  // Validate all fields
  Object.keys(errors.value).forEach(validateField)
  
  if (!isFormValid.value) {
    return
  }

  loading.value = true
  try {
    await policyStore.updatePolicy(route.params.id, form.value)
    router.push('/policies')
  } catch (error) {
    // Error handling is done in the store
  } finally {
    loading.value = false
  }
}

const confirmDiscard = () => {
  discardModal.value.show()
}

const discardChanges = () => {
  discardModal.value.hide()
  router.push('/policies')
}
</script>

<style scoped>
.required:after {
  content: " *";
  color: red;
}

.card {
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
}

.card-header {
  border-bottom: 1px solid rgba(0, 0, 0, 0.125);
}

.form-label {
  font-weight: 500;
}

.input-group .btn {
  z-index: 0;
}
</style> 