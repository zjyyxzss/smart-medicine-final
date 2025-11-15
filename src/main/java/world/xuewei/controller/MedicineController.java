package world.xuewei.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import world.xuewei.dto.RespResult;
import world.xuewei.entity.Medicine;
import world.xuewei.service.BaseService;
import world.xuewei.service.MedicineService;

import world.xuewei.service.MedicineService;

import java.util.List;


/**
 * 药品控制器
 *
 *
 */
@RestController
@RequestMapping("medicine")
public class MedicineController extends BaseController<Medicine> {
@Autowired
private MedicineService medicineService;


    @GetMapping("/{id}")
    public RespResult getById(@PathVariable("id") Integer id) {
        Medicine medicine = medicineService.getById(id);
        if (medicine == null) {
            return RespResult.notFound();
        }
        return RespResult.success(String.valueOf(medicine));
    }





}
