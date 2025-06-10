<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Create Policy</h1>
      <router-link to="/policies" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left me-2"></i> Back to List
      </router-link>
    </div>

    <div class="card">
      <div class="card-body">
        <form @submit.prevent="savePolicy">
          <div class="row">
            <div class="col-md-6">
              <div class="card mb-4">
                <div class="card-header">
                  <h5 class="card-title mb-0">Subject</h5>
                </div>
                <div class="card-body">
                  <SubjectSelector
                    v-model="form.subject"
                    :errors="errors"
                  />
                </div>
              </div>

              <div class="mb-3">
                <label class="form-label">Resource</label>
                <select v-model="form.resourceId" class="form-select" required>
                  <option v-for="resource in resources" :key="resource.id" :value="resource.id">
                    {{ resource.name }}
                  </option>
                </select>
              </div>
            </div>

            <div class="col-md-6">
              <div class="mb-3">
                <label class="form-label">Action</label>
                <input v-model="form.action" type="text" class="form-control" required />
              </div>

              <div class="mb-3">
                <label class="form-label">Effect</label>
                <select v-model="form.effect" class="form-select" required>
                  <option value="ALLOW">Allow</option>
                  <option value="DENY">Deny</option>
                </select>
              </div>

              <div class="mb-3">
                <label class="form-label">Conditions</label>
                <div class="card">
                  <div class="card-body">
                    <div class="mb-3">
                      <label class="form-label">Condition Type</label>
                      <select v-model="condition.type" class="form-select" @change="updateConditionJson">
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
                        <select v-model="condition.operator" class="form-select" @change="updateConditionJson">
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

                      <div class="mb-3" v-if="['time', 'date'].includes(condition.type) && condition.operator === 'between'">
                        <div class="row">
                          <div class="col-6">
                            <label class="form-label">Start</label>
                            <input v-model="condition.start" type="time" class="form-control" @change="updateConditionJson" />
                          </div>
                          <div class="col-6">
                            <label class="form-label">End</label>
                            <input v-model="condition.end" type="time" class="form-control" @change="updateConditionJson" />
                          </div>
                        </div>
                      </div>

                      <div class="mb-3" v-if="['time', 'date'].includes(condition.type) && ['before', 'after'].includes(condition.operator)">
                        <label class="form-label">Value</label>
                        <input v-model="condition.value" type="time" class="form-control" @change="updateConditionJson" />
                      </div>

                      <div class="mb-3" v-if="condition.type === 'ip'">
                        <label class="form-label">IP Ranges (CIDR)</label>
                        <div v-for="(ip, index) in condition.ips" :key="index" class="input-group mb-2">
                          <input v-model="condition.ips[index]" type="text" class="form-control" placeholder="192.168.1.0/24" @change="updateConditionJson" />
                          <button type="button" class="btn btn-outline-danger" @click="removeIp(index)">
                            <i class="bi bi-trash"></i>
                          </button>
                        </div>
                        <button type="button" class="btn btn-outline-primary btn-sm" @click="addIp">
                          Add IP Range
                        </button>
                      </div>

                      <div class="mb-3" v-if="condition.type === 'role'">
                        <label class="form-label">Roles</label>
                        <div v-for="(role, index) in condition.roles" :key="index" class="input-group mb-2">
                          <input v-model="condition.roles[index]" type="text" class="form-control" @change="updateConditionJson" />
                          <button type="button" class="btn btn-outline-danger" @click="removeRole(index)">
                            <i class="bi bi-trash"></i>
                          </button>
                        </div>
                        <button type="button" class="btn btn-outline-primary btn-sm" @click="addRole">
                          Add Role
                        </button>
                      </div>

                      <div class="mb-3" v-if="condition.type === 'organization'">
                        <label class="form-label">Organization IDs</label>
                        <div v-for="(orgId, index) in condition.orgIds" :key="index" class="input-group mb-2">
                          <input v-model="condition.orgIds[index]" type="number" class="form-control" @change="updateConditionJson" />
                          <button type="button" class="btn btn-outline-danger" @click="removeOrgId(index)">
                            <i class="bi bi-trash"></i>
                          </button>
                        </div>
                        <button type="button" class="btn btn-outline-primary btn-sm" @click="addOrgId">
                          Add Organization ID
                        </button>
                      </div>
                    </template>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="text-end mt-4">
            <router-link to="/policies" class="btn btn-secondary me-2">Cancel</router-link>
            <button type="submit" class="btn btn-primary">Create Policy</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { policyService } from '../../services/policyService'
import { resourceService } from '../../services/resourceService'
import { useToast } from 'vue-toastification'
import SubjectSelector from '../../components/policies/SubjectSelector.vue'

const router = useRouter()
const toast = useToast()
const resources = ref([])

const form = ref({
  name: '',
  description: '',
  subject: {
    subjectType: '',
    subjectId: ''
  },
  resourceId: null,
  action: '',
  effect: 'ALLOW',
  conditionJson: ''
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

const errors = ref({
  name: '',
  description: '',
  subjectType: '',
  subjectId: '',
  resourceId: '',
  action: '',
  effect: '',
  conditionJson: ''
})

onMounted(async () => {
  await loadResources()
})

const loadResources = async () => {
  try {
    resources.value = await resourceService.getResources()
  } catch (error) {
    toast.error('Failed to load resources')
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
  try {
    // Prepare the policy data
    const policyData = {
      name: form.value.name,
      description: form.value.description,
      subjectType: form.value.subject.subjectType,
      subjectId: form.value.subject.subjectId,
      resourceId: form.value.resourceId,
      action: form.value.action,
      effect: form.value.effect,
      conditionJson: form.value.conditionJson
    }
    console.log('policyData', policyData)
    await policyService.createPolicy(policyData)
    toast.success('Policy created successfully')
    router.push('/policies')
  } catch (error) {
    toast.error(error.response?.data?.message || 'Failed to create policy')
  }
}
</script> 