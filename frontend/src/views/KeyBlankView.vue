<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const rows = ref([])
const uses = ref([])
const form = reactive({ code: '', type: '铜坯', stock: 0, threshold: 0 })
const useForm = reactive({ keyBlankId: '', qty: 1 })

async function load() {
  const [r, u] = await Promise.all([api.listKeyBlanks(), api.listKeyBlankUses()])
  rows.value = r
  uses.value = u
}
async function submit() {
  try {
    await api.createKeyBlank({ ...form, stock: Number(form.stock), threshold: Number(form.threshold) })
    ElMessage.success('入库成功')
    Object.assign(form, { code: '', stock: 0, threshold: 0 })
    await load()
  } catch (e) { ElMessage.error(e.message) }
}
async function take() {
  try {
    if (!useForm.keyBlankId) { ElMessage.warning('请选择钥匙坯'); return }
    await api.useKeyBlank(Number(useForm.keyBlankId), Number(useForm.qty))
    ElMessage.success('领用成功，库存已扣减')
    useForm.qty = 1
    await load()
  } catch (e) { ElMessage.error(e.message) }
}

onMounted(load)
</script>

<template>
  <div>
    <h3 class="mod-title">钥匙坯库存</h3>
    <el-card class="bar" shadow="never">
      <el-input v-model="form.code" placeholder="坯型号" style="width:140px" />
      <el-select v-model="form.type" style="width:130px">
        <el-option v-for="t in ['铜坯','铁坯','不锈钢坯','芯片坯']" :key="t" :label="t" :value="t" />
      </el-select>
      <el-input v-model="form.stock" type="number" placeholder="库存" style="width:110px" />
      <el-input v-model="form.threshold" type="number" placeholder="下限" style="width:110px" />
      <el-button type="primary" @click="submit">入库</el-button>
    </el-card>

    <el-card class="bar" shadow="never">
      <el-select v-model="useForm.keyBlankId" placeholder="选择钥匙坯" style="width:200px">
        <el-option v-for="r in rows" :key="r.id" :label="`${r.code}（库存${r.stock}）`" :value="r.id" />
      </el-select>
      <el-input v-model="useForm.qty" type="number" placeholder="领用数量" style="width:120px" />
      <el-button type="warning" @click="take">领用</el-button>
    </el-card>

    <el-table :data="rows" border stripe>
      <el-table-column prop="code" label="坯型号" width="120" />
      <el-table-column prop="type" label="类型" width="120" />
      <el-table-column prop="stock" label="库存" width="100" />
      <el-table-column prop="threshold" label="下限" width="100" />
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="row.stock < row.threshold ? 'danger' : 'success'">
            {{ row.stock < row.threshold ? '低于下限' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.mod-title { margin: 0 0 12px; }
.bar { display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 14px; }
</style>
