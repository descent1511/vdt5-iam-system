<template>
  <div id="app">
    <AuthLayout v-if="isAuthRoute" />
    <DefaultLayout v-else />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AuthLayout from './layouts/AuthLayout.vue'
import DefaultLayout from './layouts/DefaultLayout.vue'

const route = useRoute()

// Check if current route is an authentication route
const isAuthRoute = computed(() => {
  return route.meta.layout === 'auth'
})
</script>

<style>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
</style>