package com.techroom.roommanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /**
     * Cấu hình Spring để phục vụ file từ thư mục /images
     * Khi frontend gọi /images/1/abc.jpg
     * Spring sẽ tìm file trong ổ đĩa tại [thư mục chạy dự án]/images/1/abc.jpg
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // "file:./images/" nghĩa là thư mục "images" nằm cùng cấp với pom.xml
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:./images/");
    }
}