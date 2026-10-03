<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const rows = ref([])
const form = reactive({ code: '', kind: '门锁', brand: '', customer: '', status: '在库' })

async function load() {
  rows.value = await api.listLockLedgers()
}
async function submit() {
  try {
    await api.createLockLedger({ ...form })
    ElMessage.success('登记成功')
    Object.assign(form, { code: '', brand: '', customer: '' })
    await load()
  } catch (e) { ElMessage.error(e.message) }
}
async function install(row) {
  try { await api.installLockLedger(row.id); ElMessage.success('已安装'); await load() }
  catch (e) { ElMessage.error(e.message) }
}
async function scrap(row) {
  try { await api.scrapLockLedger(row.id); ElMessage.success('已报废'); await load() }
  catch (e) { ElMessage.error(e.message) }
}

onMounted(load)
</script>

<template>
  <div>
    <h3 class="mod-title">锁具台账</h3>
    <!-- 收银台一条式：开单录入 -->
    <el-card class="bar" shadow="never">
      <el-input v-model="form.code" placeholder="锁编号" style="width:140px" />
      <el-select v-model="form.kind" style="width:130px">
        <el-option v-for="k in ['门锁','车锁','保险柜','抽屉锁']" :key="k" :label="k" :value="k" />
      </el-select>
      <el-input v-model="form.brand" placeholder="品牌" style="width:130px" />
      <el-input v-model="form.customer" placeholder="客户" style="width:130px" />
      <el-select v-model="form.status" style="width:120px">
        <el-option v-for="s in ['在库','已装','报废']" :key="s" :label="s" :value="s" />
      </el-select>
      <el-button type="primary" @click="submit">开单登记</el-button>
    </el-card>

    <el-table :data="rows" border stripe>
      <el-table-column prop="code" label="锁编号" width="120" />
      <el-table-column prop="kind" label="类型" width="100" />
      <el-table-column prop="brand" label="品牌" />
      <el-table-column prop="customer" label="客户" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === '在库' ? 'success' : row.status === '已装' ? 'warning' : 'danger'">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="170">
        <template #default="{ row }">
          <el-button size="small" :disabled="row.status !== '在库'" @click="install(row)">安装</el-button>
          <el-button size="small" type="danger" :disabled="row.status === '报废'" @click="scrap(row)">报废</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.mod-title { margin: 0 0 12px; }
.bar { display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 14px; }
</style>
