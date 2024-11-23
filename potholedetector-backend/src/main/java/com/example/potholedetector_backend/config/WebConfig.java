package com.example.potholedetector_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Áp dụng CORS cho tất cả các endpoint
                .allowedOrigins("*")   // Cho phép tất cả nguồn. Thay "*" bằng tên miền cụ thể nếu cần
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS"); // Các phương thức được phép
    }
}
