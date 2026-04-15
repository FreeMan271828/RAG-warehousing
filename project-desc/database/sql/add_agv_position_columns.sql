-- 为 agv_status 表添加坐标字段
-- 用于3D可视化展示小车的位置

ALTER TABLE agv_status ADD COLUMN IF NOT EXISTS x_position DECIMAL(10,2);
ALTER TABLE agv_status ADD COLUMN IF NOT EXISTS y_position DECIMAL(10,2);
ALTER TABLE agv_status ADD COLUMN IF NOT EXISTS rotation DECIMAL(10,2) DEFAULT 0;
ALTER TABLE agv_status ADD COLUMN IF NOT EXISTS move_speed DECIMAL(6,2) DEFAULT 1.0;
ALTER TABLE agv_status ADD COLUMN IF NOT EXISTS battery_consumption_rate DECIMAL(6,2) DEFAULT 0.5;
ALTER TABLE agv_status ADD COLUMN IF NOT EXISTS turn_overhead DECIMAL(6,2) DEFAULT 0.5;
ALTER TABLE agv_status ADD COLUMN IF NOT EXISTS warning_battery_level DECIMAL(5,2) DEFAULT 20.00;

-- 为 agv_task 表添加字段用于估算返回电量
ALTER TABLE agv_task ADD COLUMN IF NOT EXISTS estimated_distance DECIMAL(10,2);
ALTER TABLE agv_task ADD COLUMN IF NOT EXISTS estimated_duration INT;
ALTER TABLE agv_task ADD COLUMN IF NOT EXISTS estimated_battery_consumption DECIMAL(10,2);
ALTER TABLE agv_task ADD COLUMN IF NOT EXISTS return_distance DECIMAL(10,2);
ALTER TABLE agv_task ADD COLUMN IF NOT EXISTS return_battery_needed DECIMAL(10,2);

-- 更新现有小车的初始坐标（根据current_location解析）
-- 注意：这只对有current_location的小车有效
UPDATE agv_status 
SET x_position = (
    CASE 
        WHEN cs.station_code LIKE 'CS-%' THEN COALESCE(cs.x_position, 0)
        WHEN e.equipment_code LIKE 'AGV-001' THEN 2
        WHEN e.equipment_code LIKE 'AGV-002' THEN 4
        WHEN e.equipment_code LIKE 'AGV-003' THEN 6
        WHEN e.equipment_code LIKE 'AGV-004' THEN 8
        WHEN e.equipment_code LIKE 'AGV-005' THEN 10
        ELSE 0
    END
),
y_position = (
    CASE 
        WHEN cs.station_code LIKE 'CS-%' THEN COALESCE(cs.y_position, 0)
        ELSE 0
    END
)
FROM equipment e 
LEFT JOIN charging_station cs ON agv_status.current_location LIKE cs.station_code || '%'
WHERE agv_status.agv_id = e.id;

-- 为还没有坐标的小车设置默认值（基于agv_id）
UPDATE agv_status 
SET x_position = COALESCE(x_position, agv_id * 2.0),
    y_position = COALESCE(y_position, 0)
WHERE x_position IS NULL OR y_position IS NULL;

-- 为没有move_speed的小车设置默认值
UPDATE agv_status SET move_speed = 1.0 WHERE move_speed IS NULL;
UPDATE agv_status SET battery_consumption_rate = 0.5 WHERE battery_consumption_rate IS NULL;
UPDATE agv_status SET turn_overhead = 0.5 WHERE turn_overhead IS NULL;
UPDATE agv_status SET warning_battery_level = 20.00 WHERE warning_battery_level IS NULL;
