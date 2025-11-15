package world.xuewei.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import world.xuewei.entity.Doctor;

/**
 * 医生数据库访问
 *
 *
 */
@Repository
public interface DoctorMapper extends BaseMapper<Doctor> {
}
