-- ============================================================
-- 延安烟厂三维数字孪生系统 - 仓库管理模块
-- 数据库: PostgreSQL
-- 版本: 1.0
-- 创建日期: 2026-02-21
-- ============================================================

-- 连接数据库
-- \c tobacco_warehouse_db;

-- ============================================================
-- 六、仓库管理模块
-- ============================================================

-- 6.1 货位表 (warehouse_location)
DROP TABLE IF EXISTS warehouse_location CASCADE;
CREATE TABLE warehouse_location (
    id BIGSERIAL PRIMARY KEY,
    location_code VARCHAR(50) NOT NULL UNIQUE,
    location_name VARCHAR(100),
    area_code VARCHAR(20) NOT NULL,
    area_name VARCHAR(50),
    row_num INT DEFAULT 1,
    col_num INT DEFAULT 1,
    level_num INT DEFAULT 1,
    location_type VARCHAR(20) DEFAULT 'storage' CHECK (location_type IN ('storage', 'picking', 'buffer')),
    current_status VARCHAR(20) DEFAULT 'empty' CHECK (current_status IN ('empty', 'occupied', 'reserved')),
    capacity DECIMAL(10,2) DEFAULT 2.5,
    max_weight DECIMAL(10,2) DEFAULT 500.00,
    length DECIMAL(10,2),
    width DECIMAL(10,2),
    height DECIMAL(10,2),
    x_position DECIMAL(10,2),
    y_position DECIMAL(10,2),
    z_position DECIMAL(10,2),
    description VARCHAR(500),
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1)),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_warehouse_location_area_code ON warehouse_location(area_code);
CREATE INDEX idx_warehouse_location_status ON warehouse_location(status);

-- 6.2 托盘表 (warehouse_pallet)
DROP TABLE IF EXISTS warehouse_pallet CASCADE;
CREATE TABLE warehouse_pallet (
    id BIGSERIAL PRIMARY KEY,
    pallet_code VARCHAR(50) NOT NULL UNIQUE,
    pallet_name VARCHAR(100),
    specification VARCHAR(100),
    pallet_type VARCHAR(20) DEFAULT 'standard',
    length DECIMAL(10,2),
    width DECIMAL(10,2),
    height DECIMAL(10,2),
    max_weight DECIMAL(10,2) DEFAULT 1000.00,
    material VARCHAR(50),
    location_id BIGINT,
    location_code VARCHAR(50),
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1)),
    description VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_warehouse_pallet_location_id ON warehouse_pallet(location_id);

-- 6.3 箱子表 (warehouse_box)
DROP TABLE IF EXISTS warehouse_box CASCADE;
CREATE TABLE warehouse_box (
    id BIGSERIAL PRIMARY KEY,
    box_code VARCHAR(50) NOT NULL UNIQUE,
    box_name VARCHAR(100),
    specification VARCHAR(100),
    box_type VARCHAR(20) DEFAULT 'cardboard',
    length DECIMAL(10,2),
    width DECIMAL(10,2),
    height DECIMAL(10,2),
    max_weight DECIMAL(10,2) DEFAULT 50.00,
    material VARCHAR(50),
    color VARCHAR(30),
    pallet_id BIGINT,
    location_id BIGINT,
    location_code VARCHAR(50),
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1)),
    description VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_warehouse_box_pallet_id ON warehouse_box(pallet_id);
CREATE INDEX idx_warehouse_box_location_id ON warehouse_box(location_id);

