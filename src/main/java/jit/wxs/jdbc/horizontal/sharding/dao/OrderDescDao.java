package jit.wxs.jdbc.horizontal.sharding.dao;

import jit.wxs.jdbc.horizontal.sharding.entiy.OrderDesc;

import java.sql.SQLException;

public interface OrderDescDao {
    long insertTransaction(OrderDesc orderDesc) throws SQLException;

    long removeAll(int modulo);

    OrderDesc selectById(long id, int modulo);
}
