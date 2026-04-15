-- ============================================================
-- AGV自动化操作和调度系统 - 数据库修改
-- 添加充电口、车辆位置、电量消耗等相关字段
-- ============================================================

-- 1. 修改充电站表，增加四个充电口
ALTER TABLE charging_station 
ADD COLUMN IF NOT EXISTS top_port VARCHAR(50),      -- 上充电口
ADD COLUMN IF NOT EXISTS bottom_port VARCHAR(50),    -- 下充电口  
ADD COLUMN IF NOT EXISTS left_port VARCHAR(50),      -- 左充电口
ADD COLUMN IF NOT EXISTS right_port VARCHAR(50);     -- 右充电口

-- 2. 修改agv_status表，增加电量消耗和位置相关字段
ALTER TABLE agv_status
ADD COLUMN IF NOT EXISTS x_position DECIMAL(10,2) DEFAULT 0,     -- X坐标
ADD COLUMN IF NOT EXISTS y_position DECIMAL(10,2) DEFAULT 0,     -- Y坐标
ADD COLUMN IF NOT EXISTS rotation DECIMAL(6,2) DEFAULT 0,       -- 旋转角度
ADD COLUMN IF NOT EXISTS charging_station_id BIGINT,             -- 当前所在充电站ID
ADD COLUMN IF NOT EXISTS charging_port VARCHAR(50),               -- 当前充电口
ADD COLUMN IF NOT EXISTS battery_consumption_rate DECIMAL(6,2) DEFAULT 0.5,  -- 每秒电量消耗百分比
ADD COLUMN IF NOT EXISTS move_speed DECIMAL(6,2) DEFAULT 1.0,              -- 移动速度 m/s
ADD COLUMN IF NOT EXISTS turn_overhead DECIMAL(6,2) DEFAULT 0.5,         -- 转弯额外耗时(秒)
ADD COLUMN IF NOT EXISTS warning_battery_level DECIMAL(5,2) DEFAULT 20.00; -- 预警电量

-- 3. 创建入库口表
DROP TABLE IF EXISTS warehouse_entry_port CASCADE;
CREATE TABLE warehouse_entry_port (
    id BIGSERIAL PRIMARY KEY,
    port_code VARCHAR(50) NOT NULL UNIQUE,
    port_name VARCHAR(100) NOT NULL,
    port_position VARCHAR(20) NOT NULL CHECK (port_position IN ('top', 'bottom', 'left', 'right')),
    x_position DECIMAL(10,2),
    y_position DECIMAL(10,2),
    z_position DECIMAL(10,2),
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1)),
    description VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0
);

-- 插入默认入库口（上下左右各一个）
INSERT INTO warehouse_entry_port (port_code, port_name, port_position, x_position, y_position, z_position, status, description)
VALUES 
    ('ENTRY-TOP', '入库口-上', 'top', 0, -25, 0, 1, '仓库上方入库口'),
    ('ENTRY-BOTTOM', '入库口-下', 'bottom', 0, 25, 0, 1, '仓库下方入库口'),
    ('ENTRY-LEFT', '入库口-左', 'left', -35, 0, 0, 1, '仓库左侧入库口'),
    ('ENTRY-RIGHT', '入库口-右', 'right', 35, 0, 0, 1, '仓库右侧入库口')
ON CONFLICT (port_code) DO NOTHING;

-- 4. 创建AGV位置历史表（用于路径追踪）
DROP TABLE IF EXISTS agv_position_history CASCADE;
CREATE TABLE agv_position_history (
    id BIGSERIAL PRIMARY KEY,
    agv_id BIGINT NOT NULL REFERENCES equipment(id) ON DELETE CASCADE,
    x_position DECIMAL(10,2) NOT NULL,
    y_position DECIMAL(10,2) NOT NULL,
    rotation DECIMAL(6,2),
    record_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_agv_position_history_agv_id ON agv_position_history(agv_id);
CREATE INDEX idx_agv_position_history_record_time ON agv_position_history(record_time);

-- 5. 修改agv_task表，增加任务预计电量消耗相关字段
ALTER TABLE agv_task
ADD COLUMN IF NOT EXISTS estimated_distance DECIMAL(10,2),    -- 预计行驶距离
ADD COLUMN IF NOT EXISTS estimated_duration INT,                -- 预计耗时(秒)
ADD COLUMN IF NOT EXISTS estimated_battery_consumption DECIMAL(5,2), -- 预计电量消耗
ADD COLUMN IF NOT EXISTS actual_distance DECIMAL(10,2),         -- 实际行驶距离
ADD COLUMN IF NOT EXISTS return_distance DECIMAL(10,2),         -- 返回充电站距离
ADD COLUMN IF NOT EXISTS return_battery_needed DECIMAL(5,2);    -- 返回所需电量

-- 6. 更新现有充电站数据，增加充电口信息
UPDATE charging_station SET 
    top_port = 'PORT-TOP-001',
    bottom_port = 'PORT-BOTTOM-001',
    left_port = 'PORT-LEFT-001',
    right_port = 'PORT-RIGHT-001'
WHERE station_code = 'CS-001';

UPDATE charging_station SET 
    top_port = 'PORT-TOP-002',
    bottom_port = 'PORT-BOTTOM-002',
    left_port = 'PORT-LEFT-002',
    right_port = 'PORT-RIGHT-002'
WHERE station_code = 'CS-002';
