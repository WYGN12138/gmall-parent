package com.atguigu.gmall.item.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.thread-pool")
@Data
public class AppThreadPoolProperties {
}
