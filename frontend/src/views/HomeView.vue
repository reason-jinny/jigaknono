<template>
  <div class="container">
    <h1 class="title">지각노노: KT판교빌딩 출근 도우미</h1>
    <h3 class="label">출발지와 도착 시간을 선택하세요!</h3>
    <div class="form-group">
      <label class="label">
        <p>
          <strong>출발지</strong>
          <span class="required">*</span>
        </p>
      </label>
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
      <p v-if="showValidation && !currentLocation" class="validation-message">출발지를 선택해주세요</p>
    </div>
    <div class="form-group">
      <label class="label">
        <p>
          <strong>목표 도착 시간</strong>
          <span class="required">*</span>
        </p>
      </label>
      <input type="time" v-model="targetArrivalTimeStr" class="input" />
      <p v-if="showValidation && !targetArrivalTimeStr" class="validation-message">목표 도착 시간을 선택해주세요</p>
    </div>
    <div class="form-group">
      <label class="label">
        <p>
          <strong>날씨 상황</strong>
          <span class="optional">(선택사항)</span>
        </p>
      </label>
      <div class="weather-options">
        <label class="weather-option">
          <input type="checkbox" v-model="isRaining" />
          <i class="fas fa-cloud-rain"></i> 비
        </label>
        <label class="weather-option">
          <input type="checkbox" v-model="isSnowing" />
          <i class="fas fa-snowflake"></i> 눈
        </label>
      </div>
    </div>
    <div class="form-group">
      <label class="label">
        <p>
          <strong>교통수단 선택</strong>
        </p>
      </label>
      <div class="transport-options">
        <label class="transport-option">
          <input 
            type="radio" 
            v-model="transportPreference" 
            value="all" 
            name="transport"
          />
          <span class="transport-content">
            <i class="fas fa-bus"></i>
            <i class="fas fa-plus" style="font-size: 0.7em; margin: 0 4px;"></i>
            <i class="fas fa-shuttle-van"></i>
            <span class="transport-text">모든 교통편</span>
          </span>
        </label>
        <label class="transport-option">
          <input 
            type="radio" 
            v-model="transportPreference" 
            value="shuttle" 
            name="transport"
          />
          <span class="transport-content">
            <i class="fas fa-shuttle-van"></i>
            <span class="transport-text">셔틀버스만</span>
            <span class="kt-badge">KT</span>
          </span>
        </label>
      </div>
    </div>
    <div class="button-container">
      <button @click="getRecommendation" class="button-primary">경로 추천받기</button>
    </div>
    <div v-if="recommendation" class="card">
      <h2 class="card-header">추천 경로</h2>
      <div class="card-body">
        <div class="route-info-grid">
          <div class="route-info-row">
            <div class="route-info-cell">
              <strong>탑승지:</strong> {{ recommendation.startLocation }}
            </div>
            <div class="route-info-cell">
              <strong>출발 시간:</strong> {{ formatTime(recommendation.departureTime) }}
            </div>
          </div>
          <div class="route-info-row">
            <div class="route-info-cell">
              <strong>하차지:</strong> {{ recommendation.endLocation }}
              <span class="walk-info">
                <i class="fas fa-walking"></i> {{ recommendation.walkDuration }}분 도보
              </span>
            </div>
            <div class="route-info-cell">
              <strong>하차 시간:</strong> {{ formatTime(recommendation.busArrivalTime) }}
            </div>
          </div>
          <div class="route-info-row final-arrival">
            <div class="route-info-cell">
              <strong>최종 도착:</strong> KT판교빌딩
            </div>
            <div class="route-info-cell">
              <strong>도착 시간:</strong> {{ formatTime(recommendation.arrivalTime) }}
            </div>
          </div>
        </div>

        <p v-if="recommendation.weatherDelay > 0" class="weather-warning">
          <i class="fas" :class="recommendation.weatherIcon"></i>
          {{ recommendation.weatherMessage }} 인해 평소보다 {{ recommendation.weatherDelay }}분 더 여유 있는 출발 추천!
        </p>

        <p class="main-instruction">
          <strong class="highlight-time">{{ formatTimeWithKorean(recommendation.departureTime) }}</strong>까지
          <strong>{{ recommendation.startLocation }}</strong>에 도착하셔서,<br>
          <strong class="highlight-route">{{ recommendation.routeNumber }}{{ isKTShuttle(recommendation.routeNumber) ? '' : getBusText(recommendation.routeNumber) }}</strong>{{ getJosa(recommendation.routeNumber) }} 탑승하세요!
        </p>
        
        <p class="warning-message">이거 놓치면 지각~😖🔥🔥🔥</p>
        <p v-if="isLateArrivalTime" class="weird-time-message">근데 왜 이 시간에 출근을...?😱</p>
      </div>
    </div>
    <div v-if="error" class="alert-error">
      <p>{{ error }}</p>
      <p class="weird-time-message">아직 아무도 안 일어났을 시간인데...?😴</p>
    </div>
    <div class="button-container">
      <button @click="openNaverMapToCheonggye" class="nav-button">
        우리집에서 청계산입구역까지는 얼마나 걸리지?
      </button>
      <button @click="openNaverMapToPangyo" class="nav-button">
        우리집에서 판교역까지는 얼마나 걸리지?
      </button>
    </div>
    <div class="feedback-section">
      <button @click="openFeedbackModal" class="feedback-button">
        <i class="fas fa-comment-dots"></i> 교통편 정보 업데이트가 필요하신가요?
      </button>
    </div>

    <!-- Feedback Modal -->
    <div v-if="showFeedbackModal" class="modal-overlay" @click="closeFeedbackModal">
      <div class="modal-content" @click.stop>
        <h2 class="modal-title">교통편 정보 업데이트 요청</h2>
        <div class="modal-body">
          <div class="feedback-type">
            <p class="required-text">피드백 유형을 선택해주세요 <span class="required">*</span></p>
            <label>
              <input type="radio" v-model="feedbackType" value="add" required />
              신규 교통편 추가 요청
            </label>
            <label>
              <input type="radio" v-model="feedbackType" value="update" required />
              기존 교통편 정보 수정 요청
            </label>
            <label>
              <input type="radio" v-model="feedbackType" value="other" required />
              기타 문의 사항
            </label>
          </div>
          <div class="example-feedbacks" v-if="feedbackType">
            <p class="example-text">예시)</p>
            <ul>
              <li v-if="feedbackType === 'add'">판교역에서 출발하는 55번 버스 추가해주세요.</li>
              <li v-if="feedbackType === 'update'">청계산입구역에서 출발하는 셔틀 시간 변경됐어요.</li>
              <li v-if="feedbackType === 'other'">셔틀버스 탑승 위치를 알고 싶어요.</li>
            </ul>
          </div>
          <textarea
            v-model="feedbackContent"
            placeholder="상세한 내용을 입력해주세요."
            class="feedback-textarea"
            required
          ></textarea>
        </div>
        <div class="modal-footer">
          <button @click="submitFeedback" class="submit-button" :disabled="!feedbackContent.trim()">
            전송하기
          </button>
          <button @click="closeFeedbackModal" class="cancel-button">
            취소
          </button>
        </div>
      </div>
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
      submittedTargetArrivalTime: '', // 버튼 클릭 시 저장할 도착 시간
      recommendation: null,
      error: null,
      isRaining: false,
      isSnowing: false,
      showValidation: false,
      showFeedbackModal: false,
      feedbackType: '',
      feedbackContent: '',
      transportPreference: 'all', // 기본값은 모든 교통수단
    };
  },
  watch: {
    // 비가 선택되면 눈은 자동으로 해제만 하고, 추천 경로는 갱신하지 않음
    isRaining(newValue) {
      if (newValue) {
        this.isSnowing = false;
      }
    },
    // 눈이 선택되면 비는 자동으로 해제만 하고, 추천 경로는 갱신하지 않음
    isSnowing(newValue) {
      if (newValue) {
        this.isRaining = false;
      }
    },
    // 입력값이 변경되면 검증 메시지 숨김
    currentLocation() {
      if (this.showValidation && this.currentLocation) {
        this.showValidation = false;
      }
    }
  },
  computed: {
    weatherDelay() {
      if (this.isRaining || this.isSnowing) return 10;
      return 0;
    },
    isLateArrivalTime() {
      if (!this.targetArrivalTimeStr) return false;
      const [hours] = this.targetArrivalTimeStr.split(':');
      return parseInt(hours) >= 18;
    }
  },
  methods: {
    async getRecommendation() {
      // 버튼 클릭 시 기존 결과 초기화
      this.recommendation = null;
      this.error = null;
      this.showValidation = true;
      
      // 필수 입력값이 없으면 API 호출하지 않음
      if (!this.currentLocation || !this.targetArrivalTimeStr) {
        return;
      }

      try {
        // 시간 문자열을 Date 객체로 변환
        const [hours, minutes] = this.targetArrivalTimeStr.split(':');
        const targetTime = new Date();
        targetTime.setHours(parseInt(hours));
        targetTime.setMinutes(parseInt(minutes));
        targetTime.setSeconds(0);

        const response = await axios.get('http://localhost:8080/api/recommendation', {
          params: {
            currentLocation: this.currentLocation,
            targetArrivalTimeStr: this.targetArrivalTimeStr,
            weatherDelay: this.weatherDelay,
            preferShuttle: this.transportPreference === 'shuttle'
          },
        });
        
        this.recommendation = {
          ...response.data,
          weatherDelay: this.weatherDelay,
          weatherMessage: this.isRaining ? '비로' : this.isSnowing ? '눈으로' : '',
          weatherIcon: this.isRaining ? 'fa-cloud-rain' : this.isSnowing ? 'fa-snowflake' : ''
        };
        this.error = null;
        this.showValidation = false;  // 성공하면 검증 메시지 숨김
      } catch (err) {
        this.error = '너무 이른 시간에는 교통편이 없어요😭';
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
    formatTimeWithKorean(timeStr) {
      if (!timeStr) return '';
      const [hours, minutes] = timeStr.split(':');
      const hour = parseInt(hours);
      return `오${hour < 12 ? '전' : '후'} ${hour > 12 ? hour - 12 : hour}시 ${parseInt(minutes)}분`;
    },
    getBusText(routeNumber) {
      // 숫자로만 이루어진 노선인지 확인
      const isNumberOnly = /^\d+$/.test(routeNumber);
      return isNumberOnly ? '번 버스' : ' 버스';
    },
    isKTShuttle(routeNumber) {
      return routeNumber.toLowerCase().includes('kt') || routeNumber.toLowerCase().includes('셔틀');
    },
    openFeedbackModal() {
      this.showFeedbackModal = true;
      this.feedbackType = '';
      this.feedbackContent = '';
    },
    closeFeedbackModal() {
      this.showFeedbackModal = false;
    },
    async submitFeedback() {
      try {
        if (!this.feedbackType || !this.feedbackContent.trim()) {
          alert('피드백 유형과 내용을 모두 입력해주세요.');
          return;
        }

        const response = await axios.post('http://localhost:8080/api/feedback', {
          type: this.feedbackType,
          content: this.feedbackContent.trim()
        });
        
        if (response.data) {
          alert('소중한 의견 감사합니다! 검토 후 반영하도록 하겠습니다☺️');
          this.closeFeedbackModal();
        }
      } catch (err) {
        console.error('피드백 제출 오류:', err);
        alert('죄송합니다. 전송 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
      }
    }
  },
};
</script>

<style scoped>
/* 컨테이너 및 기본 레이아웃 */
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

/* 폼 요소 스타일링 */
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

/* 날씨 옵션 */
.weather-options {
  display: flex;
  gap: 20px;
  margin: 10px 0;
}

.weather-option {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.weather-option input[type="checkbox"] {
  width: 16px;
  height: 16px;
}

/* 버튼 스타일링 */
.button-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 20px;
}

/* 카드 및 결과 표시 */
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

/* 알림 및 경고 메시지 */
.alert-error {
  background-color: #fef2f2;
  border: 1px solid #fee2e2;
  color: #dc2626;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 20px;
  text-align: center;
}

.weather-warning {
  margin-top: 10px;
  padding: 10px;
  background-color: #fff3cd;
  border: 1px solid #ffeeba;
  border-radius: 4px;
  color: #856404;
}

.weather-warning i {
  margin-right: 8px;
}

.validation-message {
  color: #dc3545;
  font-size: 0.9em;
  margin-top: 4px;
  margin-bottom: 0;
}

.weird-time-message {
  color: #6c757d;
  font-style: italic;
  margin-top: 8px;
  text-align: center;
}

/* 필수/선택 표시 */
.required {
  color: #dc3545;
  margin-left: 4px;
}

.optional {
  color: #6c757d;
  font-size: 0.9em;
  font-weight: normal;
  margin-left: 4px;
}

/* 메인 안내 메시지 */
.main-instruction {
  margin: 15px 0;
  font-size: 1.2em;
  line-height: 1.6;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #0d6efd;
}

.highlight-time {
  color: #dc3545;
  font-size: 1.1em;
}

.highlight-route {
  color: #0d6efd;
  font-size: 1.1em;
}

/* 피드백 섹션 */
.feedback-section {
  text-align: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.feedback-button {
  background: none;
  border: none;
  color: #6b7280;
  font-size: 0.9em;
  cursor: pointer;
  text-decoration: underline;
  padding: 8px 16px;
}

.feedback-button:hover {
  color: #374151;
}

/* 모달 스타일링 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 24px;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-title {
  font-size: 1.5em;
  margin-bottom: 20px;
  color: #1f2937;
}

/* 피드백 폼 */
.feedback-type {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.example-feedbacks {
  background-color: #f3f4f6;
  padding: 12px;
  border-radius: 4px;
  margin-bottom: 16px;
}

.example-text {
  color: #6b7280;
  margin-bottom: 8px;
}

.feedback-textarea {
  width: 100%;
  min-height: 120px;
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  resize: vertical;
  margin-bottom: 20px;
}

/* 모달 푸터 버튼 */
.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.submit-button, .cancel-button {
  padding: 8px 16px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
}

.submit-button {
  background-color: #2563eb;
  color: white;
}

.submit-button:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}

.submit-button:hover:not(:disabled) {
  background-color: #1d4ed8;
}

.cancel-button {
  background-color: #e5e7eb;
  color: #4b5563;
}

.cancel-button:hover {
  background-color: #d1d5db;
}

.required-text {
  margin-bottom: 12px;
  font-weight: 500;
  color: #1f2937;
}

.route-info-grid {
  display: grid;
  /* gap: 15px; */
  margin-bottom: 20px;
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
}

.route-info-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.route-info-cell {
  padding: 8px;
}

.route-info-cell strong {
  color: #2c3e50;
  margin-right: 8px;
}

.warning-message {
  color: #dc3545;
  font-weight: bold;
  margin-top: 15px;
  text-align: center;
}

.walk-info {
  font-size: 0.9em;
  color: #666;
  margin-left: 8px;
}

.final-arrival {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px dashed #ddd;
}

.transport-options {
  display: flex;
  gap: 16px;
  margin: 10px 0;
}

.transport-option {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.transport-content {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  background-color: #f3f4f6;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.transport-option input[type="radio"] {
  display: none;
}

.transport-option input[type="radio"]:checked + .transport-content {
  background-color: #dbeafe;
  border: 1px solid #93c5fd;
}

.transport-text {
  margin-left: 8px;
  font-size: 0.95em;
  color: #4b5563;
}

.transport-option i {
  color: #4b5563;
}

.transport-option input[type="radio"]:checked + .transport-content i,
.transport-option input[type="radio"]:checked + .transport-content .transport-text {
  color: #2563eb;
}

.kt-badge {
  background-color: #2563eb;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.8em;
  margin-left: 6px;
}

.transport-option:hover .transport-content {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}
</style>
