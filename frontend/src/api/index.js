import axios from 'axios'

const http = axios.create({ baseURL: '/api', timeout: 20000 })

http.interceptors.response.use(
  (response) => response,
  (error) => {
    const message = error?.response?.data?.message || error?.message || '请求失败'
    return Promise.reject(new Error(message))
  }
)

async function call(promise) {
  const response = await promise
  const body = response.data
  if (!body || body.ok === false) {
    throw new Error((body && body.message) || '请求失败')
  }
  return body.data
}

const params = (o) => ({ params: o })

export default {
  // ---------------- 锁具台账 ----------------
  listLockLedgers: (kind, status) => call(http.get('/lock-ledgers', params(
    kind ? { kind, ...(status ? { status } : {}) }
         : (status ? { status } : {})))),
  createLockLedger: (d) => call(http.post('/lock-ledgers', d)),
  installLockLedger: (id) => call(http.post(`/lock-ledgers/${id}/install`)),
  scrapLockLedger: (id) => call(http.post(`/lock-ledgers/${id}/scrap`)),

  // ---------------- 开锁记录 ----------------
  listUnlockRecords: () => call(http.get('/unlock-records')),
  createUnlockRecord: (d) => call(http.post('/unlock-records', d)),
  settleUnlockRecord: (id) => call(http.post(`/unlock-records/${id}/settle`)),

  // ---------------- 钥匙坯库存 ----------------
  listKeyBlanks: () => call(http.get('/key-blanks')),
  listKeyBlankUses: () => call(http.get('/key-blanks/uses')),
  createKeyBlank: (d) => call(http.post('/key-blanks', d)),
  useKeyBlank: (id, qty) => call(http.post(`/key-blanks/${id}/use`, null, { params: { qty } })),

  // ---------------- 客户与收费 ----------------
  listCustomers: () => call(http.get('/bills/customers')),
  createCustomer: (d) => call(http.post('/bills/customers', d)),
  listBills: (customerId) => call(http.get('/bills', customerId ? { params: { customerId } } : {})),
  createBill: (customerId, itemType, amount) => call(http.post('/bills', null, {
    params: { customerId, itemType, amount }
  })),
  payBill: (id, amount) => call(http.post(`/bills/${id}/pay`, null, { params: { amount } }))
}
