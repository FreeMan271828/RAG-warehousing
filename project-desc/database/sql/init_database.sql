-- ============================================================
-- 延安烟厂三维数字孪生系统 - 数据库初始化脚本
-- 数据库: PostgreSQL
-- 版本: 1.1
-- 创建日期: 2025-01
-- 更新日期: 2026-02-20 (添加缺失字段修复)
-- ============================================================

-- 创建数据库
DROP DATABASE IF EXISTS tobacco_warehouse_db;
CREATE DATABASE tobacco_warehouse_db WITH ENCODING 'UTF8';

\c tobacco_warehouse_db;

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
-- 初始化默认管理员账号 (密码: admin123)
-- 使用 PostgreSQL 的 md5 加密或 bcrypt
-- ============================================================
INSERT INTO sys_user (username, password, real_name, is_admin, status) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 1, 1);

-- 初始化默认角色
INSERT INTO sys_role (role_code, role_name, role_type, description, status) 
VALUES ('admin', '超级管理员', 'system', '系统超级管理员，拥有所有权限', 1),
       ('user', '普通用户', 'user', '普通用户，基本操作权限', 1);

-- 关联管理员角色
INSERT INTO sys_user_role (user_id, role_id) 
SELECT u.id, r.id FROM sys_user u, sys_role r WHERE u.username = 'admin' AND r.role_code = 'admin';

-- ============================================================
-- 初始化默认部门
-- ============================================================
INSERT INTO sys_dept (dept_name, parent_id, sort_order, status) 
VALUES ('延安烟厂', 0, 1, 1),
       ('设备管理部', 1, 1, 1),
       ('运维部', 1, 2, 1),
       ('仓储部', 1, 3, 1);

-- ============================================================
-- 初始化默认设备类别
-- ============================================================
INSERT INTO device_category (category_code, category_name, parent_id, sort_order, status) 
VALUES ('EQUIPMENT', '设备', 0, 1, 1),
       ('BATTERY', '电池', 0, 2, 1),
       ('SENSOR', '传感器', 0, 3, 1),
       ('CONTROL', '控制柜', 0, 4, 1);

-- ============================================================
-- 完成
-- ============================================================
SELECT '数据库初始化完成！' AS message;
