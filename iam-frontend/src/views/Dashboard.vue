<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Dashboard</h1>
      
      <button class="btn btn-outline-primary">
        <i class="bi bi-download me-2"></i> Export
      </button>
    </div>
    
    <!-- Stats cards -->
    <div class="row g-3 mb-4">
      <div class="col-md-6 col-lg-3">
        <div class="card h-100">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-center">
              <div>
                <h6 class="text-muted mb-2">Total Users</h6>
                <h2 class="mb-0">{{ userStore.totalUsers }}</h2>
              </div>
              <div class="bg-primary bg-opacity-10 p-3 rounded">
                <i class="bi bi-people text-primary fs-3"></i>
              </div>
            </div>
            <div class="mt-3 small">
              <span class="text-success">
                <i class="bi bi-arrow-up me-1"></i>
                +12%
              </span>
              <span class="text-muted ms-2">from last month</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-md-6 col-lg-3">
        <div class="card h-100">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-center">
              <div>
                <h6 class="text-muted mb-2">Active Roles</h6>
                <h2 class="mb-0">{{ roleStore.totalRoles }}</h2>
              </div>
              <div class="bg-success bg-opacity-10 p-3 rounded">
                <i class="bi bi-person-badge text-success fs-3"></i>
              </div>
            </div>
            <div class="mt-3 small">
              <span class="text-success">
                <i class="bi bi-arrow-up me-1"></i>
                +3%
              </span>
              <span class="text-muted ms-2">from last month</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-md-6 col-lg-3">
        <div class="card h-100">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-center">
              <div>
                <h6 class="text-muted mb-2">Resources</h6>
                <h2 class="mb-0">{{ resourceStore.totalResources }}</h2>
              </div>
              <div class="bg-warning bg-opacity-10 p-3 rounded">
                <i class="bi bi-hdd-rack text-warning fs-3"></i>
              </div>
            </div>
            <div class="mt-3 small">
              <span class="text-success">
                <i class="bi bi-arrow-up me-1"></i>
                +7%
              </span>
              <span class="text-muted ms-2">from last month</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-md-6 col-lg-3">
        <div class="card h-100">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-center">
              <div>
                <h6 class="text-muted mb-2">Policies</h6>
                <h2 class="mb-0">{{ policyStore.totalPolicies }}</h2>
              </div>
              <div class="bg-info bg-opacity-10 p-3 rounded">
                <i class="bi bi-shield-check text-info fs-3"></i>
              </div>
            </div>
            <div class="mt-3 small">
              <span class="text-danger">
                <i class="bi bi-arrow-down me-1"></i>
                -2%
              </span>
              <span class="text-muted ms-2">from last month</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Recent activity and user sections -->
    <div class="row g-4">
      <!-- Recent activities -->
      <div class="col-lg-8">
        <div class="card mb-4">
          <div class="card-header d-flex justify-content-between align-items-center">
            <h5 class="card-title mb-0">Recent Activities</h5>
            <div class="dropdown">
              <button class="btn btn-sm btn-outline-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown">
                This Week
              </button>
              <ul class="dropdown-menu dropdown-menu-end">
                <li><a class="dropdown-item" href="#">Today</a></li>
                <li><a class="dropdown-item" href="#">This Week</a></li>
                <li><a class="dropdown-item" href="#">This Month</a></li>
                <li><a class="dropdown-item" href="#">This Year</a></li>
              </ul>
            </div>
          </div>
          <div class="card-body p-0">
            <div class="list-group list-group-flush border-top">
              <div v-for="(activity, index) in recentActivities" :key="index" class="list-group-item py-3 px-4">
                <div class="d-flex">
                  <div class="flex-shrink-0">
                    <div :class="`bg-${activity.color} bg-opacity-10 p-2 rounded`">
                      <i :class="`bi bi-${activity.icon} text-${activity.color}`"></i>
                    </div>
                  </div>
                  <div class="flex-grow-1 ms-3">
                    <div class="d-flex justify-content-between align-items-start">
                      <div>
                        <p class="mb-1">{{ activity.message }}</p>
                        <small class="text-muted">by {{ activity.user }}</small>
                      </div>
                      <small class="text-muted">{{ activity.time }}</small>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="card-footer text-center">
            <button class="btn btn-link">View All Activities</button>
          </div>
        </div>
      </div>
      
      <!-- User stats -->
      <div class="col-lg-4">
        <div class="card mb-4">
          <div class="card-header">
            <h5 class="card-title mb-0">User Stats</h5>
          </div>
          <div class="card-body">
            <div class="mb-4">
              <div class="d-flex justify-content-between mb-2">
                <div>Active Users</div>
                <div class="text-muted">78%</div>
              </div>
              <div class="progress" style="height: 8px;">
                <div class="progress-bar bg-success" role="progressbar" style="width: 78%"></div>
              </div>
            </div>
            
            <div class="mb-4">
              <div class="d-flex justify-content-between mb-2">
                <div>Inactive Users</div>
                <div class="text-muted">22%</div>
              </div>
              <div class="progress" style="height: 8px;">
                <div class="progress-bar bg-warning" role="progressbar" style="width: 22%"></div>
              </div>
            </div>
            
            <div class="mb-4">
              <div class="d-flex justify-content-between mb-2">
                <div>Admin Users</div>
                <div class="text-muted">12%</div>
              </div>
              <div class="progress" style="height: 8px;">
                <div class="progress-bar bg-danger" role="progressbar" style="width: 12%"></div>
              </div>
            </div>
            
            <div class="mb-4">
              <div class="d-flex justify-content-between mb-2">
                <div>Regular Users</div>
                <div class="text-muted">66%</div>
              </div>
              <div class="progress" style="height: 8px;">
                <div class="progress-bar bg-primary" role="progressbar" style="width: 66%"></div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="card">
          <div class="card-header">
            <h5 class="card-title mb-0">Security Overview</h5>
          </div>
          <div class="card-body">
            <div class="d-flex align-items-center mb-3">
              <div class="p-2 bg-success bg-opacity-10 rounded">
                <i class="bi bi-check-circle text-success"></i>
              </div>
              <div class="ms-3">
                <h6 class="mb-0">Policy Compliance</h6>
                <small class="text-muted">All policies are up to date</small>
              </div>
            </div>
            
            <div class="d-flex align-items-center mb-3">
              <div class="p-2 bg-warning bg-opacity-10 rounded">
                <i class="bi bi-exclamation-triangle text-warning"></i>
              </div>
              <div class="ms-3">
                <h6 class="mb-0">Access Review</h6>
                <small class="text-muted">Review needed for 3 roles</small>
              </div>
            </div>
            
            <div class="d-flex align-items-center">
              <div class="p-2 bg-danger bg-opacity-10 rounded">
                <i class="bi bi-shield-exclamation text-danger"></i>
              </div>
              <div class="ms-3">
                <h6 class="mb-0">Expired Credentials</h6>
                <small class="text-muted">2 users have expired passwords</small>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../stores/users'
