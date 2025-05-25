<template>
  <div class="auth-layout d-flex align-items-center justify-content-center min-vh-100">
    <div class="container">
      <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6 col-xl-5">
          <div class="text-center mb-4">
            <h1 class="display-5 fw-bold text-primary">Access Manager</h1>
            <p class="text-muted">Identity and Access Management</p>
          </div>
          <div class="card shadow-lg">
            <div class="card-body p-4 p-lg-5">
              <router-view></router-view>
            </div>
          </div>
          <div class="text-center mt-4 text-muted">
            <small>&copy; {{ currentYear }} Access Manager. All rights reserved.</small>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const currentYear = computed(() => new Date().getFullYear())
</script>

<style scoped>
.auth-layout {
  background-color: #f8f9fa;
  background-image: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}
</style>