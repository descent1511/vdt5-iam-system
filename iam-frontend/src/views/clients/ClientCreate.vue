<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h2 mb-0">Create Client</h1>
      <router-link to="/clients" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left me-2"></i> Back to List
      </router-link>
    </div>

    <div class="card">
      <div class="card-body">
        <form @submit.prevent="handleSubmit">
          <div class="row g-3">
            <!-- Basic Information -->
            <div class="col-12 mb-3">
              <h5 class="border-bottom pb-2">Basic Information</h5>
            </div>

            <div class="col-md-6">
              <div class="form-group">
                <label for="clientName" class="form-label">Client Name*</label>
                <input type="text" class="form-control" id="clientName" v-model="form.clientName">
                <div v-if="errors.clientName" class="text-danger small mt-1">{{ errors.clientName }}</div>
              </div>
            </div>

            <div class="col-12">
              <div class="form-group">
                <label for="description" class="form-label">Description</label>
                <textarea class="form-control" id="description" v-model="form.description" rows="3"></textarea>
              </div>
            </div>

            <div class="col-12">
              <div class="form-group">
                <label class="form-label">Redirect URIs*</label>
                <div v-for="(uri, index) in form.redirectUris" :key="index" class="input-group mb-2">
                  <input type="url" class="form-control" v-model="form.redirectUris[index]" placeholder="https://app.example.com/callback">
                  <button class="btn btn-outline-danger btn-sm" type="button" @click="removeUri(index)" :disabled="form.redirectUris.length <= 1">&times;</button>
                </div>
                <div v-if="errors.redirectUris" class="text-danger small mt-1">{{ errors.redirectUris }}</div>
                <button class="btn btn-outline-primary btn-sm" type="button" @click="addUri">+ Add URI</button>
              </div>
            </div>
            
            <!-- Authentication & Authorization -->
            <div class="col-12 mb-3 mt-4">
              <h5 class="border-bottom pb-2">Authentication & Authorization</h5>
            </div>

            <div class="col-md-4">
              <div class="form-group">
                <label class="form-label">Allowed Grant Types*</label>
                <div v-if="errors.authorizationGrantTypes" class="text-danger small mb-2">{{ errors.authorizationGrantTypes }}</div>
                <div v-for="grant in availableGrantTypes" :key="grant.value" class="form-check">
                  <input class="form-check-input" type="checkbox" :value="grant.value" :id="grant.value" v-model="form.authorizationGrantTypes">
                  <label class="form-check-label" :for="grant.value">{{ grant.text }}</label>
                </div>
              </div>
            </div>
            
            <div class="col-md-4">
              <div class="form-group">
                <label class="form-label">Token Endpoint Authentication Methods*</label>
                <div v-if="errors.clientAuthenticationMethods" class="text-danger small mb-2">{{ errors.clientAuthenticationMethods }}</div>
                <div v-for="method in availableAuthMethods" :key="method.value" class="form-check">
                  <input class="form-check-input" type="checkbox" :value="method.value" :id="method.value" v-model="form.clientAuthenticationMethods">
                  <label class="form-check-label" :for="method.value">{{ method.text }}</label>
                </div>
              </div>
            </div>

            <div class="col-md-4">
              <div class="form-group">
                <label class="form-label">Allowed Scopes*</label>
                <div v-if="errors.scopeIds" class="text-danger small mb-2">{{ errors.scopeIds }}</div>
                <div v-for="scope in availableScopes" :key="scope.id" class="form-check">
                  <input class="form-check-input" type="checkbox" :value="scope.id" :id="'scope-' + scope.id" v-model="form.scopeIds">
                  <label class="form-check-label" :for="'scope-' + scope.id">{{ scope.name }}</label>
                </div>
              </div>
            </div>

            <!-- Submit buttons -->
            <div class="col-12 mt-4 d-flex justify-content-end gap-2">
                <router-link to="/clients" class="btn btn-outline-secondary">Cancel</router-link>
                <button type="submit" class="btn btn-primary" :disabled="clientStore.loading">
                    <span v-if="clientStore.loading" class="spinner-border spinner-border-sm me-2"></span>
                    Create Client
                </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useClientStore } from '../../stores/clients';
