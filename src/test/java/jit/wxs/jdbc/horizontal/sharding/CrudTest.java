package jit.wxs.jdbc.horizontal.sharding;

import jit.wxs.jdbc.horizontal.sharding.dao.UserDao;
import jit.wxs.jdbc.horizontal.sharding.entiy.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 单表CRUD测试
 * @author jitwxs
 * @date 2020年02月15日 17:55
 */
@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CrudTest extends BaseTest {
    @Autowired
    private UserDao userDao;

    /**
     * 插入操作
     * @author jitwxs
     * @date 2020/2/16 17:42
     */
    @Before
    public void test1Insert() {
        String username = "zhangsan";

        User user = User.builder().username(username).phone("110").build();
        long id = userDao.insert(user);

        User user1 = userDao.selectByUserId(id);
        Assert.assertNotNull(user1);
        Assert.assertEquals(username, user1.getUsername());
    }

    /**
     * 更新操作
     * @author jitwxs
     * @date 2020/2/16 17:43
     */
    @Test
    public void test2Update() {
        List<User> users = userDao.listAll();
        User user = users.stream().filter(e -> "zhangsan".equals(e.getUsername())).findFirst().orElse(null);
        Assert.assertNotNull(user);

        long l = userDao.updatePhone(user.getId(), "119");
        Assert.assertEquals(1, l);

        User user1 = userDao.selectByUserId(user.getId());
        Assert.assertNotNull(user1);
        Assert.assertEquals("119", user1.getPhone());
    }

    /**
     * 删除操作
     * @author jitwxs
     * @date 2020/2/16 17:43
     */
    @After
    public void test3Remove() {
        userDao.removeAll();
        List<User> users = userDao.listAll();
        Assert.assertEquals(0, users.size());
    }
}
