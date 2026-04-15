-- ============================================================
-- 延安烟厂三维数字孪生系统 - 智能小车(AGV)模块
-- 数据库: PostgreSQL
-- 版本: 1.0
-- 创建日期: 2026-02-22
-- ============================================================

-- 连接数据库
-- \c tobacco_warehouse_db;

-- ============================================================
-- 一、设备类别（扩展）- 小车类别
-- ============================================================

-- 小车类别: AGV (统一类别，不区分入库/出库)
INSERT INTO device_category (category_code, category_name, parent_id, description, sort_order, status)
VALUES ('AGV', '智能小车', 0, '智能搬运机器人(AGV)', 10, 1)
ON CONFLICT (category_code) DO NOTHING;

-- ============================================================
-- 二、设备型号（扩展）- 小车型号
-- ============================================================

-- 获取AGV类别ID
DO $$
DECLARE
    agv_category_id BIGINT;
BEGIN
    SELECT id INTO agv_category_id FROM device_category WHERE category_code = 'AGV';
    
    -- 轻载型AGV
    INSERT INTO device_model (model_code, model_name, category_id, manufacturer, useful_life, warning_days, specification, description, status)
    VALUES ('AGV-001', '轻载型AGV', agv_category_id, 'AGV厂商A', 10, 30, '载重50kg, 速度1m/s', '适用于轻载货物运输', 1)
    ON CONFLICT (model_code) DO NOTHING;
    
    -- 中载型AGV
    INSERT INTO device_model (model_code, model_name, category_id, manufacturer, useful_life, warning_days, specification, description, status)
    VALUES ('AGV-002', '中载型AGV', agv_category_id, 'AGV厂商A', 10, 30, '载重200kg, 速度0.8m/s', '适用于中等载重货物运输', 1)
    ON CONFLICT (model_code) DO NOTHING;
    
    -- 重载型AGV
    INSERT INTO device_model (model_code, model_name, category_id, manufacturer, useful_life, warning_days, specification, description, status)
    VALUES ('AGV-003', '重载型AGV', agv_category_id, 'AGV厂商B', 10, 30, '载重500kg, 速度0.5m/s', '适用于重载货物运输', 1)
    ON CONFLICT (model_code) DO NOTHING;
END $$;

-- ============================================================
-- 三、小车状态表 (agv_status)
-- ============================================================
DROP TABLE IF EXISTS agv_status CASCADE;
CREATE TABLE agv_status (
    id BIGSERIAL PRIMARY KEY,
    agv_id BIGINT NOT NULL UNIQUE REFERENCES equipment(id) ON DELETE CASCADE,
    battery_level DECIMAL(5,2) DEFAULT 100.00 CHECK (battery_level >= 0 AND battery_level <= 100),
    agv_status VARCHAR(20) DEFAULT 'idle' CHECK (agv_status IN ('idle', 'working', 'charging', 'returning', 'fault')),
    current_task_id BIGINT,
    current_location VARCHAR(50),
    target_location VARCHAR(50),
    speed DECIMAL(6,2) DEFAULT 0.00,
    last_charge_time TIMESTAMP,
    total_work_time INT DEFAULT 0,
    total_distance DECIMAL(10,2) DEFAULT 0.00,
    fault_code VARCHAR(20),
    fault_message VARCHAR(500),
    last_update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_agv_status_agv_status ON agv_status(agv_status);
CREATE INDEX idx_agv_status_battery_level ON agv_status(battery_level);

-- ============================================================
-- 四、充电站表 (charging_station)
-- ============================================================
DROP TABLE IF EXISTS charging_station CASCADE;
CREATE TABLE charging_station (
    id BIGSERIAL PRIMARY KEY,
    station_code VARCHAR(50) NOT NULL UNIQUE,
    station_name VARCHAR(100) NOT NULL,
    location VARCHAR(100),
    x_position DECIMAL(10,2),
    y_position DECIMAL(10,2),
    z_position DECIMAL(10,2),
    power INT DEFAULT 2000,
    voltage INT DEFAULT 24,
    current INT DEFAULT 10,
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1, 2)),
    description VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_charging_station_status ON charging_station(status);

-- 插入默认充电站
INSERT INTO charging_station (station_code, station_name, location, x_position, y_position, z_position, power, voltage, current, status, description)
VALUES 
    ('CS-001', '1号充电站', '仓库A区', 0, 0, 0, 2000, 24, 10, 1, '主充电站'),
    ('CS-002', '2号充电站', '仓库B区', 20, 0, 0, 2000, 24, 10, 1, '副充电站')
ON CONFLICT (station_code) DO NOTHING;

