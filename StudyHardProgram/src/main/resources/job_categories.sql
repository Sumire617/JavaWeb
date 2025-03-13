-- 简化版SQL脚本 - 添加job_type列和示例数据

-- 1. 添加job_type列
ALTER TABLE job_posts ADD COLUMN job_type VARCHAR(50);

-- 2. 更新已有记录为'other'
UPDATE job_posts SET job_type = 'other' WHERE job_type IS NULL;

-- 获取一个用户ID作为示例数据
SET @employer_id = (SELECT user_id FROM users LIMIT 1);

-- 插入校内岗位示例
INSERT INTO job_posts 
(job_post_id, employer_id, job_title, job_description, requirements, salary_range, location, job_type, status, posted_at, updated_at)
VALUES 
(UUID(), @employer_id, '食堂勤工俭学', '负责食堂餐具回收和清洁工作', '有责任心，能吃苦耐劳', '18-25', '本部校区', 'campus', 'APPROVED', NOW(), NOW());

-- 插入助教辅导示例
INSERT INTO job_posts 
(job_post_id, employer_id, job_title, job_description, requirements, salary_range, location, job_type, status, posted_at, updated_at)
VALUES 
(UUID(), @employer_id, '高等数学助教', '协助老师批改作业、解答学生问题', '数学成绩优秀，有耐心', '30-40', '理学院', 'teaching', 'APPROVED', NOW(), NOW());

-- 插入行政助理示例
INSERT INTO job_posts 
(job_post_id, employer_id, job_title, job_description, requirements, salary_range, location, job_type, status, posted_at, updated_at)
VALUES 
(UUID(), @employer_id, '学院办公室助理', '协助学院日常行政工作', '工作认真细致，沟通能力强', '22-28', '人文学院', 'admin', 'APPROVED', NOW(), NOW());

-- 插入图书馆岗位示例 
INSERT INTO job_posts 
(job_post_id, employer_id, job_title, job_description, requirements, salary_range, location, job_type, status, posted_at, updated_at)
VALUES 
(UUID(), @employer_id, '图书馆管理员', '负责图书整理与借阅管理', '熟悉图书分类，做事细心', '20-30', '中央图书馆', 'library', 'APPROVED', NOW(), NOW());

-- 插入科研助理示例
INSERT INTO job_posts 
(job_post_id, employer_id, job_title, job_description, requirements, salary_range, location, job_type, status, posted_at, updated_at)
VALUES 
(UUID(), @employer_id, '实验室助理', '协助教授进行科研实验', '相关专业背景，动手能力强', '30-45', '科学楼', 'research', 'APPROVED', NOW(), NOW());

-- 插入信息技术示例
INSERT INTO job_posts 
(job_post_id, employer_id, job_title, job_description, requirements, salary_range, location, job_type, status, posted_at, updated_at)
VALUES 
(UUID(), @employer_id, '网络维护助理', '负责校园网络设备维护', '计算机专业，了解网络知识', '25-35', '信息中心', 'it', 'APPROVED', NOW(), NOW());

-- 插入校园服务示例
INSERT INTO job_posts 
(job_post_id, employer_id, job_title, job_description, requirements, salary_range, location, job_type, status, posted_at, updated_at)
VALUES 
(UUID(), @employer_id, '校园导游', '负责校园参观讲解', '熟悉校园历史，普通话标准', '20-25', '校史馆', 'service', 'APPROVED', NOW(), NOW());

-- 插入其他类型示例
INSERT INTO job_posts 
(job_post_id, employer_id, job_title, job_description, requirements, salary_range, location, job_type, status, posted_at, updated_at)
VALUES 
(UUID(), @employer_id, '校园记者', '采访校园活动并撰写报道', '文字功底好，摄影能力佳', '20-30', '校媒中心', 'other', 'APPROVED', NOW(), NOW()); 