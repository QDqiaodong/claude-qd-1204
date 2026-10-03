package com.lock.smith.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lock.smith.common.ApiResponse;
import com.lock.smith.entity.LockLedger;
import com.lock.smith.service.LockLedgerService;

@RestController
@RequestMapping("/lock-ledgers")
public class LockLedgerController {

    private final LockLedgerService service;

    public LockLedgerController(LockLedgerService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<LockLedger>> list(@RequestParam(required = false) String kind,
                                              @RequestParam(required = false) String status) {
        return ApiResponse.success(service.list(kind, status));
    }

    @PostMapping
    public ApiResponse<LockLedger> create(@RequestBody LockLedger input) {
        return ApiResponse.success(service.create(input));
    }

    @PutMapping("/{id}")
    public ApiResponse<LockLedger> update(@PathVariable Long id, @RequestBody LockLedger input) {
        return ApiResponse.success(service.update(id, input));
    }

    @PostMapping("/{id}/install")
    public ApiResponse<LockLedger> install(@PathVariable Long id) {
        return ApiResponse.success(service.install(id));
    }

    @PostMapping("/{id}/scrap")
    public ApiResponse<LockLedger> scrap(@PathVariable Long id) {
        return ApiResponse.success(service.scrap(id));
    }
}
