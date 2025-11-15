package world.xuewei.service.Impl;

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
    @Transactional // 【核心】数据库操作必须有事务保护
    public void processOrder(BookingMessage msg) {

        // 1. 【核心】执行数据库扣减（最终一致性兜底）
        // 再次使用 SQL 原子更新，确保最终数据库库存的准确性
        int rows = doctorScheduleDao.decreaseStockBySql(msg.getScheduleId());

        if (rows == 0) {
            // 如果 SQL 扣减失败 (理论上不应发生)，可能是 Redis 误判或数据不一致，需要日志记录
            System.err.println("异步订单处理失败: SQL 扣减库存失败, scheduleId=" + msg.getScheduleId());
            // 此时，消息会被自动 NACK (拒绝) 并重试或进入死信队列 (DLQ)
            return;
        }

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

        System.out.println("--- 订单 " + record.getBookingNo() + " 异步写入数据库成功！---");
    }
}