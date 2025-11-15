package world.xuewei.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import world.xuewei.entity.DoctorSchedule;

/**
 * 医生排班数据库访问
 *
 *
 */
@Repository
public interface DoctorScheduleMapper extends BaseMapper<DoctorSchedule> {


    @Update("UPDATE doctor_schedule " +
            "SET available_stock = available_stock - 1 " +
            "WHERE id = #{scheduleId} AND available_stock > 0")
    int decreaseStockBySql(@Param("scheduleId") Long scheduleId);
}
