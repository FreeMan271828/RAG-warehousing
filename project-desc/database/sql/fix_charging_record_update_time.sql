-- 修复 charging_record 表缺少 update_time 列的问题
-- 执行: psql -U postgres -d tobacco_warehouse_db -f fix_charging_record_update_time.sql

-- 添加 update_time 列到 charging_record 表
ALTER TABLE charging_record ADD COLUMN IF NOT EXISTS update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- 同时检查并修复 agv_task 表（如果需要）
-- ALTER TABLE agv_task ADD COLUMN IF NOT EXISTS update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