import { useScopeStore } from '../../stores/scopes';
import { useToast } from 'vue-toastification';

const router = useRouter();
const clientStore = useClientStore();
const scopeStore = useScopeStore();
const toast = useToast();

const form = reactive({
  clientName: '',
  description: '',
  redirectUris: [''],
  authorizationGrantTypes: [],
  clientAuthenticationMethods: [],
  scopeIds: [],
});

const errors = reactive({});

const availableScopes = ref([]);
const availableGrantTypes = ref([
  { value: 'authorization_code', text: 'Authorization Code' },
  { value: 'refresh_token', text: 'Refresh Token' },
  { value: 'client_credentials', text: 'Client Credentials' },
]);
const availableAuthMethods = ref([
  { value: 'client_secret_basic', text: 'Client Secret Basic' },
  { value: 'client_secret_post', text: 'Client Secret Post' },
  { value: 'none', text: 'None (for public clients)' },
]);

onMounted(async () => {
  try {
    await scopeStore.fetchScopes();
    const oidcScopes = [
        { id: 'openid', name: 'openid', description: 'Enables OpenID Connect authentication.' },
        { id: 'profile', name: 'profile', description: 'Grants access to user profile information.' }
    ];
    availableScopes.value = [...oidcScopes, ...scopeStore.scopes];
  } catch (error) {
    toast.error('Failed to load scopes.');
  }
});

function addUri() {
  if (form.redirectUris.length < 10) {
    form.redirectUris.push('');
  }
}
function removeUri(index) {
  if (form.redirectUris.length > 1) {
    form.redirectUris.splice(index, 1);
  }
}

function validateForm() {
    Object.keys(errors).forEach(key => { errors[key] = ''; });
    let isValid = true;

    if (!form.clientName.trim()) {
        errors.clientName = 'Client Name is required.';
        isValid = false;
    }

    const validUris = form.redirectUris.map(uri => uri.trim()).filter(uri => uri);
    if (validUris.length === 0) {
        errors.redirectUris = 'At least one Redirect URI is required.';
        isValid = false;
    } else {
        const urlPattern = /^(https?:\/\/[^\s$.?#].[^\s]*)$/i;
        for (const uri of validUris) {
            if (!urlPattern.test(uri)) {
                errors.redirectUris = 'One or more Redirect URIs are invalid.';
                isValid = false;
                break;
            }
        }
    }

    if (form.authorizationGrantTypes.length === 0) {
        errors.authorizationGrantTypes = 'At least one Grant Type must be selected.';
        isValid = false;
    }

    if (form.clientAuthenticationMethods.length === 0) {
        errors.clientAuthenticationMethods = 'At least one Auth Method must be selected.';
        isValid = false;
    }

    if (form.scopeIds.length === 0) {
        errors.scopeIds = 'At least one Scope must be selected.';
        isValid = false;
    }

    return isValid;
}

async function handleSubmit() {
  if (!validateForm()) {
    toast.warning('Please fix the errors before submitting.');
    return;
  }

  const payload = { ...form, redirectUris: form.redirectUris.map(uri => uri.trim()).filter(uri => uri) };
  try {
    await clientStore.createClient(payload);
    toast.success('Client created successfully!');
    router.push('/clients');
  } catch (error) {
    const errorData = error.response?.data?.errors;
    if (errorData) {
      Object.assign(errors, errorData);
      toast.error('Failed to create client. Please check the form for errors.');
    } else {
      toast.error(error.message || 'Failed to create client.');
    }
  }
}
</script>

<style scoped>
.card {
  border: none;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.08);
  border-radius: 12px;
}

.form-label {
  font-weight: 500;
  color: #2c3e50;
}
</style> 