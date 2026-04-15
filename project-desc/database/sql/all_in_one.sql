-- ============================================================
-- 延安烟厂三维数字孪生系统 - 完整数据库初始化脚本
-- 数据库: PostgreSQL
-- 版本: 1.2
-- 创建日期: 2025-01
-- 更新日期: 2026-02-22 (整合所有SQL脚本)
-- ============================================================

-- ============================================================
-- 第一部分：数据库创建
-- ============================================================

DROP DATABASE IF EXISTS tobacco_warehouse_db;
CREATE DATABASE tobacco_warehouse_db WITH ENCODING 'UTF8';

\c tobacco_warehouse_db;

-- ============================================================
-- 第二部分：基础表结构
-- ============================================================

-- ============================================================
-- 一、设备管理模块
-- ============================================================

-- 1.1 设备类别表 (device_category)
DROP TABLE IF EXISTS device_category CASCADE;
CREATE TABLE device_category (
    id BIGSERIAL PRIMARY KEY,
    category_code VARCHAR(50) NOT NULL UNIQUE,
    category_name VARCHAR(100) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    description VARCHAR(500),
    sort_order INT DEFAULT 0,
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1)),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);

-- 1.2 设备型号表 (device_model)
DROP TABLE IF EXISTS device_model CASCADE;
CREATE TABLE device_model (
    id BIGSERIAL PRIMARY KEY,
    model_code VARCHAR(50) NOT NULL UNIQUE,
    model_name VARCHAR(100) NOT NULL,
    category_id BIGINT NOT NULL,
    manufacturer VARCHAR(100),
    useful_life INT,
    warning_days INT DEFAULT 30,
    specification TEXT,
    description VARCHAR(500),
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1)),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_device_model_category_id ON device_model(category_id);

-- 1.3 设备表 (equipment)
DROP TABLE IF EXISTS equipment CASCADE;
CREATE TABLE equipment (
    id BIGSERIAL PRIMARY KEY,
    equipment_code VARCHAR(50) NOT NULL UNIQUE,
    equipment_name VARCHAR(100) NOT NULL,
    model_id BIGINT NOT NULL,
    parent_id BIGINT DEFAULT 0,
    location VARCHAR(100),
    serial_number VARCHAR(100),
    purchase_date DATE,
    warranty_expire_date DATE,
    install_date DATE,
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1, 2, 3)),
    is_enabled SMALLINT DEFAULT 1 CHECK (is_enabled IN (0, 1)),
    x_position DECIMAL(10,2),
    y_position DECIMAL(10,2),
    z_position DECIMAL(10,2),
    description VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_equipment_model_id ON equipment(model_id);

-- 1.4 零件表 (element)
DROP TABLE IF EXISTS element CASCADE;
CREATE TABLE element (
    id BIGSERIAL PRIMARY KEY,
    element_code VARCHAR(50) NOT NULL UNIQUE,
    element_name VARCHAR(100) NOT NULL,
    model_id BIGINT,
    category VARCHAR(50),
    specification VARCHAR(100),
    unit VARCHAR(20) DEFAULT '个',
    stock_quantity INT DEFAULT 0,
    min_stock INT DEFAULT 0,
    unit_price DECIMAL(10,2) DEFAULT 0.00,
    is_vulnerable SMALLINT DEFAULT 0 CHECK (is_vulnerable IN (0, 1)),
    replace_cycle INT,
    supplier VARCHAR(100),
    description VARCHAR(500),
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1)),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);

-- 1.5 知识库文档表 (guide_doc)
DROP TABLE IF EXISTS guide_doc CASCADE;
CREATE TABLE guide_doc (
    id BIGSERIAL PRIMARY KEY,
    doc_title VARCHAR(200) NOT NULL,
    category_id BIGINT,
    model_id BIGINT,
    doc_type VARCHAR(20),
    file_path VARCHAR(500),
    file_size BIGINT,
    file_type VARCHAR(20),
    content TEXT,
    view_count INT DEFAULT 0,
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1)),
    create_by BIGINT,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);

-- 1.6 知识库视频表 (guide_video)
DROP TABLE IF EXISTS guide_video CASCADE;
CREATE TABLE guide_video (
    id BIGSERIAL PRIMARY KEY,
    video_title VARCHAR(200) NOT NULL,
    category_id BIGINT,
    model_id BIGINT,
    video_type VARCHAR(20),
    video_path VARCHAR(500),
    thumbnail_path VARCHAR(500),
    duration INT,
    file_size BIGINT,
    description VARCHAR(500),
    view_count INT DEFAULT 0,
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1)),
    create_by BIGINT,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);

-- ============================================================
-- 二、系统运维模块
-- ============================================================

-- 2.1 保养点表 (keep_point)
DROP TABLE IF EXISTS keep_point CASCADE;
CREATE TABLE keep_point (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL,
    point_name VARCHAR(100) NOT NULL,
    point_code VARCHAR(50) NOT NULL UNIQUE,
    check_content VARCHAR(500),
    check_standard VARCHAR(500),
    check_method VARCHAR(200),
    cycle_type VARCHAR(20) DEFAULT 'daily',
    cycle_days INT DEFAULT 1,
    sort_order INT DEFAULT 0,
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1)),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_keep_point_category_id ON keep_point(category_id);

