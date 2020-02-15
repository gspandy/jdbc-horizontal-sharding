package jit.wxs.jdbc.horizontal.sharding.util;

/**
 * @author jitwxs
 * @date 2020年02月15日 19:04
 */
public class ModuloUtils {
    /**
     * 用户ID对2取余+1，结果为 1 or 2
     * 将用户分发到 modulo1、modulo2 的数据库上
     */
    public static int getModulo(long userId) {
        return (int) (userId % 2 + 1);
    }
}
