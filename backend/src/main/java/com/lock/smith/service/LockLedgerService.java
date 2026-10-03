package com.lock.smith.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lock.smith.common.BizException;
import com.lock.smith.common.Dict;
import com.lock.smith.entity.LockLedger;
import com.lock.smith.repo.LockLedgerRepository;

@Service
public class LockLedgerService {

    private final LockLedgerRepository repo;

    public LockLedgerService(LockLedgerRepository repo) {
        this.repo = repo;
    }

    public List<LockLedger> list(String kind, String status) {
        if (kind != null && status != null) {
            return repo.findByKindAndStatus(kind, status);
        }
        if (kind != null) {
            return repo.findByKindAndStatus(kind, "在库");
        }
        if (status != null) {
            return repo.findByStatus(status);
        }
        return repo.findAll();
    }

    public LockLedger create(LockLedger input) {
        if (input == null) {
            throw new BizException("锁具信息不能为空");
        }
        if (input.code == null || input.code.isBlank()) {
            throw new BizException("锁编号不能为空");
        }
        if (!Dict.isValidLockKind(input.kind)) {
            throw new BizException("锁类型不合法，应为 门锁 / 车锁 / 保险柜 / 抽屉锁");
        }
        LockLedger saved = new LockLedger();
        saved.code = input.code;
        saved.kind = input.kind;
        saved.brand = (input.brand == null || input.brand.isBlank()) ? "未知品牌" : input.brand;
        saved.customer = input.customer;
        saved.status = (input.status == null || input.status.isBlank()) ? "在库" : input.status;
        if (!Dict.isValidLockStatus(saved.status)) {
            throw new BizException("锁状态不合法，应为 在库 / 已装 / 报废");
        }
        saved.createdAt = LocalDateTime.now();
        return repo.save(saved);
    }

    public LockLedger update(Long id, LockLedger input) {
        LockLedger l = repo.findById(id).orElseThrow(() -> new BizException("锁具不存在"));
        if (input.code != null && !input.code.isBlank()) {
            l.code = input.code;
        }
        if (input.kind != null && !input.kind.isBlank()) {
            if (!Dict.isValidLockKind(input.kind)) {
                throw new BizException("锁类型不合法，应为 门锁 / 车锁 / 保险柜 / 抽屉锁");
            }
            l.kind = input.kind;
        }
        if (input.brand != null && !input.brand.isBlank()) {
            l.brand = input.brand;
        }
        if (input.customer != null && !input.customer.isBlank()) {
            l.customer = input.customer;
        }
        if (input.status != null && !input.status.isBlank()) {
            if (!Dict.isValidLockStatus(input.status)) {
                throw new BizException("锁状态不合法，应为 在库 / 已装 / 报废");
            }
            l.status = input.status;
        }
        return repo.save(l);
    }

    /** 安装：仅「在库」可装；「已装」「报废」不可安装（报废不可再装）。 */
    public LockLedger install(Long id) {
        LockLedger l = repo.findById(id).orElseThrow(() -> new BizException("锁具不存在"));
        if (!Dict.canInstallLock(l.status)) {
            throw new BizException("该锁当前状态为「" + l.status + "」，不可安装（报废不可再装）");
        }
        l.status = "已装";
        return repo.save(l);
    }

    /** 报废：仅「在库 / 已装」可报废；「报废」为终态。 */
    public LockLedger scrap(Long id) {
        LockLedger l = repo.findById(id).orElseThrow(() -> new BizException("锁具不存在"));
        if (!Dict.canScrapLock(l.status)) {
            throw new BizException("该锁已是「报废」状态，不可重复报废");
        }
        l.status = "报废";
        return repo.save(l);
    }
}
