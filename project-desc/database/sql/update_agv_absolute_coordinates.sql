-- ============================================================
-- 更新AGV小车绝对直角坐标
-- 确保小车位置与新坐标系统一致
-- ============================================================

-- 1. 先查看所有的小车位置，了解数据格式
SELECT 
    e.equipment_code as agv_code,
    s.current_location,
    s.x_position,
    s.y_position,
    s.target_location,
    s.agv_status,
    s.battery_level
FROM agv_status s
JOIN equipment e ON s.agv_id = e.id
ORDER BY e.equipment_code;

-- 2. 逐个更新每个小车（更安全的做法）
-- 小车1: AGV-001 位置 A-01-004 -> x=0 (区域A=0 + 排01-1=0), y=4.5 (列004-1=3 * 1.5)
UPDATE agv_status 
SET x_position = 0, y_position = 4.5
WHERE agv_id = (SELECT id FROM equipment WHERE equipment_code = 'AGV-001');

-- 小车2: AGV-002
UPDATE agv_status 
SET x_position = 2, y_position = 0
WHERE agv_id = (SELECT id FROM equipment WHERE equipment_code = 'AGV-002');

-- 小车3: AGV-003  
UPDATE agv_status 
SET x_position = 0, y_position = 0
WHERE agv_id = (SELECT id FROM equipment WHERE equipment_code = 'AGV-003');

-- 小车4: AGV-004
UPDATE agv_status 
SET x_position = 4, y_position = 0
WHERE agv_id = (SELECT id FROM equipment WHERE equipment_code = 'AGV-004');

-- 小车5: AGV-005
UPDATE agv_status 
SET x_position = 6, y_position = 0
WHERE agv_id = (SELECT id FROM equipment WHERE equipment_code = 'AGV-005');

-- 3. 或者使用更安全的更新方式：基于货位格式解析
-- 先更新所有A区货位
UPDATE agv_status 
SET 
    x_position = 0 + (SUBSTRING(current_location FROM 4 FOR 2)::INT - 1),
    y_position = (SUBSTRING(current_location FROM 7 FOR 3)::INT - 1) * 1.5
WHERE current_location LIKE 'A-%'
AND LENGTH(current_location) >= 9;

-- 更新所有B区货位
UPDATE agv_status 
SET 
    x_position = 10 + (SUBSTRING(current_location FROM 4 FOR 2)::INT - 1),
    y_position = (SUBSTRING(current_location FROM 7 FOR 3)::INT - 1) * 1.5
WHERE current_location LIKE 'B-%'
AND LENGTH(current_location) >= 9;

-- 更新所有C区货位
UPDATE agv_status 
SET 
    x_position = 20 + (SUBSTRING(current_location FROM 4 FOR 2)::INT - 1),
    y_position = (SUBSTRING(current_location FROM 7 FOR 3)::INT - 1) * 1.5
WHERE current_location LIKE 'C-%'
AND LENGTH(current_location) >= 9;

-- 更新所有D区货位
UPDATE agv_status 
SET 
    x_position = 30 + (SUBSTRING(current_location FROM 4 FOR 2)::INT - 1),
    y_position = (SUBSTRING(current_location FROM 7 FOR 3)::INT - 1) * 1.5
WHERE current_location LIKE 'D-%'
AND LENGTH(current_location) >= 9;

-- 更新充电站位置
UPDATE agv_status SET x_position = 0, y_position = 0 WHERE current_location LIKE 'CS-001%';
UPDATE agv_status SET x_position = 20, y_position = 0 WHERE current_location LIKE 'CS-002%';

-- 4. 验证更新结果
SELECT 
    e.equipment_code as agv_code,
    s.current_location,
    s.x_position,
    s.y_position,
    s.target_location,
    s.agv_status,
    s.battery_level
FROM agv_status s
JOIN equipment e ON s.agv_id = e.id
ORDER BY e.equipment_code;

-- 5. 显示距离充电站的距离
SELECT 
    e.equipment_code as agv_code,
    s.current_location,
    s.x_position,
    s.y_position,
    s.target_location,
    -- 计算到CS-001的距离
    SQRT(POWER(COALESCE(s.x_position, 0) - 0, 2) + POWER(COALESCE(s.y_position, 0) - 0, 2)) as distance_to_cs001,
    -- 计算到CS-002的距离
    SQRT(POWER(COALESCE(s.x_position, 0) - 20, 2) + POWER(COALESCE(s.y_position, 0) - 0, 2)) as distance_to_cs002
FROM agv_status s
JOIN equipment e ON s.agv_id = e.id
ORDER BY e.equipment_code;
