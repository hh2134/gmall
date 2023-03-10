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