-- 简化版：创建待审核岗位数据
-- 注意：这里使用的是固定UUID，请先确认您的数据库中存在这些雇主ID
-- 如果不存在，请先修改为实际存在的雇主ID，或先执行下面的雇主创建语句

-- 创建三个雇主用户（如果已有雇主用户可以跳过此步骤）
INSERT IGNORE INTO users (user_id, username, password, email, user_type, created_at, updated_at, status)
VALUES 
('e1a2b3c4-d5e6-f7a8-b9c0-d1e2f3a4b5c6', 'employer1', '$2a$10$dNuAxq7xKV/BX4rTCK0N6.aPlTYwPRlyGfDRXGkGy4XcNNP7G2J0e', 'employer1@example.com', 'EMPLOYER', NOW(), NOW(), 'ACTIVE'),
('a1b2c3d4-e5f6-a7b8-c9d0-e1f2a3b4c5d6', 'employer2', '$2a$10$dNuAxq7xKV/BX4rTCK0N6.aPlTYwPRlyGfDRXGkGy4XcNNP7G2J0e', 'employer2@example.com', 'EMPLOYER', NOW(), NOW(), 'ACTIVE'),
('b1c2d3e4-f5a6-b7c8-d9e0-f1a2b3c4d5e6', 'employer3', '$2a$10$dNuAxq7xKV/BX4rTCK0N6.aPlTYwPRlyGfDRXGkGy4XcNNP7G2J0e', 'employer3@example.com', 'EMPLOYER', NOW(), NOW(), 'ACTIVE');

-- 使用固定的三个雇主ID创建待审核岗位
INSERT INTO job_posts (job_post_id, employer_id, job_title, job_description, requirements, salary_range, location, status, posted_at, updated_at)
VALUES 
(UUID(), 'e1a2b3c4-d5e6-f7a8-b9c0-d1e2f3a4b5c6', '校内实验室助理', '协助教授和研究生开展实验室研究工作，包括设备维护、实验准备和数据记录等。', '理工科背景，有基本实验室操作经验，认真负责，具有良好的团队合作精神。', '150-200元/天', '本部校区', 'PENDING', NOW(), NOW()),

(UUID(), 'e1a2b3c4-d5e6-f7a8-b9c0-d1e2f3a4b5c6', '图书馆管理员助理', '负责图书分类、上架、借还管理，以及图书馆环境维护等工作。', '任何专业背景，热爱阅读，熟悉图书分类系统，工作细心有耐心。', '20-30元/小时', '图书馆', 'PENDING', NOW(), NOW()),

(UUID(), 'a1b2c3d4-e5f6-a7b8-c9d0-e1f2a3b4c5d6', '学院行政助理', '协助学院办公室处理日常行政事务，包括文档整理、会议安排、接待访客等。', '文科或管理类专业优先，具有良好的沟通能力和办公软件使用技能。', '3000-3500元/月', '文学院', 'PENDING', NOW(), NOW()),

(UUID(), 'a1b2c3d4-e5f6-a7b8-c9d0-e1f2a3b4c5d6', '计算机实验室技术支持', '维护计算机实验室设备，解决学生使用问题，协助教师准备教学软件。', '计算机相关专业，熟悉常见计算机硬件和软件，有一定的问题排查能力。', '25-35元/小时', '信息学院', 'PENDING', NOW(), NOW()),

(UUID(), 'b1c2d3e4-f5a6-b7c8-d9e0-f1a2b3c4d5e6', '校园网络管理助手', '协助网络中心维护校园网络，监控网络运行状态，处理基本网络故障。', '信息技术或计算机相关专业，了解基本网络知识，认真负责。', '4000-4500元/月', '网络中心', 'PENDING', NOW(), NOW()),

(UUID(), 'b1c2d3e4-f5a6-b7c8-d9e0-f1a2b3c4d5e6', '创新创业中心助理', '支持创新创业中心日常运营，包括活动组织、项目跟踪、资料整理等。', '创新创业相关经验优先，有较强的组织协调能力，善于沟通。', '180-220元/天', '创新园区', 'PENDING', NOW(), NOW());

-- 提示信息
SELECT 'Successfully inserted pending job posts!' AS 'Message'; 