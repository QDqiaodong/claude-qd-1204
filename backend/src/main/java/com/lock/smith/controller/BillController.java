package com.lock.smith.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lock.smith.common.ApiResponse;
import com.lock.smith.entity.Bill;
import com.lock.smith.entity.Customer;
import com.lock.smith.service.BillService;

@RestController
@RequestMapping("/bills")
public class BillController {

    private final BillService service;

    public BillController(BillService service) {
        this.service = service;
    }

    @GetMapping("/customers")
    public ApiResponse<List<Customer>> listCustomers() {
        return ApiResponse.success(service.listCustomers());
    }

    @PostMapping("/customers")
    public ApiResponse<Customer> createCustomer(@RequestBody Customer input) {
        return ApiResponse.success(service.createCustomer(input));
    }

    @GetMapping
    public ApiResponse<List<Bill>> list(@RequestParam(required = false) Long customerId) {
        return ApiResponse.success(service.listBills(customerId));
    }

    @PostMapping
    public ApiResponse<Bill> create(@RequestParam Long customerId,
                                    @RequestParam String itemType,
                                    @RequestParam BigDecimal amount) {
        return ApiResponse.success(service.createBill(customerId, itemType, amount));
    }

    @PostMapping("/{id}/pay")
    public ApiResponse<Bill> pay(@PathVariable Long id, @RequestParam BigDecimal amount) {
        return ApiResponse.success(service.pay(id, amount));
    }
}
