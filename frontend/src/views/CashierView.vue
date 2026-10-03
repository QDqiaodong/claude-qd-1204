<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const customers = ref([])
const bills = ref([])
const form = reactive({ customerId: '', itemType: '开锁费', amount: 0, pay: 0 })

const owed = computed(() => {
  const c = customers.value.find((x) => x.id === Number(form.customerId))
  return c ? bills.value.filter((b) => b.customerId === c.id && b.payStatus === '欠款') : []
})

async function load() {
  const [c, b] = await Promise.all([api.listCustomers(), api.listBills()])
  customers.value = c
  bills.value = b
}
async function open() {
  try {
    if (!form.customerId) { ElMessage.warning('请先选择客户'); return }
    const bill = await api.createBill(Number(form.customerId), form.itemType, Number(form.amount))
    if (Number(form.pay) > 0) {
      await api.payBill(bill.id, Number(form.pay))
    }
    ElMessage.success('开单并收款成功')
    Object.assign(form, { itemType: '开锁费', amount: 0, pay: 0 })
    await load()
  } catch (e) { ElMessage.error(e.message) }
}
async function pay(row) {
  try {
    const v = Number(prompt('收款金额', String(row.amount - row.paid)))
    if (!v || v <= 0) return
    await api.payBill(row.id, v)
    ElMessage.success('收款成功')
    await load()
  } catch (e) { ElMessage.error(e.message) }
}

onMounted(load)
</script>

<template>
  <div>
    <h3 class="mod-title">客户与收费（收银台一条式）</h3>

    <el-card class="bar" shadow="never">
      <el-select v-model="form.customerId" placeholder="客户" style="width:160px" filterable>
        <el-option v-for="c in customers" :key="c.id" :label="`${c.name}（${c.phone || '无手机'}）`" :value="c.id" />
      </el-select>
      <el-select v-model="form.itemType" style="width:130px">
        <el-option v-for="t in ['开锁费','配匙费']" :key="t" :label="t" :value="t" />
      </el-select>
      <el-input v-model="form.amount" type="number" placeholder="应收金额" style="width:130px" />
      <el-input v-model="form.pay" type="number" placeholder="收款金额" style="width:130px" />
      <el-button type="primary" @click="open">开单并收款</el-button>
    </el-card>

    <el-alert v-if="owed.length" type="warning" :closable="false"
      :title="`该客户尚有 ${owed.length} 笔欠款账单，开新单将被拦截`" style="margin-bottom:12px" />

    <el-table :data="bills" border stripe>
      <el-table-column prop="code" label="账单号" width="130" />
      <el-table-column prop="itemType" label="项目" width="100" />
      <el-table-column prop="amount" label="应收" width="100" />
      <el-table-column prop="paid" label="已收" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.payStatus === '已付' ? 'success' : row.payStatus === '欠款' ? 'danger' : 'info'">{{ row.payStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button size="small" type="primary" :disabled="row.payStatus === '已付'" @click="pay(row)">收款</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.mod-title { margin: 0 0 12px; }
.bar { display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 14px; }
</style>
