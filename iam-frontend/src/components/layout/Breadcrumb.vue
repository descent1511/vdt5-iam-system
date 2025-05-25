<template>
  <nav aria-label="breadcrumb">
    <ol class="breadcrumb bg-transparent mb-0 p-0">
      <li class="breadcrumb-item">
        <router-link to="/" class="text-decoration-none"></router-link>
      </li>
      
      <li 
        v-for="(crumb, index) in breadcrumbs" 
        :key="index"
        class="breadcrumb-item"
        :class="{ 'active': index === breadcrumbs.length - 1 }"
      >
        <router-link 
          v-if="index < breadcrumbs.length - 1" 
          :to="crumb.path"
          class="text-decoration-none"
        >
          {{ crumb.name }}
        </router-link>
        <span v-else>{{ crumb.name }}</span>
      </li>
    </ol>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

// Generate breadcrumbs based on current route
const breadcrumbs = computed(() => {
  const crumbs = []
  
  // Skip if on home page
  if (route.path === '/') return crumbs
  
  // Get route matched records
  const matched = route.matched
  
  // Process each matched route
  matched.forEach((record, index) => {
    // Skip the root route
    if (index === 0 && record.path === '/') return
    
    // Get the route name
    let name = record.name
    
    // Format the name
    if (name) {
      // Convert camelCase to spaces
      name = name.replace(/([A-Z])/g, ' $1')
      
      // Capitalize first letter
      name = name.charAt(0).toUpperCase() + name.slice(1)
      
      // Remove List, Create, Edit suffixes
      name = name.replace('List', '')
      name = name.replace('Create', '')
      name = name.replace('Edit', '')
      
      // Handle action words
      if (route.path.includes('/create')) {
        name += ' Create'
      } else if (route.path.includes('/edit')) {
        name += ' Edit'
      }
      
      // Trim extra spaces
      name = name.trim()
    }
    
    // Build path up to this point
    let path = ''
    for (let i = 0; i <= index; i++) {
      path += matched[i].path.replace(/:[^/]+/g, '') // Remove route params
    }
    
    // Add to breadcrumbs
    crumbs.push({
      name,
      path
    })
  })
  
  return crumbs
})
</script>

<style scoped>
.breadcrumb-item.active {
  color: #6c757d;
}
</style>