package com.linmo.nettydemo.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "netty")
public class PortDefinition {
    Map<String, Integer> port;
}
