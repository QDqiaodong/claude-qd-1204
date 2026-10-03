<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const rows = ref([])
const form = reactive({ code: '', kind: '紧急', technician: '', fee: 0, success: true })

async function load() {
  rows.value = await api.listUnlockRecords()
}
async function submit() {
  try {
    await api.createUnlockRecord({ ...form, fee: Number(form.fee) })
    ElMessage.success('服务登记成功')
    Object.assign(form, { code: '', technician: '', fee: 0 })
    await load()
  } catch (e) { ElMessage.error(e.message) }
}
async function settle(row) {
  try { await api.settleUnlockRecord(row.id); ElMessage.success('结算完成'); await load() }
  catch (e) { ElMessage.error(e.message) }
}

onMounted(load)
</script>

<template>
  <div>
    <h3 class="mod-title">开锁记录</h3>
    <el-card class="bar" shadow="never">
      <el-input v-model="form.code" placeholder="工单号" style="width:140px" />
      <el-select v-model="form.kind" style="width:120px">
        <el-option v-for="k in ['紧急','预约']" :key="k" :label="k" :value="k" />
      </el-select>
      <el-input v-model="form.technician" placeholder="技师" style="width:120px" />
      <el-input v-model="form.fee" type="number" placeholder="费用" style="width:120px" />
      <el-select v-model="form.success" style="width:120px">
        <el-option label="成功" :value="true" />
        <el-option label="未成功" :value="false" />
      </el-select>
      <el-button type="primary" @click="submit">登记服务</el-button>
    </el-card>

    <el-table :data="rows" border stripe>
      <el-table-column prop="code" label="工单号" width="120" />
      <el-table-column prop="kind" label="类型" width="90" />
      <el-table-column prop="technician" label="技师" width="100" />
      <el-table-column prop="fee" label="费用" width="90" />
      <el-table-column label="成功" width="80">
        <template #default="{ row }">
          <el-tag :type="row.success ? 'success' : 'info'">{{ row.success ? '成功' : '未成功' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="110">
        <template #default="{ row }">
          <el-button size="small" type="primary" :disabled="!row.success" @click="settle(row)">结算</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.mod-title { margin: 0 0 12px; }
.bar { display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 14px; }
</style>
