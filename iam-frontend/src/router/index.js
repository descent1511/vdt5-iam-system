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
import PolicyCreate from '../views/policies/PolicyCreate.vue'
import PolicyEdit from '../views/policies/PolicyEdit.vue'
import ResourceEdit from '../views/resources/ResourceEdit.vue'

// Organization Management
import OrganizationList from '../views/organization/OrganizationList.vue'
import OrganizationForm from '../views/organization/OrganizationForm.vue'

// Product Management
import ProductsList from '../views/products/ProductsList.vue'
import ProductCreate from '../views/products/ProductCreate.vue'
import ProductEdit from '../views/products/ProductEdit.vue'

// New scope components
import ScopeEdit from '../views/scopes/ScopeEdit.vue'
import ScopeCreate from '../views/scopes/ScopeCreate.vue'

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true }
  },
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
    meta: { requiresAuth: true }
  },
  {
    path: '/users/create',
    name: 'CreateUser',
    component: UserCreate,
    meta: { requiresAuth: true}
  },
  {
    path: '/users/:id/edit',
    name: 'EditUser',
    component: UserEdit,
    meta: { requiresAuth: true}
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
    name: 'resources',
    component: ResourcesList,
    meta: {
      requiresAuth: true,
      title: 'Resources'
    }
  },
  {
    path: '/resources/create',
    name: 'resource-create',
    component: ResourceEdit,
    meta: {
      requiresAuth: true,
      title: 'Create Resource'
    }
  },
  {
    path: '/resources/:id/edit',
    name: 'resource-edit',
    component: ResourceEdit,
    meta: {
      requiresAuth: true,
      title: 'Edit Resource'
    }
  },
  {
    path: '/scopes',
    name: 'Scopes',
    component: ScopesList,
    meta: { requiresAuth: true, requiredPermissions: ['SCOPE_READ'] }
  },
  {
    path: '/scopes/create',
    name: 'CreateScope',
    component: ScopeCreate,
    meta: { requiresAuth: true, requiredPermissions: ['SCOPE_CREATE'] }
  },
  {
    path: '/scopes/:id/edit',
    name: 'EditScope',
    component: ScopeEdit,
    meta: { requiresAuth: true, requiredPermissions: ['SCOPE_UPDATE'] }
  },
  // Policy routes
  {
    path: '/policies',
    name: 'Policies',
    component: () => import('../views/policies/PoliciesList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/policies/create',
    name: 'CreatePolicy',
    component: () => import('../views/policies/PolicyCreate.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/policies/:id/edit',
    name: 'EditPolicy',
    component: () => import('../views/policies/PolicyEdit.vue'),
    meta: { requiresAuth: true }
  },
  // Organization routes
  {
    path: '/organizations',
    name: 'Organizations',
    component: OrganizationList,
    meta: { requiresAuth: true}
  },
  {
    path: '/organizations/create',
    name: 'CreateOrganization',
    component: OrganizationForm,
    meta: { requiresAuth: true}
  },
  {
    path: '/organizations/:id/edit',
    name: 'EditOrganization',
    component: OrganizationForm,
    meta: { requiresAuth: true }
  },
  {
    path: '/clients',
    name: 'Clients',
    component: () => import('../views/clients/ClientsList.vue'),
    meta: {
      requiresAuth: true
    }
  },
  {
    path: '/clients/create',
    name: 'CreateClient',
    component: () => import('../views/clients/ClientCreate.vue'),
    meta: {
      requiresAuth: true
    }
  },
  {
    path: '/clients/:id/edit',
    name: 'EditClient',
    component: () => import('../views/clients/ClientEdit.vue'),
    meta: {
      requiresAuth: true
    }
  },
  // Product routes
  {
    path: '/products',
    name: 'Products',
    component: () => import('../views/products/ProductsList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/products/create',
    name: 'CreateProduct',
    component: () => import('../views/products/ProductCreate.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/products/:id/edit',
    name: 'EditProduct',
    component: () => import('../views/products/ProductEdit.vue'),
    meta: { requiresAuth: true }
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