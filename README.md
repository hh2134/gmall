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

Mysql启动，在虚拟机启动mysql8.0版本的mysql，运行 `systemctl start mysqld` 访问链接 `192.168.44.100` 端口 `3306` 登陆账号 `root` 密码 `MySQL8.0.32` 远程连接后，导入sql文件

Tomcat启动，在虚拟机启动apache-tomcat-7.0.70，运行 `/software/apache-tomcat-7.0.70/bin/shutdown.sh` 访问地址 `192.168.44.100:8080`

Nginx启动，在虚拟机进入 `/usr/local/nginx/sbin` 目录,执行 `./nginx`，版本Nginx-1.12.2访问地址 `192.168.44.100:80`

node启动，在node目录中启动cmd，输入 `node server.js`，版本Node-v12.15.0

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

启动 **GmallPmsApplication** 访问地址 `http://localhost:18081/pms/brand` 

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

18. 测试连接启动 **GmallSmsApplication** 

    `http://localhost:18082/sms/skubounds`

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
   server:
     port: 8888
   spring:
     cloud:
       nacos:
         discovery:
           server-addr: localhost:8848
       gateway:
         routes:
           - id: pms-route
             uri: lb://pms-service
             predicates:
               - Path=/pms/**
           - id: sms-route
             uri: lb://sms-service
             predicates:
               - Path=/sms/**
           - id: oms-route
             uri: lb://oms-service
             predicates:
               - Path=/oms/**
           - id: wms-route
             uri: lb://wms-service
             predicates:
               - Path=/wms/**
           - id: ums-route
             uri: lb://ums-service
             predicates:
               - Path=/ums/**
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

9. 测试连接启动 **GmallGatewayApplication** & **GmallSmsApplication**

   `http://localhost:8888/sms/skubounds`

## 12.通过nginx解决端口问题

1. 在 **C:\Windows\System32\drivers\etc** 目录下配置 **hosts**

   hosts

   ```
   192.168.44.100 api.gmall.com manager.gmall.com www.gmall.com
   ```

2. 在物理机获取ip，按win+r键，输入 `cmd`，执行 `ipconfig` 

   ```
   以太网适配器 VMware Network Adapter VMnet1:
   
      连接特定的 DNS 后缀 . . . . . . . :
      本地链接 IPv6 地址. . . . . . . . : fe80::4c94:7d21:d476:15a3%16
      IPv4 地址 . . . . . . . . . . . . : 192.168.12.1
      子网掩码  . . . . . . . . . . . . : 255.255.255.0
   ```

   获取到VMware Network Adapter VMnet1的IPv4地址

   `192.168.12.1`

   VMware Network Adapter VMnet1的ip不易发生改变，其他的IPv4会因为连接的网络不同而发生改变

3. 进入虚拟机，在 **/usr/local/nginx/conf** 目录下，修改 **nginx.conf** 

   nginx.conf

   ```
   worker_processes  1;
   events {
       worker_connections  1024;
   }
   
   
   http {
       include       mime.types;
       default_type  application/octet-stream;
       sendfile        on;
       keepalive_timeout  65;
   
       server {
           listen       80;
           server_name  api.gmall.com;
           location / {
               proxy_pass http://192.168.12.1:8888;
           }
           
       }
   
       server{
           listen       80;
           server_name  manager.gmall.com;
           location / {
               proxy_pass http://192.168.12.1:1000;
           }
       }
   }
   ```

流程

![nginx加入之后的请求处理流程](image/nginx加入之后的请求处理流程.jpg)

## 13.前后联调

1. 启动前端工程gmall-admin，中的 **前端工程/gmall-admin**

2. 在 **前端工程/gmall-admin** 目录中，输入cmd，输入 `npm start`

   测试连接：

   `localhost:1000`

3. 修改 **gmall-admin/src/main/resources/application.yml** 中redis的连接配置

4. 修改 **gmall-admin/src/main/resources/application-dev.yml** 中mysql的连接配置及连接账户和密码

5. 启动后端工程gmall-admin，项目中的 **gmall/gmall-admin**，启动 **GmallAdminApplication**

   测试连接：

   `localhost:1000`

   账户：admin密码：admin

## 14.跨域问题

![跨域问题](image/跨域问题.png)

访问品牌管理失败

![image-20230309120414016](image/image-20230309120414016.png)

从连接“http://manager.gmall.com”访问“http://api.gmall.com/pms/brand?t=1678334331159&pageNum=1&pageSize=10&key=”的XMLHttpRequest已被CORS策略阻止:对预检请求的响应没有通过访问控制检查:所请求的资源上没有'Access- control - allow - origin '标头。

