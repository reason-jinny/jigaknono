<template>
  <div class="container">
    <h1 class="title">출발지와 도착 시간을 선택하세요!</h1>
    <div class="form-group">
      <label class="label">출발지</label>
      <select v-model="currentLocation" class="input">
        <option value="">출발지를 선택하세요</option>
        <option value="판교역">판교역</option>
        <option value="청계산입구역">청계산입구역</option>
      </select>
    </div>
    <div class="form-group">
      <label class="label">목표 도착 시간</label>
      <input type="time" v-model="targetArrivalTimeStr" class="input" />
    </div>
    <button @click="getRecommendation" class="button-primary">경로 추천받기</button>

    <div v-if="recommendation" class="card">
      <h2 class="card-header">추천 경로</h2>
      <div class="card-body">
        <p><strong>출발 시간:</strong> {{ recommendation.departureTime }}</p>
        <p><strong>도착 시간:</strong> {{ recommendation.arrivalTime }}</p>
        <p><strong>경로:</strong> {{ recommendation.recommendedRoute }}</p>
      </div>
    </div>

    <div v-if="error" class="alert-error">
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

<style scoped>
/* 컴포넌트 내에서만 적용되는 스타일 */
.container {
    max-width: 600px;
    margin: 0 auto;
    padding: 20px;
    background-color: #f8f9fa;
    border-radius: 8px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.title {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 20px;
    text-align: center;
}

.form-group {
    margin-bottom: 15px;
}

.label {
    display: block;
    font-size: 14px;
    margin-bottom: 5px;
}

.input {
    width: 100%;
    padding: 8px;
    border: 1px solid #e2e8f0;
    border-radius: 4px;
    margin-bottom: 10px;
    transition: border-color 0.2s;
}

.input:focus {
    border-color: #3b82f6;
    outline: none;
}

.button-primary {
    display: block;
    width: 100%;
    background-color: #3b82f6;
    color: white;
    padding: 10px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.2s;
    margin-bottom: 20px;
}

.button-primary:hover {
    background-color: #2563eb;
}

.card {
    background-color: white;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
    padding: 15px;
    margin-bottom: 20px;
}

.card-header {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 10px;
}

.card-body {
    font-size: 14px;
    color: #4a5568;
}

.alert-error {
    background-color: #fef2f2;
    border: 1px solid #fee2e2;
    color: #dc2626;
    padding: 10px;
    border-radius: 4px;
    margin-bottom: 20px;
}
</style>
