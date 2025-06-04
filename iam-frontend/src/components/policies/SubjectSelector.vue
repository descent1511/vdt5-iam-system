<template>
  <div class="subject-selector">
    <div class="mb-3">
      <label class="form-label required">Subject Type</label>
      <select 
        v-model="selectedType" 
        class="form-select"
        :class="{ 'is-invalid': errors.subjectType }"
        @change="handleTypeChange"
      >
        <option value="">Select a subject type</option>
        <option value="USER">User</option>
        <option value="ROLE">Role</option>
        <option value="CLIENT">Client</option>
      </select>
      <div class="invalid-feedback">{{ errors.subjectType }}</div>
    </div>

    <div v-if="selectedType" class="mb-3">
      <label class="form-label required">Subject</label>
      <div v-if="loading" class="text-center py-3">
        <div class="spinner-border spinner-border-sm text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>
      <div v-else>
        <select 
          v-model="selectedSubject" 
          class="form-select"
          :class="{ 'is-invalid': errors.subjectId }"
          @change="handleSubjectChange"
        >
          <option value="">Select a {{ selectedType.toLowerCase() }}</option>
          <option 
            v-for="subject in subjects" 
            :key="subject.id" 
            :value="subject.id"
          >
            {{ getSubjectLabel(subject) }}
          </option>
        </select>
        <div class="invalid-feedback">{{ errors.subjectId }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue'
import { useUserStore } from '../../stores/users'
import { useRoleStore } from '../../stores/roles'
import { useClientStore } from '../../stores/clients'
import { useToast } from 'vue-toastification'

const props = defineProps({
  modelValue: {
    type: Object,
    required: true
  },
  errors: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:modelValue'])

const userStore = useUserStore()
const roleStore = useRoleStore()
const clientStore = useClientStore()
const toast = useToast()

const selectedType = ref(props.modelValue.subjectType || '')
const selectedSubject = ref(props.modelValue.subjectId || '')
const loading = ref(false)
const subjects = ref([])

// Define loadSubjects before using it in watch
const loadSubjects = async () => {
  if (!selectedType.value) return
  
  loading.value = true
  try {
    switch (selectedType.value) {
      case 'USER':
        await userStore.fetchUsers()
        subjects.value = userStore.users
        break
      case 'ROLE':
        await roleStore.fetchRoles()
        subjects.value = roleStore.roles
        break
      case 'CLIENT':
        await clientStore.fetchClients()
        subjects.value = clientStore.clients
        break
      default:
        subjects.value = []
    }
  } catch (error) {
    console.error('Error loading subjects:', error)
    toast.error('Failed to load subjects')
  } finally {
    loading.value = false
  }
}

// Define handlers before using them in template
const handleTypeChange = async (event) => {
  const newType = event.target.value
  selectedType.value = newType
  selectedSubject.value = ''
  emit('update:modelValue', {
    ...props.modelValue,
    subjectType: newType,
    subjectId: ''
  })
  if (newType) {
    await loadSubjects()
  }
}

const handleSubjectChange = (event) => {
  const newId = event.target.value
  selectedSubject.value = newId
  
  // For client, we need to use clientId instead of id
  const subjectId = selectedType.value === 'CLIENT' 
    ? subjects.value.find(s => s.id === newId)?.clientId 
    : newId
    
  emit('update:modelValue', {
    ...props.modelValue,
    subjectId: subjectId
  })
}

const getSubjectLabel = (subject) => {
  switch (selectedType.value) {
    case 'USER':
      return `${subject.fullName} (${subject.username})`
    case 'ROLE':
      return subject.name
    case 'CLIENT':
      return `${subject.name} (${subject.clientId})`
    default:
      return ''
  }
}

// Add watch after all functions are defined
watch(() => props.modelValue, (newValue) => {
  selectedType.value = newValue.subjectType || ''
  selectedSubject.value = newValue.subjectId || ''
  if (newValue.subjectType) {
    loadSubjects()
  }
}, { immediate: true })

// Add computed property for subject value
const subjectValue = computed(() => {
  if (!selectedType.value || !selectedSubject.value) return ''
  
  const subject = subjects.value.find(s => {
    switch (selectedType.value) {
      case 'USER':
        return s.id === selectedSubject.value
      case 'ROLE':
        return s.id === selectedSubject.value
      case 'CLIENT':
        return s.clientId === selectedSubject.value
      default:
        return false
    }
  })
  
  return subject ? getSubjectLabel(subject) : ''
})
</script>

<style scoped>
.subject-selector {
  margin-bottom: 1rem;
}

.required:after {
  content: " *";
  color: red;
}
</style> 