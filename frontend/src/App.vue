<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import router from './router'
import api from './api'

import LockLedgerView from './views/LockLedgerView.vue'
import UnlockRecordView from './views/UnlockRecordView.vue'
import KeyBlankView from './views/KeyBlankView.vue'
import CashierView from './views/CashierView.vue'

// ------------------------------------------------------------------
// 导航形态：底部标签栏。四个业务模块各占一个底部 tab，
// 收银台一条式操作范式贯穿各模块（开单/选项/算价/收款一条式录入）。
// ------------------------------------------------------------------

const modules = [
  { path: '/lock-ledgers',   label: '锁具台账', sub: '类型 · 状态', comp: LockLedgerView },
  { path: '/unlock-records', label: '开锁记录', sub: '技师 · 费用', comp: UnlockRecordView },
  { path: '/key-blanks',     label: '钥匙坯库存', sub: '库存 · 下限', comp: KeyBlankView },
  { path: '/cashier',        label: '客户收费', sub: '开单 · 收款', comp: CashierView }
]

const currentPath = ref(router.currentRoute.value.path)
const counts = ref({})

const currentView = computed(() => {
  const m = modules.find((mm) => mm.path === currentPath.value)
  return m ? m.comp : LockLedgerView
})

async function loadCounts() {
  try {
    const [l, u, k, c] = await Promise.all([
      api.listLockLedgers(), api.listUnlockRecords(),
      api.listKeyBlanks(), api.listBills()
    ])
    counts.value = {
      '/lock-ledgers': l.length,
      '/unlock-records': u.length,
      '/key-blanks': k.length,
      '/cashier': c.length
    }
  } catch (e) { /* 计数失败不影响主流程 */ }
}

function onTabChange(name) {
  router.push(name)
}

watch(() => router.currentRoute.value.path, (p) => { currentPath.value = p })

onMounted(async () => {
  currentPath.value = router.currentRoute.value.path
  await loadCounts()
})
</script>

<template>
  <div class="app-shell">
    <header class="topbar">
      <div class="brand">
        <span class="brand-mark">锁</span>
        <div class="brand-text">
          <div class="brand-title">开锁配匙店</div>
          <div class="brand-sub">金黄主题 · 底部标签栏导航 · 收银台一条式</div>
        </div>
      </div>
    </header>

    <main class="content">
      <component :is="currentView" @changed="loadCounts" />
    </main>

    <el-tabs
      v-model="currentPath"
      class="nav-bottom"
      tab-position="bottom"
      @tab-change="onTabChange"
    >
      <el-tab-pane
        v-for="m in modules"
        :key="m.path"
        :name="m.path"
        :label="`${m.label}（${counts[m.path] ?? 0}）`"
      />
    </el-tabs>
  </div>
</template>

<style scoped>
.app-shell { min-height: 100vh; display: flex; flex-direction: column; }

.topbar {
  display: flex; align-items: center; gap: 16px;
  padding: 0 22px; height: 60px;
  background: linear-gradient(100deg, var(--primary-dark-2) 0%, var(--primary) 70%, var(--primary-light-3) 100%);
  color: #3a2f00; box-shadow: 0 2px 12px rgba(204, 154, 6, 0.3);
}
.brand { display: flex; align-items: center; gap: 12px; }
.brand-mark {
  width: 38px; height: 38px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  background: #fff; color: var(--primary-dark-2); font-weight: 700; font-size: 20px;
}
.brand-title { font-size: 17px; font-weight: 600; }
.brand-sub { font-size: 12px; opacity: 0.85; }

.content { flex: 1; padding: 18px 22px 28px; }

.nav-bottom {
  background: #fff; border-top: 1px solid var(--line);
  box-shadow: 0 -2px 10px rgba(204, 154, 6, 0.12);
}
.nav-bottom :deep(.el-tabs__header) { margin: 0; }
.nav-bottom :deep(.el-tabs__nav-wrap::after) { display: none; }
.nav-bottom :deep(.el-tabs__item.is-active) { color: var(--primary-dark-2); font-weight: 600; }
</style>
