package jit.wxs.jdbc.horizontal.sharding.entiy;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author jitwxs
 * @date 2020年02月15日 16:42
 */
@Data
public class DatasourceSharding {
    private Integer id;
    /**
     * 数据库类型
     */
    private Integer serverType;
    /**
     * 需和jit.wxs.jdbc.horizontal.sharding.config.DataSourceConfig中Bean名相同
     */
    private String serverName;
    /**
     * 模
     */
    private Integer modulo;
    /**
     * 是否启用，1：启用；0：禁用
     */
    private boolean enable;

    private LocalDateTime createdDate;
}
