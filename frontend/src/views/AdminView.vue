<template>
  <div class="container">
    <div class="header">
      <div class="title-section">
        <h1 class="title">지각노노 관리자 페이지</h1>
      </div>
      <div class="header-buttons">
        <button @click="$router.push('/')" class="nav-button home-button">
          <i class="fas fa-home"></i> 홈페이지로 이동
        </button>
        <button @click="$router.push('/admin/account')" class="nav-button account-button">
          <i class="fas fa-user-cog"></i> 계정 관리
        </button>
        <button @click="logout" class="nav-button logout-button">
          <i class="fas fa-sign-out-alt"></i> 로그아웃
        </button>
      </div>
    </div>

    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">노선 목록</h2>
        <button @click="openModal()" class="create-button">
          <i class="fas fa-plus"></i> 새 노선 등록
        </button>
      </div>

      <div class="table-container">
        <table class="schedule-table">
          <thead>
            <tr>
              <th>노선 종류</th>
              <th>노선 번호</th>
              <th>출발지</th>
              <th>도착지</th>
              <th>출발 시간</th>
              <th>소요 시간</th>
              <th>지연 시간</th>
              <th>도보 시간</th>
              <th>작업</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="schedule in schedules" :key="schedule.id">
              <td>{{ getRouteTypeDisplay(schedule.routeType) }}</td>
              <td>{{ schedule.routeNumber }}</td>
              <td>{{ schedule.startLocation }}</td>
              <td>{{ schedule.endLocation }}</td>
              <td>{{ schedule.departureTime }}</td>
              <td>{{ schedule.duration }}분</td>
              <td>{{ schedule.trafficDelay }}분</td>
              <td>{{ schedule.walkDuration }}분</td>
              <td class="action-buttons">
                <button @click="editSchedule(schedule)" class="edit-button" title="수정">
                  <i class="fas fa-edit"></i>
                </button>
                <button @click="deleteSchedule(schedule.id)" class="delete-button" title="삭제">
                  <i class="fas fa-trash-alt"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 개선된 모달 -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>{{ isEditing ? '노선 수정하기' : '새 노선 등록' }}</h2>
          <button @click="closeModal" class="close-button">×</button>
        </div>
        
        <form @submit.prevent="saveSchedule" class="schedule-form">
          <div class="form-container">
            <!-- 왼쪽 컬럼 -->
            <div class="form-column">
              <div class="form-group">
                <label>노선 종류 <span class="required">*</span></label>
                <select v-model="currentSchedule.routeType" required class="select-box" @change="handleRouteTypeChange">
                  <option value="SHUTTLE">셔틀</option>
                  <option value="BUS">버스</option>
                </select>
              </div>

              <div class="form-group">
                <label>노선 번호 <span class="required">*</span></label>
                <input v-model="currentSchedule.routeNumber" required 
                       :readonly="currentSchedule.routeType === 'SHUTTLE'"
                       :placeholder="currentSchedule.routeType === 'BUS' ? '예: 310, 서울07출근' : 'KT셔틀'">
              </div>

              <div class="form-group">
                <label>출발지 <span class="required">*</span></label>
                <select v-model="currentSchedule.startLocation" required class="select-box">
                  <option value="청계산입구역">청계산입구역</option>
                  <option value="판교역">판교역</option>
                </select>
              </div>

              <div class="form-group">
                <label>도착지 <span class="required">*</span></label>
                <input v-model="currentSchedule.endLocation" required
                       placeholder="예: KT판교빌딩">
              </div>

              <div class="form-group">
                <label>출발 시간 <span class="required">*</span></label>
                <input type="time" v-model="currentSchedule.departureTime" required>
              </div>
            </div>

            <!-- 오른쪽 컬럼 -->
            <div class="form-column">
              <div class="form-group">
                <label>기본 소요 시간 (분) <span class="required">*</span></label>
                <input type="number" v-model="currentSchedule.duration" required
                       min="0" placeholder="예: 15">
              </div>

              <div class="form-group">
                <label>교통 지연 시간 (분)</label>
                <input type="number" v-model="currentSchedule.trafficDelay"
                       min="0" placeholder="0">
              </div>

              <div class="form-group">
                <label>도보 소요 시간 (분)</label>
                <input type="number" v-model="currentSchedule.walkDuration"
                       min="0" placeholder="0">
              </div>

              <div class="form-group">
                <label>도보 거리 (미터)</label>
                <input type="number" v-model="currentSchedule.walkDistance"
                       min="0" placeholder="0">
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button type="button" @click="closeModal" class="cancel-button">취소</button>
            <button type="submit" class="save-button">
              {{ isEditing ? '수정하기' : '등록하기' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      schedules: [],
      currentSchedule: this.getEmptySchedule(),
      isEditing: false,
      showModal: false
    };
  },
  
  methods: {
    getEmptySchedule() {
      return {
        routeType: 'BUS',
        routeNumber: '',
        startLocation: '청계산입구역',
        endLocation: 'KT판교빌딩',
        departureTime: '',
        duration: '',
        trafficDelay: '0',
        walkDuration: '0',
        walkDistance: '0'
      };
    },

    async loadSchedules() {
      try {
        const response = await axios.get('http://localhost:8080/api/admin/schedules');
        this.schedules = response.data.map(schedule => ({
          ...schedule,
          departureTime: schedule.departureTime.substring(0, 5) // 표시용 시간 포맷 변환
        }));
      } catch (error) {
        console.error('스케줄 로딩 실패:', error);
      }
    },

    async saveSchedule() {
      if (!this.validateSchedule()) {
        return;
      }

      try {
        // 저장할 데이터 준비
        const scheduleToSave = {
          ...this.currentSchedule,
          departureTime: this.currentSchedule.departureTime.includes(':') 
            ? this.currentSchedule.departureTime + ':00'  // HH:mm -> HH:mm:ss
            : this.currentSchedule.departureTime,
          duration: parseInt(this.currentSchedule.duration) || 0,
          trafficDelay: parseInt(this.currentSchedule.trafficDelay) || 0,
          walkDuration: parseInt(this.currentSchedule.walkDuration) || 0,
          walkDistance: parseInt(this.currentSchedule.walkDistance) || 0
        };

        console.log('Saving schedule:', scheduleToSave); // 디버깅용

        if (this.isEditing) {
          await axios.put(
            `http://localhost:8080/api/admin/schedules/${scheduleToSave.id}`, 
            scheduleToSave
          );
        } else {
          await axios.post(
            'http://localhost:8080/api/admin/schedules', 
            scheduleToSave
          );
        }
        
        await this.loadSchedules();
        this.closeModal();
      } catch (error) {
        console.error('스케줄 저장 실패:', error);
        console.log('Error response:', error.response?.data); // 디버깅용
      }
    },

    editSchedule(schedule) {
      this.openModal(schedule);
      // 노선 종류에 따른 노선 번호 처리
      if (schedule.routeType === 'SHUTTLE') {
        this.currentSchedule.routeNumber = 'KT셔틀';
      }
    },

    async deleteSchedule(id) {
      if (!confirm('정말 삭제하시겠습니까?')) return;
      
      try {
        await axios.delete(`http://localhost:8080/api/admin/schedules/${id}`);
        this.loadSchedules();
      } catch (error) {
        console.error('스케줄 삭제 실패:', error);
      }
    },

    openModal(schedule = null) {
      this.showModal = true;
      if (schedule) {
        this.isEditing = true;
        this.currentSchedule = { ...schedule };
        // 시간 포맷 변환
        this.currentSchedule.departureTime = schedule.departureTime.substring(0, 5);
      } else {
        this.isEditing = false;
        this.currentSchedule = this.getEmptySchedule();
      }
    },

    closeModal() {
      this.showModal = false;
      this.currentSchedule = this.getEmptySchedule();
      this.isEditing = false;
    },

    logout() {
      localStorage.removeItem('adminAuthenticated');
      this.$router.push('/admin/login');
    },

    // 노선 종류 표시 변환
    getRouteTypeDisplay(routeType) {
      return routeType === 'SHUTTLE' ? '셔틀' : '버스';
    },

    // 노선 종류 변경 처리
    handleRouteTypeChange() {
      if (this.currentSchedule.routeType === 'SHUTTLE') {
        this.currentSchedule.routeNumber = 'KT셔틀';
      } else {
        this.currentSchedule.routeNumber = '';
      }
    },

    // 저장 전 데이터 검증
    validateSchedule() {
      // 음수 값 0으로 변환
      this.currentSchedule.duration = Math.max(0, parseInt(this.currentSchedule.duration) || 0);
      this.currentSchedule.trafficDelay = Math.max(0, parseInt(this.currentSchedule.trafficDelay) || 0);
      this.currentSchedule.walkDuration = Math.max(0, parseInt(this.currentSchedule.walkDuration) || 0);
      this.currentSchedule.walkDistance = Math.max(0, parseInt(this.currentSchedule.walkDistance) || 0);

      // 도착지가 비어있으면 기본값 설정
      if (!this.currentSchedule.endLocation.trim()) {
        this.currentSchedule.endLocation = 'KT판교빌딩';
      }

      return true;
    }
  },

  created() {
    // 페이지 진입 시 인증 체크
    if (!localStorage.getItem('adminAuthenticated')) {
      this.$router.push('/admin/login');
    }
  },

  mounted() {
    this.loadSchedules();
  }
};
</script>

