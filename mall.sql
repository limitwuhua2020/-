/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : localhost:3306
 Source Schema         : mall

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 28/02/2025 06:15:36
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_admin
-- ----------------------------
DROP TABLE IF EXISTS `t_admin`;
CREATE TABLE `t_admin`  (
  `id` int NOT NULL,
  `userName` varchar(66) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `password` varchar(66) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_admin
-- ----------------------------
INSERT INTO `t_admin` VALUES (1, 'admin', '123456');

-- ----------------------------
-- Table structure for t_bigtype
-- ----------------------------
DROP TABLE IF EXISTS `t_bigtype`;
CREATE TABLE `t_bigtype`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(150) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `remark` varchar(765) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `image` varchar(765) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id` DESC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 124 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_bigtype
-- ----------------------------
INSERT INTO `t_bigtype` VALUES (10, '大米', '大米类描述', '10.jpg');
INSERT INTO `t_bigtype` VALUES (9, '粮油类', '粮油类描述', '9.jpg');
INSERT INTO `t_bigtype` VALUES (8, '海鲜水产类', '海鲜水产类描述', '8.jpg');
INSERT INTO `t_bigtype` VALUES (7, '奶类', '奶类描述', '7.jpg');
INSERT INTO `t_bigtype` VALUES (6, '禽肉类', '禽肉类描述', '6.jpg');
INSERT INTO `t_bigtype` VALUES (5, '坚果类', '坚果类描述', '5.jpg');
INSERT INTO `t_bigtype` VALUES (4, '水果类', '水果类描述', '4.jpg');
INSERT INTO `t_bigtype` VALUES (3, '豆类', '豆类描述', '3.jpg');
INSERT INTO `t_bigtype` VALUES (2, '蔬菜类', '蔬菜类描述', '2.jpg');
INSERT INTO `t_bigtype` VALUES (1, '谷类', '谷类描述', '1.jpg');

-- ----------------------------
-- Table structure for t_group_buying
-- ----------------------------
DROP TABLE IF EXISTS `t_group_buying`;
CREATE TABLE `t_group_buying`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `productId` int NOT NULL,
  `startTime` datetime NOT NULL,
  `endTime` datetime NOT NULL,
  `groupPrice` decimal(10, 2) NOT NULL,
  `requiredNumber` int NOT NULL,
  `joinedNumber` int NULL DEFAULT 0,
  `status` tinyint NULL DEFAULT 0 COMMENT '0: 进行中, 1: 成功, 2: 失败',
  `userId` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `product_id`(`productId` ASC) USING BTREE,
  CONSTRAINT `t_group_buying_ibfk_1` FOREIGN KEY (`productId`) REFERENCES `t_product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_group_buying
-- ----------------------------
INSERT INTO `t_group_buying` VALUES (1, 9, '2025-02-27 05:07:45', '2025-03-05 05:07:50', 3.30, 2, 1, 0, NULL);

-- ----------------------------
-- Table structure for t_group_buying_member
-- ----------------------------
DROP TABLE IF EXISTS `t_group_buying_member`;
CREATE TABLE `t_group_buying_member`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `groupBuyingId` int NOT NULL,
  `userId` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `joinTime` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `group_buying_id`(`groupBuyingId` ASC) USING BTREE,
  CONSTRAINT `t_group_buying_member_ibfk_1` FOREIGN KEY (`groupBuyingId`) REFERENCES `t_group_buying` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_group_buying_member
-- ----------------------------

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`  (
  `id` int NULL DEFAULT NULL,
  `orderNo` varchar(300) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `userId` varchar(600) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `totalPrice` decimal(10, 0) NULL DEFAULT NULL,
  `address` varchar(900) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `consignee` varchar(60) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `telNumber` varchar(60) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `createDate` datetime NULL DEFAULT NULL,
  `payDate` datetime NULL DEFAULT NULL,
  `status` int NULL DEFAULT NULL,
  `cancelDate` datetime NULL DEFAULT NULL COMMENT '订单取消时间',
  `isGroup` tinyint(1) NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_order
-- ----------------------------
INSERT INTO `t_order` VALUES (NULL, 'JAVA20250228054327000000536', 'oVl_J62i-LKmsaE0WK1OOgITDAwE', 141, '广东省广州市海珠区新港中路397号', '张三', '020-81167888', '2025-02-28 05:43:28', NULL, 1, NULL, 0);
INSERT INTO `t_order` VALUES (NULL, 'JAVA20250228054414000000307', 'oVl_J62i-LKmsaE0WK1OOgITDAwE', 4, '广东省广州市海珠区新港中路397号', '张三', '020-81167888', '2025-02-28 05:44:14', NULL, 1, NULL, 0);

-- ----------------------------
-- Table structure for t_order_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_order_detail`;
CREATE TABLE `t_order_detail`  (
  `id` int NULL DEFAULT NULL,
  `mId` int NULL DEFAULT NULL,
  `goodsId` int NULL DEFAULT NULL,
  `goodsNumber` int NULL DEFAULT NULL,
  `goodsPrice` decimal(10, 0) NULL DEFAULT NULL,
  `goodsName` varchar(300) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `goodsPic` varchar(765) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_order_detail
-- ----------------------------
INSERT INTO `t_order_detail` VALUES (NULL, NULL, 1, 1, 141, '本地绿色大米30kg/包', '1.jpg');
INSERT INTO `t_order_detail` VALUES (NULL, NULL, 9, 1, 4, '紫茄子1根', '9.jpg');

-- ----------------------------
-- Table structure for t_product
-- ----------------------------
DROP TABLE IF EXISTS `t_product`;
CREATE TABLE `t_product`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(300) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `stock` int NULL DEFAULT NULL COMMENT '库存',
  `proPic` varchar(765) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '图片',
  `ishot` bit(1) NULL DEFAULT NULL COMMENT '是否是热卖',
  `isSwiper` bit(1) NULL DEFAULT NULL COMMENT '是否是轮播图',
  `swiperPic` varchar(765) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '轮播图片地址',
  `swiperSort` int NULL DEFAULT NULL COMMENT '轮播图排序',
  `typeId` int NULL DEFAULT NULL,
  `hotDateTime` datetime NULL DEFAULT NULL,
  `description` varchar(6000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '商品描述',
  `groupPrice` decimal(10, 2) NULL DEFAULT NULL COMMENT '团购价格',
  `groupRequired` int NULL DEFAULT NULL COMMENT '团购所需人数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_product
-- ----------------------------
INSERT INTO `t_product` VALUES (1, '本地绿色大米30kg/包', 140.99, 1000, '1.jpg', b'1', b'1', '1.jpg', 1, 10, '2025-01-25 19:29:02', '本地绿色无害大米', 120.99, 5);
INSERT INTO `t_product` VALUES (2, '新鲜芥菜500g/份', 2.00, 423, '2.jpg', b'1', b'1', '2.jpg', 2, 1, '2025-01-25 19:29:07', '1dawd', 1.80, 3);
INSERT INTO `t_product` VALUES (3, '土猪肉500g/份', 31.00, 345, '3.jpg', b'1', b'1', '3.jpg', 3, 2, '2025-02-05 20:26:01', '3dasd', 28.00, 4);
INSERT INTO `t_product` VALUES (4, '土鸡蛋1枚', 0.65, 12, '4.jpg', b'1', b'0', 'default.jpg', 0, 3, '2025-02-15 03:14:45', 'asda', 0.60, 10);
INSERT INTO `t_product` VALUES (5, '农家西红柿1个', 2.20, 34, '5.jpg', b'0', b'1', '5.jpg', 4, 1, '2025-02-04 21:17:21', 'w\'re', 2.00, 6);
INSERT INTO `t_product` VALUES (6, '嫩豆腐一块', 1.20, 234, '6.jpg', b'1', b'0', 'default.jpg', 0, 4, '2025-02-15 03:14:51', 'da', 1.00, 8);
INSERT INTO `t_product` VALUES (7, '土豆200g', 2.30, 123, '7.jpg', b'1', b'0', 'default.jpg', 0, 1, '2025-02-15 03:14:55', 'dasd', 2.10, 7);
INSERT INTO `t_product` VALUES (8, '胡萝卜一根', 2.10, 222, '8.jpg', b'1', b'0', 'default.jpg', 0, 1, '2025-02-15 03:14:59', 'sadasd', 1.90, 9);
INSERT INTO `t_product` VALUES (9, '紫茄子1根', 3.60, 342, '9.jpg', b'1', b'0', 'default.jpg', 0, 1, '2025-02-15 03:15:16', 'asd', 3.30, 5);
INSERT INTO `t_product` VALUES (10, '新鲜娃娃菜1颗', 3.50, 341, '10.jpg', b'0', b'0', 'default.jpg', 0, 1, '2025-02-15 03:15:10', 'asd', 3.20, 4);
INSERT INTO `t_product` VALUES (11, '大蒜头50g', 2.00, 324, '11.jpg', b'0', b'0', 'default.jpg', 0, 1, '2025-02-15 03:15:04', 'das', 1.80, 6);

-- ----------------------------
-- Table structure for t_product_swiper_image
-- ----------------------------
DROP TABLE IF EXISTS `t_product_swiper_image`;
CREATE TABLE `t_product_swiper_image`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `image` varchar(765) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `sort` int NULL DEFAULT NULL,
  `productId` int NULL DEFAULT NULL,
  PRIMARY KEY (`id` DESC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_product_swiper_image
-- ----------------------------
INSERT INTO `t_product_swiper_image` VALUES (2, '2.jpg', 2, 1);
INSERT INTO `t_product_swiper_image` VALUES (1, '1.jpg', 1, 1);

-- ----------------------------
-- Table structure for t_smalltype
-- ----------------------------
DROP TABLE IF EXISTS `t_smalltype`;
CREATE TABLE `t_smalltype`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(150) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `remark` varchar(765) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `bigTypeId` int NULL DEFAULT NULL,
  PRIMARY KEY (`id` DESC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_smalltype
-- ----------------------------
INSERT INTO `t_smalltype` VALUES (14, '海鲜', '海鲜类', 8);
INSERT INTO `t_smalltype` VALUES (10, '大米', '大米', 10);
INSERT INTO `t_smalltype` VALUES (4, '豆类', '豆类', 3);
INSERT INTO `t_smalltype` VALUES (3, '蛋类', '蛋类', 6);
INSERT INTO `t_smalltype` VALUES (2, '新鲜肉类', '新鲜肉类', 6);
INSERT INTO `t_smalltype` VALUES (1, '本地蔬菜', '本地蔬菜', 2);

-- ----------------------------
-- Table structure for t_wxuserinfo
-- ----------------------------
DROP TABLE IF EXISTS `t_wxuserinfo`;
CREATE TABLE `t_wxuserinfo`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `openId` varchar(90) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `nickName` varchar(150) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `avatarUrl` varchar(600) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `registerDate` datetime NULL DEFAULT NULL,
  `lastLoginDate` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_wxuserinfo
-- ----------------------------
INSERT INTO `t_wxuserinfo` VALUES (1, 'oVl_J62i-LKmsaE0WK1OOgITDAwE', '31', 'https://thirdwx.qlogo.cn/mmopen/vi_32/icQUAujyG3h2w7ibmkjwU2QqwZxHX5gU2QF6YW7QZ8OUMEm0pEbaYK1N2YQMI3ySoodqyzoFKMfZou4oKXK8YsBg/132', '2025-02-08 03:43:33', '2025-02-28 05:49:34');

SET FOREIGN_KEY_CHECKS = 1;
