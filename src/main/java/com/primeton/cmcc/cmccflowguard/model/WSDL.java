package com.primeton.cmcc.cmccflowguard.model;

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
}
