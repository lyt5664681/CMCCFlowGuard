package com.primeton.cmcc.cmccflowguard.service;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.springframework.stereotype.Service;

/**
 * @author YunTao.Li
 * @description:
 * @date 2023/4/20 11:15
 */
@Service
public class NotificationService {


    public Boolean SMSWaring(String message) {
        String wsdlPath = "";
        String methodName = "";
        String args = "";


//
//        JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
//        Client client = clientFactory.createClient(wsdlPath);
//        Object[] result = null;
//        try {
//            //如果有命名空间的话
////            QName operationName = new QName(targetNamespace, methodName); //如果有命名空间需要加上这个，第一个参数为命名空间名称，第二个参数为WebService方法名称
//            result = client.invoke(methodName, args);//后面为WebService请求参数数组
////            //如果没有命名空间的话
////            result = client.invoke(operationName, param1); //注意第一个参数是字符串类型，表示WebService方法名称，第二个参数为请求参数
//            System.out.println(result.toString());
//        } catch (Exception e) {
//            String errMsg = "WebService发生异常！";
//            result = new Object[]{errMsg};
////            logger.error(errMsg, e);
//            e.printStackTrace();
//            System.out.println(result);
//            return false;
//        }
        return true;
    }
}
