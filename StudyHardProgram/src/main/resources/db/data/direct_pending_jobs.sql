-- 直接执行版：创建雇主用户和待审核岗位的标准SQL
-- 这个版本使用固定UUID和标准SQL语法，可以在大多数数据库环境中直接执行

-- 创建雇主用户（如果存在则忽略）
INSERT INTO users (user_id, username, password, email, user_type, created_at, updated_at, status)
SELECT 'e12345a1-b234-c345-d456-e56789abcdef', 'employer_test1', '$2a$10$dNuAxq7xKV/BX4rTCK0N6.aPlTYwPRlyGfDRXGkGy4XcNNP7G2J0e', 'employer_test1@example.com', 'EMPLOYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_id = 'e12345a1-b234-c345-d456-e56789abcdef');

-- 创建待审核岗位
INSERT INTO job_posts (job_post_id, employer_id, job_title, job_description, requirements, salary_range, location, status, posted_at, updated_at)
VALUES 
('j12345a1-b234-c345-d456-e56789abcdef', 'e12345a1-b234-c345-d456-e56789abcdef', '校内实验室助理', '协助教授和研究生开展实验室研究工作，包括设备维护、实验准备和数据记录等。', '理工科背景，有基本实验室操作经验，认真负责，具有良好的团队合作精神。', '150-200元/天', '本部校区', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('j23456b1-c345-d456-e567-f67890bcdefg', 'e12345a1-b234-c345-d456-e56789abcdef', '图书馆管理员助理', '负责图书分类、上架、借还管理，以及图书馆环境维护等工作。', '任何专业背景，热爱阅读，熟悉图书分类系统，工作细心有耐心。', '20-30元/小时', '图书馆', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('j34567c1-d456-e567-f678-g78901cdefgh', 'e12345a1-b234-c345-d456-e56789abcdef', '学院行政助理', '协助学院办公室处理日常行政事务，包括文档整理、会议安排、接待访客等。', '文科或管理类专业优先，具有良好的沟通能力和办公软件使用技能。', '3000-3500元/月', '文学院', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('j45678d1-e567-f678-g789-h89012defghi', 'e12345a1-b234-c345-d456-e56789abcdef', '计算机实验室技术支持', '维护计算机实验室设备，解决学生使用问题，协助教师准备教学软件。', '计算机相关专业，熟悉常见计算机硬件和软件，有一定的问题排查能力。', '25-35元/小时', '信息学院', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 查询待审核岗位，确认是否添加成功
SELECT * FROM job_posts WHERE status = 'PENDING'; 