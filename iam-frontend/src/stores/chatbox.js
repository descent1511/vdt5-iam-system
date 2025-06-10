import { defineStore } from 'pinia';
import aiService from '../services/aiService';

export const useChatboxStore = defineStore('chatbox', {
  state: () => ({
    isVisible: false,
    messages: [],
    selectedEntityType: 'role', // Default selected entity type
    entityTypes: [
      { label: 'Role', value: 'role' },
      { label: 'Client', value: 'client' }
      // Add more entity types here as needed
    ],
    isLoading: false, // New loading state
  }),
  actions: {
    toggleChatbox() {
      this.isVisible = !this.isVisible;
    },
    addMessage(message) {
      this.messages.push(message);
    },
    setSelectedEntityType(type) {
      this.selectedEntityType = type;
    },
    async sendMessage(userDescription) {
      this.addMessage({ sender: 'user', text: userDescription });
      this.isLoading = true; // Set loading to true

      try {
        const response = await aiService.generateEntity(this.selectedEntityType, userDescription);
        this.addMessage({ sender: 'ai', json: response });
      } catch (error) {
        console.error('Error generating AI response:', error);
        this.addMessage({ sender: 'ai', text: 'Error: Could not connect to AI service or parse response.' });
      } finally {
        this.isLoading = false; // Set loading to false regardless of success or failure
      }
    },
  },
}); 