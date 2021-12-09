package com.yang.dubbo.mysql;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @program: my-tanhua
 * @description:
 * @author: Mr.Yang
 * @create: 2021-12-03 21:00
 **/

@SpringBootApplication
@MapperScan("com.yang.dubbo.mysql.mapper")
@EnableDubbo
public class MySQLAPP {
    public static void main(String[] args) {
        SpringApplication.run(MySQLAPP.class, args);
    }
}
