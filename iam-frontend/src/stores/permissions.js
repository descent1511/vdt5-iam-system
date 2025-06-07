import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { permissionService } from '../services/permissionService';

function formatEntityName(key) {
  if (!key) return '';
  const name = key.toLowerCase().replace(/_/g, ' ');
  return name.charAt(0).toUpperCase() + name.slice(1) + ' Management';
}

export const usePermissionStore = defineStore('permissions', () => {
  // State
  const allPermissions = ref([]);
  const loading = ref(false);
  const error = ref(null);

  // Getters
  const entities = computed(() => {
    const entityKeys = new Set(allPermissions.value.map(p => {
      const parts = p.split('_');
      parts.pop(); // Remove action part
      return parts.join('_');
    }));
    return Array.from(entityKeys).map(key => ({
      key,
      name: formatEntityName(key),
      icon: 'bi-gear' // Default icon, can be mapped later
    }));
  });

  const actions = computed(() => {
    const actionKeys = new Set(allPermissions.value.map(p => p.split('_').pop()));
    return Array.from(actionKeys);
  });

  // Actions
  async function fetchAllPermissions() {
    if (allPermissions.value.length > 0) return; // Fetch only once
    
    loading.value = true;
    error.value = null;
    try {
      const response = await permissionService.getPermissions();
      console.log("response", response);
      allPermissions.value = response.map(p => p.name); // Assuming API returns {id, name}
    } catch (err) {
      error.value = err;
      console.error('Failed to fetch permissions:', err);
    } finally {
      loading.value = false;
    }
  }

  return {
    allPermissions,
    loading,
    error,
    entities,
    actions,
    fetchAllPermissions,
  };
}); 