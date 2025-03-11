import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import AdminView from '../views/AdminView.vue'
import AdminLoginView from '../views/AdminLoginView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/admin/login',
      name: 'adminLogin',
      component: AdminLoginView
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminView,
      beforeEnter: (to, from, next) => {
        if (!localStorage.getItem('adminAuthenticated')) {
          next('/admin/login');
        } else {
          next();
        }
      }
    }
  ],
})

export default router
