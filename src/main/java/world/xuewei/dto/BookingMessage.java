package world.xuewei.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 挂号订单消息体 (用于在 RabbitMQ 中传输的核心数据)
 * 必须实现 Serializable 接口才能被 MQ 传输。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L; // 序列化 ID

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 排班ID (用于查询排班详情并执行数据库扣减)
     */
    private Long scheduleId;

    /**
     * Redis 扣减后的剩余库存 (主要用于日志或验证)
     */
    private Long remainingStock;
}