<style scoped>
.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding: 1rem;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.title {
  font-size: 1.5rem;
  color: #2c3e50;
  margin: 0;
}

.header-buttons {
  display: flex;
  gap: 1rem;
}

.nav-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 6px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}

.home-button {
  background-color: #17a2b8;
  color: white;
}

.account-button {
  background-color: #6c757d;
  color: white;
}

.logout-button {
  background-color: #dc3545;
  color: white;
}

.content-section {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid #eee;
}

.section-title {
  font-size: 1.25rem;
  color: #2c3e50;
  margin: 0;
}

.create-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s;
}

.create-button:hover {
  background-color: #218838;
}

.table-container {
  overflow-x: auto;
}

.schedule-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
}

.schedule-table th,
.schedule-table td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.schedule-table th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #495057;
}

.schedule-table tr:hover {
  background-color: #f8f9fa;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
}

.edit-button,
.delete-button {
  padding: 0.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.edit-button {
  background-color: #ffc107;
  color: #000;
}

.delete-button {
  background-color: #dc3545;
  color: white;
}

.edit-button:hover {
  background-color: #e0a800;
}

.delete-button:hover {
  background-color: #c82333;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .header {
    flex-direction: column;
    gap: 1rem;
  }

  .header-buttons {
    width: 100%;
    flex-direction: column;
  }

  .nav-button {
    width: 100%;
    justify-content: center;
  }
}

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
  backdrop-filter: blur(2px);
}

