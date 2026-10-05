<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'; import { api } from '../api'; import type { Dashboard } from '../types'; const data = ref<Dashboard>(); const error = ref(''); async function load() { error.value = ''; try { data.value = await api.dashboard() } catch { error.value = '统计数据加载失败' } } onMounted(load)
const palette = ['#5b8ff9', '#61ddaa', '#f6bd16', '#7262fd', '#78d3f8', '#9661bc', '#f6903d', '#008685', '#f08bb4', '#fae1a0']
const CX = 180, CY = 180, R = 150
const slices = computed(() => {
    if (!data.value) return []
    const items = data.value.departmentDistribution
    const total = items.reduce((s, x) => s + x.count, 0)
    if (!total) return []
    if (items.length === 1) {
        const x = items[0]
        const d = `M ${CX - R} ${CY} A ${R} ${R} 0 1 1 ${CX + R} ${CY} A ${R} ${R} 0 1 1 ${CX - R} ${CY} Z`
        return [{ name: x.name, count: x.count, color: palette[0], single: true, d, lx: CX, ly: CY, label: `${x.name} ${x.count}人` }]
    }
    let acc = -Math.PI / 2
    return items.map((x, i) => {
        const frac = x.count / total
        const a0 = acc, a1 = acc + frac * 2 * Math.PI; acc = a1
        const mid = (a0 + a1) / 2
        const large = a1 - a0 > Math.PI ? 1 : 0
        const x0o = CX + R * Math.cos(a0), y0o = CY + R * Math.sin(a0)
        const x1o = CX + R * Math.cos(a1), y1o = CY + R * Math.sin(a1)
        const d = `M ${CX} ${CY} L ${x0o.toFixed(2)} ${y0o.toFixed(2)} A ${R} ${R} 0 ${large} 1 ${x1o.toFixed(2)} ${y1o.toFixed(2)} Z`
        const lx = CX + R * 0.6 * Math.cos(mid), ly = CY + R * 0.6 * Math.sin(mid)
        return { name: x.name, count: x.count, color: palette[i % palette.length], d, lx, ly, single: false, label: `${x.name} ${x.count}人` }
    })
})
const totalPeople = computed(() => slices.value.reduce((s, x) => s + x.count, 0))
</script>
<template>
    <div>
        <h2>工作台</h2>
        <p class="muted">查看人员结构和近期人事变动</p><el-alert v-if="error" :title="error" type="error" show-icon><template
                #default><el-button link @click="load">重试</el-button></template></el-alert><template v-else-if="data">
            <div class="stats"><el-card
                    v-for="x in [ { n: '员工总数', v: data.activeEmployees }, { n: '离职人数', v: data.resignedEmployees }, { n: '部门数量', v: data.departmentCount }, { n: '岗位总数', v: data.positionCount }]"
                    :key="x.n"><span class="muted">{{ x.n }}</span><strong>{{ x.v }}</strong></el-card></div>
            <div class="grid">
                <section class="page-card">
                    <h3>部门人数分布</h3><el-empty v-if="!slices.length" description="暂无部门数据" />
                    <div v-else class="donut-box">
                        <div class="donut"> <svg viewBox="0 0 360 360" class="donut-svg" role="img"
                                aria-label="部门人数分布扇形图">
                                <path v-for="s in slices" :key="s.name" :d="s.d" :fill="s.color" stroke="#fff"
                                    stroke-width="1">
                                    <title>{{ s.name }}：{{ s.count }} 人</title>
                                </path>
                                <text v-for="s in slices" :key="'t-' + s.name" :x="s.lx.toFixed(2)" :y="s.ly.toFixed(2)"
                                    class="slice-label" text-anchor="middle" dominant-baseline="central">{{ s.label
                                    }}</text>
                            </svg></div>
                        <p class="total-note">合计 {{ totalPeople }} 人</p>
                    </div>
                </section>
                <section class="page-card">
                    <h3>近期人事变动</h3><el-empty v-if="!data.recentChanges.length" description="暂无变动记录" /><el-timeline
                        v-else><el-timeline-item v-for="c in data.recentChanges" :key="c.id"
                            :timestamp="c.occurredAt"><b>{{ c.employeeName }}</b> {{ c.type }} ·
                            {{ c.reason }}</el-timeline-item></el-timeline>
                </section>
            </div>
        </template><el-skeleton v-else :rows="8" animated />
    </div>
</template>
<style scoped>
.stats {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    margin: 20px 0
}

.stats strong {
    display: block;
    font-size: 30px;
    margin-top: 8px
}

.grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px
}

.donut-box {
    display: flex;
    flex-direction: column;
    align-items: center
}

.donut {
    position: relative;
    width: 340px;
    height: 340px
}

.donut-svg {
    width: 100%;
    height: 100%;
    display: block
}

.slice-label {
    font-size: 11px;
    stroke: rgba(53, 53, 53, 0.936);
    stroke-linejoin: round;
    pointer-events: none;
}

.total-note {
    margin-top: 12px;
    font-size: 14px;
    color: #606266
}

@media(max-width:900px) {

    .stats,
    .grid {
        grid-template-columns: 1fr 1fr
    }
}
</style>
