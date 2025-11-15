package world.xuewei.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import world.xuewei.dto.RespResult;
import world.xuewei.entity.IllnessMedicine;
import world.xuewei.utils.Assert;


/**
 * 疾病药品控制器
 *
 *
 */
@RestController
@RequestMapping("illness_medicine")
public class IllnessMedicineController extends BaseController<IllnessMedicine> {
    @GetMapping("/{id}")
    public RespResult getById(@PathVariable("id") Integer id) {
        IllnessMedicine illnessMedicine = service.getById(id);
        if (Assert.isEmpty(illnessMedicine)) {
            return RespResult.fail("查询失败");
        }
        return RespResult.success(String.valueOf(illnessMedicine));
    }

}
