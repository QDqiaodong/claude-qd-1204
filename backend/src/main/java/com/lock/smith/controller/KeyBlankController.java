package com.lock.smith.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lock.smith.common.ApiResponse;
import com.lock.smith.entity.KeyBlank;
import com.lock.smith.entity.KeyBlankUse;
import com.lock.smith.service.KeyBlankService;

@RestController
@RequestMapping("/key-blanks")
public class KeyBlankController {

    private final KeyBlankService service;

    public KeyBlankController(KeyBlankService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<KeyBlank>> list() {
        return ApiResponse.success(service.list());
    }

    @GetMapping("/uses")
    public ApiResponse<List<KeyBlankUse>> listUses() {
        return ApiResponse.success(service.listUses());
    }

    @PostMapping
    public ApiResponse<KeyBlank> create(@RequestBody KeyBlank input) {
        return ApiResponse.success(service.create(input));
    }

    @PostMapping("/{id}/use")
    public ApiResponse<KeyBlankUse> use(@PathVariable Long id,
                                        @RequestParam Integer qty) {
        return ApiResponse.success(service.use(id, qty));
    }
}