-- 2.2 保养计划表 (maintenance_plan)
DROP TABLE IF EXISTS maintenance_plan CASCADE;
CREATE TABLE maintenance_plan (
    id BIGSERIAL PRIMARY KEY,
    plan_code VARCHAR(50) NOT NULL UNIQUE,
    equipment_id BIGINT NOT NULL,
    plan_type VARCHAR(20),
    plan_name VARCHAR(200) NOT NULL,
    plan_content TEXT,
    cycle_type VARCHAR(20),
    cycle_days INT,
    start_date DATE NOT NULL,
    end_date DATE,
    responsible_user BIGINT,
    estimated_hours DECIMAL(10,2),
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1, 2, 3)),
    create_by BIGINT,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_maintenance_plan_equipment_id ON maintenance_plan(equipment_id);

-- 2.3 保养记录表 (maintenance_record)
DROP TABLE IF EXISTS maintenance_record CASCADE;
CREATE TABLE maintenance_record (
    id BIGSERIAL PRIMARY KEY,
    record_code VARCHAR(50) NOT NULL UNIQUE,
    plan_id BIGINT,
    equipment_id BIGINT NOT NULL,
    maintenance_type VARCHAR(20),
    execute_date DATE NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    actual_hours DECIMAL(10,2),
    executor_id BIGINT,
    check_points TEXT,
    result VARCHAR(20),
    problem_desc VARCHAR(500),
    solution VARCHAR(500),
    attachments VARCHAR(1000),
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1, 2, 3)),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_maintenance_record_equipment_id ON maintenance_record(equipment_id);

-- 2.4 设备维修记录表 (maintain_record)
DROP TABLE IF EXISTS maintain_record CASCADE;
CREATE TABLE maintain_record (
    id BIGSERIAL PRIMARY KEY,
    record_code VARCHAR(50) NOT NULL UNIQUE,
    equipment_id BIGINT NOT NULL,
    fault_type VARCHAR(50),
    fault_desc VARCHAR(1000) NOT NULL,
    fault_level SMALLINT CHECK (fault_level IN (1, 2, 3)),
    occur_time TIMESTAMP NOT NULL,
    report_user BIGINT,
    assign_user BIGINT,
    maintain_user BIGINT,
    receive_time TIMESTAMP,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    repair_hours DECIMAL(10,2),
    fault_cause VARCHAR(500),
    solution VARCHAR(1000),
    cost_amount DECIMAL(10,2) DEFAULT 0.00,
    result VARCHAR(20),
    status SMALLINT DEFAULT 1 CHECK (status IN (1, 2, 3, 4, 5)),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_maintain_record_equipment_id ON maintain_record(equipment_id);

-- 2.5 零件更换记录表 (used_element)
DROP TABLE IF EXISTS used_element CASCADE;
CREATE TABLE used_element (
    id BIGSERIAL PRIMARY KEY,
    record_code VARCHAR(50) NOT NULL UNIQUE,
    equipment_id BIGINT NOT NULL,
    element_id BIGINT NOT NULL,
    maintain_record_id BIGINT,
    replace_quantity INT DEFAULT 1,
    replace_date DATE NOT NULL,
    replace_reason VARCHAR(500),
    cost_amount DECIMAL(10,2) DEFAULT 0.00,
    operator_id BIGINT,
    remark VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);

-- ============================================================
-- 三、用户角色管理模块
-- ============================================================

-- 3.1 系统用户表 (sys_user)
DROP TABLE IF EXISTS sys_user CASCADE;
CREATE TABLE sys_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL,
    real_name VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    avatar VARCHAR(500),
    dept_id BIGINT,
    post VARCHAR(50),
    login_ip VARCHAR(50),
    login_time TIMESTAMP,
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1)),
    is_admin SMALLINT DEFAULT 0 CHECK (is_admin IN (0, 1)),
    create_by BIGINT,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);

-- 3.2 角色表 (sys_role)
DROP TABLE IF EXISTS sys_role CASCADE;
CREATE TABLE sys_role (
    id BIGSERIAL PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    role_name VARCHAR(100) NOT NULL,
    role_type VARCHAR(20) DEFAULT 'system',
    description VARCHAR(500),
    sort_order INT DEFAULT 0,
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1)),
    create_by BIGINT,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);

-- 3.3 用户角色关联表 (sys_user_role)
DROP TABLE IF EXISTS sys_user_role CASCADE;
CREATE TABLE sys_user_role (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_sys_user_role_user_id ON sys_user_role(user_id);
CREATE INDEX idx_sys_user_role_role_id ON sys_user_role(role_id);

-- 3.4 权限表 (sys_permission)
DROP TABLE IF EXISTS sys_permission CASCADE;
CREATE TABLE sys_permission (
    id BIGSERIAL PRIMARY KEY,
    permission_code VARCHAR(100) NOT NULL UNIQUE,
    permission_name VARCHAR(100) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    permission_type VARCHAR(20) DEFAULT 'menu',
    resource_path VARCHAR(200),
    icon VARCHAR(50),
    sort_order INT DEFAULT 0,
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1)),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);

