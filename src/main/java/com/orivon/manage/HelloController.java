package com.orivon.manage;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: api-limit-manage
 * @description: 初始测试
 * @author: yuowu
 * @create: 2026-04-03 15:44
 **/
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "恭喜！你的后端服务已经成功启动了！";
    }
}
