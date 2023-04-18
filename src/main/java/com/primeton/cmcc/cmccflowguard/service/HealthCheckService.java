package com.primeton.cmcc.cmccflowguard.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author YunTao.Li
 * @description:
 * @date 2023/4/17 23:46
 */
@Service
public class HealthCheckService {

    /**
     * @param url
     * @return boolean
     * @description 接口健康检测
     * @author YunTao.Li
     * @date 2023/4/18 9:58
     */
    public boolean checkHttpHealth(String url) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.getForObject(url, String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param host
     * @param port
     * @return boolean
     * @description 主机健康检测
     * @author YunTao.Li
     * @date 2023/4/18 9:58
     */

    public boolean checkHostHealth(String host, int port) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 5000);
            socket.close();
            return true;
        } catch (Exception e) {
            // 告警通知
            return false;
        }
    }
}

