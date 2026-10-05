export type Role = 'ADMIN' | 'EMPLOYEE'
export interface User { username: string; role: Role; employeeId?: string }
export interface Department { id: string; name: string; description?: string; employeeCount: number }
export interface Position { id: string; name: string; description?: string; departmentId?: string; departmentName?: string; employeeCount: number }
export interface Employee { id: string; employeeNo: string; name: string; phone?: string; email?: string; address?: string; education?: string; departmentId: string; departmentName: string; positionId: string; positionName: string; hireDate: string; status: 'ACTIVE' | 'RESIGNED'; createdAt: string; updatedAt: string }
export interface Page<T> { content: T[]; total: number; page: number; size: number }
export interface Dashboard { totalEmployees: number; activeEmployees: number; resignedEmployees: number; departmentCount: number; positionCount: number; departmentDistribution: { name: string; count: number }[]; recentChanges: Change[] }
export interface Change { id: string; type: 'ONBOARD' | 'TRANSFER' | 'RESIGNATION'; employeeName: string; employeeNo: string; occurredAt: string; reason: string; operator: string }
