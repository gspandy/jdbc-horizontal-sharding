package jit.wxs.jdbc.horizontal.sharding.entiy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jitwxs
 * @date 2020年02月15日 19:15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDesc {
    private long id;

    private long orderId;

    private long userId;

    private String description;
}
