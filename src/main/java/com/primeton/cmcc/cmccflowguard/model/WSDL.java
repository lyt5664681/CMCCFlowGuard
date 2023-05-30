package com.primeton.cmcc.cmccflowguard.model;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.Data;

import java.util.List;

/**
 * @author YunTao.Li
 * @description:
 * @date 2023/4/19 11:25
 */
@Data
public class WSDL {
    private String name;
    private String url;
    private List<Method> methods;

    @Data
    public static class Method {
        private String name;
        private String tenantid;
        private List<Param> params;

        @Data
        public static class Param {
            private String name;
            private String type;
            private String value;
        }
    }

    public static void main(String[] args) {
        try {
            ConfigService config = NacosFactory.createConfigService("127.0.0.1:8848");
            config.toString();
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
}