-- 3.5 角色权限关联表 (sys_role_permission)
DROP TABLE IF EXISTS sys_role_permission CASCADE;
CREATE TABLE sys_role_permission (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_sys_role_permission_role_id ON sys_role_permission(role_id);

-- 3.6 部门表 (sys_dept)
DROP TABLE IF EXISTS sys_dept CASCADE;
CREATE TABLE sys_dept (
    id BIGSERIAL PRIMARY KEY,
    dept_name VARCHAR(100) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    dept_leader BIGINT,
    phone VARCHAR(20),
    sort_order INT DEFAULT 0,
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1)),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);

-- ============================================================
-- 四、电池管理模块
-- ============================================================

-- 4.1 电池基本信息表 (battery_basic_info)
DROP TABLE IF EXISTS battery_basic_info CASCADE;
CREATE TABLE battery_basic_info (
    id BIGSERIAL PRIMARY KEY,
    battery_code VARCHAR(50) NOT NULL UNIQUE,
    battery_name VARCHAR(100) NOT NULL,
    battery_type VARCHAR(50),
    battery_model VARCHAR(50),
    manufacturer VARCHAR(100),
    purchase_date DATE,
    warranty_expire_date DATE,
    rated_capacity DECIMAL(10,2),
    rated_voltage DECIMAL(10,2),
    total_charge_times INT DEFAULT 0,
    total_usage_hours DECIMAL(10,2) DEFAULT 0.00,
    health_status SMALLINT DEFAULT 100 CHECK (health_status >= 0 AND health_status <= 100),
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1, 2, 3, 4)),
    location VARCHAR(100),
    remark VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);

-- 4.2 充电实时记录表 (charging_now)
DROP TABLE IF EXISTS charging_now CASCADE;
CREATE TABLE charging_now (
    id BIGSERIAL PRIMARY KEY,
    battery_id BIGINT NOT NULL,
    battery_code VARCHAR(50) NOT NULL,
    charge_start_time TIMESTAMP NOT NULL,
    voltage DECIMAL(10,2),
    current DECIMAL(10,2),
    temperature DECIMAL(10,2),
    charge_power DECIMAL(10,2),
    charge_duration INT DEFAULT 0,
    charged_capacity DECIMAL(10,2) DEFAULT 0.00,
    soc SMALLINT DEFAULT 0 CHECK (soc >= 0 AND soc <= 100),
    is_charging SMALLINT DEFAULT 1 CHECK (is_charging IN (0, 1)),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_charging_now_battery_id ON charging_now(battery_id);

-- 4.3 充电历史记录表 (charge_history)
DROP TABLE IF EXISTS charge_history CASCADE;
CREATE TABLE charge_history (
    id BIGSERIAL PRIMARY KEY,
    battery_id BIGINT NOT NULL,
    battery_code VARCHAR(50) NOT NULL,
    charge_start_time TIMESTAMP NOT NULL,
    charge_end_time TIMESTAMP,
    duration INT DEFAULT 0,
    start_voltage DECIMAL(10,2),
    end_voltage DECIMAL(10,2),
    start_soc SMALLINT DEFAULT 0 CHECK (start_soc >= 0 AND start_soc <= 100),
    end_soc SMALLINT DEFAULT 0 CHECK (end_soc >= 0 AND end_soc <= 100),
    charged_capacity DECIMAL(10,2) DEFAULT 0.00,
    charge_power DECIMAL(10,2) DEFAULT 0.00,
    max_temperature DECIMAL(10,2),
    is_completed SMALLINT DEFAULT 1 CHECK (is_completed IN (0, 1)),
    is_error SMALLINT DEFAULT 0 CHECK (is_error IN (0, 1)),
    error_desc VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_charge_history_battery_id ON charge_history(battery_id);

-- 4.4 充电故障记录表 (charge_error)
DROP TABLE IF EXISTS charge_error CASCADE;
CREATE TABLE charge_error (
    id BIGSERIAL PRIMARY KEY,
    battery_id BIGINT NOT NULL,
    battery_code VARCHAR(50) NOT NULL,
    error_code VARCHAR(50),
    error_type VARCHAR(50),
    error_desc VARCHAR(500) NOT NULL,
    error_time TIMESTAMP NOT NULL,
    resolve_time TIMESTAMP,
    voltage DECIMAL(10,2),
    current DECIMAL(10,2),
    temperature DECIMAL(10,2),
    is_resolved SMALLINT DEFAULT 0 CHECK (is_resolved IN (0, 1)),
    resolve_method VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);

-- ============================================================
-- 五、日志管理模块
-- ============================================================

-- 5.1 操作日志表 (sys_operation_log)
DROP TABLE IF EXISTS sys_operation_log CASCADE;
CREATE TABLE sys_operation_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(50),
    module VARCHAR(50),
    operation VARCHAR(50),
    method VARCHAR(200),
    request_url VARCHAR(500),
    request_method VARCHAR(10),
    request_params TEXT,
    response_result TEXT,
    ip_address VARCHAR(50),
    execute_time INT,
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1)),
    error_msg VARCHAR(1000),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_sys_operation_log_user_id ON sys_operation_log(user_id);
