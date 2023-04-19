package com.primeton.cmcc.cmccflowguard.service;

import com.primeton.cmcc.cmccflowguard.model.HealthCheckConfig;
import com.primeton.cmcc.cmccflowguard.model.WSDL;
import com.primeton.cmcc.cmccflowguard.model.Website;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    /**
     * @return void
     * @description 定时拨测主机
     * @author YunTao.Li
     * @date 2023/4/19 13:36
     */
//    @Scheduled(fixedDelayString = "${health-check.polling.time}")
    public void checkHostListHealthHandle() {
        List<Website> websites = healthCheckConfig.getWebsites();

        //当前时间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日 H:mm:ss");
        String formattedNow = now.format(formatter);

        for (Website website : websites) {

            String websiteName = website.getName();
            String url = website.getUrl();
            int port = website.getPort();

            boolean health = healthCheckService.checkHostHealth(url, port);

            //TODO: 记录日志
            if (!health) {
                String noticeContext = "[WEBSITES]告警通知：" + "服务器 " + websiteName + ", host : " + url + ",port:" + port + "于 " + formattedNow + "发生故障，无法ping通。请介入";
                System.out.println(noticeContext);
                //TODO: 短信通知
                log.debug(noticeContext);
            }
        }
    }

    /**
     * @return void
     * @description 定时拨测wsdl接口
     * @author YunTao.Li
     * @date 2023/4/19 13:37
     */
//    @Scheduled(fixedDelayString = "${health-check.polling.time}")
    public void checkWSDLListHealthHandle() {
        List<WSDL> wsdls = healthCheckConfig.getWsdls();

        //当前时间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日 H:mm:ss");
        String formattedNow = now.format(formatter);

        for (WSDL wsdl : wsdls) {

            List<WSDL.Method> methods = wsdl.getMethods();
            String wsdlPath = wsdl.getUrl();

            for (WSDL.Method method : methods) {

                String methodName = method.getName();
                List<WSDL.Method.Param> params = method.getParams();
                List<Object> objs = null;
                try {
                    objs = incetenceParamValues(params);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                boolean health = healthCheckService.checkWSMethodHealth(wsdlPath, methodName, objs.toArray());
                //TODO: 记录日志
                if (!health) {
                    String noticeContext = "[WSDL]告警通知：" + "wsdlPath " + wsdlPath + ", methodName : " + methodName + ",port:" + objs.toArray() + "于 " + formattedNow + "发生故障，接口调用失败。请介入";
                    System.out.println(noticeContext);
                    //TODO: 短信通知
                    log.debug(noticeContext);
                }
            }
        }
    }

    /**
     * @return void
     * @description 定时拨测nginx日志
     * @author YunTao.Li
     * @date 2023/4/19 13:37
     */
    public void checkNginxLogHealthHandle() {

    }

    protected List<Object> incetenceParamValues(List<WSDL.Method.Param> params) throws Exception {
        List<Object> paramValues = new ArrayList<>();
        for (WSDL.Method.Param param : params) {
            String paramType = param.getType();
            Object paramValue = param.getValue();

            // 使用反射机制根据参数类型推断参数值的类型，并进行类型转换
            Class<?> clazz;
            switch (paramType) {
                case "int":
                    clazz = Integer.class;
                    break;
                case "float":
                    clazz = Float.class;
                    break;
                case "double":
                    clazz = Double.class;
                    break;
                case "boolean":
                    clazz = Boolean.class;
                    break;
                default:
                    clazz = String.class;
                    break;
            }
            Constructor<?> constructor = clazz.getConstructor(String.class);
            Object convertedParamValue = constructor.newInstance(paramValue);
            paramValues.add(convertedParamValue);
        }
        return paramValues;
    }
}
