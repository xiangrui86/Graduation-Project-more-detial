/*
 Navicat Premium Dump SQL

 Source Server         : bi she
 Source Server Type    : MySQL
 Source Server Version : 80037 (8.0.37)
 Source Host           : localhost:3306
 Source Schema         : mall

 Target Server Type    : MySQL
 Target Server Version : 80037 (8.0.37)
 File Encoding         : 65001

 Date: 28/03/2026 14:17:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `city` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `detail_address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `district` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `is_default` bit(1) NOT NULL,
  `province` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `receiver_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `receiver_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKrbi6ij3u9oy8qrwyb2r0q3v09`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FKrbi6ij3u9oy8qrwyb2r0q3v09` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of address
-- ----------------------------

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `image_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `redirect_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `sort` int NOT NULL,
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `link` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `product_id` bigint NOT NULL,
  `sort_order` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of banner
-- ----------------------------

-- ----------------------------
-- Table structure for cart_item
-- ----------------------------
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item`  (
  `quantity` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `spec_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK2dhdhp9i9ld88nv1bejg9hbx7`(`user_id` ASC, `product_id` ASC) USING BTREE,
  UNIQUE INDEX `UKqulwp04wxfroadnvfvoex2p1g`(`user_id` ASC, `product_id` ASC, `spec_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart_item
-- ----------------------------

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `sort_order` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint NULL DEFAULT NULL,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `icon` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '2026-03-08 16:23:40.538136', 1, 0, '球类', NULL);
INSERT INTO `category` VALUES (3, '2026-03-08 16:23:40.542359', 3, 0, '健身器材', NULL);
INSERT INTO `category` VALUES (6, '2026-03-13 15:01:29.203325', 6, NULL, '自行车', NULL);
INSERT INTO `category` VALUES (8, '2026-03-13 15:48:14.962726', 8, NULL, '头盔', NULL);
INSERT INTO `category` VALUES (0, '2026-03-27 15:58:37.788469', 12, NULL, '户外露营', NULL);
INSERT INTO `category` VALUES (0, '2026-03-27 16:02:57.295645', 13, NULL, '水上 / 冰雪', NULL);

-- ----------------------------
-- Table structure for chat_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message`  (
  `read_flag` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `receiver_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  `receiver_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `sender_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_message
-- ----------------------------
INSERT INTO `chat_message` VALUES (b'0', '2026-03-12 17:06:43.070359', 1, 2, 3, 'MERCHANT', 'USER', '111');

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`  (
  `created_at` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK7mwcjtab8q4b44jw3bur1okk8`(`user_id` ASC, `product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of favorite
-- ----------------------------
INSERT INTO `favorite` VALUES ('2026-03-18 18:21:05.802533', 4, 3, 3);
INSERT INTO `favorite` VALUES ('2026-03-19 14:19:27.886767', 5, 11, 3);
INSERT INTO `favorite` VALUES ('2026-03-26 19:06:59.706780', 6, 4, 3);

-- ----------------------------
-- Table structure for merchant
-- ----------------------------
DROP TABLE IF EXISTS `merchant`;
CREATE TABLE `merchant`  (
  `enabled` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `contact_person` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `logo` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of merchant
-- ----------------------------
INSERT INTO `merchant` VALUES (b'1', '2026-03-08 16:23:40.402009', 1, '2026-03-26 17:44:02.243858', NULL, NULL, '运营1号', NULL, '专业运动器材和装备销售');

-- ----------------------------
-- Table structure for news
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news`  (
  `published` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of news
-- ----------------------------
INSERT INTO `news` VALUES (b'1', '2026-03-08 16:23:40.551931', 1, '2026-03-13 15:58:43.510932', 'ANNOUNCEMENT', '欢迎来到体育用品电商平台', '我们为您提供最优质的运动器材和专业的购物体验。');
INSERT INTO `news` VALUES (b'1', '2026-03-13 15:57:05.780882', 2, '2026-03-13 15:57:05.780882', 'NEWS', '2026 春季科学健身指南', '\n春季是恢复训练的黄金时期！建议从低强度有氧开始，搭配核心力量训练，循序渐进提升体能。平台已上线多款居家健身好物，助力科学健身！');

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `price` decimal(12, 2) NOT NULL,
  `quantity` int NOT NULL,
  `sub_total` decimal(12, 2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `product_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `product_image` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `refund_reason` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `refund_request_time` datetime(6) NULL DEFAULT NULL,
  `refund_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `spec_id` bigint NULL DEFAULT NULL,
  `spec_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `reviewed` bit(1) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (199.00, 1, 199.00, '2026-03-08 16:26:02.631778', 1, 1, 2, '篮球', '/api/images/basketball.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (299.00, 4, 1196.00, '2026-03-09 13:55:15.482136', 2, 2, 1, '专业足球', '/api/images/football.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (299.00, 7, 2093.00, '2026-03-11 23:49:34.165607', 3, 3, 1, '专业足球', '/api/images/football.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (299.00, 7, 2093.00, '2026-03-13 15:06:49.443967', 4, 4, 1, '专业足球', '/api/images/football.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (1899.00, 3, 5697.00, '2026-03-13 15:06:49.447677', 5, 4, 3, '跑步机', '/api/images/treadmill.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (399.00, 1, 399.00, '2026-03-13 15:06:49.447996', 6, 4, 4, '羽毛球拍', '/api/images/badminton.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (6.00, 1, 6.00, '2026-03-13 15:06:49.457335', 7, 4, 7, '自行车', '/api/images/badminton.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (199.00, 1, 199.00, '2026-03-19 14:19:50.755594', 8, 5, 2, '篮球', '/api/images/basketball.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (1899.00, 2, 3798.00, '2026-03-19 14:19:50.761957', 9, 5, 3, '跑步机', '/api/images/treadmill.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (399.00, 2, 798.00, '2026-03-19 14:19:50.765050', 10, 5, 4, '羽毛球拍', '/api/images/badminton.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (99.00, 1, 99.00, '2026-03-19 14:19:50.767569', 11, 5, 11, '哑铃', '/api/images/Dumbbell.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (199.00, 2, 398.00, '2026-03-24 22:20:30.404409', 12, 6, 2, '篮球', '/api/images/basketball.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (1899.00, 2, 3798.00, '2026-03-24 22:20:30.407279', 13, 6, 3, '跑步机', '/api/images/treadmill.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (399.00, 1, 399.00, '2026-03-24 22:20:30.409285', 14, 6, 4, '羽毛球拍', '/api/images/badminton.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (200.00, 1, 200.00, '2026-03-24 22:20:30.411777', 15, 6, 10, '骑行头盔', '/api/images/Bicycle helmet.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (300.00, 1, 300.00, '2026-03-24 22:27:06.249611', 16, 7, 1, '专业足球', '/api/images/football.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (1899.00, 1, 1899.00, '2026-03-24 22:27:06.254320', 17, 7, 3, '跑步机', '/api/images/treadmill.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (300.00, 1, 300.00, '2026-03-25 19:05:30.833045', 18, 8, 1, '迪卡侬儿童足球', 'blob:http://localhost:8081/7ac3a0e7-81f1-4c2c-98bb-d19efe7f13cb', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (200.00, 1, 200.00, '2026-03-25 19:05:30.837418', 19, 8, 10, '骑行头盔', '/api/images/Bicycle helmet.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (99.00, 3, 297.00, '2026-03-25 19:05:30.840204', 20, 8, 11, '哑铃', '/api/images/Dumbbell.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (199.00, 1, 199.00, '2026-03-26 14:16:03.617603', 21, 9, 2, '篮球', '/api/images/basketball.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (59.90, 1, 59.90, '2026-03-26 18:04:07.891043', 22, 10, 1, '迪卡侬儿童足球', 'http://localhost:8080/api/pub/images/view/1774511322168_1774442212560_football__2_.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (59.90, 1, 59.90, '2026-03-26 19:10:17.422892', 23, 11, 1, '迪卡侬儿童足球', 'http://localhost:8080/api/pub/images/view/1774511322168_1774442212560_football__2_.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (2199.00, 1, 2199.00, '2026-03-26 19:10:17.429003', 24, 11, 8, '福特山地自行车', 'http://localhost:8080/api/pub/images/view/1774517819328_8a48660db5767d675acc1aa609406d2.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (1899.00, 1, 1899.00, '2026-03-27 15:31:58.133882', 25, 12, 3, '立久佳猛犸跑步机', 'http://localhost:8080/api/pub/images/view/1774516799208_df50af8402564e7446921eb829a8e58.jpg', NULL, NULL, NULL, NULL, NULL, b'0');
INSERT INTO `order_item` VALUES (99.00, 1, 99.00, '2026-03-27 15:31:58.135891', 26, 12, 4, '李宁羽毛球套装', 'http://localhost:8080/api/pub/images/view/1774517190662_019b2a577e16f58b09be74b5257a3b2.jpg', NULL, NULL, NULL, NULL, NULL, b'0');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `pay_amount` decimal(12, 2) NULL DEFAULT NULL,
  `total_amount` decimal(12, 2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `merchant_id` bigint NOT NULL,
  `pay_time` datetime(6) NULL DEFAULT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `pay_method` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `receiver_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `receiver_address` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `refund_reason` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `refund_request_time` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UKg8pohnngqi5x1nask7nff2u7w`(`order_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (199.00, 199.00, '2026-03-08 16:26:02.627628', 1, 1, '2026-03-08 16:26:04.202754', '2026-03-18 22:41:57.431736', 3, '1', 'REFUNDED', 'O202603081626023c1f598e', 'SIMULATE', '1', '1', '11', '2026-03-18 19:39:13.009988');
INSERT INTO `orders` VALUES (1196.00, 1196.00, '2026-03-09 13:55:15.464522', 2, 1, '2026-03-09 13:55:17.324065', '2026-03-11 23:52:21.396560', 3, '111111', 'RECEIVED', 'O2026030913551511c84639', 'SIMULATE', '1111', '11111111', NULL, NULL);
INSERT INTO `orders` VALUES (2093.00, 2093.00, '2026-03-11 23:49:34.153428', 3, 1, '2026-03-11 23:49:35.650374', '2026-03-24 22:09:29.858550', 3, '11', 'REFUNDED', 'O202603112349348b0d6b6e', 'SIMULATE', '11', '11', '不想要了', '2026-03-24 22:08:41.981713');
INSERT INTO `orders` VALUES (8195.00, 8195.00, '2026-03-13 15:06:49.441514', 4, 1, '2026-03-13 15:06:52.600141', '2026-03-24 22:09:26.961495', 3, '11', 'REFUNDED', 'O2026031315064927b38c44', 'SIMULATE', '11', '11', '11', '2026-03-24 18:28:38.777243');
INSERT INTO `orders` VALUES (4894.00, 4894.00, '2026-03-19 14:19:50.743136', 5, 1, '2026-03-19 14:19:54.218451', '2026-03-19 14:23:29.904443', 3, '111', 'REFUNDED', 'O20260319141950fd596366', 'SIMULATE', '111', '11', '不需要了', '2026-03-19 14:23:08.879997');
INSERT INTO `orders` VALUES (4795.00, 4795.00, '2026-03-24 22:20:30.382671', 6, 1, '2026-03-24 22:20:35.098439', '2026-03-24 22:21:33.383950', 3, '110', 'RECEIVED', 'O202603242220305710293a', 'SIMULATE', '我', '郑州', NULL, NULL);
INSERT INTO `orders` VALUES (NULL, 2199.00, '2026-03-24 22:27:06.245274', 7, 1, NULL, '2026-03-24 22:27:12.523123', 3, '11', 'CANCELLED', 'O20260324222706e1dc68d3', NULL, '11', '11', NULL, NULL);
INSERT INTO `orders` VALUES (NULL, 797.00, '2026-03-25 19:05:30.824733', 8, 1, NULL, '2026-03-25 19:05:34.719042', 3, '1111111111', 'CANCELLED', 'O20260325190530d38f5db5', NULL, '请求', '11', NULL, NULL);
INSERT INTO `orders` VALUES (NULL, 199.00, '2026-03-26 14:16:03.612286', 9, 1, NULL, '2026-03-26 17:31:52.477908', 3, '11', 'CANCELLED', 'O2026032614160388c0f621', NULL, '11', '11', NULL, NULL);
INSERT INTO `orders` VALUES (59.90, 59.90, '2026-03-26 18:04:07.844867', 10, 1, '2026-03-26 18:04:08.227896', '2026-03-26 19:16:38.330180', 3, '13013013010', 'COMPLETED', 'O20260326180407542b3cc8', 'WECHAT', '王', '郑州科技学院', NULL, NULL);
INSERT INTO `orders` VALUES (2258.90, 2258.90, '2026-03-26 19:10:17.409752', 11, 1, '2026-03-26 19:10:17.665524', '2026-03-26 19:10:27.563881', 3, '13013013010', 'CANCELLED', 'O20260326191017ffc07648', 'WECHAT', '王', '郑州科技学院', NULL, NULL);
INSERT INTO `orders` VALUES (1998.00, 1998.00, '2026-03-27 15:31:58.130842', 12, 1, '2026-03-27 15:31:58.387339', '2026-03-27 16:03:42.199533', 3, '13013013010', 'RECEIVED', 'O202603271531583caf353a', 'WECHAT', '王', '郑州科技学院', NULL, NULL);

-- ----------------------------
-- Table structure for payment_record
-- ----------------------------
DROP TABLE IF EXISTS `payment_record`;
CREATE TABLE `payment_record`  (
  `pay_amount` decimal(12, 2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `pay_time` datetime(6) NOT NULL,
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `pay_method` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payment_record
-- ----------------------------
INSERT INTO `payment_record` VALUES (199.00, '2026-03-08 16:26:04.202754', 1, 1, '2026-03-08 16:26:04.202754', 'O202603081626023c1f598e', 'SIMULATE');
INSERT INTO `payment_record` VALUES (1196.00, '2026-03-09 13:55:17.325197', 2, 2, '2026-03-09 13:55:17.324065', 'O2026030913551511c84639', 'SIMULATE');
INSERT INTO `payment_record` VALUES (2093.00, '2026-03-11 23:49:35.650374', 3, 3, '2026-03-11 23:49:35.650374', 'O202603112349348b0d6b6e', 'SIMULATE');
INSERT INTO `payment_record` VALUES (8195.00, '2026-03-13 15:06:52.600449', 4, 4, '2026-03-13 15:06:52.600449', 'O2026031315064927b38c44', 'SIMULATE');
INSERT INTO `payment_record` VALUES (4894.00, '2026-03-19 14:19:54.219454', 5, 5, '2026-03-19 14:19:54.219454', 'O20260319141950fd596366', 'SIMULATE');
INSERT INTO `payment_record` VALUES (4795.00, '2026-03-24 22:20:35.099491', 6, 6, '2026-03-24 22:20:35.099491', 'O202603242220305710293a', 'SIMULATE');
INSERT INTO `payment_record` VALUES (59.90, '2026-03-26 18:04:08.228441', 7, 10, '2026-03-26 18:04:08.228441', 'O20260326180407542b3cc8', 'WECHAT');
INSERT INTO `payment_record` VALUES (2258.90, '2026-03-26 19:10:17.666738', 8, 11, '2026-03-26 19:10:17.666738', 'O20260326191017ffc07648', 'WECHAT');
INSERT INTO `payment_record` VALUES (1998.00, '2026-03-27 15:31:58.387339', 9, 12, '2026-03-27 15:31:58.387339', 'O202603271531583caf353a', 'WECHAT');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `is_new` bit(1) NULL DEFAULT NULL,
  `on_sale` bit(1) NOT NULL,
  `original_price` decimal(12, 2) NULL DEFAULT NULL,
  `price` decimal(12, 2) NOT NULL,
  `sales` int NOT NULL,
  `stock` int NOT NULL,
  `review_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `review_reason` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `category_id` bigint NULL DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `merchant_id` bigint NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `image` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `images` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `specs` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `colors` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `detail_description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `detail_images` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `brand` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `attributes` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `image_list` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `unit` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (b'1', b'1', 99.00, 59.90, 20, 99, 1, '2026-03-08 16:23:40.545122', 1, 1, '2026-03-26 19:49:30.503619', '迪卡侬儿童足球', '迪卡侬儿童足球小学生专用3号中考学生成人青少年训练比赛IVO2', 'http://localhost:8080/api/pub/images/view/1774511322168_1774442212560_football__2_.jpg', NULL, NULL, NULL, '<p style=\"text-align:center;\"><img src=\"http://localhost:8080/api/pub/images/view/1774512511224_02d9e3cbcd10e5bac3edfd073ad0796.jpg\" style=\"max-width:100%;\" contenteditable=\"false\" width=\"50%\"/><img src=\"http://localhost:8080/api/pub/images/view/1774513295716_d6236ccffd07f6203481edefe5a3cc6.jpg\" style=\"max-width:100%;\" contenteditable=\"false\" width=\"50%\"/><br/>参数</p><table border=\"0\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"text-align:center;\"><tbody><tr><th>足球重量</th><th>足球缝线</th><th>适用场地</th></tr><tr><td>350g</td><td>机缝足球</td><td>草地</td></tr></tbody></table>', 'http://localhost:8080/api/pub/images/view/1774511561302_1774509734969_1774442212560_football__2_.jpg,http://localhost:8080/api/pub/images/view/1774513467083_a5481a93f60823e1e3c75877f4c9cb6.jpg,http://localhost:8080/api/pub/images/view/1774513470349_acac7945dca6a227afa7805e4f707cb.jpg,http://localhost:8080/api/pub/images/view/1774514781883_ef57c37ed020822d3bc3d26c97502a5.jpg', '迪卡侬', NULL, NULL, '件');
INSERT INTO `product` VALUES (b'1', b'1', 69.00, 99.00, 4, 101, 1, '2026-03-08 16:23:40.547148', 2, 1, '2026-03-26 17:32:48.033027', '安踏儿童足球', '安踏足球4号儿童小学生专用初中生中考比赛训练标准5号球官方正品', 'http://localhost:8080/api/pub/images/view/1774516049112_4c97c92ab258fe02bb0857066133f03.jpg', NULL, NULL, NULL, '<img src=\"http://localhost:8080/api/pub/images/view/1774516210004_b6fbdad355a141ad95e086de0fdf63a.jpg\" style=\"max-width:100%;\" contenteditable=\"false\" width=\"50%\"/><p data-we-empty-p=\"\" style=\"text-align:center;\"><img src=\"http://localhost:8080/api/pub/images/view/1774516258540_7ca5a2c3ecd9e4e354f8f4825c727b9.jpg\" style=\"max-width:100%;\" contenteditable=\"false\" width=\"50%\"/><br/><img src=\"http://localhost:8080/api/pub/images/view/1774516326494_250271aef8399f2edac384d76ef693a.jpg\" style=\"max-width:100%;\" contenteditable=\"false\" width=\"50%\"/><br/><br/></p>', 'http://localhost:8080/api/pub/images/view/1774516054852_60239dc25003d1eb14bc1edbdcc4c29.jpg,http://localhost:8080/api/pub/images/view/1774516079338_ce03d38e9cdde0203563d77ac351a7d.jpg,http://localhost:8080/api/pub/images/view/1774516083341_4c97c92ab258fe02bb0857066133f03.jpg,http://localhost:8080/api/pub/images/view/1774516091756_edbdf54fbfccd107f0eb8757faac5f6.jpg', '安踏', NULL, NULL, '件');
INSERT INTO `product` VALUES (b'1', b'1', 2499.00, 1899.00, 13, 12, 3, '2026-03-08 16:23:40.548299', 3, 1, '2026-03-27 15:31:58.400413', '立久佳猛犸跑步机', '立久佳猛犸跑步机家用款折叠爬坡静音小型室内家庭新款健身房专用', 'http://localhost:8080/api/pub/images/view/1774516799208_df50af8402564e7446921eb829a8e58.jpg', NULL, NULL, NULL, '<p data-we-empty-p=\"\" style=\"text-align:center;\"><img src=\"http://localhost:8080/api/pub/images/view/1774516822137_49b4ff4b4ee8e11d2e0c6fa997ff7a1.jpg\" style=\"max-width:100%;\" contenteditable=\"false\" width=\"50%\"/><br/><img src=\"http://localhost:8080/api/pub/images/view/1774516844168_084eca51b7871e459f9746051f2ba95.jpg\" style=\"max-width:100%;\" contenteditable=\"false\" width=\"50%\"/></p><p data-we-empty-p=\"\" style=\"text-align:center;\"><img src=\"http://localhost:8080/api/pub/images/view/1774516867528_317cda1d8088d99036e079e2bb4694c.jpg\" style=\"max-width:100%;\" contenteditable=\"false\" width=\"50%\"/></p>', NULL, '利久佳', NULL, NULL, '件');
INSERT INTO `product` VALUES (b'1', b'1', 199.00, 99.00, 8, 99, 1, '2026-03-08 16:23:40.549535', 4, 1, '2026-03-27 15:31:58.400413', '李宁羽毛球套装', '李宁羽毛球拍雷霆9官方正品凯胜昊天锤8单双拍套装旗舰碳素纤维店', 'http://localhost:8080/api/pub/images/view/1774517190662_019b2a577e16f58b09be74b5257a3b2.jpg', NULL, NULL, NULL, '', 'http://localhost:8080/api/pub/images/view/1774517202598_be6b842175ce0edfd1f29b4a7597100.jpg,http://localhost:8080/api/pub/images/view/1774517211278_4c7f82f4a5de21611bc0d3e9e68e2e7.jpg,http://localhost:8080/api/pub/images/view/1774517220512_a53314f000d87a59c5df5fb77d5c730.jpg,http://localhost:8080/api/pub/images/view/1774517229128_397931925fd77ded45cd076a82fad23.jpg', '李宁', NULL, NULL, '件');
INSERT INTO `product` VALUES (b'1', b'1', 2999.00, 2199.00, 1, 100, 6, '2026-03-13 15:27:48.018899', 8, 1, '2026-03-26 19:10:27.563881', '福特山地自行车', 'Ford福特LM350山地车成人探路者单车学生女款LM380男式山地自行车', 'http://localhost:8080/api/pub/images/view/1774517819328_8a48660db5767d675acc1aa609406d2.jpg', NULL, NULL, NULL, '<p data-we-empty-p=\"\" style=\"text-align:center;\"><img src=\"http://localhost:8080/api/pub/images/view/1774517962692_0d588f35ce07682fecb70c8f392575b.jpg\" style=\"max-width:100%;\" contenteditable=\"false\" width=\"50%\"/></p><p data-we-empty-p=\"\" style=\"text-align:center;\"><img src=\"http://localhost:8080/api/pub/images/view/1774517978177_a2ceb7547d68692d327a8a66bfd9014.jpg\" style=\"max-width:100%;\" contenteditable=\"false\" width=\"50%\"/></p><p data-we-empty-p=\"\" style=\"text-align:center;\"><img src=\"http://localhost:8080/api/pub/images/view/1774517999101_6c8b57910571d95cd0587036f775879.jpg\" style=\"max-width:100%;\" contenteditable=\"false\" width=\"50%\"/></p><p data-we-empty-p=\"\" style=\"text-align:center;\"><img src=\"http://localhost:8080/api/pub/images/view/1774518018824_b33547b1d65d9a85221d29cdf235cc2.jpg\" style=\"max-width:100%;\" contenteditable=\"false\" width=\"50%\"/></p>', 'http://localhost:8080/api/pub/images/view/1774517826185_9294e25e3d5880e9f7cc721d79ffdff.jpg,http://localhost:8080/api/pub/images/view/1774517833261_dd498429abf209ee760ee0be60cf3d1.jpg,http://localhost:8080/api/pub/images/view/1774517840499_6b24664e87f1d9f50e158243fa0d7b2.jpg,http://localhost:8080/api/pub/images/view/1774517849136_df2f39228219a92d64ccdf3fadd6fe3.jpg,http://localhost:8080/api/pub/images/view/1774517854150_8a48660db5767d675acc1aa609406d2.jpg', '福特', NULL, NULL, '件');
INSERT INTO `product` VALUES (b'1', b'1', 7299.00, 6999.00, 0, 10, 13, '2026-03-27 16:32:42.169199', 14, 1, '2026-03-27 16:32:42.169199', 'ATOMIC阿托米克滑雪双板2526新品', 'ATOMIC阿托米克滑雪双板2526新品小回转进阶滑雪板Redster S9I', 'http://localhost:8080/api/pub/images/view/1774600155582_f2e77b97878dc4c78b4254824dc125a.jpg', NULL, NULL, NULL, '<p data-we-empty-p=\"\" style=\"text-align:center;\"><img src=\"http://localhost:8080/api/pub/images/view/1774600227987_b3ef126c27d0995dcc34c7c1320c6b6.jpg\" style=\"max-width:100%;\" contenteditable=\"false\" width=\"50%\"/><br/><img src=\"http://localhost:8080/api/pub/images/view/1774600257237_945eca7037c3e44e82cd198b62b0292.jpg\" style=\"max-width:100%;\" contenteditable=\"false\" width=\"50%\"/></p><p data-we-empty-p=\"\" style=\"text-align:center;\"><img src=\"http://localhost:8080/api/pub/images/view/1774600270664_d18b5f6b75895948f634902c40612b6.jpg\" style=\"max-width:100%;\" contenteditable=\"false\" width=\"50%\"/></p><p data-we-empty-p=\"\" style=\"text-align:center;\"><img src=\"http://localhost:8080/api/pub/images/view/1774600280360_2ef54447705da6b4e283c656be16bc1.jpg\" style=\"max-width:100%;\" contenteditable=\"false\" width=\"50%\"/></p><p data-we-empty-p=\"\" style=\"text-align:center;\"><img src=\"http://localhost:8080/api/pub/images/view/1774600292659_bd4130839e1656be1f71df650f84dbf.jpg\" style=\"max-width:100%;\" contenteditable=\"false\" width=\"50%\"/></p><p data-we-empty-p=\"\" style=\"text-align:center;\"><img src=\"http://localhost:8080/api/pub/images/view/1774600311095_45f0d0982807289ebb512c419fd07bb.jpg\" style=\"max-width:100%;\" contenteditable=\"false\" width=\"50%\"/></p>', NULL, 'ATOMIC', NULL, NULL, '件');

-- ----------------------------
-- Table structure for product_review
-- ----------------------------
DROP TABLE IF EXISTS `product_review`;
CREATE TABLE `product_review`  (
  `rating` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NULL DEFAULT NULL,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_review
-- ----------------------------

-- ----------------------------
-- Table structure for product_sku
-- ----------------------------
DROP TABLE IF EXISTS `product_sku`;
CREATE TABLE `product_sku`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `price` decimal(12, 2) NULL DEFAULT NULL,
  `product_id` bigint NOT NULL,
  `sku_image` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `sku_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `sku_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `stock` int NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_sku
-- ----------------------------

-- ----------------------------
-- Table structure for product_spec
-- ----------------------------
DROP TABLE IF EXISTS `product_spec`;
CREATE TABLE `product_spec`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `spec_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `spec_value` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `stock` int NULL DEFAULT 0,
  `price_delta` decimal(10, 2) NULL DEFAULT 0.00,
  `spec_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `sort_order` int NULL DEFAULT 0,
  `enabled` tinyint(1) NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `image` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `spec_images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product`(`product_id` ASC) USING BTREE,
  CONSTRAINT `product_spec_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_spec
-- ----------------------------

-- ----------------------------
-- Table structure for shop
-- ----------------------------
DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `logo` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `merchant_id` bigint NOT NULL,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK2oyqeiiol473xsir1ktfcarlp`(`merchant_id` ASC) USING BTREE,
  UNIQUE INDEX `idx_shop_merchant_id`(`merchant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shop
-- ----------------------------
INSERT INTO `shop` VALUES (1, '2026-03-18 18:31:24.393290', 'auto', NULL, 6, 'new-m_62cf2731', 'ENABLED', '2026-03-18 18:31:24.393290');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `enabled` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `merchant_id` bigint NULL DEFAULT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `nickname` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('ADMIN','MERCHANT','USER') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `receiver_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `receiver_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK51bvuyvihefoh4kp5syh2jpi4`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (b'1', '2026-03-08 16:23:40.377371', 1, NULL, '2026-03-08 16:23:40.377371', NULL, '管理员', NULL, 'admin', '$2a$10$iQY3JGSs53jQOHTpqVXP0OJPntG3aVIZdGzNvH/2qhUFjN4azwGsm', 'ADMIN', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (b'1', '2026-03-08 16:23:40.470006', 2, 1, '2026-03-26 17:43:29.767808', NULL, '运营', NULL, 'merchant', '$2a$10$QgkYQ/fbTmgOH5.MW09lXur3prDcXs0GPiLYG1Dk0MhhGipnUJVQK', 'MERCHANT', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (b'1', '2026-03-08 16:23:40.536304', 3, NULL, '2026-03-24 23:01:29.537429', '13013013010', '用户', '783194158@qq.com', 'user', '$2a$10$iYwyslyKDQb6VI2pCY3cMObqXydOFCe9S8SGU09XRgvAEPfVCGq02', 'USER', NULL, 'MALE', '郑州科技学院', '王', '13013013010');
INSERT INTO `sys_user` VALUES (b'1', '2026-03-27 16:38:17.062537', 10, NULL, '2026-03-27 16:39:17.871936', '18800000000', '乖宝宝', '123456@qq.com', 'jmr', '$2a$10$5DfnQv7gwlfWy1DJS1MlButKk4pr2YPeyKRg3Bh7oQam26fVFmqeu', 'USER', NULL, 'MALE', '郑州科技学院', '姬', '18800000000');

SET FOREIGN_KEY_CHECKS = 1;
