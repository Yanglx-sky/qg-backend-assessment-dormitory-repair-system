import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue')
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/RegisterView.vue')
  },
  {
    path: '/student',
    name: 'student',
    component: () => import('../views/student/StudentIndexView.vue'),
    children: [
      {
        path: '',
        name: 'student-home',
        component: () => import('../views/student/StudentHomeView.vue')
      },
      {
        path: 'dorm',
        name: 'student-dorm',
        component: () => import('../views/student/StudentDormView.vue')
      },
      {
        path: 'orders',
        name: 'student-orders',
        component: () => import('../views/student/StudentOrdersView.vue')
      },
      {
        path: 'submit',
        name: 'student-submit',
        component: () => import('../views/student/StudentSubmitView.vue')
      }
    ]
  },
  {
    path: '/repairman',
    name: 'repairman',
    component: () => import('../views/repairman/RepairmanIndexView.vue'),
    children: [
      {
        path: '',
        name: 'repairman-home',
        component: () => import('../views/repairman/RepairmanHomeView.vue')
      },
      {
        path: 'orders',
        name: 'repair-orders',
        component: () => import('../views/repairman/RepairOrdersView.vue')
      },
      {
        path: 'accept',
        name: 'repair-accept',
        component: () => import('../views/repairman/RepairAcceptView.vue')
      }
    ]
  },
  {
    path: '/admin',
    name: 'admin',
    component: () => import('../views/admin/AdminIndexView.vue'),
    children: [
      {
        path: '',
        name: 'admin-home',
        component: () => import('../views/admin/AdminHomeView.vue')
      },
      {
        path: 'orders',
        name: 'admin-orders',
        component: () => import('../views/admin/AdminOrdersView.vue')
      },
      {
        path: 'dormitories',
        name: 'admin-dormitories',
        component: () => import('../views/admin/AdminDormitoriesView.vue')
      },
      {
        path: 'students',
        name: 'admin-students',
        component: () => import('../views/admin/AdminStudentsView.vue')
      },
      {
        path: 'repairmen',
        name: 'admin-repairmen',
        component: () => import('../views/admin/AdminRepairmenView.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const isLogin = localStorage.getItem('accessToken')
  const publicPages = ['/login', '/register']
  const authRequired = !publicPages.includes(to.path)

  console.log('路由守卫检查:', {
    to: to.path,
    from: from.path,
    isLogin,
    authRequired
  })

  if (authRequired && !isLogin) {
    console.log('未登录，跳转到登录页')
    next('/login')
  } else {
    console.log('允许访问', to.path)
    next()
  }
})

export default router