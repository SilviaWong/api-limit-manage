package com.orivon.manage.controller;

import com.orivon.manage.common.Result;
import com.orivon.manage.model.entity.ApiAuth;
import com.orivon.manage.service.ApiAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class ApiAuthController {
    @Autowired
    private ApiAuthService apiAuthService;

    @GetMapping("/list")
    public Result<List<ApiAuth>> list() {
        return Result.success(apiAuthService.list());
    }

    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody ApiAuth apiAuth) {
        // Controller 变得极其清爽：只负责分发请求到业务层
        return Result.success(apiAuthService.createAuth(apiAuth));
    }

    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody ApiAuth apiAuth) {
        return Result.success(apiAuthService.updateById(apiAuth));
    }

    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Integer id) {
        return Result.success(apiAuthService.removeById(id));
    }
}
