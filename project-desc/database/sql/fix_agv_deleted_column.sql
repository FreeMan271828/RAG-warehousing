-- ============================================================
-- 为 AGV 小车相关表添加 deleted 字段
-- 解决 MyBatis-Plus 逻辑删除查询报错
-- ============================================================

-- 为 agv_status 表添加 deleted 字段
ALTER TABLE agv_status ADD COLUMN IF NOT EXISTS deleted SMALLINT DEFAULT 0;

-- 为 charging_station 表添加 deleted 字段
ALTER TABLE charging_station ADD COLUMN IF NOT EXISTS deleted SMALLINT DEFAULT 0;

-- 为 charging_record 表添加 deleted 字段
ALTER TABLE charging_record ADD COLUMN IF NOT EXISTS deleted SMALLINT DEFAULT 0;

-- 为 agv_task 表添加 deleted 字段
ALTER TABLE agv_task ADD COLUMN IF NOT EXISTS deleted SMALLINT DEFAULT 0;

-- 验证字段是否添加成功
SELECT column_name, data_type, column_default, table_name
FROM information_schema.columns 
WHERE table_name IN ('agv_status', 'charging_station', 'charging_record', 'agv_task') 
AND column_name = 'deleted';
