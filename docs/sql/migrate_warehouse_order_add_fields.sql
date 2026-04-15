-- 出入库工单表结构修改
-- 添加审核相关字段和货物来源字段

-- 1. 添加审核相关字段
ALTER TABLE warehouse_order ADD COLUMN IF NOT EXISTS reviewer_id BIGINT;
ALTER TABLE warehouse_order ADD COLUMN IF NOT EXISTS reviewer_name VARCHAR(100);
ALTER TABLE warehouse_order ADD COLUMN IF NOT EXISTS review_time TIMESTAMP;
ALTER TABLE warehouse_order ADD COLUMN IF NOT EXISTS review_remark TEXT;

-- 2. 添加货物来源相关字段
ALTER TABLE warehouse_order ADD COLUMN IF NOT EXISTS source_warehouse_code VARCHAR(50);
ALTER TABLE warehouse_order ADD COLUMN IF NOT EXISTS source_warehouse_name VARCHAR(100);
ALTER TABLE warehouse_order ADD COLUMN IF NOT EXISTS source_location_id BIGINT;
ALTER TABLE warehouse_order ADD COLUMN IF NOT EXISTS source_location_code VARCHAR(50);

-- 3. 添加调拨类型字段（用于仓库内部调拨）
ALTER TABLE warehouse_order ADD COLUMN IF NOT EXISTS transfer_type VARCHAR(20);
COMMENT ON COLUMN warehouse_order.transfer_type IS '调拨类型: internal-内部调拨, external-外部调拨';

-- 4. 添加目标仓库相关字段
ALTER TABLE warehouse_order ADD COLUMN IF NOT EXISTS target_warehouse_code VARCHAR(50);
ALTER TABLE warehouse_order ADD COLUMN IF NOT EXISTS target_warehouse_name VARCHAR(100);

-- 5. 添加审核备注字段（兼容旧名称）
-- review_remark 已经添加在上面

-- 6. 修改状态字段说明（如果需要）
COMMENT ON COLUMN warehouse_order.order_status IS '工单状态: created-新建, pending_review-待审核, rejected-已拒绝, pending_in-待入货, in_progress-入库中, stored-存储中, out_progress-出库中, completed-已完成, cancelled-已取消';

-- 7. 添加批次号（用于追溯）
ALTER TABLE warehouse_order ADD COLUMN IF NOT EXISTS batch_no VARCHAR(50);

-- 8. 添加运输相关信息
ALTER TABLE warehouse_order ADD COLUMN IF NOT EXISTS transporter VARCHAR(100);
ALTER TABLE warehouse_order ADD COLUMN IF NOT EXISTS vehicle_no VARCHAR(50);

-- 9. 添加实际数量（用于记录实际入库/出库数量）
ALTER TABLE warehouse_order ADD COLUMN IF NOT EXISTS actual_quantity DECIMAL(18,4);
ALTER TABLE warehouse_order ADD COLUMN IF NOT EXISTS difference_quantity DECIMAL(18,4);

-- 10. 添加操作备注（区分审核备注）
ALTER TABLE warehouse_order ADD COLUMN IF NOT EXISTS operation_remark TEXT;

-- 11. 添加状态变更历史（JSON格式）
ALTER TABLE warehouse_order ADD COLUMN IF NOT EXISTS status_history JSONB;

PRINT 'Database migration completed successfully!';
