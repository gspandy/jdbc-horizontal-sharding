package jit.wxs.jdbc.horizontal.sharding;

import jit.wxs.jdbc.horizontal.sharding.dao.UserDao;
import jit.wxs.jdbc.horizontal.sharding.entiy.User;
import jit.wxs.jdbc.horizontal.sharding.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 批量插入测试
 * @author jitwxs
 * @date 2020年02月15日 18:32
 */
@Slf4j
public class BatchInsertTest extends BaseTest {
    @Autowired
    private UserDao userDao;

    @Before
    @After
    public void clean() {
        userDao.removeAll();
    }

    /**
     * 1k条数据插入耗时PK，jdbc-url需开启：rewriteBatchedStatements=true
     * Db#batchInsert VS Db#update + 拼串
     * @author jitwxs
     * @date 2020/2/15 18:33
     */
    @Test
    public void testSpeed() {
        int count = 1000;
        List<User> userList = new ArrayList<>(count);
        IntStream.range(0, count).forEach((e) -> userList.add(User.builder()
                .username(RandomStringUtils.randomAlphabetic(5))
                .phone(RandomUtils.nextInt(1,1000)+"").build()));

        long start1Time = DateUtils.now();
        userDao.batchInsert1(userList);
        long end1Time = DateUtils.now();

        long start2Time = DateUtils.now();
        userDao.batchInsert2(userList);
        long end2Time = DateUtils.now();

        log.warn("batchInsert timeCost:{} || batchInsertSlow timeCost:{}", end1Time - start1Time, end2Time - start2Time);
    }
}
