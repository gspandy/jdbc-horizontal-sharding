package jit.wxs.jdbc.horizontal.sharding;

import jit.wxs.jdbc.horizontal.sharding.dao.OrderDao;
import jit.wxs.jdbc.horizontal.sharding.dao.UserDao;
import jit.wxs.jdbc.horizontal.sharding.entiy.Order;
import jit.wxs.jdbc.horizontal.sharding.entiy.User;
import jit.wxs.jdbc.horizontal.sharding.util.ModuloUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 拆分测试
 * @author jitwxs
 * @date 2020年02月15日 18:54
 */
public class ShardingTest extends BaseTest {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private UserDao userDao;

    @Test
    public void testInsert() {
        User user = User.builder().username(RandomStringUtils.randomAscii(4)).phone(RandomStringUtils.randomNumeric(5)).build();
        long userId = userDao.insert(user);

        double amount = RandomUtils.nextDouble(3, 6);
        Order order = Order.builder().userId(userId).amount(amount).build();
        long orderId = orderDao.insert(order);

        int modulo = ModuloUtils.getModulo(userId);
        Order order1 = orderDao.selectById(orderId, modulo);
        Assert.assertNotNull(order1);
        Assert.assertEquals(amount, order1.getAmount(), 0.000001);
    }
}
