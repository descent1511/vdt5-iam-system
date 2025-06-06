<template>
  <div class="permission-display">
    <div v-if="permissions && permissions.length > 0" class="permission-groups">
      <!-- Users Group -->
      <div v-if="hasUserPermissions" class="permission-group mb-3">
        <h6 class="group-title">
          <i class="bi bi-people me-2"></i>Users
        </h6>
        <div class="permission-tags">
          <span v-if="hasUserRead" class="permission-tag bg-primary-subtle text-primary">
            <i class="bi bi-eye me-1"></i>View
          </span>
          <span v-if="hasUserCreate" class="permission-tag bg-success-subtle text-success">
            <i class="bi bi-plus-lg me-1"></i>Create
          </span>
          <span v-if="hasUserUpdate" class="permission-tag bg-warning-subtle text-warning">
            <i class="bi bi-pencil me-1"></i>Edit
          </span>
          <span v-if="hasUserDelete" class="permission-tag bg-danger-subtle text-danger">
            <i class="bi bi-trash me-1"></i>Delete
          </span>
        </div>
      </div>

      <!-- Roles Group -->
      <div v-if="hasRolePermissions" class="permission-group mb-3">
        <h6 class="group-title">
          <i class="bi bi-shield-lock me-2"></i>Roles
        </h6>
        <div class="permission-tags">
          <span v-if="hasRoleRead" class="permission-tag bg-primary-subtle text-primary">
            <i class="bi bi-eye me-1"></i>View
          </span>
          <span v-if="hasRoleCreate" class="permission-tag bg-success-subtle text-success">
            <i class="bi bi-plus-lg me-1"></i>Create
          </span>
          <span v-if="hasRoleUpdate" class="permission-tag bg-warning-subtle text-warning">
            <i class="bi bi-pencil me-1"></i>Edit
          </span>
          <span v-if="hasRoleDelete" class="permission-tag bg-danger-subtle text-danger">
            <i class="bi bi-trash me-1"></i>Delete
          </span>
        </div>
      </div>

      <!-- Resources Group -->
      <div v-if="hasResourcePermissions" class="permission-group mb-3">
        <h6 class="group-title">
          <i class="bi bi-box me-2"></i>Resources
        </h6>
        <div class="permission-tags">
          <span v-if="hasResourceRead" class="permission-tag bg-primary-subtle text-primary">
            <i class="bi bi-eye me-1"></i>View
          </span>
          <span v-if="hasResourceCreate" class="permission-tag bg-success-subtle text-success">
            <i class="bi bi-plus-lg me-1"></i>Create
          </span>
          <span v-if="hasResourceUpdate" class="permission-tag bg-warning-subtle text-warning">
            <i class="bi bi-pencil me-1"></i>Edit
          </span>
          <span v-if="hasResourceDelete" class="permission-tag bg-danger-subtle text-danger">
            <i class="bi bi-trash me-1"></i>Delete
          </span>
        </div>
      </div>

      <!-- Policies Group -->
      <div v-if="hasPolicyPermissions" class="permission-group mb-3">
        <h6 class="group-title">
          <i class="bi bi-file-earmark-text me-2"></i>Policies
        </h6>
        <div class="permission-tags">
          <span v-if="hasPolicyRead" class="permission-tag bg-primary-subtle text-primary">
            <i class="bi bi-eye me-1"></i>View
          </span>
          <span v-if="hasPolicyCreate" class="permission-tag bg-success-subtle text-success">
            <i class="bi bi-plus-lg me-1"></i>Create
          </span>
          <span v-if="hasPolicyUpdate" class="permission-tag bg-warning-subtle text-warning">
            <i class="bi bi-pencil me-1"></i>Edit
          </span>
          <span v-if="hasPolicyDelete" class="permission-tag bg-danger-subtle text-danger">
            <i class="bi bi-trash me-1"></i>Delete
          </span>
        </div>
      </div>
      <!-- Client Group -->
      <div v-if="hasClientPermissions" class="permission-group mb-3">
        <h6 class="group-title">
          <i class="bi bi-key me-2"></i>Clients
        </h6>
        <div class="permission-tags">
          <span v-if="hasClientRead" class="permission-tag bg-primary-subtle text-primary">
            <i class="bi bi-eye me-1"></i>View
          </span>
          <span v-if="hasClientCreate" class="permission-tag bg-success-subtle text-success">
            <i class="bi bi-plus-lg me-1"></i>Create
          </span>
          <span v-if="hasClientUpdate" class="permission-tag bg-warning-subtle text-warning">
            <i class="bi bi-pencil me-1"></i>Edit
          </span>
          <span v-if="hasClientDelete" class="permission-tag bg-danger-subtle text-danger">
            <i class="bi bi-trash me-1"></i>Delete
          </span>
        </div>
      </div>

      <!-- Product Group -->
      <div v-if="hasProductPermissions" class="permission-group mb-3">
        <h6 class="group-title">
          <i class="bi bi-bag me-2"></i>Products
        </h6>
        <div class="permission-tags">
          <span v-if="hasProductRead" class="permission-tag bg-primary-subtle text-primary">
            <i class="bi bi-eye me-1"></i>View
          </span>
          <span v-if="hasProductCreate" class="permission-tag bg-success-subtle text-success">
            <i class="bi bi-plus-lg me-1"></i>Create
          </span>
          <span v-if="hasProductUpdate" class="permission-tag bg-warning-subtle text-warning">
            <i class="bi bi-pencil me-1"></i>Edit
          </span>
          <span v-if="hasProductDelete" class="permission-tag bg-danger-subtle text-danger">
            <i class="bi bi-trash me-1"></i>Delete
          </span>
        </div>
      </div>

      <!-- Scope Group -->
      <div v-if="hasScopePermissions" class="permission-group mb-3">
        <h6 class="group-title">
          <i class="bi bi-diagram-3 me-2"></i>Scopes
        </h6>
        <div class="permission-tags">
          <span v-if="hasScopeRead" class="permission-tag bg-primary-subtle text-primary">
            <i class="bi bi-eye me-1"></i>View
          </span>
          <span v-if="hasScopeCreate" class="permission-tag bg-success-subtle text-success">
            <i class="bi bi-plus-lg me-1"></i>Create
          </span>
          <span v-if="hasScopeUpdate" class="permission-tag bg-warning-subtle text-warning">
            <i class="bi bi-pencil me-1"></i>Edit
          </span>
          <span v-if="hasScopeDelete" class="permission-tag bg-danger-subtle text-danger">
            <i class="bi bi-trash me-1"></i>Delete
          </span>
        </div>
      </div>
    </div>
    <span v-else class="text-muted">No permissions</span>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  permissions: {
    type: Array,
    default: () => []
  }
})

