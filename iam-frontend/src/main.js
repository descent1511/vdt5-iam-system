import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'
import Toast from 'vue-toastification'
import 'vue-toastification/dist/index.css'
import './assets/scss/main.scss'
import 'bootstrap-icons/font/bootstrap-icons.css'
import { useAuthStore } from './stores/auth'

// Import all Bootstrap JS
import * as bootstrap from 'bootstrap'

const app = createApp(App)
const pinia = createPinia()

// Configure toast notifications
const toastOptions = {
  position: 'top-right',
  timeout: 3000,
  closeOnClick: true,
  pauseOnFocusLoss: true,
  pauseOnHover: true,
  draggable: true,
  draggablePercent: 0.6
}

// Make bootstrap available globally
window.bootstrap = bootstrap

app.use(pinia)
app.use(router)
app.use(Toast, toastOptions)

// Initialize auth store
const authStore = useAuthStore()
authStore.init().then(() => {
app.mount('#app')
})