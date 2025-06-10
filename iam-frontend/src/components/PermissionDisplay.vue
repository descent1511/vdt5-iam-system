<template>
  <div class="permission-display">
    <div v-if="groupedPermissions.length > 0" class="d-flex flex-column gap-3">
      <div 
        v-for="group in groupedPermissions" 
        :key="group.name" 
        class="permission-display-group"
      >
        <h6 class="group-title">
          <i :class="`bi ${group.icon} me-2`"></i>{{ group.name }}
        </h6>
        <div class="permission-tags">
          <span 
            v-for="action in group.actions" 
            :key="action.type" 
            :class="['permission-tag', `action-${action.type.toLowerCase()}`]"
          >
            <i :class="`bi ${action.icon} me-1`"></i>{{ action.label }}
          </span>
        </div>
      </div>
    </div>
    <span v-else class="text-muted">No permissions assigned</span>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  permissions: {
    type: Array,
    default: () => [],
  },
});

const permissionStructure = [
  { name: 'Users', key: 'USER', icon: 'bi-people' },
  { name: 'Roles', key: 'ROLE', icon: 'bi-shield-lock' },
  { name: 'Policies', key: 'POLICY', icon: 'bi-file-earmark-text' },
  { name: 'Clients', key: 'CLIENT', icon: 'bi-key' },
  { name: 'Products', key: 'PRODUCT', icon: 'bi-bag' },
  { name: 'Services', key: 'SERVICE', icon: 'bi-hdd-rack' },
  { name: 'Scopes', key: 'SCOPE', icon: 'bi-diagram-3' },
  { name: 'Resources', key: 'RESOURCE', icon: 'bi-box' },
];

const actionsStructure = [
  { type: 'READ', label: 'View', icon: 'bi-eye' },
  { type: 'CREATE', label: 'Create', icon: 'bi-plus-lg' },
  { type: 'UPDATE', label: 'Edit', icon: 'bi-pencil' },
  { type: 'DELETE', label: 'Delete', icon: 'bi-trash' },
];

const groupedPermissions = computed(() => {
  const userPermissions = new Set(props.permissions);
  return permissionStructure
    .map(group => {
      const availableActions = actionsStructure.filter(action =>
        userPermissions.has(`${group.key}_${action.type}`)
      );
      return {
        ...group,
        actions: availableActions,
      };
    })
    .filter(group => group.actions.length > 0);
});
</script>

<style scoped>
.permission-display {
  display: inline-block;
}

.permission-groups {
  display: flex;
  flex-direction: column;
}

.permission-group {
  background-color: #f8f9fa;
  border-radius: 8px;
}

.group-title {
  font-size: 0.875rem;
  font-weight: 600;
  color: #495057;
  margin-bottom: 0.5rem;
}

.permission-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.permission-tag {
  display: inline-flex;
  align-items: center;
  padding: 0.35rem 0.75rem;
  border-radius: 50rem;
  font-size: 0.875rem;
  font-weight: 500;
}

.permission-tag i {
  font-size: 0.875rem;
}

/* Background colors for different actions */
.bg-primary-subtle {
  background-color: rgba(13, 110, 253, 0.1);
}

.bg-success-subtle {
  background-color: rgba(25, 135, 84, 0.1);
}

.bg-warning-subtle {
  background-color: rgba(255, 193, 7, 0.1);
}

.bg-danger-subtle {
  background-color: rgba(220, 53, 69, 0.1);
}

/* Text colors */
.text-primary {
  color: #0d6efd !important;
}

.text-success {
  color: #198754 !important;
}

.text-warning {
  color: #ffc107 !important;
}

.text-danger {
  color: #dc3545 !important;
}
</style> 