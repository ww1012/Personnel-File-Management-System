<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'; import { ElMessage } from 'element-plus'; import { api } from '../api'; import type { Department, Employee, Position } from '../types'
const rows = ref<Employee[]>([]), total = ref(0), loading = ref(false), error = ref(''), departments = ref<Department[]>([]), positions = ref<Position[]>([]), dialog = ref(false), editing = ref<Employee>(), detail = ref<Employee>(), transferDialog = ref(false), resignDialog = ref(false), changing = ref<Employee>(), submitting = ref(false), query = reactive({ keyword: '', departmentId: '', status: '', page: 1, size: 10 })
const form = reactive({ employeeNo: '', name: '', phone: '', email: '', address: '', education: '', departmentId: '', positionId: '', hireDate: '', status: 'ACTIVE' })
const transferForm = reactive({ departmentId: '', positionId: '', occurredAt: '', reason: '' }), resignForm = reactive({ occurredAt: '', reason: '' })
function resetForm(e?: Employee) { Object.assign(form, e ? { employeeNo: e.employeeNo, name: e.name, phone: e.phone || '', email: e.email || '', address: e.address || '', education: e.education || '', departmentId: e.departmentId, positionId: e.positionId, hireDate: e.hireDate, status: e.status } : { employeeNo: '', name: '', phone: '', email: '', address: '', education: '', departmentId: '', positionId: '', hireDate: '', status: 'ACTIVE' }) }
async function load() { loading.value = true; error.value = ''; try { const p = await api.employees(query); rows.value = p.content; total.value = p.total } catch { error.value = '员工列表加载失败' } finally { loading.value = false } }
async function openEdit(e?: Employee) { editing.value = e; resetForm(e); dialog.value = true } async function save() { try { if (editing.value) await api.updateEmployee(editing.value.id, form); else await api.createEmployee(form); ElMessage.success('保存成功'); dialog.value = false; load() } catch (e: any) { ElMessage.error(e.response?.data?.message || '保存失败') } }
function nowLocal() { const d = new Date(); const p = (n: number) => String(n).padStart(2, '0'); return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}` } function openTransfer(e: Employee) { changing.value = e; Object.assign(transferForm, { departmentId: e.departmentId, positionId: e.positionId, occurredAt: nowLocal(), reason: '' }); transferDialog.value = true } function openResign(e: Employee) { changing.value = e; Object.assign(resignForm, { occurredAt: nowLocal(), reason: '' }); resignDialog.value = true } async function transfer() { if (!changing.value) return; submitting.value = true; try { await api.transfer(changing.value.id, transferForm); ElMessage.success('调岗已保存'); transferDialog.value = false; load() } catch (e: any) { ElMessage.error(e.response?.data?.message || '调岗失败') } finally { submitting.value = false } } async function resign() { if (!changing.value) return; submitting.value = true; try { await api.resign(changing.value.id, resignForm); ElMessage.success('离职已办理，关联账号已停用'); resignDialog.value = false; load() } catch (e: any) { ElMessage.error(e.response?.data?.message || '离职失败') } finally { submitting.value = false } }
function positionsOf(deptId: string) { return positions.value.filter(p => !deptId || p.departmentId === deptId) }
onMounted(async () => { await Promise.all([load(), api.departments().then(x => departments.value = x), api.positions().then(x => positions.value = x)]) })
</script>
<template>
    <div>
        <h2>员工列表</h2>
        <p class="muted">管理员工档案、任职信息和联系方式</p>
        <section class="page-card">
            <div class="toolbar"><el-input v-model="query.keyword" placeholder="工号、姓名或手机号" clearable
                    @keyup.enter="query.page = 1; load()" /><el-select v-model="query.departmentId" placeholder="全部部门"
                    clearable><el-option v-for="d in departments" :key="d.id" :value="d.id"
                        :label="d.name" /></el-select><el-select v-model="query.status" placeholder="全部状态"
                    clearable><el-option label="在职" value="ACTIVE" /><el-option label="离职"
                        value="RESIGNED" /></el-select><el-button type="primary"
                    @click="query.page = 1; load()">搜索</el-button><el-button
                    @click="Object.assign(query, { keyword: '', departmentId: '', status: '', page: 1 }); load()">重置</el-button><el-button
                    type="primary" class="add" @click="openEdit()">+ 新增员工</el-button></div><el-alert v-if="error"
                :title="error" type="error" show-icon><template #default><el-button link
                        @click="load">重试</el-button></template></el-alert><el-table v-else v-loading="loading"
                :data="rows"><el-table-column prop="employeeNo" label="工号" width="110" /><el-table-column prop="name"
                    label="姓名" width="100" /><el-table-column prop="departmentName" label="部门" /><el-table-column
                    prop="positionName" label="岗位" /><el-table-column prop="phone" label="手机号"
                    width="130" /><el-table-column prop="hireDate" label="入职日期" width="120" /><el-table-column
                    label="状态" width="90"><template #default="{ row }"><el-tag
                            :type="row.status === 'ACTIVE' ? 'success' : 'info'">{{ row.status === 'ACTIVE' ? '在职' : '离职' }}</el-tag></template></el-table-column><el-table-column
                    label="操作" width="230" fixed="right"><template #default="{ row }"><el-button link type="primary"
                            @click="detail = row">详情</el-button><el-button link type="primary"
                            @click="openEdit(row)">编辑</el-button><el-button v-if="row.status === 'ACTIVE'" link
                            type="warning" @click="openTransfer(row)">调岗</el-button><el-button
                            v-if="row.status === 'ACTIVE'" link type="danger"
                            @click="openResign(row)">离职</el-button></template></el-table-column><template
                    #empty><el-empty description="暂无匹配员工" /></template></el-table>
            <div class="pager"><el-pagination v-model:current-page="query.page" v-model:page-size="query.size"
                    :total="total" layout="total, prev, pager, next" @current-change="load" /></div>
        </section><el-dialog v-model="dialog" :title="editing ? '编辑员工资料' : '新增员工'" width="640px"><el-form
                label-width="90px"><el-form-item label="工号" required><el-input v-model="form.employeeNo"
                        :disabled="!!editing" /></el-form-item><el-form-item label="姓名" required><el-input
                        v-model="form.name" /></el-form-item>                    <el-form-item label="部门" required><el-select
                        v-model="form.departmentId" :disabled="!!editing" @change="form.positionId = ''"><el-option v-for="d in departments"
                            :key="d.id" :label="d.name" :value="d.id" /></el-select></el-form-item><el-form-item
                    label="岗位" required><el-select v-model="form.positionId" :disabled="!!editing"><el-option
                            v-for="p in positionsOf(form.departmentId)" :key="p.id" :label="p.name"
                            :value="p.id" /></el-select></el-form-item><el-form-item label="入职日期"
                    required><el-date-picker v-model="form.hireDate" type="date"
                        value-format="YYYY-MM-DD" /></el-form-item><el-form-item label="手机号"><el-input
                        v-model="form.phone" /></el-form-item><el-form-item label="邮箱"><el-input
                        v-model="form.email" /></el-form-item><el-form-item label="联系地址"><el-input
                        v-model="form.address" /></el-form-item><el-form-item label="学历"><el-input
                        v-model="form.education" /></el-form-item></el-form><template #footer><el-button
                    @click="dialog = false">取消</el-button><el-button type="primary"
                    @click="save">保存</el-button></template></el-dialog><el-dialog v-model="transferDialog" title="办理调岗"
            width="500px">
            <p class="muted">调岗会记录变更前后部门和岗位。</p><el-form label-width="90px"><el-form-item label="新部门"
                    required><el-select v-model="transferForm.departmentId" @change="transferForm.positionId = ''"><el-option v-for="d in departments"
                            :key="d.id" :label="d.name" :value="d.id" /></el-select></el-form-item><el-form-item
                    label="新岗位" required><el-select v-model="transferForm.positionId"><el-option v-for="p in positionsOf(transferForm.departmentId)"
                            :key="p.id" :label="p.name" :value="p.id" /></el-select></el-form-item><el-form-item
                    label="变动日期" required><el-date-picker v-model="transferForm.occurredAt" type="datetime"
                        value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item><el-form-item label="原因" required><el-input
                        v-model="transferForm.reason" type="textarea" /></el-form-item></el-form><template
                #footer><el-button @click="transferDialog = false">取消</el-button><el-button type="primary"
                    :loading="submitting" @click="transfer">确认调岗</el-button></template>
        </el-dialog><el-dialog v-model="resignDialog" title="办理离职" width="500px"><el-alert title="离职后档案会保留，关联职员账号将立即停用。"
                type="warning" :closable="false" /><el-form label-width="90px" style="margin-top:16px"><el-form-item
                    label="离职日期" required><el-date-picker v-model="resignForm.occurredAt" type="datetime"
                        value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item><el-form-item label="离职原因" required><el-input
                        v-model="resignForm.reason" type="textarea" /></el-form-item></el-form><template
                #footer><el-button @click="resignDialog = false">取消</el-button><el-button type="danger"
                    :loading="submitting" @click="resign">确认办理离职</el-button></template></el-dialog><el-drawer
            v-model="detail" title="员工详情" size="420px"><el-descriptions v-if="detail" :column="1"
                border><el-descriptions-item label="员工 ID">{{ detail.id }}</el-descriptions-item><el-descriptions-item
                    label="工号">{{ detail.employeeNo }}</el-descriptions-item><el-descriptions-item
                    label="姓名">{{ detail.name }}</el-descriptions-item><el-descriptions-item
                    label="部门/岗位">{{ detail.departmentName }} /
                    {{ detail.positionName }}</el-descriptions-item><el-descriptions-item
                    label="入职日期">{{ detail.hireDate }}</el-descriptions-item><el-descriptions-item
                    label="手机号">{{ detail.phone }}</el-descriptions-item><el-descriptions-item
                    label="邮箱">{{ detail.email }}</el-descriptions-item><el-descriptions-item
                    label="联系地址">{{ detail.address }}</el-descriptions-item><el-descriptions-item
                    label="学历">{{ detail.education }}</el-descriptions-item></el-descriptions></el-drawer>
    </div>
</template>
<style
    scoped>
    .toolbar .el-select {
        width: 140px
    }

    .add {
        margin-left: auto
    }

    .pager {
        display: flex;
        justify-content: flex-end;
        margin-top: 16px
    }
</style>
