package jit.wxs.jdbc.horizontal.sharding.dao.impl;

import jit.wxs.jdbc.horizontal.sharding.dao.OrderDescDao;
import jit.wxs.jdbc.horizontal.sharding.datasource.Db;
import jit.wxs.jdbc.horizontal.sharding.datasource.DbAssist;
import jit.wxs.jdbc.horizontal.sharding.datasource.ShardingContext;
import jit.wxs.jdbc.horizontal.sharding.entiy.Order;
import jit.wxs.jdbc.horizontal.sharding.entiy.OrderDesc;
import jit.wxs.jdbc.horizontal.sharding.util.ModuloUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author jitwxs
 * @date 2020年02月15日 19:04
 */
@Repository
public class OrderDescDaoImpl implements OrderDescDao {
    private static String buildTable(int modulo) {
        return "order_desc_" + modulo;
    }

    @Override
    public long insertTransaction(OrderDesc orderDesc) throws SQLException {
        int modulo = ModuloUtils.getModulo(orderDesc.getUserId());

        String sql = "INSERT INTO " + buildTable(modulo) + " (user_id, order_id, description) VALUES (?,?,?)";
        Object[] objects = {orderDesc.getUserId(), orderDesc.getOrderId(), orderDesc.getDescription()};
        return Db.updateTransaction(sql, objects, ShardingContext.master(modulo));
    }

    @Override
    public long removeAll(int modulo) {
        String sql = "DELETE FROM " + buildTable(modulo) + " WHERE 1 = 1";
        return Db.update(sql, null, ShardingContext.master(modulo));
    }

    @Override
    public OrderDesc selectById(long id, int modulo) {
        String sql = "SELECT * FROM " + buildTable(modulo) + " WHERE id = ?";
        List<Map<String, Object>> mapList = Db.query(sql, new Object[]{id}, ShardingContext.master(modulo));
        return CollectionUtils.isEmpty(mapList) ? null : DbAssist.convert(mapList.get(0), OrderDesc.class);
    }
}
