import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

// Import components
import Login from '../views/auth/Login.vue'
import Signup from '../views/auth/Signup.vue'
import Dashboard from '../views/Dashboard.vue'
import Profile from '../views/profile/Profile.vue'
import NotFound from '../views/NotFound.vue'

// User Management
import UsersList from '../views/users/UsersList.vue'
import UserCreate from '../views/users/UserCreate.vue'
import UserEdit from '../views/users/UserEdit.vue'

// Role Management  
import RolesList from '../views/roles/RolesList.vue'
import RoleCreate from '../views/roles/RoleCreate.vue'
import RoleEdit from '../views/roles/RoleEdit.vue'

// Resource Management
import ResourcesList from '../views/resources/ResourcesList.vue'
import ScopesList from '../views/scopes/ScopesList.vue'
import PoliciesList from '../views/policies/PoliciesList.vue'

// Organization Management
import Organization from '../views/organization/Organization.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { layout: 'auth', requiresAuth: false }
  },
  {
    path: '/signup',
    name: 'Signup',
    component: Signup,
    meta: { layout: 'auth', requiresAuth: false }
  },
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile', 
    component: Profile,
    meta: { requiresAuth: true }
  },
  // User routes
  {
    path: '/users',
    name: 'Users',
    component: UsersList,
    meta: { requiresAuth: true, requiredPermissions: ['USER_READ'] }
  },
  {
    path: '/users/create',
    name: 'CreateUser',
    component: UserCreate,
    meta: { requiresAuth: true, requiredPermissions: ['USER_WRITE'] }
  },
  {
    path: '/users/:id/edit',
    name: 'EditUser',
    component: UserEdit,
    meta: { requiresAuth: true, requiredPermissions: ['USER_UPDATE'] }
  },
  // Role routes
  {
    path: '/roles',
    name: 'Roles',
    component: RolesList,
    meta: { requiresAuth: true, requiredPermissions: ['ROLE_READ'] }
  },
  {
    path: '/roles/create',
    name: 'CreateRole',
    component: RoleCreate,
    meta: { requiresAuth: true, requiredPermissions: ['ROLE_CREATE'] }
  },
  {
    path: '/roles/:id/edit',
    name: 'EditRole',
    component: RoleEdit,
    meta: { requiresAuth: true, requiredPermissions: ['ROLE_UPDATE'] }
  },
  // Resource Management routes
  {
    path: '/resources',
    name: 'Resources',
    component: ResourcesList,
    meta: { requiresAuth: true, requiredPermissions: ['RESOURCE_READ'] }
  },
  {
    path: '/scopes',
    name: 'Scopes',
    component: ScopesList,
    meta: { requiresAuth: true, requiredPermissions: ['SCOPE_READ'] }
  },
  {
    path: '/policies',
    name: 'Policies',
    component: PoliciesList,
    meta: { requiresAuth: true, requiredPermissions: ['POLICY_READ'] }
  },
  // Organization route
  {
    path: '/organization',
    name: 'Organization',
    component: Organization,
    meta: { requiresAuth: true, requiredPermissions: ['ORGANIZATION_READ'] }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  // const requiredPermissions = to.meta.requiredPermissions || []

  if (requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } 
   else if (to.path === '/login' && authStore.isAuthenticated) {
    next('/')
  } else {
    next()
  }
})

export default router