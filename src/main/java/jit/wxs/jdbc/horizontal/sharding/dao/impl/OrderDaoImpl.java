package jit.wxs.jdbc.horizontal.sharding.dao.impl;

import jit.wxs.jdbc.horizontal.sharding.dao.OrderDao;
import jit.wxs.jdbc.horizontal.sharding.datasource.Db;
import jit.wxs.jdbc.horizontal.sharding.datasource.DbAssist;
import jit.wxs.jdbc.horizontal.sharding.datasource.ShardingContext;
import jit.wxs.jdbc.horizontal.sharding.entiy.Order;
import jit.wxs.jdbc.horizontal.sharding.entiy.User;
import jit.wxs.jdbc.horizontal.sharding.util.ModuloUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author jitwxs
 * @date 2020年02月15日 19:11
 */
@Repository
public class OrderDaoImpl implements OrderDao {
    private static String buildTable(int modulo) {
        return "order_" + modulo;
    }

    @Override
    public long insertTransaction(Order order) throws SQLException {
        int modulo = ModuloUtils.getModulo(order.getUserId());

        String sql = "INSERT INTO " + buildTable(modulo) + " (user_id, amount, created_date) VALUES (?,?,now())";
        Object[] objects = {order.getUserId(), order.getAmount()};
        return Db.updateTransaction(sql, objects, ShardingContext.master(modulo));
    }

    @Override
    public long insert(Order order) {
        int modulo = ModuloUtils.getModulo(order.getUserId());

        String sql = "INSERT INTO " + buildTable(modulo) + " (user_id, amount, created_date) VALUES (?,?,now())";
        Object[] objects = {order.getUserId(), order.getAmount()};
        return Db.update(sql, objects, ShardingContext.master(modulo));
    }

    @Override
    public Order selectById(long orderId, int modulo) {
        String sql = "SELECT * FROM " + buildTable(modulo) + " WHERE id = ?";
        List<Map<String, Object>> mapList = Db.query(sql, new Object[]{orderId}, ShardingContext.master(modulo));
        return CollectionUtils.isEmpty(mapList) ? null : DbAssist.convert(mapList.get(0), Order.class);
    }

    @Override
    public long removeAll(int modulo) {
        String sql = "DELETE FROM " + buildTable(modulo) + " WHERE 1 = 1";
        return Db.update(sql, null, ShardingContext.master(modulo));
    }
}
