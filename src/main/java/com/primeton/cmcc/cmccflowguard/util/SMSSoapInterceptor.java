package com.primeton.cmcc.cmccflowguard.util;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author YunTao.Li
 * @description:
 * @date 2023/4/21 17:57
 */
public class SMSSoapInterceptor implements PhaseInterceptor<Message> {

    @Override
    public void handleMessage(Message message) throws Fault {
        System.out.println("handleMessage in");
        Map<String, List<String>> headers = (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);
        if (headers == null) {
            headers = new HashMap<>();
            message.put(Message.PROTOCOL_HEADERS, headers);
        }
        message.put(Message.PROTOCOL_HEADERS, headers);

        String token = "1";
        String sourceSystemId = "WFP";
        String provinceCode = "HQ";
        headers.put("TOKEN_ID", Collections.singletonList(token));
        headers.put("SOURCESYSTEMID", Collections.singletonList(sourceSystemId));
        headers.put("PROVINCE_CODE", Collections.singletonList(provinceCode));

        System.out.println(message.getAttachments().toArray());
        System.out.println("handleMessage end");
    }

    @Override
    public void handleFault(Message message) {

    }

    @Override
    public Set<String> getAfter() {
        return null;
    }

    @Override
    public Set<String> getBefore() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getPhase() {
        return null;
    }

    @Override
    public Collection<PhaseInterceptor<? extends Message>> getAdditionalInterceptors() {
        return null;
    }
}
