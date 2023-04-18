package com.primeton.cmcc.cmccflowguard.controller;

import com.primeton.cmcc.cmccflowguard.model.HealthCheckConfig;
import com.primeton.cmcc.cmccflowguard.model.Website;
import com.primeton.cmcc.cmccflowguard.service.HealthCheckHistoryService;
import com.primeton.cmcc.cmccflowguard.service.HealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author YunTao.Li
 * @description:
 * @date 2023/4/17 23:45
 */
@Controller
public class HealthCheckController {
    @Autowired
    private HealthCheckService healthCheckService;

    @Autowired
    private HealthCheckConfig healthCheckConfig;

    @Autowired
    private HealthCheckHistoryService healthCheckHistoryService;

    @GetMapping("/")
    public String index(Model model) {
        // step1 : 检测所有的host
        List<Website> websites = healthCheckConfig.getWebsites();
        List<Map<String, Object>> hostsHealthList = new ArrayList<>();

        for (Website website : websites) {

            String name = website.getName();
            String url = website.getUrl();
            int port = website.getPort();

            boolean health = healthCheckService.checkHostHealth(url, port);

            Map<String, Object> hostMap = new HashMap<>();
            hostMap.put("name", name);
            hostMap.put("health", health ? "Healthy" : "Unhealthy");
            hostsHealthList.add(hostMap);
        }
        model.addAttribute("websites", hostsHealthList);
        return "index";
    }

    @GetMapping("/websites")
    public ModelAndView getWebsiteTable() {
        List<Website> websites = healthCheckConfig.getWebsites();
        List<Map<String, Object>> hostsHealthList = new ArrayList<>();
        for (Website website : websites) {

            String name = website.getName();
            String url = website.getUrl();
            int port = website.getPort();

            boolean health = healthCheckService.checkHostHealth(url, port);

            Map<String, Object> hostMap = new HashMap<>();
            hostMap.put("name", name);
            hostMap.put("health", health ? "Healthy" : "Unhealthy");
            hostsHealthList.add(hostMap);
        }
        Map<String, Object> hostMap = new HashMap<>();
        String random = String.valueOf(new Random(111).nextInt());
        hostMap.put("name", random);
        hostMap.put("health", "Healthy");
        hostsHealthList.add(hostMap);

        ModelAndView modelAndView = new ModelAndView("website_table");
        modelAndView.addObject("websites", hostsHealthList);

        return modelAndView;
    }

    @GetMapping("/logs")
    public ModelAndView logs() {
        List<String> logFiles = healthCheckHistoryService.getHistoryLogFiles();

        ModelAndView modelAndView = new ModelAndView("logfiles_list");
        modelAndView.addObject("logfiles", logFiles);
        return modelAndView;
    }

}

