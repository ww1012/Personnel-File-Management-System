<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'; import { ElMessage, ElMessageBox } from 'element-plus'; import { api } from '../api'; import type { Department, Position } from '../types'
const props = defineProps<{ kind: 'department' | 'position' }>(); const list = ref<(Department | Position)[]>([]), dialog = ref(false), editing = ref<Department | Position>(), form = ref({ name: '', description: '', departmentId: '' }), departments = ref<Department[]>([]); const filter = reactive({ name: '', departmentId: '' }); const title = computed(() => props.kind === 'department' ? '部门' : '岗位');
async function load() { if (props.kind === 'department') list.value = await api.departments(); else list.value = await api.positions({ departmentId: filter.departmentId, name: filter.name }) } function search() { load() } function resetFilter() { Object.assign(filter, { name: '', departmentId: '' }); load() } function open(row?: Department | Position) { editing.value = row; form.value = { name: row?.name || '', description: row?.description || '', departmentId: (row as Position)?.departmentId || '' }; dialog.value = true } async function save() { try { props.kind === 'department' ? await api.saveDepartment(editing.value?.id, form.value) : await api.savePosition(editing.value?.id, form.value); ElMessage.success('保存成功'); dialog.value = false; load() } catch (e: any) { ElMessage.error(e.response?.data?.message || '保存失败') } } async function remove(row: Department | Position) { try { await ElMessageBox.confirm(`确认删除"${row.name}"吗？`, '删除确认', { type: 'warning' }); props.kind === 'department' ? await api.deleteDepartment(row.id) : await api.deletePosition(row.id); ElMessage.success('已删除'); load() } catch (e: any) { if (e !== 'cancel') ElMessage.error(e.response?.data?.message || '删除失败') } } onMounted(async () => { await load(); if (props.kind === 'position') departments.value = await api.departments() })
watch(() => props.kind, async () => { editing.value = undefined; dialog.value = false; Object.assign(filter, { name: '', departmentId: '' }); await load(); if (props.kind === 'position') departments.value = await api.departments() })
</script>
<template>
    <div>
        <h2>{{ title }}管理</h2>
        <p class="muted">维护可供员工档案关联的{{ title }}字典</p>
        <section class="page-card">
            <div class="toolbar"><el-button type="primary" @click="open()">+ 新增{{ title }}</el-button></div>
            <div v-if="props.kind === 'position'" class="toolbar filter-bar"><el-input v-model="filter.name"
                    placeholder="岗位名称" clearable style="width:200px" @keyup.enter="search" /><el-select
                    v-model="filter.departmentId" placeholder="全部部门" clearable style="width:160px"><el-option
                        v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" /></el-select><el-button
                    type="primary" @click="search">搜索</el-button><el-button @click="resetFilter">重置</el-button></div>
            <el-table :data="list"><el-table-column prop="name" :label="title + '名称'" /><el-table-column
                    prop="description" label="说明" /><el-table-column v-if="props.kind === 'position'"
                    prop="departmentName" label="所属部门" /><el-table-column prop="employeeCount" label="关联员工数"
                    width="120" /><el-table-column label="操作" width="140"><template #default="{ row }"><el-button link
                            type="primary" @click="open(row)">编辑</el-button><el-button link type="danger"
                            @click="remove(row)">删除</el-button></template></el-table-column><template #empty><el-empty
                        :description="'暂无' + title + '数据'" /></template></el-table>
        </section> <el-dialog v-model="dialog" :title="(editing ? '编辑' : '新增') + title" width="480px"><el-form
                label-width="80px"><el-form-item :label="title + '名称'" required><el-input
                        v-model="form.name" /></el-form-item><el-form-item v-if="props.kind === 'position'"
                    label="所属部门"><el-select v-model="form.departmentId" clearable placeholder="不限定部门"><el-option
                            v-for="d in departments" :key="d.id" :label="d.name"
                            :value="d.id" /></el-select></el-form-item><el-form-item label="说明"><el-input
                        v-model="form.description" type="textarea" /></el-form-item></el-form><template
                #footer><el-button @click="dialog = false">取消</el-button><el-button type="primary"
                    @click="save">保存</el-button></template></el-dialog>
    </div>
</template>
<style scoped>
.toolbar {
    margin-bottom: 12px
}

.filter-bar .el-input,
.filter-bar .el-select {
    margin-right: 8px
}
</style>
