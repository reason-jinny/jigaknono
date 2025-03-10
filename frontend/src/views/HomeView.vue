<template>
  <div class="container">
    <h1 class="title">지각노노: KT판교빌딩 출근 도우미</h1>
    <h3 class="label">출발지와 도착 시간을 선택하세요!</h3>
    <div class="form-group">
      <div class="form-group">
        <label class="label"><p><strong>출발지</strong></p></label>
          <div>
            <label>
              <input type="radio" value="청계산입구역" v-model="currentLocation" />
              청계산입구역
            </label>
            <label>
              <input type="radio" value="판교역" v-model="currentLocation" />
              판교역
            </label>
          </div>
        </div>
      </div>
    <div class="form-group">
      <label class="label"><p><strong>목표 도착 시간</strong></p></label>
      <input type="time" v-model="targetArrivalTimeStr" class="input" />
    </div>
    <div class="button-container">
      <button @click="getRecommendation" class="button-primary">경로 추천받기</button>
    </div>
    <div v-if="recommendation" class="card">
      <h2 class="card-header">추천 경로</h2>
      <div class="card-body">
        <p><strong>출발 시간:</strong> {{ formatTime(recommendation.departureTime) }}</p>
        <p><strong>도착 시간:</strong> {{ formatTime(recommendation.arrivalTime) }}</p>
        <p><strong>{{ formatTime(recommendation.departureTime) }}</strong>까지
          <strong>{{ recommendation.startLocation }}</strong>에 도착하셔서,
          <strong>{{ recommendation.routeNumber }}{{ isNumber(recommendation.routeNumber) ? '번 버스' : '' }}</strong>{{ getJosa(recommendation.routeNumber) }} 탑승하세요!
        </p>
        <p>이거 놓치면 지각~😖🔥🔥🔥</p>
      </div>
    </div>
    <div v-if="error" class="alert-error">
      <p>{{ error }}</p>
    </div>
    <div class="button-container">
      <button @click="openNaverMapToCheonggye" class="nav-button">
        우리집에서 청계산입구역까지는 얼마나 걸리지?
      </button>
      <button @click="openNaverMapToPangyo" class="nav-button">
        우리집에서 판교역까지는 얼마나 걸리지?
      </button>
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
    formatTime(timeStr) {
      // timeStr가 존재하면 앞의 5글자(예: "08:30:00" -> "08:30")만 반환합니다.
      return timeStr ? timeStr.slice(0, 5) : '';
    },
    isNumber(value) {
      return !isNaN(value) && typeof value !== 'boolean';
    },
    getJosa(text) {
      if (typeof text === 'number') return '를';
      
      const lastChar = text.toString().charAt(text.length - 1);
      return (lastChar.charCodeAt(0) - 0xAC00) % 28 > 0 ? '을' : '를';
    },
    openNaverMapToCheonggye() {
      // 🟢 청계산입구역 링크
      const url = "https://map.naver.com/p/directions/-/14143624.0533892,4501872.9388012,%EC%B2%AD%EA%B3%84%EC%82%B0%EC%9E%85%EA%B5%AC%EC%97%AD1%EB%B2%88%EC%B6%9C%EA%B5%AC,21406671,PLACE_POI/-/transit?c=15.00,0,0,0,dh";
      window.open(url, "_blank");
    },
    openNaverMapToPangyo() {
      // 🟢 판교역 링크
      const url = "https://map.naver.com/p/directions/-/14149986.2851145,4494348.5212732,%ED%8C%90%EA%B5%90%EC%97%AD(%ED%8C%90%EA%B5%90%ED%85%8C%ED%81%AC%EB%85%B8%EB%B0%B8%EB%A6%AC)1%EB%B2%88%EC%B6%9C%EA%B5%AC,21405017,PLACE_POI/-/transit?c=17.16,0,0,0,dh";
      window.open(url, "_blank");
    },
  },
};
</script>

<style scoped>
.container {
    max-width: 600px;
    margin: 0 auto;
    padding: 20px;
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.title {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 20px;
    text-align: center;
    color: black;
}

.form-group {
    margin-bottom: 20px;
}

.label {
    display: block;
    font-size: 16px;
    margin-bottom: 8px;
    color: black;
}

.input {
    width: 100%;
    max-width: 200px;
}

/* 라디오 버튼 그룹 스타일링 */
.radio-group {
    display: flex;
    gap: 20px;
    margin: 10px 0;
}

.radio-group label {
    display: flex;
    align-items: center;
    gap: 5px;
    cursor: pointer;
}

/* 버튼 컨테이너 */
.button-container {
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin-top: 20px;
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
