<script>
import api from '@/api';

export default {
  data() {
    return {
      message: '로딩 중...'
    };
  },
  mounted() {
    api.get('/hello')
      .then(response => {
        this.message = response.data;
      })
      .catch(error => {
        console.error(error);
        this.message = 'API 요청 실패!';
      });
  }
};
</script>

<template>
  <div>
    <h1>API 테스트</h1>
    <p>{{ message }}</p>
  </div>
</template>

<style scoped>

</style>
