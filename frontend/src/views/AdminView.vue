<template>
  <div class="container">
    <div class="header">
      <div class="title-section">
        <h1 class="title">지각노노 관리자 페이지</h1>
      </div>
      <div class="header-buttons">
        <button @click="$router.push('/')" class="nav-button home-button">
          <i class="fas fa-home"></i>
          <span>홈페이지로 이동</span>
        </button>
        <button @click="$router.push('/admin/account')" class="nav-button account-button">
          <i class="fas fa-user-cog"></i>
          <span>계정 관리</span>
        </button>
        <button @click="logout" class="nav-button logout-button">
          <i class="fas fa-sign-out-alt"></i>
          <span>로그아웃</span>
        </button>
      </div>
    </div>

    <!-- 탭 메뉴 추가 -->
    <div class="tab-menu">
      <button 
        @click="currentTab = 'routes'"
        :class="['tab-button', { active: currentTab === 'routes' }]"
      >
        <i class="fas fa-route"></i> 노선 관리
      </button>
      <button 
        @click="currentTab = 'feedback'"
        :class="['tab-button', { active: currentTab === 'feedback' }]"
      >
        <i class="fas fa-comments"></i> 피드백 관리
        <span v-if="unreadFeedbackCount" class="notification-badge">
          {{ unreadFeedbackCount }}
        </span>
      </button>
    </div>

    <!-- 노선 관리 탭 -->
    <div v-if="currentTab === 'routes'" class="content-section">
      <div class="section-header">
        <div class="title-with-info">
          <h2 class="section-title">노선 목록</h2>
          <div class="info-tooltip">
            <i class="fas fa-info-circle"></i>
            <span class="tooltip-text">
              정렬 기준:<br>
              1. 셔틀버스가 상단에 표시됩니다.<br>
              2. 같은 출발지끼리 그룹화됩니다.<br>
              3. 출발 시간이 빠른 순서대로 정렬됩니다.<br>
              4. 도착 시간이 빠른 순서대로 정렬됩니다.
            </span>
          </div>
        </div>
        <div class="header-actions">
          <div class="search-container">
            <select v-model="routePathFilter" class="route-path-filter">
              <option value="">모든 경로</option>
              <option value="PANGYO_TO_KT">판교역 → KT판교빌딩</option>
              <option value="CHEONGGYE_TO_KT">청계산입구역 → KT판교빌딩</option>
            </select>
            <div v-if="showSearch" class="search-box">
              <input 
                type="text" 
                v-model="routeFilter" 
                placeholder="노선 번호로 검색"
                class="route-search"
                ref="searchInput"
              >
              <button @click="clearSearch" class="clear-search">
                <i class="fas fa-times"></i>
              </button>
            </div>
            <button @click="toggleSearch" class="search-button" :class="{ active: showSearch }">
              <i class="fas fa-search"></i>
            </button>
          </div>
          <button @click="openModal()" class="create-button">
            <i class="fas fa-plus"></i> 새 노선 등록
          </button>
        </div>
      </div>

      <div class="table-container">
        <table class="schedule-table">
          <thead>
            <tr>
              <th class="text-left">노선 종류</th>
              <th class="text-left">노선 번호</th>
              <th class="text-left">출발지</th>
              <th class="text-left">도착지</th>
              <th class="text-center">출발 시간</th>
              <th class="text-center">도착 시간</th>
              <th class="text-center">소요 시간</th>
              <th class="text-center">지연 시간</th>
              <th class="text-center">도보 시간</th>
              <th class="text-center action-header">작업</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="schedule in filteredAndSortedSchedules" :key="schedule.id" :class="{ 'hover-row': true }">
              <td>
                <span :class="['route-type-tag', schedule.routeType === 'SHUTTLE' ? 'shuttle' : 'bus']">
                  {{ getRouteTypeDisplay(schedule.routeType) }}
                </span>
              </td>
              <td>{{ schedule.routeNumber }}</td>
              <td>{{ schedule.startLocation }}</td>
              <td>{{ schedule.endLocation }}</td>
              <td class="text-center">{{ schedule.departureTime }}</td>
              <td class="text-center">{{ calculateArrivalTime(schedule) }}</td>
              <td class="text-center">{{ schedule.duration }}분</td>
              <td class="text-center">{{ schedule.trafficDelay }}분</td>
              <td class="text-center">{{ schedule.walkDuration }}분</td>
              <td class="action-column">
                <div class="action-buttons">
                  <button @click="editSchedule(schedule)" class="action-button edit-button" title="수정">
                    <i class="fas fa-edit"></i>
                  </button>
                  <button @click="deleteSchedule(schedule.id)" class="action-button delete-button" title="삭제">
                    <i class="fas fa-trash-alt"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 피드백 관리 탭 -->
    <div v-if="currentTab === 'feedback'" class="content-section">
      <div class="section-header">
        <h2 class="section-title">피드백 목록</h2>
        <div class="header-actions">
          <div class="filter-options">
            <label class="filter-label">
              <input type="checkbox" v-model="showResolved" @change="loadFeedbacks">
              처리완료 피드백 표시
            </label>
          </div>
        </div>
      </div>

      <div class="feedback-list">
        <div v-for="feedback in filteredFeedbacks" :key="feedback.id" 
             class="feedback-card" :class="{ 'resolved': feedback.isResolved }">
          <div class="feedback-header">
            <span class="feedback-type" :class="feedback.type.toLowerCase()">
              {{ getFeedbackTypeDisplay(feedback.type) }}
            </span>
            <span class="feedback-date">{{ formatDate(feedback.createdAt) }}</span>
          </div>
          
          <div class="feedback-content">{{ feedback.content }}</div>
          
          <div v-if="feedback.isResolved" class="admin-comment">
            <strong>처리결과:</strong> {{ feedback.adminComment }}
          </div>
          
          <div v-if="!feedback.isResolved" class="feedback-actions">
            <textarea 
              v-model="feedback.tempComment" 
              placeholder="처리 결과를 입력하세요"
              class="admin-input"
            ></textarea>
            <button @click="resolveFeedback(feedback)" class="resolve-button" :disabled="!feedback.tempComment">
              처리완료
            </button>
          </div>
        </div>
        
        <div v-if="filteredFeedbacks.length === 0" class="no-feedback">
          표시할 피드백이 없습니다.
        </div>
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
                <label>노선 경로 <span class="required">*</span></label>
                <div class="radio-group">
                  <label class="radio-label">
                    <input 
                      type="radio" 
                      v-model="currentSchedule.routePath" 
                      value="PANGYO_TO_KT"
                      @change="handleRoutePathChange"
                    >
                    판교역 → KT판교빌딩
                  </label>
                  <label class="radio-label">
                    <input 
                      type="radio" 
                      v-model="currentSchedule.routePath" 
                      value="CHEONGGYE_TO_KT"
                      @change="handleRoutePathChange"
                    >
                    청계산입구역 → KT판교빌딩
                  </label>
                </div>
              </div>

              <div class="form-group">
                <label>출발지 <span class="required">*</span></label>
                <input 
                  v-model="currentSchedule.startLocation" 
                  required
                  :placeholder="getStartLocationPlaceholder()"
                >
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
      showModal: false,
      showSearch: false,
      routeFilter: '',
      routePathFilter: '',
      currentTab: 'routes',
      feedbacks: [],
      showResolved: false,
      unreadFeedbackCount: 0
    };
  },
  
  methods: {
    getEmptySchedule() {
      return {
        routeType: 'BUS',
        routeNumber: '',
        routePath: 'PANGYO_TO_KT',  // 기본값 설정
        startLocation: '',
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
        // 셔틀인 경우 출발지를 자동으로 설정
        this.currentSchedule.startLocation = this.currentSchedule.routePath === 'PANGYO_TO_KT' 
          ? '판교역' 
          : '청계산입구역';
      } else {
        this.currentSchedule.routeNumber = '';
        this.currentSchedule.startLocation = ''; // 버스인 경우 출발지 초기화
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
    },

    toggleSearch() {
      this.showSearch = !this.showSearch;
      if (this.showSearch) {
        // 검색창이 나타나면 자동으로 포커스
        this.$nextTick(() => {
          this.$refs.searchInput.focus();
        });
      } else {
        // 검색창이 사라질 때 필터 초기화
        this.routeFilter = '';
      }
    },

    clearSearch() {
      this.routeFilter = '';
      this.$refs.searchInput.focus();
    },

    async loadFeedbacks() {
      try {
        const response = await axios.get('http://localhost:8080/api/feedback');
        this.feedbacks = response.data.map(feedback => ({
          ...feedback,
          tempComment: '',
          createdAt: new Date(feedback.createdAt).toISOString() // ISO 형식으로 변환
        }));
        this.updateUnreadCount();
      } catch (error) {
        console.error('피드백 목록을 불러오는데 실패했습니다:', error);
      }
    },

    async resolveFeedback(feedback) {
      try {
        await axios.put(`http://localhost:8080/api/feedback/${feedback.id}/resolve`, {
          adminComment: feedback.tempComment
        });
        await this.loadFeedbacks();
      } catch (error) {
        console.error('피드백 처리에 실패했습니다:', error);
      }
    },

    getFeedbackTypeDisplay(type) {
      const types = {
        'add': '신규 추가 요청',
        'update': '정보 수정 요청',
        'other': '기타 문의 사항'
      };
      return types[type] || type;
    },

    formatDate(dateStr) {
      if (!dateStr) return '';
      const options = {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        hour12: true,
        timeZone: 'Asia/Seoul'
      };
      return new Date(dateStr).toLocaleString('ko-KR', options);
    },

    updateUnreadCount() {
      this.unreadFeedbackCount = this.feedbacks.filter(f => !f.isResolved).length;
    },

    calculateArrivalTime(schedule) {
      // HH:mm 형식의 출발 시간을 Date 객체로 변환
      const [hours, minutes] = schedule.departureTime.split(':');
      const departureDate = new Date();
      departureDate.setHours(parseInt(hours));
      departureDate.setMinutes(parseInt(minutes));
      
      // 소요 시간(분)을 더함
      const totalMinutes = parseInt(schedule.duration) + 
                         parseInt(schedule.trafficDelay) + 
                         parseInt(schedule.walkDuration);
      departureDate.setMinutes(departureDate.getMinutes() + totalMinutes);
      
      // HH:mm 형식으로 반환
      return `${String(departureDate.getHours()).padStart(2, '0')}:${String(departureDate.getMinutes()).padStart(2, '0')}`;
    },

    handleRoutePathChange() {
      // 셔틀버스인 경우 출발지를 자동으로 설정
      if (this.currentSchedule.routeType === 'SHUTTLE') {
        this.currentSchedule.startLocation = this.currentSchedule.routePath === 'PANGYO_TO_KT' 
          ? '판교역' 
          : '청계산입구역';
      }
    },

    getStartLocationPlaceholder() {
      if (this.currentSchedule.routeType === 'SHUTTLE') {
        return this.currentSchedule.routePath === 'PANGYO_TO_KT' 
          ? '판교역' 
          : '청계산입구역';
      }
      return this.currentSchedule.routePath === 'PANGYO_TO_KT' 
        ? '예: 판교역동편, 판교역' 
        : '예: 청계산입구역';
    }
  },

  computed: {
    sortedSchedules() {
      return [...this.schedules].sort((a, b) => {
        // 1. 셔틀을 상단에 배치
        if (a.routeType !== b.routeType) {
          return a.routeType === 'SHUTTLE' ? -1 : 1;
        }
        
        // 2. 출발지 기준 그룹핑
        if (a.startLocation !== b.startLocation) {
          return a.startLocation.localeCompare(b.startLocation);
        }
        
        // 3. 출발 시간순 정렬
        return a.departureTime.localeCompare(b.departureTime);
      });
    },

    filteredAndSortedSchedules() {
      let filtered = this.schedules;
      
      // 노선 번호 필터 적용
      if (this.routeFilter) {
        filtered = filtered.filter(schedule => 
          schedule.routeNumber.toLowerCase().includes(this.routeFilter.toLowerCase())
        );
      }

      // 경로 필터 적용
      if (this.routePathFilter) {
        filtered = filtered.filter(schedule => 
          schedule.routePath === this.routePathFilter
        );
      }

      // 정렬 로직 적용
      return filtered.sort((a, b) => {
        // 1. 셔틀을 상단에 배치
        if (a.routeType !== b.routeType) {
          return a.routeType === 'SHUTTLE' ? -1 : 1;
        }
        
        // 2. 출발지 기준 그룹핑
        if (a.startLocation !== b.startLocation) {
          return a.startLocation.localeCompare(b.startLocation);
        }
        
        // 3. 출발 시간 기준 정렬
        if (a.departureTime !== b.departureTime) {
          return a.departureTime.localeCompare(b.departureTime);
        }

        // 4. 도착 시간 기준 정렬
        const arrivalTimeA = this.calculateArrivalTime(a);
        const arrivalTimeB = this.calculateArrivalTime(b);
        return arrivalTimeA.localeCompare(arrivalTimeB);
      });
    },

    filteredFeedbacks() {
      return this.feedbacks
        .filter(f => this.showResolved || !f.isResolved)
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
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
    this.loadFeedbacks();
  },

  watch: {
    currentTab(newTab) {
      if (newTab === 'feedback') {
        this.loadFeedbacks();
      }
    }
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
  display: inline-flex;
  align-items: center;      /* 수직 중앙 정렬 */
  justify-content: center;  /* 수평 중앙 정렬 */
  gap: 8px;                /* 아이콘과 텍스트 사이 간격 */
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
  height: 36px;            /* 버튼 높이 고정 */
  line-height: 1;          /* 줄 높이를 1로 설정 */
}

.nav-button i {
  font-size: 1rem;
}

.nav-button span {
  display: inline-block;
  vertical-align: middle;  /* 텍스트 수직 중앙 정렬 */
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

/* 호버 효과 */
.home-button:hover {
  background-color: #138496;
}

.account-button:hover {
  background-color: #5a6268;
}

.logout-button:hover {
  background-color: #c82333;
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

.header-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.search-container {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.route-path-filter {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.9rem;
  background-color: white;
  color: #333;
  cursor: pointer;
  min-width: 220px;
}

.route-path-filter:hover {
  border-color: #aaa;
}

.route-path-filter:focus {
  outline: none;
  border-color: #17a2b8;
  box-shadow: 0 0 0 2px rgba(23, 162, 184, 0.1);
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.route-search {
  width: 200px;
  padding: 0.5rem 2rem 0.5rem 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.clear-search {
  position: absolute;
  right: 8px;
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  padding: 4px;
}

.search-button {
  padding: 0.5rem;
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  transition: color 0.2s;
}

.search-button.active {
  color: #17a2b8;
}

.search-button:hover {
  color: #333;
}

/* 검색창 애니메이션 */
.route-search {
  opacity: 1;
  transform: translateX(0);
  transition: all 0.3s ease;
}

.route-search.hidden {
  opacity: 0;
  transform: translateX(-10px);
  pointer-events: none;
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
  border-collapse: separate;
  border-spacing: 0;
  margin-top: 1rem;
}

.schedule-table th,
.schedule-table td {
  padding: 0.75rem;
  border-bottom: 1px solid #dee2e6;
  vertical-align: middle;
}

.text-left {
  text-align: left;
}

.text-center {
  text-align: center;
}

.action-header {
  width: 120px;
}

.action-column {
  width: 120px;
  text-align: center;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
  align-items: center;
}

.action-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  line-height: 1;
  padding: 0;
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

/* 아이콘 크기 조정 */
.action-button i {
  font-size: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
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
  appearance: textfield;
  -moz-appearance: textfield;
}

input[type="number"]::-webkit-outer-spin-button,
input[type="number"]::-webkit-inner-spin-button {
  -webkit-appearance: none;
  appearance: none;
  margin: 0;
}

.route-type-tag {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.85rem;
  font-weight: 500;
  text-align: center;
  min-width: 64px;
}

.route-type-tag.bus {
  background-color: #82c91e;  /* 연두색 */
  color: white;
}

.route-type-tag.shuttle {
  background-color: #e03131;  /* 빨간색 */
  color: white;
}

/* tbody의 행에만 호버 효과 적용 */
.schedule-table tbody tr:hover {
  background-color: #f8f9fa;
  transition: background-color 0.2s ease;
  cursor: default;
}

/* 테이블 헤더 스타일 */
.schedule-table thead th {
  background-color: #f8f9fa;  /* 헤더 배경색 */
  font-weight: 600;
  color: #495057;
  border-bottom: 2px solid #dee2e6;  /* 헤더 아래 굵은 구분선 */
}

/* 데이터 행 스타일 */
.schedule-table tbody td {
  padding: 0.75rem;
  border-bottom: 1px solid #dee2e6;
  vertical-align: middle;
}

.tab-menu {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.tab-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background-color: #f8f9fa;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  color: #6c757d;
  position: relative;
}

.tab-button.active {
  background-color: #e9ecef;
  color: #2c3e50;
  font-weight: 500;
}

.notification-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background-color: #dc3545;
  color: white;
  font-size: 0.75rem;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 20px;
  text-align: center;
}

.feedback-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.feedback-card {
  background-color: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 1rem;
}

.feedback-card.resolved {
  background-color: #f8f9fa;
  border-color: #dee2e6;
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.feedback-type {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.875rem;
  font-weight: 500;
}

.feedback-type.add {
  background-color: #dbeafe;
  color: #1e40af;
}

.feedback-type.update {
  background-color: #fef3c7;
  color: #92400e;
}

.feedback-type.other {
  background-color: #e9d5ff;
  color: #6b21a8;
}

.feedback-date {
  color: #6b7280;
  font-size: 0.875rem;
}

.feedback-content {
  color: #1f2937;
  line-height: 1.5;
  margin-bottom: 1rem;
}

.admin-comment {
  background-color: #f3f4f6;
  padding: 0.75rem;
  border-radius: 4px;
  color: #4b5563;
}

.feedback-actions {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.admin-input {
  width: 100%;
  min-height: 80px;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  resize: vertical;
}

.resolve-button {
  align-self: flex-end;
  background-color: #2563eb;
  color: white;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.resolve-button:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}

.no-feedback {
  text-align: center;
  padding: 2rem;
  color: #6b7280;
  background-color: #f9fafb;
  border: 1px dashed #d1d5db;
  border-radius: 8px;
}

.filter-options {
  display: flex;
  align-items: center;
}

.filter-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #4b5563;
  cursor: pointer;
}

.radio-group {
  display: flex;
  gap: 1rem;
  margin-top: 0.5rem;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.radio-label input[type="radio"] {
  margin: 0;
}

.title-with-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.info-tooltip {
  position: relative;
  display: inline-block;
  color: #6c757d;
  cursor: help;
}

.info-tooltip i {
  font-size: 1rem;
}

.info-tooltip:hover .tooltip-text {
  visibility: visible;
  opacity: 1;
}

.tooltip-text {
  visibility: hidden;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  bottom: 100%;
  margin-bottom: 5px;
  padding: 0.75rem;
  background-color: #2c3e50;
  color: white;
  text-align: left;
  border-radius: 6px;
  font-size: 0.875rem;
  line-height: 1.5;
  white-space: nowrap;
  z-index: 1;
  opacity: 0;
  transition: opacity 0.2s;
}

.tooltip-text::after {
  content: "";
  position: absolute;
  top: 100%;
  left: 50%;
  margin-left: -5px;
  border-width: 5px;
  border-style: solid;
  border-color: #2c3e50 transparent transparent transparent;
}
</style> 