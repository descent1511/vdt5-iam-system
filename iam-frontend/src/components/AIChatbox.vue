<template>
  <div :class="['ai-chatbox-container', { 'is-visible': chatboxStore.isVisible }]">
    <button class="chatbox-toggle-button" @click="chatboxStore.toggleChatbox()">
      <i class="bi bi-robot"></i> AI
    </button>
    <div class="chatbox-content">
      <div class="chatbox-header">
        <h3>AI Assistant</h3>
        <button class="close-button" @click="chatboxStore.toggleChatbox()">&times;</button>
      </div>
      <div class="chatbox-messages" ref="messagesContainer">
        <div v-for="(message, index) in chatboxStore.messages" :key="index" :class="['message', message.sender]">
          <div class="message-bubble">{{ message.text }}</div>
          <div v-if="message.json" class="message-json-formatted">
            <h4>Generated {{ chatboxStore.selectedEntityType === 'role' ? 'Role' : 'Client' }} Details:</h4>
            <div v-if="message.json.name">
              <strong>Name:</strong> {{ message.json.name }}
            </div>
            <div v-if="message.json.clientName">
              <strong>Client Name:</strong> {{ message.json.clientName }}
            </div>
            <div v-if="message.json.description">
              <strong>Description:</strong> {{ message.json.description }}
            </div>
            <div v-if="message.json.permissions && message.json.permissions.length > 0">
              <strong>Permissions:</strong>
              <ul>
                <li v-for="(permission, pIndex) in message.json.permissions" :key="pIndex">{{ permission }}</li>
              </ul>
            </div>
            <div v-if="message.json.redirectUris && message.json.redirectUris.length > 0">
              <strong>Redirect URIs:</strong>
              <ul>
                <li v-for="(uri, uIndex) in message.json.redirectUris" :key="uIndex">{{ uri }}</li>
              </ul>
            </div>
            <div v-if="message.json.authorizationGrantTypes && message.json.authorizationGrantTypes.length > 0">
              <strong>Grant Types:</strong>
              <ul>
                <li v-for="(type, tIndex) in message.json.authorizationGrantTypes" :key="tIndex">{{ type }}</li>
              </ul>
            </div>
            <div v-if="message.json.clientAuthenticationMethods && message.json.clientAuthenticationMethods.length > 0">
              <strong>Auth Methods:</strong>
              <ul>
                <li v-for="(method, mIndex) in message.json.clientAuthenticationMethods" :key="mIndex">{{ method }}</li>
              </ul>
            </div>
            <div v-if="message.json.scopes && message.json.scopes.length > 0">
              <strong>Scopes:</strong>
              <ul>
                <li v-for="(scope, sIndex) in message.json.scopes" :key="sIndex">{{ scope }}</li>
              </ul>
            </div>
            <button class="confirm-button" @click="confirmCreation(message.json)">Confirm Creation</button>
          </div>
        </div>
        <div v-if="chatboxStore.isLoading" class="message ai loading-message">
          <div class="message-bubble">Thinking...</div>
        </div>
      </div>
      <div class="chatbox-input">
        <div class="input-top-row">
          <label for="entity-type-select" class="entity-type-label">Generate For:</label>
          <select id="entity-type-select" v-model="chatboxStore.selectedEntityType" class="entity-type-select" :disabled="chatboxStore.isLoading">
            <option v-for="type in chatboxStore.entityTypes" :key="type.value" :value="type.value">
              {{ type.label }}
            </option>
          </select>
        </div>
        <div class="input-and-button">
          <input
            v-model="newMessage"
            @keyup.enter="sendMessage"
            placeholder="Type your request..."
            :disabled="chatboxStore.isLoading"
          />
          <button @click="sendMessage" :disabled="chatboxStore.isLoading" class="send-button">
            <i class="bi bi-send-fill"></i>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import { useChatboxStore } from '../stores/chatbox';
import { useRoleStore } from '../stores/roles'; // Import the role store
import { useClientStore } from '../stores/clients'; // Import the client store

const chatboxStore = useChatboxStore();
const roleStore = useRoleStore(); // Initialize the role store
const clientStore = useClientStore(); // Initialize the client store
const newMessage = ref('');
const messagesContainer = ref(null);