-- ============================================================
-- 五、充电记录表 (charging_record)
-- ============================================================
DROP TABLE IF EXISTS charging_record CASCADE;
CREATE TABLE charging_record (
    id BIGSERIAL PRIMARY KEY,
    agv_id BIGINT NOT NULL REFERENCES equipment(id) ON DELETE CASCADE,
    station_id BIGINT NOT NULL REFERENCES charging_station(id) ON DELETE CASCADE,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    start_battery DECIMAL(5,2),
    end_battery DECIMAL(5,2),
    charging_duration INT,
    electricity_used DECIMAL(10,2) DEFAULT 0.00,
    charge_type VARCHAR(20) DEFAULT 'auto' CHECK (charge_type IN ('auto', 'manual')),
    result VARCHAR(20) CHECK (result IN ('success', 'interrupted', 'failed')),
    interrupt_reason VARCHAR(200),
    operator_id BIGINT,
    remark VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_charging_record_agv_id ON charging_record(agv_id);
CREATE INDEX idx_charging_record_station_id ON charging_record(station_id);
CREATE INDEX idx_charging_record_start_time ON charging_record(start_time);

-- ============================================================
-- 六、AGV任务表 (agv_task)
-- ============================================================
DROP TABLE IF EXISTS agv_task CASCADE;
CREATE TABLE agv_task (
    id BIGSERIAL PRIMARY KEY,
    task_code VARCHAR(50) NOT NULL UNIQUE,
    task_type VARCHAR(20) NOT NULL CHECK (task_type IN ('in', 'out', 'transfer')),
    order_id BIGINT,
    order_code VARCHAR(50),
    agv_id BIGINT REFERENCES equipment(id) ON DELETE SET NULL,
    priority INT DEFAULT 5 CHECK (priority >= 1 AND priority <= 10),
    from_location VARCHAR(50) NOT NULL,
    to_location VARCHAR(50) NOT NULL,
    cargo_info VARCHAR(200),
    task_status VARCHAR(20) DEFAULT 'pending' CHECK (task_status IN ('pending', 'executing', 'completed', 'failed', 'cancelled')),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    actual_duration INT,
    error_message VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_agv_task_task_status ON agv_task(task_status);
CREATE INDEX idx_agv_task_agv_id ON agv_task(agv_id);
CREATE INDEX idx_agv_task_order_id ON agv_task(order_id);
CREATE INDEX idx_agv_task_priority ON agv_task(priority DESC);

-- ============================================================
-- 七、小车零件（扩展）
-- ============================================================

-- 获取小车型号ID并插入零件
DO $$
DECLARE
    model_agv_001 BIGINT;
    model_agv_002 BIGINT;
    model_agv_003 BIGINT;
BEGIN
    SELECT id INTO model_agv_001 FROM device_model WHERE model_code = 'AGV-001';
    SELECT id INTO model_agv_002 FROM device_model WHERE model_code = 'AGV-002';
    SELECT id INTO model_agv_003 FROM device_model WHERE model_code = 'AGV-003';
    
    -- 锂电池组
    INSERT INTO element (element_code, element_name, model_id, category, specification, unit, stock_quantity, min_stock, unit_price, is_vulnerable, replace_cycle, supplier, description, status)
    VALUES 
        ('AGV_BATTERY_001', '锂电池组', model_agv_001, '小车零件', '24V/20Ah', '套', 10, 2, 2000.00, 0, 1095, '电池厂商', '适用于轻载型AGV', 1),
        ('AGV_BATTERY_002', '锂电池组', model_agv_002, '小车零件', '24V/30Ah', '套', 8, 2, 2800.00, 0, 1095, '电池厂商', '适用于中载型AGV', 1),
        ('AGV_BATTERY_003', '锂电池组', model_agv_003, '小车零件', '48V/40Ah', '套', 5, 1, 4500.00, 0, 1095, '电池厂商', '适用于重载型AGV', 1)
    ON CONFLICT (element_code) DO NOTHING;
    
    -- 驱动轮
    INSERT INTO element (element_code, element_name, category, specification, unit, stock_quantity, min_stock, unit_price, is_vulnerable, replace_cycle, supplier, description, status)
    VALUES ('AGV_WHEEL_001', '驱动轮', '小车零件', '直径150mm', '个', 20, 5, 300.00, 1, 180, '轮子厂商', 'AGV专用驱动轮', 1)
    ON CONFLICT (element_code) DO NOTHING;
    
    -- 避障传感器
    INSERT INTO element (element_code, element_name, category, specification, unit, stock_quantity, min_stock, unit_price, is_vulnerable, replace_cycle, supplier, description, status)
    VALUES ('AGV_SENSOR_001', '避障传感器', '小车零件', '激光雷达', '个', 15, 3, 1500.00, 0, 730, '传感器厂商', '激光避障传感器', 1)
    ON CONFLICT (element_code) DO NOTHING;
    
    -- 充电模块
    INSERT INTO element (element_code, element_name, category, specification, unit, stock_quantity, min_stock, unit_price, is_vulnerable, replace_cycle, supplier, description, status)
    VALUES ('AGV_CHARGER_001', '充电模块', '小车零件', '24V自适应', '套', 10, 2, 800.00, 0, 1095, '充电厂商', '智能充电模块', 1)
    ON CONFLICT (element_code) DO NOTHING;
END $$;

-- ============================================================
-- 八、示例数据 - 小车设备（不区分入库/出库，统一为AGV）
-- ============================================================

-- 获取小车型号ID
DO $$
DECLARE
    model_agv_001 BIGINT;
    model_agv_002 BIGINT;
    model_agv_003 BIGINT;
    charging_station_1 BIGINT;
BEGIN
    SELECT id INTO model_agv_001 FROM device_model WHERE model_code = 'AGV-001';
    SELECT id INTO model_agv_002 FROM device_model WHERE model_code = 'AGV-002';
    SELECT id INTO model_agv_003 FROM device_model WHERE model_code = 'AGV-003';
    SELECT id INTO charging_station_1 FROM charging_station WHERE station_code = 'CS-001';
    
    -- 插入AGV小车1号
    INSERT INTO equipment (equipment_code, equipment_name, model_id, location, serial_number, purchase_date, warranty_expire_date, install_date, status, is_enabled, x_position, y_position, z_position, description)
    VALUES 
        ('AGV-001', 'AGV小车1号', model_agv_001, '仓库A区', 'SN-AGV-001', '2025-01-01', '2028-01-01', '2025-01-15', 1, 1, 2, 0, 0, '轻载型AGV')
    ON CONFLICT (equipment_code) DO NOTHING;
    
    -- 插入AGV小车2号
    INSERT INTO equipment (equipment_code, equipment_name, model_id, location, serial_number, purchase_date, warranty_expire_date, install_date, status, is_enabled, x_position, y_position, z_position, description)
    VALUES 
        ('AGV-002', 'AGV小车2号', model_agv_002, '仓库A区', 'SN-AGV-002', '2025-01-01', '2028-01-01', '2025-01-15', 1, 1, 4, 0, 0, '中载型AGV')
    ON CONFLICT (equipment_code) DO NOTHING;
    
    -- 插入AGV小车3号
    INSERT INTO equipment (equipment_code, equipment_name, model_id, location, serial_number, purchase_date, warranty_expire_date, install_date, status, is_enabled, x_position, y_position, z_position, description)
    VALUES 
        ('AGV-003', 'AGV小车3号', model_agv_003, '仓库B区', 'SN-AGV-003', '2025-01-01', '2028-01-01', '2025-01-15', 1, 1, 6, 0, 0, '重载型AGV')
    ON CONFLICT (equipment_code) DO NOTHING;
    
    -- 插入AGV小车4号
    INSERT INTO equipment (equipment_code, equipment_name, model_id, location, serial_number, purchase_date, warranty_expire_date, install_date, status, is_enabled, x_position, y_position, z_position, description)
    VALUES 
        ('AGV-004', 'AGV小车4号', model_agv_001, '仓库B区', 'SN-AGV-004', '2025-01-01', '2028-01-01', '2025-01-15', 1, 1, 8, 0, 0, '轻载型AGV')
    ON CONFLICT (equipment_code) DO NOTHING;
    
    -- 插入AGV小车5号
    INSERT INTO equipment (equipment_code, equipment_name, model_id, location, serial_number, purchase_date, warranty_expire_date, install_date, status, is_enabled, x_position, y_position, z_position, description)
    VALUES 
        ('AGV-005', 'AGV小车5号', model_agv_002, '仓库C区', 'SN-AGV-005', '2025-01-01', '2028-01-01', '2025-01-15', 1, 1, 10, 0, 0, '中载型AGV')
    ON CONFLICT (equipment_code) DO NOTHING;
    
    -- 插入小车状态
    INSERT INTO agv_status (agv_id, battery_level, agv_status, current_location, last_charge_time, last_update_time)
    SELECT e.id, 85.00, 'idle', 'A-01-01', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP
    FROM equipment e WHERE e.equipment_code = 'AGV-001'
    ON CONFLICT (agv_id) DO NOTHING;
    
    INSERT INTO agv_status (agv_id, battery_level, agv_status, current_location, last_charge_time, last_update_time)
    SELECT e.id, 60.00, 'working', 'A-02-01', CURRENT_TIMESTAMP - INTERVAL '5 hours', CURRENT_TIMESTAMP
    FROM equipment e WHERE e.equipment_code = 'AGV-002'
    ON CONFLICT (agv_id) DO NOTHING;
    
    INSERT INTO agv_status (agv_id, battery_level, agv_status, current_location, last_charge_time, last_update_time)
    SELECT e.id, 95.00, 'charging', 'CS-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    FROM equipment e WHERE e.equipment_code = 'AGV-003'
    ON CONFLICT (agv_id) DO NOTHING;

    INSERT INTO agv_status (agv_id, battery_level, agv_status, current_location, last_charge_time, last_update_time)
    SELECT e.id, 45.00, 'returning', 'CS-002', CURRENT_TIMESTAMP - INTERVAL '8 hours', CURRENT_TIMESTAMP
    FROM equipment e WHERE e.equipment_code = 'AGV-004'
    ON CONFLICT (agv_id) DO NOTHING;

    INSERT INTO agv_status (agv_id, battery_level, agv_status, current_location, last_charge_time, last_update_time)
    SELECT e.id, 30.00, 'fault', 'A-03-01', CURRENT_TIMESTAMP - INTERVAL '1 hours', CURRENT_TIMESTAMP
    FROM equipment e WHERE e.equipment_code = 'AGV-005'
    ON CONFLICT (agv_id) DO NOTHING;
END $$;

