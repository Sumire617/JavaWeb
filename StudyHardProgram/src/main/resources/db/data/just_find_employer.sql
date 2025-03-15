-- 最简单版：查询现有雇主ID并使用它们添加岗位

-- 先查询并打印所有雇主用户ID，查看是否有雇主用户
SELECT user_id, username, email FROM users WHERE user_type = 'EMPLOYER';

-- 如果没有雇主用户，则先添加雇主用户
INSERT IGNORE INTO users (user_id, username, password, email, user_type, created_at, updated_at, status)
VALUES 
(UUID(), 'employer1', '$2a$10$dNuAxq7xKV/BX4rTCK0N6.aPlTYwPRlyGfDRXGkGy4XcNNP7G2J0e', 'employer1@example.com', 'EMPLOYER', NOW(), NOW(), 'ACTIVE'),
(UUID(), 'employer2', '$2a$10$dNuAxq7xKV/BX4rTCK0N6.aPlTYwPRlyGfDRXGkGy4XcNNP7G2J0e', 'employer2@example.com', 'EMPLOYER', NOW(), NOW(), 'ACTIVE');

-- 再次查询雇主ID
SELECT user_id, username, email FROM users WHERE user_type = 'EMPLOYER';

-- 【重要】: 执行完上面的查询后，请从结果中复制一个实际存在的雇主ID，
-- 替换下面INSERT语句中的"{实际雇主ID}"部分，然后再执行下面的INSERT语句

-- 使用实际存在的雇主ID创建待审核岗位
INSERT INTO job_posts (job_post_id, employer_id, job_title, job_description, requirements, salary_range, location, status, posted_at, updated_at)
VALUES 
(UUID(), '{实际雇主ID}', '校内实验室助理', '协助教授和研究生开展实验室研究工作，包括设备维护、实验准备和数据记录等。', '理工科背景，有基本实验室操作经验，认真负责，具有良好的团队合作精神。', '150-200元/天', '本部校区', 'PENDING', NOW(), NOW()),

(UUID(), '{实际雇主ID}', '图书馆管理员助理', '负责图书分类、上架、借还管理，以及图书馆环境维护等工作。', '任何专业背景，热爱阅读，熟悉图书分类系统，工作细心有耐心。', '20-30元/小时', '图书馆', 'PENDING', NOW(), NOW()),

(UUID(), '{实际雇主ID}', '学院行政助理', '协助学院办公室处理日常行政事务，包括文档整理、会议安排、接待访客等。', '文科或管理类专业优先，具有良好的沟通能力和办公软件使用技能。', '3000-3500元/月', '文学院', 'PENDING', NOW(), NOW()),

(UUID(), '{实际雇主ID}', '计算机实验室技术支持', '维护计算机实验室设备，解决学生使用问题，协助教师准备教学软件。', '计算机相关专业，熟悉常见计算机硬件和软件，有一定的问题排查能力。', '25-35元/小时', '信息学院', 'PENDING', NOW(), NOW());

-- 查询是否成功添加
SELECT * FROM job_posts WHERE status = 'PENDING'; 