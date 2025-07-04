<template>
  <DataList
    ref="dataList"
    title="Users"
    :items="userStore.users"
    :columns="columns"
    :can-create="hasPermission('USER_CREATE')"
    create-route="/users/create"
    create-button-text="New User"
    search-placeholder="Search users..."
    :search-fields="['fullName', 'username', 'email']"
    :filters="roleFilters"
    filter-placeholder="All Roles"
    :loading="userStore.loading"
    :error="userStore.error"
    :pagination="userStore.pagination"
    @page-change="changePage"
    @delete="deleteUser"
  >
    <!-- Custom column slots -->
    <template #name="{ item }">
      <div class="d-flex align-items-center">
        <div class="avatar-initials me-3">
          {{ getInitials(item.fullName) }}
        </div>
        <div>
          <div class="fw-bold">{{ item.fullName }}</div>
          <div class="small text-muted">{{ item.email }}</div>
        </div>
      </div>
    </template>

    <template #roles="{ item }">
      <div class="d-flex flex-wrap gap-1">
        <span 
          v-for="role in item.roles" 
          :key="role"
          :class="['role-badge', getRoleBadgeClass(role)]"
        >
          {{ formatRoleName(role) }}
        </span>
      </div>
    </template>

    <template #permissions="{ item }">
      <div class="permissions-container">
        <div class="permissions-wrapper">
          <span class="permission-count">
            {{ item.permissions.length }} permissions
          </span>
          <div
            class="permissions-list"
            :class="{ 'show': item.showAllPermissions }"
            v-if="item.permissions.length > 0"
          >
            <span
              v-for="permission in item.permissions"
              :key="permission"
              class="permission-badge"
            >
              <span class="permission-type">{{ formatPermissionType(permission) }}</span>
              <span class="permission-action">{{ formatPermissionAction(permission) }}</span>
            </span>
          </div>
        </div>
        <button
          v-if="item.permissions.length > 0"
          class="btn btn-link toggle-btn"
          @click="togglePermissions(item)"
        >
          <span class="toggle-content">
            <span v-if="!item.showAllPermissions">
              Show
              <i class="bi bi-chevron-down"></i>
            </span>
            <span v-else>
              Hide
              <i class="bi bi-chevron-up"></i>
            </span>
          </span>
        </button>
      </div>
    </template>

    <template #actions="{ item }">
      <div class="d-flex justify-content-end">
        <router-link
          v-if="hasPermission('USER_UPDATE')"
          :to="`/users/${item.id}/edit`"
          class="btn-action btn-edit"
          data-bs-toggle="tooltip" title="Edit User"
        >
          <i class="bi bi-pencil"></i>
        </router-link>
        <button
          v-if="hasPermission('USER_DELETE')"
          class="btn-action btn-delete"
          @click="confirmDelete(item)"
          data-bs-toggle="tooltip" title="Delete User"
        >
          <i class="bi bi-trash"></i>
        </button>
      </div>
    </template>
  </DataList>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../../stores/users'
import { useAuthStore } from '../../stores/auth'
import DataList from '../../components/layout/DataList.vue'

// Store
const userStore = useUserStore()
const authStore = useAuthStore()
const dataList = ref(null)

// Columns configuration
const columns = [
  { key: 'name', label: 'Name' },
  { key: 'username', label: 'Username' },
  { key: 'email', label: 'Email' },
  { key: 'roles', label: 'Roles' },
  { key: 'permissions', label: 'Permissions' },
  { key: 'actions', label: 'Actions', class: 'text-end' }
]

// Role filters
const roleFilters = [
  { value: 'ROLE_ADMIN', label: 'Admin' },
  { value: 'ROLE_MANAGER', label: 'Manager' },
  { value: 'ROLE_USER', label: 'User' }
]

// Methods
function getInitials(fullName) {
  if (!fullName) return ''
  return fullName.split(' ')
    .map(name => name.charAt(0))
    .join('')
    .toUpperCase()
}

function getRoleBadgeClass(role) {
  const roleName = formatRoleName(role).toLowerCase();
  if (roleName === 'admin') return 'role-admin';
  if (roleName === 'user') return 'role-user';
  return 'role-guest';
}

function formatRoleName(role) {
  if (!role) return '';
  return role.replace('ROLE_', '');
}

function changePage(page) {
  userStore.pagination.page = page
  loadUsers()
}

function confirmDelete(user) {
  dataList.value?.showDeleteModal(user)
}

async function deleteUser(user) {
  if (!user) return
  
  try {
    await userStore.deleteUser(user.id)
    await loadUsers()
    dataList.value.hideDeleteModal()
  } catch (error) {
    console.error('Failed to delete user:', error)
  }
}

function hasPermission(permission) {
  return authStore.can(permission)
}

function togglePermissions(user) {
  user.showAllPermissions = !user.showAllPermissions
}

function formatPermissionType(permission) {
  const type = permission.split('_')[0].toLowerCase()
  return type.charAt(0).toUpperCase() + type.slice(1)
}

