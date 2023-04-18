package com.primeton.cmcc.cmccflowguard.service;

import com.primeton.cmcc.cmccflowguard.model.HealthCheckConfig;
import com.primeton.cmcc.cmccflowguard.model.Website;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author YunTao.Li
 * @description:
 * @date 2023/4/18 16:18
 */
@Service
@Slf4j
public class HealthCheckHandle {
    @Autowired
    private HealthCheckService healthCheckService;

    @Autowired
    private HealthCheckConfig healthCheckConfig;

    @Scheduled(fixedDelayString = "${health-check.polling.time}")
    public void checkHostListHealthHandle() {
        List<Website> websites = healthCheckConfig.getWebsites();

        //当前时间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日 H:mm");
        String formattedNow = now.format(formatter);

        for (Website website : websites) {

            String websiteName = website.getName();
            String url = website.getUrl();
            int port = website.getPort();

            boolean health = healthCheckService.checkHostHealth(url, port);

            //TODO: 记录日志
            if (!health) {
                String noticeContext = "告警通知：" + "服务器 " + websiteName + ", host : " + url + ",port:" + port + "于 " + formattedNow + "发生故障，无法ping通。请介入";
                System.out.println(noticeContext);
                //TODO: 短信通知
                log.debug(noticeContext);
            }
        }
    }
}
