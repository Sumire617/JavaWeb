-- 创建员工（单位人员）表
CREATE TABLE IF NOT EXISTS `staff` (
  `id` VARCHAR(36) NOT NULL PRIMARY KEY,
  `company_id` VARCHAR(36) NOT NULL,
  `name` VARCHAR(50) NOT NULL COMMENT '员工姓名',
  `employee_id` VARCHAR(50) COMMENT '工号',
  `department` VARCHAR(50) COMMENT '部门',
  `position` VARCHAR(50) COMMENT '职位',
  `phone` VARCHAR(20) COMMENT '手机号',
  `email` VARCHAR(100) COMMENT '邮箱',
  `status` VARCHAR(20) DEFAULT '在职' COMMENT '状态: 在职/离职/实习',
  `join_date` DATE COMMENT '入职日期',
  `role` VARCHAR(20) DEFAULT 'STAFF' COMMENT '角色: STAFF/ADMIN',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`company_id`) REFERENCES `companies`(`company_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工（单位人员）表'; 