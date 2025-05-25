// Mock data for development purposes

// Generate a random ID
const generateId = () => Math.random().toString(36).substring(2, 15)

// Roles with permissions
export const mockRoles = [
  {
    id: generateId(),
    name: 'admin',
    description: 'Full system access',
    permissions: [
      'read:users', 'write:users', 'delete:users',
      'read:roles', 'write:roles', 'delete:roles',
      'read:scopes', 'write:scopes', 'delete:scopes',
      'read:policies', 'write:policies', 'delete:policies',
      'read:resources', 'write:resources', 'delete:resources',
      'read:organizations', 'write:organizations'
    ]
  },
  {
    id: generateId(),
    name: 'manager',
    description: 'Department manager access',
    permissions: [
      'read:users', 'write:users',
      'read:roles',
      'read:policies',
      'read:resources'
    ]
  },
  {
    id: generateId(),
    name: 'user',
    description: 'Basic user access',
    permissions: [
      'read:resources'
    ]
  }
]

// Users with roles and permissions
export const mockUsers = [
  {
    id: generateId(),
    username: 'admin',
    email: 'admin@example.com',
    firstName: 'Admin',
    lastName: 'User',
    role: 'admin',
    department: 'IT',
    permissions: mockRoles.find(r => r.name === 'admin').permissions,
    status: 'active',
    createdAt: '2023-01-01T00:00:00.000Z',
    lastLogin: '2023-05-10T14:30:00.000Z'
  },
  {
    id: generateId(),
    username: 'manager',
    email: 'manager@example.com',
    firstName: 'Department',
    lastName: 'Manager',
    role: 'manager',
    department: 'Sales',
    permissions: mockRoles.find(r => r.name === 'manager').permissions,
    status: 'active',
    createdAt: '2023-01-02T00:00:00.000Z',
    lastLogin: '2023-05-09T10:15:00.000Z'
  },
  {
    id: generateId(),
    username: 'user',
    email: 'user@example.com',
    firstName: 'Regular',
    lastName: 'User',
    role: 'user',
    department: 'Marketing',
    permissions: mockRoles.find(r => r.name === 'user').permissions,
    status: 'active',
    createdAt: '2023-01-03T00:00:00.000Z',
    lastLogin: '2023-05-08T09:20:00.000Z'
  }
]

// Resources
export const mockResources = [
  {
    id: generateId(),
    name: 'User Management API',
    path: '/api/users',
    description: 'User management endpoints',
    requiredPermissions: ['read:users', 'write:users', 'delete:users']
  },
  {
    id: generateId(),
    name: 'Role Management API',
    path: '/api/roles',
    description: 'Role management endpoints',
    requiredPermissions: ['read:roles', 'write:roles', 'delete:roles']
  },
  {
    id: generateId(),
    name: 'Public Resources',
    path: '/api/public',
    description: 'Publicly accessible resources',
    requiredPermissions: ['read:resources']
  }
]

// Scopes
export const mockScopes = [
  {
    id: generateId(),
    name: 'read:users',
    description: 'Read user information'
  },
  {
    id: generateId(),
    name: 'write:users',
    description: 'Create and update users'
  },
  {
    id: generateId(),
    name: 'delete:users',
    description: 'Delete users'
  },
  {
    id: generateId(),
    name: 'read:roles',
    description: 'Read role information'
  },
  {
    id: generateId(),
    name: 'write:roles',
    description: 'Create and update roles'
  },
  {
    id: generateId(),
    name: 'delete:roles',
    description: 'Delete roles'
  },
  {
    id: generateId(),
    name: 'read:resources',
    description: 'Read resource information'
  }
]

// Policies
export const mockPolicies = [
  {
    id: generateId(),
    name: 'Admin Full Access',
    description: 'Grants full access to administrators',
    effect: 'allow',
    subjects: ['admin'],
    resources: ['*'],
    actions: ['*'],
    conditions: {},
    createdAt: '2023-01-01T00:00:00.000Z',
    updatedAt: '2023-01-01T00:00:00.000Z'
  },
  {
    id: generateId(),
    name: 'Manager User Management',
    description: 'Allows managers to manage users',
    effect: 'allow',
    subjects: ['manager'],
    resources: ['/api/users'],
    actions: ['read', 'write'],
    conditions: {},
    createdAt: '2023-01-02T00:00:00.000Z',
    updatedAt: '2023-01-02T00:00:00.000Z'
  },
  {
    id: generateId(),
    name: 'User Basic Access',
    description: 'Basic access for regular users',
    effect: 'allow',
    subjects: ['user'],
    resources: ['/api/public'],
    actions: ['read'],
    conditions: {},
    createdAt: '2023-01-03T00:00:00.000Z',
    updatedAt: '2023-01-03T00:00:00.000Z'
  }
]

// Organization
export const mockOrganization = {
  id: generateId(),
  name: 'Example Organization',
  description: 'A sample organization for development',
  address: '123 Main St, City, Country',
  phone: '+1-234-567-8900',
  email: 'contact@example.org',
  website: 'https://example.org',
  createdAt: '2023-01-01T00:00:00.000Z',
  updatedAt: '2023-01-01T00:00:00.000Z'
}

// Departments
export const mockDepartments = [
  {
    id: generateId(),
    name: 'IT',
    description: 'Information Technology department',
    managerId: mockUsers.find(u => u.role === 'manager' && u.department === 'IT')?.id,
    createdAt: '2023-01-01T00:00:00.000Z',
    updatedAt: '2023-01-01T00:00:00.000Z'
  },
  {
    id: generateId(),
    name: 'Sales',
    description: 'Sales and Marketing department',
    managerId: mockUsers.find(u => u.role === 'manager' && u.department === 'Sales')?.id,
    createdAt: '2023-01-01T00:00:00.000Z',
    updatedAt: '2023-01-01T00:00:00.000Z'
  },
  {
    id: generateId(),
    name: 'Marketing',
    description: 'Marketing and Communications department',
    managerId: mockUsers.find(u => u.role === 'manager' && u.department === 'Marketing')?.id,
    createdAt: '2023-01-01T00:00:00.000Z',
    updatedAt: '2023-01-01T00:00:00.000Z'
  }
]