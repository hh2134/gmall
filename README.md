# gmall
谷粒商品商城项目

## 1.创建github远程代码仓库

ssh：

git@github.com:hh2134/gmall.git

https：

https://github.com/hh2134/gmall.git

## 2.创建gmall项目

将 **gmall-admin** 和 **gmall-common** 复制到 **gmall** 项目中

添加忽略 **gmall/.gitignore**

```
.idea/
target/
*.iml
mvnw*
.mvn/


# Compiled class file
*.class

# Log file
*.log

# BlueJ files
*.ctxt

# Mobile Tools for Java (J2ME)
.mtj.tmp/

# Package Files #
*.jar
*.war
*.nar
*.ear
*.zip
*.tar.gz
*.rar

# virtual machine crash logs, see http://www.java.com/en/download/help/error_hotspot.xml
hs_err_pid*

```

## 3.框架

基础框架

SSM redis mysql rabbitmq
web启动器：Spring SpringMVC
MP启动器：mybatis mybatis-plus
mysql驱动
redis启动器

架构依赖

springboot springCloud
nacos-discovery启动器
nacos-config启动器
sentinel启动器
feign启动器
zipkin启动器
gateway启动器

## 4.搭建5个ms

gmall-pms：谷粒商城商品管理系统

添加依赖：

- Spring Web
- MySQL Driver
- Spring Data Redis(Access+Driver)
- Zipkin
- OpenFign

gmall-ums：谷粒商城用户管理系统

添加依赖：

- Spring Web
- MySQL Driver
- Spring Data Redis(Access+Driver)
- OpenFign

gmall-oms：谷粒商城订单管理系统

添加依赖：

- Spring Web
- MySQL Driver
- Spring Data Redis(Access+Driver)
- OpenFign

gmall-wms：谷粒商城仓储管理系统

添加依赖：

- Spring Web
- MySQL Driver
- Spring Data Redis(Access+Driver)
- OpenFign

gmall-sms：谷粒商城营销管理系统

添加依赖：

- Spring Web
- MySQL Driver
- Spring Data Redis(Access+Driver)
- OpenFign

## 5.启动的服务

Nacos启动，在bin目录下cmd，运行 `startup.cmd -m standalone` 访问地址 `localhost:8848/nacos` 登陆账户 `nacos` 密码 `nacos` 

sentinel启动，在根目录下cmd，运行 `java -jar sentinel-dashboard-1.8.2.jar` 

zipkin启动，在根目录下cmd，运行 `java -jar zipkin-server-2.20.2-exec.jar`

Mysql启动，在虚拟机启动mysql8.0版本的mysql，运行 `systemctl start mysqld` 远程连接后，导入sql文件

## 6.以pms为例搭建模块

1. 依赖问题：pom.xml

   以自己的父工程作为父工程

   删除 `properties` 标签

   修改依赖 `mysql-connector-j` 改为 `mysql-connector-java`

   手动引入依赖

   - gmall-common依赖：

     `gmall-common`

   - mybatis-plus启动器：

     `mybatis-plus-boot-starter`

   - nacos-discovery启动器：

     `spring-cloud-starter-alibaba-nacos-discovery`

   - nacos-config启动器：

     `spring-cloud-starter-alibaba-nacos-config`

   - sentinel启动器：

     `spring-cloud-starter-alibaba-sentinel`

   - zipkin启动器：

     `spring-cloud-starter-zipkin`

2. 配置文件

删除 **gmall-pms/src/main/resources/application.properties**

新建 **gmall-pms/src/main/resources/application.yml**

application.yml

```yaml
server:
# 配置该服务的端口号
  port: 18081
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
    sentinel:
      transport:
#       sentinel的连接地址
        dashboard: localhost:8080
        port: 8719
  zipkin:
#   zipkin的连接地址
    base-url: http://localhost:9411
    discovery-client-enabled: false
    sender:
      type: web
  sleuth:
    sampler:
#     采集率设置为1，指100%
      probability: 1
# 配置数据源
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://192.168.44.100:3306/guli_pms
    username: root
    password: MySQL8.0.32
# redis连接
  redis:
    host: 192.168.44.100
    port: 6379
#feign哨兵启动
feign:
  sentinel:
#   开启
    enabled: true
mybatis-plus:
# mybatis-plus的写sql语句的xml地址
  mapper-locations: classpath:mapper/pms/**/*.xml
# 别名扫描配置包路径
  type-aliases-package: com.atguigu.gmall.pms.entity
  global-config:
    db-config:
#     配置mybatis-plus的id策略默认是分布式ID自动自增
      id-type: auto
```

