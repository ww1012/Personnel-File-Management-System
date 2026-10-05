<script setup lang="ts">
import { ref } from 'vue'; import { useRouter } from 'vue-router'; import { ElMessage } from 'element-plus'; import { api } from '../api'
const router = useRouter(), form = ref({ username: '', password: '' }), loading = ref(false)
async function submit() { loading.value = true; try { const r = await api.login(form.value.username, form.value.password); localStorage.setItem('token', r.token); localStorage.setItem('user', JSON.stringify(r.user)); router.replace(r.user.role === 'ADMIN' ? '/admin/dashboard' : '/profile') } catch (e: any) { ElMessage.error(e.response?.data?.message || (e.request ? '后端服务不可用，请确认后端已启动' : '登录失败，请检查账号和密码')) } finally { loading.value = false } }
</script>
<template>
    <main class="login">
        <section class="brand">
            <div class="logo">人</div>
            <h1>人事管理系统</h1>
            <p>人员信息统一管理平台</p>
        </section><el-card class="login-card">
            <h2>账号登录</h2>
            <p class="muted">请输入账号和密码登录系统</p><el-form :model="form" @submit.prevent="submit"><el-form-item><el-input
                        v-model="form.username" placeholder="账号" size="large" /></el-form-item><el-form-item><el-input
                        v-model="form.password" type="password" show-password placeholder="密码" size="large"
                        @keyup.enter="submit" /></el-form-item><el-button type="primary" size="large" :loading="loading"
                    class="submit" @click="submit">登录</el-button></el-form>
        </el-card>
    </main>
</template>
<style
    scoped>
    .login {
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 100px;
        background: linear-gradient(120deg, #eef6ff, #f8fbff)
    }

    .brand {
        color: #175cd3
    }

    .logo {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        background: #2563eb;
        color: #fff;
        display: grid;
        place-items: center;
        font-size: 24px
    }

    .brand h1 {
        margin: 16px 0 8px
    }

    .login-card {
        width: 380px;
        padding: 16px
    }

    .submit {
        width: 100%
    }

    @media(max-width:700px) {
        .login {
            gap: 30px;
            flex-direction: column
        }

        .brand {
            text-align: center
        }

        .logo {
            margin: auto
        }
    }
</style>
