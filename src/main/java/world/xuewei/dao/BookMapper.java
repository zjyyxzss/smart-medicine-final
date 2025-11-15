package world.xuewei.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import world.xuewei.entity.BookingRecord; // 确保导入正确的实体

@Mapper
@Repository
public interface BookMapper extends BaseMapper<BookingRecord> {

}