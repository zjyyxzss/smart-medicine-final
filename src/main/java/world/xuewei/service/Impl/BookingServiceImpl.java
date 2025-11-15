package world.xuewei.service.Impl;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import world.xuewei.config.RabbitMQConfig;
import world.xuewei.dao.BookMapper;
import world.xuewei.dao.DoctorScheduleMapper;
import world.xuewei.dto.BookingMessage;
import world.xuewei.dto.RespResult;
import world.xuewei.entity.DoctorSchedule;
import world.xuewei.service.BookingService;

import world.xuewei.entity.BookingRecord;

import java.util.UUID;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;


/**
 * 预约服务实现类
 *
 *
 */
@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private DoctorScheduleMapper doctorScheduleMapper;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private static final String STOCK_KEY_PREFIX = "stock:schedule:";

    @Override
    public void initStock(Long scheduleId) {
        //1.从数据库中查询库存
        DoctorSchedule doctorSchedule = doctorScheduleMapper.selectById(scheduleId);
        if (doctorSchedule == null) {
            return;
        }
        //2.将库存写入Redis
        redisTemplate.opsForValue().set(STOCK_KEY_PREFIX + scheduleId, String.valueOf(doctorSchedule.getAvailableStock()));
        log.info("排班ID:"+scheduleId+ " 库存已预热到 Redis!");
    }
    @PostConstruct
    public void initAllStockOnStartup() {
        log.info("--- 正在执行所有库存预热 ---");
        initStock(1L);
        log.info("--- 库存预热完成 ---");
    }
    /**
     * 用户预约医生
     *
     * @param userId     用户ID
     * @param scheduleId 排班ID
     * @return 预约结果，包含成功或失败信息
     */

    @Override
    public RespResult book(Long userId, Long scheduleId) {
        String stockKey = STOCK_KEY_PREFIX + scheduleId;

        // 1. Redis 原子扣减库存
        Long currentStock = redisTemplate.opsForValue().decrement(stockKey);

        // 1.1 校验：检查 Key 是否存在 (currentStock == null)
        if (currentStock == null) {
            return RespResult.fail("排班不存在或库存未初始化！");
        }

        // 1.2 校验：检查是否抢完
        if (currentStock < 0) {
            redisTemplate.opsForValue().increment(stockKey); // 回滚 Redis 多扣的库存
            return RespResult.fail("号源已抢完！");
        }

        // 2. 【核心】构建消息 DTO
        BookingMessage message = new BookingMessage();
        message.setUserId(userId);
        message.setScheduleId(scheduleId);
        message.setRemainingStock(currentStock);

        // 3. 【核心】发送消息到 MQ，立即返回成功
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                message
        );
        // 4. 立即返回成功 (响应时间将低于 10ms)
        return RespResult.success("抢号成功！订单已进入异步队列处理。");
    }
}
