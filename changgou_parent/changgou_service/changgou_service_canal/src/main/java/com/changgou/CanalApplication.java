package com.changgou;

import com.xpand.starter.canal.annotation.EnableCanalClient;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/17 11:27
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//禁止springboot自动注入数据源
@EnableEurekaClient
@EnableCanalClient//开启canal客户端
@EnableFeignClients//开启feign客户端
@EnableRabbit
public class CanalApplication {
    public static void main(String[] args) {
        SpringApplication.run(CanalApplication.class,args);
    }
}
