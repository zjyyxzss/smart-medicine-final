package world.xuewei.service.Impl;

// 1. 【必须】导入 @Service 注解
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

// 2. 导入我们 Day 6/7 需要的类
// (ES Repository)
import world.xuewei.dao.MedicineDao;
// (或叫 Medicine)

import world.xuewei.entity.Medicine;
import world.xuewei.service.BaseService;
import world.xuewei.service.MedicineService;


// 3. 导入 Day 7 的 ES 查询类

import world.xuewei.utils.Assert;
import world.xuewei.utils.BeanUtil;
import world.xuewei.utils.VariableNameUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service // <-- 4. 【核心】告诉 Spring 这是
public class MedicineServiceImpl extends BaseService<Medicine> implements MedicineService {

    // 5. 【核心】注入 Day 7 需要的组件
    // (BaseService 已经注入了所有的 DAO，但 ES 相关的需要在这里注入)

    @Autowired
    private MedicineDao medicineDao; // 确保你注入了 MySQL 的 DAO




    /**
     * 根据ID查询药品详情，并使用缓存
     *
     */
    @Override
    @Cacheable(value = "medicine", key = "#id")
    public Medicine getById(Serializable id) {
        log.info("正在从数据库查询药品详情,id={}", id);
        return medicineDao.selectById(id);
    }

    @Override
    public List<Medicine> query(Medicine o) {
        QueryWrapper<Medicine> wrapper = new QueryWrapper<>();
        if (Assert.notEmpty(o)) {
            Map<String, Object> bean2Map = BeanUtil.bean2Map(o);
            for (String key : bean2Map.keySet()) {
                if (Assert.isEmpty(bean2Map.get(key))) {
                    continue;
                }
                wrapper.eq(VariableNameUtils.humpToLine(key), bean2Map.get(key));
            }
        }
        return medicineDao.selectList(wrapper);
    }

    @Override
    public List<Medicine> all() {
        return query(null);
    }

    @Override
    public Medicine save(Medicine o) {
        if (Assert.isEmpty(o.getId())) {
            medicineDao.insert(o);
        } else {
            medicineDao.updateById(o);
        }
        return medicineDao.selectById(o.getId());
    }

    @Override
    public Medicine get(Serializable id) {
        return medicineDao.selectById(id);
    }

    @Override
    public int delete(Serializable id) {
        return medicineDao.deleteById(id);
    }

    /**
     * 获取药品列表，支持按名称、关键字或功效搜索并分页
     *
     * @param nameValue 搜索关键词，用于匹配药品名称、关键字或功效
     * @param page 页码，用于分页查询
     * @return 包含药品列表和总页数的Map对象
     */
    public Map<String, Object> getMedicineList(String nameValue, Integer page) {

        List<Medicine> medicineList;
        Map<String, Object> map = new HashMap<>(4);
        // 根据是否有搜索关键词执行不同的查询逻辑
        if (Assert.notEmpty(nameValue)) {
            // 如果有搜索关键词，则在药品名称、关键字或功效中进行模糊搜索
            medicineList = medicineDao.selectList(new QueryWrapper<Medicine>().
                    like("medicine_name", nameValue)
                    .or().like("keyword", nameValue)
                    .or().like("medicine_effect", nameValue)
                    .last("limit " + (page - 1) * 9 + "," + page * 9));
        } else {
            // 如果没有搜索关键词，则查询所有药品
            medicineList = medicineDao.selectList(new QueryWrapper<Medicine>()
                    .last("limit " + (page - 1) * 9 + "," + page * 9));
        }

        map.put("medicineList", medicineList);
        // 计算并存储总页数
        map.put("size", medicineList.size() < 9 ? 1 : medicineList.size() / 9 + 1);
        return map;
    }
}