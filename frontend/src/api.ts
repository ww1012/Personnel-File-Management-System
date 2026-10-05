import axios from 'axios'
import type { Dashboard, Department, Employee, Page, Position, User } from './types'

const client = axios.create({ baseURL: '/api' })
client.interceptors.request.use(c => { const token = localStorage.getItem('token'); if (token) c.headers.Authorization = `Bearer ${token}`; return c })
client.interceptors.response.use(r => r, e => { if (e.response?.status === 401) { localStorage.removeItem('token'); location.hash = '#/login' } return Promise.reject(e) })
const get = <T>(url: string, params?: object) => client.get<T>(url, { params }).then(r => r.data)
export const api = {
  login: (username: string, password: string) => client.post<{ token: string; user: User }>('/auth/login', { username, password }).then(r => r.data),
  logout: () => client.post('/auth/logout'), currentUser: () => get<User>('/auth/me'), changePassword: (currentPassword: string, newPassword: string) => client.put('/auth/password', { currentPassword, newPassword }),
  dashboard: () => get<Dashboard>('/dashboard'),
  employees: (params: object) => get<Page<Employee>>('/employees', params), employee: (id: string) => get<Employee>(`/employees/${id}`), createEmployee: (body: object) => client.post('/employees', body), updateEmployee: (id: string, body: object) => client.put(`/employees/${id}`, body),
  departments: () => get<Department[]>('/departments'), positions: (params?: { departmentId?: string; name?: string }) => get<Position[]>('/positions', params),
  saveDepartment: (id: string | undefined, body: object) => id ? client.put(`/departments/${id}`, body) : client.post('/departments', body), deleteDepartment: (id: string) => client.delete(`/departments/${id}`),
  savePosition: (id: string | undefined, body: object) => id ? client.put(`/positions/${id}`, body) : client.post('/positions', body), deletePosition: (id: string) => client.delete(`/positions/${id}`),
  changes: (params: object) => get<Page<any>>('/changes', params), transfer: (id: string, body: object) => client.post(`/employees/${id}/transfer`, body), resign: (id: string, body: object) => client.post(`/employees/${id}/resign`, body),
  accounts: () => get<any[]>('/accounts'), createAccount: (body: object) => client.post('/accounts', body), disableAccount: (username: string) => client.post(`/accounts/${username}/disable`), resetAccountPassword: (username: string, password: string) => client.post(`/accounts/${username}/reset-password`, { password }),
  myProfile: () => get<Employee>('/profile'), updateMyProfile: (body: object) => client.put('/profile', body)
}
