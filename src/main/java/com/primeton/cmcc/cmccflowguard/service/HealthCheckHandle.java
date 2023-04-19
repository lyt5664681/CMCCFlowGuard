package com.primeton.cmcc.cmccflowguard.service;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.FIFOCache;
import cn.hutool.core.date.DateUtil;
import com.jcraft.jsch.*;
import com.jcraft.jsch.JSch;

import com.primeton.cmcc.cmccflowguard.model.HealthCheckConfig;
import com.primeton.cmcc.cmccflowguard.model.NginxLog;
import com.primeton.cmcc.cmccflowguard.model.WSDL;
import com.primeton.cmcc.cmccflowguard.model.Website;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
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

    @Value("${health-check.website-enable:false}")
    private Boolean websiteEnable;

    @Value("${health-check.wsdl-enable:false}")
    private Boolean wsdlEnable;

    @Value("${health-check.nginx-log-enable:false}")
    private Boolean nginxLogEnable;

    @Value("${health-check.nging-log-err-limit:50}")
    private int errorToleranceThreshold;

    @Autowired
    private HealthCheckService healthCheckService;

    @Autowired
    private HealthCheckConfig healthCheckConfig;

    private static FIFOCache<String, String> cache = CacheUtil.newFIFOCache(1000);

    /**
     * @return void
     * @description 定时拨测主机
     * @author YunTao.Li
     * @date 2023/4/19 13:36
     */
    @Scheduled(fixedDelayString = "${health-check.polling.time}")
    public void checkHostListHealthHandle() {
        if (!websiteEnable) {
            return;
        }
        List<Website> websites = healthCheckConfig.getWebsites();

        //当前时间
        String formattedNow = DateUtil.format(DateUtil.date(), "yyyy年M月d日 H:mm:ss");

        for (Website website : websites) {

            String websiteName = website.getName();
            String url = website.getUrl();
            int port = website.getPort();

            boolean health = healthCheckService.checkHostHealth(url, port);

            // 打印日志或通知
            if (!health) {
                StringBuilder noticeContextBuilder = new StringBuilder();
                noticeContextBuilder.append("[ERROR][").append(formattedNow).append("][WEBSITES]拨测失败：")
                        .append("服务器:").append(websiteName).append(", host:").append(url).append(",port:").append(port)
                        .append("。发生故障，无法ping通。请人工介入");
                //TODO: 短信通知
                log.debug(noticeContextBuilder.toString());
            } else {
                StringBuilder noticeContextBuilder = new StringBuilder();
                noticeContextBuilder.append("[INFO][").append(formattedNow).append("][WEBSITES]拨测正常：")
                        .append("服务器:").append(websiteName).append(", host:").append(url).append(",port:").append(port);
                log.debug(noticeContextBuilder.toString());
            }
        }
    }

    /**
     * @return void
     * @description 定时拨测wsdl接口
     * @author YunTao.Li
     * @date 2023/4/19 13:37
     */
    @Scheduled(fixedDelayString = "${health-check.polling.time}")
    public void checkWSDLListHealthHandle() {
        if (!wsdlEnable) {
            return;
        }
        List<WSDL> wsdls = healthCheckConfig.getWsdls();

        //当前时间
        String formattedNow = DateUtil.format(DateUtil.date(), "yyyy年M月d日 H:mm:ss");

        for (WSDL wsdl : wsdls) {

            List<WSDL.Method> methods = wsdl.getMethods();
            String wsdlPath = wsdl.getUrl();

            for (WSDL.Method method : methods) {

                try {
                    String methodName = method.getName();
                    List<WSDL.Method.Param> params = method.getParams();
                    List<Object> objs = null;
                    try {
                        objs = incetenceParamValues(params);
                    } catch (Exception e) {
                        throw e;
                    }

                    boolean health = healthCheckService.checkWSMethodHealth(wsdlPath, methodName, objs.toArray());
                    if (!health) {
                        StringBuilder noticeContextBuilder = new StringBuilder();
                        noticeContextBuilder.append("[ERROR][").append(formattedNow).append("][WSDL]拨测失败：")
                                .append("wsdlPath:").append(wsdlPath).append(", methodName:").append(methodName).append(",argus:").append(objs.toArray())
                                .append("。发生故障，接口调用失败。请人工介入");
                        //TODO: 短信通知
                        log.debug(noticeContextBuilder.toString());
                    } else {
                        StringBuilder noticeContextBuilder = new StringBuilder();
                        noticeContextBuilder.append("[INFO][").append(formattedNow).append("][WSDL]拨测成功：")
                                .append("wsdlPath:").append(wsdlPath).append(", methodName:").append(methodName).append(",argus:").append(objs.toArray());
                        log.debug(noticeContextBuilder.toString());
                    }
                } catch (Exception e) {
                    String exceptionMsg = null;
                    try {
                        exceptionMsg = e.getCause().getMessage();
                    } catch (Exception e2) {
                        exceptionMsg = e.getMessage();
                    }
                    StringBuilder noticeContextBuilder = new StringBuilder();
                    noticeContextBuilder.append("[ERROR][").append(formattedNow).append("][WSDL]拨测失败：")
                            .append("程序异常:").append(exceptionMsg);
                    //TODO: 短信通知
                    log.debug(noticeContextBuilder.toString());
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
    @Scheduled(fixedDelayString = "${health-check.polling.time}")
    public void checkNginxLogHealthHandle() {
        if (!nginxLogEnable) {
            return;
        }

        // 当前时间
        String formattedNow = DateUtil.format(DateUtil.date(), "yyyy年M月d日 H:mm:ss");
        String formattedNowForLog = DateUtil.format(DateUtil.date(), "yyyy-MM-dd");

        List<NginxLog> nginxLogs = healthCheckConfig.getNginxlogs();

        /***
         * 循环遍历每一个主机,查找每一个主机的日志,统计500的数量
         * */
        for (NginxLog nginxLog : nginxLogs) {
            String host = nginxLog.getHost();
            String nginxName = nginxLog.getName();
            int port = nginxLog.getPort();
            String user = nginxLog.getAccount();
            String password = nginxLog.getPassword();
            String nginxLogPath = nginxLog.getPath();

            List<String> result = new ArrayList<>();

            // step 1: 执行远程连接
            JSch jsch = new JSch();
            Session session = null;
            try {
                session = jsch.getSession(user, host, port);
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();
            } catch (Exception e) {
                //TODO: 短信通知
                StringBuilder noticeContextBuilder = new StringBuilder();
                noticeContextBuilder.append("[ERROR][").append(formattedNow).append("][NGINX-LOG]拨测失败：")
                        .append("user:").append(user).append(", host:").append(host).append(",port:").append(port)
                        .append("。原因：ssh主机连接失败");
                log.debug(noticeContextBuilder.toString());
                continue;
            }

            // step 2: 执行远程命令
            InputStream in = null;
            ChannelExec channelExec = null;
            String command = "cat " + nginxLogPath + "/access_webservice_" + formattedNowForLog + ".log|grep '\"sta\":\"500\"' |wc -l";
            try {
                channelExec = (ChannelExec) session.openChannel("exec");
                in = channelExec.getInputStream();
                channelExec.setCommand(command);
                channelExec.connect();
            } catch (Exception e) {
                //TODO: 短信通知
                StringBuilder noticeContextBuilder = new StringBuilder();
                noticeContextBuilder.append("[ERROR][").append(formattedNow).append("][NGINX-LOG]拨测失败：")
                        .append("user:").append(user).append(", host:").append(host).append(",port:").append(port)
                        .append("。原因：ssh执行命令出错");
                log.debug(noticeContextBuilder.toString());
                continue;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            // step 3: 获得远程调用结果进行解析读取
            int errCount = 0;
            try {
                String line;
                if ((line = reader.readLine()) != null) {
                    errCount = Integer.parseInt(line);
                }

                channelExec.disconnect();
                session.disconnect();
            } catch (Exception e) {
                //TODO: 短信通知
                StringBuilder noticeContextBuilder = new StringBuilder();
                noticeContextBuilder.append("[ERROR][").append(formattedNow).append("][NGINX-LOG]拨测失败：")
                        .append("user:").append(user).append(", host:").append(host).append(",port:").append(port)
                        .append("。原因：解析返回结果失败。");
                log.debug(noticeContextBuilder.toString());
                continue;
            }

            // step 4: 拿到errCount跟本地缓存做对比,如果两次对比之间阈值大于50则告警
            String cacheKey = host + "_" + port;
            int lastErrCount = Integer.parseInt(cache.get(cacheKey) == null ? "0" : cache.get(cacheKey));
            int errCountDelta = errCount - lastErrCount;
            if (errCountDelta > errorToleranceThreshold) {
                //TODO: 短信通知
                StringBuilder noticeContextBuilder = new StringBuilder();
                noticeContextBuilder.append("[ERROR][").append(formattedNow).append("[NGINX-LOG]告警：")
                        .append("user:").append(user).append(", host:").append(host).append(",port:").append(port)
                        .append("。原因：两次拨测间产生问题数量超过阈值。差值为：").append(errCountDelta);
                log.debug(noticeContextBuilder.toString());
                cache.put(cacheKey, String.valueOf(errCount));
                continue;
            }
            cache.put(cacheKey, String.valueOf(errCount));

            // 正常记录
            StringBuilder noticeContextBuilder = new StringBuilder();
            noticeContextBuilder.append("[INFO][").append(formattedNow).append("][NGINX-LOG]拨测通过：")
                    .append("user:").append(user).append(", host:").append(host).append(",port:").append(port)
                    .append("。当前差值为:").append(errCountDelta).append("。当前总体数量为:").append(errCount);
            log.debug(noticeContextBuilder.toString());
        }

        return;
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
                case "Integer":
                    clazz = Integer.class;
                    break;
                case "float":
                    clazz = Float.class;
                    break;
                case "Float":
                    clazz = Float.class;
                    break;
                case "double":
                    clazz = Double.class;
                    break;
                case "Double":
                    clazz = Double.class;
                    break;
                case "boolean":
                    clazz = Boolean.class;
                    break;
                case "Boolean":
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
