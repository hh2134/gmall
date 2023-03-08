package com.atguigu.gmall.pms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/*
* @SpringBootApplication    来标注一个主程序类，说明这是一个Spring Boot应用
* @EnableDiscoveryClient    注解是一个Spring Boot的注解，主要是用于发现 Eureka 注册中心的服务，它可以把当前应用注册到 Eureka Server，从而使服务消费者能够找到
* @EnableFeignClients       启动feign客户端
* @EnableSwagger2           是Springfox提供的一个注解，代表swagger2相关技术开启，会扫描当前类所在包，及子包中所有类型的swagger相关注解，做swagger文档定制
* @MapperScan               指定要变成实现类的接口所在的包，然后包下面的所有接口在编译后都会生成相应的实体类
 * */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableSwagger2
@MapperScan("com.atguigu.gmall.pms.mapper")
public class GmallPmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallPmsApplication.class, args);
    }

}