const sendMessage = async () => {
  if (newMessage.value.trim() === '' || chatboxStore.isLoading) return;

  const userMessage = newMessage.value;
  await chatboxStore.sendMessage(userMessage); // Use the store's sendMessage action
  newMessage.value = '';
  scrollToBottom();
};

const confirmCreation = async (generatedData) => {
  console.log('Confirming creation with data:', generatedData);
  try {
    let success = false;
    if (chatboxStore.selectedEntityType === 'role') {
      // For roles, only name, description, and permissions are relevant
      const roleToCreate = {
        name: generatedData.name,
        description: generatedData.description,
        permissions: generatedData.permissions || []
      };
      success = await roleStore.createRole(roleToCreate);
    } else if (chatboxStore.selectedEntityType === 'client') {
      // For clients, map relevant fields from generatedData to client creation data
      const clientToCreate = {
        clientName: generatedData.clientName,
        description: generatedData.description,
        redirectUris: generatedData.redirectUris || [],
        authorizationGrantTypes: generatedData.authorizationGrantTypes || [],
        clientAuthenticationMethods: generatedData.clientAuthenticationMethods || [],
        scopes: generatedData.scopes || []
      };
      success = await clientStore.createClient(clientToCreate);
    }

    if (success) {
      chatboxStore.addMessage({ sender: 'ai', text: `Successfully created ${chatboxStore.selectedEntityType}.` });
    } else {
      chatboxStore.addMessage({ sender: 'ai', text: `Failed to create ${chatboxStore.selectedEntityType}. Please check backend logs.` });
    }
  } catch (error) {
    console.error('Error during confirm creation:', error);
    chatboxStore.addMessage({ sender: 'ai', text: `An error occurred while creating ${chatboxStore.selectedEntityType}.` });
  }
  scrollToBottom();
};

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    }
  });
};

onMounted(() => {
  // Initial scroll to bottom if there are pre-existing messages
  scrollToBottom();
});
</script>

<style scoped>
.ai-chatbox-container {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1000;
  font-family: Arial, sans-serif;
}

.chatbox-toggle-button {
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 50%;
  width: 60px;
  height: 60px;
  font-size: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  transition: transform 0.3s ease-in-out;
  position: absolute;
  bottom: 0;
  right: 0;
  z-index: 1001;
}

.chatbox-toggle-button:hover {
  transform: scale(1.1);
}

.chatbox-toggle-button i {
  margin-right: 5px;
}

.chatbox-content {
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  width: 400px; /* Increased width to provide more horizontal space */
  height: 450px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: absolute;
  bottom: 0;
  right: 0;
  opacity: 0;
  visibility: hidden;
  transform: translateY(10px) scale(0.95);
  transition: opacity 0.3s ease-in-out, transform 0.3s ease-in-out, visibility 0.3s;
}

.ai-chatbox-container.is-visible .chatbox-content {
  opacity: 1;
  visibility: visible;
  transform: translateY(-80px) scale(1); /* Move up and scale to reveal */
}

.chatbox-header {
  background-color: #f0f0f0;
  padding: 10px 15px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chatbox-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.chatbox-header .close-button {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #555;
}

.chatbox-messages {
  flex-grow: 1;
  padding: 15px;
  overflow-y: auto;
  background-color: #f9f9f9;
}

.message {
  margin-bottom: 12px;
  display: flex;
  flex-direction: column;
}

.message.user {
  align-items: flex-end; /* Tin nhắn của người dùng căn phải */
}

.message.ai {
  align-items: flex-start; /* Tin nhắn của bot căn trái */
}

.message-bubble {
  max-width: 360px; /* Adjusted to fixed pixel width for better control */
  padding: 10px 15px;
  border-radius: 20px;
  overflow-wrap: break-word;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  line-height: 1.4;
  box-sizing: border-box;
}

.message.user .message-bubble {
  background-color: #007bff;
  color: white;
  border-bottom-right-radius: 5px;
}

.message.ai .message-bubble {
  background-color: #f0f0f0;
  color: #333;
  border-bottom-left-radius: 5px;
}

.loading-message .message-bubble {
    animation: pulse 1s infinite alternate;
    background-color: #e0e0e0;
    color: #555;
}

@keyframes pulse {
    from { opacity: 1; }
    to { opacity: 0.6; }
}

.message-json-formatted {
  background-color: #e9ecef;
  padding: 12px;
  border-radius: 12px;
  margin-top: 8px;
  font-size: 0.88em;
  border: 1px solid #ced4da;
  max-width: 360px; /* Adjusted to fixed pixel width to match message-bubble */
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  align-self: flex-start;
  box-sizing: border-box;
}

.message.user .message-json-formatted { /* If JSON message belongs to user, align right */
  align-self: flex-end;
}

.message-json-formatted h4 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #0056b3;
  font-size: 1.1em;
}

