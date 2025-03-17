/*
 Navicat Premium Data Transfer

 Source Server         : PythonProject
 Source Server Type    : MySQL
 Source Server Version : 80029
 Source Host           : localhost:3306
 Source Schema         : studyhardprogram

 Target Server Type    : MySQL
 Target Server Version : 80029
 File Encoding         : 65001

 Date: 18/03/2025 00:53:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for companies
-- ----------------------------
DROP TABLE IF EXISTS `companies`;
CREATE TABLE `companies`  (
  `company_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `contact_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `description` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `employer_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `industry` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `size` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`company_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of companies
-- ----------------------------
INSERT INTO `companies` VALUES ('f5e16529-5c6d-44e0-8f98-924031bdc822', '16@qq.com', '111', '15098001588', '2025-03-17 06:51:00.785345', '2452542452542542', 'ea2ec884-e7d0-42c0-9cfb-04b07542318d', '互联网/IT', '2542452452542452', '2542452542', '0-20人', '2025-03-17 06:51:00.785345');

-- ----------------------------
-- Table structure for job_applications
-- ----------------------------
DROP TABLE IF EXISTS `job_applications`;
CREATE TABLE `job_applications`  (
  `application_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `job_post_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `introduction` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `resume` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `apply_time` timestamp(0) NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING',
  `review_comment` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `review_time` timestamp(0) NULL DEFAULT NULL,
  `reviewer_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`application_id`) USING BTREE,
  INDEX `job_post_id_fk`(`job_post_id`) USING BTREE,
  INDEX `user_id_fk`(`user_id`) USING BTREE,
  CONSTRAINT `job_post_id_fk` FOREIGN KEY (`job_post_id`) REFERENCES `job_posts` (`job_post_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of job_applications
-- ----------------------------
INSERT INTO `job_applications` VALUES ('18f90926-c2c3-4e93-874a-4d69334cc503', '0a1de7dc-0183-11f0-895d-025086b7c084', '45dc1f25-9f0f-42db-b1a8-4b02c692674e', 'qhodg', '2201010618', '18008464985', '166@example.org', '我想干想干我想干想干', NULL, '2025-03-15 10:10:24', 'PENDING', NULL, NULL, NULL);
INSERT INTO `job_applications` VALUES ('app-004', 'd11d1523-2551-4159-b5a7-2bc5edc2865a', '4e46477a-3586-4308-90df-ffd7961782f0', 'Christopher', '2021001004', '+1-941-635-0114', 'uwilliams@example.org', '具有3年工程师经验，希望能够得到这个机会。', 'resumes/christopher-resume.pdf', '2025-03-13 13:20:00', 'APPROVED', '经验丰富，技术能力强。', '2025-03-14 10:00:00', '42f15136-a9db-4fed-bbfd-6b2af59a9cec');

-- ----------------------------
-- Table structure for job_audits
-- ----------------------------
DROP TABLE IF EXISTS `job_audits`;
CREATE TABLE `job_audits`  (
  `audit_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `job_post_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '关联的岗位ID',
  `admin_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_approved` int(0) NULL DEFAULT NULL,
  `audit_comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `audit_time` datetime(0) NOT NULL COMMENT '审核时间',
  `audit_result` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`audit_id`) USING BTREE,
  INDEX `fk_audit_job_post`(`job_post_id`) USING BTREE,
  INDEX `fk_audit_admin`(`admin_id`) USING BTREE,
  CONSTRAINT `fk_audit_admin` FOREIGN KEY (`admin_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_audit_job_post` FOREIGN KEY (`job_post_id`) REFERENCES `job_posts` (`job_post_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of job_audits
-- ----------------------------
INSERT INTO `job_audits` VALUES ('9e907912-9b0f-4792-8dd3-6b41e7d79156', 'd4bbfcef-bf91-40cd-bbdc-9ad179d5df56', '42f15136-a9db-4fed-bbfd-6b2af59a9cec', 1, '', '2025-03-17 08:20:18', '');
INSERT INTO `job_audits` VALUES ('fa484a5b-7b4e-4406-9fa1-0408aa4eceac', 'd11d1523-2551-4159-b5a7-2bc5edc2865a', '42f15136-a9db-4fed-bbfd-6b2af59a9cec', 1, '可以', '2025-03-16 22:22:04', '可以');

-- ----------------------------
-- Table structure for job_evaluations
-- ----------------------------
DROP TABLE IF EXISTS `job_evaluations`;
CREATE TABLE `job_evaluations`  (
  `evaluation_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `job_post_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `rating` int(0) NOT NULL,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `evaluation_time` timestamp(0) NOT NULL,
  `is_anonymous` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`evaluation_id`) USING BTREE,
  INDEX `evaluation_job_post_id_fk`(`job_post_id`) USING BTREE,
  INDEX `evaluation_user_id_fk`(`user_id`) USING BTREE,
  CONSTRAINT `evaluation_job_post_id_fk` FOREIGN KEY (`job_post_id`) REFERENCES `job_posts` (`job_post_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `evaluation_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of job_evaluations
-- ----------------------------
INSERT INTO `job_evaluations` VALUES ('eval-004', 'd11d1523-2551-4159-b5a7-2bc5edc2865a', '4e46477a-3586-4308-90df-ffd7961782f0', 5, '团队氛围很好，技术栈先进，有很多学习成长的机会。', '2025-03-15 09:15:00', 0);

-- ----------------------------
-- Table structure for job_posts
-- ----------------------------
DROP TABLE IF EXISTS `job_posts`;
CREATE TABLE `job_posts`  (
  `job_post_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位发布的唯一标识符，主键',
  `employer_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `job_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `job_description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `requirements` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `salary_range` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `posted_at` datetime(0) NULL DEFAULT NULL COMMENT '岗位发布时间',
  `updated_at` datetime(0) NULL DEFAULT NULL COMMENT '岗位更新时间',
  `job_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `company_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`job_post_id`) USING BTREE,
  INDEX `fk_job_posts_company`(`company_id`) USING BTREE,
  CONSTRAINT `fk_job_posts_company` FOREIGN KEY (`company_id`) REFERENCES `companies` (`company_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of job_posts
-- ----------------------------
INSERT INTO `job_posts` VALUES ('0a1de7dc-0183-11f0-895d-025086b7c084', 'ea2ec884-e7d0-42c0-9cfb-04b07542318d', '食堂勤工俭学', '负责食堂餐具回收和清洁工作', '有责任心，能吃苦耐劳', '18-24', '本部校区', 'APPROVED', '2025-03-15 17:51:17', '2025-03-17 06:21:24', 'campus', 'f5e16529-5c6d-44e0-8f98-924031bdc822');
INSERT INTO `job_posts` VALUES ('0a1dec66-0183-11f0-895d-025086b7c084', '42f15136-a9db-4fed-bbfd-6b2af59a9cec', '高等数学助教', '协助老师批改作业、解答学生问题', '数学成绩优秀，有耐心', '30-40', '理学院', 'APPROVED', '2025-03-15 17:51:17', '2025-03-15 17:51:17', 'teaching', NULL);
INSERT INTO `job_posts` VALUES ('0a1dee62-0183-11f0-895d-025086b7c084', '42f15136-a9db-4fed-bbfd-6b2af59a9cec', '学院办公室助理', '协助学院日常行政工作', '工作认真细致，沟通能力强', '22-28', '人文学院', 'APPROVED', '2025-03-15 17:51:17', '2025-03-15 17:51:17', 'admin', NULL);
INSERT INTO `job_posts` VALUES ('0a1defed-0183-11f0-895d-025086b7c084', '42f15136-a9db-4fed-bbfd-6b2af59a9cec', '图书馆管理员', '负责图书整理与借阅管理', '熟悉图书分类，做事细心', '20-30', '中央图书馆', 'APPROVED', '2025-03-15 17:51:17', '2025-03-15 17:51:17', 'library', NULL);
INSERT INTO `job_posts` VALUES ('0a1df14a-0183-11f0-895d-025086b7c084', '42f15136-a9db-4fed-bbfd-6b2af59a9cec', '实验室助理', '协助教授进行科研实验', '相关专业背景，动手能力强', '30-45', '科学楼', 'APPROVED', '2025-03-15 17:51:17', '2025-03-15 17:51:17', 'research', NULL);
INSERT INTO `job_posts` VALUES ('0a1df3ff-0183-11f0-895d-025086b7c084', '42f15136-a9db-4fed-bbfd-6b2af59a9cec', '校园导游', '负责校园参观讲解', '熟悉校园历史，普通话标准', '20-25', '校史馆', 'APPROVED', '2025-03-15 17:51:17', '2025-03-15 17:51:17', 'service', NULL);
INSERT INTO `job_posts` VALUES ('0a1df61f-0183-11f0-895d-025086b7c084', '42f15136-a9db-4fed-bbfd-6b2af59a9cec', '校园记者', '采访校园活动并撰写报道', '文字功底好，摄影能力佳', '20-30', '校媒中心', 'APPROVED', '2025-03-15 17:51:17', '2025-03-15 17:51:17', 'other', NULL);
INSERT INTO `job_posts` VALUES ('9cd0b3c8-1923-4050-b237-883876ec8f85', '', '121111', '1111111111', '1111', '3k-5k', '11111', 'APPROVED', '2025-03-17 15:14:25', '2025-03-17 16:20:08', NULL, NULL);
INSERT INTO `job_posts` VALUES ('d11d1523-2551-4159-b5a7-2bc5edc2865a', 'company_2521', '工程师', '拥有基本单位来源他们一切主要.点击一个合作.两个其他销售一些.', '首页，地址，发展，人民，很多', '12k-21k', '张家港县', 'APPROVED', '2025-03-11 10:10:47', '2025-03-17 06:22:04', 'other', NULL);
INSERT INTO `job_posts` VALUES ('d4bbfcef-bf91-40cd-bbdc-9ad179d5df56', NULL, '1515616516', '1561561561651', '26165156165156', '3k-5k', '151515', 'APPROVED', '2025-03-17 14:55:34', '2025-03-17 16:20:18', NULL, NULL);
INSERT INTO `job_posts` VALUES ('f27d6e61-887b-4307-af14-62fe48896bc6', 'company_8921', '采购员', '已经比较合作他们位置报告.经验时间内容欢迎资源单位.', '到了，搜索，业务，这个，也是', '10k-21k', '沈阳市', 'PENDING', '2025-03-03 11:16:04', '2025-03-17 06:21:42', 'other', NULL);
INSERT INTO `job_posts` VALUES ('f8e8102a-752e-434b-91d9-47df1f06a1f9', 'company_892', '软件UI设计师/工程师', '北京责任的话您的项目有些.要求有限提供帖子过程.男人文化论坛她的.', '语言，到了，投资，这个，为了', '14k-22k', '楠县', 'PENDING', '2025-03-04 01:13:34', '2025-03-17 06:21:45', 'other', NULL);

-- ----------------------------
-- Table structure for messages
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages`  (
  `message_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `application_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `content` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `job_post_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `read_status` bit(1) NOT NULL,
  `read_time` datetime(6) NULL DEFAULT NULL,
  `receiver_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `send_time` datetime(6) NOT NULL,
  `sender_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `sender_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`message_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of messages
-- ----------------------------

-- ----------------------------
-- Table structure for recommendations
-- ----------------------------
DROP TABLE IF EXISTS `recommendations`;
CREATE TABLE `recommendations`  (
  `recommendation_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '推荐记录的唯一标识符，主键',
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '被推荐的用户ID，外键关联users表的user_id',
  `job_post_is` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '推荐的岗位发布ID，外键关联job_posts表的job_post_id',
  `recommendation_score` double NULL DEFAULT NULL COMMENT '推荐的得分，衡量推荐的相关性',
  `recommended_at` datetime(0) NULL DEFAULT NULL COMMENT '推荐生成的时间',
  `clicked_at` datetime(0) NULL DEFAULT NULL COMMENT '用户点击了推荐，记录点击的时间',
  PRIMARY KEY (`recommendation_id`) USING BTREE,
  INDEX `recommendation_id_foreignkey`(`user_id`) USING BTREE,
  INDEX `recommendation_job_id_foreignkey`(`job_post_is`) USING BTREE,
  CONSTRAINT `recommendation_id_foreignkey` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `recommendation_job_id_foreignkey` FOREIGN KEY (`job_post_is`) REFERENCES `job_posts` (`job_post_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recommendations
-- ----------------------------

-- ----------------------------
-- Table structure for report_configurations
-- ----------------------------
DROP TABLE IF EXISTS `report_configurations`;
CREATE TABLE `report_configurations`  (
  `config_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '报表配置的唯一标识符，主键',
  `report_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报表类型',
  `report_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报表的标题',
  `report_columns` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '报表包含的列信息',
  `created_at` datetime(0) NULL DEFAULT NULL COMMENT '报表配置创建时间',
  `updated_at` datetime(0) NULL DEFAULT NULL COMMENT '报表配置更新时间',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of report_configurations
-- ----------------------------

-- ----------------------------
-- Table structure for report_records
-- ----------------------------
DROP TABLE IF EXISTS `report_records`;
CREATE TABLE `report_records`  (
  `record_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '报表记录的唯一标识符，主键',
  `config_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联的报表配置ID，外键关联report_configurations表的config_id',
  `report_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '报表的具体数据内容',
  `generated_at` datetime(0) NULL DEFAULT NULL COMMENT '报表生成的时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `records_id_foreignkey`(`config_id`) USING BTREE,
  CONSTRAINT `records_id_foreignkey` FOREIGN KEY (`config_id`) REFERENCES `report_configurations` (`config_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of report_records
-- ----------------------------

-- ----------------------------
-- Table structure for security_risks
-- ----------------------------
DROP TABLE IF EXISTS `security_risks`;
CREATE TABLE `security_risks`  (
  `risk_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '安全风险的唯一标识符，主键',
  `risk_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '风险类型',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '风险描述',
  `severity` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '风险严重程度',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '风险状态',
  `detected_at` datetime(0) NULL DEFAULT NULL COMMENT '风险检测到的时间',
  `resolved_at` datetime(0) NULL DEFAULT NULL COMMENT '风险解决的时间',
  PRIMARY KEY (`risk_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of security_risks
-- ----------------------------

-- ----------------------------
-- Table structure for staff
-- ----------------------------
DROP TABLE IF EXISTS `staff`;
CREATE TABLE `staff`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `company_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `department` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `employee_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `join_date` date NULL DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `position` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of staff
-- ----------------------------
INSERT INTO `staff` VALUES ('7d7e9dc3-02ff-11f0-a546-025086b7c084', 'f5e16529-5c6d-44e0-8f98-924031bdc822', NULL, '研发部', 'zhangsan@example.com', 'EMP001', '2022-01-10', '张三', '13900000001', '前端工程师', 'STAFF', '在职', NULL);
INSERT INTO `staff` VALUES ('7d7ea62b-02ff-11f0-a546-025086b7c084', 'f5e16529-5c6d-44e0-8f98-924031bdc822', NULL, '研发部', 'lisi@example.com', 'EMP002', '2021-06-15', '李四', '13900000002', '后端工程师', 'STAFF', '在职', NULL);
INSERT INTO `staff` VALUES ('7d7f498b-02ff-11f0-a546-025086b7c084', 'f5e16529-5c6d-44e0-8f98-924031bdc822', NULL, '市场部', 'wangwu@example.com', 'EMP003', '2020-09-01', '王五', '13900000003', '市场经理', 'ADMIN', '在职', NULL);
INSERT INTO `staff` VALUES ('7d7f4bc7-02ff-11f0-a546-025086b7c084', 'f5e16529-5c6d-44e0-8f98-924031bdc822', NULL, '财务部', 'zhaoliu@example.com', 'EMP004', '2023-02-20', '赵六', '13900000004', '财务主管', 'STAFF', '在职', NULL);
INSERT INTO `staff` VALUES ('7d7f4ca8-02ff-11f0-a546-025086b7c084', 'f5e16529-5c6d-44e0-8f98-924031bdc822', NULL, '人力资源部', 'liuqi@example.com', 'EMP005', '2022-11-05', '刘七', '13900000005', 'HR专员', 'STAFF', '在职', '2025-03-17 16:21:04.620538');
INSERT INTO `staff` VALUES ('7d7f4d64-02ff-11f0-a546-025086b7c084', 'f5e16529-5c6d-44e0-8f98-924031bdc822', NULL, '研发部', 'chenba@example.com', 'EMP006', '2023-07-01', '陈八', '13900000006', '测试工程师', 'STAFF', '实习', NULL);
INSERT INTO `staff` VALUES ('7d7f4e11-02ff-11f0-a546-025086b7c084', 'f5e16529-5c6d-44e0-8f98-924031bdc822', NULL, '销售部', 'qianjiu@example.com', 'EMP007', '2021-03-15', '钱九', '13900000007', '销售经理', 'ADMIN', '在职', NULL);
INSERT INTO `staff` VALUES ('7d7f4ec8-02ff-11f0-a546-025086b7c084', 'f5e16529-5c6d-44e0-8f98-924031bdc822', NULL, '客服部', 'sunshi@example.com', 'EMP008', '2020-05-10', '孙十', '13900000008', '客服专员', 'STAFF', '离职', NULL);
INSERT INTO `staff` VALUES ('7d7f4f7b-02ff-11f0-a546-025086b7c084', 'f5e16529-5c6d-44e0-8f98-924031bdc822', NULL, '产品部', 'zhouyi@example.com', 'EMP009', '2022-08-12', '周一', '13900000009', '产品经理', 'STAFF', '在职', NULL);
INSERT INTO `staff` VALUES ('7d7f5024-02ff-11f0-a546-025086b7c084', 'f5e16529-5c6d-44e0-8f98-924031bdc822', NULL, '技术部', 'wuer@example.com', 'EMP010', '2019-12-01', '吴二', '13900000010', '架构师', 'ADMIN', '在职', NULL);

-- ----------------------------
-- Table structure for user_notifications
-- ----------------------------
DROP TABLE IF EXISTS `user_notifications`;
CREATE TABLE `user_notifications`  (
  `notification_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接收通知的用户ID，外键关联users表的user_id',
  `message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_read` tinyint(0) NULL DEFAULT NULL COMMENT '表示通知是否已读的标志',
  `sent_at` datetime(0) NULL DEFAULT NULL COMMENT '通知发送的时间',
  `read_at` datetime(0) NULL DEFAULT NULL COMMENT '通知读取的时间',
  `read_status` bit(1) NOT NULL,
  PRIMARY KEY (`notification_id`) USING BTREE,
  INDEX `notification_id_foreignkey`(`user_id`) USING BTREE,
  CONSTRAINT `notification_id_foreignkey` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_notifications
-- ----------------------------

-- ----------------------------
-- Table structure for user_preferences
-- ----------------------------
DROP TABLE IF EXISTS `user_preferences`;
CREATE TABLE `user_preferences`  (
  `preference_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '偏好记录的唯一标识符，主键',
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联的用户ID，外键关联users表的user_id',
  `preference_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '偏好类型',
  `preference_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '偏好的值',
  `created_at` datetime(0) NULL DEFAULT NULL COMMENT '偏好记录创建时间',
  `updated_at` datetime(0) NULL DEFAULT NULL COMMENT '偏好记录更新时间',
  PRIMARY KEY (`preference_id`) USING BTREE,
  INDEX `preferences_id_foreignkey`(`user_id`) USING BTREE,
  CONSTRAINT `preferences_id_foreignkey` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_preferences
-- ----------------------------

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户唯一标识符，主键',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户登录名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户密码，加密存储',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户的邮箱，邮箱登录和接收通知',
  `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户的手机号码，用于手机号登录',
  `created_at` datetime(0) NULL DEFAULT NULL,
  `updated_at` datetime(0) NULL DEFAULT NULL,
  `user_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_status` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('42f15136-a9db-4fed-bbfd-6b2af59a9cec', 'Sumire', 'qaz98001588', '1667312433@qq.com', '18008464985', '2025-03-12 09:46:39', '2025-03-13 09:46:43', 'ADMIN', '正常');
INSERT INTO `users` VALUES ('45dc1f25-9f0f-42db-b1a8-4b02c692674e', 'qhodg', '123456', '1667312433@example.org', '18008464985', '2020-12-22 10:46:07', '2025-03-17 16:19:56', 'NORMAL', '冻结');
INSERT INTO `users` VALUES ('4e46477a-3586-4308-90df-ffd7961782f0', 'thompsonchristopher', '@5M+NsPe', 'uwilliams@example.org', '+1-941-635-0114x4318', '2019-08-03 12:22:14', '2025-03-17 06:20:41', 'NORMAL', '正常');
INSERT INTO `users` VALUES ('b569a3c6-123b-44de-97bd-f6190461d272', 'kingolivia', '+)7ZmA+i', 'robertser@example.com', '(815)603-5836x70775', '2004-04-17 17:18:42', '2025-03-17 06:22:40', 'NORMAL', '正常');
INSERT INTO `users` VALUES ('be917156-82a4-45b8-b88e-d078afefaf96', 'matthew55', '^7aNQVam', 'vkim@example.net', '001-398-734-0037x195', '2000-07-10 08:59:59', '2003-03-29 12:11:02', 'NORMAL', '正常');
INSERT INTO `users` VALUES ('e9270db7-3ea4-4e30-a4fe-8944621d3606', 'su', '123456', '1663@qq.com', '15098001588', NULL, NULL, 'NORMAL', NULL);
INSERT INTO `users` VALUES ('ea2ec884-e7d0-42c0-9cfb-04b07542318d', 'danielleparker', 'ou#1KVg_', 'idawson@example.com', '784.730.9942', '2016-06-04 09:13:27', '1997-11-05 21:58:30', 'EMPLOYER', '正常');

SET FOREIGN_KEY_CHECKS = 1;
