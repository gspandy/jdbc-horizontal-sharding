package jit.wxs.jdbc.horizontal.sharding.datasource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jitwxs
 * @date 2020年02月15日 13:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShardingContext {
    private Server.Type type;
    private Integer modulo;

    public static ShardingContext master(int modulo) {
        return new ShardingContext(Server.Type.MASTER, modulo);
    }

    public static ShardingContext slave(int modulo) {
        return new ShardingContext(Server.Type.SLAVE, modulo);
    }

    public static final ShardingContext CENTER = new ShardingContext(Server.Type.CENTER, null);
}
