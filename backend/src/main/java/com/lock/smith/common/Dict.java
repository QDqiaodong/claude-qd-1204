package com.lock.smith.common;

import java.util.List;

/**
 * 开锁配匙店业务词典与规则参数表：锁类型 / 锁状态与流转、服务类型、
 * 钥匙坯类型、收费项目、收款状态。规则集中在此，便于出题时被要求调整或扩展。
 */
public final class Dict {

    private Dict() {
    }

    // ---------------- 锁具台账 ----------------
    /** 门锁 / 车锁 / 保险柜 / 抽屉锁 */
    public static final List<String> LOCK_KINDS = List.of("门锁", "车锁", "保险柜", "抽屉锁");

    /** 在库 / 已装 / 报废 */
    public static final List<String> LOCK_STATUS = List.of("在库", "已装", "报废");

    // ---------------- 开锁记录 ----------------
    /** 紧急 / 预约 */
    public static final List<String> UNLOCK_KINDS = List.of("紧急", "预约");

    // ---------------- 钥匙坯库存 ----------------
    /** 铜坯 / 铁坯 / 不锈钢坯 / 芯片坯 */
    public static final List<String> KEY_BLANK_TYPES = List.of("铜坯", "铁坯", "不锈钢坯", "芯片坯");

    // ---------------- 客户与收费 ----------------
    /** 开锁费 / 配匙费 */
    public static final List<String> BILL_ITEM_TYPES = List.of("开锁费", "配匙费");

    /** 未付 / 已付 / 欠款 */
    public static final List<String> PAY_STATUS = List.of("未付", "已付", "欠款");

    // ---------------- 取值方法 ----------------

    public static boolean isValidLockKind(String kind) {
        return kind != null && LOCK_KINDS.contains(kind);
    }

    public static boolean isValidLockStatus(String status) {
        return status != null && LOCK_STATUS.contains(status);
    }

    public static boolean isValidUnlockKind(String kind) {
        return kind != null && UNLOCK_KINDS.contains(kind);
    }

    public static boolean isValidKeyBlankType(String type) {
        return type != null && KEY_BLANK_TYPES.contains(type);
    }

    public static boolean isValidBillItemType(String type) {
        return type != null && BILL_ITEM_TYPES.contains(type);
    }

    public static boolean isValidPayStatus(String status) {
        return status != null && PAY_STATUS.contains(status);
    }

    /**
     * 锁状态机：仅「在库」可安装（→已装）；「报废」为终态（不可再装）。
     */
    public static boolean canInstallLock(String from) {
        return "在库".equals(from);
    }

    /**
     * 锁状态机：仅「在库 / 已装」可报废（→报废）；「报废」不可再报废。
     */
    public static boolean canScrapLock(String from) {
        return "在库".equals(from) || "已装".equals(from);
    }
}
