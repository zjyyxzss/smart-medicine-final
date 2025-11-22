package world.xuewei.service.Impl;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import world.xuewei.config.RabbitMQConfig;
import world.xuewei.dao.DoctorScheduleMapper;
import world.xuewei.dto.BookingMessage;
import world.xuewei.dto.RespResult;
import world.xuewei.entity.DoctorSchedule;
import world.xuewei.service.BookingService;


import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private DoctorScheduleMapper doctorScheduleMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String STOCK_KEY_PREFIX = "stock:schedule:";
    private static final String ORDER_HISTORY_PREFIX = "order:history:";

    // 定义 Lua 脚本对象
    private DefaultRedisScript<Long> seckillScript;

    // 初始化时加载 Lua 脚本，避免每次请求都重新解析，提升效率
    @PostConstruct
    public void init() {
        seckillScript = new DefaultRedisScript<>();

        String scriptText =
                "if redis.call('sismember', KEYS[2], ARGV[1]) == 1 then return -1 end " +
                        "local stock = tonumber(redis.call('get', KEYS[1])) " +
                        "if (stock == nil) then return -3 end " +
                        "if (stock <= 0) then return -2 end " +
                        "redis.call('decr', KEYS[1]) " +
                        "redis.call('sadd', KEYS[2], ARGV[1]) " +
                        "return 1";
        seckillScript.setScriptText(scriptText);
        seckillScript.setResultType(Long.class);

        // 顺便做一次安全的预热
        initAllStockOnStartup();
    }

    /**
     * 安全的库存预热：仅当 Redis 中没有 key 时才从数据库读取
     */
    public void initAllStockOnStartup() {
        log.info("--- 检查库存预热状态 ---");
        // 假设我们要预热 ID 为 1 的排班
        Long scheduleId = 1L;
        String stockKey = STOCK_KEY_PREFIX + scheduleId;

        // 使用 setIfAbsent (NX)，如果 Key 存在则不操作，防止重启覆盖库存！
        Boolean hasKey = redisTemplate.hasKey(stockKey);
        if (!hasKey) {
            DoctorSchedule schedule = doctorScheduleMapper.selectById(scheduleId);
            if (schedule != null) {
                redisTemplate.opsForValue().set(stockKey, String.valueOf(schedule.getAvailableStock()));
                log.info("排班ID: {} 库存已加载到 Redis", scheduleId);
            }
        } else {
            log.info("排班ID: {} Redis库存已存在，跳过预热，避免覆盖脏数据", scheduleId);
        }
    }


    @Override
    public RespResult book(Long userId, Long scheduleId) {
        String stockKey = STOCK_KEY_PREFIX + scheduleId;
        String historyKey = ORDER_HISTORY_PREFIX + scheduleId;

        // 1. 执行 Lua 脚本 (原子操作：查重 + 查库存 + 扣减 + 记录)
        // keys: [stockKey, historyKey]
        // args: [userId]
        List<String> keys = Arrays.asList(stockKey, historyKey);
        Long result = redisTemplate.execute(seckillScript, keys, String.valueOf(userId));

        // 2. 根据 Lua 返回值处理结果
        if (result == null) {
            return RespResult.fail("系统繁忙，请重试");
        }
        if (result == -1) {
            return RespResult.fail("您已预约过，请勿重复抢单！"); // 拦截黄牛/手抖
        }
        if (result == -2) {
            return RespResult.fail("非常遗憾，号源已抢完！");
        }
        if (result == -3) {
            return RespResult.fail("预约未开始（库存未初始化）");
        }

        // 3. 此时 result == 1，说明 Redis 扣减成功且拿到了资格
        // 发送消息到 MQ 异步写入数据库
        BookingMessage message = new BookingMessage();
        message.setUserId(userId);
        message.setScheduleId(scheduleId);
        // 此时不需要传 remainingStock 给 Consumer，Consumer 只管 -1 即可

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY,
                    message
            );
        } catch (Exception e) {
            // 如果 MQ 发送失败，必须在 Redis 里把库存加回去，并移除购买记录
            log.error("MQ发送失败，回滚Redis库存", e);
            redisTemplate.opsForValue().increment(stockKey);
            redisTemplate.opsForSet().remove(historyKey, String.valueOf(userId));
            return RespResult.fail("网络拥堵，抢号失败，请重试");
        }

        return RespResult.success("抢号成功！系统正在生成订单...");
    }

    @Override
    public void initStock(Long scheduleId) {

    }
}