CREATE INDEX idx_sys_operation_log_create_time ON sys_operation_log(create_time);

-- ============================================================
-- 六、仓库管理模块
-- ============================================================

-- 6.1 货位表 (warehouse_location)
DROP TABLE IF EXISTS warehouse_location CASCADE;
CREATE TABLE warehouse_location (
    id BIGSERIAL PRIMARY KEY,
    location_code VARCHAR(50) NOT NULL UNIQUE,
    location_name VARCHAR(100),
    area_code VARCHAR(20) NOT NULL,
    area_name VARCHAR(50),
    row_num INT DEFAULT 1,
    col_num INT DEFAULT 1,
    level_num INT DEFAULT 1,
    location_type VARCHAR(20) DEFAULT 'storage' CHECK (location_type IN ('storage', 'picking', 'buffer')),
    current_status VARCHAR(20) DEFAULT 'empty' CHECK (current_status IN ('empty', 'occupied', 'reserved')),
    capacity DECIMAL(10,2) DEFAULT 2.5,
    max_weight DECIMAL(10,2) DEFAULT 500.00,
    length DECIMAL(10,2),
    width DECIMAL(10,2),
    height DECIMAL(10,2),
    x_position DECIMAL(10,2),
    y_position DECIMAL(10,2),
    z_position DECIMAL(10,2),
    description VARCHAR(500),
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1)),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_warehouse_location_area_code ON warehouse_location(area_code);
CREATE INDEX idx_warehouse_location_status ON warehouse_location(status);

-- 6.2 托盘表 (warehouse_pallet)
DROP TABLE IF EXISTS warehouse_pallet CASCADE;
CREATE TABLE warehouse_pallet (
    id BIGSERIAL PRIMARY KEY,
    pallet_code VARCHAR(50) NOT NULL UNIQUE,
    pallet_name VARCHAR(100),
    specification VARCHAR(100),
    pallet_type VARCHAR(20) DEFAULT 'standard',
    length DECIMAL(10,2),
    width DECIMAL(10,2),
    height DECIMAL(10,2),
    max_weight DECIMAL(10,2) DEFAULT 1000.00,
    material VARCHAR(50),
    location_id BIGINT,
    location_code VARCHAR(50),
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1)),
    description VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_warehouse_pallet_location_id ON warehouse_pallet(location_id);

-- 6.3 箱子表 (warehouse_box)
DROP TABLE IF EXISTS warehouse_box CASCADE;
CREATE TABLE warehouse_box (
    id BIGSERIAL PRIMARY KEY,
    box_code VARCHAR(50) NOT NULL UNIQUE,
    box_name VARCHAR(100),
    specification VARCHAR(100),
    box_type VARCHAR(20) DEFAULT 'cardboard',
    length DECIMAL(10,2),
    width DECIMAL(10,2),
    height DECIMAL(10,2),
    max_weight DECIMAL(10,2) DEFAULT 50.00,
    material VARCHAR(50),
    color VARCHAR(30),
    pallet_id BIGINT,
    location_id BIGINT,
    location_code VARCHAR(50),
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1)),
    description VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_warehouse_box_pallet_id ON warehouse_box(pallet_id);
CREATE INDEX idx_warehouse_box_location_id ON warehouse_box(location_id);