function formatPermissionAction(permission) {
  const action = permission.split('_')[1].toLowerCase()
  return action.charAt(0).toUpperCase() + action.slice(1)
}

async function loadUsers() {
  const params = {
    page: userStore.pagination.page,
    limit: userStore.pagination.limit
  }
  await userStore.fetchUsers(params)

  if (userStore.users && userStore.users.length > 0) {
    userStore.users.forEach(user => {
      if (typeof user.showAllPermissions === 'undefined') {
        user.showAllPermissions = true;
      }
    });
  }
}

onMounted(loadUsers)
</script>

<style scoped>
/* Component-specific styles */

/* Avatar */
.avatar {
  width: 36px;
  height: 36px;
  font-size: 14px;
  font-weight: 600;
}

.avatar-initials {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #007bff; /* Example background color */
  color: white;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
}

.badge {
  font-size: 0.75rem;
  padding: 0.35em 0.65em;
}

.btn-link {
  font-size: 0.75rem;
  color: var(--bs-info);
}

.btn-link:hover {
  color: var(--bs-info-dark);
}

/* Specific alignment for Actions header - assuming it's the last column */
:deep(.table th:last-child) {
  text-align: right;
}

:deep(.table td) {
  padding: 1rem;
  vertical-align: middle;
  border-bottom: 1px solid #e2e8f0; /* Slightly softer border color */
  text-align: left; /* Default left align for cells */
}

:deep(.table tr:hover) {
  background-color: #edf2f7; /* Slightly more distinct hover color */
}

/* Column specific styles */
:deep(.table td:nth-child(1)) { /* Name column */
  min-width: 200px;
}

:deep(.table td:nth-child(2)) { /* Username column */
  min-width: 150px;
}

:deep(.table td:nth-child(3)) { /* Email column */
  min-width: 200px;
}

:deep(.table td:nth-child(4)) { /* Roles column */
  min-width: 150px;
}

:deep(.table td:nth-child(5)) { /* Permissions column */
  min-width: 250px;
}

:deep(.table td:nth-child(6)) { /* Actions column */
  min-width: 100px;
  text-align: right;
}

/* Permissions container styles */
.permissions-container {
  position: relative;
  min-width: 150px;
}


.permission-count {
  font-size: 0.75rem;
  color: #6c757d;
  font-weight: 500;
}

.permissions-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.35rem;
  max-height: 0;
  opacity: 0;
  transform: translateY(-10px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.permissions-list.show {
  max-height: 500px;
  opacity: 1;
  transform: translateY(0);
}

.permission-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.25em 0.5em;
  font-size: 0.7rem;
  font-weight: 500;
  color: #0c5460;
  border-radius: 50rem;
  transition: all 0.2s ease-in-out;
  gap: 0.35rem;
  white-space: nowrap;
  transform: scale(0.95);
  opacity: 0;
  animation: fadeIn 0.3s cubic-bezier(0.4, 0, 0.2, 1) forwards;
}

.permissions-list.show .permission-badge {
  transform: scale(1);
  opacity: 1;
}

@keyframes fadeIn {
  from {
    transform: scale(0.95);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}


.toggle-btn {
  position: absolute;
  right: 0;
  top: 0;
  padding: 0;
  font-size: 0.75rem;
  color: #0c5460;
  border: none;
  transition: all 0.2s ease-in-out;
}

.toggle-content {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.25rem 0.5rem;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 50rem;
  transition: all 0.2s ease-in-out;
}

.toggle-btn:hover {
  color: #0a3d42;
}

.toggle-btn:hover .toggle-content {
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.toggle-btn i {
  font-size: 0.7rem;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.permissions-list.show + .toggle-btn i {
  transform: rotate(180deg);
}

/* Action buttons */
:deep(.btn-group) {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
}

:deep(.btn-group .btn) {
  padding: 0.375rem 0.5rem;
  line-height: 1;
}

:deep(.btn-group .btn i) {
  font-size: 0.875rem;
}

/* Card styles */
:deep(.card) {
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border-radius: 12px;
  margin-bottom: 1.5rem;
  overflow: hidden; /* Important for border-radius on table */
}

:deep(.card-body) {
  padding: 1.5rem;
}

/* DataList specific adjustments based on common UI patterns */
:deep(.data-list-header) {
  padding: 1.5rem;
  border-bottom: 1px solid #e9ecef;
  background-color: #ffffff;
}

:deep(.data-list-footer) {
  padding: 1rem 1.5rem;
  border-top: 1px solid #e9ecef;
  background-color: #ffffff;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  :deep(.table td),
  :deep(.table th) {
    padding: 0.75rem;
  }
  
  :deep(.table td:nth-child(1)) {
    min-width: 150px;
  }
  
  :deep(.table td:nth-child(3)) {
    min-width: 150px;
  }
  
  :deep(.table td:nth-child(5)) {
    min-width: 200px;
  }
}
</style>