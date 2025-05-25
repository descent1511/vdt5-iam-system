import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useSidebarStore = defineStore('sidebar', () => {
  // Default to open on desktop, closed on mobile
  const isOpen = ref(window.innerWidth >= 992)
  
  function toggle() {
    isOpen.value = !isOpen.value
  }
  
  function close() {
    isOpen.value = false
  }
  
  function open() {
    isOpen.value = true
  }
  
  return {
    isOpen,
    toggle,
    close,
    open
  }
})