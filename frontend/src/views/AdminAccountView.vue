<template>
  <div class="admin-container">
    <div class="header">
      <h1 class="title">관리자 계정 관리</h1>
      <div class="header-buttons">
        <button @click="$router.push('/')" class="home-button">홈페이지로 이동</button>
        <button @click="$router.push('/admin')" class="back-button">관리자 페이지로 돌아가기</button>
      </div>
    </div>

    <div class="content-box">
      <h2>비밀번호 변경</h2>
      <form @submit.prevent="changePassword">
        <div class="form-group">
          <label>현재 비밀번호:</label>
          <input type="password" v-model="currentPassword" required>
        </div>
        <div class="form-group">
          <label>새 비밀번호:</label>
          <input type="password" v-model="newPassword" required>
        </div>
        <div class="form-group">
          <label>새 비밀번호 확인:</label>
          <input type="password" v-model="confirmPassword" required>
        </div>
        <button type="submit" class="submit-button">비밀번호 변경</button>
      </form>
      <div v-if="message" :class="['message', messageType]">
        {{ message }}
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
      message: '',
      messageType: 'success'
    }
  },
  methods: {
    async changePassword() {
      if (this.newPassword !== this.confirmPassword) {
        this.message = '새 비밀번호가 일치하지 않습니다.';
        this.messageType = 'error';
        return;
      }

      try {
        const response = await axios.post('http://localhost:8080/api/admin/change-password', {
          currentPassword: this.currentPassword,
          newPassword: this.newPassword
        });

        if (response.data.status === 'success') {
          this.message = '비밀번호가 성공적으로 변경되었습니다.';
          this.messageType = 'success';
          this.resetForm();
        }
      } catch (error) {
        this.message = error.response?.data?.message || '비밀번호 변경에 실패했습니다.';
        this.messageType = 'error';
      }
    },
    resetForm() {
      this.currentPassword = '';
      this.newPassword = '';
      this.confirmPassword = '';
    }
  }
}
</script>

<style scoped>
.admin-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.title {
  font-size: 24px;
  margin: 0;
  color: #333;
}

.header-buttons {
  display: flex;
  gap: 1rem;
}

.home-button {
  padding: 8px 16px;
  background-color: #17a2b8;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.home-button:hover {
  background-color: #138496;
}

.back-button {
  padding: 8px 16px;
  background-color: #6c757d;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.back-button:hover {
  background-color: #5a6268;
}

.content-box {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.submit-button {
  width: 100%;
  padding: 12px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  margin-top: 10px;
}

.submit-button:hover {
  background-color: #45a049;
}

.message {
  margin-top: 20px;
  padding: 12px;
  border-radius: 4px;
  font-size: 14px;
}

.message.success {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.message.error {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}
</style> 