// User permissions
const hasUserRead = computed(() => props.permissions.includes('USER_READ'))
const hasUserCreate = computed(() => props.permissions.includes('USER_CREATE'))
const hasUserUpdate = computed(() => props.permissions.includes('USER_UPDATE'))
const hasUserDelete = computed(() => props.permissions.includes('USER_DELETE'))
const hasUserPermissions = computed(() => hasUserRead.value || hasUserCreate.value || hasUserUpdate.value || hasUserDelete.value)

// Role permissions
const hasRoleRead = computed(() => props.permissions.includes('ROLE_READ'))
const hasRoleCreate = computed(() => props.permissions.includes('ROLE_CREATE'))
const hasRoleUpdate = computed(() => props.permissions.includes('ROLE_UPDATE'))
const hasRoleDelete = computed(() => props.permissions.includes('ROLE_DELETE'))
const hasRolePermissions = computed(() => hasRoleRead.value || hasRoleCreate.value || hasRoleUpdate.value || hasRoleDelete.value)

// Resource permissions
const hasResourceRead = computed(() => props.permissions.includes('RESOURCE_READ'))
const hasResourceCreate = computed(() => props.permissions.includes('RESOURCE_CREATE'))
const hasResourceUpdate = computed(() => props.permissions.includes('RESOURCE_UPDATE'))
const hasResourceDelete = computed(() => props.permissions.includes('RESOURCE_DELETE'))
const hasResourcePermissions = computed(() => hasResourceRead.value || hasResourceCreate.value || hasResourceUpdate.value || hasResourceDelete.value)

// Policy permissions
const hasPolicyRead = computed(() => props.permissions.includes('POLICY_READ'))
const hasPolicyCreate = computed(() => props.permissions.includes('POLICY_CREATE'))
const hasPolicyUpdate = computed(() => props.permissions.includes('POLICY_UPDATE'))
const hasPolicyDelete = computed(() => props.permissions.includes('POLICY_DELETE'))
const hasPolicyPermissions = computed(() => hasPolicyRead.value || hasPolicyCreate.value || hasPolicyUpdate.value || hasPolicyDelete.value)

// Client permissions
const hasClientRead = computed(() => props.permissions.includes('CLIENT_READ'))
const hasClientCreate = computed(() => props.permissions.includes('CLIENT_CREATE'))
const hasClientUpdate = computed(() => props.permissions.includes('CLIENT_UPDATE'))
const hasClientDelete = computed(() => props.permissions.includes('CLIENT_DELETE'))
const hasClientPermissions = computed(() => hasClientRead.value || hasClientCreate.value || hasClientUpdate.value || hasClientDelete.value)

// Product permissions
const hasProductRead = computed(() => props.permissions.includes('PRODUCT_READ'))
const hasProductCreate = computed(() => props.permissions.includes('PRODUCT_CREATE'))
const hasProductUpdate = computed(() => props.permissions.includes('PRODUCT_UPDATE'))
const hasProductDelete = computed(() => props.permissions.includes('PRODUCT_DELETE'))
const hasProductPermissions = computed(() => hasProductRead.value || hasProductCreate.value || hasProductUpdate.value || hasProductDelete.value)

// Scope permissions
const hasScopeRead = computed(() => props.permissions.includes('SCOPE_READ'))
const hasScopeCreate = computed(() => props.permissions.includes('SCOPE_CREATE'))
const hasScopeUpdate = computed(() => props.permissions.includes('SCOPE_UPDATE'))
const hasScopeDelete = computed(() => props.permissions.includes('SCOPE_DELETE'))
const hasScopePermissions = computed(() => hasScopeRead.value || hasScopeCreate.value || hasScopeUpdate.value || hasScopeDelete.value)
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