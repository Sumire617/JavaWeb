-- 初始化员工数据
-- 注意：在执行此脚本前，请确保已创建companies表和至少一条公司记录

-- 获取第一个公司ID
SET @company_id = (SELECT company_id FROM companies LIMIT 1);

-- 如果没有公司记录，则先创建一个
INSERT IGNORE INTO companies (company_id, employer_id, name, industry, size, location, description, contact_name, contact_phone, contact_email)
SELECT 
    UUID(), 
    (SELECT user_id FROM users WHERE user_type = 'EMPLOYER' LIMIT 1), 
    '测试公司', 
    '互联网/IT', 
    '20-99人', 
    '北京市海淀区', 
    '这是一个测试公司的描述', 
    '张三', 
    '13800000000', 
    'test@example.com'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM companies LIMIT 1);

-- 重新获取公司ID
SET @company_id = (SELECT company_id FROM companies LIMIT 1);

-- 插入测试员工数据
INSERT INTO staff (id, company_id, name, employee_id, department, position, phone, email, status, join_date, role)
VALUES
(UUID(), @company_id, '张三', 'EMP001', '研发部', '前端工程师', '13900000001', 'zhangsan@example.com', '在职', '2022-01-10', 'STAFF'),
(UUID(), @company_id, '李四', 'EMP002', '研发部', '后端工程师', '13900000002', 'lisi@example.com', '在职', '2021-06-15', 'STAFF'),
(UUID(), @company_id, '王五', 'EMP003', '市场部', '市场经理', '13900000003', 'wangwu@example.com', '在职', '2020-09-01', 'ADMIN'),
(UUID(), @company_id, '赵六', 'EMP004', '财务部', '财务主管', '13900000004', 'zhaoliu@example.com', '在职', '2023-02-20', 'STAFF'),
(UUID(), @company_id, '刘七', 'EMP005', '人力资源部', 'HR专员', '13900000005', 'liuqi@example.com', '在职', '2022-11-05', 'STAFF'),
(UUID(), @company_id, '陈八', 'EMP006', '研发部', '测试工程师', '13900000006', 'chenba@example.com', '实习', '2023-07-01', 'STAFF'),
(UUID(), @company_id, '钱九', 'EMP007', '销售部', '销售经理', '13900000007', 'qianjiu@example.com', '在职', '2021-03-15', 'ADMIN'),
(UUID(), @company_id, '孙十', 'EMP008', '客服部', '客服专员', '13900000008', 'sunshi@example.com', '离职', '2020-05-10', 'STAFF'),
(UUID(), @company_id, '周一', 'EMP009', '产品部', '产品经理', '13900000009', 'zhouyi@example.com', '在职', '2022-08-12', 'STAFF'),
(UUID(), @company_id, '吴二', 'EMP010', '技术部', '架构师', '13900000010', 'wuer@example.com', '在职', '2019-12-01', 'ADMIN');

-- 确认插入成功
SELECT '员工数据初始化完成！' as 'Message'; 