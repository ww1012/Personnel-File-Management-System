<script setup lang="ts">
import { useRouter } from 'vue-router'; import { api } from '../api'; const router = useRouter(); async function logout() { await api.logout().catch(() => undefined); localStorage.removeItem('token'); localStorage.removeItem('user'); router.replace('/login') }
</script>
<template><el-container class="shell"><el-aside width="220px">
            <div class="side-title">▣ 人事管理系统</div><el-menu router :default-active="$route.path"
                background-color="#142b4a" text-color="#cbd5e1" active-text-color="#fff"><el-menu-item
                    index="/admin/dashboard">工作台</el-menu-item><el-menu-item
                    index="/admin/employees">员工管理</el-menu-item><el-menu-item
                    index="/admin/departments">部门管理</el-menu-item><el-menu-item
                    index="/admin/positions">岗位管理</el-menu-item><el-menu-item
                    index="/admin/changes">人事变动</el-menu-item><el-menu-item
                    index="/admin/accounts">职员账号管理</el-menu-item></el-menu>
        </el-aside><el-container><el-header><span>人事管理 / 管理后台</span><el-button link
                    @click="logout">退出登录</el-button></el-header><el-main><router-view /></el-main></el-container></el-container></template>
<style
    scoped>
    .shell {
        min-height: 100vh
    }

    .el-aside {
        background: #142b4a
    }

    .side-title {
        height: 64px;
        display: flex;
        align-items: center;
        padding: 0 22px;
        color: #fff;
        font-weight: 700
    }

    .el-menu {
        border: 0
    }

    .el-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        background: #fff;
        border-bottom: 1px solid #e5e7eb
    }

    .el-main {
        padding: 24px
    }
</style>
