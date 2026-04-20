package com.orivon.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import org.mybatis.spring.annotation.MapperScan;

@EnableAsync
@SpringBootApplication
@MapperScan("com.orivon.manage.mapper")
public class ApiLimitManageApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiLimitManageApplication.class, args);
	}

}
