package world.xuewei.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import world.xuewei.dto.RespResult;
import world.xuewei.entity.IllnessKind;
import world.xuewei.utils.Assert;

/**
 * 疾病分类控制器
 *
 *
 */
@RestController
@RequestMapping("illness_kind")
public class IllnessKindController extends BaseController<IllnessKind> {
    @GetMapping("/{id}")
    public RespResult getById(@PathVariable("id") Integer id) {
        IllnessKind illnessKind = service.getById(id);
        if (Assert.isEmpty(illnessKind)) {
            return RespResult.fail("查询失败");
        }
        return RespResult.success(String.valueOf(illnessKind));
    }

}
