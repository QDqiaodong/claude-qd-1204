package com.lock.smith.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lock.smith.common.BizException;
import com.lock.smith.common.Dict;
import com.lock.smith.entity.KeyBlank;
import com.lock.smith.entity.KeyBlankUse;
import com.lock.smith.repo.KeyBlankRepository;
import com.lock.smith.repo.KeyBlankUseRepository;

@Service
public class KeyBlankService {

    private final KeyBlankRepository repo;
    private final KeyBlankUseRepository useRepo;

    public KeyBlankService(KeyBlankRepository repo, KeyBlankUseRepository useRepo) {
        this.repo = repo;
        this.useRepo = useRepo;
    }

    public List<KeyBlank> list() {
        return repo.findAll();
    }

    public List<KeyBlankUse> listUses() {
        return useRepo.findAll();
    }

    public KeyBlank create(KeyBlank input) {
        if (input == null) {
            throw new BizException("钥匙坯信息不能为空");
        }
        if (input.code == null || input.code.isBlank()) {
            throw new BizException("坯型号不能为空");
        }
        if (!Dict.isValidKeyBlankType(input.type)) {
            throw new BizException("钥匙坯类型不合法，应为 铜坯 / 铁坯 / 不锈钢坯 / 芯片坯");
        }
        if (input.stock == null || input.stock < 0) {
            throw new BizException("库存不能为负数");
        }
        if (input.threshold == null || input.threshold < 0) {
            throw new BizException("安全下限不能为负数");
        }
        KeyBlank saved = new KeyBlank();
        saved.code = input.code;
        saved.type = input.type;
        saved.stock = input.stock;
        saved.threshold = input.threshold;
        saved.createdAt = LocalDateTime.now();
        return repo.save(saved);
    }

    /**
     * 领用：库存不足或领用后低于安全下限一律拦截，并扣减库存。
     */
    public KeyBlankUse use(Long keyBlankId, Integer qty) {
        KeyBlank kb = repo.findById(keyBlankId).orElseThrow(() -> new BizException("钥匙坯不存在"));
        if (qty == null || qty <= 0) {
            throw new BizException("领用数量必须大于 0");
        }
        if (kb.stock < qty) {
            throw new BizException("库存不足（当前 " + kb.stock + "），无法领用");
        }
        if (kb.stock - qty < kb.threshold) {
            throw new BizException("领用后将低于安全下限（" + kb.threshold + "），禁止领用");
        }
        kb.stock = kb.stock - qty;
        repo.save(kb);

        KeyBlankUse use = new KeyBlankUse();
        use.keyBlankId = keyBlankId;
        use.qty = qty;
        use.useDate = LocalDate.now();
        use.createdAt = LocalDateTime.now();
        return useRepo.save(use);
    }
}