![image-20230309120339167](image/image-20230309120339167.png)

跨域：浏览器的同源策略导致的 

http://api.gmall.com:10010/pms/brand

1. 协议不同：http https
2. 二级域名不同：api.gmall.com manager.gmall.com
3. 一级域名不同：gmall.com jd.com tmall.com
4. 端口号不同：10010 10086

> 路径不同不属于跨域

跨域并不总是有跨域问题，跨域问题是浏览器针对ajax请求的一种限制

解决方案

1. jsonp：把动态数据伪装成静态资源以骗过浏览器
   1. 只能解决get请求的跨域问题
   2. 前后端开发人员的密切配合

2. nginx：反向代理为同一个域名

3. cors规范：w3c提供的解决规范，需要服务器端配置白名单（域名 请求方式 头信息 是否可用携带cooke）

   原理：两次请求

   - 一次预检请求：
     - 请求方法：OPTIONS
     - 只有预检请求通过，才会发送真正的请求
     - 响应标头失败
       - Access-Control-Request-Headers: token
       - Access-Control-Request-Method: GET
       - Origin: http://manager.gmall.com
     - 响应标头成功
       - Access-Control-Allow-Credentials: true
       - Access-Control-Allow-Headers: token
       - Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS
       - Access-Control-Allow-Origin: http://manager.gmall.com
       - Access-Control-Max-Age: 3600

   - 一次真正请求

玩法：制作白名单

1. nginx：通过cors实现跨域，配置 **nginx.conf**，将

   ```
   add_header Access-Control-Allow-Origin '*';  
   add_header Access-Control-Allow-Credentials "true";
   add_header Access-Control-Allow-Methods 'GET, POST, OPTIONS';
   add_header Access-Control-Allow-Headers  'token,DNT,X-Mx-ReqToken,Keep-Alive,User-Agent,XRequested-With';
   ```

   放入server{}中

   缺点：违背了devops思想

2. 网关配置：在 **application.yml** 配置 `spring.cloud.gateway.globalcors.cors-cofigurations`

   ![image-20230309142513517](image/image-20230309142513517.png)

   需要新建Map<K,V>

   K存这类：Access-Control-Allow-Credentials

   V存这类：true

   缺点：语义不明

3. 网关通过过滤器：

   CorsWebFilter（适用与gateway）

   CorsFilter（适用于zuul）

4. 注解：@CrossOrigin

## 15.解决跨域问题

使用网关过滤器 `CorsWebFilter` 解决跨域问题

在 **gmall-gateway/src/main/java/com/example/gmall/gateway/config** 新建 **CorsConfig.java** 

CorsConfig.java

```java
package com.example.gmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @ClassName CorsConfig
 * @Author 鳄鱼魜
 * @Date 2023/3/9 15:21
 * @Version 1.0
 * @Description 使用过滤器 CorsWebFilter 解决跨域问题
 */

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 允许跨域访问的域名，”*“代表所有域名，不建议使用”*“，因为不安全，不能携带cookie
        corsConfiguration.addAllowedOrigin("http://manager.gmall.com");
        corsConfiguration.addAllowedOrigin("http://localhsot:1000");
        // 允许跨域访问的请求方式，可以写“get”，“post”，建议写"*"，因为“*”代表任何请求方式
        corsConfiguration.addAllowedMethod("*");
        // 是否允许携带cookie，允许
        corsConfiguration.setAllowCredentials(true);
        // 允许跨域访问的头信息，“*”表示任意访问
        corsConfiguration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);
        return new CorsWebFilter(urlBasedCorsConfigurationSource);
    }

}
```

启动服务

`GmallGatewayApplication`

`GmallPmsApplication`

`GmallAdminApplication`

测试连接：

http://manager.gmall.com/

查看

![image-20230309153812948](image/image-20230309153812948.png)

## 16.图片上传

1. 用aliyun的oss来存储图片

   使用文档 https://help.aliyun.com/document_detail/31927.html 

2. 进入aliyun的OSS控制台

   https://oss.console.aliyun.com/overview

   bucket列表

   ![image-20230309162258504](image/image-20230309162258504.png)

   创建bucket（选择低频访问存储，公共读）

   ![bucket](image/bucket.png)

   跨域设置规则

   ![image-20230309161840088](image/image-20230309161840088.png)

3. 查看上传图片的请求网址

   ![image-20230309155823706](image/image-20230309155823706.png)

   可以看出上传图片的请求网址是 http://api.gmall.com/pms/oss/policy?t=1678348677810

   请求路径是 **api.gmall.com/pms/oss/policy**

   **?t=1678348677810** 这个是防止浏览器缓存的随机值

