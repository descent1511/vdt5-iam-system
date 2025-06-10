import axios from 'axios';
import api from './api'
const aiService = {

  async generateEntity(entityType, userDescription) {
    try {
      const response = await api.post('/api/v1/ai/generate-entity', {
        entityType: entityType,
        userDescription: userDescription,
      });
      console.log('AI Service Response:', response);
      return response;
    } catch (error) {
      console.error('Error in aiService.generateEntity:', error);
      throw error; // Re-throw to be handled by the calling component/store
    }
  },
};

export default aiService; 

