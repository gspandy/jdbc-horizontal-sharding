SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS `hor-shard-center` default charset utf8 COLLATE utf8_general_ci;
USE `hor-shard-center`;

DROP TABLE IF EXISTS `datasource_sharding`;
CREATE TABLE `datasource_sharding`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `server_type` int(11) NULL DEFAULT NULL COMMENT '数据库类型',
  `server_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `modulo` int(255) NULL DEFAULT NULL COMMENT '模',
  `enable` tinyint(2) NULL DEFAULT 1 COMMENT '是否启用，1：启用；0：禁用',
  `created_date` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

INSERT INTO `datasource_sharding` VALUES (1, 1, 'db_center', NULL, 1, '2020-02-15 16:41:30');
INSERT INTO `datasource_sharding` VALUES (2, 2, 'db_ds1_master', 1, 1, '2020-02-15 16:41:50');
INSERT INTO `datasource_sharding` VALUES (3, 3, 'db_ds1_slave', 1, 1, '2020-02-15 16:42:05');
INSERT INTO `datasource_sharding` VALUES (4, 2, 'db_ds2_master', 2, 1, '2020-02-15 16:42:15');
INSERT INTO `datasource_sharding` VALUES (5, 3, 'db_ds1_slave', 2, 1, '2020-02-15 16:42:26');

CREATE TABLE `user` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `phone` varchar(32) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8;

CREATE DATABASE IF NOT EXISTS `hor-shard-ds1` default charset utf8 COLLATE utf8_general_ci;
USE `hor-shard-ds1`;

CREATE TABLE `order_1` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `amount` decimal(16,8) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8;

CREATE TABLE `order_desc_1` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=50000 DEFAULT CHARSET=utf8;


CREATE DATABASE IF NOT EXISTS `hor-shard-ds2` default charset utf8 COLLATE utf8_general_ci;
USE `hor-shard-ds2`;

CREATE TABLE `order_2` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `amount` decimal(16,8) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8;

CREATE TABLE `order_desc_2` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `order_id` bigint(20) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=50000 DEFAULT CHARSET=utf8;