-- 6.4 出入库工单表 (warehouse_order)
DROP TABLE IF EXISTS warehouse_order CASCADE;
CREATE TABLE warehouse_order (
    id BIGSERIAL PRIMARY KEY,
    order_code VARCHAR(50) NOT NULL UNIQUE,
    order_type VARCHAR(20) NOT NULL CHECK (order_type IN ('in', 'out', 'transfer')),
    order_status VARCHAR(20) DEFAULT 'pending' CHECK (order_status IN ('pending', 'processing', 'completed', 'cancelled')),
    priority SMALLINT DEFAULT 1 CHECK (priority IN (1, 2, 3)),
    warehouse_code VARCHAR(50),
    warehouse_name VARCHAR(100),
    source_location_id BIGINT,
    source_location_code VARCHAR(50),
    target_location_id BIGINT,
    target_location_code VARCHAR(50),
    pallet_id BIGINT,
    pallet_code VARCHAR(50),
    box_id BIGINT,
    box_code VARCHAR(50),
    item_code VARCHAR(50),
    item_name VARCHAR(100),
    item_quantity DECIMAL(10,2),
    item_unit VARCHAR(20),
    operator_id BIGINT,
    operator_name VARCHAR(50),
    plan_start_time TIMESTAMP,
    plan_end_time TIMESTAMP,
    actual_start_time TIMESTAMP,
    actual_end_time TIMESTAMP,
    remark VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_warehouse_order_order_type ON warehouse_order(order_type);
CREATE INDEX idx_warehouse_order_order_status ON warehouse_order(order_status);
CREATE INDEX idx_warehouse_order_create_time ON warehouse_order(create_time);

-- 6.5 库存表 (warehouse_inventory)
DROP TABLE IF EXISTS warehouse_inventory CASCADE;
CREATE TABLE warehouse_inventory (
    id BIGSERIAL PRIMARY KEY,
    inventory_code VARCHAR(50) NOT NULL UNIQUE,
    item_code VARCHAR(50) NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    item_category VARCHAR(50),
    specification VARCHAR(100),
    unit VARCHAR(20),
    quantity DECIMAL(10,2) DEFAULT 0,
    available_quantity DECIMAL(10,2) DEFAULT 0,
    locked_quantity DECIMAL(10,2) DEFAULT 0,
    location_id BIGINT,
    location_code VARCHAR(50),
    pallet_id BIGINT,
    pallet_code VARCHAR(50),
    box_id BIGINT,
    box_code VARCHAR(50),
    batch_code VARCHAR(50),
    production_date DATE,
    expire_date DATE,
    min_stock DECIMAL(10,2) DEFAULT 0,
    max_stock DECIMAL(10,2),
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1)),
    description VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_warehouse_inventory_item_code ON warehouse_inventory(item_code);
CREATE INDEX idx_warehouse_inventory_location_id ON warehouse_inventory(location_id);

-- ============================================================
-- 初始化仓库测试数据
-- ============================================================

-- 初始化货位数据
INSERT INTO warehouse_location (location_code, location_name, area_code, area_name, row_num, col_num, level_num, location_type, current_status, capacity, max_weight, status)
VALUES 
('A-01-01-01', 'A区01排01列01层', 'A', 'A区', 1, 1, 1, 'storage', 'empty', 2.5, 500, 1),
('A-01-01-02', 'A区01排01列02层', 'A', 'A区', 1, 1, 2, 'storage', 'empty', 2.5, 500, 1),
('A-01-01-03', 'A区01排01列03层', 'A', 'A区', 1, 1, 3, 'storage', 'empty', 2.5, 500, 1),
('A-01-02-01', 'A区01排02列01层', 'A', 'A区', 1, 2, 1, 'storage', 'occupied', 2.5, 500, 1),
('A-01-02-02', 'A区01排02列02层', 'A', 'A区', 1, 2, 2, 'storage', 'empty', 2.5, 500, 1),
('A-02-01-01', 'A区02排01列01层', 'A', 'A区', 2, 1, 1, 'storage', 'empty', 2.5, 500, 1),
('A-02-01-02', 'A区02排01列02层', 'A', 'A区', 2, 1, 2, 'storage', 'empty', 2.5, 500, 1),
('B-01-01-01', 'B区01排01列01层', 'B', 'B区', 1, 1, 1, 'storage', 'empty', 3.0, 800, 1),
('B-01-01-02', 'B区01排01列02层', 'B', 'B区', 1, 1, 2, 'storage', 'empty', 3.0, 800, 1),
('B-01-01-03', 'B区01排01列03层', 'B', 'B区', 1, 1, 3, 'storage', 'reserved', 3.0, 800, 1),
('C-01-01-01', 'C区01排01列01层', 'C', 'C区', 1, 1, 1, 'picking', 'empty', 1.5, 200, 1),
('C-01-01-02', 'C区01排01列02层', 'C', 'C区', 1, 1, 2, 'picking', 'empty', 1.5, 200, 1);

