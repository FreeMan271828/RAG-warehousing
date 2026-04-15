-- 为 sys_user 表添加 deleted 字段
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS deleted SMALLINT DEFAULT 0;

-- 为 sys_role 表添加 deleted 字段
ALTER TABLE sys_role ADD COLUMN IF NOT EXISTS deleted SMALLINT DEFAULT 0;

-- 验证字段是否添加成功
SELECT column_name, data_type, column_default 
FROM information_schema.columns 
WHERE table_name IN ('sys_user', 'sys_role') 
AND column_name = 'deleted';
