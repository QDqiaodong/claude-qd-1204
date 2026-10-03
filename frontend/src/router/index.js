import { createRouter, createWebHashHistory } from 'vue-router'

// 注意：main.js 里没有 app.use(router)，路由实例由 App.vue 直接驱动，
// 所以这里全部用静态 import —— <component :is> 拿到的是组件对象本身。
import LockLedgerView from '../views/LockLedgerView.vue'
import UnlockRecordView from '../views/UnlockRecordView.vue'
import KeyBlankView from '../views/KeyBlankView.vue'
import CashierView from '../views/CashierView.vue'

const routes = [
  { path: '/', redirect: '/lock-ledgers' },
  { path: '/lock-ledgers', name: 'lock-ledgers', component: LockLedgerView },
  { path: '/unlock-records', name: 'unlock-records', component: UnlockRecordView },
  { path: '/key-blanks', name: 'key-blanks', component: KeyBlankView },
  { path: '/cashier', name: 'cashier', component: CashierView },
  { path: '/:pathMatch(.*)*', redirect: '/lock-ledgers' }
]

export default createRouter({
  history: createWebHashHistory(),
  routes
})
