<template>
  <div class="container">
    <h1 class="title">지각노노 관리자 페이지</h1>
    
    <!-- 스케줄 목록 -->
    <div class="schedule-list">
      <h2>노선 목록</h2>
      <table>
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
            <td>{{ schedule.routeType }}</td>
            <td>{{ schedule.routeNumber }}</td>
            <td>{{ schedule.startLocation }}</td>
            <td>{{ schedule.endLocation }}</td>
            <td>{{ schedule.departureTime }}</td>
            <td>{{ schedule.duration }}분</td>
            <td>{{ schedule.trafficDelay }}분</td>
            <td>{{ schedule.walkDuration }}분</td>
            <td>
              <button @click="editSchedule(schedule)">수정</button>
              <button @click="deleteSchedule(schedule.id)">삭제</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 스케줄 등록/수정 폼 -->
    <div class="schedule-form">
      <h2>{{ isEditing ? '노선 수정' : '새 노선 등록' }}</h2>
      <form @submit.prevent="saveSchedule">
        <div class="form-group">
          <label>노선 종류:</label>
          <select v-model="currentSchedule.routeType">
            <option value="셔틀">셔틀</option>
            <option value="버스">버스</option>
          </select>
        </div>

        <div class="form-group">
          <label>노선 번호:</label>
          <input v-model="currentSchedule.routeNumber" required>
        </div>

        <div class="form-group">
          <label>출발지:</label>
          <select v-model="currentSchedule.startLocation">
            <option value="청계산입구역">청계산입구역</option>
            <option value="판교역">판교역</option>
          </select>
        </div>

        <div class="form-group">
          <label>도착지:</label>
          <input v-model="currentSchedule.endLocation" required>
        </div>

        <div class="form-group">
          <label>출발 시간:</label>
          <input type="time" v-model="currentSchedule.departureTime" required>
        </div>

        <div class="form-group">
          <label>기본 소요 시간 (분):</label>
          <input type="number" v-model="currentSchedule.duration" required>
        </div>

        <div class="form-group">
          <label>교통 지연 시간 (분):</label>
          <input type="number" v-model="currentSchedule.trafficDelay">
        </div>

        <div class="form-group">
          <label>도보 소요 시간 (분):</label>
          <input type="number" v-model="currentSchedule.walkDuration">
        </div>

        <div class="form-group">
          <label>도보 거리 (미터):</label>
          <input type="number" v-model="currentSchedule.walkDistance">
        </div>

        <button type="submit">{{ isEditing ? '수정' : '등록' }}</button>
        <button type="button" @click="resetForm">취소</button>
      </form>
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
      isEditing: false
    };
  },
  
  methods: {
    getEmptySchedule() {
      return {
        routeType: '버스',
        routeNumber: '',
        startLocation: '청계산입구역',
        endLocation: 'KT판교빌딩',
        departureTime: '',
        duration: '',
        trafficDelay: '',
        walkDuration: '',
        walkDistance: ''
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
        this.resetForm();
      } catch (error) {
        console.error('스케줄 저장 실패:', error);
        console.log('Error response:', error.response?.data); // 디버깅용
      }
    },

    editSchedule(schedule) {
      // 시간 데이터 포맷 변환 (HH:mm:ss -> HH:mm)
      this.currentSchedule = {
        ...schedule,
        departureTime: schedule.departureTime.substring(0, 5)  // HH:mm 형식으로 자르기
      };
      this.isEditing = true;
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

    resetForm() {
      this.currentSchedule = this.getEmptySchedule();
      this.isEditing = false;
    }
  },

  mounted() {
    this.loadSchedules();
  }
};
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.title {
  text-align: center;
  margin-bottom: 30px;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

th, td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

th {
  background-color: #f4f4f4;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
}

input, select {
  width: 100%;
  padding: 8px;
  margin-bottom: 10px;
}

button {
  padding: 8px 15px;
  margin-right: 10px;
}
</style> 