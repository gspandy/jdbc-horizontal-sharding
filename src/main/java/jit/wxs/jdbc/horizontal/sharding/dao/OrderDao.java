package jit.wxs.jdbc.horizontal.sharding.dao;

import jit.wxs.jdbc.horizontal.sharding.entiy.Order;

import java.sql.SQLException;

public interface OrderDao {
    long insertTransaction(Order order) throws SQLException;

    long insert(Order order);

    Order selectById(long orderId, int modulo);

    long removeAll(int modulo);
}