4. 创建密钥

   AccessKey管理

   ![image-20230309162623716](image/image-20230309162623716.png)

   使用子用户 -> 创建用户

   ![image-20230309162542733](image/image-20230309162542733.png)

   用户

   ![image-20230309161717647](image/image-20230309161717647.png)

   获取到 

   AccessKey ID：`LTAI5tFCfUQy2JgwZTkbH9ea`

   AccessKey Secret：`iseWdd6lMZMpbvtsUdsuYKPWXJyrk4`

   添加权限（AliyunOSSFullAccess：表示所有权限）

   ![image-20230309163831516](image/image-20230309163831516.png)

5. gmall-eyvren -> 概述 -> 外网访问

   ![image-20230309164454411](image/image-20230309164454411.png)

   获取到地域节点： `oss-cn-shanghai.aliyuncs.com`

6. bucket列表 -> bucket名称 -> 复制 gmall-eyvren

   ![image-20230309164608649](image/image-20230309164608649.png)

   获取到bucket名称：`gmall-eyvren`

7. 需要的参数

   ```java
   // 代码直接 copy 官方文档
   String accessId = "AccessKey ID";
   String accessKey = "AccessKey Secret";
   String endpoint = "地域节点";
   String bucket = "bucket名称";
   ```

8. 在 **gmall-pms/src/main/java/com/atguigu/gmall/pms/controller** 目录下新建 **OssController.java**

   OssController.java

   ```java
   package com.atguigu.gmall.pms.controller;
   
   import com.aliyun.oss.OSS;
   import com.aliyun.oss.OSSClientBuilder;
   import com.aliyun.oss.common.utils.BinaryUtil;
   import com.aliyun.oss.model.MatchMode;
   import com.aliyun.oss.model.PolicyConditions;
   import com.atguigu.gmall.common.bean.ResponseVo;
   import org.apache.commons.net.ftp.FTP;
   import org.apache.commons.net.ftp.FTPClient;
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RequestMapping;
   import org.springframework.web.bind.annotation.RestController;
   
   import java.io.File;
   import java.io.FileInputStream;
   import java.io.IOException;
   import java.text.SimpleDateFormat;
   import java.util.Date;
   import java.util.LinkedHashMap;
   import java.util.Map;
   
   /**
    * @ClassName OssController
    * @Author 鳄鱼魜
    * @Date 2023/3/9 16:58
    * @Version 1.0
    * @Description 图片上传到阿里云OSS
    */
   
   @RestController
   @RequestMapping("pms/oss")
   public class OssController {
   
       @GetMapping("policy")
       public ResponseVo<Object> policy() {
           // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
           String accessId = "LTAI5tFCfUQy2JgwZTkbH9ea";
           String accessKey = "iseWdd6lMZMpbvtsUdsuYKPWXJyrk4";
           // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
           String endpoint = "oss-cn-shanghai.aliyuncs.com";
           // 填写Bucket名称，例如examplebucket。
           String bucket = "gmall-eyvren";
           // 填写Host地址，格式为https://bucketname.endpoint。
           String host = "https://" + bucket + "." + endpoint;
           // 设置上传到OSS文件的前缀，可置空此项。置空后，文件将上传至Bucket的根目录下。
           Date date = new Date();
           // 以 yyyy-MM-dd 名称方式创建文件夹来存储
           String dir = new SimpleDateFormat("yyyy-MM-dd").format(date);
   
           // 创建ossClient实例。
           OSS ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);
           try {
               long expireTime = 30;
               long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
               Date expiration = new Date(expireEndTime);
               PolicyConditions policyConds = new PolicyConditions();
               policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
               policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
   
               String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
               byte[] binaryData = postPolicy.getBytes("utf-8");
               String encodedPolicy = BinaryUtil.toBase64String(binaryData);
               String postSignature = ossClient.calculatePostSignature(postPolicy);
   
               Map<String, String> respMap = new LinkedHashMap<String, String>();
               respMap.put("accessid", accessId);
               respMap.put("policy", encodedPolicy);
               respMap.put("signature", postSignature);
               respMap.put("dir", dir);
               respMap.put("host", host);
               respMap.put("expire", String.valueOf(expireEndTime / 1000));
               // respMap.put("expire", formatISO8601Date(expiration));
   
               return ResponseVo.ok(respMap);
   
           } catch (Exception e) {
               // Assert.fail(e.getMessage());
               System.out.println(e.getMessage());
           }
           return ResponseVo.fail("获取签名失败！");
   
       }
   
   }
   ```

























































 
