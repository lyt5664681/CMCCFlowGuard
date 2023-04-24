package com.primeton.cmcc.cmccflowguard.service;

import cn.hutool.core.lang.UUID;
import com.primeton.cmcc.cmccflowguard.model.WSDL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.InetSocketAddress;
import java.net.Socket;
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

//    /**
//     * @param wsdlPath   http://127.0.0.1:8081/WSWorklistQueryManagerServiceBinding?WSDL
//     * @param methodName queryFourCount
//     * @param args       todo
//     * @return boolean
//     * @description
//     * @author YunTao.Li
//     * @date 2023/4/19 11:20
//     */
//    public boolean checkWSMethodHealth(String wsdlPath, String methodName, Object... args) throws Exception {
//        Client client = null;
////        try {
////            URL wsdlUrl = new URL("http://192.168.30.103:8080/default/WSProcessInstManagerService?wsdl");
////            WSDLFactory wsdlFactory = WSDLFactory.newInstance();
////            WSDLReader reader = wsdlFactory.newWSDLReader();
////            Definition wsdlDefinition = reader.readWSDL(wsdlUrl.toString());
////            javax.xml.ws.Service service = javax.xml.ws.Service.create(wsdlUrl, new QName(wsdlDefinition.getTargetNamespace(), "queryProcessInstDetail"));
////            JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
////            client = clientFactory.createClient(wsdlUrl, (List<String>) service);
////
////
//////            JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
//////            Client client = clientFactory.createClient(wsdlPath);
////        } catch (Exception e1) {
////            e1.printStackTrace();
////            throw e1;
////        }
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
//            throw e;
//        }
//        return true;
//    }

    public boolean checkWSMethodHealthWithTemplate(String wsdlPath, String tenantId, String methodName, List<WSDL.Method.Param> params) {
        try {
            UUID uuid = UUID.randomUUID();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(MediaType.TEXT_XML_VALUE));
            headers.add("SOAPAction", "Envelope");

            String xmlPayload = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                    "<s:Header><Header xmlns=\"http://www.primeton.com/BPS\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                    "            <ClientId/>" +
                    "            <LoginCode/>" +
                    "            <LoginPassword/>" +
                    "            <OperationCode/>" +
                    "            <ReqID/>" +
                    "            <RequestId>" + uuid + "</RequestId>" +
                    "            <TenantID>" + tenantId + "</TenantID>" +
                    "        </Header>" +
                    "        <others xmlns=\"http://www.primeton.com/EOS\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                    "            <RequestId>54207d32-9cb3-4005-88e8-7dfe3fe80c58</RequestId>" +
                    "        </others>" +
                    "    </s:Header>" +
                    "<s:Body xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
                    "<" + methodName + ">";
            for (WSDL.Method.Param param : params) {
                String paramName = param.getName();
                String paramValue = param.getValue();
                xmlPayload += "<" + paramName + ">" + paramValue + "</" + paramName + ">";
            }
            xmlPayload += "</" + methodName + ">";
            xmlPayload += "</s:Body></s:Envelope>";

//            log.debug("[wsdl call]wsdlPath:" + wsdlPath + ";methodName:" + methodName + ";wsdl=xmlPayload:" + xmlPayload);
            HttpEntity<String> request = new HttpEntity<>(xmlPayload, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(wsdlPath, HttpMethod.POST, request, String.class);
//            log.debug("[wsdl response]wsdlPath:" + wsdlPath + ";methodName:" + methodName + ";responseBody:" + response);
            if (response.getStatusCode().value() == 200) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}

