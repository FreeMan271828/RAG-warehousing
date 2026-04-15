-- ================================================================
-- 货位编码统一改为 排-列-层 格式（零填充），例如 "01-06-02"、"03-12-01"
-- 无区前缀，location_code 在同一区内唯一（配合 area_code 字段区分区域）
-- 坐标 x_position = 区偏移(每区10) + (排号-1)，用于仿真定位
-- ================================================================

-- 1. 更新 A 区货位
UPDATE warehouse_location
SET
    location_code = LPAD(row_num::TEXT, 2, '0')
                 || '-' || LPAD(col_num::TEXT, 2, '0')
                 || '-' || LPAD(COALESCE(level_num, 1)::TEXT, 2, '0'),  -- "01-06-02"
    x_position    = (row_num - 1),                                          -- 0-9
    y_position    = (col_num - 1) * 1.5,                                   -- 0-28.5
    z_position    = (COALESCE(level_num, 1) - 1) * 2.0                    -- 层高间隔2
WHERE area_code = 'A'
  AND row_num IS NOT NULL
  AND col_num IS NOT NULL;

-- 2. 更新 B 区货位
UPDATE warehouse_location
SET
    location_code = LPAD(row_num::TEXT, 2, '0')
                 || '-' || LPAD(col_num::TEXT, 2, '0')
                 || '-' || LPAD(COALESCE(level_num, 1)::TEXT, 2, '0'),
    x_position    = 10 + (row_num - 1),           -- 10-19
    y_position    = (col_num - 1) * 1.5,
    z_position    = (COALESCE(level_num, 1) - 1) * 2.0
WHERE area_code = 'B'
  AND row_num IS NOT NULL
  AND col_num IS NOT NULL;

-- 3. 更新 C 区货位
UPDATE warehouse_location
SET
    location_code = LPAD(row_num::TEXT, 2, '0')
                 || '-' || LPAD(col_num::TEXT, 2, '0')
                 || '-' || LPAD(COALESCE(level_num, 1)::TEXT, 2, '0'),
    x_position    = 20 + (row_num - 1),           -- 20-29
    y_position    = (col_num - 1) * 1.5,
    z_position    = (COALESCE(level_num, 1) - 1) * 2.0
WHERE area_code = 'C'
  AND row_num IS NOT NULL
  AND col_num IS NOT NULL;

-- 4. 更新 D 区货位
UPDATE warehouse_location
SET
    location_code = LPAD(row_num::TEXT, 2, '0')
                 || '-' || LPAD(col_num::TEXT, 2, '0')
                 || '-' || LPAD(COALESCE(level_num, 1)::TEXT, 2, '0'),
    x_position    = 30 + (row_num - 1),           -- 30-39
    y_position    = (col_num - 1) * 1.5,
    z_position    = (COALESCE(level_num, 1) - 1) * 2.0
WHERE area_code = 'D'
  AND row_num IS NOT NULL
  AND col_num IS NOT NULL;

-- 5. 验证
SELECT area_code,
       location_code,
       row_num,
       col_num,
       level_num,
       x_position,
       y_position,
       z_position
FROM warehouse_location
WHERE row_num IS NOT NULL
ORDER BY area_code, row_num, col_num, level_num
LIMIT 30;
