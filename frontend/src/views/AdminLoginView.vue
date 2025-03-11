<template>
  <div class="login-container">
    <div class="login-box">
      <h2>관리자 로그인</h2>
      <div class="error-message" v-if="error">{{ error }}</div>
      <form @submit.prevent="login">
        <div class="form-group">
          <label>아이디:</label>
          <input type="text" v-model="username" required>
        </div>
        <div class="form-group">
          <label>비밀번호:</label>
          <input type="password" v-model="password" required>
        </div>
        <button type="submit">로그인</button>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: '',
      password: '',
      error: ''
    }
  },
  methods: {
    login() {
      // 실제 운영 환경에서는 이런 방식으로 하면 안 됩니다!
      // 이것은 데모용 간단 인증입니다.
      if (this.username === 'admin' && this.password === 'jigaknono123') {
        localStorage.setItem('adminAuthenticated', 'true');
        this.$router.push('/admin');
      } else {
        this.error = '아이디 또는 비밀번호가 올바르지 않습니다.';
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.login-box {
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.form-group {
  margin-bottom: 1rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
}

input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

button {
  width: 100%;
  padding: 0.75rem;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}

.error-message {
  color: red;
  margin-bottom: 1rem;
}
</style> 