.modal-content {
  background-color: white;
  padding: 2rem;
  border-radius: 12px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #eee;
}

.modal-header h2 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #666;
  cursor: pointer;
  padding: 0.5rem;
  transition: color 0.2s;
}

.close-button:hover {
  color: #333;
}

.form-container {
  display: flex;
  gap: 2rem;
}

.form-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-size: 0.9rem;
  color: #333;
}

.required {
  color: #dc3545;
}

/* 드롭박스(select)와 입력창(input) 너비 조정 */
.select-box {
  width: 200px; /* 또는 적절한 픽셀값 */
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.9rem;
}

.form-group input {
  width: 100%; /* 입력창은 100% 너비 유지 */
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.9rem;
}

.form-group input[readonly] {
  background-color: #f8f9fa;
  width: 200px; /* 읽기 전용 입력창도 select와 같은 너비로 */
}

/* 시간 입력창도 동일한 너비로 */
.form-group input[type="time"] {
  width: 200px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  margin-top: 2rem;
  padding-top: 1rem;
  border-top: 1px solid #eee;
}

.cancel-button,
.save-button {
  padding: 0.5rem 1.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.cancel-button {
  background-color: #6c757d;
  color: white;
}

.save-button {
  background-color: #28a745;
  color: white;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .form-container {
    flex-direction: column;
  }

  .modal-content {
    padding: 1.5rem;
    margin: 1rem;
  }
}

/* 읽기 전용 입력 필드 스타일 */
input[readonly] {
  background-color: #f8f9fa;
  cursor: not-allowed;
}

/* 숫자 입력 필드의 화살표 스타일링 */
input[type="number"] {
  -moz-appearance: textfield;
}

input[type="number"]::-webkit-outer-spin-button,
input[type="number"]::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
</style> 