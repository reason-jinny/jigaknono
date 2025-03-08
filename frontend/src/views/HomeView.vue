<template>
  <div class="container mx-auto p-5">
    <h1 class="text-2xl font-bold mb-4">출발지와 도착 시간을 선택하세요!</h1>
    <div class="mb-4">
      <label class="block text-sm font-medium mb-1">출발지</label>
      <select v-model="currentLocation" class="border p-2 rounded">
        <option value="">출발지를 선택하세요</option>
        <option value="판교역">판교역</option>
        <option value="청계산입구역">청계산입구역</option>
      </select>
    </div>
    <div class="mb-4">
      <label class="block text-sm font-medium mb-1">목표 도착 시간</label>
      <input type="time" v-model="targetArrivalTimeStr" class="border p-2 rounded" />
    </div>
    <button @click="getRecommendation" class="bg-blue-500 text-white p-2 rounded">경로 추천받기</button>
    <div v-if="recommendation" class="mt-5 p-4 border rounded bg-gray-50">
      <h2 class="text-xl font-bold mb-2">추천 경로</h2>
      <p><strong>출발 시간:</strong> {{ recommendation.departureTime }}</p>
      <p><strong>도착 시간:</strong> {{ recommendation.arrivalTime }}</p>
      <p><strong>경로:</strong> {{ recommendation.recommendedRoute }}</p>
    </div>
    <div v-if="error" class="mt-5 p-4 border rounded bg-red-50 text-red-600">
      <p>{{ error }}</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      currentLocation: '',
      targetArrivalTimeStr: '',
      recommendation: null,
      error: null,
    };
  },
  methods: {
    async getRecommendation() {
      try {
        const response = await axios.get('http://localhost:8080/api/recommendation', {
          params: {
            currentLocation: this.currentLocation,
            targetArrivalTimeStr: this.targetArrivalTimeStr,
          },
        });
        this.recommendation = response.data;
        this.error = null;
      } catch (err) {
        this.error = err.response?.data?.message || '추천 경로를 찾을 수 없습니다.';
        this.recommendation = null;
      }
    },
  },
};
</script>
