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
              <span class="permission-action-badge" :class="`action-${action.toLowerCase()}`">{{ action }}</span>
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