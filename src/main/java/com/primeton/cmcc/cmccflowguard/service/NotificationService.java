package com.primeton.cmcc.cmccflowguard.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.nio.charset.Charset;
import java.util.Collections;


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

//            System.out.println("xmlPayload:" + xmlPayload);
            HttpEntity<String> request = new HttpEntity<>(xmlPayload, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            // 解析响应并获取结果
            String responseBody = response.getBody();

//            System.out.println("responseBody:" + responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
