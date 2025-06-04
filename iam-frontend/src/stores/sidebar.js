import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useSidebarStore = defineStore('sidebar', {
  state: () => ({
    menuItems: [
      {
        title: 'Dashboard',
        icon: 'bi-speedometer2',
        route: '/dashboard'
      },
      {
        title: 'Users',
        icon: 'bi-people',
        route: '/users'
      },
      {
        title: 'Roles',
        icon: 'bi-person-badge',
        route: '/roles'
      },
      {
        title: 'Policies',
        icon: 'bi-shield-lock',
        route: '/policies'
      },
      {
        title: 'Resources',
        icon: 'bi-box',
        route: '/resources'
      },
      {
        title: 'Scopes',
        icon: 'bi-diagram-3',
        route: '/scopes'
      },
      {
        title: 'Clients',
        icon: 'bi-app',
        route: '/clients'
      },
      {
        title: 'Organizations',
        icon: 'bi-building',
        route: '/organizations'
      }
    ]
  })
})