package com.example.potholedetector_backend.config;

import com.example.potholedetector_backend.service.PotholeWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Bean
    public PotholeWebSocketHandler potholeWebSocketHandler() {
        return new PotholeWebSocketHandler();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(potholeWebSocketHandler(), "/pothole-updates").setAllowedOrigins("*");
    }
}
