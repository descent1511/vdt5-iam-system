<template>
  <footer class="footer bg-white mt-auto py-3 border-top">
    <div class="container-fluid">
      <div class="row align-items-center">
        <div class="col-md-6 text-center text-md-start">
          <span class="text-muted">
            &copy; {{ currentYear }} Access Manager. All rights reserved.
          </span>
        </div>
        <div class="col-md-6 text-center text-md-end">
          <span class="text-muted">Version 0.1.0</span>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { computed } from 'vue'

const currentYear = computed(() => new Date().getFullYear())
</script>

<style scoped>
.footer {
  font-size: 0.875rem;
}
</style>