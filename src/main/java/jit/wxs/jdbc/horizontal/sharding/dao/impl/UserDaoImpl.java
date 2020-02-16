package jit.wxs.jdbc.horizontal.sharding.dao.impl;

import jit.wxs.jdbc.horizontal.sharding.dao.UserDao;
import jit.wxs.jdbc.horizontal.sharding.datasource.Db;
import jit.wxs.jdbc.horizontal.sharding.datasource.DbAssist;
import jit.wxs.jdbc.horizontal.sharding.datasource.ShardingContext;
import jit.wxs.jdbc.horizontal.sharding.entiy.User;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jitwxs
 * @date 2020年02月15日 17:47
 */
@Repository
public class UserDaoImpl implements UserDao {
    private static String buildTable() {
        return "user";
    }

    @Override
    public long insert(User user) {
        String sql = "INSERT INTO " + buildTable() + "(username, phone, created_date) VALUES(?,?,now())";
        return Db.update(sql, new Object[]{user.getUsername(), user.getPhone()}, ShardingContext.CENTER);
    }

    @Override
    public long[] batchInsert1(List<User> userList) {
        String sql = "INSERT INTO " + buildTable() + "(username, phone, created_date) VALUES(?,?,now())";
        List<Object[]> params = new ArrayList<>(userList.size());
        userList.forEach(e -> params.add(new Object[]{e.getUsername(), e.getPhone()}));

        return Db.batchInsert(sql, params, ShardingContext.CENTER);
    }

    @Override
    public long batchInsert2(List<User> userList) {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("INSERT INTO ").append(buildTable()).append("(username, phone, created_date) VALUES ");
        for(User user : userList) {
            sb.append("(")
                    .append("'").append(user.getUsername()).append("'").append(",")
                    .append("'").append(user.getPhone()).append("'").append(",")
                    .append("now()")
                    .append("),");
        }
        sb.deleteCharAt(sb.length() -1);
        return Db.update(sb.toString(), null, ShardingContext.CENTER);
    }

    @Override
    public long updatePhone(long userId, String phone) {
        String sql = "UPDATE " + buildTable() + " SET phone = ? WHERE id = ?";
        return Db.update(sql, new Object[]{phone, userId}, ShardingContext.CENTER);
    }

    @Override
    public long updatePhoneTransaction(long userId, String phone) throws SQLException {
        String sql = "UPDATE " + buildTable() + " SET phone = ? WHERE id = ?";
        return Db.updateTransaction(sql, new Object[]{phone, userId}, ShardingContext.CENTER);
    }

    @Override
    public List<User> listAll() {
        String sql = "SELECT * FROM " + buildTable() + " WHERE 1 = 1";
        List<Map<String, Object>> mapList = Db.query(sql,null, ShardingContext.CENTER);
        return DbAssist.convert(mapList, User.class);
    }

    @Override
    public User selectByUserId(long userId) {
        String sql = "SELECT * FROM " + buildTable() + " WHERE id = ?";
        List<Map<String, Object>> mapList = Db.query(sql, new Object[]{userId}, ShardingContext.CENTER);
        return CollectionUtils.isEmpty(mapList) ? null : DbAssist.convert(mapList.get(0), User.class);
    }

    @Override
    public long removeAll() {
        String sql = "DELETE FROM " + buildTable() + " WHERE 1 = 1";
        return Db.update(sql, null, ShardingContext.CENTER);
    }
}
