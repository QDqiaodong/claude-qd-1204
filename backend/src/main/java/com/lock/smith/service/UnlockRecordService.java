package com.lock.smith.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lock.smith.common.BizException;
import com.lock.smith.common.Dict;
import com.lock.smith.entity.UnlockRecord;
import com.lock.smith.repo.UnlockRecordRepository;

@Service
public class UnlockRecordService {

    private final UnlockRecordRepository repo;

    public UnlockRecordService(UnlockRecordRepository repo) {
        this.repo = repo;
    }

    public List<UnlockRecord> list() {
        return repo.findAll();
    }

    public UnlockRecord create(UnlockRecord input) {
        if (input == null) {
            throw new BizException("开锁记录不能为空");
        }
        if (input.code == null || input.code.isBlank()) {
            throw new BizException("工单号不能为空");
        }
        if (!Dict.isValidUnlockKind(input.kind)) {
            throw new BizException("服务类型不合法，应为 紧急 / 预约");
        }
        if (input.success == null) {
            throw new BizException("请填写本次服务是否成功");
        }
        // 费用规则：成功后费用必须 > 0；未成功不得收费
        if (input.success) {
            if (input.fee == null || input.fee.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BizException("成功工单费用必须大于 0");
            }
        } else {
            if (input.fee != null && input.fee.compareTo(BigDecimal.ZERO) > 0) {
                throw new BizException("未成功不得收费");
            }
            input.fee = BigDecimal.ZERO;
        }
        // 紧急单须登记到场时间
        if ("紧急".equals(input.kind) && input.arriveAt == null) {
            throw new BizException("紧急单须登记到场时间");
        }
        UnlockRecord saved = new UnlockRecord();
        saved.code = input.code;
        saved.kind = input.kind;
        saved.technician = input.technician;
        saved.arriveAt = input.arriveAt;
        saved.finishAt = input.finishAt;
        saved.fee = (input.fee == null) ? BigDecimal.ZERO : input.fee;
        saved.success = input.success;
        saved.createdAt = LocalDateTime.now();
        return repo.save(saved);
    }

    /**
     * 结算：未成功不得收费；紧急单须已登记到场时间，方可结算。
     */
    public UnlockRecord settle(Long id) {
        UnlockRecord r = repo.findById(id).orElseThrow(() -> new BizException("开锁记录不存在"));
        if (!r.success) {
            throw new BizException("未成功不得收费，无法结算");
        }
        if ("紧急".equals(r.kind) && r.arriveAt == null) {
            throw new BizException("紧急单须登记到场时间，无法结算");
        }
        if (r.finishAt == null) {
            r.finishAt = LocalDateTime.now();
        }
        return repo.save(r);
    }
}
