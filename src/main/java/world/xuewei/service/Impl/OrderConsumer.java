package world.xuewei.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import world.xuewei.config.RabbitMQConfig;

import world.xuewei.dao.BookMapper;
import world.xuewei.dao.DoctorScheduleMapper;
import world.xuewei.dto.BookingMessage;
import world.xuewei.entity.BookingRecord;

import java.util.Date;
import java.util.UUID;

/**
 * Day 11 核心：MQ 消费者，异步处理订单写入
 */
@Slf4j
@Component
@RabbitListener(queues = {RabbitMQConfig.QUEUE_NAME}) // 监听订单队列
public class OrderConsumer {

    @Autowired
    private BookMapper bookingRecordDao;

    @Autowired
    private DoctorScheduleMapper doctorScheduleDao;


    private String generateBookingNo() {
        return "B" + UUID.randomUUID().toString().replace("-", "").substring(0, 18);
    }

    @RabbitHandler
    @Transactional
    public void processOrder(BookingMessage msg) {
        try {
            // 1. 数据库扣减库存 (乐观锁/行锁)
            // update doctor_schedule set available_stock = available_stock - 1 where id = ? and available_stock > 0
            int rows = doctorScheduleDao.decreaseStockBySql(msg.getScheduleId());

            if (rows > 0) {
                // 2. 插入订单记录
                BookingRecord record = new BookingRecord();
                record.setUserId(msg.getUserId().intValue());
                record.setScheduleId(msg.getScheduleId().intValue());
                record.setDoctorId(doctorScheduleDao.selectById(msg.getScheduleId()).getDoctorId().intValue());
                record.setAppointmentDate(doctorScheduleDao.selectById(msg.getScheduleId()).getScheduleDate());
                record.setAppointmentTimeSlot(doctorScheduleDao.selectById(msg.getScheduleId()).getTimeSlot());
                record.setStatus(1); // 状态设置为已确认/已完成
                record.setBookingNo(generateBookingNo());

                // 设置创建和修改时间
                Date now = new Date();
                record.setGmtCreate(now);
                record.setGmtModified(now);

                bookingRecordDao.insert(record);

                log.info("订单生成成功: {}", record.getBookingNo());
            } else {
                log.warn("数据库库存不足，扣减失败。可能出现Redis与DB不一致的情况，需人工排查。");
            }
        } catch (org.springframework.dao.DuplicateKeyException e) {
            // 这是预期的异常，说明该用户已经有订单了（幂等性处理）
            log.warn("重复消费/重复下单拦截: userId={}, scheduleId={}", msg.getUserId(), msg.getScheduleId());
        } catch (Exception e) {
            log.error("订单处理异常", e);
            // 抛出异常，让 MQ 重试（或进入死信队列）
            throw e;
        }
    }
}