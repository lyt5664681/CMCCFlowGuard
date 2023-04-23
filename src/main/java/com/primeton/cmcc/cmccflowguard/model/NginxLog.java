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

    /**
     * @description nginx日志文件前缀名
     * @author YunTao.Li
     * @date 2023/4/23 14:56
     */
    private String prefix;

    /**
     * @description 是否使用登录授权,true:使用用户名密码授权,false:使用私钥授权
     * @author YunTao.Li
     * @date 2023/4/23 13:42
     */
    private boolean authorized;
    private String prikey;
}
