package world.xuewei.service;


import world.xuewei.entity.Medicine;

import java.util.List;
import java.util.Map;

/**
 * 药品服务接口
 * 继承自定义的 IService
 */
public interface MedicineService extends IService<Medicine> { // <-- 确保继承 IService



    Map<String,?> getMedicineList(String nameValue, Integer page);
}