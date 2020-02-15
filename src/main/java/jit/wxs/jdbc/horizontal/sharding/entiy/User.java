package jit.wxs.jdbc.horizontal.sharding.entiy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author jitwxs
 * @date 2020年02月15日 17:43
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private long id;

    private String username;

    private String phone;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;
}
