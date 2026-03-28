package org.example.dormrepairsystem.config;

import org.example.dormrepairsystem.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 配置跨域请求
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")// 允许所有路径的跨域请求
                .allowedOriginPatterns("*")// 允许所有来源的跨域请求
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")// 允许所有HTTP方法
                .allowedHeaders("*")// 允许所有请求头
                .allowCredentials(true)// 允许跨域请求携带凭证
                .maxAge(3600);// 最大缓存时间，单位秒
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册JWT拦截器
        JwtInterceptor jwtInterceptor = new JwtInterceptor();
        
        // 添加拦截器，指定需要拦截的路径
        // 排除登录和注册接口，因为这些接口不需要令牌验证
        // 排除静态资源，确保CSS、JS等文件可以正常加载
        // 排除Swagger和Knife4j相关路径
        // 排除上传目录，允许直接访问上传的图片
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/repair-orders/**", "/users/**", "/dormitories/**")
                .excludePathPatterns("/users/login", "/users", "/static/**", "/uploads/**",
                        "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/doc.html", "/webjars/**");
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射上传目录到静态资源
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}