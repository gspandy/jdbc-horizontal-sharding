package jit.wxs.jdbc.horizontal.sharding.dao.impl;

import jit.wxs.jdbc.horizontal.sharding.dao.DatasourceShardingDao;
import jit.wxs.jdbc.horizontal.sharding.datasource.Db;
import jit.wxs.jdbc.horizontal.sharding.datasource.DbAssist;
import jit.wxs.jdbc.horizontal.sharding.datasource.ShardingContext;
import jit.wxs.jdbc.horizontal.sharding.entiy.DatasourceSharding;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class DatasourceShardingDaoImpl implements DatasourceShardingDao {
    private static String buildTable() {
        return "datasource_sharding";
    }

    @Override
    public List<DatasourceSharding> listAllEnabled() {
        String sql = "SELECT * FROM " + buildTable() + " WHERE enable = 1";
        List<Map<String, Object>> list = Db.select(sql, null, ShardingContext.CENTER);

        return DbAssist.convert(list, DatasourceSharding.class);
    }
}
