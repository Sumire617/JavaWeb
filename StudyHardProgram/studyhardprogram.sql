-- 为用户添加岗位评价记录
-- 使用实际的UUID

-- 首先确保job_evaluations表存在
CREATE TABLE IF NOT EXISTS job_evaluations (
    evaluation_id VARCHAR(36) NOT NULL PRIMARY KEY,
    job_post_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    rating INT NOT NULL,
    content TEXT NOT NULL,
    evaluation_time TIMESTAMP NOT NULL,
    is_anonymous BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (job_post_id) REFERENCES job_posts(job_post_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 插入评价记录
-- 注意：以下UUID是生成的，job_post_id和user_id需要替换为实际存在的ID
INSERT INTO job_evaluations (evaluation_id, job_post_id, user_id, rating, content, evaluation_time, is_anonymous)
VALUES 
-- 第一批评价
('e51f5204-c37a-11ee-a506-0242ac120002', 
 (SELECT job_post_id FROM job_posts ORDER BY posted_at DESC LIMIT 1), 
 (SELECT user_id FROM users WHERE username = 'student1' LIMIT 1), 
 5, '这是一个很好的兼职机会，工作环境很好，老师也很负责。', NOW(), FALSE),

-- 第二批评价
('e51f5452-c37a-11ee-a506-0242ac120002', 
 (SELECT job_post_id FROM job_posts ORDER BY posted_at DESC LIMIT 1), 
 (SELECT user_id FROM users WHERE username = 'student2' LIMIT 1), 
 4, '工作内容比较简单，但是时间安排有点不灵活。', NOW(), FALSE),

-- 第三批评价
('e51f556a-c37a-11ee-a506-0242ac120002', 
 (SELECT job_post_id FROM job_posts ORDER BY posted_at DESC LIMIT 1), 
 (SELECT user_id FROM users WHERE username = 'student3' LIMIT 1), 
 5, '很好的锻炼机会，能够提高自己的能力。', NOW(), FALSE),

-- 第四批评价
('e51f5682-c37a-11ee-a506-0242ac120002', 
 (SELECT job_post_id FROM job_posts ORDER BY posted_at DESC LIMIT 1 OFFSET 1), 
 (SELECT user_id FROM users WHERE username = 'student1' LIMIT 1), 
 4, '工资待遇不错，但是工作量有点大。', NOW(), FALSE),

-- 第五批评价
('e51f579a-c37a-11ee-a506-0242ac120002', 
 (SELECT job_post_id FROM job_posts ORDER BY posted_at DESC LIMIT 1 OFFSET 1), 
 (SELECT user_id FROM users WHERE username = 'student2' LIMIT 1), 
 3, '一般般吧，工作内容比较单调。', NOW(), FALSE);

-- 如果没有student1、student2、student3等用户，可以先创建这些用户
INSERT IGNORE INTO users (user_id, username, password, email, phone_number, user_type, created_at, updated_at)
VALUES
('f8e7d6c5-b4a3-2c1d-0e9f-8g7h6i5j4k3l', 'student1', 'password123', 'student1@example.com', '13800000001', 'USER', NOW(), NOW()),
('f8e7d6c5-b4a3-2c1d-0e9f-8g7h6i5j4k3m', 'student2', 'password123', 'student2@example.com', '13800000002', 'USER', NOW(), NOW()),
('f8e7d6c5-b4a3-2c1d-0e9f-8g7h6i5j4k3n', 'student3', 'password123', 'student3@example.com', '13800000003', 'USER', NOW(), NOW());

-- 如果没有岗位，可以先创建一些岗位
INSERT IGNORE INTO job_posts (job_post_id, employer_id, job_title, job_description, requirements, salary_range, location, status, posted_at, updated_at)
VALUES
('a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6', 
 (SELECT user_id FROM users WHERE user_type = 'EMPLOYER' LIMIT 1), 
 '图书馆助理', '负责图书整理、借还登记等工作', '熟悉图书分类，工作认真负责', '20-30', '本部校区', 'APPROVED', NOW(), NOW()),
 
('b2c3d4e5-f6g7-8h9i-0j1k-l2m3n4o5p6q7', 
 (SELECT user_id FROM users WHERE user_type = 'EMPLOYER' LIMIT 1), 
 '行政助理', '协助办公室日常行政工作', '熟悉办公软件，有责任心', '25-35', '医学部', 'APPROVED', NOW(), NOW()); 