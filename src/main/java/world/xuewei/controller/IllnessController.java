package world.xuewei.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import world.xuewei.dto.RespResult;
import world.xuewei.entity.Illness;
import world.xuewei.utils.Assert;


/**
 * 疾病控制器
 *
 *
 */
@RestController
@RequestMapping("illness")
public class IllnessController extends BaseController<Illness> {

    @GetMapping("/{id}")
    public RespResult getById(@PathVariable("id") Integer id) {
        Illness illness = service.getById(id);
        if (Assert.isEmpty(illness)) {
            return RespResult.fail("查询失败");
        }
        return RespResult.success(String.valueOf(illness));
    }

}
