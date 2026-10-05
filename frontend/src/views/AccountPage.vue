<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../api'
import type { Employee } from '../types'
const rows = ref<any[]>([]), employees = ref<Employee[]>([]), dialog = ref(false), resetDialog = ref(false), resetting = ref<any>(), form = ref({ employeeId: '', username: '', password: '' }), resetPassword = ref('')
async function load() { rows.value = await api.accounts() }
async function open() { employees.value = (await api.employees({ page: 1, size: 100 })).content.filter(e => e.status === 'ACTIVE'); dialog.value = true }
async function save() { try { await api.createAccount(form.value); ElMessage.success('账号已创建'); dialog.value = false; load() } catch (e: any) { ElMessage.error(e.response?.data?.message || '创建失败') } }
async function disable(row: any) { try { await ElMessageBox.confirm(`确认停用账号 ${row.username} 吗？`, '停用确认', { type: 'warning' }); await api.disableAccount(row.username); ElMessage.success('账号已停用'); load() } catch (e: any) { if (e !== 'cancel') ElMessage.error(e.response?.data?.message || '操作失败') } }
async function reset() { if (!resetting.value || !resetPassword.value) { ElMessage.error('请输入新初始密码'); return } try { await ElMessageBox.confirm(`重置后 ${resetting.value.username} 的旧登录凭证会失效。`, '重置密码确认', { type: 'warning' }); await api.resetAccountPassword(resetting.value.username, resetPassword.value); ElMessage.success('密码已重置'); resetDialog.value = false; resetPassword.value = '' } catch (e: any) { if (e !== 'cancel') ElMessage.error(e.response?.data?.message || '重置失败') } }
onMounted(load)
</script>
<template>
    <div>
        <h2>职员账号管理</h2>
        <p class="muted">创建职员登录账号，或停用不再使用的账号</p>
        <section class="page-card">
            <div class="toolbar"><el-button type="primary" @click="open">+ 创建职员账号</el-button></div><el-table
                :data="rows"><el-table-column prop="username" label="登录账号" /><el-table-column prop="employeeNo"
                    label="工号" /><el-table-column prop="employeeName" label="关联员工" /><el-table-column
                    label="状态"><template #default="{ row }"><el-tag
                            :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? '正常' : '已停用' }}</el-tag></template></el-table-column><el-table-column
                    label="操作" width="160"><template #default="{ row }"><el-button link type="primary"
                            @click="resetting = row; resetDialog = true">重置密码</el-button><el-button link type="danger"
                            :disabled="!row.enabled"
                            @click="disable(row)">停用</el-button></template></el-table-column></el-table>
        </section><el-dialog v-model="dialog" title="创建职员账号" width="480px"><el-form label-width="90px"><el-form-item
                    label="关联员工" required><el-select v-model="form.employeeId"><el-option v-for="e in employees"
                            :key="e.id" :label="e.employeeNo + ' ' + e.name"
                            :value="e.id" /></el-select></el-form-item><el-form-item label="登录账号" required><el-input
                        v-model="form.username" /></el-form-item><el-form-item label="初始密码" required><el-input
                        v-model="form.password" type="password" show-password /></el-form-item></el-form><template
                #footer><el-button @click="dialog = false">取消</el-button><el-button type="primary"
                    @click="save">创建</el-button></template></el-dialog><el-dialog v-model="resetDialog" title="重置职员密码"
            width="420px">
            <p>账号：{{ resetting?.username }}</p><el-input v-model="resetPassword" type="password" show-password
                placeholder="输入新的初始密码" /><template #footer><el-button
                    @click="resetDialog = false">取消</el-button><el-button type="warning"
                    @click="reset">确认重置</el-button></template>
        </el-dialog>
    </div>
</template>