新建 **gmall-pms/src/main/resources/bootstrap.yml**

bootstrap.yml

```yaml
spring:
  application:
    name: pms-service
  cloud:
    nacos:
      config:
#       nacos的连接地址
        server-addr: localhost:8848
#       登陆nacos，在命名空间中新建，命名空间名称：pms，描述：谷粒商城商品管理系统，复制所生成的命名空间ID
#       nacos的pms的命名空间ID
        namespace: d6a420a1-e34e-4980-aec3-1b8afa451bd8
#       分组：dev
        group: dev
#       文件扩展名：yml格式
        file-extension: yml
```

## 7.在pms启动类上添加注解

**gmall-pms/src/main/java/com/atguigu/gmall/pms/GmallPmsApplication.java**

GmallPmsApplication.java

```java
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

```

## 8.使用逆向工程生成代码

将逆向工程 **gmall-generator** 复制到项目中

修改 **gmall-generator/src/main/resources/application.yml**

```yaml
# 原代码
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    #MySQL配置
    driverClassName: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://172.16.116.100:3306/guli_oms?useUnicode=true&characterEncoding=UTF-8&useSSL=false
    username: root
    password: root

# 修改后
# mysql
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    #MySQL配置
    driverClassName: com.mysql.cj.jdbc.Driver
#    修改远程连接mysql的ip，修改guli_oms改成需要生成代码的数据库名
    url: jdbc:mysql://192.168.44.100:3306/guli_pms?useUnicode=true&characterEncoding=UTF-8&useSSL=false
    username: root
#    修改远程mysql的密码
    password: MySQL8.0.32
```

修改 **gmall-generator/src/main/resources/generator.properties**

```yaml
#修改前
mainPath=com.atguigu
#\u5305\u540D
package=com.atguigu.gmall
moduleName=oms
#\u4F5C\u8005
author=eyvren
#Email
email=eyvren@atguigu.com
#\u8868\u524D\u7F00(\u7C7B\u540D\u4E0D\u4F1A\u5305\u542B\u8868\u524D\u7F00)
tablePrefix=oms_

# 修改后
mainPath=com.atguigu
#包名
package=com.atguigu.gmall
#修改成需要生成代码的数据库名
moduleName=pms
#作者
author=eyvren
#Email
email=eyvren@atguigu.com
#mysql表前缀（类名不会包含前缀）
#修改成需要生成代码的数据库名前缀
tablePrefix=pms_
```

启动逆向工程，访问连接 `localhost:80` 全选->生成代码（会自动下载）

将生成的代码

- **guli\main\java\com\atguigu\gmall\pms** 下的包复制到 **gmall-pms/src/main/java/com/atguigu/gmall/pms** 包下

- **guli\main\resources** 下的 **mapper** 包复制到 **gmall-pms/src/main/resources** 包下

## 9.测试连接gmall-pms

启动 **gmall-pms** 访问地址 `http://localhost:18081/pms/brand` 

显示

```json
{
    "code": 0,
    "msg": null,
    "data": {
        "pageSize": 10,
        "pageNum": 1,
        "total": 0,
        "totalPage": 0,
        "list": [
            {
                "id": 1,
                "name": "尚硅谷",
                "logo": "http://www.atguigu.com/images/pic_new/logo.png",
                "status": 1,
                "firstLetter": "S",
                "sort": 1,
                "remark": null
            },
            {
                "id": 2,
                "name": "华为",
                "logo": "http://www-file.huawei.com/-/media/corporate/images/home/logo/huawei_logo.png",
                "status": 1,
                "firstLetter": "H",
                "sort": 2,
                "remark": null
            },
            {
                "id": 3,
                "name": "小米",
                "logo": "http://s02.mifile.cn/assets/static/image/mi-logo.png",
                "status": 1,
                "firstLetter": "X",
                "sort": 3,
                "remark": null
            }
        ]
    }
}
```

其中 `"total": 0,` 总记录数应该为3， `"totalPage": 0,` 总页数应该为1

在 **gmall-pms/src/main/java/com/atguigu/gmall/pms/config** 写入mybatis-plus分页插件，直接从官方文档中copy

MybatisPlusConfig.java

