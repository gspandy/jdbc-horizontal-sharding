package jit.wxs.jdbc.horizontal.sharding.dao;

import jit.wxs.jdbc.horizontal.sharding.entiy.User;

import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    /**
     * @return 主键ID
     */
    long insert(User user);

    /**
     * 使用封装好的batchInsert方法
     */
    long[] batchInsert1(List<User> userList);

    /**
     * 手动拼串
     */
    long batchInsert2(List<User> userList);

    long updatePhone(long userId, String phone);

    long updatePhoneTransaction(long userId, String phone) throws SQLException;

    List<User> listAll();

    User selectByUserId(long userId);

    long removeAll();
}
