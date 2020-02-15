package jit.wxs.jdbc.horizontal.sharding.config;

import jit.wxs.jdbc.horizontal.sharding.dao.DatasourceShardingDao;
import jit.wxs.jdbc.horizontal.sharding.datasource.Server;
import jit.wxs.jdbc.horizontal.sharding.entiy.DatasourceSharding;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author jitwxs
 * @date 2020年02月15日 16:11
 */
@Slf4j
@Configuration
public class ServerConfig implements InitializingBean {
    @Autowired
    private DataSourceConfig dataSourceConfig;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private DatasourceShardingDao datasourceShardingDao;

    /**
     * 先加载Center库分库信息，然后根据信息加载具体分库
     * @author jitwxs
     * @date 2020/2/15 17:27
     */
    @Override
    public void afterPropertiesSet() {
        Server.addInstance(Server.Type.CENTER, null, "db_center", dataSourceConfig.getDbCenter());
        addServers(datasourceShardingDao.listAllEnabled());
    }

    private void addServers(List<DatasourceSharding> list) {
        if(CollectionUtils.isNotEmpty(list)) {
           list.forEach(e -> {
               DataSource dataSource = (DataSource) context.getBean(e.getServerName());
               Server.addInstance(Server.Type.fetch(e.getServerType()), e.getModulo(), e.getServerName(), dataSource);
           });
        }
    }
}
