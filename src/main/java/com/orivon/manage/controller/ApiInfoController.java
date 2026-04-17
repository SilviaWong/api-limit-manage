package com.orivon.manage.controller;

import com.orivon.manage.common.Result;
import com.orivon.manage.model.entity.ApiInfo;
import com.orivon.manage.service.ApiInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description: 接口管理
 * @author: yuowu
 * @create: 2026-04-03 16:35
 **/
@RestController
@RequestMapping("/api")
public class ApiInfoController {

    @Autowired
    private ApiInfoService apiInfoService;

    // 1. 获取所有接口列表
    @GetMapping("/list")
    public Result<List<ApiInfo>> list() {
        return Result.success(apiInfoService.list());
    }

    // 2. 根据 ID 获取接口详情
    @GetMapping("/get/{id}")
    public Result<ApiInfo> getById(@PathVariable Integer id) {
        ApiInfo apiInfo = apiInfoService.getById(id);
        if (apiInfo == null) {
            return Result.error("未找到 ID 为 " + id + " 的接口信息");
        }
        return Result.success(apiInfo);
    }

    // 3. 新增一个接口
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody ApiInfo apiInfo) {
        boolean saved = apiInfoService.save(apiInfo);
        return saved ? Result.success("接口新增成功", true) : Result.error("接口新增失败");
    }

    // 4. 更新接口信息
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody ApiInfo apiInfo) {
        if (apiInfo.getId() == null) {
            return Result.error("更新操作必须提供 ID");
        }
        boolean updated = apiInfoService.updateById(apiInfo);
        return updated ? Result.success("接口更新成功", true) : Result.error("接口更新失败");
    }

    // 5. 删除接口
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Integer id) {
        boolean removed = apiInfoService.removeById(id);
        return removed ? Result.success("接口已成功删除", true) : Result.error("接口删除失败或不存在");
    }
}
