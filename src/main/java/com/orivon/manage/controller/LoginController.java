package com.orivon.manage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orivon.manage.common.Result;
import com.orivon.manage.common.ResultCode;
import com.orivon.manage.model.entity.SysUser;
import com.orivon.manage.service.SysUserService;
import com.orivon.manage.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sys")
public class LoginController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            return Result.error(ResultCode.BAD_REQUEST, "用户名或密码不能为空");
        }

        SysUser user = sysUserService.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .eq(SysUser::getPassword, password)
                .eq(SysUser::getStatus, 1));

        if (user == null) {
            return Result.error(ResultCode.UNAUTHORIZED, "用户名或密码错误，或账号已被禁用");
        }

        // 生成 Token
        String token = JwtUtil.generateToken(username);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());

        return Result.success("登录成功", data);
    }
}
