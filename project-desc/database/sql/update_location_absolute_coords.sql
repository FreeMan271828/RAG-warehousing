-- ============================================================
-- 仓库货位绝对坐标更新脚本
-- 坐标格式：排-列（例如 1-4 表示第1排第4列）
-- ============================================================

-- 1. 先删除现有坐标数据
UPDATE warehouse_location SET x_position = NULL, y_position = NULL, z_position = NULL;

-- 2. 定义仓库布局常量
-- A区: 10排 x 10列 (x: 0-9, y: 0-9)
-- B区: 10排 x 10列 (x: 10-19, y: 0-9)
-- C区: 10排 x 10列 (x: 20-29, y: 0-9)
-- D区: 10排 x 10列 (x: 30-39, y: 0-9)
-- E区: 10排 x 10列 (x: 40-49, y: 0-9)

-- 3. 更新A区货位坐标 (排 1-10, 列 1-10)
UPDATE warehouse_location
SET 
    x_position = (row_num - 1),           -- 0-9
    y_position = (col_num - 1),           -- 0-9
    z_position = (level_num - 1) * 2      -- 层高间隔2
WHERE area_code = 'A';

-- 4. 更新B区货位坐标
UPDATE warehouse_location
SET 
    x_position = 10 + (row_num - 1),      -- 10-19
    y_position = (col_num - 1),           -- 0-9
    z_position = (level_num - 1) * 2      -- 层高间隔2
WHERE area_code = 'B';

-- 5. 更新C区货位坐标
UPDATE warehouse_location
SET 
    x_position = 20 + (row_num - 1),      -- 20-29
    y_position = (col_num - 1),           -- 0-9
    z_position = (level_num - 1) * 2      -- 层高间隔2
WHERE area_code = 'C';

-- 6. 更新D区货位坐标
UPDATE warehouse_location
SET 
    x_position = 30 + (row_num - 1),      -- 30-39
    y_position = (col_num - 1),           -- 0-9
    z_position = (level_num - 1) * 2      -- 层高间隔2
WHERE area_code = 'D';

-- 7. 更新E区货位坐标
UPDATE warehouse_location
SET 
    x_position = 40 + (row_num - 1),      -- 40-49
    y_position = (col_num - 1),           -- 0-9
    z_position = (level_num - 1) * 2      -- 层高间隔2
WHERE area_code = 'E';

-- 8. 验证更新结果
SELECT 
    area_code,
    COUNT(*) as total,
    COUNT(CASE WHEN x_position IS NOT NULL THEN 1 END) as with_x,
    COUNT(CASE WHEN y_position IS NOT NULL THEN 1 END) as with_y,
    MIN(x_position) as min_x,
    MAX(x_position) as max_x,
    MIN(y_position) as min_y,
    MAX(y_position) as max_y
FROM warehouse_location
GROUP BY area_code
ORDER BY area_code;

-- 9. 显示更新后的样例数据
SELECT location_code, area_code, row_num, col_num, level_num, 
       x_position, y_position, z_position 
FROM warehouse_location 
ORDER BY area_code, row_num, col_num, level_num
LIMIT 20;
