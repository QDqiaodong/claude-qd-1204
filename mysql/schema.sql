SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- ---------------------------------------------------------------------
-- 开锁配匙店 · 锁具台账 / 开锁记录 / 钥匙坯库存 / 客户与收费
-- 库名 lock_smith；application.yml 里 ddl-auto: none，表结构全部由本文件在 MySQL 镜像构建期导入。
-- 注意：mysql 容器是 UTC，时间戳一律写 NOW()；日期用 DATE_SUB(CURDATE(), INTERVAL N DAY)，绝不用 CURDATE()-N。

CREATE DATABASE IF NOT EXISTS lock_smith
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE lock_smith;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS key_blank_use;
DROP TABLE IF EXISTS bill;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS unlock_record;
DROP TABLE IF EXISTS key_blank;
DROP TABLE IF EXISTS lock_ledger;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------- 锁具台账 ----------------
CREATE TABLE lock_ledger (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    code       VARCHAR(32) NOT NULL UNIQUE COMMENT '锁编号',
    kind       VARCHAR(16) COMMENT '门锁 / 车锁 / 保险柜 / 抽屉锁',
    brand      VARCHAR(64) COMMENT '品牌',
    customer   VARCHAR(64) COMMENT '客户',
    status     VARCHAR(16) COMMENT '在库 / 已装 / 报废',
    created_at DATETIME COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------- 开锁记录 ----------------
CREATE TABLE unlock_record (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    code       VARCHAR(32) NOT NULL UNIQUE COMMENT '工单号',
    kind       VARCHAR(16) COMMENT '紧急 / 预约',
    technician VARCHAR(64) COMMENT '技师',
    arrive_at  DATETIME COMMENT '到场时间',
    finish_at  DATETIME COMMENT '完成时间',
    fee        DECIMAL(10,2) COMMENT '费用',
    success    TINYINT(1) COMMENT '是否成功',
    created_at DATETIME COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------- 钥匙坯库存 ----------------
CREATE TABLE key_blank (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    code       VARCHAR(32) NOT NULL UNIQUE COMMENT '坯型号',
    type       VARCHAR(16) COMMENT '铜坯 / 铁坯 / 不锈钢坯 / 芯片坯',
    stock      INT NOT NULL COMMENT '库存',
    threshold  INT NOT NULL COMMENT '安全下限',
    created_at DATETIME COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------- 钥匙坯领用（依赖 key_blank） ----------------
CREATE TABLE key_blank_use (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    key_blank_id BIGINT NOT NULL COMMENT '钥匙坯 id',
    qty          INT NOT NULL COMMENT '领用数量',
    use_date     DATE COMMENT '领用日期',
    created_at   DATETIME COMMENT '创建时间',
    CONSTRAINT fk_kbu_blank FOREIGN KEY (key_blank_id) REFERENCES key_blank (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------- 客户 ----------------
CREATE TABLE customer (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    code       VARCHAR(32) NOT NULL UNIQUE COMMENT '客户编号',
    name       VARCHAR(64) NOT NULL COMMENT '姓名',
    phone      VARCHAR(24) COMMENT '手机',
    created_at DATETIME COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------- 账单（依赖 customer） ----------------
CREATE TABLE bill (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    code       VARCHAR(32) NOT NULL UNIQUE COMMENT '账单号',
    customer_id BIGINT NOT NULL COMMENT '客户 id',
    item_type  VARCHAR(16) COMMENT '开锁费 / 配匙费',
    amount     DECIMAL(10,2) COMMENT '应收金额',
    paid       DECIMAL(10,2) COMMENT '已收金额',
    pay_status VARCHAR(16) COMMENT '未付 / 已付 / 欠款',
    created_at DATETIME COMMENT '创建时间',
    CONSTRAINT fk_bill_customer FOREIGN KEY (customer_id) REFERENCES customer (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 种子数据（含已装/报废/紧急未到场/库存低于下限/欠款 等异常态，便于审计点检每条业务规则）
-- ---------------------------------------------------------------------

INSERT IGNORE INTO lock_ledger (code, kind, brand, customer, status, created_at) VALUES
('L2026001', '门锁',   '凯迪仕',   '王建国', '在库', NOW()),
('L2026002', '车锁',   '金点原子', '李文博', '在库', NOW()),
('L2026003', '保险柜', '艾谱',     '张明远', '已装', NOW()),   -- 异常态：已装再安装将被拦截
('L2026004', '抽屉锁', '玥玛',     '赵思齐', '报废', NOW()),   -- 异常态：报废不可再装
('L2026005', '门锁',   '德施曼',   '孙倩',   '已装', NOW());

INSERT IGNORE INTO unlock_record (code, kind, technician, arrive_at, finish_at, fee, success, created_at) VALUES
('U2026001', '紧急', '师傅甲', NOW(),                                  NOW(), 150.00, 1, NOW()),
('U2026002', '预约', '师傅乙', DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_SUB(CURDATE(), INTERVAL 2 DAY), 80.00, 1, NOW()),
('U2026003', '紧急', '师傅丙', NULL,                                  NULL, 0.00, 0, NOW()),  -- 异常态：紧急单未登记到场时间（结算将被拦截）
('U2026004', '预约', '师傅甲', DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_SUB(CURDATE(), INTERVAL 5 DAY), 0.00, 0, NOW());

INSERT IGNORE INTO key_blank (code, type, stock, threshold, created_at) VALUES
('K2026001', '铜坯',     200, 30, NOW()),
('K2026002', '铁坯',     120, 20, NOW()),
('K2026003', '不锈钢坯', 5,   50, NOW()),   -- 异常态：库存已低于下限（领用将被拦截）
('K2026004', '芯片坯',   8,   10, NOW());   -- 异常态：库存已低于下限（领用将被拦截）

INSERT IGNORE INTO key_blank_use (key_blank_id, qty, use_date, created_at) VALUES
(1, 10, CURDATE(), NOW()),
(2, 5,  CURDATE(), NOW());

INSERT IGNORE INTO customer (code, name, phone, created_at) VALUES
('C2026001', '王建国', '13800138001', NOW()),
('C2026002', '李文博', '13900139002', NOW()),
('C2026003', '张明远', '13700137003', NOW());

INSERT IGNORE INTO bill (code, customer_id, item_type, amount, paid, pay_status, created_at) VALUES
('B2026001', 1, '开锁费', 150.00, 150.00, '已付', NOW()),
('B2026002', 2, '配匙费', 80.00,  0.00,  '未付', NOW()),
('B2026003', 3, '开锁费', 200.00, 50.00, '欠款', NOW());   -- 异常态：客户3欠款（开新单将被拦截）
