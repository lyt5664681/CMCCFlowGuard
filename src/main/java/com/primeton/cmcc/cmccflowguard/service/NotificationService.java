package com.primeton.cmcc.cmccflowguard.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.cmcc.soa.osb_eip_eip_hq_importsmssrv.InputParameters;
import com.cmcc.soa.osb_eip_eip_hq_importsmssrv.OutputParameters;
import com.primeton.cmcc.cmccflowguard.util.SMSSoapInterceptor;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.message.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author YunTao.Li
 * @description:
 * @date 2023/4/20 11:15
 */
@Service
public class NotificationService {

    @Value("${health-check.sms-notification.sms-enable:false}")
    private boolean SMSEnable;

    @Value("${health-check.sms-notification.sms-wsdl-path:false}")
    private String SMSWSDLPath;

    @Value("${health-check.sms-notification.appid:10344}")
    private String appid;
    @Value("${health-check.sms-notification.templateid:10344202304171118001}")
    private String templateid;
    @Value("${health-check.sms-notification.receiver-number:17276440520}")
    private String receiverNumber;

//    public Boolean SMSWaring(String messageContext) {
//        if (!SMSEnable) {
//            return true;
//        }
//        try {
//            SMSWaringTemplate(messageContext);
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("SMSWaringTemplate");
//        }
//
//        String wsdlPath = SMSWSDLPath;
//        String methodName = SMSMethodName;
//        String args = SMSArg;
//        String token = "1";
//        String sourceSystemId = "WFP";
//        String sourceSystemName = "集中化流程平台";
//        String provinceCode = "HQ";
//
//        Object[] result = null;
//        try {
//            JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
//            Client client = clientFactory.createClient(wsdlPath);
//
//            // 创建SOAP请求头
//            MessageFactory factory = MessageFactory.newInstance();
//            SOAPMessage message = factory.createMessage();
//            SOAPHeader header = message.getSOAPHeader();
//
//            // 创建请求头元素并设置值
//            QName tokenHeaderName = new QName("http://soa.cmcc.com/OSB_EIP_EIP_HQ_ImportSmsSrv", "TOKEN_ID");
//            SOAPElement tokenHeaderElement = header.addChildElement(tokenHeaderName);
//            tokenHeaderElement.setValue(token);
//
//            QName sourceSystemIdHeaderName = new QName("http://soa.cmcc.com/OSB_EIP_EIP_HQ_ImportSmsSrv", "SOURCESYSTEMID");
//            SOAPElement sourceSystemIdHeaderElement = header.addChildElement(sourceSystemIdHeaderName);
//            sourceSystemIdHeaderElement.setValue(sourceSystemId);
//
//            QName sourceSystemNameHeaderName = new QName("http://soa.cmcc.com/OSB_EIP_EIP_HQ_ImportSmsSrv", "PROVINCE_CODE");
//            SOAPElement sourceSystemNameHeaderElement = header.addChildElement(sourceSystemNameHeaderName);
//            sourceSystemNameHeaderElement.setValue(provinceCode);
//
//            // 将请求头添加到客户端
//            Map<String, Object> requestContext = client.getRequestContext();
//            requestContext.put("com.sun.xml.internal.ws.client.BindingProviderProperties.HTTP_CLIENT_REQUEST_CONTEXT", new HashMap<String, Object>());
//            Map<String, List<String>> headers = new HashMap<String, List<String>>();
//            headers.put("TOKEN_ID", Collections.singletonList(token));
//            headers.put("SOURCESYSTEMID", Collections.singletonList(sourceSystemId));
//            headers.put("PROVINCE_CODE", Collections.singletonList(provinceCode));
//            requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
//
//            // 创建一个输入集合对象
//            InputParameters.InputCollection inputCollection = new InputParameters.InputCollection();
//            InputParameters.InputCollectionItem[] inputCollectionItems = new InputParameters.InputCollectionItem[1];
//
//
//            // 创建一个输入集合项对象
//            InputParameters.InputCollectionItem inputCollectionItem = new InputParameters.InputCollectionItem();
//
//            // 设置输入集合项的各个字段值
//            inputCollectionItem.setPriKey("147");
//            inputCollectionItem.setInternalUserFlag("1");
//            inputCollectionItem.setUserAccount("jinfanbo@hq.cmcc");
//            inputCollectionItem.setReceiverNumber("17276440520");
//            inputCollectionItem.setMessageContent("{'ip':'192.123.123.123','applicationName':'UAT测试看到此消息联系李云涛'}");
//            inputCollectionItem.setForceFlag(1);
//            inputCollectionItem.setMergeSendFlag(1);
//            inputCollectionItem.setAppid("10182");
//            inputCollectionItem.setTemplateNum("10344202304171118001");
//            inputCollectionItem.setInputExt("123");
//
//            // 将输入集合项加入输入集合
//            inputCollectionItems[0] = inputCollectionItem;
//            inputCollection.setInputCollectionItems(inputCollectionItems);
//
//            InputParameters.MSGHEADER msgheader = new InputParameters.MSGHEADER();
//            msgheader.setToken(token);
//            msgheader.setSourceSystemID(sourceSystemId);
//            msgheader.setProvinceCode(provinceCode);
//
//            // 创建输入参数对象
//            InputParameters inputParameters = new InputParameters();
//            inputParameters.setInputCollection(inputCollection);
//            inputParameters.setMsgHeader(msgheader);
//            // 构造请求参数
//            Object[] params = new Object[1];
//            params[0] = inputParameters;
//
//            // 调用 Web Service
//            try {
//                System.out.println("get request exception begin");
//                RequestAttributes ra = RequestContextHolder.getRequestAttributes();
//                if (ra == null) {
//                    throw new Exception("ra:为空");
//                }
//                ServletRequestAttributes sra = (ServletRequestAttributes) ra;
//                HttpServletRequest request = sra.getRequest();
//// 从 RequestFacade 中获取 org.apache.catalina.connector.Request
//                Field connectorField = ReflectionUtils.findField(RequestFacade.class, "request", Request.class);
//                connectorField.setAccessible(true);
//                Request connectorRequest = (Request) connectorField.get(request);
//
//                // 从 org.apache.catalina.connector.Request 中获取 org.apache.coyote.Request
//                Field coyoteField = ReflectionUtils.findField(Request.class, "coyoteRequest", org.apache.coyote.Request.class);
//                coyoteField.setAccessible(true);
//                org.apache.coyote.Request coyoteRequest = (org.apache.coyote.Request) coyoteField.get(connectorRequest);
//
//                // 从 org.apache.coyote.Request 中获取 MimeHeaders
//                Field mimeHeadersField = ReflectionUtils.findField(org.apache.coyote.Request.class, "headers", MimeHeaders.class);
//                mimeHeadersField.setAccessible(true);
//                MimeHeaders mimeHeaders = (MimeHeaders) mimeHeadersField.get(coyoteRequest);
//
//                mimeHeaders.addHeader("SOURCESYSTEMID", "WFP");
//                mimeHeaders.addHeader("SOURCESYSTEMNAME", "集中化流程平台");
//                mimeHeaders.addHeader("TOKEN", "1");
//                mimeHeaders.addHeader("PROVINCE_CODE", "HQ");
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("get request exception end");
//            }
//            try {
////                System.out.println("SMSSoapInterceptor smsSoapInterceptor = new SMSSoapInterceptor(); begin");
////                SMSSoapInterceptor smsSoapInterceptor = new SMSSoapInterceptor();
////                client.getOutInterceptors().add(smsSoapInterceptor);
//
//                System.out.println("client.getRequestContext()" + client.getRequestContext());
//                System.out.println("ready invoke：begin1");
//                result = client.invoke(methodName, params);
//                System.out.println("ready invoke：end1");
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw e;
//            }
//
//            System.out.println("invoke success: " + result);
//
//            // 处理响应结果
//            try {
//                System.out.println("ready 处理响应结果");
//                OutputParameters output = (OutputParameters) result[0];
//                System.out.println("SMS return: " + output.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("SMS resCode error");
//                throw e;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }

