package com.primeton.cmcc.cmccflowguard.controller;

import com.primeton.cmcc.cmccflowguard.handle.LogFileWatcher;
import com.primeton.cmcc.cmccflowguard.model.HealthCheckConfig;
import com.primeton.cmcc.cmccflowguard.model.Website;
import com.primeton.cmcc.cmccflowguard.handle.HealthCheckHistoryService;
import com.primeton.cmcc.cmccflowguard.handle.HealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
        return "index";
    }

    private final LogFileWatcher logFileWatcher;

    public HealthCheckController(LogFileWatcher logFileWatcher) {
        this.logFileWatcher = logFileWatcher;
    }


    /**
     * @return java.lang.String
     * @description 滚动读取当前日志
     * @author YunTao.Li
     * @date 2023/4/20 17:04
     */
    @GetMapping("/log-polling")
    @ResponseBody
    public String nioLog() {
        String logTail = logFileWatcher.getLogTail();
        return logTail;
    }

    /**
     * @return org.springframework.web.servlet.ModelAndView
     * @description 主机拨测
     * @author YunTao.Li
     * @date 2023/4/20 17:04
     */
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
            hostMap.put("host", url + ":" + port);
            hostMap.put("health", health ? "Healthy" : "Unhealthy");
            hostsHealthList.add(hostMap);
        }

        ModelAndView modelAndView = new ModelAndView("website_table");
        modelAndView.addObject("websites", hostsHealthList);

        return modelAndView;
    }

    /**
     * @return org.springframework.web.servlet.ModelAndView
     * @description 日志列表展示
     * @author YunTao.Li
     * @date 2023/4/20 17:04
     */
    @GetMapping("/logs")
    public ModelAndView logs() {
        List<String> logFiles = healthCheckHistoryService.getHistoryLogFiles();

        ModelAndView modelAndView = new ModelAndView("logfiles_list");
        modelAndView.addObject("logfiles", logFiles);
        return modelAndView;
    }

    /**
     * @param filename todo
     * @return ResponseEntity<Resource>
     * @description 日志点击下载
     * @author YunTao.Li
     * @date 2023/4/20 17:05
     */
    @GetMapping("/log/download")
    public ResponseEntity<ByteArrayResource> downloadLogFile(@RequestParam String filename) throws IOException {
        File file = new File("logs/" + filename);

        if (file.exists()) {
            Path path = Paths.get(file.getAbsolutePath());
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");
            return ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
        } else {
//            log.error("File not found: " + file.getAbsolutePath());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/wsdl")
    public ModelAndView wsdl() {
        List<String> logFiles = healthCheckHistoryService.getHistoryLogFiles();

        ModelAndView modelAndView = new ModelAndView("logfiles_list");
        modelAndView.addObject("logfiles", logFiles);
        return modelAndView;
    }

    @GetMapping("/nginx-log")
    public ModelAndView nginxLog() {
        List<String> logFiles = healthCheckHistoryService.getHistoryLogFiles();

        ModelAndView modelAndView = new ModelAndView("logfiles_list");
        modelAndView.addObject("logfiles", logFiles);
        return modelAndView;
    }

}

