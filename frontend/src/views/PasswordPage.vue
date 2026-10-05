<script setup lang="ts">
import { reactive, ref } from 'vue'; import { useRouter } from 'vue-router'; import { ElMessage } from 'element-plus'; import { api } from '../api'
const router = useRouter(), loading = ref(false), form = reactive({ currentPassword: '', newPassword: '', confirmPassword: '' })
async function save() { if (form.newPassword !== form.confirmPassword) { ElMessage.error('两次输入的新密码不一致'); return } loading.value = true; try { await api.changePassword(form.currentPassword, form.newPassword); ElMessage.success('密码已修改，请重新登录'); localStorage.clear(); router.replace('/login') } catch (e: any) { ElMessage.error(e.response?.data?.message || '修改失败') } finally { loading.value = false } }
</script>
<template>
    <main class="password"><el-card>
            <h2>修改密码</h2><el-form label-width="96px"><el-form-item label="当前密码" required><el-input
                        v-model="form.currentPassword" type="password" show-password /></el-form-item><el-form-item
                    label="新密码" required><el-input v-model="form.newPassword" type="password"
                        show-password /></el-form-item><el-form-item label="确认新密码" required><el-input
                        v-model="form.confirmPassword" type="password" show-password /></el-form-item><el-button
                    @click="router.back()">取消</el-button><el-button type="primary" :loading="loading"
                    @click="save">保存并重新登录</el-button></el-form>
        </el-card></main>
</template>
<style scoped>
.password {
    max-width: 520px;
    margin: 90px auto;
    padding: 0 20px
}
</style>