import { useRoleStore } from '../stores/roles'
import { usePolicyStore } from '../stores/policies'
import { useResourceStore } from '../stores/resources'

// Stores
const userStore = useUserStore()
const roleStore = useRoleStore()
const policyStore = usePolicyStore()
const resourceStore = useResourceStore()

// Recent activities
const recentActivities = ref([
  {
    message: 'New user "Jane Smith" created',
    user: 'Admin User',
    time: '2 hours ago',
    icon: 'person-plus',
    color: 'success'
  },
  {
    message: 'Role "Finance Manager" updated',
    user: 'Admin User',
    time: '5 hours ago',
    icon: 'person-badge',
    color: 'primary'
  },
  {
    message: 'New policy "API Access" created',
    user: 'John Doe',
    time: '8 hours ago',
    icon: 'shield-check',
    color: 'info'
  },
  {
    message: 'User "Robert Johnson" deleted',
    user: 'Sarah Williams',
    time: '1 day ago',
    icon: 'person-x',
    color: 'danger'
  },
  {
    message: 'Resource "/api/reports" added',
    user: 'Admin User',
    time: '2 days ago',
    icon: 'hdd-rack',
    color: 'warning'
  }
])

// Load dashboard data
async function loadDashboardData() {
  try {
    await Promise.all([
      userStore.fetchUsers(),
      roleStore.fetchRoles(),
      resourceStore.fetchResources(),
      policyStore.fetchPolicies()
    ])
  } catch (error) {
    console.error('Failed to load dashboard data:', error)
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
/* Add any component-specific styles here */
</style>