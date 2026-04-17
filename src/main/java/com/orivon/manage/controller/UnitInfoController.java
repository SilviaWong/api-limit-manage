package com.orivon.manage.controller;

import com.orivon.manage.common.Result;
import com.orivon.manage.model.entity.UnitInfo;
import com.orivon.manage.service.UnitInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/unit")
public class UnitInfoController {
    @Autowired
    private UnitInfoService unitInfoService;

    @GetMapping("/list")
    public Result<List<UnitInfo>> list() {
        return Result.success(unitInfoService.list());
    }

    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody UnitInfo unitInfo) {
        return Result.success(unitInfoService.save(unitInfo));
    }

    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody UnitInfo unitInfo) {
        return Result.success(unitInfoService.updateById(unitInfo));
    }

    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Integer id) {
        return Result.success(unitInfoService.removeById(id));
    }
}
