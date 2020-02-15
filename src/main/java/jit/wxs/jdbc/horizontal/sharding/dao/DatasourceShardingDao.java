package jit.wxs.jdbc.horizontal.sharding.dao;

import jit.wxs.jdbc.horizontal.sharding.entiy.DatasourceSharding;

import java.util.List;

/**
 * 分库配置信息
 * @author jitwxs
 * @date 2020年02月15日 16:45
 */
public interface DatasourceShardingDao {
    List<DatasourceSharding> listAllEnabled();
}
