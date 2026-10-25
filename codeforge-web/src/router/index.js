import { createRouter, createWebHistory } from 'vue-router'
import Login from '../components/Login.vue'
import Register from '../components/Register.vue'
import ProblemList from '../components/ProblemList.vue'
import ProblemDetail from '../components/ProblemDetail.vue'
import ProblemForm from '../components/ProblemForm.vue'
import SubmissionList from '../components/SubmissionList.vue'
import SubmissionDetail from '../components/SubmissionDetail.vue'
import UserProfile from '../components/UserProfile.vue'
import ReviewPage from '../components/ReviewPage.vue'
import MyProblems from '../components/MyProblems.vue'
import ProblemSetList from '../components/ProblemSetList.vue'
import ProblemSetDetail from '../components/ProblemSetDetail.vue'
import ProblemSetForm from '../components/ProblemSetForm.vue'
import ExamList from '../components/ExamList.vue'
import ExamDetail from '../components/ExamDetail.vue'
import ExamForm from '../components/ExamForm.vue'
import AdminUserList from '../components/AdminUserList.vue'

const routes = [
  { path: '/', redirect: '/login' },
  // hideNav：登录/注册页不显示顶部导航栏
  { path: '/login', component: Login, meta: { hideNav: true } },
  { path: '/register', component: Register, meta: { hideNav: true } },
  { path: '/problems', component: ProblemList },
  { path: '/problem/create', component: ProblemForm },
  { path: '/problem/:id/edit', component: ProblemForm },
  { path: '/problem/:id', component: ProblemDetail },
  { path: '/submissions', component: SubmissionList },
  { path: '/submission/:id', component: SubmissionDetail },
  { path: '/profile', component: UserProfile },
  { path: '/review', component: ReviewPage },
  { path: '/my-problems', component: MyProblems },
  { path: '/problem-sets', component: ProblemSetList },
  { path: '/problem-set/create', component: ProblemSetForm },
  { path: '/problem-set/:id/edit', component: ProblemSetForm },
  { path: '/problem-set/:id', component: ProblemSetDetail },
  { path: '/exams', component: ExamList },
  { path: '/exam/create', component: ExamForm },
  { path: '/exam/:id/edit', component: ExamForm },
  { path: '/exam/:id', component: ExamDetail },
  { path: '/admin/users', component: AdminUserList },
  // 访问不存在的路径时回到题目列表，避免出现空白页
  { path: '/:pathMatch(.*)*', redirect: '/problems' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 仅管理员 / 仅出题人可进的页面，路由层提前拦掉，避免用户进去只看到 403
const ADMIN_PAGES = ['/admin/users', '/review']
const AUTHOR_PATHS = [/^\/problem\/create$/, /^\/problem\/\d+\/edit$/, /^\/my-problems$/]

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role') || ''
  const publicPages = ['/login', '/register']

  // 已登录时不再停留在登录/注册页
  if (publicPages.includes(to.path)) {
    return token ? '/problems' : true
  }
  if (!token) {
    return '/login'
  }
  if (ADMIN_PAGES.includes(to.path) && role !== 'ADMIN') {
    return '/problems'
  }
  if (AUTHOR_PATHS.some(pattern => pattern.test(to.path)) && role !== 'AUTHOR' && role !== 'ADMIN') {
    return '/problems'
  }
})

export default router