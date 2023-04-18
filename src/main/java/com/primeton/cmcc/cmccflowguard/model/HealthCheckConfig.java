package com.primeton.cmcc.cmccflowguard.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author YunTao.Li
 * @description:
 * @date 2023/4/18 0:22
 */
@Data
@Component
@EnableConfigurationProperties(HealthCheckConfig.class)
@ConfigurationProperties(prefix = "health-check")
public class HealthCheckConfig {
    private List<Website> websites;

    public List<Website> getWebsites() {
        return websites;
    }

    public void setWebsites(List<Website> websites) {
        this.websites = websites;
    }
}
