package com.primeton.cmcc.cmccflowguard.model;

import lombok.Data;

/**
 * @author YunTao.Li
 * @description:
 * @date 2023/4/19 14:35
 */
@Data
public class NginxLog {
    private String name;
    private String host;
    private int port;
    private String path;
    private String account;
    private String password;
}
