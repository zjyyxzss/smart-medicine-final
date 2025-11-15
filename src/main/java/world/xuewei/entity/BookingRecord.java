package world.xuewei.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("booking_record")
public class BookingRecord implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer doctorId;
    private String bookingNo;
    private Integer status;
    private Integer scheduleId;
    private Date appointmentDate;
    private String department;
    private String doctorName;
    private Integer appointmentTimeSlot;
    private Date gmtCreate;
    private Date gmtModified;

}