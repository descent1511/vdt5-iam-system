import { defineStore } from 'pinia'

export const useThemeStore = defineStore('theme', {
  state: () => ({
    isDark: localStorage.getItem('theme') === 'dark'
  }),
  
  actions: {
    toggleTheme() {
      this.isDark = !this.isDark
      localStorage.setItem('theme', this.isDark ? 'dark' : 'light')
      this.applyTheme()
    },
    
    initTheme() {
      // Check if user has a saved preference
      const savedTheme = localStorage.getItem('theme')
      if (savedTheme) {
        this.isDark = savedTheme === 'dark'
      } else {
        // If no saved preference, check system preference
        this.isDark = window.matchMedia('(prefers-color-scheme: dark)').matches
        localStorage.setItem('theme', this.isDark ? 'dark' : 'light')
      }
      this.applyTheme()
    },
    
    applyTheme() {
      document.documentElement.setAttribute('data-bs-theme', this.isDark ? 'dark' : 'light')
    }
  }
}) 