.message-json-formatted div {
  margin-bottom: 6px;
  word-break: break-all; /* Ensure all divs break long words */
}

.message-json-formatted strong {
  color: #495057;
  display: block; /* Make strong tags block-level to push content below */
  margin-bottom: 4px; /* Add some space below the strong tag */
}

.message-json-formatted ul {
  list-style: none; /* Remove default list styling */
  margin-left: 0; /* Reset margin-left */
  padding-left: 0; /* Reset UL padding */
  margin-top: 5px;

  border-radius: 5px;
  padding-top: 5px;
  padding-bottom: 5px;
  box-sizing: border-box;
}

.message-json-formatted ul li {
  position: relative; /* For positioning the pseudo-element */
  margin-bottom: 4px;
  padding-left: 20px; /* Space for the bullet point */
  word-break: break-all; /* Force long words/URIs to break at any character */
  line-height: 1.4;
  text-indent: -15px; /* Pull the bullet left into the padding space */
  box-sizing: border-box;
}



.confirm-button {
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 10px 15px;
  margin-top: 15px;
  cursor: pointer;
  transition: background-color 0.2s, transform 0.2s;
  width: auto;
}

.message.ai .confirm-button {
  align-self: flex-start;
}

.message.user .confirm-button {
  align-self: flex-end;
}

.confirm-button:hover {
  background-color: #218838;
  transform: translateY(-2px);
}

.chatbox-input {
  display: flex;
  flex-direction: column;
  padding: 10px 15px;
  border-top: 1px solid #eee;
  background-color: #fff;
}

.input-top-row {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.entity-type-label {
  font-size: 0.9em;
  color: #555;
  margin-right: 10px;
  white-space: nowrap;
}

.chatbox-input .entity-type-select {
  width: auto; /* Cho phép select co giãn theo nội dung */
  flex-grow: 1;
  padding: 8px 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  background-color: #f5f5f5;
  appearance: none; /* Xóa style mặc định của select */
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 16 16'%3E%3Cpath fill='none' stroke='%23333' stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M2 5l6 6 6-6'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 10px center;
  background-size: 12px;
  cursor: pointer;
}

.chatbox-input .entity-type-select:focus {
  border-color: #007bff;
  outline: none;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}

.chatbox-input .input-and-button {
  display: flex;
  width: 100%;
}

.chatbox-input input {
  flex-grow: 1;
  border: 1px solid #ddd;
  border-radius: 20px;
  padding: 8px 15px;
  margin-right: 10px;
  font-size: 14px;
}

.chatbox-input .send-button {
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 50%; /* Nút tròn */
  width: 40px; /* Kích thước nhỏ hơn */
  height: 40px; /* Kích thước nhỏ hơn */
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.2s, transform 0.2s;
  padding: 0; /* Loại bỏ padding text */
  font-size: 1.2em; /* Kích thước icon */
}

.chatbox-input .send-button:hover {
  background-color: #218838;
  transform: scale(1.1); /* Hiệu ứng phóng to khi hover */
}

.chatbox-input button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.chatbox-input input:disabled {
  background-color: #f0f0f0;
  cursor: not-allowed;
}

.chatbox-input .entity-type-select:disabled {
  background-color: #f0f0f0;
  cursor: not-allowed;
}
</style> 