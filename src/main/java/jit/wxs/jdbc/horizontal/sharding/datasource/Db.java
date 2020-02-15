package jit.wxs.jdbc.horizontal.sharding.datasource;

import jit.wxs.jdbc.horizontal.sharding.constant.Symbol;
import jit.wxs.jdbc.horizontal.sharding.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jitwxs
 * @date 2020年02月15日 13:56
 */
@Slf4j
public class Db {
    /**
     * 更新操作
     * @author jitwxs
     * @date 2020/2/15 14:20
     */
    public static long update(String sql, Object[] params, ShardingContext context) {
        if(isIllegalSql(sql)) {
            return 0;
        }

        PreparedStatement statement = null;
        Connection conn;
        try {
            conn = getConn(context);

            boolean isInsert = false;
            if(sql.contains("insert") || sql.contains("INSERT")) {
                isInsert = true;
            }
            statement = isInsert ? conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS) : conn.prepareStatement(sql);

            if(ArrayUtils.isNotEmpty(params)) {
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }
            }

            return executeUpdate(statement, isInsert);
        } catch (Exception e) {
            String message = e.getMessage();
            if(StringUtils.isNotEmpty(message) && message.contains("Duplicate entry")) {
                log.error("Duplicate entry, error sql: {}, params: {}", sql, DbAssist.buildParams(params));
            } else {
                log.error("Db#update execute error, server: {}, sql: {}, params:{}",
                        Server.getServerName(context.getType(), context.getModulo()), sql, DbAssist.buildParams(params), e);
            }
            return -1;
        } finally {
            closeStatement(statement);
            releaseContext(context);
        }
    }

    /**
     * 更新操作（事务）
     * @author jitwxs
     * @date 2020/2/15 14:20
     */
    public static long updateTransaction(String sql, Object[] params, ShardingContext context) throws SQLException {
        if(isIllegalSql(sql)) {
            return 0;
        }

        PreparedStatement statement = null;
        Connection conn;

        try {
            conn = getConnTransaction(context);

            boolean isInsert = false;
            if(sql.contains("insert") || sql.contains("INSERT")) {
                isInsert = true;
            }
            statement = isInsert ? conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS) : conn.prepareStatement(sql);

            if(ArrayUtils.isNotEmpty(params)) {
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }
            }
            return executeUpdate(statement, isInsert);
        } finally {
            closeStatement(statement);
        }
    }

    private static long executeUpdate(PreparedStatement statement, boolean isInsert) throws SQLException {
        if(isInsert) {
            long result = statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            long id = 0;
            if(keys.next()) {
                id = keys.getLong(1);
            }
            if(id != 0) {
                result = id;
            }
            return result;
        } else {
            return statement.executeUpdate();
        }
    }

    /**
     * 批量插入操作
     * @author jitwxs
     * @date 2020/2/15 14:20
     */
    public static long[] batchInsert(String sql, List<Object[]> params, ShardingContext context) {
        try {
            return batchInsert(sql, params, context, false);
        } catch (Exception e) {
            String paramStr = params.stream().map(DbAssist::buildParams).collect(Collectors.joining(Symbol.COMMA));
            log.error("batchInsert error sql: {}, param: {}", sql, paramStr, e);
            return null;
        }
    }

    /**
     * 批量插入操作（事务）
     * @author jitwxs
     * @date 2020/2/15 14:20
     */
    public static long[] batchInsertTransaction(String sql, List<Object[]> params, ShardingContext context) throws Exception {
        return batchInsert(sql, params, context, true);
    }

    private static long[] batchInsert(String sql, List<Object[]> params, ShardingContext context, boolean isTx) throws Exception {
        if(isIllegalSql(sql) || !sql.contains("?")) {
            return null;
        }

        int i = 0;
        int rowCount = params.size();
        long[] keys = new long[rowCount];
        Connection conn;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            conn = isTx ? getConnTransaction(context) : getConn(context);
            statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            for (Object[] objects : params) {
                int idx = 1;
                for (Object object : objects) {
                    statement.setObject(idx, object);
                    idx++;
                }
                statement.addBatch();
            }
            statement.executeBatch();
            rs = statement.getGeneratedKeys();
            while (rs.next() && i < rowCount) {
                keys[i] = rs.getLong(Statement.RETURN_GENERATED_KEYS);
                i++;
            }
        } finally {
            closeResultSet(rs);
            closeStatement(statement);
            if (!isTx) {
                releaseContext(context);
            }
        }
        return keys;
    }

    /**
     * 查询操作
     * @author jitwxs
     * @date 2020/2/15 15:55
     */
    public static List<Map<String, Object>> query(String sql, Object[] params, ShardingContext context) {
        if(isIllegalSql(sql)) {
            return Collections.emptyList();
        }

        PreparedStatement statement = null;
        Connection conn;
        ResultSet resultSet = null;
        try {
            long startTime = DateUtils.now();
            conn = getConn(context);
            long connectTime = DateUtils.now() - startTime;

            statement = conn.prepareStatement(sql);

            if(ArrayUtils.isNotEmpty(params)) {
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }
            }

            resultSet = statement.executeQuery();

            long endTime = DateUtils.now();
            if ((endTime - startTime) > 2000) {
                log.warn("Db#query execute too long, server: {}, costTime:{}, connectTime:{}, sql: {}, params:{}",
                        Server.getServerName(context.getType(), context.getModulo()), endTime - startTime, connectTime,
                        sql, DbAssist.buildParams(params));
            }

            return DbAssist.getResultMap(resultSet);

        } catch (Exception e) {
            log.error("Db#query error, sql:{}, server:{}, params:{}", sql,
                    Server.getServerName(context.getType(), context.getModulo()), DbAssist.buildParams(params), e);

            return Collections.emptyList();
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            releaseContext(context);
        }
    }

    /**
     * 查询操作（事务）
     * @author jitwxs
     * @date 2020/2/15 15:55
     */
    public static List<Map<String, Object>> queryTransaction(String sql, Object[] params, ShardingContext context) throws SQLException {
        if(isIllegalSql(sql)) {
            return Collections.emptyList();
        }

        PreparedStatement statement = null;
        Connection conn;
        ResultSet resultSet = null;
        try {
            long startTime = DateUtils.now();
            conn = getConnTransaction(context);
            long connectTime = DateUtils.now() - startTime;

            statement = conn.prepareStatement(sql);

            if(ArrayUtils.isNotEmpty(params)) {
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }
            }

            resultSet = statement.executeQuery();

            long endTime = DateUtils.now();
            if ((endTime - startTime) > 2000) {
                log.warn("Db#queryTransaction execute too long, server: {}, costTime:{}, connectTime:{}, sql: {}, params:{}",
                        Server.getServerName(context.getType(), context.getModulo()), endTime - startTime, connectTime,
                        sql, DbAssist.buildParams(params));
            }

            return DbAssist.getResultMap(resultSet);
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
        }
    }

    /**
     * 获取数据库连接
     * @author jitwxs
     * @date 2020/2/15 14:05
     */
    private static Connection getConn(ShardingContext context) throws SQLException {
        Server server = getServer(context);

        ThreadLocal<Connection> threadLocal = server.getConnThreadLocal();
        Connection connection = threadLocal.get();
        if(connection == null || connection.isClosed()) {
            connection = getPoolConnection(server);
            threadLocal.set(connection);
        }

        return connection;
    }

    /**
     * 获取数据库连接（事务）
     * @author jitwxs
     * @date 2020/2/15 14:05
     */
    private static Connection getConnTransaction(ShardingContext context) throws SQLException {
        long startTime = DateUtils.now();
        Server server = getServer(context);

        ThreadLocal<Connection> threadLocal = server.getConnThreadLocal();
        Connection connection = threadLocal.get();
        if(connection == null || connection.isClosed()) {
            log.error("Db#getConnTransaction connection is null or closed, Server is {}", server.getName());
            throw new SQLException("getConnTransaction connection is null or closed");
        }

        long costTime = DateUtils.now() - startTime;
        if(costTime > 5) {
            log.warn("Db#getConnTransaction too long, costTime:{}, modulo:{}", costTime, context.getModulo());
        }
        return connection;
    }

    public static void beginTransaction(ShardingContext context) throws SQLException {
        long startTime = DateUtils.now();
        Connection conn = getConn(context);
        long endTime = DateUtils.now();
        conn.setAutoCommit(false);

        long timeCost = endTime - startTime;
        if (timeCost > 2000) {
            log.warn("Db#beginTransaction too long, server: {}, timeCost:{}",
                    Server.getServerName(context.getType(), context.getModulo()), timeCost);
        }
    }

    public static void commitTransaction(ShardingContext context) {
        Connection conn;
        try {
            conn = getConnTransaction(context);
            conn.commit();
            conn.setAutoCommit(true);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException("Db#commitTransaction error " + ex.getMessage());
        } finally {
            releaseContext(context);
        }
    }

    public static void rollbackTransaction(ShardingContext context) {
        Connection conn;
        try {
            conn = getConnTransaction(context);
            conn.rollback();
            conn.setAutoCommit(true);
        } catch (Exception e) {
            log.error("Db#rollbackTransaction error", e);
        } finally {
            releaseContext(context);
        }
    }

    private static Server getServer(ShardingContext context) {
        return Server.getInstance(context.getType(), context.getModulo());
    }

    private static Connection getPoolConnection(Server server) throws SQLException {
        return server.getDataSource().getConnection();
    }

    private static void closeStatement(PreparedStatement statement) {
        if(statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.error("close preparedStatement error", e);
            }
        }
    }

    private static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.error("close resultSet error", e);
            }
        }
    }

    private static void releaseContext(ShardingContext context) {
        ThreadLocal<Connection> threadLocal = getServer(context).getConnThreadLocal();
        Connection conn = threadLocal.get();
        if(conn != null) {
            try {
                if(!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.error("releaseContext error", e);
            }
        }
        threadLocal.set(null);
    }

    private static boolean isIllegalSql(String sql) {
        if(StringUtils.isEmpty(sql)) {
            return true;
        }
        if(!DbAssist.checkSql(sql)) {
            log.error("danger sql not execute: " + sql);
            return true;
        }
        return false;
    }
}
