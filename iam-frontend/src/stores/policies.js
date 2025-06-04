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
      // Thêm showAllResources cho mỗi policy
      policies.value = data.map(policy => ({
        ...policy,
        showAllResources: false // Khởi tạo thuộc tính
      }))
      totalPolicies.value = data.length 
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
      console.log('Policy data from API:', data)
      
      if (!data) {
        throw new Error('Policy not found')
      }
      
      currentPolicy.value = { ...data, showAllResources: false } // Thêm showAllResources
      return data
    } catch (err) {
      console.error('Error in fetchPolicyById:', err)
      error.value = err.message || 'Failed to fetch policy'
      toast.error(error.value)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function createPolicy(policyData) {
    try {
      loading.value = true
      error.value = null
      
      const data = await policyService.createPolicy(policyData)
      policies.value.push({ ...data, showAllResources: false }) // Thêm showAllResources
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
      
      // Cập nhật policy trong mảng policies
      const index = policies.value.findIndex(policy => policy.id === id)
      if (index !== -1) {
        policies.value[index] = { 
          ...policies.value[index], 
          ...policyData,
          showAllResources: policies.value[index].showAllResources // Giữ lại showAllResources
        }
      }
    
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
      
      // Xóa policy khỏi mảng policies
      policies.value = policies.value.filter(policy => policy.id !== id)
      
      return true
    } catch (err) {
      error.value = err.message || 'Failed to delete policy'
      toast.error(error.value)
      return false
    } finally {
      loading.value = false
    }
  }

  // Action để toggle showAllResources
  function toggleResourceVisibility(id) {
    const policy = policies.value.find(policy => policy.id === id)
    if (policy) {
      policy.showAllResources = !policy.showAllResources
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
    toggleResourceVisibility,
    resetState
  }
})