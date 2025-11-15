package world.xuewei.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorSchedule {
    private Integer id;
    private Integer doctorId;
    private Date scheduleDate;
    private Integer timeSlot;
    private Integer totalStock;
    private Integer availableStock;

    private Date gmtCreate;
    private Date gmtModified;
}
