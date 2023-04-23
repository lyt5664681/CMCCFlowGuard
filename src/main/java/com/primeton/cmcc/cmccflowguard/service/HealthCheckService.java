package com.primeton.cmcc.cmccflowguard.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.primeton.cmcc.cmccflowguard.model.WSDL;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

/**
 * @author YunTao.Li
 * @description:
 * @date 2023/4/17 23:46
 */
@Slf4j
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

    public boolean checkWSMethodHealthWithTemplate(String wsdlPath, String tenantId, String methodName, List<WSDL.Method.Param> params) {
        try {
            UUID uuid = UUID.randomUUID();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.add("SOAPAction", methodName);
            headers.setAcceptCharset(Collections.singletonList(Charset.forName("UTF-8")));
            headers.set("Accept-Charset", "UTF-8");

            String xmlPayload = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                    "<s:Header>\n" +
                    "        <Header\n" +
                    "            xmlns=\"http://www.primeton.com/BPS\"\n" +
                    "            xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                    "            <ClientId/>\n" +
                    "            <LoginCode/>\n" +
                    "            <LoginPassword/>\n" +
                    "            <OperationCode/>\n" +
                    "            <ReqID/>\n" +
                    "            <RequestId>" + uuid + "</RequestId>\n" +
                    "            <TenantID>" + tenantId + "</TenantID>\n" +
                    "        </Header>\n" +
                    "        <others\n" +
                    "            xmlns=\"http://www.primeton.com/EOS\"\n" +
                    "            xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                    "            <RequestId>54207d32-9cb3-4005-88e8-7dfe3fe80c58</RequestId>\n" +
                    "        </others>\n" +
                    "    </s:Header>" +
                    "<s:Body\n" +
                    "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                    "        xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
                    "        <" + methodName + ">\n";
            for (WSDL.Method.Param param : params) {
                String paramName = param.getName();
                String paramValue = param.getValue();
                xmlPayload += "<" + paramName + ">" + paramValue + "</" + paramName + ">";
            }
            xmlPayload += "        </" + methodName + ">\n";
            xmlPayload += "    </s:Body>" +
                    "</s:Envelope>";

            System.out.println("wsdl=xmlPayload:" + xmlPayload);
            HttpEntity<String> request = new HttpEntity<>(xmlPayload, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(wsdlPath, HttpMethod.POST, request, String.class);
            // 解析响应并获取结果
            String responseBody = response.getBody();

            System.out.println("responseBody:" + responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}

