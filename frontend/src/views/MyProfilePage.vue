<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'; import { useRouter } from 'vue-router'; import { ElMessage } from 'element-plus'; import { api } from '../api'; import type { Employee } from '../types'
const router = useRouter(), profile = ref<Employee>(), edit = ref(false), loading = ref(true), form = reactive({ phone: '', email: '', address: '', education: '' })
async function load() { loading.value = true; try { profile.value = await api.myProfile(); Object.assign(form, { phone: profile.value.phone || '', email: profile.value.email || '', address: profile.value.address || '', education: profile.value.education || '' }) } catch { ElMessage.error('个人档案加载失败') } finally { loading.value = false } } async function save() { try { await api.updateMyProfile(form); ElMessage.success('资料已保存'); edit.value = false; load() } catch (e: any) { ElMessage.error(e.response?.data?.message || '保存失败') } } async function logout() { await api.logout().catch(() => undefined); localStorage.removeItem('token'); localStorage.removeItem('user'); router.replace('/login') } onMounted(load)
</script>
<template>
    <main class="profile">
        <header>
            <div>
                <h2>我的资料</h2>
                <p class="muted">查看个人档案，维护联系方式</p>
            </div>
            <div><el-button link @click="router.push('/password')">修改密码</el-button><el-button link
                    @click="logout">退出登录</el-button></div>
        </header><el-skeleton v-if="loading" :rows="10" animated /><template v-else-if="profile">
            <section class="hero">
                <div class="avatar">{{ profile.name.slice(0, 1) }}</div>
                <div>
                    <h2>{{ profile.name }}</h2>
                    <p>{{ profile.employeeNo }} · <el-tag
                            :type="profile.status === 'ACTIVE' ? 'success' : 'info'">{{ profile.status === 'ACTIVE' ? '在职' : '离职' }}</el-tag>
                    </p>
                </div>
            </section>
            <div class="cards">
                <section class="page-card">
                    <h3>任职信息</h3><el-descriptions :column="1"><el-descriptions-item
                            label="部门">{{ profile.departmentName }}</el-descriptions-item><el-descriptions-item
                            label="岗位">{{ profile.positionName }}</el-descriptions-item><el-descriptions-item
                            label="入职日期">{{ profile.hireDate }}</el-descriptions-item></el-descriptions>
                </section>
                <section class="page-card">
                    <div class="card-title">
                        <h3>联系资料</h3><el-button type="primary" plain @click="edit = true">编辑资料</el-button>
                    </div><el-descriptions :column="1"><el-descriptions-item
                            label="手机号">{{ profile.phone || '未填写' }}</el-descriptions-item><el-descriptions-item
                            label="邮箱">{{ profile.email || '未填写' }}</el-descriptions-item><el-descriptions-item
                            label="联系地址">{{ profile.address || '未填写' }}</el-descriptions-item><el-descriptions-item
                            label="学历">{{ profile.education || '未填写' }}</el-descriptions-item></el-descriptions>
                </section>
            </div>
        </template><el-dialog v-model="edit" title="编辑联系资料" width="480px"><el-form label-width="90px"><el-form-item
                    label="手机号"><el-input v-model="form.phone" /></el-form-item><el-form-item label="邮箱"><el-input
                        v-model="form.email" /></el-form-item><el-form-item label="联系地址"><el-input
                        v-model="form.address" /></el-form-item><el-form-item label="学历"><el-input
                        v-model="form.education" /></el-form-item></el-form><template #footer><el-button
                    @click="edit = false">取消</el-button><el-button type="primary"
                    @click="save">保存</el-button></template></el-dialog>
    </main>
</template>
<style
    scoped>
    .profile {
        max-width: 980px;
        margin: 0 auto;
        padding: 36px 24px
    }

    .profile>header,
    .card-title {
        display: flex;
        justify-content: space-between;
        align-items: center
    }

    .hero {
        display: flex;
        gap: 16px;
        align-items: center;
        background: #eaf3ff;
        padding: 24px;
        border-radius: 10px;
        margin: 20px 0
    }

    .avatar {
        width: 54px;
        height: 54px;
        border-radius: 50%;
        display: grid;
        place-items: center;
        color: #fff;
        background: #2563eb;
        font-size: 22px
    }

    .cards {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 16px
    }

    @media(max-width:700px) {
        .cards {
            grid-template-columns: 1fr
        }
    }
</style>
