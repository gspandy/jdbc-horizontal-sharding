package jit.wxs.jdbc.horizontal.sharding.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @author jitwxs
 * @date 2020年02月15日 13:27
 */
@Getter
@Setter
@Configuration
public class DataSourceConfig {
    @Value("${datasource.type}")
    private Class<? extends DataSource> dataSourceType;

    @Primary
    @Bean("db_center")
    @ConfigurationProperties(prefix = "datasource.db.center")
    public DataSource getDbCenter() {
        return DataSourceBuilder.create().type(this.dataSourceType).build();
    }

    @Bean("db_ds1_master")
    @ConfigurationProperties(prefix = "datasource.db.ds1-master")
    public DataSource getDbDs1Master() {
        return DataSourceBuilder.create().type(this.dataSourceType).build();
    }

    @Bean("db_ds1_slave")
    @ConfigurationProperties(prefix = "datasource.db.ds1-slave")
    public DataSource getDbDs1Slave() {
        return DataSourceBuilder.create().type(this.dataSourceType).build();
    }

    @Bean("db_ds2_master")
    @ConfigurationProperties(prefix = "datasource.db.ds2-master")
    public DataSource getDbDs2Master() {
        return DataSourceBuilder.create().type(this.dataSourceType).build();
    }

    @Bean("db_ds2_slave")
    @ConfigurationProperties(prefix = "datasource.db.ds2-slave")
    public DataSource getDbDs2Slave() {
        return DataSourceBuilder.create().type(this.dataSourceType).build();
    }
}
