package jit.wxs.jdbc.horizontal.sharding.entiy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author jitwxs
 * @date 2020年02月15日 19:00
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private long id;

    private long userId;

    private double amount;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;
}
