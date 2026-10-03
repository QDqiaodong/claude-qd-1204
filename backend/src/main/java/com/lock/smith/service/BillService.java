package com.lock.smith.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lock.smith.common.BizException;
import com.lock.smith.common.Dict;
import com.lock.smith.entity.Bill;
import com.lock.smith.entity.Customer;
import com.lock.smith.repo.BillRepository;
import com.lock.smith.repo.CustomerRepository;

@Service
public class BillService {

    private final CustomerRepository customerRepo;
    private final BillRepository billRepo;

    public BillService(CustomerRepository customerRepo, BillRepository billRepo) {
        this.customerRepo = customerRepo;
        this.billRepo = billRepo;
    }

    // ---------------- 客户 ----------------

    public List<Customer> listCustomers() {
        return customerRepo.findAll();
    }

    public Customer createCustomer(Customer input) {
        if (input == null || input.name == null || input.name.isBlank()) {
            throw new BizException("客户姓名不能为空");
        }
        Customer saved = new Customer();
        saved.code = (input.code == null || input.code.isBlank())
                ? "C" + System.currentTimeMillis() : input.code;
        saved.name = input.name;
        saved.phone = input.phone;
        saved.createdAt = LocalDateTime.now();
        return customerRepo.save(saved);
    }

    // ---------------- 账单 ----------------

    public List<Bill> listBills(Long customerId) {
        if (customerId != null) {
            return billRepo.findByCustomerId(customerId);
        }
        return billRepo.findAll();
    }

    /**
     * 开单：客户尚有「欠款」账单时禁止开新单。
     */
    public Bill createBill(Long customerId, String itemType, BigDecimal amount) {
        Customer c = customerRepo.findById(customerId)
                .orElseThrow(() -> new BizException("客户不存在"));
        if (billRepo.existsByCustomerIdAndPayStatus(customerId, "欠款")) {
            throw new BizException("该客户尚有欠款账单，禁止开新单");
        }
        if (!Dict.isValidBillItemType(itemType)) {
            throw new BizException("收费项目不合法，应为 开锁费 / 配匙费");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("账单金额必须大于 0");
        }
        Bill b = new Bill();
        b.code = "B" + System.currentTimeMillis();
        b.customerId = customerId;
        b.itemType = itemType;
        b.amount = amount;
        b.paid = BigDecimal.ZERO;
        b.payStatus = "未付";
        b.createdAt = LocalDateTime.now();
        return billRepo.save(b);
    }

    /**
     * 收款：收款金额须 > 0 且不得超出账单应收金额，收款后重算收款状态。
     */
    public Bill pay(Long billId, BigDecimal amount) {
        Bill b = billRepo.findById(billId).orElseThrow(() -> new BizException("账单不存在"));
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("收款金额必须大于 0");
        }
        BigDecimal after = b.paid.add(amount);
        if (after.compareTo(b.amount) > 0) {
            throw new BizException("收款金额超出账单应收金额");
        }
        b.paid = after;
        b.payStatus = after.compareTo(b.amount) >= 0 ? "已付" : "欠款";
        return billRepo.save(b);
    }
}
