## 基于原生 JDBC 的水平分表实现

## 一、起步

### 1.1 SQL

运行 `rescources/db.sql` 文件，创建数据库并初始化表：

（1）主库 hor-shard-center

- `datasource_sharding`: 分库配置表
- `user`: 用户信息表

（2）分库 hor-shard-ds1

- `order_1`：订单表
- `order_desc_1`：订单详情表

（2）分库 hor-shard-ds2

- `order_2`：订单表
- `order_desc_2`：订单详情表

### 1.2 配置

修改 `rescources/application.yml` 文件，修改各数据库的用户名和密码即可。

### 2.3 运行

执行 `test` 目录下的测试方法。

## 二、介绍

### 2.1 modulo

在本项目中，以用户 ID 作为分片维度，只分了2个分片，每个分片对应一个数据库。即：

```
数据库 ds1 --> modulo1
数据库 ds2 --> modulo2
```

`modulo` 获取规则为：

```
modulo = userId % (moduloCount) + 1
```

举个例子，若 moduloCount = 2，则：

```
- user16 = modulo1
- user15 = modulo2
```

每个分片拥有独立的业务表，也就是项目中的 `order_{modulo}` 表和 `order_desc_{modulo}` 表。

### 2.2 modulo 与数据库对应

2 个数据库并不意味着只能分两个分片，想分多少就分多少，64、128 个都没有问题，只需要配置 `datasource_sharding` 表即可。

| server_type | server_name   | modulo |
| ----------- | ------------- | ------ |
| 1           | db_center     |        |
| 2           | db_ds1_master | 1      |
| 3           | db_ds1_slave  | 1      |
| ...         | ...           | ...    |

`server_type` 表示数据库类型，1 代表中央库，2 代表主分库，3 代表从分库。【之所以分主/从分库，为后续做数据库的读写分离预留。】

`server_name` 值必须与 `jit.wxs.jdbc.horizontal.sharding.config.DataSourceConfig`中注入的对应 Bean 名相同。

`modulo` 表示该数据库处理的分片。

## 三、运行

目前编写了以下 Test 方法：

| ClassName        | Description  |
| ---------------- | ------------ |
| CrudTest         | 单表增删改查 |
| ShardingTest     | 分片测试     |
| BatchInsertTest  | 批量插入测试 |
| TransractionTest | 事务测试     |

