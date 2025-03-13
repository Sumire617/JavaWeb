-- 为用户添加岗位评价记录

-- 评价ID使用UUID
-- job_evaluations表的结构：
-- evaluation_id: 主键
-- job_post_id: 外键，引用job_posts表的job_post_id
-- user_id: 外键，引用users表的user_id
-- rating: 评分，整数，1-5
-- content: 评价内容
-- evaluation_time: 评价时间
-- is_anonymous: 是否匿名，布尔值

-- 插入评价记录（假设我们有一些岗位ID和用户ID）
INSERT INTO job_evaluations (evaluation_id, job_post_id, user_id, rating, content, evaluation_time, is_anonymous)
VALUES 
-- 用户1对岗位1的评价
(UUID(), '需要替换为实际岗位ID1', '需要替换为实际用户ID1', 5, '这是一个很好的兼职机会，工作环境很好，老师也很负责。', NOW(), FALSE),
-- 用户2对岗位1的评价
(UUID(), '需要替换为实际岗位ID1', '需要替换为实际用户ID2', 4, '工作内容比较简单，但是时间安排有点不灵活。', NOW(), FALSE),
-- 用户3对岗位1的评价
(UUID(), '需要替换为实际岗位ID1', '需要替换为实际用户ID3', 5, '很好的锻炼机会，能够提高自己的能力。', NOW(), FALSE),
-- 用户1对岗位2的评价
(UUID(), '需要替换为实际岗位ID2', '需要替换为实际用户ID1', 4, '工资待遇不错，但是工作量有点大。', NOW(), FALSE),
-- 用户2对岗位2的评价
(UUID(), '需要替换为实际岗位ID2', '需要替换为实际用户ID2', 3, '一般般吧，工作内容比较单调。', NOW(), FALSE);

-- 注意：运行此脚本前，请确保替换实际的岗位ID和用户ID
-- 你可以先执行以下查询来获取实际的岗位ID和用户ID：
-- SELECT job_post_id FROM job_posts;
-- SELECT user_id FROM users; 