    public Boolean smsWaringWithTemplate(String ip, String messageContext) {
        if (!SMSEnable) {
            return true;
        }
        try {
            UUID uuid = UUID.randomUUID();
            String url = SMSWSDLPath;
            String token = "1";
            String sourceSystemId = "WFP";
            String sourceSystemName = "集中化流程平台";
            String provinceCode = "HQ";
            DateTime now = DateUtil.date();
            String formattedDateTime = now.toString("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.add("SOAPAction", "process");
            headers.setAcceptCharset(Collections.singletonList(Charset.forName("UTF-8")));
            headers.set("Accept-Charset", "UTF-8");

            String xmlPayload = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                    "   <soap:Body>" +
                    "         <ns2:InputParameters xmlns:ns2=\"http://soa.cmcc.com/OSB_EIP_EIP_HQ_ImportSmsSrv\" xmlns=\"http://soa.cmcc.com/MsgHeader\">" +
                    "                <ns2:MSGHEADER>" +
                    "                    <SOURCESYSTEMID>" + sourceSystemId + "</SOURCESYSTEMID>" +
                    "                    <SOURCESYSTEMNAME>" + sourceSystemName + "</SOURCESYSTEMNAME>" +
                    "                    <TOKEN>" + token + "</TOKEN>" +
                    "                    <USER_ID xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></USER_ID>" +
                    "                    <USER_NAME xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></USER_NAME>" +
                    "                    <USER_PASSWD xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></USER_PASSWD>" +
                    "                    <SUBMIT_DATE>" + formattedDateTime + "</SUBMIT_DATE>" +
                    "                    <PAGE_SIZE xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></PAGE_SIZE>" +
                    "                    <CURRENT_PAGE xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></CURRENT_PAGE>" +
                    "                    <TOTAL_RECORD xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></TOTAL_RECORD>" +
                    "                    <PROVINCE_CODE>" + provinceCode + "</PROVINCE_CODE>" +
                    "                    <ROUTE_CODE xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></ROUTE_CODE>" +
                    "                    <TRACE_ID xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></TRACE_ID>" +
                    "                    <RESERVED_1 xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></RESERVED_1>" +
                    "                    <RESERVED_2 xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></RESERVED_2>" +
                    "                </ns2:MSGHEADER>" +
                    "                <ns2:INPUTCOLLECTION>" +
                    "                    <ns2:INPUTCOLLECTION_ITEM>" +
                    "                        <ns2:PRI_KEY>" + uuid + "</ns2:PRI_KEY>" +
                    "                        <ns2:INTERNAL_USER_FLAG>0</ns2:INTERNAL_USER_FLAG>" +
                    "                        <ns2:USER_ACCOUNT xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></ns2:USER_ACCOUNT>" +
                    "                        <ns2:RECEIVER_NUMBER>" + receiverNumber + "</ns2:RECEIVER_NUMBER>" +
                    "                        <ns2:MESSAGE_CONTENT>{\"ip\":\"" + ip + "\",\"applicationName\":\"" + messageContext + "\"}</ns2:MESSAGE_CONTENT>" +
                    "                        <ns2:FORCE_FLAG>1</ns2:FORCE_FLAG>" +
                    "                        <ns2:MERGE_SEND_FLAG xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></ns2:MERGE_SEND_FLAG>" +
                    "                        <ns2:APPID>" + appid + "</ns2:APPID>" +
                    "                        <ns2:TEMPLATE_NUM>" + templateid + "</ns2:TEMPLATE_NUM>" +
                    "                        <ns2:INPUT_EXT xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></ns2:INPUT_EXT>" +
                    "                    </ns2:INPUTCOLLECTION_ITEM>" +
                    "                </ns2:INPUTCOLLECTION>" +
                    "         </ns2:InputParameters>" +
                    "   </soap:Body>" +
                    "</soap:Envelope>";

            System.out.println("xmlPayload:" + xmlPayload);
            HttpEntity<String> request = new HttpEntity<>(xmlPayload, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            // 解析响应并获取结果
            String responseBody = response.getBody();

            System.out.println("responseBody:" + responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

//    public static void main(String[] args) {
//        UUID uuid = UUID.randomUUID();
//        String token = "******";
//        String sourceSystemId = "SOA";
//        String sourceSystemName = "集成平台";
//        String provinceCode = "HQ";
//        DateTime now = DateUtil.date();
//        String formattedDateTime = now.toString("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
//
//
//        String xmlPayload = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
//                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
//                "   <soap:Body>" +
//                "         <ns2:InputParameters xmlns:ns2=\"http://soa.cmcc.com/OSB_EIP_EIP_HQ_ImportSmsSrv\" xmlns=\"http://soa.cmcc.com/MsgHeader\">" +
//                "                <ns2:MSGHEADER>" +
//                "                    <SOURCESYSTEMID>" + sourceSystemId + "</SOURCESYSTEMID>" +
//                "                    <SOURCESYSTEMNAME>" + sourceSystemName + "</SOURCESYSTEMNAME>" +
//                "                    <TOKEN>" + token + "</TOKEN>" +
//                "                    <USER_ID xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></USER_ID>" +
//                "                    <USER_NAME xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></USER_NAME>" +
//                "                    <USER_PASSWD xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></USER_PASSWD>" +
//                "                    <SUBMIT_DATE>" + formattedDateTime + "</SUBMIT_DATE>" +
//                "                    <PAGE_SIZE xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></PAGE_SIZE>" +
//                "                    <CURRENT_PAGE xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></CURRENT_PAGE>" +
//                "                    <TOTAL_RECORD xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></TOTAL_RECORD>" +
//                "                    <PROVINCE_CODE>" + provinceCode + "</PROVINCE_CODE>" +
//                "                    <ROUTE_CODE xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></ROUTE_CODE>" +
//                "                    <TRACE_ID xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></TRACE_ID>" +
//                "                    <RESERVED_1 xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></RESERVED_1>" +
//                "                    <RESERVED_2 xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></RESERVED_2>" +
//                "                </ns2:MSGHEADER>" +
//                "                <ns2:INPUTCOLLECTION>" +
//                "                    <ns2:INPUTCOLLECTION_ITEM>" +
//                "                        <ns2:PRI_KEY>" + uuid + "</ns2:PRI_KEY>" +
//                "                        <ns2:INTERNAL_USER_FLAG>0</ns2:INTERNAL_USER_FLAG>" +
//                "                        <ns2:USER_ACCOUNT xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></ns2:USER_ACCOUNT>" +
//                "                        <ns2:RECEIVER_NUMBER>15524638915</ns2:RECEIVER_NUMBER>" +
//                "                        <ns2:MESSAGE_CONTENT>{\"annoumcementTitle\":\"【测试新增公告】\"}</ns2:MESSAGE_CONTENT>" +
//                "                        <ns2:FORCE_FLAG>1</ns2:FORCE_FLAG>" +
//                "                        <ns2:MERGE_SEND_FLAG xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></ns2:MERGE_SEND_FLAG>" +
//                "                        <ns2:APPID>14161</ns2:APPID>" +
//                "                        <ns2:TEMPLATE_NUM>8310020230407174212</ns2:TEMPLATE_NUM>" +
//                "                        <ns2:INPUT_EXT xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"></ns2:INPUT_EXT>" +
//                "                    </ns2:INPUTCOLLECTION_ITEM>" +
//                "                </ns2:INPUTCOLLECTION>" +
//                "         </ns2:InputParameters>" +
//                "   </soap:Body>" +
//                "</soap:Envelope>";
//
//        System.out.println("xmlPayload:" + xmlPayload);
//    }
}
