package world.xuewei.service;



import world.xuewei.dto.RespResult;


/**
 * 预约服务
 *
 *
 */
public interface BookingService  {
/**
 * 执行挂号
 * @param userId 用户ID
 * @param scheduleId 排班ID
 * @return 挂号结果
 */

RespResult book(Long userId, Long scheduleId);

void initStock(Long scheduleId);
}
