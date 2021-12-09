package com.yang.appserver;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @program: my-tanhua
 * @description:
 * @author: Mr.Yang
 * @create: 2021-12-03 21:16
 **/
@SpringBootApplication
public class ServerAPP {
    public static void main(String[] args) {
        SpringApplication.run(ServerAPP.class, args);
    }
}
