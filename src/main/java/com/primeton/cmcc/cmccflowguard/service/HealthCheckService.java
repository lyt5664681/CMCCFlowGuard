package com.primeton.cmcc.cmccflowguard.service;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
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

    /**
     * @param wsdlPath   http://127.0.0.1:8081/WSWorklistQueryManagerServiceBinding?WSDL
     * @param methodName queryFourCount
     * @param args       todo
     * @return boolean
     * @description
     * @author YunTao.Li
     * @date 2023/4/19 11:20
     */
    public boolean checkWSMethodHealth(String wsdlPath, String methodName, Object... args) {
        JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
        Client client = clientFactory.createClient(wsdlPath);
        Object[] result = null;
        try {
            //如果有命名空间的话
//            QName operationName = new QName(targetNamespace, methodName); //如果有命名空间需要加上这个，第一个参数为命名空间名称，第二个参数为WebService方法名称
            result = client.invoke(methodName, args);//后面为WebService请求参数数组
//            //如果没有命名空间的话
//            result = client.invoke(operationName, param1); //注意第一个参数是字符串类型，表示WebService方法名称，第二个参数为请求参数
            System.out.println(result.toString());
        } catch (Exception e) {
            String errMsg = "WebService发生异常！";
            result = new Object[]{errMsg};
//            logger.error(errMsg, e);
            e.printStackTrace();
            System.out.println(result);
            return false;
        }
        return true;
    }
}

