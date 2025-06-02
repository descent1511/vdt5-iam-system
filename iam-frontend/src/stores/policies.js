import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { policyService } from '../services/policyService'
import { useToast } from 'vue-toastification'

export const usePolicyStore = defineStore('policies', () => {
  const toast = useToast()
  
  // State
  const policies = ref([])
  const currentPolicy = ref(null)
  const loading = ref(false)
  const error = ref(null)
  const pagination = ref({
    page: 1,
    limit: 10,
    totalPages: 0,
    total: 0
  })
  const totalPolicies = ref(0)

  // Actions
  async function fetchPolicies(params = {}) {
    try {
      loading.value = true
      error.value = null
      const data = await policyService.getPolicies(params)
      console.log(data)
      policies.value = data
      totalPolicies = data.length 
      pagination.value = {
        page: params.page || 1,
        limit: params.limit || 10,
        totalPages: Math.ceil(data.total / (params.limit || 10)),
        total: data.total
      }
      
      return data
    } catch (err) {
      console.log(err)
      error.value = err.message || 'Failed to fetch policies'
      toast.error(error.value)
    } finally {
      loading.value = false
    }
  }

  async function fetchPolicyById(id) {
    try {
      loading.value = true
      error.value = null
      
      const data = await policyService.getPolicyById(id)
      console.log(data)
      currentPolicy.value = data
      return data
    } catch (err) {
      error.value = err.message || 'Failed to fetch policy'
      toast.error(error.value)
    } finally {
      loading.value = false
    }
  }

  async function createPolicy(policyData) {
    try {
      loading.value = true
      error.value = null
      
      const data = await policyService.createPolicy(policyData)
      
      toast.success('Policy created successfully')
      policies.value.push(data)
      return data
    } catch (err) {
      error.value = err.message || 'Failed to create policy'
      toast.error(error.value)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updatePolicy(id, policyData) {
    try {
      loading.value = true
      error.value = null
      
      const data = await policyService.updatePolicy(id, policyData)
      
      // Update the policy in the policies array
      const index = policies.value.findIndex(policy => policy.id === id)
      if (index !== -1) {
        policies.value[index] = { ...policies.value[index], ...policyData }
      }
      
      toast.success('Policy updated successfully')
      return data
    } catch (err) {
      error.value = err.message || 'Failed to update policy'
      toast.error(error.value)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function deletePolicy(id) {
    try {
      loading.value = true
      error.value = null
      
      await policyService.deletePolicy(id)
      
      // Remove the policy from the policies array
      policies.value = policies.value.filter(policy => policy.id !== id)
      
      toast.success('Policy deleted successfully')
      return true
    } catch (err) {
      error.value = err.message || 'Failed to delete policy'
      toast.error(error.value)
      return false
    } finally {
      loading.value = false
    }
  }
  
  // Reset store state
  function resetState() {
    policies.value = []
    currentPolicy.value = null
    loading.value = false
    error.value = null
    pagination.value = {
      page: 1,
      limit: 10,
      totalPages: 0,
      total: 0
    }
  }

  return {
    // State
    policies,
    currentPolicy,
    loading,
    error,
    pagination,
    
    // Getters
    totalPolicies,
    
    // Actions
    fetchPolicies,
    fetchPolicyById,
    createPolicy,
    updatePolicy,
    deletePolicy,
    resetState
  }
})