import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/RegisterView.vue')
  },
  {
    path: '/student',
    name: 'StudentIndex',
    component: () => import('../views/student/StudentIndexView.vue'),
    children: [
      {
        path: '',
        component: () => import('../views/student/StudentHomeView.vue')
      },
      {
        path: 'dorm',
        component: () => import('../views/student/StudentDormView.vue')
      },
      {
        path: 'orders',
        component: () => import('../views/student/StudentOrdersView.vue')
      },
      {
        path: 'submit',
        component: () => import('../views/student/StudentSubmitView.vue')
      }
    ]
  },
  {
    path: '/admin',
    name: 'AdminIndex',
    component: () => import('../views/admin/AdminIndexView.vue'),
    children: [
      {
        path: '',
        component: () => import('../views/admin/AdminHomeView.vue')
      },
      {
        path: 'orders',
        component: () => import('../views/admin/AdminOrdersView.vue')
      },
      {
        path: 'students',
        component: () => import('../views/admin/AdminStudentsView.vue')
      },
      {
        path: 'repairmen',
        component: () => import('../views/admin/AdminRepairmenView.vue')
      },
      {
        path: 'dormitories',
        component: () => import('../views/admin/AdminDormitoriesView.vue')
      }
    ]
  },
  {
    path: '/repairman',
    name: 'RepairmanIndex',
    component: () => import('../views/repairman/RepairmanIndexView.vue'),
    children: [
      {
        path: '',
        component: () => import('../views/repairman/RepairmanHomeView.vue')
      },
      {
        path: 'accept',
        component: () => import('../views/repairman/RepairAcceptView.vue')
      },
      {
        path: 'orders',
        component: () => import('../views/repairman/RepairOrdersView.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router