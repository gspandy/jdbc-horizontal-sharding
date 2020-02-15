package jit.wxs.jdbc.horizontal.sharding.datasource;

import jit.wxs.jdbc.horizontal.sharding.constant.Symbol;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author jitwxs
 * @date 2020年02月15日 13:34
 */
@Slf4j
@Getter
@Setter
public class Server {
    private Type type;
    private Integer modulo;
    private String name;
    private DataSource dataSource;

    private final ThreadLocal<Connection> connThreadLocal = new ThreadLocal<>();
    private static final Map<String, Server> INSTANCE_MAP = new HashMap<>();

    private Server(Type type, Integer modulo, String name, DataSource dataSource) {
        this.type = type;
        this.name = name;
        this.dataSource = dataSource;
        if(isShardingAware(type)) {
            this.modulo = modulo;
        }
    }

    public static void addInstance(Type type, Integer modulo, String name, DataSource dataSource) {
        Server server = new Server(type, modulo, name, dataSource);

        String key = buildInstanceMapKey(type, modulo);
        INSTANCE_MAP.put(key, server);
        log.info("Db Sharding: {} --> {}", key, ((com.zaxxer.hikari.HikariDataSource)dataSource).getJdbcUrl());
    }

    public static Server getInstance(Type type, Integer modulo) {
        return INSTANCE_MAP.get(buildInstanceMapKey(type, modulo));
    }

    public static String getServerName(Type type, Integer modulo) {
        Server server = INSTANCE_MAP.get(buildInstanceMapKey(type, modulo));
        return Objects.nonNull(server) ? server.getName() : StringUtils.EMPTY;
    }

    private static String buildInstanceMapKey(Type type, Integer modulo) {
        String key = type.name();
        if(isShardingAware(type)) {
            key = key + Symbol.UNDERLINE + modulo;
        }
        return key;
    }

    /**
     * 是否是需要分库的类型
     */
    private static boolean isShardingAware(Type type) {
        return type == Type.MASTER || type == Type.SLAVE;
    }

    @Getter
    @AllArgsConstructor
    public enum Type {
        /**
         * 中央库
         */
        CENTER(1),
        /**
         * 主库
         */
        MASTER(2),
        /**
         * 从库
         */
        SLAVE(3);

        private int code;

        public static Type fetch(int code) {
            return Arrays.stream(Type.values()).filter(e -> e.getCode() == code).findFirst().orElse(null);
        }
    }
}
