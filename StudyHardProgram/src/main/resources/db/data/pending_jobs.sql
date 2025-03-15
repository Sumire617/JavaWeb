-- 先查询系统中所有雇主用户的ID
-- 注意: 在执行此脚本前，请确保用户表中已有type为EMPLOYER的用户记录
-- 如果没有，则先创建雇主用户

-- 获取一个雇主ID作为变量（如果在你的环境中不支持变量，请手动替换为实际值）
SET @employer_id1 = (SELECT user_id FROM users WHERE user_type = 'EMPLOYER' LIMIT 1 OFFSET 0);
SET @employer_id2 = (SELECT user_id FROM users WHERE user_type = 'EMPLOYER' LIMIT 1 OFFSET 1);
SET @employer_id3 = (SELECT user_id FROM users WHERE user_type = 'EMPLOYER' LIMIT 1 OFFSET 2);

-- 检查是否获取到雇主ID
SELECT @employer_id1 AS 'Employer ID 1';
SELECT @employer_id2 AS 'Employer ID 2';
SELECT @employer_id3 AS 'Employer ID 3';

-- 如果没有足够的雇主用户，先插入一些雇主用户
INSERT IGNORE INTO users (user_id, username, password, email, user_type, created_at, updated_at, status)
VALUES 
(UUID(), 'employer1', '$2a$10$dNuAxq7xKV/BX4rTCK0N6.aPlTYwPRlyGfDRXGkGy4XcNNP7G2J0e', 'employer1@example.com', 'EMPLOYER', NOW(), NOW(), 'ACTIVE'),
(UUID(), 'employer2', '$2a$10$dNuAxq7xKV/BX4rTCK0N6.aPlTYwPRlyGfDRXGkGy4XcNNP7G2J0e', 'employer2@example.com', 'EMPLOYER', NOW(), NOW(), 'ACTIVE'),
(UUID(), 'employer3', '$2a$10$dNuAxq7xKV/BX4rTCK0N6.aPlTYwPRlyGfDRXGkGy4XcNNP7G2J0e', 'employer3@example.com', 'EMPLOYER', NOW(), NOW(), 'ACTIVE');

-- 再次获取雇主ID
SET @employer_id1 = (SELECT user_id FROM users WHERE user_type = 'EMPLOYER' LIMIT 1 OFFSET 0);
SET @employer_id2 = (SELECT user_id FROM users WHERE user_type = 'EMPLOYER' LIMIT 1 OFFSET 1);
SET @employer_id3 = (SELECT user_id FROM users WHERE user_type = 'EMPLOYER' LIMIT 1 OFFSET 2);

-- 插入待审核的岗位数据
INSERT INTO job_posts (job_post_id, employer_id, job_title, job_description, requirements, salary_range, location, status, posted_at, updated_at)
VALUES 
(UUID(), @employer_id1, '校内实验室助理', '协助教授和研究生开展实验室研究工作，包括设备维护、实验准备和数据记录等。', '理工科背景，有基本实验室操作经验，认真负责，具有良好的团队合作精神。', '150-200元/天', '本部校区', 'PENDING', NOW(), NOW()),

(UUID(), @employer_id1, '图书馆管理员助理', '负责图书分类、上架、借还管理，以及图书馆环境维护等工作。', '任何专业背景，热爱阅读，熟悉图书分类系统，工作细心有耐心。', '20-30元/小时', '图书馆', 'PENDING', NOW(), NOW()),

(UUID(), @employer_id2, '学院行政助理', '协助学院办公室处理日常行政事务，包括文档整理、会议安排、接待访客等。', '文科或管理类专业优先，具有良好的沟通能力和办公软件使用技能。', '3000-3500元/月', '文学院', 'PENDING', NOW(), NOW()),

(UUID(), @employer_id2, '计算机实验室技术支持', '维护计算机实验室设备，解决学生使用问题，协助教师准备教学软件。', '计算机相关专业，熟悉常见计算机硬件和软件，有一定的问题排查能力。', '25-35元/小时', '信息学院', 'PENDING', NOW(), NOW()),

(UUID(), @employer_id3, '校园网络管理助手', '协助网络中心维护校园网络，监控网络运行状态，处理基本网络故障。', '信息技术或计算机相关专业，了解基本网络知识，认真负责。', '4000-4500元/月', '网络中心', 'PENDING', NOW(), NOW()),

(UUID(), @employer_id3, '创新创业中心助理', '支持创新创业中心日常运营，包括活动组织、项目跟踪、资料整理等。', '创新创业相关经验优先，有较强的组织协调能力，善于沟通。', '180-220元/天', '创新园区', 'PENDING', NOW(), NOW()),

(UUID(), @employer_id1, '心理咨询中心接待员', '负责心理咨询中心前台接待、预约安排、基本信息收集等工作。', '心理学相关专业优先，有较强的保密意识和同理心，沟通能力强。', '22-28元/小时', '学生服务中心', 'PENDING', NOW(), NOW()),

(UUID(), @employer_id2, '校园媒体内容助理', '协助校媒体中心创作文字、图片、视频等内容，参与校园活动报道。', '新闻传播或媒体相关专业，有内容创作经验，熟悉媒体工具使用。', '3500-4000元/月', '传媒学院', 'PENDING', NOW(), NOW()),

(UUID(), @employer_id3, '国际交流办公室助理', '协助接待国际访问学者和学生，翻译文件，组织国际文化活动等。', '英语或其他外语能力优秀，具备跨文化交流经验，形象气质佳。', '160-200元/天', '国际交流中心', 'PENDING', NOW(), NOW()),

(UUID(), @employer_id1, '校史馆讲解员', '负责校史馆参观讲解，资料整理，展览布置等工作。', '对学校历史有浓厚兴趣，表达能力强，形象气质佳，责任心强。', '18-25元/小时', '校史馆', 'PENDING', NOW(), NOW());

-- 提示信息
SELECT 'Successfully inserted pending job posts!' AS 'Message'; 