-- 初始化托盘数据
INSERT INTO warehouse_pallet (pallet_code, pallet_name, specification, pallet_type, length, width, height, max_weight, material, status)
VALUES 
('PLT-001', '标准托盘1', '1200*1000mm', 'standard', 1200, 1000, 150, 1000, '木质', 1),
('PLT-002', '标准托盘2', '1200*1000mm', 'standard', 1200, 1000, 150, 1000, '木质', 1),
('PLT-003', '标准托盘3', '1200*1000mm', 'standard', 1200, 1000, 150, 1000, '木质', 1),
('PLT-004', '塑料托盘1', '1200*1000mm', 'plastic', 1200, 1000, 150, 800, '塑料', 1),
('PLT-005', '塑料托盘2', '1200*1000mm', 'plastic', 1200, 1000, 150, 800, '塑料', 1);

-- 初始化箱子数据
INSERT INTO warehouse_box (box_code, box_name, specification, box_type, length, width, height, max_weight, material, status)
VALUES 
('BOX-001', '纸箱1', '600*400*300mm', 'cardboard', 600, 400, 300, 50, '纸板', 1),
('BOX-002', '纸箱2', '600*400*300mm', 'cardboard', 600, 400, 300, 50, '纸板', 1),
('BOX-003', '纸箱3', '800*600*400mm', 'cardboard', 800, 600, 400, 80, '纸板', 1),
('BOX-004', '塑料箱1', '600*400*300mm', 'plastic', 600, 400, 300, 60, '塑料', 1),
('BOX-005', '塑料箱2', '600*400*300mm', 'plastic', 600, 400, 300, 60, '塑料', 1);

-- 初始化出入库工单数据
INSERT INTO warehouse_order (order_code, order_type, order_status, priority, warehouse_code, source_location_code, target_location_code, item_code, item_name, item_quantity, item_unit, operator_name, plan_start_time, plan_end_time)
VALUES 
('IN-20260221-001', 'in', 'pending', 1, 'WH001', NULL, 'A-01-01-01', 'MAT-001', '烟丝原料', 100, 'kg', '张三', '2026-02-21 09:00:00', '2026-02-21 12:00:00'),
('IN-20260221-002', 'in', 'processing', 1, 'WH001', NULL, 'A-01-01-02', 'MAT-002', '辅料A', 50, 'kg', '李四', '2026-02-21 10:00:00', '2026-02-21 14:00:00'),
('OUT-20260221-001', 'out', 'completed', 2, 'WH001', 'A-01-02-01', NULL, 'MAT-001', '烟丝原料', 30, 'kg', '王五', '2026-02-20 14:00:00', '2026-02-20 16:00:00'),
('OUT-20260221-002', 'out', 'pending', 3, 'WH001', 'B-01-01-01', NULL, 'PKG-001', '包装材料', 200, '个', '赵六', '2026-02-22 09:00:00', '2026-02-22 12:00:00');

-- 初始化库存数据
INSERT INTO warehouse_inventory (inventory_code, item_code, item_name, item_category, specification, unit, quantity, available_quantity, location_code, batch_code, production_date, min_stock, max_stock, status)
VALUES 
('INV-001', 'MAT-001', '烟丝原料', '原料', 'A级烟丝', 'kg', 100, 100, 'A-01-01-01', 'BATCH-20260201', '2026-02-01', 20, 500, 1),
('INV-002', 'MAT-002', '辅料A', '辅料', '标准规格', 'kg', 50, 50, 'A-01-01-02', 'BATCH-20260210', '2026-02-10', 10, 200, 1),
('INV-003', 'PKG-001', '包装材料', '包材', '标准包装', '个', 200, 200, 'B-01-01-01', 'BATCH-20260115', '2026-01-15', 50, 1000, 1),
('INV-004', 'MAT-003', '香精香料', '原料', '特殊配方', 'kg', 30, 30, 'A-01-02-01', 'BATCH-20260205', '2026-02-05', 5, 100, 1);

-- ============================================================
-- 仓库模块初始化完成
-- ============================================================
SELECT '仓库管理模块数据初始化完成！' AS message;