-- 6.4 出入库工单表 (warehouse_order)
DROP TABLE IF EXISTS warehouse_order CASCADE;
CREATE TABLE warehouse_order (
    id BIGSERIAL PRIMARY KEY,
    order_code VARCHAR(50) NOT NULL UNIQUE,
    order_type VARCHAR(20) NOT NULL CHECK (order_type IN ('in', 'out', 'transfer')),
    order_status VARCHAR(20) DEFAULT 'pending' CHECK (order_status IN ('pending', 'processing', 'completed', 'cancelled')),
    priority SMALLINT DEFAULT 1 CHECK (priority IN (1, 2, 3)),
    warehouse_code VARCHAR(50),
    warehouse_name VARCHAR(100),
    source_location_id BIGINT,
    source_location_code VARCHAR(50),
    target_location_id BIGINT,
    target_location_code VARCHAR(50),
    pallet_id BIGINT,
    pallet_code VARCHAR(50),
    box_id BIGINT,
    box_code VARCHAR(50),
    item_code VARCHAR(50),
    item_name VARCHAR(100),
    item_quantity DECIMAL(10,2),
    item_unit VARCHAR(20),
    operator_id BIGINT,
    operator_name VARCHAR(50),
    plan_start_time TIMESTAMP,
    plan_end_time TIMESTAMP,
    actual_start_time TIMESTAMP,
    actual_end_time TIMESTAMP,
    remark VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_warehouse_order_order_type ON warehouse_order(order_type);
CREATE INDEX idx_warehouse_order_order_status ON warehouse_order(order_status);
CREATE INDEX idx_warehouse_order_create_time ON warehouse_order(create_time);

-- 6.5 库存表 (warehouse_inventory)
DROP TABLE IF EXISTS warehouse_inventory CASCADE;
CREATE TABLE warehouse_inventory (
    id BIGSERIAL PRIMARY KEY,
    inventory_code VARCHAR(50) NOT NULL UNIQUE,
    item_code VARCHAR(50) NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    item_category VARCHAR(50),
    specification VARCHAR(100),
    unit VARCHAR(20),
    quantity DECIMAL(10,2) DEFAULT 0,
    available_quantity DECIMAL(10,2) DEFAULT 0,
    locked_quantity DECIMAL(10,2) DEFAULT 0,
    location_id BIGINT,
    location_code VARCHAR(50),
    pallet_id BIGINT,
    pallet_code VARCHAR(50),
    box_id BIGINT,
    box_code VARCHAR(50),
    batch_code VARCHAR(50),
    production_date DATE,
    expire_date DATE,
    min_stock DECIMAL(10,2) DEFAULT 0,
    max_stock DECIMAL(10,2),
    status SMALLINT DEFAULT 1 CHECK (status IN (0, 1)),
    description VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_warehouse_inventory_item_code ON warehouse_inventory(item_code);
CREATE INDEX idx_warehouse_inventory_location_id ON warehouse_inventory(location_id);

-- ============================================================
-- 七、AGV智能小车模块
-- ============================================================

-- 7.1 小车状态表 (agv_status)
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
    -- AGV自动化扩展字段
    x_position DECIMAL(10,2) DEFAULT 0,
    y_position DECIMAL(10,2) DEFAULT 0,
    rotation DECIMAL(6,2) DEFAULT 0,
    charging_station_id BIGINT,
    charging_port VARCHAR(50),
    battery_consumption_rate DECIMAL(6,2) DEFAULT 0.5,
    move_speed DECIMAL(6,2) DEFAULT 1.0,
    turn_overhead DECIMAL(6,2) DEFAULT 0.5,
    warning_battery_level DECIMAL(5,2) DEFAULT 20.00,
    last_update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_agv_status_agv_status ON agv_status(agv_status);
CREATE INDEX idx_agv_status_battery_level ON agv_status(battery_level);

-- 7.2 充电站表 (charging_station)
DROP TABLE IF EXISTS charging_station CASCADE;
CREATE TABLE charging_station (
    id BIGSERIAL PRIMARY KEY,
    station_code VARCHAR(50) NOT NULL UNIQUE,
    station_name VARCHAR(100) NOT NULL,
    location VARCHAR(100),
    x_position DECIMAL(10,2),
    y_position DECIMAL(10,2),
    z_position DECIMAL(10,2),
    -- AGV自动化扩展字段
    top_port VARCHAR(50),
    bottom_port VARCHAR(50),
    left_port VARCHAR(50),
    right_port VARCHAR(50),
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

-- 7.3 充电记录表 (charging_record)
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
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_charging_record_agv_id ON charging_record(agv_id);
CREATE INDEX idx_charging_record_station_id ON charging_record(station_id);
CREATE INDEX idx_charging_record_start_time ON charging_record(start_time);

-- 7.4 AGV任务表 (agv_task)
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
    -- AGV自动化扩展字段
    estimated_distance DECIMAL(10,2),
    estimated_duration INT,
    estimated_battery_consumption DECIMAL(5,2),
    actual_distance DECIMAL(10,2),
    return_distance DECIMAL(10,2),
    return_battery_needed DECIMAL(5,2),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0
);
CREATE INDEX idx_agv_task_task_status ON agv_task(task_status);
CREATE INDEX idx_agv_task_agv_id ON agv_task(agv_id);
CREATE INDEX idx_agv_task_order_id ON agv_task(order_id);
CREATE INDEX idx_agv_task_priority ON agv_task(priority DESC);

-- 7.5 入库口表 (warehouse_entry_port)
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

-- 7.6 AGV位置历史表 (agv_position_history)
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

-- ============================================================
-- 第三部分：初始化基础数据
-- ============================================================

-- ============================================================
-- 一、初始化基础数据
-- ============================================================

-- 初始化默认管理员账号 (密码: admin123)
INSERT INTO sys_user (username, password, real_name, is_admin, status) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 1, 1);

-- 初始化默认角色
INSERT INTO sys_role (role_code, role_name, role_type, description, status) 
VALUES ('admin', '超级管理员', 'system', '系统超级管理员，拥有所有权限', 1),
       ('user', '普通用户', 'user', '普通用户，基本操作权限', 1);

-- 关联管理员角色
INSERT INTO sys_user_role (user_id, role_id) 
SELECT u.id, r.id FROM sys_user u, sys_role r WHERE u.username = 'admin' AND r.role_code = 'admin';

-- 初始化默认部门
INSERT INTO sys_dept (dept_name, parent_id, sort_order, status) 
VALUES ('延安烟厂', 0, 1, 1),
       ('设备管理部', 1, 1, 1),
       ('运维部', 1, 2, 1),
       ('仓储部', 1, 3, 1);

-- 初始化默认设备类别
INSERT INTO device_category (category_code, category_name, parent_id, sort_order, status) 
VALUES ('EQUIPMENT', '设备', 0, 1, 1),
       ('BATTERY', '电池', 0, 2, 1),
       ('SENSOR', '传感器', 0, 3, 1),
       ('CONTROL', '控制柜', 0, 4, 1),
       ('AGV', '智能小车', 0, 10, 1);

-- ============================================================
-- 二、初始化小车型号
-- ============================================================

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
-- 三、初始化仓库测试数据
-- ============================================================

-- 初始化货位数据
INSERT INTO warehouse_location (location_code, location_name, area_code, area_name, row_num, col_num, level_num, location_type, current_status, capacity, max_weight, status)
VALUES 
('A-01-01-01', 'A区01排01列01层', 'A', 'A区', 1, 1, 1, 'storage', 'empty', 2.5, 500, 1),
('A-01-01-02', 'A区01排01列02层', 'A', 'A区', 1, 1, 2, 'storage', 'empty', 2.5, 500, 1),
('A-01-01-03', 'A区01排01列03层', 'A', 'A区', 1, 1, 3, 'storage', 'empty', 2.5, 500, 1),
('A-01-02-01', 'A区01排02列01层', 'A', 'A区', 1, 2, 1, 'storage', 'occupied', 2.5, 500, 1),
('A-01-02-02', 'A区01排02列02层', 'A', 'A区', 1, 2, 2, 'storage', 'empty', 2.5, 500, 1),
('A-02-01-01', 'A区02排01列01层', 'A', 'A区', 2, 1, 1, 'storage', 'empty', 2.5, 500, 1),
('A-02-01-02', 'A区02排01列02层', 'A', 'A区', 2, 1, 2, 'storage', 'empty', 2.5, 500, 1),
('B-01-01-01', 'B区01排01列01层', 'B', 'B区', 1, 1, 1, 'storage', 'empty', 3.0, 800, 1),
('B-01-01-02', 'B区01排01列02层', 'B', 'B区', 1, 1, 2, 'storage', 'empty', 3.0, 800, 1),
('B-01-01-03', 'B区01排01列03层', 'B', 'B区', 1, 1, 3, 'storage', 'reserved', 3.0, 800, 1),
('C-01-01-01', 'C区01排01列01层', 'C', 'C区', 1, 1, 1, 'picking', 'empty', 1.5, 200, 1),
('C-01-01-02', 'C区01排01列02层', 'C', 'C区', 1, 1, 2, 'picking', 'empty', 1.5, 200, 1);

-- 初始化托盘数据
INSERT INTO warehouse_pallet (pallet_code, pallet_name, specification, pallet_type, length, width, height, max_weight, material, status)
VALUES 
('PLT-001', '标准托盘1', '1200*1000mm', 'standard', 1200, 1000, 150, 1000, '木质', 1),
('PLT-002', '标准托盘2', '1200*1000mm', 'standard', 1200, 1000, 150, 1000, '木质', 1),
('PLT-003', '标准托盘3', '1200*1000mm', 'standard', 1200, 1000, 150, 1000, '木质', 1),
('PLT-004', '塑料托盘1', '1200*1000mm', 'plastic', 1200, 1000, 150, 800, '塑料', 1),
('PLT-005', '塑料托盘2', '1200*1000mm', 'plastic', 1200, 1000, 150, 800, '塑料', 1);

-- 初始化箱子数据
INSERT INTO warehouse_box (box_code, box_name, specification, box_type, length, width, height, max_weight, material, status)
VALUES 
('BOX-001', '纸箱1', '600*400*300mm', 'cardboard', 600, 400, 300, 50, '纸板', 1),
('BOX-002', '纸箱2', '600*400*300mm', 'cardboard', 600, 400, 300, 50, '纸板', 1),
('BOX-003', '纸箱3', '800*600*400mm', 'cardboard', 800, 600, 400, 80, '纸板', 1),
('BOX-004', '塑料箱1', '600*400*300mm', 'plastic', 600, 400, 300, 60, '塑料', 1),
('BOX-005', '塑料箱2', '600*400*300mm', 'plastic', 600, 400, 300, 60, '塑料', 1);

-- 初始化出入库工单数据
INSERT INTO warehouse_order (order_code, order_type, order_status, priority, warehouse_code, source_location_code, target_location_code, item_code, item_name, item_quantity, item_unit, operator_name, plan_start_time, plan_end_time)
VALUES 
('IN-20260221-001', 'in', 'pending', 1, 'WH001', NULL, 'A-01-01-01', 'MAT-001', '烟丝原料', 100, 'kg', '张三', '2026-02-21 09:00:00', '2026-02-21 12:00:00'),
('IN-20260221-002', 'in', 'processing', 1, 'WH001', NULL, 'A-01-01-02', 'MAT-002', '辅料A', 50, 'kg', '李四', '2026-02-21 10:00:00', '2026-02-21 14:00:00'),
('OUT-20260221-001', 'out', 'completed', 2, 'WH001', 'A-01-02-01', NULL, 'MAT-001', '烟丝原料', 30, 'kg', '王五', '2026-02-20 14:00:00', '2026-02-20 16:00:00'),
('OUT-20260221-002', 'out', 'pending', 3, 'WH001', 'B-01-01-01', NULL, 'PKG-001', '包装材料', 200, '个', '赵六', '2026-02-22 09:00:00', '2026-02-22 12:00:00');

-- 初始化库存数据
INSERT INTO warehouse_inventory (inventory_code, item_code, item_name, item_category, specification, unit, quantity, available_quantity, location_code, batch_code, production_date, min_stock, max_stock, status)
VALUES 
('INV-001', 'MAT-001', '烟丝原料', '原料', 'A级烟丝', 'kg', 100, 100, 'A-01-01-01', 'BATCH-20260201', '2026-02-01', 20, 500, 1),
('INV-002', 'MAT-002', '辅料A', '辅料', '标准规格', 'kg', 50, 50, 'A-01-01-02', 'BATCH-20260210', '2026-02-10', 10, 200, 1),
('INV-003', 'PKG-001', '包装材料', '包材', '标准包装', '个', 200, 200, 'B-01-01-01', 'BATCH-20260115', '2026-01-15', 50, 1000, 1),
('INV-004', 'MAT-003', '香精香料', '原料', '特殊配方', 'kg', 30, 30, 'A-01-02-01', 'BATCH-20260205', '2026-02-05', 5, 100, 1);

-- ============================================================
-- 四、初始化充电站数据
-- ============================================================

INSERT INTO charging_station (station_code, station_name, location, x_position, y_position, z_position, power, voltage, current, status, description, top_port, bottom_port, left_port, right_port)
VALUES 
    ('CS-001', '1号充电站', '仓库A区', 0, 0, 0, 2000, 24, 10, 1, '主充电站', 'PORT-TOP-001', 'PORT-BOTTOM-001', 'PORT-LEFT-001', 'PORT-RIGHT-001'),
    ('CS-002', '2号充电站', '仓库B区', 20, 0, 0, 2000, 24, 10, 1, '副充电站', 'PORT-TOP-002', 'PORT-BOTTOM-002', 'PORT-LEFT-002', 'PORT-RIGHT-002')
ON CONFLICT (station_code) DO NOTHING;

-- ============================================================
-- 五、初始化入库口数据
-- ============================================================

INSERT INTO warehouse_entry_port (port_code, port_name, port_position, x_position, y_position, z_position, status, description)
VALUES 
    ('ENTRY-TOP', '入库口-上', 'top', 0, -25, 0, 1, '仓库上方入库口'),
    ('ENTRY-BOTTOM', '入库口-下', 'bottom', 0, 25, 0, 1, '仓库下方入库口'),
    ('ENTRY-LEFT', '入库口-左', 'left', -35, 0, 0, 1, '仓库左侧入库口'),
    ('ENTRY-RIGHT', '入库口-右', 'right', 35, 0, 0, 1, '仓库右侧入库口')
ON CONFLICT (port_code) DO NOTHING;

-- ============================================================
-- 六、初始化AGV小车数据
-- ============================================================

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
    INSERT INTO agv_status (agv_id, battery_level, agv_status, current_location, last_charge_time, last_update_time, x_position, y_position)
    SELECT e.id, 85.00, 'idle', 'A-01-01', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP, 2, 0
    FROM equipment e WHERE e.equipment_code = 'AGV-001'
    ON CONFLICT (agv_id) DO NOTHING;
    
    INSERT INTO agv_status (agv_id, battery_level, agv_status, current_location, last_charge_time, last_update_time, x_position, y_position)
    SELECT e.id, 60.00, 'working', 'A-02-01', CURRENT_TIMESTAMP - INTERVAL '5 hours', CURRENT_TIMESTAMP, 4, 0
    FROM equipment e WHERE e.equipment_code = 'AGV-002'
    ON CONFLICT (agv_id) DO NOTHING;
    
    INSERT INTO agv_status (agv_id, battery_level, agv_status, current_location, last_charge_time, last_update_time, x_position, y_position, charging_station_id)
    SELECT e.id, 95.00, 'charging', 'CS-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 0, charging_station_1
    FROM equipment e WHERE e.equipment_code = 'AGV-003'
    ON CONFLICT (agv_id) DO NOTHING;

    INSERT INTO agv_status (agv_id, battery_level, agv_status, current_location, last_charge_time, last_update_time, x_position, y_position)
    SELECT e.id, 45.00, 'returning', 'CS-002', CURRENT_TIMESTAMP - INTERVAL '8 hours', CURRENT_TIMESTAMP, 20, 0
    FROM equipment e WHERE e.equipment_code = 'AGV-004'
    ON CONFLICT (agv_id) DO NOTHING;

    INSERT INTO agv_status (agv_id, battery_level, agv_status, current_location, last_charge_time, last_update_time, x_position, y_position)
    SELECT e.id, 30.00, 'fault', 'A-03-01', CURRENT_TIMESTAMP - INTERVAL '1 hours', CURRENT_TIMESTAMP, 6, 0
    FROM equipment e WHERE e.equipment_code = 'AGV-005'
    ON CONFLICT (agv_id) DO NOTHING;
END $$;

-- ============================================================
-- 七、初始化零件数据
-- ============================================================

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
-- 八、初始化电池测试数据
-- ============================================================

INSERT INTO battery_basic_info (
    id, battery_code, battery_name, battery_type, battery_model, 
    manufacturer, purchase_date, warranty_expire_date, rated_capacity, 
    rated_voltage, total_charge_times, total_usage_hours, health_status, 
    status, location, remark, create_time, update_time, deleted
) VALUES 
(1, 'BAT-001', '磷酸铁锂电池A1', '磷酸铁锂', 'LFP-48V-100Ah', 
    '宁德时代', '2024-01-15', '2027-01-15', 100.00, 
    48.00, 50, 500.00, 95, 
    1, 'A-01-001', '测试电池1', now(), now(), 0),

(2, 'BAT-002', '磷酸铁锂电池A2', '磷酸铁锂', 'LFP-48V-100Ah', 
    '宁德时代', '2024-01-15', '2027-01-15', 100.00, 
    48.00, 45, 450.00, 90, 
    1, 'A-01-002', '测试电池2', now(), now(), 0),

(3, 'BAT-003', '三元锂电池B1', '三元锂', 'NCM-48V-80Ah', 
    '比亚迪', '2024-02-20', '2027-02-20', 80.00, 
    48.00, 30, 300.00, 85, 
    2, 'B-02-003', '测试电池3-使用中', now(), now(), 0),

(4, 'BAT-004', '三元锂电池B2', '三元锂', 'NCM-48V-80Ah', 
    '比亚迪', '2024-02-20', '2027-02-20', 80.00, 
    48.00, 25, 250.00, 80, 
    3, 'CS-001', '测试电池4-充电中', now(), now(), 0),

(5, 'BAT-005', '铅酸电池C1', '铅酸', 'LA-24V-150Ah', 
    '超威', '2023-06-10', '2026-06-10', 150.00, 
    24.00, 100, 1000.00, 60, 
    4, 'A-03-005', '测试电池5-故障', now(), now(), 0),

(6, 'BAT-006', '磷酸铁锂电池A3', '磷酸铁锂', 'LFP-48V-100Ah', 
    '宁德时代', '2024-03-01', '2027-03-01', 100.00, 
    48.00, 20, 200.00, 92, 
    1, 'A-02-006', '测试电池6', now(), now(), 0),

(7, 'BAT-007', '磷酸铁锂电池A4', '磷酸铁锂', 'LFP-48V-100Ah', 
    '宁德时代', '2024-03-01', '2027-03-01', 100.00, 
    48.00, 18, 180.00, 88, 
    1, 'A-02-007', '测试电池7', now(), now(), 0),

(8, 'BAT-008', '三元锂电池B3', '三元锂', 'NCM-48V-80Ah', 
    '比亚迪', '2024-04-15', '2027-04-15', 80.00, 
    48.00, 15, 150.00, 82, 
    2, 'B-01-008', '测试电池8-使用中', now(), now(), 0)
ON CONFLICT (battery_code) DO NOTHING;

-- ============================================================
-- 第四部分：数据修复脚本
-- ============================================================

-- 修复 sys_user 表的 deleted 字段（如果不存在）
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS deleted SMALLINT DEFAULT 0;

-- 修复 sys_role 表的 deleted 字段（如果不存在）
ALTER TABLE sys_role ADD COLUMN IF NOT EXISTS deleted SMALLINT DEFAULT 0;

-- ============================================================
-- 第五部分：坐标更新脚本
-- ============================================================

-- 更新AGV小车绝对直角坐标
-- 小车1: AGV-001 位置 A-01-004 -> x=0, y=4.5
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

-- 更新所有A区货位坐标
UPDATE agv_status 
SET 
    x_position = 0 + (SUBSTRING(current_location FROM 4 FOR 2)::INT - 1),
    y_position = (SUBSTRING(current_location FROM 7 FOR 3)::INT - 1) * 1.5
WHERE current_location LIKE 'A-%'
AND LENGTH(current_location) >= 9;

-- 更新所有B区货位坐标
UPDATE agv_status 
SET 
    x_position = 10 + (SUBSTRING(current_location FROM 4 FOR 2)::INT - 1),
    y_position = (SUBSTRING(current_location FROM 7 FOR 3)::INT - 1) * 1.5
WHERE current_location LIKE 'B-%'
AND LENGTH(current_location) >= 9;

-- 更新所有C区货位坐标
UPDATE agv_status 
SET 
    x_position = 20 + (SUBSTRING(current_location FROM 4 FOR 2)::INT - 1),
    y_position = (SUBSTRING(current_location FROM 7 FOR 3)::INT - 1) * 1.5
WHERE current_location LIKE 'C-%'
AND LENGTH(current_location) >= 9;

-- 更新充电站位置
UPDATE agv_status SET x_position = 0, y_position = 0 WHERE current_location LIKE 'CS-001%';
UPDATE agv_status SET x_position = 20, y_position = 0 WHERE current_location LIKE 'CS-002%';

-- ============================================================
-- 完成
-- ============================================================
SELECT '数据库初始化完成！' AS message;
