package com.lock.smith.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lock.smith.common.ApiResponse;
import com.lock.smith.entity.UnlockRecord;
import com.lock.smith.service.UnlockRecordService;

@RestController
@RequestMapping("/unlock-records")
public class UnlockRecordController {

    private final UnlockRecordService service;

    public UnlockRecordController(UnlockRecordService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<UnlockRecord>> list() {
        return ApiResponse.success(service.list());
    }

    @PostMapping
    public ApiResponse<UnlockRecord> create(@RequestBody UnlockRecord input) {
        return ApiResponse.success(service.create(input));
    }

    @PostMapping("/{id}/settle")
    public ApiResponse<UnlockRecord> settle(@PathVariable Long id) {
        return ApiResponse.success(service.settle(id));
    }
}
