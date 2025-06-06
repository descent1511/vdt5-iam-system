<template>
  <div class="access-denied-container d-flex align-items-center justify-content-center text-center">
    <div>
      <h1 class="display-1 text-danger">403</h1>
      <h2 class="mb-4">Access Denied</h2>
      <p class="lead">Sorry, you don't have permission to access this page.</p>
      <router-link to="/" class="btn btn-primary mt-3">Go to Dashboard</router-link>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.access-denied-container {
  height: 80vh;
}
</style> 