package world.xuewei.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import world.xuewei.dto.RespResult;
import world.xuewei.entity.History;
import world.xuewei.utils.Assert;


/**
 * 历史控制器
 *
 *
 */
@RestController
@RequestMapping("history")
public class HistoryController extends BaseController<History> {
    @GetMapping("/{id}")
    public RespResult getById(@PathVariable("id") Integer id) {
        History history = service.getById(id);
        if (Assert.isEmpty(history)) {
            return RespResult.fail("查询失败");
        }
        return RespResult.success(String.valueOf(history));
    }

}
