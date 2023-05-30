package com.primeton.cmcc.cmccflowguard.handle;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author YunTao.Li
 * @description:
 * @date 2023/4/18 16:51
 */
@Service
public class HealthCheckHistoryService {

    @Value("${health-check.log-path:./logs}")
    private String logDirectory; // 日志文件目录

    /**
     * @return List<String>
     * @description 获得所有的历史文件列表
     * @author YunTao.Li
     * @date 2023/4/18 16:51
     */
    public List<String> getHistoryLogFiles() {
        List<String> logFiles = new ArrayList<>();
        File directory = new File(logDirectory);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".log")) {
                    logFiles.add(file.getName());
                }
            }
        }
        return logFiles;
    }
}