```java
package com.atguigu.gmall.pms.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("scan.your.mapper.package")
public class MybatisPlusConfig {

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.H2));
        return interceptor;
    }

}
```

再次运行，显示正确 `"total": 3,` 总记录数为3， `"totalPage": 1,` 总页数为1

## 10.完成sms的pom以及配置

1. gmall-sms/pom.xml

2. 将 `spring-boot-starter-parent` 改成父类依赖 `gmall`

3. 删除 `properties` 标签

4. 引入 `gmall-common` 依赖

5. 引入 `mybatis-plus-boot-starter` 启动器

6. 引入 `spring-cloud-starter-alibaba-nacos-discovery` 依赖

7. 引入 `spring-cloud-starter-alibaba-nacos-config` 依赖

8. 引入 `spring-cloud-starter-alibaba-sentinel` 依赖

9. 引入 `spring-cloud-starter-zipkin` 依赖

10. 将 `mysql-connector-j` 依赖改成 `mysql-connector-java` 依赖

11. 删除 `dependencyManagement` 标签

12. 删除 **gmall-sms/src/main/resources/application.properties** 文件

13. 新建 **gmall-sms/src/main/resources/application.yml**

    application.yml

    ```yaml
    server:
      port: 18082
    spring:
      cloud:
        nacos:
          discovery:
            server-addr: localhost:8848
        sentinel:
          transport:
            dashboard: localhost:8080
    #       端口号被占用的情况下，会自动递增
            port: 8719
      zipkin:
        base-url: http://localhost:9411
        discovery-client-enabled: false
        sender:
          type: web
      sleuth:
        sampler:
          probability: 1
      datasource:
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://192.168.44.100:3306/guli_sms
        username: root
        password: MySQL8.0.32
      redis:
        host: 192.168.44.100
        port: 6379
    feign:
      sentinel:
        enabled: true
    mybatis-plus:
      mapper-locations: classpath:mapper/sms/**/*.xml
      type-aliases-package: com.atguigu.gmall.sms.entity
      global-config:
        db-config:
          id-type: auto
    ```

14. 新建 **gmall-sms/src/main/resources/bootstrap.yml**

    bootstrap.yml

    ```yaml
    spring:
      application:
        name: sms-service
      cloud:
        nacos:
          config:
            server-addr: localhost:8848
    #       nacos新建sms命名空间
            namespace: 3d468a45-2c49-4fb4-ba07-dd965353f326
            group: dev
            file-extension: yml
    ```

15. 将pms中的分页插件 **gmall-pms/src/main/java/com/atguigu/gmall/pms/config/MybatisPlusConfig.java** 复制到 **gmall-sms/src/main/java/com/atguigu/gmall/sms** 中

16. 给启动类 **gmall-sms/src/main/java/com/atguigu/gmall/sms/GmallSmsApplication.java** 添加注解

    ```java
    @EnableDiscoveryClient
    @EnableFeignClients
    @EnableSwagger2
    @MapperScan("com.atguigu.gmall.sms.mapper")
    ```

17. 使用逆向工程生成 `gmall-sms` 代码，将生成的代码

    - **guli\main\java\com\atguigu\gmall\pms** 下的包复制到 **gmall-pms/src/main/java/com/atguigu/gmall/pms** 包下

    - **guli\main\resources** 下的 **mapper** 包复制到 **gmall-sms/src/main/resources** 包下

## 11.搭建网关工程

gmall-gateway：谷粒商城网关系统

添加依赖：

- Gateway

1. 将 `spring-boot-starter-parent` 改成父类依赖 `gmall`

2. 删除 `properties` 标签

3. 引入 `spring-cloud-starter-alibaba-nacos-discovery` 依赖

4. 引入 `spring-cloud-starter-alibaba-nacos-config` 依赖

5. 删除 `dependencyManagement` 标签

6. 删除 **gmall-gateway/src/main/resources/application.properties** 文件

7. 新建 **gmall-sms/src/main/resources/application.yml**

   application.yml

   ```yaml
   
   ```

8. 新建 **gmall-sms/src/main/resources/bootstrap.yml**

   bootstrap.yml

   ```yaml
   spring:
     application:
       name: gateway-api
     cloud:
       nacos:
         config:
           server-addr: localhost:8848
   #       在nacos中新建命名空间
           namespace: 49950b7f-310c-47e9-a82d-554a1d218a28
           group: dev
           file-extension: yml
   ```

9. 

   

   































 
