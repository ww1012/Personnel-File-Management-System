<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { api } from '../api'
const rows = ref<any[]>([]), keyword = ref(''), type = ref('')
async function load() { rows.value = (await api.changes({ keyword: keyword.value, type: type.value })).content }
onMounted(load)
</script>
<template>
    <div>
        <h2>人事变动</h2>
        <p class="muted">查看入职、调岗与离职历史记录</p>
        <section class="page-card">
            <div class="toolbar"><el-input v-model="keyword" placeholder="工号或姓名" /><el-select v-model="type"
                    placeholder="全部类型" clearable><el-option label="入职" value="ONBOARD" /><el-option label="调岗"
                        value="TRANSFER" /><el-option label="离职" value="RESIGNATION" /></el-select><el-button
                    type="primary" @click="load">搜索</el-button></div><el-table :data="rows"><el-table-column
                    prop="occurredAt" label="时间" /><el-table-column prop="employeeNo" label="工号" /><el-table-column
                    prop="employeeName" label="姓名" /><el-table-column prop="type" label="类型" /><el-table-column
                    prop="reason" label="变动说明" /><el-table-column prop="operator" label="操作人" /></el-table>
        </section>
    </div>
</template>
