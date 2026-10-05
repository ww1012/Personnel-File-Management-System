import { createRouter, createWebHashHistory } from 'vue-router'
import LoginPage from './views/LoginPage.vue'
import AdminLayout from './views/AdminLayout.vue'
import DashboardPage from './views/DashboardPage.vue'
import EmployeeListPage from './views/EmployeeListPage.vue'
import MyProfilePage from './views/MyProfilePage.vue'
import DictionaryPage from './views/DictionaryPage.vue'
import ChangePage from './views/ChangePage.vue'
import AccountPage from './views/AccountPage.vue'
import PasswordPage from './views/PasswordPage.vue'
import { api } from './api'
const router = createRouter({ history: createWebHashHistory(), routes: [
  { path: '/login', component: LoginPage },
  { path: '/admin', component: AdminLayout, children: [{ path: '', redirect: '/admin/dashboard' }, { path: 'dashboard', component: DashboardPage }, { path: 'employees', component: EmployeeListPage }, { path: 'departments', component: DictionaryPage, props:{kind:'department'} }, { path: 'positions', component: DictionaryPage, props:{kind:'position'} }, { path: 'changes', component: ChangePage }, { path: 'accounts', component: AccountPage }] },
  { path: '/profile', component: MyProfilePage, meta:{role:'EMPLOYEE'} }, { path: '/password', component: PasswordPage }, { path: '/:pathMatch(.*)*', redirect: '/login' }
] })
router.beforeEach(async to => {
  if (to.path === '/login') return true
  if (!localStorage.getItem('token')) return '/login'
  try { const user=await api.currentUser(); localStorage.setItem('user', JSON.stringify(user)); if(to.path.startsWith('/admin') && user.role!=='ADMIN') return '/profile'; if(to.meta.role==='EMPLOYEE' && user.role!=='EMPLOYEE') return '/admin/dashboard'; return true } catch { return '/login' }
})
export default router
