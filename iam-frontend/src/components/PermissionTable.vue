<template>
  <div>
    <div v-if="permissionStore.loading" class="text-center">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">Loading permissions...</span>
      </div>
    </div>

    <div v-else-if="permissionStore.error" class="alert alert-danger">
      Failed to load permissions. Please try again later.
    </div>

    <div v-else class="table-responsive">
      <table class="table">
        <thead>
          <tr>
            <th>Management Type</th>
            <th v-for="action in permissionStore.actions" :key="action" class="text-center">
              <span class="permission-type" :class="`permission-${action.toLowerCase()}`">{{ action }}</span>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="entity in permissionStore.entities" :key="entity.key">
            <td>
              <i class="bi" :class="entity.icon"></i>
              {{ entity.name }}
            </td>
            <td v-for="action in permissionStore.actions" :key="action" class="text-center">
              <input 
                class="form-check-input" 
                type="checkbox" 
                :checked="hasPermission(entity.key, action)"
                @change="togglePermission(entity.key, action)"
                :disabled="disabled"
              >
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, onMounted } from 'vue';
import { usePermissionStore } from '../stores/permissions';

const props = defineProps({
  modelValue: {
    type: Array,
    required: true
  },
  disabled: {
    type: Boolean,
    default: false,
  }
});

const emit = defineEmits(['update:modelValue']);

const permissionStore = usePermissionStore();

onMounted(() => {
  permissionStore.fetchAllPermissions();
  console.log(permissionStore.entities);
});

const hasPermission = (entityKey, action) => {
  const permission = `${entityKey}_${action}`;
  return props.modelValue.includes(permission);
};

const togglePermission = (entityKey, action) => {
  const permission = `${entityKey}_${action}`;

  const newPermissions = [...props.modelValue];
  const index = newPermissions.indexOf(permission);

  if (index === -1) {
    newPermissions.push(permission);
  } else {
    newPermissions.splice(index, 1);
  }
  emit('update:modelValue', newPermissions);
};
</script>

<style scoped>
.table {
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  overflow: hidden;
}

.table thead th {
  background-color: #f8f9fa;
  border-bottom: 2px solid #dee2e6;
  padding: 1rem;
  font-weight: 600;
  text-transform: capitalize;
  font-size: 0.85rem;
  letter-spacing: 0.5px;
}

.table tbody td {
  padding: 1rem;
  vertical-align: middle;
}

.table tbody tr {
  transition: background-color 0.2s ease;
}

.table tbody tr:hover {
  background-color: #f8f9fa;
}

.table tbody td:first-child {
  font-weight: 500;
  color: #495057;
}

.table tbody td:first-child i {
  color: #6c757d;
  font-size: 1.1rem;
  margin-right: 0.5rem;
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

.form-check-input:focus {
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}

.permission-type {
  font-size: 0.75rem;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-weight: 500;
}

.permission-read { background-color: #e3f2fd; color: #0d6efd; }
.permission-create { background-color: #e8f5e9; color: #198754; }
.permission-update { background-color: #fff3e0; color: #fd7e14; }
.permission-delete { background-color: #fbe9e7; color: #dc3545; }
</style> 